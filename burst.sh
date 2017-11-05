#!/bin/bash



echo "le nombre de clients testés : "
read nombreClients
echo "le nombre d'objets partagés : "
read nombreObjects
java -cp ./build/ jvn.JvnCoordImpl &

sleep 10

for i in `seq 1 $nombreClients`
do
	java -cp ./build/ test.SimulClient $i $nombreObjects &
	sleep 1
done
