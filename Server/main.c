#include <unistd.h>
#include <stdlib.h>
#include <string.h>
#include <stdio.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <pthread.h>
#include <time.h>
#include "globals.h"
#include "item.h"
#include "player.h"
#include "serverudp.h"
#include "servertcp.h"
#include "globals.h"
#include "map.h"
#include "bomb.h"
#include "game.h"
#include "convert.h"

extern int give_answer(char *, char *, char *, Player);
ServerTcp tcpServ;

/**
Send to all other player in the game
*/
static void sendMultiAnswer(Player pla, char *buf) {
  int i;
  if (pla->gameId != -1 && buf[0])
    for (i = 0; i < MAX_PLAYER; i++)
      if (game_list[pla->gameId]->player_list[i] &&
          game_list[pla->gameId]->player_list[i]->id != pla->id)
        playerSend(game_list[pla->gameId]->player_list[i], buf);
}

/**
Wait for a message, and try to answer to it
*/
void *run(void *arg) {
  char buffer[BUFFER_SIZE];
  char answer[BUFFER_SIZE];
  char multiAnswer[BUFFER_SIZE];
  int i;
  Player pla = (Player)malloc(sizeof(struct player));
  pla->fd = *(int *)arg;
  pla->gameId = -1;
  for (;;) {
    memset(buffer, 0, BUFFER_SIZE);
    memset(answer, 0, BUFFER_SIZE);
    memset(multiAnswer, 0, BUFFER_SIZE);
    playerReceive(pla, buffer, BUFFER_SIZE);
    give_answer(answer, multiAnswer, buffer, pla);
    if (playerSend(pla, answer) == ERR) {
      if (pla->gameId != -1) {
        removePlayer(pla);
        gameQuitToJson(pla, multiAnswer, BUFFER_SIZE);
        sendMultiAnswer(pla, multiAnswer);
      }
      free(pla);
      return NULL;
    }
    sendMultiAnswer(pla, multiAnswer);
    gameQuitToJson(pla, multiAnswer, BUFFER_SIZE);
    if (!strcmp(answer, multiAnswer))
      pla->gameId = -1;
  }
}

/**
Wait for tcp connection to accept and create a a run thread for each of them
*/
void *runTcp(void *arg) {
  pthread_t runThreads[MAX_CONNECTION_TCP];
  int cliNum, i = 0;
  tcpServ = server_create_tcp();
  tcpServ->server_bind(tcpServ, TCPPORT);
  tcpServ->server_listen(tcpServ);
  for (;;) {
    if ((cliNum = tcpServ->server_accept(tcpServ)) != ERR) {
      printf("tcp connection from %d port : %d\n",
             tcpServ->cli[cliNum].sin_addr.s_addr,
             tcpServ->cli[cliNum].sin_port);
      pthread_create(&runThreads[i], NULL, run, &tcpServ->new_fd[cliNum]);
      pthread_detach(runThreads[i]);
      i++;
    }
  }
  server_close_and_free_tcp(tcpServ);
}

/**
Wait for udp connection and send the answer if the client is searching for a
bomberstudent server
*/
void runUdp() {
  ServerUdp udpServ;
  char buffer[BUFFER_SIZE];
  udpServ = server_create_udp();
  udpServ->server_bind(udpServ, UDPPORT);
  for (;;) {
    udpServ->server_receive(udpServ, buffer, BUFFER_SIZE);
    printf("%s\n", buffer);
    if (!strncmp(buffer, SEARCH_MSG, strlen(SEARCH_MSG))) {
      printf("udp connection from %d port : %d\n", udpServ->cli.sin_addr.s_addr,
             udpServ->cli.sin_port);
      udpServ->server_send(udpServ, SEARCH_ANSWER);
    }
  }
  server_close_and_free_udp(udpServ);
}

/**
Main function
*/
int main() {
  pthread_t tcpThread;
  srand(time(NULL));
  pthread_create(&tcpThread, NULL, runTcp, NULL);
  pthread_detach(tcpThread);
  runUdp();
  return 0;
}
