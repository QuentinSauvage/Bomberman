#ifndef BOMB_H
#define BOMB_H

#define MINE_DMG 0.60
#define CLASSIC_DMG 0.40
#define CLASSIC_TIME 4

struct bomb {
  int posX;
  int posY;
  int ownerId;
  int gameId;
  int bonus;
};

typedef struct bomb *Bomb;

/**
Create a new bomb
*/
Bomb createBomb(int, int, int, int, int);

#endif
