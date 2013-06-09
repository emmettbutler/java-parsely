GSONJAR = lib/gson-2.2.4.jar
TESTJAR = lib/junit-4.10.jar
JFLAGS = -g
JC = javac
.SUFFIXES: .java .class


CLASSES = \
        src/Parsely.java \
        src/ParselyAPIConnection.java \
        src/ParselyUser.java \
        src/ParselyModel.java \
        src/RequestOptions.java \
        src/Secret.java

.java.class:
		$(JC) $(JFLAGS) -classpath ".:$(GSONJAR)" $(CLASSES)

TESTCLASSES = $(CLASSES) src/Tests.java

default: classes

tests:
		$(JC) $(JFLAGS) -classpath ".:$(GSONJAR):$(TESTJAR)" $(TESTCLASSES)

classes: $(CLASSES:.java=.class)

clean:
		$(RM) *.class .*.swp .*.un~ src/*.class src/.*.swp src/.*.un~
