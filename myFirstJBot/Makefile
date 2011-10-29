JAVAC=javac
JAVAC_ARGS=

SOURCES=\
 Ants.java \
 Bot.java \
 Aim.java \
 Ilk.java \
 Order.java \
 Tile.java \
 AbstractSystemInputParser.java \
 AbstractSystemInputReader.java \
 Impulse.java \
 SingleAnt.java \
 MyBot.java\
 ImpulseTypes.java\
 Overlord.java
 
CLASSES=$(SOURCES:%.java=%.class)
JAR=MyBot.jar

.PHONY: all clean

all: $(CLASSES) $(JAR)

$(JAR): $(CLASSES)
	jar cvfm $(JAR) Manifest.txt *.class

%.class: %.java
	$(JAVAC) $(JAVAC_ARGS) $<

clean:
	-rm -Rf $(CLASSES)
	-rm -Rf *.class
	-rm -Rf $(JAR)