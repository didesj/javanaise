# javanaise1
DIDES Julien - NGUYEN Minh Trung

Le javanaise1 fonctionne et est synchronisé.
Le coordinateur n'est pas encore optimisé.
Le button Unlock a été ajouté à IRC pour pouvoir tester manuellement et débugger notre implémentation.
Il reste à implémenter les méthodes jvnTerminate(dans le JvnServerImpl et le JvnCoordImpl).
Il reste à construitre un burst pour le vraiment tester.


#javanaise2
Le javanaise 2 est implémenté et fonctionnel
Coté client, il suffit de créer une interface coorespondant à une classe 'paratagé' et préciser avec les annotations sur les méthodes : @LockType(lockType = "write") et @LockType(lockType = "read")
Pour préciser le type de lock souhaité (read ou write)

Maintenant pour créer ou chercher un objet patager il suffit de faire appel à la méthodle JvnProxy.getOrNewInstance(name, object);
exemple : ISentence sentence = (ISentence) JvnProxy.getOrNewInstance("IRC"+i, new Sentence());


Le burst, est fonctionnel et permet de choisir le nombre de client et d'objets partagés. Donc, nous avons ajouté un temps d'attente (sleep) .Ensuite, afin d'exécuter ce test, nous avons écrit un script shell qui permet de lancer le coordinateur et les clients, l'ID de chaque client est passé en paramètre 
Pour le fichier burst.sh il faut préciser le dossier qui contient les fichier class (à la place de ./build/)


Pour l'extension du javanaise2 : 
	- nous avons fait la gestion de la saturation du cache local (la limite de la mémoire local est de 10 objets) : 
		- si la mémoire est saturé c'est l'objet qui a été unlock le plus anciennement qui est supprimé (et qui n'a pas de lockread ou lockwrite)
	- nous avons traité le problème de pannes des clients
	- nous avons traité le problème de pannes du coordinateur (si le coordinateur )

Les méthodes jvnTerminate dans le JvnServerImpl et le JvnCoordImpl sont implémentés ( elles n'était pas implémentés dans javanaise1)


