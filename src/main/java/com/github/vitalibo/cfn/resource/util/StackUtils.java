package com.github.vitalibo.cfn.resource.util;

import com.github.vitalibo.cfn.resource.model.ResourceProvisionRequest;

public class StackUtils {

    private static final String DEFAULT_PHYSICAL_RESOURCE_ID_PATTER = "%s-%s-[A-Z0-9]+";

    public static String makeDefaultPhysicalResourceId(ResourceProvisionRequest request) {
        return String.format("%s-%s-%s", request.getStackId(), request.getLogicalResourceId(), Random.nextString(13));
    }

    public static boolean hasDefaultPhysicalResourceId(ResourceProvisionRequest request) {
        return request.getPhysicalResourceId()
            .matches(String.format(DEFAULT_PHYSICAL_RESOURCE_ID_PATTER, request.getStackId(), request.getLogicalResourceId()));
    }

    static class Random {

        private static final byte[] CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789".getBytes();

        private static final java.util.Random random = new java.util.Random();

        static String nextString(int length) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < length; i++) {
                sb.append((char) CHARACTERS[random.nextInt(CHARACTERS.length)]);
            }

            return sb.toString();
        }

    }

}