package com.github.vitalibo.cfn.resource.model;

import org.testng.Assert;
import org.testng.annotations.Test;

public class ResourcePropertiesTest {

    @Test
    public void testNotHasDeserializationError() {
        ResourceProperties actual = new ResourceProperties();

        Assert.assertFalse(actual.hasDeserializationError());
    }

    @Test
    public void testHasDeserializationError() {
        ResourceProperties actual = new ResourceProperties()
            .withDeserializationError("error");

        Assert.assertTrue(actual.hasDeserializationError());
    }

}