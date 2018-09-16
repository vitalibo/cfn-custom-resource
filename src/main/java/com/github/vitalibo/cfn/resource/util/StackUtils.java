package com.github.vitalibo.cfn.resource.util;

import com.github.vitalibo.cfn.resource.model.ResourceProvisionRequest;

public class StackUtils {

    private static final String DEFAULT = "default";

    public static String makeDefaultPhysicalResourceId(ResourceProvisionRequest request) {
        return DEFAULT;
    }

    public static boolean hasDefaultPhysicalResourceId(ResourceProvisionRequest request) {
        return DEFAULT.equalsIgnoreCase(request.getPhysicalResourceId());
    }

}