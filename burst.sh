#!/bin/bash

java -cp ./build/ jvn.JvnCoordImpl &

sleep 10

java -cp ./build/ test.SimulClient 1 &

sleep 1

java -cp ./build/ test.SimulClient 2 &

sleep 1

java -cp ./build/ test.SimulClient 3 &
