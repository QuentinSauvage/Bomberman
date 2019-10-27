#include <sys/types.h>
#include <dirent.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <unistd.h>
#include <stdlib.h>
#include <string.h>
#include <stdio.h>
#include "globals.h"
#include "item.h"
#include "map.h"

/**
Create a map
*/
static Map createMap(char *name, int width, int height,
                     char content[MAP_MAX_WIDTH][MAP_MAX_WIDTH]) {
  Map m;
  int i;
  m = (Map)malloc(sizeof(struct map));
  strcpy(m->name, name);
  m->width = width;
  m->height = height;
  m->content = (char **)malloc(sizeof(char *) * height);
  for (i = 0; i < height; i++) {
    m->content[i] = (char *)malloc(sizeof(char) * width);
    strcpy(m->content[i], content[i]);
  }
  return m;
}

/**
Try to break a wall at the given position in the map
*/
int breakWall(Map m, int posX, int posY) {
  if (posX >= 0 && posX <= m->width - 1 && posY >= 0 && posY <= m->height - 1) {
    if (m->content[posY][posX] == '_')
      return ERR;
    if (m->content[posY][posX] == '*') {
      m->content[posY][posX] = '_';
      return 0;
    }
  }
  return 1;
}

/**
Free a map
*/
void freeMap(Map m) {
  int i;
  for (i = 0; i < m->height; i++)
    free(m->content[i]);
  free(m->content);
  free(m);
}

/**
Read the map file content
*/
static Map readMap(char *name) {
  int fd, readed, i;
  char buf[MAP_MAX_WIDTH], content[MAP_MAX_WIDTH][MAP_MAX_WIDTH],
      filePath[MAP_NAME_SIZE];
  strcat(filePath, MAP_DIR);
  strcat(filePath, "/");
  strcat(filePath, name);
  if ((fd = open(filePath, O_RDONLY)) == ERR) {
    syserror(7);
    return NULL;
  }
  if ((read(fd, buf, MAP_MAX_WIDTH)) <= 0)
    return NULL;
  for (readed = 0; buf[readed] != '\n'; readed++)
    if (!buf[readed])
      return NULL;
  strncpy(content[0], buf, readed);
  lseek(fd, readed + 1, SEEK_SET);
  for (i = 1; read(fd, buf, readed + 1) > 0; i++)
    strncpy(content[i], buf, readed);
  return createMap(name, readed, i, content);
}

/**
Search if the given map name exists
*/
Map searchMap(char *name) {
  DIR *dir;
  struct dirent *file;
  if ((dir = opendir(MAP_DIR)) == NULL) {
    syserror(8);
    return NULL;
  }
  while ((file = readdir(dir)) != NULL)
    if (!strcmp(name, file->d_name))
      return readMap(file->d_name);
  closedir(dir);
  return NULL;
}

/**
Search all the maps in the map directory and format it
*/
int mapListToJson(char *buf, int size) {
  DIR *dir;
  struct dirent *file;
  int writed, total = 0;
  char *start = buf;
  if ((dir = opendir(MAP_DIR)) == NULL) {
    syserror(8);
    return ERR;
  }
  strncpy(start, MAP_JSON_LIST_BEGIN, size);
  writed = strlen(MAP_JSON_LIST_BEGIN);
  start += writed;
  size -= writed;
  total += writed;
  while ((file = readdir(dir)) != NULL) {
    if (*(file->d_name) != '.') {
      strncpy(start, file->d_name, size);
      writed = strlen(file->d_name);
      start += writed;
      size -= writed;
      total += writed;
      strncpy(start, ",", size);
      start++, size++, total++;
    }
  }
  strcpy(start - 1, "]");
  closedir(dir);
  return total;
}

/**
Test if a map cell is free
*/
int isFree(Map m, int posX, int posY) {
  return m->content[posY][posX] == EMPTY_CELL;
}

/**
Give the starting position of a new player
*/
void getStartPosition(Map m, int numPlayer, int *x, int *y) {
  switch (numPlayer) {
  case 1:
    *x = 0, *y = m->height - 1;
    break;
  case 2:
    *x = m->width - 1, *y = 0;
    break;
  case 3:
    *x = m->width - 1, *y = m->height - 1;
    break;
  default:
    *x = 0, *y = 0;
    break;
  }
}

