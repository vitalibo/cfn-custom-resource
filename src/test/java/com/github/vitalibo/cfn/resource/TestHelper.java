package com.github.vitalibo.cfn.resource;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

public final class TestHelper {

    private TestHelper() {
    }

    public static String resourceAsString(String resource) {
        return new BufferedReader(new InputStreamReader(resourceAsInputStream(resource)))
            .lines().collect(Collectors.joining());
    }

    public static InputStream resourceAsInputStream(String resource) {
        return TestHelper.class.getResourceAsStream(resource);
    }

}
