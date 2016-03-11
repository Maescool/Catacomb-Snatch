#
# Catacomb-Snatch
#

# Usage
# Run "make" or "make jar" to create a redistributable jar file
# Run "make clean" or "make clean-class" to clean up files created by make

distdir = dist
distjar = $(distdir)/Catacomb-Snatch.jar

all: jar | natives

class:
	find src/ -name '*.java' -exec javac -classpath lib/CodecJOrbis.jar:lib/CodecWav.jar:lib/LibraryJavaSound.jar:lib/SoundSystem.jar:lib/LibraryLWJGLOpenAL.jar:lib/lwjgl.jar:lib/kryonet-2.21-all.jar:lib/jruby.jar:lib/jython.jar:lib/jinput.jar:res/ '{}' '+'

jar: class | lib/.tmp $(distdir)
	cd lib/.tmp && find ../ -maxdepth 1 -name '*.jar' -exec jar xf '{}' ';'
	jar cfe $(distjar) com.mojang.mojam.MojamStartup -C src/ . -C res/ .
	jar ufe $(distjar) com.mojang.mojam.MojamStartup -C lib/.tmp/ .

natives: $(distdir)
	jar cf $(distdir)/linux_native.jar -C lib/native/linux/ .
	jar cf $(distdir)/macosx_native.jar -C lib/native/macosx/ .
	jar cf $(distdir)/solaris_native.jar -C lib/native/solaris/ .
	jar cf $(distdir)/windows_native.jar -C lib/native/windows/ .

lib/.tmp:
	mkdir -p lib/.tmp

$(distdir):
	mkdir -p $(distdir)

clean: clean-class clean-jar

clean-jar:
	rm -rf $(distjar) lib/.tmp

clean-class:
	find src/ -name '*.class' -exec rm '{}' '+'

.PHONY: all class jar natives clean clean-jar clean-class
