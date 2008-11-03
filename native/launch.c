
#include <sys/types.h>
#include <unistd.h>
#include <stdio.h>

#include <jni.h>


int main(int const argc, const char* argv[]) {
    
    // must be started with root privileges
    if (geteuid() != 0) {
        puts("JCap requires root privileges");
        return 666;
    }
    
    // immediately drop root privileges
    // they will be restored when opening a live pcap capture
    if (seteuid(getuid()) < 0) {
        puts("Failed to set euid to uid!");
        return 1;
    }
    
    // launch the JVM
    JavaVMInitArgs vm_args;
    vm_args.version = JNI_VERSION_1_2;
    vm_args.ignoreUnrecognized = JNI_TRUE;
    JavaVMOption options[2];
    options[0].optionString = "-Djava.class.path=" CLASSPATH;
    options[1].optionString = "-Djava.library.path=" LIBPATH;
    vm_args.options = options;
    vm_args.nOptions = 2;
    JavaVM* jvm;
    JNIEnv* env;
    puts("Creating JVM");
    if (JNI_CreateJavaVM(&jvm, (void**)&env, &vm_args) < 0) {
        puts("Failed to create JVM");
        return 2;
    }
    jclass const stringClass = (*env)->FindClass(env, "java/lang/String");
    if (stringClass == NULL) {
        goto exception;
    }
    jobjectArray const javaArgs = (*env)->NewObjectArray(env, argc - 1,
        stringClass, NULL);
    if (javaArgs == NULL) {
        goto exception;
    }
    for (int i = 1; i < argc; i++) {
        jstring const javaString = (*env)->NewStringUTF(env, argv[i]);
        if (javaString == NULL) {
            goto exception;
        }
        (*env)->SetObjectArrayElement(env, javaArgs, i - 1, javaString);
        if ((*env)->ExceptionOccurred(env) != NULL) {
            goto exception;
        }
    }
    jclass const mainClass = (*env)->FindClass(env, MAINCLASS);
    if (mainClass == NULL) {
        goto exception;
    }
    jmethodID const mainMethod = (*env)->GetStaticMethodID(env, mainClass, "main", "([Ljava/lang/String;)V");
    if (mainMethod == NULL) {
        goto exception;
    }
    puts("Calling main");
    (*env)->CallStaticVoidMethod(env, mainClass, mainMethod, javaArgs);
    if ((*env)->ExceptionOccurred(env) != NULL) {
        goto exception;
    }

    puts("Destroying JVM");
    (*jvm)->DestroyJavaVM(jvm);
    return 0;
    
    exception:
    (*env)->ExceptionDescribe(env);
    (*jvm)->DestroyJavaVM(jvm);
    return 2;
}