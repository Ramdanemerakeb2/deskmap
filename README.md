# DeskMap v1.0

Desktop version of map browers such as Google Maps

This software was developped as an assignment during the 2019/20 university year at the CERI, Avignon University (France), by the following students:
* Ghemmour Amar 
* Merakeb Ramdane
* Lakhdari Zine Eddine 
* Harkouken Idir

# Organisation du code source
>Les classes principales:
- Launcher.java: Est la classe principale qui contient et gérer l'interface.
- Map java: Est la classe qui gérer la carte
- MapCreator.java: Est la classe qui permet de créer la carte a partir d'un parametre (Ville). Si la carte est enregistrer dans le cache et la charge.
- OverPass Relation: La classe qui gère les Relation OSM
- OverPassWay: La classe qui gère les chemins OSM
- OverPassNode: La classe qui gère les points OSM
- Dijkstra: la classe qui fait le calcul du plus court chemin
- OverPassLigneBusTramIrigo : la classe qui permet de récupérer les informations des arrêts de bus de la ville d'angers 
- busLive : la classe qui permet de récupérer les informations des bus en temps réel

# Installation
Pour installer le logiciel il suffit de télécharger le fichier [DeskMap_v2.jar]
# Ressources : 
- RAM: 1024 Mb
- RAM Minimale :800 Mb


# Exécution et utilisation

Pour exécuter le logiciel il faut importer tous le projet puis exécuter la commande suivante
```sh
$ java -jar DeskMap_v2.jar
```
-- Note: les deux icons buslive.png et icon.png soient dans le meme emplacement que DeskMap_v2.jar --
# Dépendances

- Java 8

# Bibliographie
 - Documentation OSM - OpenStreetMap. https://wiki.openstreetmap.org/wiki/Main_Page
 - OverPass API - OpenStreetMap. https://wiki.openstreetmap.org/wiki/Overpass_API
 - JavaFX Documentation - Java. https://docs.oracle.com/javase/8/javafx/api/toc.html
 - Dijkstra Algorithm - Geeksforgeeks. https://www.geeksforgeeks.org/dijkstras-shortest-path-algorithm-greedy-algo-7/
 - API de la ville d'angers : https://data.angers.fr/explore/dataset/bus-tram-topologie-dessertes/information/
 	https://data.angers.fr/explore/dataset/bus-tram-position-tr/api/
 	
 