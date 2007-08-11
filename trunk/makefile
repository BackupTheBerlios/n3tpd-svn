JAVAC   = javac
JAVA    = java
SOURCES = n3tpd/*.java

all:
	$(JAVAC) -cp . -d ../bin/ $(SOURCES)

run:
	cd ../bin/
	$(JAVA) -cp . n3tpd/Main
	cd ../svn/

clean:
	rm -R ../bin/*
