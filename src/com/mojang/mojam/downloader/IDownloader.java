package com.mojang.mojam.downloader;

import java.io.IOException;
import java.io.InputStream;

public interface IDownloader {

    public void downloadTo(String url, String dest) throws IOException;
    
}
