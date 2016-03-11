package com.mojang.mojam.downloader;

import com.mojang.mojam.MojamComponent;
import com.mojang.mojam.MojamStartup;
import com.mojang.mojam.Options;
import com.mojang.mojam.gui.DownloadScreen;
import com.mojang.mojam.mc.EnumOSMappingHelper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class Downloader {

	private static IDownloader downloadAgent = null;

	private static String baseURL = "http://assets.catacombsnatch.net/";

	private static String[][] binFiles = {
			{"jython.jar", "f63bfc8b51789b4c3d8554aa9642c7bff56b84c25215f3595457fa98ef34093b"},
			{"jruby.jar", "b1cce9788e6c242f55423b5d88e69186e37a53e00cc9fc4cbcbe4999ab91b9a3"}
	};

	private static String nativeVersion = "lwjgl-2.9.3";
	private static String[][] nativeFiles = {
			{ "linux_native.jar", "a82b1906072f90018f05bd0851254729248a624b0127ef6e0ff487c748785236" },
			{ "macosx_native.jar", "1d02f1062cf2f0dc7a41e604bc436d487e7b98371a666681f1e72f773eb74a81" },
			{ "solaris_native.jar", "63c710911f3b5081f2ec698ed76229359787fe1b9172f516e8cd4c9ee4b8b50b" },
			{ "windows_native.jar", "47c9fb959ec2617fdfd4bb581ed8defed7964d9642dd62d4e6d4ca91cd7c163c" }
	};

	private static String[][][] nativeLocalFiles = {
			// linux
			{
					{"libjinput-linux64.so", "86e650f47790e789696a7a5809461eb4b503f5f841e17488aa7ee5a1bedc05a6"},
					{"libjinput-linux.so", "ff7af7a1306451428c98e3f50c5bf2f19bb6cbc5835730917cdd755b8cc626d0"},
					{"liblwjgl64.so", "4622966cceee1c13df9e293fbae8fa402d1be84b3e1ade7256eca57892392f08"},
					{"liblwjgl.so", "7739ce295bfb88bceb462b3a130ec4c1668689ec3acea4b006422b43e2386b5a"},
					{"libopenal64.so", "265310b84e3fbc292354ad9901425a4f6532e8c3f730f36be96edb790174091c"},
					{"libopenal.so", "1b8d26bdf799a14005e4c3e20b67c4449c48d4bf6bb51e871522669b351d20ca"}
			},
			// solaris
			{
					{"liblwjgl64.so", "fce46ded1e187f4041131d69368c6b91d6dc06effefbe36b2cec7157a25949ef"},
					{"liblwjgl.so", "9e9e0152a346ff9afd6e7105f039459e2d215a8a939dbbc45500a93164e2c778"},
					{"libopenal64.so", "857367e54db3ca84af99a303479375db4480e56a8088a2ec3b9e6fb96ec3421b"},
					{"libopenal.so", "64462fec9140e1475fe74ba9383f09965463b45441951d94901d561454696801"}
			},
			// windows
			{
					{"jinput-dx8_64.dll", "511dc50c2001d3e25845dd479ca82fdfc9d42403f9aa69c6493257c66ddf0266"},
					{"jinput-dx8.dll", "f6ee33701bfbba481870f4a370d707b87001fb3213efcc60bff325013b4e219c"},
					{"jinput-raw_64.dll", "74cd74d55ea20e8fcea7aed8b97c2cf096da1fcde3faf183f815a4dce9364ec3"},
					{"jinput-raw.dll", "0fcd33e00ba5c51f3fdf3613d89c6e9e00381fef03b550412ea73bc837237dcf"},
					{"lwjgl64.dll", "094c38a9b6b9ab76e3730838d542419811e63de5d4a4ba5a22d83f6edd803943"},
					{"lwjgl.dll", "caf7074511c9ed3af7704223ec491b2992dc23b8c4769dffd64b9fa6c34451bd"},
					{"OpenAL32.dll", "baf27fc91dc852d78889e052cfc9ed2b6fc0927258bb507a895c6fcd50f10fef"},
					{"OpenAL64.dll", "9261b66010a845ddef9f61d5e4266fe2f08a53f3605da002e9e8f8d202bdbc5e"}
			},
			// macosx
			{
					{"libjinput-osx.dylib", "d155c29cfa7d7b49cab0821d5ba00a8fdc8b386c8bf5669f0313a62e44ba70d6"},
					{"liblwjgl.dylib", "4e9512afc0aa0831cc72591852c47c804c57ed13bd4ab9ba3ac798650616db0e"},
					{"openal.dylib", "d845fab22fd58425deafbcb1a552633b4a62bd5dfa27b9263c050d91a3fad8c0"}
			}
	};

	private static String[][] soundFiles = {
			{ "Background 1.ogg", "9fcfef68c88ce6f7b180bc14ce0e920588d596b791dafd40b53336cd86bbe325" },
			{ "Background 2.ogg", "b303abffca9c89ee22c595a86631f5eb9957d03774820510bbc90e700dfc2ada" },
			{ "Background 3.ogg", "28d72ab1b778c9e9d58b874c1554d5f7b8a9ea7c96cf26b699327b284b6d4ab1" },
			{ "Background 4.ogg", "489657f5c2ec87271e8d18ec145710955635b457f6db7dffecfd02febfb76bda" },
			{ "ThemeEnd.ogg", "480f07e97b53367954f59b5f7fdf9e79ff47f5517fd5445a78696fe8f20ff167" },
			{ "ThemeTitle.ogg", "cc445bec1e1e079eb69b483ec22f1d7a23c7d8330fb2de3259aa63618b0a2ff0" }
	};

	public void CheckFiles() {
		// testSpeeds(); //<- Eye-opening
		if (Options.getAsInteger(Options.DLSYSTEM, 0) == 1) {
			downloadAgent = new ChannelDownloader(); // Faster, less control
		} else if (Options.getAsInteger(Options.DLSYSTEM, 0) == 0) {
			downloadAgent = new DefaultDownloader();
		}
		checkBinDir();
		checkNativeDir();
		checkSoundDir();
		MojamStartup.instance.startgame();
	}

	public static void testSpeeds() {
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
				String binFile = binFiles[i][0].toString();
				String sha256 = binFiles[i][1].toString();
				boolean checksum = false;
				for (int j = 0; j < found.size(); j++) {
					// check if file is found
					if (binFile.equals(found.get(j).toString())) {
						System.out.println("Found: " + binFile);
						try {
							checksum = verifySHA256Checksum(new File(binDir, binFile).getAbsolutePath(), sha256);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							// could not check
							checksum = true;
							break;
						}
						if (checksum) {
							System.out.println("Checksum ok.");
							break;
						}
					}
				}
				if (!checksum) {
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
				boolean notok = false;
				for (int i = 0; i < nativeLocalFiles[osId - 1].length; i++) {
					String nativeFile = nativeLocalFiles[osId - 1][i][0].toString();
					String sha256 = nativeLocalFiles[osId - 1][i][1].toString();
					boolean checksum = false;
					boolean notfound = true;
					for (int j = 0; j < found.size(); j++) {
						// check if file is found
						if (nativeFile.equals(found.get(j).toString())) {
							notfound = false;
							System.out.println("Found: " + nativeFile);
							try {
								checksum = verifySHA256Checksum(new File(nativeDir, nativeFile).getAbsolutePath(), sha256);
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
								// could not check
								checksum = true;
								break;
							}
							if (checksum) {
								System.out.println("Checksum ok.");
								break;
							}
						}
					}
					if (notfound || !checksum) {
						notok = true;
						break;
					}
				}
				if (notok) {
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
			return;
		}
		String sha256 = "";
		for (int i = 0; i < nativeFiles.length; i++) {
			if (jarFile.equals(nativeFiles[i][0])) {
				sha256 = nativeFiles[i][1];
				break;
			}
		}
		if (!jarFile.isEmpty()) {
			boolean checksum = false;
			// check if we have file locally and check md5
			if (new File(binDir, jarFile).exists()) {
				try {
					checksum = verifySHA256Checksum(new File(binDir, jarFile).getAbsolutePath(), sha256);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if (!checksum) {
					if (!download(baseURL + "native/" + nativeVersion + "/" + jarFile, binDir + jarFile)) {
						return;
					}
				}
				unpackJar(binDir + jarFile, nativeLibDir);
			} else {
				// jar not found.. download and unpack!
				if (download(baseURL + "native/" + nativeVersion + "/" + jarFile, binDir + jarFile)) {
					try {
						checksum = verifySHA256Checksum(new File(binDir, jarFile).getAbsolutePath(), sha256);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						checksum = true;
					}
					if (checksum) {
						unpackJar(binDir + jarFile, nativeLibDir);
					} else {
						// oO something went fishy..
						System.out.println("oO, something is broken.. checksum was wrong..");
					}
				} else {
					System.out.println("oO, so.. yeah.. download failed..");
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
				String soundFile = soundFiles[i][0].toString();
				String sha256 = soundFiles[i][1].toString();
				boolean checksum = false;
				// check if file is found
				for (int j = 0; j < found.size(); j++) {
					if (soundFile.equals(found.get(j).toString())) {
						System.out.println("Found: " + soundFile);
						try {
							checksum = verifySHA256Checksum(new File(soundDir, soundFile).getAbsolutePath(), sha256);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						if (checksum) {
							System.out.println("Checksum ok.");
							break;
						}
					}
				}
				if (!checksum) {
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

	// From http://www.sha1-online.com/sha256-java/
	/**
	 * Verifies file's SHA256 checksum
	 * @param file FilePath and name of a file that is to be verified
	 * @param testChecksum the expected checksum
	 * @return true if the expeceted SHA256 checksum matches the file's SHA256 checksum; false otherwise.
	 * @throws NoSuchAlgorithmException
	 * @throws IOException
	 */
	public static boolean verifySHA256Checksum(String file, String testChecksum) throws NoSuchAlgorithmException, IOException
	{
		MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
		FileInputStream fis = new FileInputStream(file);

		byte[] data = new byte[1024];
		int read = 0;
		while ((read = fis.read(data)) != -1) {
			sha256.update(data, 0, read);
		};
		byte[] hashBytes = sha256.digest();

		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < hashBytes.length; i++) {
			sb.append(Integer.toString((hashBytes[i] & 0xff) + 0x100, 16).substring(1));
		}

		String fileHash = sb.toString();

		return fileHash.equals(testChecksum);
	}

	// unpack jar
	public static boolean unpackJar(String jarFile, String destDir) {
		DownloadScreen.unpackStart(new File(jarFile).getName().toString());
		
		File jarFileJar = new File(jarFile);
		File destDirDir = new File(destDir);

		if (!destDirDir.isDirectory()) {
			destDirDir.mkdirs();
		}

		FileInputStream input;
		try	{
			input = new FileInputStream(jarFileJar);
		} catch (FileNotFoundException e) { return false; }

		ZipInputStream zipIn = new ZipInputStream(input); 
		try {
			ZipEntry currentEntry = zipIn.getNextEntry();
			while (currentEntry != null) {
				if (currentEntry.getName().contains("META-INF")) {
					currentEntry = zipIn.getNextEntry();
					continue;
				}

				FileOutputStream outStream = new FileOutputStream(new File(destDirDir, currentEntry.getName()));

				int readLen;
				byte[] buffer = new byte[1024];
				while ((readLen = zipIn.read(buffer, 0, buffer.length)) > 0) {
					outStream.write(buffer, 0, readLen);
				}
				outStream.close();

				currentEntry = zipIn.getNextEntry();
			}
		} catch (IOException e) {
			return false;
		} finally {
			try {
				DownloadScreen.unpackStop();
				zipIn.close();
				input.close();
			} catch (IOException e) { }
		}

		jarFileJar.delete();
		DownloadScreen.unpackStop();
		return true;
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
			break;
		default:
			osName = "Unknown";
		}
		return "Mozilla/5.0 (Catacomb Snatch; "+osName+") CatacombSnatch/"+MojamComponent.GAME_VERSION;
	}
}
