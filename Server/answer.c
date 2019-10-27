#include <string.h>
#include <stdlib.h>
#include <stdio.h>
#include "globals.h"
#include "item.h"
#include "map.h"
#include "bomb.h"
#include "player.h"
#include "game.h"
#include "answer.h"
#include "convert.h"

/**
read argument name and valu from the request
*/
static void read_arg(char **req, char **name, char **value) {
  int cpt = 0;
  *name = (*req)++;
  for (; **req != ':' && **req != '\0'; (*req)++)
    ;
  *(*req)++ = '\0';
  *value = (*req);
  for (; **req != '}' && **req != '\0'; (*req)++) {
    if (**req == '"')
      cpt++;
    if (**req == ',' && !(cpt & 1))
      break;
  }
  *(*req)++ = '\0';
}

/**
Remove " from the argument
*/
static void cpy_arg(char *dest, char *src) {
  for (; *src; src++, dest++) {
    if (*src == '"')
      src++;
    *dest = *src;
  }
}

/**
Test if the char pos correspond to player position
*/
static int isPlayerPos(Player pla, char pos[ARG_VAL_SIZE]) {
  int x, y;
  char *s;
  for (s = pos; s && *s != ','; s++)
    ;
  *s++ = '\0';
  x = atoi(pos);
  y = atoi(s);
  return (x == pla->posX && y == pla->posY);
}

/**
Test if the string is a bomb class
*/
static int isBombClass(char *class) {
  int c;
  for (c = CLASSIC; c <= REMOTE; c++) {
    if (!strcmp(class, itemClassList[c]))
      return c;
  }
  return ERR;
}

/**
Test if the string is a bonus/malus class
*/
static int isBonusClass(char *class) {
  int c;
  for (c = BOMB_UP; c <= LIFE_MAX; c++) {
    if (!strcmp(class, itemClassList[c]))
      return c;
  }
  return ERR;
}

/**
GET game/list
*/
int get_game_list(char *ans, char *multiAns, char *req, Player pla) {
  gameListToJson(ans, BUFFER_SIZE);
  return 0;
}

/**
POST game/create
*/
int post_game_create(char *ans, char *multiAns, char *req, Player pla) {
  char *arg_name, *arg_value;
  char name[ARG_VAL_SIZE], map[ARG_VAL_SIZE];
  read_arg(&req, &arg_name, &arg_value);
  if (!strcmp(arg_name, JSON_VAR_NAME))
    cpy_arg(name, arg_value);
  else
    return 1;
  read_arg(&req, &arg_name, &arg_value);
  if (!strcmp(arg_name, JSON_VAR_MAP))
    cpy_arg(map, arg_value);
  else
    return 1;
  if (createGame(name, map, pla) == ERR)
    strcpy(ans, GAME_CREATE_ERROR);
  else {
    createGameToJson(pla, ans, BUFFER_SIZE);
  }
  return 0;
}

/**
POST game/join
*/
int post_game_join(char *ans, char *multiAns, char *req, Player pla) {
  char *arg_name, *arg_value;
  char name[ARG_VAL_SIZE];
  read_arg(&req, &arg_name, &arg_value);
  if (!strcmp(arg_name, JSON_VAR_NAME))
    cpy_arg(name, arg_value);
  else
    return 1;
  if (joinGame(name, pla) == ERR)
    strcpy(ans, GAME_JOIN_ERROR);
  else {
    joinGameToJson(pla, ans, BUFFER_SIZE);
    snprintf(multiAns, BUFFER_SIZE, GAME_NEW_PLAYER, pla->id + 1, pla->posX,
             pla->posY);
  }
  return 0;
}

/**
POST player/move
*/
int post_player_move(char *ans, char *multiAns, char *req, Player pla) {
  char *arg_name, *arg_value;
  char move[ARG_VAL_SIZE];
  Direction d;
  if (pla->gameId == -1)
    return 1;
  read_arg(&req, &arg_name, &arg_value);
  if (!strcmp(arg_name, JSON_VAR_MOVE))
    cpy_arg(move, arg_value);
  else
    return 1;
  for (d = UP; d <= RIGHT; d++) {
    if (!strcmp(move, directionList[d])) {
      if (!movePlayer(pla, d)) {
        snprintf(ans, BUFFER_SIZE, PLAYER_POSITION_UPDATE, pla->id + 1, move);
        snprintf(multiAns, BUFFER_SIZE, PLAYER_POSITION_UPDATE, pla->id + 1,
                 move);
        return 0;
      } else
        return 1;
    }
  }
  return 1;
}

/**
POST attack/bomb
*/
int post_attack_bomb(char *ans, char *multiAns, char *req, Player pla) {
  char *arg_name, *arg_value;
  char pos[ARG_VAL_SIZE], cla[ARG_VAL_SIZE], bonus[ARG_VAL_SIZE];
  int c, b;
  if (pla->gameId == -1)
    return 1;
  read_arg(&req, &arg_name, &arg_value);
  if (!strcmp(arg_name, JSON_VAR_POS))
    cpy_arg(pos, arg_value);
  else
    return 1;
  read_arg(&req, &arg_name, &arg_value);
  if (!strcmp(arg_name, JSON_VAR_CLASS))
    cpy_arg(cla, arg_value);
  else
    return 1;
  read_arg(&req, &arg_name, &arg_value);
  if (!strcmp(arg_name, JSON_VAR_BONUS))
    cpy_arg(bonus, arg_value);
  else
    return 1;
  if (!strcmp(bonus, boolean[0]))
    b = 1;
  else if (!strcmp(bonus, boolean[1]))
    b = 0;
  else
    return 1;
  if ((c = isBombClass(cla)) == ERR)
    return 1;
  if (isPlayerPos(pla, pos)) {
    if (!putBomb(pla, c, b)) {
      attackBombToJson(pla, ans, BUFFER_SIZE);
      snprintf(multiAns, BUFFER_SIZE, ATTACK_NEW_BOMB, pla->posX, pla->posY,
               itemClassList[c]);
      return 0;
    }
  }
  strcpy(ans, FORBIDDEN_BOMB_JSON);
  return 0;
}

/**
POST attack/remote/go
*/
int post_attack_remote_go(char *ans, char *multiAns, char *req, Player pla) {
  if (pla->gameId == -1)
    return 1;
  explodeRemotes(pla);
  return 0;
}

/**
POST object/new
*/
int post_object_new(char *ans, char *multiAns, char *req, Player pla) {
  char *arg_name, *arg_value;
  char type[ARG_VAL_SIZE], cla[ARG_VAL_SIZE];
  int c;
  if (pla->gameId == -1)
    return 1;
  read_arg(&req, &arg_name, &arg_value);
  if (!strcmp(arg_name, JSON_VAR_TYPE))
    cpy_arg(type, arg_value);
  else
    return 1;
  read_arg(&req, &arg_name, &arg_value);
  if (!strcmp(arg_name, JSON_VAR_CLASS))
    cpy_arg(cla, arg_value);
  else
    return 1;
  if (!strcmp(type, JSON_TYPE_BOMB)) {
    if ((c = isBombClass(cla)) == ERR)
      return 1;
    if (pickUpBomb(pla, c))
      return 1;
    objectNewToJson(pla, ans, BUFFER_SIZE);
    return 0;
  } else if (!strcmp(type, JSON_TYPE_BONUS)) {
    if ((c = isBonusClass(cla)) == ERR)
      return 1;
    if (pickUpBonus(pla, c))
      return 1;
    objectNewToJson(pla, ans, BUFFER_SIZE);
    return 0;
  }
  return 1;
}

/**
POST game/quit
*/
int post_game_quit(char *ans, char *multiAns, char *req, Player pla) {
  if (pla->gameId == -1)
    return 1;
  removePlayer(pla);
  gameQuitToJson(pla, ans, BUFFER_SIZE);
  gameQuitToJson(pla, multiAns, BUFFER_SIZE);
  return 0;
}

/**
Take the header of the request
*/
static char *get_action(char **req) {
  char *action;
  int i;
  for (i = 0, action = *req; action[i] != '\0'; i++)
    if (action[i] == '{' || action[i] == '\n') {
      action[i] = '\0';
      *req += i + 1;
      break;
    }
  return action;
}

/**
Test the header of the request and use the corresponding function
*/
void give_answer(char *ans, char *multiAns, char *req, Player pla) {
  char *action;
  int i;
  printf("request :%s\n", req);
  action = get_action(&req);
  for (i = 0; action_list[i]; i++) {
    if (!strcmp(action_list[i], action)) {
      if (function_list[i](ans, multiAns, req, pla))
        strcpy(ans, BAD_REQUEST);
      return;
    }
  }
  strcpy(ans, BAD_REQUEST);
}

