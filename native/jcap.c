
#include "jcap_jni.h"
#include <pcap.h>
#include <stdlib.h>
#include <string.h>
#include <errno.h>
#include <sys/types.h>
#include <unistd.h>

#ifndef NDEBUG
#include <stdio.h>
#endif

const char* const NATIVE_PTR_NAME = "nativePointer";

typedef struct _jcap_state
{
    pcap_t* handle;
} jcap_state;

#ifdef JCAP_64BIT
#define LONG_AS_PTR(l) ((jcap_state*)l)
#define PTR_AS_LONG(p) ((jlong)p)
#else
#define LONG_AS_PTR(l) ((jcap_state*)(int)l)
#define PTR_AS_LONG(p) ((jlong)(int)p)
#endif

typedef struct _callback_args
{
    JNIEnv* env;
    jobject jcap;
    jclass eventClass;
    jmethodID eventCtorID;
    jmethodID fireMethodID;
} callback_args;


static void throwJCapException(JNIEnv* const env, const char* msg)
{
    const jclass exClass = (*env)->FindClass(env, "com/me/lodea/jcap/JCapException");
    (*env)->ThrowNew(env, exClass, msg);
}

static jcap_state* getNativePointer(JNIEnv* const env, jobject const obj)
{
    const jclass cl = (*env)->GetObjectClass(env, obj);
    const jfieldID nativePtrID = (*env)->GetFieldID(env, cl,
        NATIVE_PTR_NAME, "J");
    if (nativePtrID == NULL)
    {
        return NULL;
    }
    return LONG_AS_PTR((*env)->GetLongField(env, obj, nativePtrID));
}

static jstring getInterfaceString(JNIEnv* const env, jobject const obj)
{
    const jclass cl = (*env)->GetObjectClass(env, obj);
    const jfieldID ifaceID = (*env)->GetFieldID(env, cl, "iface",
        "Ljava/net/NetworkInterface;");
    if (ifaceID == NULL)
    {
        return NULL;
    }
    const jobject iface = (*env)->GetObjectField(env, obj, ifaceID);
    const jmethodID getNameID = (*env)->GetMethodID(env,
        (*env)->GetObjectClass(env, iface), "getName",
        "()Ljava/lang/String;");
    if (getNameID == NULL)
    {
        return NULL;
    }
    const jstring ifaceString = (*env)->CallObjectMethod(env, iface, getNameID);
    if ((*env)->ExceptionOccurred(env) != NULL)
    {
        return NULL;
    }
    return ifaceString;
}

void packet_callback(u_char* const charArgs,
    const struct pcap_pkthdr* const header, const u_char* const packet)
{
    const callback_args* const args = (const callback_args*)charArgs;
    JNIEnv* const env = args->env;
    if ((*env)->ExceptionOccurred(env) != NULL)
    {
        return;
    }

    const jlong timeStamp = ((jlong)header->ts.tv_sec)*1000L +
        ((jlong)header->ts.tv_usec)/1000L;
    const jobject buffer = (*env)->NewDirectByteBuffer(env, (void*)packet, header->caplen);
    if (buffer == NULL)
    {
        return;
    }
    const jobject event = (*env)->NewObject(env, args->eventClass,
        args->eventCtorID, args->jcap, timeStamp, header->len, buffer);
    (*env)->DeleteLocalRef(env, buffer);
    if (event == NULL)
    {
        return;
    }
    (*env)->CallVoidMethod(env, args->jcap, args->fireMethodID, event);
}


/*
 * Class:     com_me_lodea_jcap_JCapSession
 * Method:    getDefaultInterface
 * Signature: ()Ljava/net/NetworkInterface;
 */
JNIEXPORT jobject JNICALL Java_com_me_lodea_jcap_JCapSession_getDefaultInterface
  (JNIEnv* const env, const jclass clazz)
{
    char errorbuf[PCAP_ERRBUF_SIZE];
    const char* const dev = pcap_lookupdev(errorbuf);
    if (dev == NULL)
    {
        throwJCapException(env, errorbuf);
        return NULL;
    }
    const jclass ifaceClass = (*env)->FindClass(env, "java/net/NetworkInterface");
    if (ifaceClass == NULL)
    {
        return NULL;
    }
    const jmethodID factory = (*env)->GetStaticMethodID(env, ifaceClass,
        "getByName", "(Ljava/lang/String;)Ljava/net/NetworkInterface;");
    if (factory == NULL)
    {
        return NULL;
    }
    const jstring ifaceName = (*env)->NewStringUTF(env, dev);
    if (ifaceName == NULL)
    {
        return NULL;
    }
    return (*env)->CallStaticObjectMethod(env, ifaceClass, factory, ifaceName);
}

/*
 * Class:     com_me_lodea_jcap_JCapSession
 * Method:    pcapOpen
 * Signature: (Ljava/lang/String;IZI)V
 */
JNIEXPORT void JNICALL Java_com_me_lodea_jcap_JCapSession_pcapOpen
  (JNIEnv * const env, jobject const obj, jstring const ifaceString,
  jstring const dumpFileString, jboolean const promisc, jint const snaplen,
  jint const timeout)
{
#ifndef NDEBUG
    setlinebuf(stdout);
#endif
    const jclass cl = (*env)->GetObjectClass(env, obj);
    const jfieldID nativePtrID = (*env)->GetFieldID(env, cl,
        NATIVE_PTR_NAME, "J");
    if (nativePtrID == NULL)
    {
        return;
    }
    jcap_state* const self = (jcap_state*)malloc(sizeof(jcap_state));
    if (self == NULL)
    {
        /* out of memory */
        const jclass exClass = (*env)->FindClass(env, "java/lang/OutOfMemoryError");
        (*env)->ThrowNew(env, exClass, strerror(errno));
        return;
    }
    (*env)->SetLongField(env, obj, nativePtrID, PTR_AS_LONG(self));
    
    char errorbuf[PCAP_ERRBUF_SIZE];
    if (ifaceString != NULL)
    {
        const char* const ifaceName = (*env)->GetStringUTFChars(env,
            ifaceString, NULL);
        if (ifaceName == NULL)
        {
            free(self);
            return;
        }

        // set effective user ID to root, which is required to open a live capture
        uid_t const initial_euid = geteuid();
        if (seteuid(0) < 0)
        {
            free(self);
            const jclass exClass = (*env)->FindClass(env, "com/me/lodea/jcap/JCapPermissionException");
            (*env)->ThrowNew(env, exClass, "Root access required for live capture");
            return;
        }
        self->handle = pcap_open_live((char*)ifaceName, snaplen, promisc, timeout,
            errorbuf);
        int const drop_root_result = seteuid(initial_euid);
        (*env)->ReleaseStringUTFChars(env, ifaceString, ifaceName);
        if (drop_root_result < 0)
        {
            free(self);
            const jclass exClass = (*env)->FindClass(env, "java/lang/IllegalStateException");
            (*env)->ThrowNew(env, exClass, "Unable to drop root privileges");
            return;
        }
    }
    else
    {
        const char* const dumpFile = (*env)->GetStringUTFChars(env,
            dumpFileString, NULL);
        if (dumpFile == NULL)
        {
            free(self);
            return;
        }
        self->handle = pcap_open_offline((char*)dumpFile, errorbuf);
        (*env)->ReleaseStringUTFChars(env, dumpFileString, dumpFile);
    }
    if (self->handle == NULL)
    {
        throwJCapException(env, errorbuf);
        return;
    }
}

/*
 * Class:     com_me_lodea_jcap_JCapSession
 * Method:    setFilter
 * Signature: (Ljava/lang/String;)V
 */
JNIEXPORT void JNICALL Java_com_me_lodea_jcap_JCapSession_setFilter
  (JNIEnv* const env, const jobject obj, const jstring filter)
{
    const jcap_state* self = getNativePointer(env, obj);
    if (self == NULL)
    {
        return;
    }

    const jstring ifaceString = getInterfaceString(env, obj);
    if (ifaceString == NULL)
    {
        return;
    }
    const char* const ifaceName = (*env)->GetStringUTFChars(
        env, ifaceString, NULL);
    if (ifaceName == NULL)
    {
        return;
    }
    char errorbuf[PCAP_ERRBUF_SIZE];
    bpf_u_int32 net;
    bpf_u_int32 mask;
    int pcapResult = pcap_lookupnet((char*)ifaceName, &net, &mask, errorbuf);
    (*env)->ReleaseStringUTFChars(env, ifaceString, ifaceName);
    if (pcapResult < 0)
    {
        throwJCapException(env, errorbuf);
        return;
    }
    const char* const filterUTF8 = (*env)->GetStringUTFChars(
        env, filter, NULL);
    if (filterUTF8 == NULL)
    {
        return;
    }
    struct bpf_program bpfProg;
    pcapResult = pcap_compile(self->handle, &bpfProg,
        (char*)filterUTF8, 1, mask);
    (*env)->ReleaseStringUTFChars(env, filter, filterUTF8);
    if (pcapResult < 0)
    {
        throwJCapException(env, pcap_geterr(self->handle));
        return;
    }
    pcapResult = pcap_setfilter(self->handle, &bpfProg);
    if (pcapResult < 0)
    {
        throwJCapException(env, pcap_geterr(self->handle));
        return;
    }
    pcap_freecode(&bpfProg);
}

/*
 * Class:     com_me_lodea_jcap_JCapSession
 * Method:    capture
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_com_me_lodea_jcap_JCapSession_capture
  (JNIEnv* const env, jobject const obj, jint const maxPackets)
{
    const jcap_state* self = getNativePointer(env, obj);
    if (self == NULL)
    {
        return -1;
    }

    callback_args args;
    args.env = env;
    args.jcap = obj;
    args.eventClass = (*env)->FindClass(env, "com/me/lodea/jcap/PacketEvent");
    if (args.eventClass == NULL)
    {
        return -1;
    }
    args.eventCtorID = (*env)->GetMethodID(env, args.eventClass, "<init>",
        "(Lcom/me/lodea/jcap/JCapSession;JILjava/nio/ByteBuffer;)V");
    if (args.eventCtorID == NULL)
    {
        return -1;
    }
    args.fireMethodID = (*env)->GetMethodID(env,
        (*env)->GetObjectClass(env, obj), "firePacketEvent",
        "(Lcom/me/lodea/jcap/PacketEvent;)V");
    if (args.fireMethodID == NULL)
    {
        return -1;
    }
    const int result = pcap_dispatch(self->handle, maxPackets, packet_callback,
        (u_char*)&args);
    if (result < 0)
    {
        throwJCapException(env, pcap_geterr(self->handle));
    }
    return result;
}

/*
 * Class:     com_me_lodea_jcap_JCapSession
 * Method:    pcapClose
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_com_me_lodea_jcap_JCapSession_pcapClose
  (JNIEnv* const env, jobject const obj)
{
    jcap_state* const self = getNativePointer(env, obj);
    if (self == NULL)
    {
        return;
    }
    pcap_close(self->handle);
    free(self);
}
