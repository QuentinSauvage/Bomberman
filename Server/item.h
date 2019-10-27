#ifndef ITEM_H
#define ITEM_H

enum itemClass{ CLASSIC, MINE, REMOTE, BOMB_UP, BOMB_DOWN, FIRE_POWER, SCOOTER, BROKEN_LEGS, MAJOR, LIFE_UP, LIFE_MAX };

struct item {
  enum itemClass iClass;
  int posX;
  int posY;
  int number;
};

typedef enum itemClass ItemClass;
typedef struct item *Item;

/**
Create a new Item
*/
Item createItem(ItemClass, int, int);

/**
Compare item class and position to a given Item
*/
int cmpItem(Item, ItemClass, int, int);

/**
Create a random Item
*/
Item createRandomItem(int, int);
#endif
