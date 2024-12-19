package com.github.aanno.serialversion;

import com.google.common.collect.ImmutableSet;
import com.google.common.reflect.ClassPath;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class Serialversion {

    private static final String JAR_PATH1 = "build/test-jars-1/junit-jupiter-engine-5.10.0.jar";
    private static final String JAR_PATH2 = "build/test-jars-1/junit-jupiter-engine-5.10.0.jar";

    private static final class MyClassLoader extends URLClassLoader {
        MyClassLoader() {
            super(new URL[0], Thread.currentThread().getContextClassLoader());
        }

        @Override
        protected void addURL(URL url) {
            super.addURL(url);
        }

        protected void addJar(File jar) throws IOException {
            super.addURL(new URL("jar:file:" + jar.getCanonicalFile().getPath() + "!/"));
        }

        protected void addDirOfJars(File dir) throws IOException {
            dir = dir.getCanonicalFile();
            for (File file : dir.listFiles()) {
                if (file.isDirectory()) {
                    addDirOfJars(file);
                } else {
                    addJar(file);
                }
            }
        }
    }

    private final MyClassLoader classLoader = new MyClassLoader();

    // cache
    private Class<?> serializable = null;
    private Method lookup = null;

    public Serialversion() {
    }

    public Set<String> getClassNamesFromJarFile(File jar) throws IOException {
        Set<String> classNames = new HashSet<>();
        try (JarFile jarFile = new JarFile(jar)) {
            Enumeration<JarEntry> e = jarFile.entries();
            while (e.hasMoreElements()) {
                JarEntry jarEntry = e.nextElement();
                if (jarEntry.getName().endsWith(".class")) {
                    String className = jarEntry.getName()
                            .replace("/", ".")
                            .replace(".class", "");
                    classNames.add(className);
                }
            }
            return classNames;
        }
    }

    // https://stackoverflow.com/questions/15720822/how-to-get-names-of-classes-inside-a-jar-file
    public List<String> getClassNamesFromJarFile2(File jar) throws IOException {
        List<String> classNames = new ArrayList<String>();
        ZipInputStream zip = new ZipInputStream(new FileInputStream(jar));
        for (ZipEntry entry = zip.getNextEntry(); entry != null; entry = zip.getNextEntry()) {
            if (!entry.isDirectory() && entry.getName().endsWith(".class")) {
                // This ZipEntry represents a class. Now, what class does it represent?
                String className = entry.getName().replace('/', '.'); // including ".class"
                classNames.add(className.substring(0, className.length() - ".class".length()));
            }
        }
        return classNames;
    }

    public Set<Class> getClassesFromJarFile(File jarFile) throws IOException, ClassNotFoundException {
        classLoader.addJar(jarFile);
        Set<String> classNames = getClassNamesFromJarFile(jarFile);
        Set<Class> classes = new HashSet<>(classNames.size());
        for (String name : classNames) {
            if ("module-info".equals(name)) continue;
            if ("package-info".equals(name)) continue;
            Class clazz = classLoader.loadClass(name); // Load the class by its name
            classes.add(clazz);
        }
        return classes;
    }

    public Map<Class,Long> getClassesWithSvuFromJarFile(File jarFile) throws IOException, ClassNotFoundException, NoSuchFieldException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        // also add all JARs in same dir
        classLoader.addDirOfJars(jarFile.getParentFile());
        Map<Class,Long> result = new HashMap<>();
        for (Class clazz : getClassesFromJarFile(jarFile)) {
            result.put(clazz, getSerialVersionUID3(clazz));
        }
        return result;
    }

    public ImmutableSet<ClassPath.ClassInfo> getGuavaClassInfo(String packageName) throws IOException {
        ClassPath cp = ClassPath.from(Thread.currentThread().getContextClassLoader());
        return cp.getTopLevelClassesRecursive(packageName);
    }

    public long getSerialVersionUID(Class<?> clazz) throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException {
        if (!isSerializable(clazz)) return 0L;
        // https://stackoverflow.com/questions/22169268/is-it-possible-to-get-serialversionuid-of-the-java-class-in-runtime
        try {
            Field f = clazz.getDeclaredField("serialVersionUID");
            f.setAccessible(true);
            return (long) f.getLong(null);
        } catch (NoSuchFieldException e) {
            return -1L;
        }
    }

    public long getSerialVersionUID2(Class<?> clazz) throws ClassNotFoundException {
        if (!isSerializable(clazz)) return 0L;
        // https://stackoverflow.com/questions/1321988/finding-serialversionuid-of-serialized-object
        return ObjectStreamClass.lookup(clazz).getSerialVersionUID();
    }

    public long getSerialVersionUID3(Class<?> clazz) throws ClassNotFoundException, InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        if (!isSerializable(clazz)) return 0L;
        // https://stackoverflow.com/questions/1321988/finding-serialversionuid-of-serialized-object
        // return ObjectStreamClass.lookup(clazz).getSerialVersionUID();
        if (lookup == null) {
            Class<?> osc = classLoader.loadClass("java.io.ObjectStreamClass");
            lookup = osc.getMethod("lookup", Class.class);
        }
        ObjectStreamClass streamClass = (ObjectStreamClass) lookup.invoke(null, clazz);
        if (streamClass == null) return -1;
        return streamClass.getSerialVersionUID();
    }

    public boolean isSerializable(Class<?> clazz) throws ClassNotFoundException {
        if (serializable == null) {
            serializable = classLoader.loadClass("java.io.Serializable");
        }
        return serializable.isAssignableFrom(clazz);
    }

    public static void main(String[] args) throws IOException, ClassNotFoundException, NoSuchFieldException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        Serialversion instance = new Serialversion();
        Map<Class, Long> map = instance.getClassesWithSvuFromJarFile(new File(JAR_PATH2));
        for (Class<?> key : map.keySet()) {
            System.out.println(key.getName() + " -> " + Long.toHexString(map.get(key)));
        }
    }

}