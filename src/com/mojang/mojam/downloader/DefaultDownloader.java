package com.mojang.mojam.downloader;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Date;
import java.util.zip.GZIPInputStream;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;

import com.mojang.mojam.gui.DownloadScreen;

public class DefaultDownloader implements IDownloader {

	@Override
	public void downloadTo(String url, String dest) throws IOException {
		url = url.replace(" ", "%20");
		URL urll = new URL(url);
		System.out.println("Opening connection to " + url + "...");
		URLConnection urlC = urll.openConnection();

		// allow both GZip and Deflate (ZLib) encodings

		urlC.setRequestProperty("Accept-Encoding", "gzip, deflate");

		// set the user agent to pass Cloud-Flare

		urlC.setRequestProperty("User-agent", "Mozilla/4.0 (compatible; Catacomb-Snatch; UnKnown)");

		// Print info about resource

		Date date = new Date(urlC.getLastModified());
		int fileSize = urlC.getContentLength();
		System.out.print("Copying resource (type: " + urlC.getContentType());
		System.out.println(", modified on: " + date.toString() + ")...");
		System.out.flush();

		String encoding = urlC.getContentEncoding();

		InputStream is = null;

		if (encoding != null && encoding.equalsIgnoreCase("gzip")) {
			is = new GZIPInputStream(urlC.getInputStream());
		} else if (encoding != null && encoding.equalsIgnoreCase("deflate")) {
			is = new InflaterInputStream(urlC.getInputStream(), new Inflater(true));
		} else {
			is = urlC.getInputStream();
		}
		FileOutputStream fos = null;
		fos = new FileOutputStream(dest);
		int oneChar, count = 0;
		while ((oneChar = is.read()) != -1) {
			fos.write(oneChar);
			count++;
			DownloadScreen.drawGraph(count, fileSize);
		}
		is.close();
		fos.close();

		System.out.println(count + " byte(s) of " + fileSize + " copied");
		return;
	}

}
