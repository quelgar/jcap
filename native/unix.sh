#!/bin/sh

if [ ! -d "$JAVA_HOME" ]
then
    echo "JAVA_HOME must be set"
    exit 1
fi

args=`getopt do: $*`
if [ $? != 0 ]
then
    echo "Usage: $0 [-d] [-o output directory]"
    exit 1
fi
jcapdir=`dirname $0`/..
set -- $args
outdir="$jcapdir/out/native"
debug=0
for i
do
    case "$i"
    in
        -o)
            outdir="$2"
            shift;
            shift;;
        -d)
            debug=1
            shift;;
        --)
            shift
            break;;
    esac
done

mkdir -p "$jcapdir/out/native" || exit 2
$JAVA_HOME/bin/javah -classpath "$jcapdir/out/production/Jcap" \
    -o "$jcapdir/out/native/jcap_jni.h" \
    com.me.lodea.jcap.JCapSession || exit 3

if [ $debug ]
then
    DEBUG=-DNDEBUG
fi

platform=`uname`

if [ "$platform" = "Darwin" ]
then
    gcc -Wall -Werror -std=c99 -dynamiclib $DEBUG \
        -I "$JAVA_HOME/include" -I "$jcapdir/out/native" \
        -o "$outdir/libjcap.jnilib" "$jcapdir/native/jcap.c" -lpcap
fi
