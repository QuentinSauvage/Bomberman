#ifndef CONVERT_H
#define CONVERT_H

/*Game*/
#define GAME_JSON_SKELETON "{\"name\":\"%s\",\"nbPlayer\":%d,\"map\":\"%s\"},"
#define GAME_LIST_JSON_SKELETON "{\"action\":\"game/list\",\"status\":\"200\",\"message\":\"ok\",\"numberGameList\":%d,"
#define GAME_LIST_JSON_BEGIN "\"games\":["
#define GAME_LIST_JSON_END "],"
#define CREATE_GAME_JSON_SKELETON "{\"action\":\"game/create\",\"status\":\"201\",\"message\":\"game created with %s\",\"nbPlayers\":%d,\"startPos\":\"%d,%d\","
#define JOIN_GAME_JSON_SKELETON_BEGIN "{\"action\":\"game/join\",\"status\":\"201\",\"nbPlayers\":%d,\"players\":["
#define JOIN_GAME_JSON_SKELETON_END "],\"startPos\":\"%d,%d\","
#define OBJECT_NEW_JSON "{\"status\":201,\"action\":\"object/new\","
#define GAME_QUIT "POST game/quit{\"player\":%d}\n"
#define ATTACK_EXPLOSE_BEGIN "POST attack/explose{\"pos\":\"%d,%d\",\"type\":\"%s\",\"bonus\":\"%s\","
#define ATTACK_EXPLOSE_BONUS "\"bonusMalus\":["
#define ATTACK_EXPLOSE_BOMB "],\"bomb\":["
#define ATTACK_EXPLOSE_ITEM "{\"pos\":\"%d,%d\",\"type\":\"%s\"},"
#define ATTACK_EXPLOSE_CHAIN "\"chain\":[]}\n"
/*Item*/
#define ITEM_JSON_SKELETON "{\"class\":\"%s\",\"number\":%d},"
/*Map*/
#define MAP_JSON_SKELETON "\"map\":{\"width\":%d,\"height\":%d,\"content\":\""
#define MAP_LESS_JSON_SKELETON "\"map\":\""
/*Player*/
#define PLAYER_JSON_SKELETON "\"player\":{\"id\":%d,\"life\":%d,\"maxLife\":%d,\"speed\":%d,\"currentNbClassicBomb\":%d,\"currentNbMine\":%d,\"currentNbRemoteBomb\":%d,\"maxNbBomb\":%d,"
#define PLAYER_BONUS_LIST_JSON "\"bonusMalus\":["
#define PLAYER_POS_JSON_SKELETON "{\"id\":%d,\"pos\":\"%d,%d\"},"
#define ATTACK_BOMB_JSON_SKELETON "{\"action\":\"attack/bomb\",\"status\":\"201\",\"player\":{"
#define PLAYER_LESS_JSON_SKELETON "\"life\":%d,\"maxLife\":%d,\"speed\":%d,\"currentNbClassicBomb\":%d,\"currentNbMine\":%d,\"currentNbRemoteBomb\":%d,\"maxNbBomb\":%d,"
#define ATTACK_AFFECT "POST attack/affect{"

extern int nbGame;
extern char *itemClassList[];
extern int snprintf(char *str, size_t size, const char *format, ...);

/*Game*/
/**
"{\"name\":\"%s\",\"nbPlayer\":%d,\"map\":\"%s\"},"
*/
int gameToJson(Game, char *, int);

/**
"{"action":"game/list","status":"200","message":"ok"..."
*/
void gameListToJson(char *, int);

/**
"{"action":"game/create","status":"201","message":"game created with %s"..."
*/
void createGameToJson(Player, char *, int);

/**
"{"action":"game/join","status":"201",..."
*/
void joinGameToJson(Player, char *, int);

/**
"{"status":201,"action":"object/new",..."
*/
void objectNewToJson(Player, char *, int);

/**
"POST game/quit{"player":%d}"
*/
void gameQuitToJson(Player, char *, int);

/**
"{"class":"%s","number":%d},"
*/
int itemToJson(Item, char *, int);

/*Map*/

/**
""map":{"width":%d,"height":%d,"content":..."
*/
int mapToJson(Map, char *, int);
int mapLessToJson(Map, char *, int);
/*Player*/
/**
"{"id":%d,"pos":"%d,%d"},{"id":%d,"pos":"%d,%d"},"
*/
int playerListToJson(Game, char *, int);

/**
""player":{"id":%d,"life":%d,..."
*/
int playerToJson(Player, char *, int);

/**
""life":%d,"maxLife":%d,..."
*/
int playerLessToJson(Player, char *, int);

/**
"{"id":%d,"pos":"%d,%d"},"
*/
int playerPosToJson(Player, char *, int);

/**
"{"action":"attack/bomb","status":"201","player":{"
*/
int attackBombToJson(Player, char *, int);

/**
 "POST attack/affect{..."
*/
void playerAffectToJson(Player, char *, int);
#endif
