/**
* TP 2
* \file server.h
* \brief Gestion du serveur .h
* \author Quentin Sauvage
*/

#ifndef SERVER_H
#define SERVER_H

/* header ============================================================== */
#include <netinet/in.h>
/* macros ============================================================== */

/* constants =========================================================== */

/* structures ========================================================== */
struct server {
	int fd;
	struct sockaddr_in cli;
	struct sockaddr_in srv;
	socklen_t len;
	void (*server_bind)(struct server *, int);
	ssize_t (*server_receive)(struct server *, char *, size_t);
	void (*server_send)(struct server *, char *);
};
/* types =============================================================== */
typedef struct server *Server;
/* functions =========================================================== */
Server server_create_udp();
void server_close_and_free(Server);

#endif
