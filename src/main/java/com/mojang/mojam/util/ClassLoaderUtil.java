package com.mojang.mojam.util;


import java.io.File;
import java.io.FilenameFilter;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.logging.Logger;

public class ClassLoaderUtil {
    private static final Logger logger = Logger.getLogger(ClassLoaderUtil.class.getName());

    public static void injectJar(File file) {
        Object classLoader = ClassLoaderUtil.class.getClassLoader();
        try {
            logger.fine("Injecting " + file.getName() + "...");
            Method method = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
            method.setAccessible(true);
            method.invoke(classLoader, file.toURI().toURL());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void injectJars(File dir) {
        File[] jars = dir.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.toLowerCase().endsWith(".jar");
            }
        });
        for (File jar : jars) {
            injectJar(jar);
        }
    }
}