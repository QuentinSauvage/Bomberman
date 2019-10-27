#include <stdlib.h>
#include <string.h>
#include <stdio.h>
#include "globals.h"
#include "item.h"
#include "player.h"
#include "servertcp.h"

extern ServerTcp tcpServ;
extern int playerAffectToJson(Player, char *, int);

/**
initialize player health number of bomb...
*/
void initPlayer(Player p, int id, int gameId, int posX, int posY) {
  p->life = BASE_LIFE;
  p->maxLife = BASE_LIFE;
  p->speed = BASE_SPEED;
  p->nbClassic = DEFAULT_CLASSIC;
  p->nbMine = DEFAULT_MINE;
  p->nbRemote = DEFAULT_REMOTE;
  p->maxBomb = BASE_MAX_BOMB;
  p->posX = posX;
  p->posY = posY;
  p->id = id;
  p->gameId = gameId;
  p->nbBonus = 0;
}

/**
Apply the effect of a bonus to the player
*/
static void addBonusEffect(Player p, Item i) {
  if (i->iClass == BOMB_UP)
    p->maxBomb++;
  else if (i->iClass == BOMB_UP)
    p->maxBomb++;
  else if (i->iClass == BOMB_DOWN)
    p->maxBomb--;
  else if (i->iClass == SCOOTER)
    p->speed++;
  else if (i->iClass == BROKEN_LEGS)
    p->speed--;
  else if (i->iClass == LIFE_UP)
    p->maxLife *= LIFE_UP_EFFECT;
  else if (i->iClass == LIFE_MAX)
    p->life = p->maxLife;
}

/**
Add a bonus to the player bonus list
*/
void addBonus(Player p, Item i) {
  int j;
  addBonusEffect(p, i);
  for (j = 0; j < p->nbBonus; j++) {
    if (i->iClass == p->bonusList[j]->iClass) {
      if (i->iClass == MAJOR)
        return;
      p->bonusList[j]->number++;
      return;
    }
  }
  p->bonusList[p->nbBonus++] = i;
}

/**
Deal damage the the player and send the message to the client
*/
void affectPlayer(Player p, float damage) {
  char buffer[BUFFER_SIZE];
  int i;
  for (i = 0; i < p->nbBonus; i++) {
    if (p->bonusList[i]->iClass == MAJOR && p->bonusList[i]->number)
      p->bonusList[i]->number--, damage = 0;
  }
  p->life -= damage;
  playerAffectToJson(p, buffer, BUFFER_SIZE);
  playerSend(p, buffer);
}

/**
Check if the player have the fire_power bonus
*/
int gotFirePower(Player p) {
  int i;
  for (i = 0; i < p->nbBonus; i++)
    if (p->bonusList[i]->iClass == FIRE_POWER)
      return 1;
  return 0;
}

/**
Read a message from the client
*/
int playerReceive(Player p, char *buffer, int size) {
  return tcpServ->server_receive(p->fd, buffer, size);
}

/**
Send a message to the the client
*/
int playerSend(Player p, char *answer) {
  return tcpServ->server_send(p->fd, answer);
}

