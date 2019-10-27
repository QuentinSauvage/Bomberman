/**
* TP 2
* \file server.c
* \brief Gestion du serveur .c
* \author Quentin Sauvage
*/

/* header ============================================================== */
#include <unistd.h>
#include <stdlib.h>
#include <string.h>
#include <stdio.h>
#include <sys/types.h>
#include <sys/socket.h>
#include "error.h"
#include "server.h"
/* macros (private)============================================================== */

/* constants (private)=========================================================== */

/* types (private)=============================================================== */

/* structures (private)========================================================== */

/* private functions =========================================== */
static void server_bind(Server this, int port) {
	this->srv.sin_family = AF_INET;
	this->srv.sin_port = htons((uint16_t) port);
	this->srv.sin_addr.s_addr = INADDR_ANY;
	this->len = sizeof(struct sockaddr_in);
	if(bind(this->fd, (struct sockaddr *)&this->srv, this->len) == ERR) {
		neterror(BIND_ERROR);
	}
}

static ssize_t server_receive_udp(Server this, char *buf, size_t size) {
	return recvfrom(this->fd, buf, size, 0, (struct sockaddr *)&this->cli, &this->len) == ERR;
}

static void server_send_udp(Server this, char *msg) {
	if(sendto(this->fd, msg, strlen(msg), 0, (struct sockaddr *)&this->cli, this->len) == ERR) {
		neterror(BIND_ERROR);
	}
}
/* public functions =========================================== */
Server server_create_udp() {
	Server s = (Server) malloc(sizeof(struct server));
	if((s->fd = socket(PF_INET, SOCK_DGRAM, 0)) == ERR) {
		free(s);
		neterror(SOCKET_ERROR);
	}
	s->server_bind = server_bind;
	s->server_receive = server_receive_udp;
	s->server_send = server_send_udp;
	memset(&s->srv,0,sizeof(struct sockaddr_in));
	return s;
}

void server_close_and_free(Server this) {
	close(this->fd);
	free(this);
}
