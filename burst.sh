#!/bin/bash



echo "le nombre de clients testés : "
read nombre
java -cp ./build/ jvn.JvnCoordImpl &

sleep 10

#java -cp ./build/ test.SimulClient 1 &

#sleep 1

#java -cp ./build/ test.SimulClient 2 &

#sleep 1

#java -cp ./build/ test.SimulClient 3 &


for i in `seq 1 $nombre`
do
	java -cp ./build/ test.SimulClient $i &
	sleep 1
done
