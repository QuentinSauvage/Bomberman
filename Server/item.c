#include <stdlib.h>
#include <stdio.h>
#include <string.h>
#include <time.h>
#include "item.h"

extern char *itemClassList[];

/**
Create a new Item
*/
Item createItem(ItemClass iClass, int posX, int posY) {
  Item i = (Item)malloc(sizeof(struct item));
  i->iClass = iClass;
  i->posX = posX;
  i->posY = posY;
  i->number = 1;
  return i;
}

/**
Compare item class and position to a given Item
*/
int cmpItem(Item i1, ItemClass iClass, int posX, int posY) {
  return (i1->iClass == iClass && i1->posX == posX && i1->posY == posY);
}

/**
Create a random Item
*/
Item createRandomItem(int posX, int posY) {
  int r;
  r = rand() % 21;
  if (r < 5)
    return createItem(CLASSIC, posX, posY);
  if (r < 9)
    return createItem(MINE, posX, posY);
  if (r < 13)
    return createItem(REMOTE, posX, posY);
  return createItem(r - 12, posX, posY);
}

