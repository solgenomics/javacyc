 /* UnixDomainSocket.c                          */
/* J-BUDS version 1.0                          */
/* Copyright (c) 2001; Echogent Systems, Inc.  */
/* See COPYRIGHT file for license details      */

/* Modified by Thomas Yan on 05/29/2003 to compile on cc on Solaris */ 
/* Modified by Thomas Yan on 08/02/2004 nativeClose should call close instead
   of shutdown to close the socket since shutdown does not actually close
   a socket. */

#include <jni.h>
#include "UnixDomainSocket.h"

#include <sys/socket.h>
#include <sys/types.h>
#include <sys/un.h>
#include <sys/unistd.h>
#include <unistd.h>
#include <stdio.h>
#include <stdlib.h>
#include <errno.h>
#include <strings.h>


JNIEXPORT jint JNICALL Java_UnixDomainSocket_nativeOpen(JNIEnv *jEnv, jclass jClass, jstring jSocketFile)
{
	struct sockaddr_un serverAddress;
	int serverAddressLength;
	int socketFileHandle;

/* 	printf("open\n");	/\* debug *\/ */
	
	/* Convert the Java socket file String to a C string */
	const char *socketFile = (*jEnv)->GetStringUTFChars(jEnv, jSocketFile, 0);
	
	/* Create the server address */
	bzero((char *)&serverAddress,sizeof(serverAddress));
	serverAddress.sun_family = AF_UNIX;
	strcpy(serverAddress.sun_path, socketFile);
	serverAddressLength = strlen(serverAddress.sun_path) + sizeof(serverAddress.sun_family); 	
	
	/* Create the socket */
	if( (socketFileHandle = socket(AF_UNIX, SOCK_STREAM, 0)) < 0)
	{
	        /* Return error */
/* 	  printf("ERRNO: %d\n", errno); */
	  perror("open");
		return -1;
	}

	if(connect(socketFileHandle, (struct sockaddr *)&serverAddress, serverAddressLength) <0)
	{
	        /* Return error */
/* 	  printf("ERRNO: %d\n", errno); */
	  perror("open");
		return -1;
	}
	
	/* Release the C socket file string */
	(*jEnv)->ReleaseStringUTFChars(jEnv, jSocketFile, socketFile);
	
	/* Return the socket file handle */
	return socketFileHandle;
}

JNIEXPORT jint JNICALL Java_UnixDomainSocket_nativeRead(JNIEnv *jEnv, jclass jClass, jint jSocketFileHandle)
{
        /* Create the char buffer */
	char buffer[1];
	
	/* Read a byte from the socket into the buffer */
	int result = read(jSocketFileHandle, buffer, 1);
	
/* 	fprintf(stderr, "The return value is: %d\n", result);  */

	/* If the result is less than 0, return the error */
	if(result<=0)  /** code modified here to also catch 0 **/
	{
	  return -1;
/* 		return result; */
	}
	/* Otherwise, return the data read */
	else
	{	
        return buffer[0];
 	}

}

JNIEXPORT jint JNICALL Java_UnixDomainSocket_nativeWrite(JNIEnv *jEnv, jclass jClass, jint jSocketFileHandle, jint jData)
{
        /* Create the char buffer and put the data in it */
        /*char buffer[] = {jData}; */
        char buffer[1];
	int result;
        buffer[0] = jData;

	/* Write a byte from the buffer to the socket */
	result = write(jSocketFileHandle, buffer, 1);

	/* Return the result */
	return result;
}

JNIEXPORT void JNICALL Java_UnixDomainSocket_nativeClose(JNIEnv *jEnv, jclass jClass, jint jSocketFileHandle)
{
        /* Close the socket */
	close(jSocketFileHandle);
}

JNIEXPORT void JNICALL Java_UnixDomainSocket_nativeCloseInput(JNIEnv *jEnv, jclass jClass, jint jSocketFileHandle)
{
        /* Close the socket input stream */
	shutdown(jSocketFileHandle, SHUT_RD);
}

JNIEXPORT void JNICALL Java_UnixDomainSocket_nativeCloseOutput(JNIEnv *jEnv, jclass jClass, jint jSocketFileHandle)
{
        /* Close the socket output stream */
	shutdown(jSocketFileHandle, SHUT_WR);
}
