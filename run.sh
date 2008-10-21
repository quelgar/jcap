#!/bin/sh

if [ ! $1 ]
then
    echo "Must specify interface to listen to"
    exit 1
fi

jcapdir=`dirname $0`

sudo java -ea -classpath "$jcapdir/out/production/Jcap" \
  -Djava.library.path="$jcapdir/out/native" \
   com.me.lodea.jcap.example.SimpleDump "$1"
