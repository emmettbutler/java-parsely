GSONJAR = lib/gson-2.2.4.jar
TESTJAR = lib/junit-4.10.jar
JFLAGS = -g
JC = javac
.SUFFIXES: .java .class


CLASSES = \
        Parsely.java \
        ParselyAPIConnection.java \
        ParselyModel.java \
        RequestOptions.java \
        Secret.java

.java.class:
		$(JC) $(JFLAGS) -classpath ".:$(GSONJAR)" $(CLASSES)

TESTCLASSES = \
        Parsely.java \
        ParselyAPIConnection.java \
        ParselyModel.java \
        RequestOptions.java \
        Secret.java \
        Tests.java

default: classes

tests:
		$(JC) $(JFLAGS) -classpath ".:$(GSONJAR):$(TESTJAR)" $(TESTCLASSES)

classes: $(CLASSES:.java=.class)

clean:
		$(RM) *.class .*.swp .*.un~
