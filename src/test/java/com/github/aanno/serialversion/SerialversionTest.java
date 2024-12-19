package com.github.aanno.serialversion;

import com.google.common.collect.Sets;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SerialversionTest implements Serializable {

    private static final String JAR_PATH = "build/test-jars-1/jakarta.annotation-api-3.0.0.jar";
    private static final Set<String> EXPECTED_CLASS_NAMES = Sets.newHashSet(
            "jakarta.annotation.Generated",
            "jakarta.annotation.Nonnull",
            "jakarta.annotation.Nullable",
            "jakarta.annotation.PostConstruct",
            "jakarta.annotation.PreDestroy",
            "jakarta.annotation.Resource",
            "jakarta.annotation.Resources"
    );

    private Serialversion dut = new Serialversion();

    @Test
    public void givenJarFilePath_whenLoadClassNames_thenGetClassNames() throws IOException, URISyntaxException {
        /*
        File jarFile = new File(
                Objects.requireNonNull(
                        getClass().getClassLoader().getResource(JAR_PATH)).toURI());
        */
        Set<String> classNames = dut.getClassNamesFromJarFile(new File(JAR_PATH));

        assertTrue(classNames.containsAll(EXPECTED_CLASS_NAMES));
    }

    @Test
    public void givenJarFilePath_whenLoadClass_thenGetClassObjects()
            throws IOException, ClassNotFoundException, URISyntaxException {
        /*
        File jarFile
                = new File(
                        Objects.requireNonNull(
                                getClass().getClassLoader().getResource(JAR_PATH)).toURI());
         */
        Set<Class> classes = dut.getClassesFromJarFile(new File(JAR_PATH));
        Set<String> names = classes.stream().map(Class::getName).collect(Collectors.toSet());

        assertTrue(names.containsAll(EXPECTED_CLASS_NAMES));
    }

    @Test
    public void svuOfThisClass() throws ClassNotFoundException, InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        long result = dut.getSerialVersionUID3(SerialversionTest.class);
        System.out.println("serialVersionUID: " + result);
        assertTrue(result != 0 && result != -1);
    }

}
