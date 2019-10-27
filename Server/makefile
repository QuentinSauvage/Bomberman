.PHONY: clean

CC = gcc
CFLAGS = -ansi -pedantic -Wall -Wextra -g

server:   main.c servertcp.o serverudp.o globals.o answer.o game.o map.o player.o bomb.o item.o convert.o
	$(CC) -o $@ $^ -lpthread

%.o: %.c
	$(CC) $(CFLAGS) -o $@ -c $<

clean:
	rm -f *.o server
