# javanaise1
DIDES Julien - NGUYEN Minh Trung

Le javanaise1 fonctionne et est synchronisé.
Le coordinateur n'est pas encore optimisé.
Le button Unlock a été ajouté à IRC pour pouvoir tester manuellement et débugger notre implémentation.
Il reste à implémenter les méthodes jvnTerminate(dans le JvnServerImpl et le JvnCoordImpl).
Il reste à construitre un burst pour le vraiment tester.


#javanaise2
Le javanaise 2 est implémenté et fonctionnel 


Le méthode jvnTerminate est implémenté dans le JvnServerImpl et le JvnCoordImpl

Pour le burst, nous avons un problème lorsque nous effectuons une boucle infinie.Donc, nous avons ajouté un temps d'attente (sleep) .Ensuite, afin d'exécuter ce test, nous avons écrit un script shell qui permet de lancer le coordinateur et les clients, l'ID de chaque client est passé en paramètre 


Pour l'extension du javanaise2, nous avons cherché à étendre le prototype réalisé pour la gestion de la saturation du cache local.En suite, nous avons traité le problème de pannes des clients et aussi de panne du coordinateur.


