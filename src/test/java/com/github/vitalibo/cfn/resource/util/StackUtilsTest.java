package com.github.vitalibo.cfn.resource.util;

import com.github.vitalibo.cfn.resource.model.ResourceProvisionRequest;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class StackUtilsTest {

    @DataProvider
    public Object[][] samples() {
        return new Object[][]{
            {"stack-name-LogicalId"}, {"stack-name-LogicalId--AW1"}, {"stack-name-LogicalId-Az24a"},
            {"other-stack-name-LogicalId-AW1"}, {"other-LogicalId-AW1"}
        };
    }

    @Test(dataProvider = "samples")
    public void testDoNotHasDefaultPhysicalResourceId(String physicalResourceId) {
        ResourceProvisionRequest request = new ResourceProvisionRequest();
        request.setStackId("stack-name");
        request.setLogicalResourceId("LogicalId");
        request.setPhysicalResourceId(physicalResourceId);

        boolean actual = StackUtils.hasDefaultPhysicalResourceId(request);

        Assert.assertFalse(actual);
    }

    @Test
    public void testMakeDefaultPhysicalResourceId() {
        ResourceProvisionRequest request = new ResourceProvisionRequest();
        request.setStackId("stack-name");
        request.setLogicalResourceId("LogicalId");

        String actual = StackUtils.makeDefaultPhysicalResourceId(request);

        Assert.assertNotNull(actual);
        Assert.assertTrue(actual.matches("stack-name-LogicalId-[A-Z0-9]{13}"));
    }

    @Test
    public void testHasDefaultPhysicalResourceId() {
        ResourceProvisionRequest request = new ResourceProvisionRequest();
        request.setStackId("stack-name");
        request.setLogicalResourceId("LogicalId");
        request.setPhysicalResourceId("stack-name-LogicalId-23RFVBHASD6A2");

        boolean actual = StackUtils.hasDefaultPhysicalResourceId(request);

        Assert.assertTrue(actual);
    }

    @Test(invocationCount = 10)
    public void testDefaultPhysicalResourceId() {
        ResourceProvisionRequest request = new ResourceProvisionRequest();
        request.setStackId("stack-name");
        request.setLogicalResourceId("LogicalId");

        String physicalResourceId = StackUtils.makeDefaultPhysicalResourceId(request);
        request.setPhysicalResourceId(physicalResourceId);
        boolean actual = StackUtils.hasDefaultPhysicalResourceId(request);

        Assert.assertTrue(actual);
    }

    @Test
    public void testRandomNextString() {
        String actual = StackUtils.Random.nextString(10);

        Assert.assertNotNull(actual);
        Assert.assertEquals(actual.length(), 10);
        Assert.assertTrue(actual.matches("[A-Z0-9]{10}"));
    }

}