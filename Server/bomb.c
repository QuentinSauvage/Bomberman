#include <stdlib.h>
#include "bomb.h"

/**
Create a new bomb
*/
Bomb createBomb(int x, int y, int ownerId, int gameId, int bonus) {
  Bomb b = (Bomb)malloc(sizeof(struct bomb));
  b->posX = x;
  b->posY = y;
  b->ownerId = ownerId;
  b->gameId = gameId;
  b->bonus = bonus;
  return b;
}

