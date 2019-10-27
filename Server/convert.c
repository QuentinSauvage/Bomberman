#include <string.h>
#include <stdio.h>
#include "globals.h"
#include "item.h"
#include "map.h"
#include "bomb.h"
#include "player.h"
#include "game.h"
#include "convert.h"

/*Game*/
/**
"{"name":"%s","nbPlayer":%d,"map":"%s"},"
*/
int gameToJson(Game g, char *buf, int size) {
  return snprintf(buf, size, GAME_JSON_SKELETON, g->name, g->nbPlayer,
                  g->m->name);
}

/**
"{"action":"game/list","status":"200","message":"ok"..."
*/
void gameListToJson(char *buf, int size) {
  char *start;
  int i, j, writed;
  writed = snprintf(buf, size, GAME_LIST_JSON_SKELETON, nbGame);
  start = buf + writed;
  size -= writed;
  if (nbGame) {
    strcpy(start, GAME_LIST_JSON_BEGIN);
    writed = strlen(GAME_LIST_JSON_BEGIN);
    start += writed;
    size -= writed;
    for (i = 0, j = nbGame; j && i < MAX_GAME; i++, j--) {
      if (game_list[i]) {
        writed = gameToJson(game_list[i], start, size);
        start += writed;
        size -= writed;
      }
    }
    strncpy(start, GAME_LIST_JSON_END, size);
    writed = strlen(GAME_LIST_JSON_END);
    start += writed;
    size -= writed;
  }
  if ((writed = mapListToJson(start, size)) != ERR) {
    start += writed;
    size -= writed;
  }
  strcpy(start, "}\n");
}

/**
"{"action":"game/create","status":"201","message":"game created with %s"..."
*/
void createGameToJson(Player p, char *buf, int size) {
  char *start;
  int writed;
  Game g = game_list[p->gameId];
  writed = snprintf(buf, size, CREATE_GAME_JSON_SKELETON, g->m->name,
                    g->nbPlayer, p->posX, p->posY);
  start = buf + writed;
  size -= writed;
  writed = playerToJson(p, start, size);
  start += writed;
  size -= writed;
  writed = mapToJson(g->m, start, size);
  start += writed;
  size -= writed;
  strcpy(start, "}\n");
}

/**
"{"action":"game/join","status":"201",..."
*/
void joinGameToJson(Player p, char *buf, int size) {
  char *start;
  int writed;
  Game g = game_list[p->gameId];
  writed = snprintf(buf, size, JOIN_GAME_JSON_SKELETON_BEGIN, g->nbPlayer);
  start = buf + writed;
  size -= writed;
  writed = playerListToJson(g, start, size);
  start += writed;
  size -= writed;
  writed = snprintf(start, size, JOIN_GAME_JSON_SKELETON_END, p->posX, p->posY);
  start += writed;
  size -= writed;
  writed = playerToJson(p, start, size);
  start += writed;
  size -= writed;
  writed = mapToJson(g->m, start, size);
  start += writed;
  size -= writed;
  strcpy(start, "}\n");
}

/**
"{"status":201,"action":"object/new",..."
*/
void objectNewToJson(Player pla, char *buf, int size) {
  char *start;
  int writed;
  strcpy(buf, OBJECT_NEW_JSON);
  writed = strlen(OBJECT_NEW_JSON);
  start = buf + writed;
  size -= writed;
  writed = playerLessToJson(pla, start, size);
  start += writed;
  size -= writed;
  strcpy(start, "}\n");
}

/**
"POST game/quit{"player":%d}"
*/
void gameQuitToJson(Player pla, char *buf, int size) {
  snprintf(buf, size, GAME_QUIT, pla->id + 1);
}
/*Item*/

/**
"{"class":"%s","number":%d},"
*/
int itemToJson(Item i, char *buf, int size) {
  return snprintf(buf, size, ITEM_JSON_SKELETON, itemClassList[i->iClass],
                  i->number);
}
/*Map*/

/**
""map":{"width":%d,"height":%d,"content":..."
*/
int mapToJson(Map m, char *buf, int size) {
  char *start;
  int writed, i, total;
  writed = snprintf(buf, size, MAP_JSON_SKELETON, m->width, m->height);
  start = buf + writed;
  size -= writed;
  total = writed;
  for (i = 0; i < m->height; i++) {
    strncpy(start, m->content[i], size);
    start += m->width;
    size -= m->width;
    total += m->width;
  }
  strncpy(start, "\"}", size);
  return total + 2;
}

int mapLessToJson(Map m, char *buf, int size) {
  char *start;
  int writed, i, total;
  writed = snprintf(buf, size, MAP_LESS_JSON_SKELETON);
  start = buf + writed;
  size -= writed;
  total = writed;
  for (i = 0; i < m->height; i++) {
    strncpy(start, m->content[i], size);
    start += m->width;
    size -= m->width;
    total += m->width;
  }
  *start++ = '"';
  *start = ',';
  return total + 2;
}
/*Player*/

/**
"{"id":%d,"pos":"%d,%d"},{"id":%d,"pos":"%d,%d"},"
*/
int playerListToJson(Game g, char *buf, int size) {
  char *start = buf;
  int i, writed, total = 0;
  for (i = 0; i < MAX_PLAYER; i++) {
    if (g->player_list[i]) {
      writed = playerPosToJson(g->player_list[i], start, size);
      start += writed;
      size -= writed;
      total += writed;
    }
  }
  return total;
}

/**
bonusMalus:[...]"
*/
static int playerBonusListToJson(Player p, char *buf, int size) {
  char *start = buf;
  int i, writed, total = 0;
  strcpy(start, PLAYER_BONUS_LIST_JSON);
  writed = strlen(PLAYER_BONUS_LIST_JSON);
  start += writed;
  size -= writed;
  total += writed;
  for (i = 0; i < p->nbBonus; i++) {
    if (p->bonusList[i]->number) {
      writed = itemToJson(p->bonusList[i], start, size);
      start += writed;
      size -= writed;
      total += writed;
    }
  }
  strcpy(start, "],");
  return total + 2;
}

/**
""player":{"id":%d,"life":%d,..."
*/
int playerToJson(Player p, char *buf, int size) {
  char *start;
  int writed, total = 0;
  writed =
      snprintf(buf, size, PLAYER_JSON_SKELETON, p->id + 1, p->life, p->maxLife,
               p->speed, p->nbClassic, p->nbMine, p->nbRemote, p->maxBomb);
  start = buf + writed;
  size -= writed;
  total += writed;
  writed = playerBonusListToJson(p, start, size);
  start += writed;
  size -= writed;
  total += writed;
  strcpy(start, "},");
  return total + 2;
}

/**
""life":%d,"maxLife":%d,..."
*/
int playerLessToJson(Player p, char *buf, int size) {
  char *start;
  int writed, total = 0;
  writed = snprintf(buf, size, PLAYER_LESS_JSON_SKELETON, p->life, p->maxLife,
                    p->speed, p->nbClassic, p->nbMine, p->nbRemote, p->maxBomb);
  start = buf + writed;
  size -= writed;
  total += writed;
  writed = playerBonusListToJson(p, start, size);
  return total + writed;
}

/**
"{"id":%d,"pos":"%d,%d"},"
*/
int playerPosToJson(Player p, char *buf, int size) {
  return snprintf(buf, size, PLAYER_POS_JSON_SKELETON, p->id + 1, p->posX,
                  p->posY);
}

/**
"{"action":"attack/bomb","status":"201","player":{"
*/
int attackBombToJson(Player p, char *buf, int size) {
  char *start;
  int writed, total = 0;
  strncpy(buf, ATTACK_BOMB_JSON_SKELETON, size);
  writed = strlen(ATTACK_BOMB_JSON_SKELETON);
  start = buf + writed;
  size -= writed;
  total += writed;
  writed = playerLessToJson(p, start, size);
  start += writed;
  size -= writed;
  total += writed;
  *start++ = '}';
  *start++ = '\n';
  *start = '\0';
  return total + 2;
}

/**
 "POST attack/affect{..."
*/
void playerAffectToJson(Player p, char *buf, int size) {
  char *start;
  int writed;
  strcpy(buf, ATTACK_AFFECT);
  writed = strlen(ATTACK_AFFECT);
  start = buf + writed;
  writed = playerLessToJson(p, start, size - writed);
  start += writed;
  strcpy(start, "}\n");
}

