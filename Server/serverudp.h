#ifndef SERVERUDP_H
#define SERVERUDP_H

#include <netinet/in.h>

#define MAX_CONNECTION_UDP 256

struct serverudp {
	int fd;
	struct sockaddr_in srv;
	struct sockaddr_in cli;
	socklen_t len;
	void (*server_bind)(struct serverudp *, int);
	ssize_t (*server_receive)(struct serverudp *, char *, size_t);
	void (*server_send)(struct serverudp *, char *);
};

typedef struct serverudp *ServerUdp;

/**
Create an udp server
*/
ServerUdp server_create_udp();
/**
Close and free an udp server
*/
void server_close_and_free_udp(ServerUdp);

#endif
