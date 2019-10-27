#include <unistd.h>
#include <stdlib.h>
#include <string.h>
#include <stdio.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <errno.h>
#include "globals.h"
#include "servertcp.h"

/**
Bind the tcp server to the port
*/
static void server_bind(ServerTcp this, int port) {
  this->srv.sin_family = AF_INET;
  this->srv.sin_port = htons(port);
  this->srv.sin_addr.s_addr = INADDR_ANY;
  this->len = sizeof(struct sockaddr_in);
  if (bind(this->fd, (struct sockaddr *)&this->srv, this->len) == ERR) {
    close(this->fd);
    fatalsyserror(BIND_ERROR);
  }
}

/**
Make server listening to incomming connection
*/
static void server_listen(ServerTcp this) {
  if (listen(this->fd, SOMAXCONN) == ERR) {
    close(this->fd);
    fatalsyserror(LISTEN_ERROR);
  }
}

/**
accept 1 connection to the server
*/
static int server_accept(ServerTcp this) {
  socklen_t len = sizeof(struct sockaddr_in);
  if (this->nbConnection == MAX_CONNECTION_TCP) {
    syserror(MAX_CONNECTION_ERROR);
    return ERR;
  }
  if ((this->new_fd[this->nbConnection] =
           accept(this->fd, (struct sockaddr *)&this->cli[this->nbConnection],
                  &len)) == ERR) {
    close(this->fd);
    fatalsyserror(ACCEPT_ERROR);
  }
  return this->nbConnection++;
}

/**
read incoming message from the tcp server
*/
static ssize_t server_receive_tcp(int fd, char *buf, size_t size) {
  ssize_t len;
  if ((len = recv(fd, buf, size, 0)) == ERR) {
    close(fd);
    syserror(RECEIVE_ERROR);
  }
  return len;
}

/**
Send message to the tcp client
*/
static int server_send_tcp(int fd, char *msg) {
  if (send(fd, msg, strlen(msg), MSG_NOSIGNAL) == ERR) {
    close(fd);
    syserror(SEND_ERROR);
    return ERR;
  }
  return 0;
}

/**
Create a tcp server
*/
ServerTcp server_create_tcp() {
  ServerTcp s = (ServerTcp)malloc(sizeof(struct servertcp));
  if ((s->fd = socket(AF_INET, SOCK_STREAM, 0)) == ERR) {
    free(s);
    fatalsyserror(SOCKET_ERROR);
  }
  s->nbConnection = 0;
  s->server_bind = server_bind;
  s->server_listen = server_listen;
  s->server_accept = server_accept;
  s->server_receive = server_receive_tcp;
  s->server_send = server_send_tcp;
  memset(&s->srv, 0, sizeof(struct sockaddr_in));
  return s;
}

/**
Close and free a tcp server
*/
void server_close_and_free_tcp(ServerTcp this) {
  int i;
  close(this->fd);
  this->server_bind = NULL;
  this->server_receive = NULL;
  this->server_send = NULL;
  for (i = 0; i < this->nbConnection; i++) {
    close(this->new_fd[i]);
  }
  free(this);
}

