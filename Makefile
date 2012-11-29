#
# Catacomb-Snatch
#

# Usage
# Run "make" or "make jar" to create a redistributable jar file
# Run "make clean" or "make clean-class" to clean up files created by make

distdir = dist
distjar = $(distdir)/Catacomb-Snatch.jar

all: jar

class:
	find src/ -name '*.java' -exec javac -classpath lib/CodecJOrbis.jar:lib/CodecWav.jar:lib/LibraryJavaSound.jar:lib/SoundSystem.jar:lib/LibraryLWJGLOpenAL.jar:lib/lwjgl.jar:lib/kryonet-1.04-all.jar:lib/jruby.jar:lib/jython.jar:lib/jinput.jar:res/ '{}' '+'

jar: class | lib/.tmp $(distdir)
	cd lib/.tmp && find ../ -maxdepth 1 -name '*.jar' -exec jar xf '{}' ';'
	jar cfe $(distjar) com.mojang.mojam.MojamStartup -C src/ . -C res/ .
	jar ufe $(distjar) com.mojang.mojam.MojamStartup -C lib/.tmp/ .

lib/.tmp:
	mkdir -p lib/.tmp

$(distdir):
	mkdir -p $(distdir)

clean: clean-class clean-jar

clean-jar:
	rm -rf $(distjar) lib/.tmp

clean-class:
	find src/ -name '*.class' -exec rm '{}' '+'

.PHONY: all class jar clean clean-jar clean-class
