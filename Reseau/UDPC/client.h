/**
* TP 2
* \file client.h
* \brief Gestion du client .h
* \author Quentin Sauvage
*/

#ifndef CLIENT_H
#define CLIENT_H

/* header ============================================================== */
#include <netinet/in.h>
/* macros ============================================================== */

/* structures ========================================================== */
struct client {
	int fd;
	struct sockaddr_in c_addr;
	socklen_t len;
	ssize_t (*client_receive)(struct client *, char *, size_t);
	void (*client_send)(struct client *, char *);
};
/* types =============================================================== */
typedef struct client *Client;
/* functions =========================================================== */
Client client_create_udp(char *, int);
void client_close_and_free(Client);

#endif
