#
# Catacomb-Snatch
#

# Usage
# Run "make jar" to create a redistributable jar file

distdir = dist
distjar = $(distdir)/Catacomb-Snatch.jar

all:
	find src/ -name '*.java' -exec javac -classpath lib/CodecJOrbis.jar:lib/CodecWav.jar:lib/LibraryJavaSound.jar:lib/SoundSystem.jar:res/ '{}' '+'

jar: all | lib/.tmp $(distdir)
	cd lib/.tmp && find ../ -maxdepth 1 -name '*.jar' -exec jar xf '{}' ';'
	jar cfe $(distjar) com.mojang.mojam.MojamComponent -C src/ . -C res/ .
	jar ufe $(distjar) com.mojang.mojam.MojamComponent -C lib/.tmp/ .

lib/.tmp:
	mkdir -p lib/.tmp

$(distdir):
	mkdir -p $(distdir)

clean:
	find src/ -name '*.class' -exec rm '{}' '+'
	rm -rf $(distjar) lib/.tmp

.PHONY: all jar clean
