#include <unistd.h>
#include <stdlib.h>
#include <string.h>
#include <stdio.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <netinet/in.h>

#define ERR -1

int main() {
	int s = socket(AF_INET, SOCK_STREAM, 0);
	if(s == ERR) {
		perror("Error socket");
		exit(1);
	}
	struct sockaddr_in srv;
	memset(&srv,0,sizeof(struct sockaddr_in));
	srv.sin_family = AF_INET;
	srv.sin_addr.s_addr = INADDR_ANY;
	srv.sin_port = htons(2409);
	if(bind(s,(struct sockaddr *) &srv, sizeof(struct sockaddr_in)) == ERR) {
		close(s);
		perror("Bind"),exit(2);
	}
	if(listen(s,SOMAXCONN) == ERR) {
		close(s);
		perror("Listen"),exit(3);
	}
	int new_socket;
	struct sockaddr_in clt;
	socklen_t len = sizeof(struct sockaddr_in);
	if((new_socket = accept(s, (struct sockaddr *) &clt, &len)) == ERR) {
		close(s);
		perror("Accept"),exit(4);
	}
	char buffer[500];
	if(recv(new_socket, buffer, 500, 0) == ERR) {
		close(s),close(new_socket);
		perror("Receive"),exit(5);
	}
	printf("%s", buffer);
	char *answer = "{'status', 200}";
	if(send(new_socket, answer, strlen(answer), 0) == ERR) {
		close(new_socket),close(s);
		perror("Send"),exit(6);
	}
	close(new_socket),close(s),exit(0);
}

//librairie pthread
//select
