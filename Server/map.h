#ifndef MAP_H
#define MAP_H


#define MAP_DIR "map"
#define MAP_JSON_LIST_BEGIN "\"maps\":["
#define EMPTY_CELL '_'
#define SOLID_WALL_CELL '-'
#define WALL_CELL '*'
#define MAX_MAP 6
#define MAP_NAME_SIZE 256
#define MAP_MAX_WIDTH 126

struct map {
  char name[MAP_NAME_SIZE];
  int width;
  int height;
  char **content;
};

typedef struct map *Map;

/**
Search if the given map name exists
*/
Map searchMap(char *);

/**
Try to break a wall at the given position in the map
*/
int breakWall(Map, int, int);
/**
Free a map
*/
void freeMap(Map);
/**
Search all the maps in the map directory and format it
*/
int mapListToJson(char *, int);
/**
Test if a map cell is free
*/
int isFree(Map, int, int);
/**
Give the starting position of a new player
*/
void getStartPosition(Map, int, int *, int *);

#endif
