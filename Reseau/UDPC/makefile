.PHONY : clean

CC = gcc
CFLAGS = -g
#-ansi -Wall -W -pedantic
LDFLAGS =
LIBS =

# Finding #
SRC = $(wildcard *.c)
OBJS = $(SRC:.c=.o)


# Rules

all : server client

server: server.o error.o main.o
		gcc -o server server.o error.o main.o

client: client.o error.o main_client.o
		gcc -o client client.o error.o main_client.o

%.o : %.c
	$(CC) $(CFLAGS) -o $@ -c $<

clean:
	@rm -f server client *.o
