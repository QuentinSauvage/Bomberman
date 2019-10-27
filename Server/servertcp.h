#ifndef SERVERTCP_H
#define SERVERTCP_H

#include <netinet/in.h>

#define MAX_CONNECTION_TCP 256

struct servertcp {
	int fd;
	int new_fd[MAX_CONNECTION_TCP];
	int nbConnection;
	struct sockaddr_in srv;
	struct sockaddr_in cli[MAX_CONNECTION_TCP];
	socklen_t len;
	void (*server_bind)(struct servertcp *, int);
	void (*server_listen)(struct servertcp *);
	int (*server_accept)(struct servertcp *);
	ssize_t (*server_receive)(int, char *, size_t);
	int (*server_send)(int, char *);
};

typedef struct servertcp *ServerTcp;

/**
Create a tcp server
*/
ServerTcp server_create_tcp();
/**
Close and free a tcp server
*/
void server_close_and_free_tcp(ServerTcp);

#endif
