PLAT = linux
INCLUDEPATH = -I$(JAVA_HOME)/include  -I$(JAVA_HOME)/include/$(PLAT) -I/usr/java/include/ -I/usr/java/include/solaris/

JAVA_FLAGS = -g


Javacyc.class: Javacyc.java libunixdomainsocket.so
	javac $(JAVA_FLAGS) Javacyc.java

libunixdomainsocket.so: UnixDomainSocket.o
	ld -G -z text -o libunixdomainsocket.so UnixDomainSocket.o

UnixDomainSocket.o: UnixDomainSocket.c UnixDomainSocket.h
	gcc -Wall -fPIC -c $(INCLUDEPATH) UnixDomainSocket.c

UnixDomainSocket.h: UnixDomainSocket$1.class
	javah UnixDomainSocket

UnixDomainSocket$1.class: UnixDomainSocket.java
	javac $(JAVA_FLAGS) UnixDomainSocket.java

test: Javacyc.class JavacycTest.java
	javac $(JAVA_FLAGS) JavacycTest.java

stress: Javacyc.class
	javac $(JAVA_FLAGS) StressTest.java
	java StressTest


clean:
	rm -f *.class *.so *.o
