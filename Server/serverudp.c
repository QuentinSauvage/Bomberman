#include <unistd.h>
#include <stdlib.h>
#include <string.h>
#include <stdio.h>
#include <sys/types.h>
#include <sys/socket.h>
#include "globals.h"
#include "serverudp.h"

/**
Bind udp server
*/
static void server_bind(ServerUdp this, int port) {
  this->srv.sin_family = AF_INET;
  this->srv.sin_port = htons((uint16_t)port);
  this->srv.sin_addr.s_addr = INADDR_ANY;
  this->len = sizeof(struct sockaddr_in);
  if (bind(this->fd, (struct sockaddr *)&this->srv, this->len) == ERR) {
    fatalsyserror(BIND_ERROR);
  }
}

/**
read incoming message from the udp server
*/
static ssize_t server_receive_udp(ServerUdp this, char *buf, size_t size) {
  return recvfrom(this->fd, buf, size, 0, (struct sockaddr *)&this->cli,
                  &this->len) == ERR;
}

/**
Send message to the udp client
*/
static void server_send_udp(ServerUdp this, char *msg) {
  if (sendto(this->fd, msg, strlen(msg), 0, (struct sockaddr *)&this->cli,
             this->len) == ERR) {
    server_close_and_free_udp(this);
    syserror(BIND_ERROR);
  }
}

/**
Create an udp server
*/
ServerUdp server_create_udp() {
  ServerUdp s = (ServerUdp)malloc(sizeof(struct serverudp));
  if ((s->fd = socket(PF_INET, SOCK_DGRAM, 0)) == ERR) {
    free(s);
    fatalsyserror(SOCKET_ERROR);
  }
  s->server_bind = server_bind;
  s->server_receive = server_receive_udp;
  s->server_send = server_send_udp;
  memset(&s->srv, 0, sizeof(struct sockaddr_in));
  return s;
}

/**
Close and free an udp server
*/
void server_close_and_free_udp(ServerUdp this) {
  close(this->fd);
  this->server_bind = NULL;
  this->server_receive = NULL;
  this->server_send = NULL;
  free(this);
}

