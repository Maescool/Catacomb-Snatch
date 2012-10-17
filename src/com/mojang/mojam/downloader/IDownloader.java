package com.mojang.mojam.downloader;

import java.io.IOException;

public interface IDownloader {

    public void downloadTo(String url, String dest) throws IOException;   
}