#include <stdlib.h>
#include <string.h>
#include <stdio.h>
#include <unistd.h>
#include <pthread.h>
#include "globals.h"
#include "item.h"
#include "map.h"
#include "player.h"
#include "bomb.h"
#include "game.h"
#include "convert.h"

int nbGame = 0;

/**
Send a message to all players in the game
*/
static void sendAllPlayer(Game g, char *buf) {
  int i;
  for (i = 0; i < MAX_PLAYER; i++)
    if (g->player_list[i])
      playerSend(g->player_list[i], buf);
}

/**
Create and initialise a game
*/
int createGame(char *name, char *map, Player pla) {
  Map m;
  int i;
  if (nbGame == MAX_GAME)
    return ERR;
  if ((m = searchMap(map)) == NULL)
    return ERR;
  for (i = 0; i < MAX_GAME; i++) {
    if (game_list[i] && !strcmp(game_list[i]->name, name))
      return ERR;
  }
  for (i = 0; i < MAX_GAME; i++) {
    if (!game_list[i])
      break;
  }
  game_list[i] = (Game)malloc(sizeof(struct game));
  strcpy(game_list[i]->name, name);
  game_list[i]->nbPlayer = 1;
  game_list[i]->m = m;
  game_list[i]->nbClassic = 0;
  game_list[i]->nbMine = 0;
  game_list[i]->nbRemote = 0;
  game_list[i]->nbItem = 0;
  initPlayer(pla, 0, i, 0, 0);
  game_list[i]->player_list[0] = pla;
  nbGame++;
  return i;
}

/**
Add a player to the game
*/
int joinGame(char *name, Player pla) {
  int i, j, *x, *y;
  Game g;
  for (i = 0; i < MAX_GAME; i++) {
    g = game_list[i];
    if (g && !strcmp(name, g->name)) {
      if (g->nbPlayer == MAX_PLAYER)
        break;
      for (j = 0; j < MAX_PLAYER; j++) {
        if (!g->player_list[j])
          break;
      }
      getStartPosition(g->m, j, x, y);
      initPlayer(pla, j, i, *x, *y);
      g->player_list[j] = pla;
      g->nbPlayer++;
      return i;
    }
  }
  return ERR;
}

/**
Make a mine explode
*/
static void explodeMine(Game g, Player pla, int mineId) {
  free(g->mine_list[mineId]);
  g->nbMine--;
  affectPlayer(pla, pla->maxLife * MINE_DMG);
}

/**
Try to move the player in the given direction
*/
int movePlayer(Player pla, Direction d) {
  Game g = game_list[pla->gameId];
  Map m = g->m;
  int bomb;
  if (d == UP) {
    if (pla->posY > 0 && isFree(m, pla->posX, pla->posY - 1)) {
      if ((bomb = getClassic(g, pla->posX, pla->posY - 1)) != ERR)
        return 1;
      if ((bomb = getRemote(g, pla->posX, pla->posY - 1)) != ERR)
        return 1;
      pla->posY--;
      if ((bomb = getMine(g, pla->posX, pla->posY)) != ERR)
        explodeMine(g, pla, bomb);

      return 0;
    }
  } else if (d == DOWN) {
    if (pla->posY < m->height - 1 && isFree(m, pla->posX, pla->posY + 1)) {
      if ((bomb = getRemote(g, pla->posX, pla->posY + 1)) != ERR)
        return 1;
      if ((bomb = getClassic(g, pla->posX, pla->posY + 1)) != ERR)
        return 1;
      pla->posY++;
      if ((bomb = getMine(g, pla->posX, pla->posY)) != ERR)
        explodeMine(g, pla, bomb);

      return 0;
    }
  } else if (d == LEFT) {
    if (pla->posX > 0 && isFree(m, pla->posX - 1, pla->posY)) {
      if ((bomb = getRemote(g, pla->posX - 1, pla->posY)) != ERR)
        return 1;
      if ((bomb = getClassic(g, pla->posX - 1, pla->posY)) != ERR)
        return 1;
      pla->posX--;
      if ((bomb = getMine(g, pla->posX, pla->posY)) != ERR)
        explodeMine(g, pla, bomb);

      return 0;
    }
  } else if (d == RIGHT) {
    if (pla->posX < m->width && isFree(m, pla->posX + 1, pla->posY)) {
      if ((bomb = getClassic(g, pla->posX + 1, pla->posY)) != ERR)
        return 1;
      if ((bomb = getRemote(g, pla->posX + 1, pla->posY)) != ERR)
        return 1;
      pla->posX++;
      if ((bomb = getMine(g, pla->posX, pla->posY)) != ERR)
        explodeMine(g, pla, bomb);
      return 0;
    }
  }
  return 1;
}

/**
Thread function for the classic bombs
*/
static void *classicThread(void *arg) {
  Bomb b = (Bomb)arg;
  sleep(CLASSIC_TIME);
  if (b)
    explodeBomb(game_list[b->gameId], b, CLASSIC);
  return NULL;
}

/**
Put a classic bomb
*/
static int putClassic(Player pla, int bonus) {
  int i;
  pthread_t bombThread;
  Game g = game_list[pla->gameId];
  if (pla->nbClassic && g->nbClassic != MAX_BOMB) {
    pla->nbClassic--;
    for (i = 0; i < MAX_BOMB; i++) {
      if (!g->classic_list[i])
        break;
    }
    g->classic_list[i] =
        createBomb(pla->posX, pla->posY, pla->id, pla->gameId, bonus);
    g->nbClassic++;
    pthread_create(&bombThread, NULL, classicThread, g->classic_list[i]);
    pthread_detach(bombThread);
    return 0;
  }
  return 1;
}

/**
Put a mine
*/
static int putMine(Player pla, int bonus) {
  int i;
  Game g = game_list[pla->gameId];
  if (pla->nbMine && g->nbMine != MAX_BOMB) {
    pla->nbMine--;
    for (i = 0; i < MAX_BOMB; i++) {
      if (!g->mine_list[i])
        break;
    }
    g->mine_list[i] =
        createBomb(pla->posX, pla->posY, pla->id, pla->gameId, bonus);
    g->nbMine++;
    return 0;
  }
  return 1;
}

/**
Put a remote
*/
static int putRemote(Player pla, int bonus) {
  int i;
  Game g = game_list[pla->gameId];
  if (pla->nbRemote && g->nbRemote != MAX_BOMB) {
    pla->nbRemote--;
    for (i = 0; i < MAX_BOMB; i++) {
      if (!g->remote_list[i])
        break;
    }
    g->remote_list[i] =
        createBomb(pla->posX, pla->posY, pla->id, pla->gameId, bonus);
    g->nbRemote++;
    return 0;
  }
  return 1;
}

/**
Try to put a bomb on player position
*/
int putBomb(Player pla, ItemClass c, int bonus) {
  int i, nbBomb = 0;
  Game g = game_list[pla->gameId];
  if (bonus != gotFirePower(pla))
    return 1;
  for (i = 0; i < MAX_BOMB; i++) {
    if (g->classic_list[i] && g->classic_list[i]->ownerId == pla->id)
      nbBomb++;
    if (g->mine_list[i] && g->mine_list[i]->ownerId == pla->id)
      nbBomb++;
    if (g->remote_list[i] && g->remote_list[i]->ownerId == pla->id)
      nbBomb++;
    if (nbBomb >= pla->maxBomb)
      return 1;
  }
  if (c == CLASSIC && !putClassic(pla, bonus))
    return 0;
  if (c == MINE && !putMine(pla, bonus))
    return 0;
  if (c == REMOTE && !putRemote(pla, bonus))
    return 0;
  return 1;
}

/**
Test if there is a classic bomb at the position
*/
int getClassic(Game g, int posX, int posY) {
  int i;
  Bomb b;
  if (g->nbClassic) {
    for (i = 0; i < MAX_BOMB; i++) {
      b = g->classic_list[i];
      if (b && b->posX == posX && b->posY == posY)
        return i;
    }
  }
  return ERR;
}

/**
Test if there is a mine at the position
*/
int getMine(Game g, int posX, int posY) {
  int i;
  Bomb b;
  if (g->nbMine) {
    for (i = 0; i < MAX_BOMB; i++) {
      b = g->mine_list[i];
      if (b && b->posX == posX && b->posY == posY)
        return i;
    }
  }
  return ERR;
}

/**
Test if there is a remote bomb at the position
*/
int getRemote(Game g, int posX, int posY) {
  int i;
  Bomb b;
  if (g->nbRemote) {
    for (i = 0; i < MAX_BOMB; i++) {
      b = g->remote_list[i];
      if (b && b->posX == posX && b->posY == posY)
        return i;
    }
  }
  return ERR;
}

/**
Test if a map cell contains a breakable wall or a player
*/
static int testMapCell(Game g, int posX, int posY, int *playerId) {
  int i, wall;
  Player p;
  *playerId = -1;
  wall = breakWall(g->m, posX, posY);
  if (wall == ERR) {
    for (i = 0; i < MAX_PLAYER; i++) {
      p = g->player_list[i];
      if (p && p->posX == posX && p->posY == posY) {
        *playerId = i;
        break;
      }
    }
  }
  return wall;
}

/**
Calculate and send the damage for all player in the list
*/
static void affectedPlayers(Game g, int playerId[MAX_AFFECTED][2]) {
  int i;
  for (i = 0; i < MAX_AFFECTED; i++) {
    if (playerId[i][0] != -1 && g->player_list[playerId[i][0]])
      affectPlayer(g->player_list[playerId[i][0]],
                   g->player_list[playerId[i][0]]->maxLife *
                       (CLASSIC_DMG - 0.1 * (playerId[i][1] - 1)));
  }
}

/**
bonusMalus:[...], bomb[...]
*/
static int itemListToJson(char *buf, int size, Item loot[MAX_LOOTED]) {
  char *start;
  int writed, i, total = 0;
  writed = snprintf(buf, size, ATTACK_EXPLOSE_BONUS);
  start = buf + writed;
  size -= writed;
  total += writed;
  for (i = 0; i < MAX_LOOTED; i++) {
    if (loot[i] && loot[i]->iClass > REMOTE) {
      writed = snprintf(start, size, ATTACK_EXPLOSE_ITEM, loot[i]->posX,
                        loot[i]->posY, itemClassList[loot[i]->iClass]);
      start += writed;
      size -= writed;
      total += writed;
    }
  }
  writed = snprintf(start, size, ATTACK_EXPLOSE_BOMB);
  start += writed;
  size -= writed;
  total += writed;
  for (i = 0; i < MAX_LOOTED; i++) {
    if (loot[i] && loot[i]->iClass <= REMOTE) {
      writed = snprintf(start, size, ATTACK_EXPLOSE_ITEM, loot[i]->posX,
                        loot[i]->posY, itemClassList[loot[i]->iClass]);
      start += writed;
      size -= writed;
      total += writed;
    }
  }
  strcpy(start, "],");
  return total + 2;
}

/**
chain[]
*/
static int chainBombToJson(char *buf, int size) {
  return snprintf(buf, size, ATTACK_EXPLOSE_CHAIN);
}

/**
Build and send the attack/explose responds to all player in the game
*/
static void sendAttackExplose(Game g, Bomb b, Item loot[MAX_LOOTED],
                              ItemClass c) {
  char buf[BUFFER_SIZE], *start;
  int writed, size = BUFFER_SIZE;
  writed = snprintf(buf, size, ATTACK_EXPLOSE_BEGIN, b->posX, b->posY,
                    itemClassList[c], boolean[b->bonus]);
  start = buf + writed;
  size -= writed;
  writed = mapLessToJson(g->m, start, size);
  start += writed;
  size -= writed;
  writed = itemListToJson(start, size, loot);
  start += writed;
  size -= writed;
  chainBombToJson(start, size);
  sendAllPlayer(g, buf);
}

/**
Add all the items in the item list
*/
static void addItemToGame(Game g, Item loot[MAX_LOOTED]) {
  int i, j;
  for (i = 0; i < MAX_LOOTED; i++) {
    if (loot[i]) {
      for (j = 0; j < MAX_ITEM; j++) {
        if (!g->itemList[j]) {
          g->itemList[j] = loot[i];
          g->nbItem++;
          break;
        }
      }
    }
  }
}

/**
Delete a bomb from the game list
*/
void deleteBomb(Game g, Bomb b, ItemClass c) {
  int i;
  for (i = 0; i < MAX_BOMB; i++) {
    if (c == CLASSIC && g->classic_list[i] &&
        g->classic_list[i]->ownerId == b->ownerId &&
        g->classic_list[i]->posX == b->posX &&
        g->classic_list[i]->posY == b->posY) {
      free(b);
      g->classic_list[i] = NULL;
      return;
    }
    if (c == MINE && g->mine_list[i] &&
        g->mine_list[i]->ownerId == b->ownerId &&
        g->mine_list[i]->posX == b->posX && g->mine_list[i]->posY == b->posY) {
      free(b);
      g->mine_list[i] = NULL;
      return;
    }
    if (c == REMOTE && g->remote_list[i] &&
        g->remote_list[i]->ownerId == b->ownerId &&
        g->remote_list[i]->posX == b->posX &&
        g->remote_list[i]->posY == b->posY) {
      free(b);
      g->remote_list[i] = NULL;
      return;
    }
  }
}

/**
Test if there is a bomb at the position for a cahin
*/
static int testChain(Game g, int posX, int posY) {
  int i;
  Bomb b;
  for (i = 0; i < MAX_BOMB; i++) {
    b = g->classic_list[i];
    if (b && b->posX == posX && b->posY == posY)
      return i;
    b = g->remote_list[i];
    if (b && b->posX == posX && b->posY == posY)
      return i;
    b = g->mine_list[i];
    if (b && b->posX == posX && b->posY == posY)
      return i;
  }
  return ERR;
}

/**
Explode every bomb in the list
*/
void chainExplode(Game g, int chainBombId[MAX_AFFECTED]) {
  int i;
  Bomb b;
  for (i = 0; i < MAX_AFFECTED; i++) {
    if (chainBombId[i] != ERR) {
      b = g->classic_list[chainBombId[i]];
      if (b)
        explodeBomb(g, b, CLASSIC);
      b = g->remote_list[chainBombId[i]];
      if (b)
        explodeBomb(g, b, REMOTE);
      b = g->mine_list[chainBombId[i]];
      if (b)
        explodeMine(g, g->player_list[b->ownerId], i);
    }
  }
}

/**
Explose the given bomb, test for wall break, player affects and bomb chain in
every direction
*/
void explodeBomb(Game g, Bomb b, ItemClass c) {
  Item loot[MAX_LOOTED];
  int playerId[MAX_AFFECTED][2], chainBombId[MAX_AFFECTED], i, j, block,
      distance;
  if (!(b && b->posX >= 0 && b->posX <= g->m->width - 1 && b->posY >= 0 &&
        b->posY <= g->m->height - 1))
    return;
  for (i = 0; i < MAX_AFFECTED; i++) {
    playerId[i][0] = -1;
    chainBombId[i] = -1;
  }
  for (j = 0; j < MAX_LOOTED; j++) {
    loot[j] = NULL;
  }
  /*UP*/
  distance = 1;
  i = 0;
  j = 0;
  block = testMapCell(g, b->posX, b->posY - distance, &playerId[i][0]);
  if (playerId[i][0] != -1)
    playerId[i][1] = distance;
  if (!block)
    loot[j++] = createRandomItem(b->posX, b->posY - distance);
  if (block == ERR)
    chainBombId[i] = testChain(g, b->posX, b->posY - distance);
  i++;
  distance++;
  if (block == ERR) {
    block = testMapCell(g, b->posX, b->posY - distance, &playerId[i][0]);
    if (playerId[i][0] != -1)
      playerId[i][1] = distance;
    if (block == ERR)
      chainBombId[i] = testChain(g, b->posX, b->posY - distance);
    i++;
    distance++;
  }
  if (block == ERR && b->bonus) {
    testMapCell(g, b->posX, b->posY - distance, &playerId[i][0]);
    if (playerId[i][0] != -1)
      playerId[i][1] = distance;
    if (block == ERR)
      chainBombId[i] = testChain(g, b->posX, b->posY - distance);
    i++;
    distance++;
  }
  /*DOWN*/
  distance = 1;
  block = testMapCell(g, b->posX, b->posY + distance, &playerId[i][0]);
  if (playerId[i][0] != -1)
    playerId[i][1] = distance;
  if (!block)
    loot[j++] = createRandomItem(b->posX, b->posY + distance);
  if (block == ERR)
    chainBombId[i] = testChain(g, b->posX, b->posY + distance);
  i++;
  distance++;
  if (block == ERR) {
    block = testMapCell(g, b->posX, b->posY + distance, &playerId[i][0]);
    if (playerId[i][0] != -1)
      playerId[i][1] = distance;
    if (block == ERR)
      chainBombId[i] = testChain(g, b->posX, b->posY + distance);
    i++;
    distance++;
  }
  if (block == ERR && b->bonus) {
    testMapCell(g, b->posX, b->posY + distance, &playerId[i][0]);
    if (playerId[i][0] != -1)
      playerId[i][1] = distance;
    if (block == ERR)
      chainBombId[i] = testChain(g, b->posX, b->posY + distance);
    i++;
    distance++;
  }
  /*LEFT*/
  distance = 1;
  block = testMapCell(g, b->posX - distance, b->posY, &playerId[i][0]);
  if (playerId[i][0] != -1)
    playerId[i][1] = distance;
  if (!block)
    loot[j++] = createRandomItem(b->posX - distance, b->posY);
  if (block == ERR)
    chainBombId[i] = testChain(g, b->posX - distance, b->posY);
  i++;
  distance++;
  if (block == ERR) {
    block = testMapCell(g, b->posX - distance, b->posY, &playerId[i][0]);
    if (playerId[i][0] != -1)
      playerId[i][1] = distance;
    if (block == ERR)
      chainBombId[i] = testChain(g, b->posX - distance, b->posY);
    i++;
    distance++;
  }
  if (block == ERR && b->bonus) {
    testMapCell(g, b->posX - distance, b->posY, &playerId[i][0]);
    if (playerId[i][0] != -1)
      playerId[i][1] = distance;
    if (block == ERR)
      chainBombId[i] = testChain(g, b->posX - distance, b->posY);
    i++;
    distance++;
  }
  /*RIGHT*/
  distance = 1;
  block = testMapCell(g, b->posX + distance, b->posY, &playerId[i][0]);
  if (playerId[i][0] != -1)
    playerId[i][1] = distance;
  if (!block)
    loot[j++] = createRandomItem(b->posX + distance, b->posY);
  if (block == ERR)
    chainBombId[i] = testChain(g, b->posX + distance, b->posY);
  i++;
  distance++;
  if (block == ERR) {
    block = testMapCell(g, b->posX + distance, b->posY, &playerId[i][0]);
    if (playerId[i][0] != -1)
      playerId[i][1] = distance;
    if (block == ERR)
      chainBombId[i] = testChain(g, b->posX + distance, b->posY);
    i++;
    distance++;
  }
  if (block == ERR && b->bonus) {
    testMapCell(g, b->posX + distance, b->posY, &playerId[i][0]);
    if (playerId[i][0] != -1)
      playerId[i][1] = distance;
    if (block == ERR)
      chainBombId[i] = testChain(g, b->posX + distance, b->posY);
    i++;
    distance++;
  }
  testMapCell(g, b->posX, b->posY, &playerId[i][0]);
  if (playerId[i][0] != -1)
    playerId[i][1] = 1;
  i++;
  addItemToGame(g, loot);
  sendAttackExplose(g, b, loot, c);
  affectedPlayers(g, playerId);
  if (c == CLASSIC)
    g->nbClassic--;
  if (c == MINE)
    g->nbMine--;
  if (c == REMOTE)
    g->nbRemote--;
  deleteBomb(g, b, c);
  chainExplode(g, chainBombId);
}

/**
Explose all the remotes of the player
*/
void explodeRemotes(Player pla) {
  int i;
  Bomb b;
  Game g = game_list[pla->gameId];
  for (i = 0; i < MAX_BOMB; i++) {
    b = g->remote_list[i];
    if (b && b->ownerId == pla->id)
      explodeBomb(g, b, REMOTE);
  }
}

/**
Test if their is a bomb item and add it to the player inventory
*/
int pickUpBomb(Player pla, ItemClass c) {
  int i;
  Game g = game_list[pla->gameId];
  for (i = 0; i < MAX_ITEM; i++) {
    if (g->itemList[i] && cmpItem(g->itemList[i], c, pla->posX, pla->posY)) {
      if (c == CLASSIC) {
        pla->nbClassic++;
        return 0;
      }
      if (c == MINE) {
        pla->nbMine++;
        return 0;
      }
      if (c == REMOTE) {
        pla->nbRemote++;
        return 0;
      }
      break;
    }
  }
  return 1;
}

/**
Test if their is a bonus/malus and add it to the player inventory
*/
int pickUpBonus(Player pla, ItemClass c) {
  int i;
  Game g = game_list[pla->gameId];
  for (i = 0; i < MAX_ITEM; i++) {
    if (g->itemList[i] && cmpItem(g->itemList[i], c, pla->posX, pla->posY)) {
      addBonus(pla, g->itemList[i]);
      g->itemList[i] = NULL;
      g->nbItem--;
      return 0;
    }
  }
  return 1;
}

/**
remove the player from the game
*/
void removePlayer(Player pla) {
  int i;
  Game g = game_list[pla->gameId];
  Bomb b;
  for (i = 0; i < MAX_BOMB; i++) {
    b = g->classic_list[i];
    if (b && b->ownerId == pla->id)
      explodeBomb(g, b, CLASSIC);
    b = g->remote_list[i];
    if (b && b->ownerId == pla->id)
      explodeBomb(g, b, REMOTE);
  }
  g->player_list[pla->id] = NULL;
  if (!--g->nbPlayer)
    freeGame(game_list[pla->gameId]);
}

/**
free a game
*/
void freeGame(Game g) {
  int i;
  for (i = 0; i < MAX_ITEM; i++) {
    if (g->itemList[i])
      free(g->itemList[i]);
  }
  for (i = 0; i < MAX_BOMB; i++) {
    if (g->classic_list[i])
      free(g->classic_list[i]);
    if (g->mine_list[i])
      free(g->mine_list[i]);
    if (g->remote_list[i])
      free(g->remote_list[i]);
  }
  free(g);
  nbGame--;
}

