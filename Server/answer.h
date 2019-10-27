#ifndef ANSWER_H
#define ANSWER_H

#define ARG_VAL_SIZE 256
#define BAD_REQUEST "{\"status\":\"400\",\"message\": \"Bad request\"}\n"
#define UNKNOWN_ERROR "{\"status\":\"520\",\"message\": \"Unknown Error\"}\n"
#define GAME_CREATE_ERROR "{\"action\":\"game/create\",\"status\":\"501\",\"message\":\"cannot create game\"}\n"
#define GAME_JOIN_ERROR "{\"action\":\"game/join\",\"status\":\"501\",\"message\":\"cannot join the game\"}\n"
#define JSON_VAR_NAME "\"name\""
#define JSON_VAR_MAP "\"map\""
#define JSON_VAR_MOVE "\"move\""
#define JSON_VAR_POS "\"pos\""
#define JSON_VAR_CLASS "\"class\""
#define JSON_VAR_BONUS "\"bonus\""
#define JSON_VAR_TYPE "\"type\""
#define PLAYER_POSITION_UPDATE "POST player/position/update{\"player\":%d,\"dir\":\"%s\"}\n"
#define GAME_NEW_PLAYER "POST game/newplayer{\"id\":%d,\"pos\":\"%d,%d\"}\n"
#define ATTACK_NEW_BOMB "POST attack/newbomb{\"pos\":\"%d,%d\",\"class\":\"%s\"}\n"
#define FORBIDDEN_BOMB_JSON "{\"action\":\"attack/bomb\",\"status\":\"403\",\"message\":\"forbidden action\"}\n"
#define JSON_TYPE_BOMB "bomb"
#define JSON_TYPE_BONUS "bonusMalus"

/**
Test the header of the request and use the corresponding function
*/
void give_answer(char *, char *, char *, Player);

/**
GET game/list
*/
int get_game_list(char *, char *, char *, Player);

/**
POST game/create
*/
int post_game_create(char *, char *, char *, Player);

/**
POST game/join
*/
int post_game_join(char *, char *, char *, Player);

/**
POST player/move
*/
int post_player_move(char *, char *, char *, Player);

/**
POST attack/bomb
*/
int post_attack_bomb(char *, char *, char *, Player);

/**
POST attack/remote/go
*/
int post_attack_remote_go(char *, char *, char *, Player);

/**
POST object/new
*/
int post_object_new(char *, char *, char *, Player);

/**
POST game/quit
*/
int post_game_quit(char *, char *, char *, Player);

char *directionList[] = { "up", "down", "left", "right"};
char *itemClassList[] = { "classic", "mine", "remote", "bomb_up", "bomb_down", "fire_power", "scooter", "broken_legs", "major", "life_up", "life_max"};
char *boolean[] = { "true", "false"};
char *action_list[] = {"GET game/list",
                       "POST game/create",
                       "POST game/join",
                       "POST player/move",
                       "POST attack/bomb",
                       "POST attack/remote/go",
                       "POST object/new",
                       "POST game/quit",
                       NULL};
int (*function_list[])(char *, char*, char *, Player) = {get_game_list,
                                   post_game_create,
                                   post_game_join,
                                   post_player_move,
                                   post_attack_bomb,
                                   post_attack_remote_go,
                                   post_object_new,
                                   post_game_quit};
#endif
