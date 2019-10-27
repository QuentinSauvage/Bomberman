#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "error.h"
#include "client.h"
#define SIZE 20
/* private functions =========================================== */

static void get_msg(char* msg){
   //récupére un message de l'utilisateur
   scanf("%s", msg);
}

/* public functions =========================================== */

int main(){
    //Création d'un client
    Client clt = client_create_udp("127.0.0.1", 8080);
    //les buffers
    char buffer_send[SIZE];
    char buffer_recv[SIZE];
    ssize_t n;
    //initialisation à 0 des buffers
    for(;;) {
		memset(buffer_send, 0, sizeof(char *)*SIZE);
		memset(buffer_recv, 0, sizeof(char *)*SIZE);
        get_msg(buffer_send);
        //si le message est "exit" on quitte
        if(strncmp(buffer_send, "exit", 4) == 0)
            break;
        //on envoie le message
        clt->client_send(clt, buffer_send);
        //on reçoit
        n = clt->client_receive(clt, buffer_recv, SIZE);
		//buffer_recv[n] = '\n';
        //on affiche
        printf("%s\n", buffer_recv);
    }
    client_close_and_free(clt);
    exit(0);
}
