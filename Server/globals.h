#ifndef GLOBALS_H
#define GLOBALS_H

#define ERR -1
#define NO_ERROR 0
#define SOCKET_ERROR 1
#define BIND_ERROR 2
#define LISTEN_ERROR 3
#define ACCEPT_ERROR 4
#define RECEIVE_ERROR 5
#define SEND_ERROR 6
#define MAX_CONNECTION_ERROR 9

#define BUFFER_SIZE 8192
#define UDPPORT 3945
#define TCPPORT 1418

#define SEARCH_MSG "looking for bomberstudent servers"
#define SEARCH_ANSWER "i'm a bomberstudent server"

#define syserror(x) perror(myErr[x])
#define fatalsyserror(x) syserror(x), exit(x)


extern char *itemClassList[];
extern char *boolean[];
extern char *myErr[];


#endif
