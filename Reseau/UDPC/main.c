#include <stdio.h>
#include <string.h>
#include "error.h"
#include "server.h"
#define MAX 500

int main(){
  char buf[MAX];
  char *msg=NULL;
  ssize_t n;
  Server s = server_create_udp();
  //bind server
  s->server_bind(s, 8080);
  for(;;){
	memset(buf,0,sizeof(char) * MAX);
    //réception d'un message:
    n=s->server_receive(s, buf, MAX);
    if(n==-1) break;
    //buf[n]='\n';
    if(strncmp(buf, "PING", 4) == 0) //si le message est PING alors on retourne au client PONG
      msg="PONG";
    else //Sinon le message à retourner est PAS PONG
      msg="PAS PONG";
    printf("%s\n",buf);
    //On envoie le msg
    s->server_send(s, msg);
  }
  return 0;
}
