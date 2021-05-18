package com.chinaunicom.torn.mcloud.utils;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.springframework.util.ClassUtils;

public class ClassScanner {

    public static Set<Class<?>> find(Class<?> clazz) throws IOException, ClassNotFoundException {
        Package pkg = clazz.getPackage();
        Enumeration<URL> urls = ClassUtils.getDefaultClassLoader().getResources(pkg.getName().replace('.', '/'));

        Set<Class<?>> result = new HashSet<>();

        while (urls.hasMoreElements()) {
            URL url = urls.nextElement();

            if (url != null) {
                String protocol = url.getProtocol();

                if (protocol.equals("file")) {
                    result.addAll(findFileClass(url, pkg.getName()));
                }
                else if (protocol.equals("jar")) {
                    result.addAll(findJarClass(url, pkg.getName()));
                }
            }
        }

        return result;
    }

    private static Set<Class<?>> findFileClass(URL pkgPath, String pkgName) throws ClassNotFoundException {
        Set<Class<?>> result = new HashSet<>();

        String path = pkgPath.getPath().replaceAll("%20", " ");
        File[] files = new File(path).listFiles(new FileFilter(){

            @Override
            public boolean accept(File pathname) {
                return (pathname.isFile() && pathname.getName().endsWith(".class"));
            }
        });

        for (File file : files) {
            if (file.isFile()) {
                String className = file.getName().substring(0, file.getName().lastIndexOf('.'));
                result.add(Class.forName(pkgName + "." + className));
            }
        }

        return result;
    }

    private static Set<Class<?>> findJarClass(URL pkgPath, String pkgName) throws IOException, ClassNotFoundException {
        Set<Class<?>> result = new HashSet<>();

        JarURLConnection conn = (JarURLConnection) pkgPath.openConnection();
        if (conn == null) {
            return result;
        }

        JarFile jar = conn.getJarFile();
        if (jar == null) {
            return result;
        }

        Enumeration<JarEntry> entries = jar.entries();
        while (entries.hasMoreElements()) {
            JarEntry entry = entries.nextElement();

            if (entry.getName().endsWith(".class")) {
                result.add(Class.forName(entry.getName().substring(0, entry.getName().lastIndexOf('.')).replace('/', '.')));
            }
        }

        return result;
    }
}
