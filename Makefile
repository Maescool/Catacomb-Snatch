#
# Catacomb-Snatch
#

# Usage
# Run "make jar" to create a redistributable jar file

distdir = dist
distjar = $(distdir)/Catacomb-Snatch.jar

all:
	@make clean
	@make jar
	@make clean-class

class:
	@echo "[CLASS BUILD] >"
	find src/ -name '*.java' -exec javac -classpath lib/CodecJOrbis.jar:lib/CodecWav.jar:lib/LibraryJavaSound.jar:lib/SoundSystem.jar:res/ '{}' '+'

jar: class | lib/.tmp $(distdir)
	@echo "[JAR BUILDER] >"
	cd lib/.tmp && find ../ -maxdepth 1 -name '*.jar' -exec jar xf '{}' ';'
	jar cfe $(distjar) com.mojang.mojam.MojamComponent -C src/ . -C res/ .
	jar ufe $(distjar) com.mojang.mojam.MojamComponent -C lib/.tmp/ .

lib/.tmp:
	mkdir -p lib/.tmp

$(distdir):
	mkdir -p $(distdir)

clean:
	@make clean-class clean-jar
	

clean-jar:
	@echo "[CLEAN JAR]   : \c"
	rm -rf $(distjar) lib/.tmp

clean-class:
	@echo "[CLEAN CLASS] : \c"
	find src/ -name '*.class' -exec rm '{}' '+'

.PHONY: all jar clean
