/**
* TP 2
* \file error.h
* \brief Gestion des erreurs .h
* \author Quentin Sauvage
*/

#ifndef ERROR_H
#define ERROR_H

/* header ============================================================== */
#include <stdio.h>
/* macros ============================================================== */
#define syserror(m,e) (perror(m), exit(e))
#define neterror(n) syserror(myErr[n],n)
/* constants =========================================================== */
#define ERR -1
#define NO_ERROR 0
#define SOCKET_ERROR 1
#define BIND_ERROR 2
/* types =============================================================== */

/* structures ========================================================== */
extern char *myErr[];
/* functions =========================================================== */


#endif
