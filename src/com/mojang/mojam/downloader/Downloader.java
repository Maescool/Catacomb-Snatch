package com.mojang.mojam.downloader;

import java.net.*;
import java.io.*;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.jar.*;

import com.mojang.mojam.MojamComponent;
import com.mojang.mojam.MojamStartup;
import com.mojang.mojam.Options;
import com.mojang.mojam.mc.EnumOS2;
import com.mojang.mojam.mc.EnumOSMappingHelper;

import com.mojang.mojam.gui.DownloadScreen;

public class Downloader {


	// private static DownloadScreen dScreen = null;

	private static IDownloader downloadAgent = null;

	private static String baseURL = "http://assets.catacombsnatch.net/";

	private static String[][] binFiles = { { "lwjgl.jar", "a9deb51bea77db85de26f7b2eb1ecbea" },
			{ "LibraryLWJGLOpenAL.jar", "9f6d36c4e4c6e5252c245bd3c61def01" },
			{"jython.jar", "ac8b8066bf44cdc304b816179f0a96e3"},
			{"jruby.jar", "23fcb8bdf2ac1d20b8cf247212e0310c"}
	};

	private static String[][] nativeFiles = { { "linux_native.jar", "6e05a97164478a1c0428179d78dc47ea" },
			{ "solaris_native.jar", "f8d81693de7d8738b6adb94e022cddde" },
			{ "windows_native.jar", "73a252d4a759230013f96e5ba6740650" },
			{ "macosx_native.jar", "0bdc70d053e81a71a84d6f11c29ec009" } };
	private static String[][][] nativeLocalFiles = {
			// linux
			{ { "libjinput-linux64.so", "23a6b611eaab617a9394f932b69ae034" },
					{ "libjinput-linux.so", "f2317f7c050cd441510423e90fb16dfd" },
					{ "liblwjgl64.so", "14fba12975855da3387c57100adad0d9" },
					{ "liblwjgl.so", "b6750ca7411ba90d131f81700d4c8e5c" },
					{ "libopenal64.so", "36dd2aefe04f5deb4b8184d9aedecc81" },
					{ "libopenal.so", "cc73e342aa11a584b727ceb5ff33aeff" } },
			// solaris
			{ { "liblwjgl64.so", "30a37613ae3b615e4908f1cd0dc64d15" },
					{ "liblwjgl.so", "458a2f41ce039366df5b9bfaafff2cbe" },
					{ "libopenal64.so", "960249b6592ce57f0665e2a9478f9016" },
					{ "libopenal.so", "8dc38fc3d4d02c08af9c2644587ce946" } },
			// windows
			{ { "jinput-dx8_64.dll", "f1a51706365a44ea21aa96a9a04bfb37" },
					{ "jinput-dx8.dll", "83fd6e1b034dc927773322472417589e" },
					{ "jinput-raw_64.dll", "4d1cfc36d1b5b1dd496d6e3090044cb1" },
					{ "jinput-raw.dll", "77f9d40a81f8f062148172bac873bcad" },
					{ "lwjgl64.dll", "13e15d61ab3d6b0c7ec602749a3bac14" },
					{ "lwjgl.dll", "c7a795f9b8aa774c12b329d51b6307fc" },
					{ "OpenAL32.dll", "258c3ad28efbda28363729ab6c9ca727" },
					{ "OpenAL64.dll", "29610cfe77635b636a191746b51b71a3" } },
			// macosx
			{ { "libjinput-osx.jnilib", "b0f62f4735ad754a7e6c8e2f744a0523" },
					{ "liblwjgl.jnilib", "c94e04323ebe89a60866ac3e164c0a81" },
					{ "openal.dylib", "938a20055127b2262d114b8bba5a9e07" } } };

	private static String[][] soundFiles = { { "Background 1.ogg", "dc981f1c6abbc9cbd789316c13404d39" },
			{ "Background 2.ogg", "70c878491e3f4de3e0ab99c27af0bc60" },
			{ "Background 3.ogg", "81dd986e281f42ea268d4de7751622d1" },
			{ "Background 4.ogg", "a3d7be4ce42b2df567513728830d990a" },
			{ "ThemeEnd.ogg", "32c3edef7c01e13bb0e55e71a71b4d99" },
			{ "ThemeTitle.ogg", "53c6d27b515e583a7922fd41c2a10b06" } };

	public void CheckFiles() {
		// testSpeeds(); //<- Eye-opening
		if (Options.getAsInteger(Options.DLSYSTEM, 0) == 1) {
			downloadAgent = new ChannelDownloader();// Faster, less control
		} else if (Options.getAsInteger(Options.DLSYSTEM, 0) == 0) {
			downloadAgent = new DefaultDownloader();
		}
		checkBinDir();
		checkNativeDir();
		checkSoundDir();
		MojamStartup.instance.startgame();
	}

	private static void testSpeeds() {
		IDownloader old = downloadAgent;
		downloadAgent = new DefaultDownloader();
		long startDefault = System.nanoTime();
		downloadNative();
		downloadAllSound(getSoundDir());
		long endDefault = System.nanoTime();
		downloadAgent = new ChannelDownloader();
		long startChannel = System.nanoTime();
		downloadNative();
		downloadAllSound(getSoundDir());
		long endChannel = System.nanoTime();
		System.out.println("Times: ");
		System.out.println(endDefault - startDefault);
		System.out.println(endChannel - startChannel);
		downloadAgent = old;

	}

	// bindir

	private static void checkBinDir() {
		File binDir = getBinDir();
		if (!binDir.exists()) {
			binDir.mkdirs();
			downloadAllBin(binDir);
			return;
		}

		ArrayList<String> found = new ArrayList<String>();

		File[] children = binDir.listFiles();
		if (children != null) {
			for (File child : children) {
				if (child.isDirectory()) {
					continue;
				}
				String fileName = child.getName();
				found.add(fileName);
			}
			// now get the missing files..
			for (int i = 0; i < binFiles.length; i++) {
				boolean notfoundorok = true;
				String binFile = binFiles[i][0].toString();
				String md5 = binFiles[i][1].toString();
				for (int j = 0; j < found.size(); j++) {
					// check if file is found
					if (binFile.equals(found.get(j).toString())) {
						System.out.println("Found: " + binFile);
						// md5 check
						String hash = "";
						try {
							hash = getMD5Checksum(new File(binDir, binFile).getAbsolutePath());
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						if (md5.equals("") || md5.equals(hash)) {
							System.out.println("MD5 OK: " + hash);
							notfoundorok = false;
							break;
						}
					}
				}
				if (notfoundorok) {
					downloadBinFile(binFile);
				}
			}
		} else {
			downloadAllBin(binDir);
		}
	}

	private static void downloadAllBin(File binDir) {
		for (int i = 0; i < binFiles.length; i++) {
			downloadBinFile(binFiles[i][0]);
		}
	}

	private static void downloadBinFile(String binFile) {
		String url = baseURL + "libs/" + binFile;
		File binDir = getBinDir();
		String toPath = binDir.getAbsolutePath().toString() + File.separator + binFile;

		System.out.println("Downloading binfile to: " + toPath + " from: "+ url);

		download(url, toPath);
	}

	public static File getBinDir() {
		return new File(MojamComponent.getMojamDir(), "bin");
	}

	// native dir

	private static void checkNativeDir() {
		File nativeDir = getNativeDir();
		if (!nativeDir.exists()) {
			nativeDir.mkdirs();
			downloadNative();
		}
		ArrayList<String> found = new ArrayList<String>();

		File[] children = nativeDir.listFiles();
		if (children != null) {
			for (File child : children) {
				if (child.isDirectory()) {
					continue;
				}
				String fileName = child.getName();
				found.add(fileName);
			}
			//
			int osId = EnumOSMappingHelper.enumOSMappingArray[MojamComponent.getOs().ordinal()];
			if (osId < 5 && osId > 0) {
				// now check the missing files..
				boolean notfoundorok = false;
				for (int i = 0; i < nativeLocalFiles[osId - 1].length; i++) {
					String nativeFile = nativeLocalFiles[osId - 1][i][0].toString();
					String md5 = nativeLocalFiles[osId - 1][i][1].toString();
					boolean notfound = true;
					for (int j = 0; j < found.size(); j++) {
						// check if file is found
						if (nativeFile.equals(found.get(j).toString())) {
							notfound = false;
							System.out.println("Found: " + nativeFile);
							// md5 check
							String hash = "";
							try {
								hash = getMD5Checksum(new File(nativeDir, nativeFile).getAbsolutePath());
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							if (!md5.equals(hash)) {
								notfoundorok = true;
								break;
							} else {
								System.out.println("MD5 OK: " + hash);
							}
						}
					}
					if (notfound || notfoundorok) {
						notfoundorok = true;
						break;
					}
				}
				if (notfoundorok) {
					downloadNative();
				}
			}
		} else {
			downloadNative();
		}
	}

	private static void downloadNative() {
		final String nativeLibDir = MojamComponent.getMojamDir().getAbsolutePath().toString() + File.separator + "bin"
				+ File.separator + "native" + File.separator;
		final String binDir = getBinDir().getAbsolutePath().toString() + File.separator;
		String jarFile = "";
		switch (EnumOSMappingHelper.enumOSMappingArray[MojamComponent.getOs().ordinal()]) {
		case 1: // '\001'
			// linux
			jarFile = "linux_native.jar";
			break;
		case 2: // '\002'
			// solaris
			jarFile = "solaris_native.jar";
			break;

		case 3: // '\003'
			// windows
			jarFile = "windows_native.jar";
			break;

		case 4: // '\004'
			// macosX
			jarFile = "macosx_native.jar";
			break;
		default:
			// oO OS not detected..
			break;
		}
		String hash = "";
		String md5 = "";
		for (int i = 0; i < nativeFiles.length; i++) {
			if (jarFile.equals(nativeFiles[i][0])) {
				md5 = nativeFiles[i][1];
				break;
			}
		}
		if (!jarFile.isEmpty()) {
			// check if we have file locally and check md5
			if (new File(binDir, jarFile).exists()) {

				try {
					hash = getMD5Checksum(new File(binDir, jarFile).getAbsolutePath());
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if (!md5.equals(hash)) {
					if (!download(baseURL + "native/" + jarFile, binDir + jarFile)) {
						return;
					}
				}
				unpackJar(binDir + jarFile, nativeLibDir);
			} else {
				// jar not found.. download and unpack!
				if (download(baseURL + "native/" + jarFile, binDir + jarFile)) {
					try {
						hash = getMD5Checksum(new File(binDir, jarFile).getAbsolutePath());
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					if (md5.equals(hash)) {
						unpackJar(binDir + jarFile, nativeLibDir);
					} else {
						// oO something went fishy..
					}
				}
			}
		}
	}

	public static File getNativeDir() {
		return new File(getBinDir(), "native");
	}

	// sound dir

	/**
	 * Check the sound directory if all sounds are locally available
	 * 
	 */
	private void checkSoundDir() {
		File soundDir = getSoundDir();
		if (!soundDir.exists()) {
			soundDir.mkdirs();
			downloadAllSound(soundDir);
		}

		ArrayList<String> found = new ArrayList<String>();

		File[] children = soundDir.listFiles();
		if (children != null) {
			for (File child : children) {
				if (child.isDirectory()) {
					continue;
				}
				String fileName = child.getName();
				found.add(fileName);
			}
			// now get the missing files..
			for (int i = 0; i < soundFiles.length; i++) {
				boolean notfoundorok = true;
				String soundFile = soundFiles[i][0].toString();
				String md5 = soundFiles[i][1].toString();
				// check if file is found
				for (int j = 0; j < found.size(); j++) {
					if (soundFile.equals(found.get(j).toString())) {
						System.out.println("Found: " + soundFile);
						// md5 check
						String hash = "";
						try {
							hash = getMD5Checksum(new File(soundDir, soundFile).getAbsolutePath());
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						if (md5.equals("") || md5.equals(hash)) {
							System.out.println("MD5 OK: " + hash);
							notfoundorok = false;
							break;
						}
					}
				}
				if (notfoundorok) {
					downloadSoundFile(soundFile);
				}
			}
		} else {
			downloadAllSound(soundDir);
		}
	}

	/**
	 * Download all the sounds.
	 * 
	 * @param soundDir
	 *            Sound directory.
	 */
	private static void downloadAllSound(File soundDir) {
		for (int i = 0; i < soundFiles.length; i++) {
			downloadSoundFile(soundFiles[i][0]);
		}
	}

	/**
	 * Download certain sound file that is missing.
	 * 
	 * @param soundFile
	 *            Name of the sound file
	 */
	private static void downloadSoundFile(String soundFile) {
		String url = baseURL + "sound/" + soundFile;
		File soundDir = getSoundDir();
		String toPath = soundDir.getAbsolutePath().toString() + File.separator + soundFile;

		System.out.println("Downloading soundFile to: " + toPath);

		download(url, toPath);
	}

	public static File getSoundDir() {
		return new File(new File(MojamComponent.getMojamDir(), "resources"), "sound");
	}

	// MD5 from http://www.rgagnon.com/javadetails/java-0416.html
	public static byte[] createChecksum(String filename) throws Exception {
		InputStream fis = new FileInputStream(filename);

		byte[] buffer = new byte[1024];
		MessageDigest complete = MessageDigest.getInstance("MD5");
		int numRead;

		do {
			numRead = fis.read(buffer);
			if (numRead > 0) {
				complete.update(buffer, 0, numRead);
			}
		} while (numRead != -1);

		fis.close();
		return complete.digest();
	}

	public static String getMD5Checksum(String filename) throws Exception {
		byte[] b = createChecksum(filename);
		String result = "";

		for (int i = 0; i < b.length; i++) {
			result += Integer.toString((b[i] & 0xff) + 0x100, 16).substring(1);
		}
		return result;
	}

	// unpack jar

	public static void unpackJar(String jarFile, String destDir) {
		DownloadScreen.unpackStart(new File(jarFile).getName().toString());
		try {
			JarFile jar = new JarFile(jarFile);
			Enumeration<JarEntry> enumi = jar.entries();

			while (enumi.hasMoreElements()) {
				JarEntry file = (JarEntry) enumi.nextElement();
				File f = new File(destDir + File.separator + file.getName());
				if (file.isDirectory()) { // if its a directory, create it
					f.mkdir();
					continue;
				}
				InputStream is = jar.getInputStream(file); // get the
				// input
				// stream
				FileOutputStream fos = new FileOutputStream(f);
				byte[] buffer = new byte[1 << Options.getAsInteger(Options.DLBUFFERSIZE, 13)];
				System.out.println(buffer.length);
				int read;

				while ((read = is.read(buffer)) != -1) {
					fos.write(buffer, 0, read);
				}
				fos.close();
				is.close();
			}
		} catch (Exception e) {
			DownloadScreen.unpackStop();
			e.printStackTrace();
		}
		DownloadScreen.unpackStop();
	}

	// download

	/**
	 * Download from URL to certain file
	 * 
	 * @param url
	 * @param toFile
	 *            (path+)filename to write to.
	 */
	public static boolean download(String url, String toFile) {
		try {
			DownloadScreen.downLoadStarted(new File(toFile).getName().toString());
			downloadAgent.downloadTo(url, toFile);
			DownloadScreen.downloadEnd();
			return true;
		} catch (MalformedURLException e) {
			System.err.println(e.toString());
			return false;
		} catch (UnknownHostException e) {
			System.err.println(e.toString());
			return false;
		} catch (IOException e) {
			System.out.println(e.getMessage());
			System.err.println(e.toString());
			return false;
		}
	}
	
	public static String getUserAgent(){
		String osName = "Unknown";
		switch (EnumOSMappingHelper.enumOSMappingArray[MojamComponent.getOs().ordinal()]){
		case 1:
			osName = "Linux";
			break;
		case 2:
			osName = "Solaris";
			break;
		case 3:
			osName = "Windows";
			break;
		case 4:
			osName = "MacOSX";
			default:
				osName = "Unknown";
		}
		return "Mozilla/5.0 (Catacomb Snatch; "+osName+") CatacombSnatch/"+MojamComponent.GAME_VERSION;
	}
}
