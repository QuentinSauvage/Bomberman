# Bomberman
Projet réalisé en 1ère année de master informatique.

## Présentation du projet

L'utilisateur lance l'application et arrive sur le menu. Il peut soit consulter les options (qui sont vides), soit changer les contrôles du jeu, soit chercher un serveur.
Lorsque le joueur cherche un serveur, il reçoit la liste des serveurs disponibles, en sélectionne un, puis choisis ensuite de créer ou rejoindre une partie sur ce serveur, après quoi la partie se lance.
Le jeu ne comporte aucune animation, donc l'élément _speed_ du joueur n'est pas pris en compte. Concernant le protocole, nous avons décidé de ne pas utiliser l'élément _chain_ lors de l'explosion de plusieurs bombes dues à une autre explosion.
A la place, nous avons préféré renvoyer un message d'explosion au client. 

## Lancer le projet

Lancer _./server_ en ligne de commandes pour le serveur, et double cliquer sur run.bat pour lancer le client.

## Architecture client

Le menu et le jeu sont découpées selon le modèle _MVC_, les informations telles que les différents types de bombes, de bonus ou d'actions sont présents dans leur classe Enum respective. Une factory remaniée a été utilisée plusieurs fois, pour appeler par exemple une méthode de control ou de lecture json. Par exemple, recevoir l'action _position/update_ appellera les méthodes _readPositionUpdate_ et _controlPositionUpdate_, sans avoir besoin de passer par un if.
Un singleton a été utilisé pour représenter les classes _GameHandler_, _JsonReader_, _JsonWriter_, et _SpriteLoader_.

## Architecture server

Le server peut être découpé en trois couches : La couche réseau avec les fichiers _servertcp_ et _serverudp_ qui permettent de dialoguer avec les clients, la couche de traitement avec les fichiers _answer_ et _convert_ qui permettent d'analyser les requêtes et de construire les réponses, et la couche logique qui va englober tous les fichiers qui stockent les données de jeu et réaliser les requêtes. Lors de la conception, seules les fonctions du fichier _main_ permettaient de faire la liaison entre ces couches, mais lors du développement il s'est avéré qu'il était nécessaire de lier la couche logique à la couche réseau via la structure correspondant au joueur.

## Bugs côté client

-Concernant les contrôles, ceux-ci sont un peu bugués : une touche peut être configurée plusieurs fois (entraine des erreurs durant la partie), et certaines touches comme les flèches directionnelles ne peuvent être affectées.

-Parfois les fenêtres ne se centrent pas au milieu de l'écran, surtout l'écran de fin de partie.

-Quand un joueur rejoint une partie, la totalité des joueurs voient leur sprite redessinés, ce qui mène à une duplication des sprites pour les joueurs déjà présents.

-La seule information disponible durant la partie est la vie du joueur. Nous voulions afficher toutes les informations du joueur dans un panel à côté, mais cela provoquer un bug au niveau du _keyListener_ qui ne reconnaissait plus aucun évènement.

-L'application ne se ferme pas d'elle-même lorsqu'une __partie__ (et pas le menu) est quittée. Il faut donc interrompre l'application manuellement. 

-Les erreurs _Bad Request_ du serveur sont affichées dans la console pour prévenir l'utilisateur. Lors d'un déplacement impossible, une erreur est donc affichée. Il ne s'agit pas d'un bug mais juste d'une information.

-Lorsqu'un joueur rejoint une partie, sa vie n'est pas visible tant qu'il n'a pas effectué d'action.

-Le protocole ne permet pas à un joueur d'avoir connaissance du fait qu'un autre joueur a ramassé un bonus. Nous aurions éventuellement pu corriger cela en vérifiant si le déplacement de tous les joueurs provoquaient un ramassage d'objet, mais dans tous les cas il est impossible de savoir depuis un client si les autres joueurs ont pu ramasser cet objet.

## Bugs côté serveur

-Lorsqu'un joueur marche sur une mine, il arrive parfois que la zone mémoire correspondante à cette mine soit libérée plusieurs fois après son explosion ce qui provoque une erreur.

-Le serveur n'utilise pas de sémaphore pour isoler des sections critiques, ce qui peut entrainer des bugs si le serveur traite plusieurs requêtes qui accédent à la même variable en même temps.

-Les différentes listes permettant de stocker les informations sont définies avec des tailles statiques, ce qui force le serveur a rejeter certaines demandes dans la plupart des cas ou éventuellement de provoquer un dépassement de capacité.

-La liste des cartes disponibles est obtenue en listant tous les fichiers contenus dans le répertoire concerné sans vérifier leur contenu, et par conséquent envoyer au client une carte ne correspondant pas au format attendu.

-Sur certaine machine (uniquement des machines virtuelles ?), il arrive que lorsque le premier client se connecte au serveur et souhaite créer une partie, le serveur ne parvient pas à accéder au repertoire "Map" contenant les cartes de jeu. Ce problème peut être régler de manière simple en relancant un nouveau client.

Membres : __ROMON__ Emile, __SAUVAGE__ Quentin
