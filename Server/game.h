#ifndef GAME_H
#define GAME_H

#define NAME_SIZE 256
#define MAX_GAME 32
#define MAX_BOMB 32
#define MAX_PLAYER 32
#define MAX_ITEM 256
#define MAX_AFFECTED 13
#define MAX_LOOTED 5

struct game {
  char name[NAME_SIZE];
  Player player_list[MAX_PLAYER];
  int nbPlayer;
  Bomb classic_list[MAX_BOMB];
  int nbClassic;
  Bomb mine_list[MAX_BOMB];
  int nbMine;
  Bomb remote_list[MAX_BOMB];
  int nbRemote;
  Map m;
  Item itemList[MAX_ITEM];
  int nbItem;
};

enum direction { UP, DOWN, LEFT, RIGHT };

typedef struct game *Game;
typedef enum direction Direction;

Game game_list[MAX_GAME];

/**
Create and initialise a game
*/
int createGame(char *, char *, Player);

/**
Add a player to the game
*/
int joinGame(char *, Player);

/**
Try to move the player in the given direction
*/
int movePlayer(Player, Direction);

/**
Try to put a bomb on player position
*/
int putBomb(Player, ItemClass, int);

/**
Test if there is a classic bomb at the position
*/
int getClassic(Game, int, int);

/**
Test if there is a mine at the position
*/
int getMine(Game, int, int);

/**
Test if there is a remote bomb at the position
*/
int getRemote(Game, int, int);

/**
Explose the given bomb, test for wall break, player affects and bomb chain in every direction
*/
void explodeBomb(Game, Bomb, ItemClass);

/**
Explose all the remotes of the player
*/
void explodeRemotes(Player);

/**
Test if their is a bomb item and add it to the player inventory
*/
int pickUpBomb(Player, ItemClass);

/**
Test if their is a bonus/malus and add it to the player inventory
*/
int pickUpBonus(Player, ItemClass);

/**
remove the player from the game
*/
void removePlayer(Player);

/**
free a game
*/
void freeGame(Game);
#endif
