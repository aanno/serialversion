package com.github.aanno.serialversion;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class JarDiff {

    private static final String JAR_PATH1 = "build/test-jars-1/commons-lang3-3.17.0.jar";
    private static final String JAR_PATH2 = "build/test-jars-2/commons-lang3-3.3.2.jar";

    private DiffResultConfig diffResultConfig;

    private File dirA;
    private File dirB;

    private Serialversion svA;
    private Serialversion svB;

    public JarDiff(DiffResultConfig config, File dirA, File dirB) throws IOException {
        this.diffResultConfig = config;

        this.dirA = dirA;
        this.dirB = dirB;

        this.svA = new Serialversion(dirA);
        this.svB = new Serialversion(dirB);
    }

    public DiffResult diff(String jarInA, String jarInB) throws IOException, NoSuchFieldException, ClassNotFoundException, InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        Map<String, Long> mapA = svA.getClassesWithSvuFromJarFile(new File(dirA, jarInA), diffResultConfig.isSerializableOnly());
        Map<String, Long> mapB = svB.getClassesWithSvuFromJarFile(new File(dirB, jarInB), diffResultConfig.isSerializableOnly());

        Set<String> onlyInA = new HashSet<>();
        Set<String> onlyInB = new HashSet<>(mapB.keySet());
        Set<String> sameInAandB = new HashSet<>();
        Map<String, DiffSvu> svuDiff = new HashMap<>();

        for (String key: mapA.keySet()) {
            if (onlyInB.remove(key)) {
                // was in B (and A)
                Long svuA = mapA.get(key);
                Long svuB = mapB.get(key);
                if (svuA.longValue() == svuB.longValue()) {
                    sameInAandB.add(key);
                } else {
                    svuDiff.put(key, new DiffSvu(svuA, svuB));
                }
            } else {
                // was not in B (but A)
                onlyInA.add(key);
            }
        }
        return new DiffResult(onlyInA, onlyInB, sameInAandB, svuDiff);
    }

    public static void main(String[] args) throws IOException, NoSuchFieldException, ClassNotFoundException, InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        // File jarA = new File(JAR_PATH1);
        // File jarB = new File(JAR_PATH2);
        File jarA = new File(args[0]);
        File jarB = new File(args[1]);

        DiffResultConfig config = new DiffResultConfig();
        JarDiff instance = new JarDiff(config, jarA.getCanonicalFile().getParentFile(), jarB.getCanonicalFile().getParentFile());
        DiffResult result = instance.diff(jarA.getName(), jarB.getName());
        DiffResultFormatter formatter = new DiffResultFormatter(config);
        System.out.println(formatter.format(result));
        // System.out.println(result);
    }
}
