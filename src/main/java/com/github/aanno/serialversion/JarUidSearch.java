package com.github.aanno.serialversion;

import java.io.File;
import java.io.IOException;
import java.io.ObjectStreamClass;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;


/**
 * Searches all the jars in a given directory for a class with the given UID.
 * Additional directories can be specified to support with loading the jars.
 * For example, you might want to load the lib dir from your app server to get
 * the Servlet API, etc.
 *
 * https://stackoverflow.com/questions/191766/find-which-class-in-which-jar-has-a-given-serialversionuid
 */
public class JarUidSearch {
    public static void main(String args[])
            throws IOException, ClassNotFoundException {
        if (args.length < 2) {
            System.err.println("Usage: <UID to search for> <directory with jars to search> [additional directories with jars]");
            System.exit(-1);
        }
        long targetUID = Long.parseLong(args[0]);
        ArrayList<URL> urls = new ArrayList<URL>();
        File libDir = new File(args[1]);

        for (int i = 1; i < args.length; i++) {
            gatherJars(urls, new File(args[i]));
        }

        File[] files = libDir.listFiles();
        for (File file : files) {
            try {
                checkJar(targetUID, urls, file);
            } catch (Throwable t) {
                System.err.println("checkJar for " + file + " threw: " + t);
                t.printStackTrace();
            }
        }
    }

    /**
     * @param urls
     * @param libDir
     * @throws MalformedURLException
     */
    public static void gatherJars(ArrayList<URL> urls, File libDir)
            throws MalformedURLException {
        File[] files = libDir.listFiles();

        for (File file : files) {
            urls.add(file.toURL());
        }
    }

    /**
     * @param urls
     * @param file
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public static void checkJar(long targetUID, ArrayList<URL> urls, File file)
            throws IOException, ClassNotFoundException {
        System.out.println("Checking: " + file);
        JarFile jarFile = new JarFile(file);
        Enumeration allEntries = jarFile.entries();
        while (allEntries.hasMoreElements()) {
            JarEntry entry = (JarEntry) allEntries.nextElement();
            String name = entry.getName();

            if (!name.endsWith(".class")) {
                // System.out.println("Skipping: " + name);
                continue;
            }

            try {
                URLClassLoader loader = URLClassLoader.newInstance((URL[]) urls.toArray(new URL[0]));
                String className = name.substring(0,
                        name.length() - ".class".length()).replaceAll("/", ".");
                Class<?> clazz = loader.loadClass(className);
                ObjectStreamClass lookup = ObjectStreamClass.lookup(clazz);

                if (lookup != null) {
                    long uid = lookup.getSerialVersionUID();

                    if (targetUID == uid) {
                        System.out.println(file + " has class: " + clazz);
                    }
                }
            } catch (Throwable e) {
                System.err.println("entry " + name + " caused Exception: " + e);
            }
        }
    }
}
