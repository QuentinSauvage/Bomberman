/**
* TP 2
* \file client.c
* \brief Gestion du client .c
* \author Quentin Sauvage
*/

/* header ============================================================== */
#include <unistd.h>
#include <stdlib.h>
#include <string.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <arpa/inet.h>
#include "error.h"
#include "client.h"
/* macros (private)============================================================== */

/* constants (private)=========================================================== */

/* types (private)=============================================================== */

/* structures (private)========================================================== */

/* private functions =========================================== */
static ssize_t client_receive_udp(Client this, char* buf, size_t size) {
	if(!buf) return 0;
	return recvfrom(this->fd, buf, size, 0, (struct sockaddr *)&this->c_addr, &this->len) == ERR;
}

static void client_send_udp(Client this, char *msg) {
	if(sendto(this->fd, msg, strlen(msg), 0, (struct sockaddr *)&this->c_addr, this->len) == ERR) {
		neterror(BIND_ERROR);
	}
}
/* public functions =========================================== */
Client client_create_udp(char *addr, int port) {
  Client c = (Client) malloc(sizeof(struct client));
	if((c->fd = socket(PF_INET, SOCK_DGRAM, 0)) == ERR) {
		free(c);
		neterror(SOCKET_ERROR);
	}
	c->c_addr.sin_family = AF_INET;
	c->c_addr.sin_port = htons(port);
	c->c_addr.sin_addr.s_addr = INADDR_ANY;
	c->client_receive = client_receive_udp;
	c->client_send = client_send_udp;
	c->len = sizeof(struct sockaddr_in);
	
	if(!inet_aton(addr,&c->c_addr.sin_addr)) {
		neterror(SOCKET_ERROR);
	}
	return c;
}

void client_close_and_free(Client this) {
  close(this->fd);
	free(this);
}
