#ifndef PLAYER_H
#define PLAYER_H

#define BASE_LIFE 100
#define BASE_SPEED 1
#define DEFAULT_CLASSIC 2
#define DEFAULT_MINE 0 
#define DEFAULT_REMOTE 0
#define BASE_MAX_BOMB 1
#define MAX_BONUS 64
#define LIFE_UP_EFFECT 1.1

struct player {
  int id;
  int gameId;
  int life;
  int maxLife;
  int speed;
  int nbClassic;
  int nbMine;
  int nbRemote;
  int maxBomb;
  int posX;
  int posY;
  int fd;
  Item bonusList[MAX_BONUS];
  int nbBonus;
};

typedef struct player *Player;

/**
initialize player health number of bomb...
*/
void initPlayer(Player, int, int, int, int);
/**
Add a bonus to the player bonus list
*/
void addBonus(Player, Item);
/**
Deal damage the the player and send the message to the client
*/
void affectPlayer(Player, float);
/**
Check if the player have the fire_power bonus
*/
int gotFirePower(Player);
/**
Read a message from the client
*/
int playerReceive(Player, char *, int);
/**
Send a message to the the client
*/
int playerSend(Player, char *);

#endif
