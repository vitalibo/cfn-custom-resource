package com.github.vitalibo.cfn.resource.facade;

import com.github.vitalibo.cfn.resource.model.*;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class CreateFacadeTest {

    @Spy
    private CreateFacade<ResourceProperties, ResourceData> spyCreateFacade;

    @BeforeMethod
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testProcess() {
        ResourceProvisionRequest request = new ResourceProvisionRequest();
        request.setRequestType(RequestType.Create);
        request.setResponseUrl("http://foo.bar");
        request.setStackId("stack id");
        request.setRequestId("request id");
        request.setLogicalResourceId("logical resource id");
        ResourceProperties resourceProperties = new ResourceProperties();
        resourceProperties.setServiceToken("service token");
        request.setResourceProperties(resourceProperties);
        ResourceData resourceData = new ResourceData();
        resourceData.setPhysicalResourceId("physical resource id");


        Mockito.doReturn(resourceData)
            .when(spyCreateFacade).create(Mockito.eq(resourceProperties));

        ResourceProvisionResponse actual = spyCreateFacade.process(request);

        Assert.assertNotNull(actual);
        Assert.assertNotNull(actual);
        Assert.assertEquals(actual.getStatus(), Status.SUCCESS);
        Assert.assertEquals(actual.getLogicalResourceId(), "logical resource id");
        Assert.assertEquals(actual.getRequestId(), "request id");
        Assert.assertEquals(actual.getStackId(), "stack id");
        Assert.assertEquals(actual.getPhysicalResourceId(), "physical resource id");
        Assert.assertEquals(actual.getData(), resourceData);
        Mockito.verify(spyCreateFacade).create(resourceProperties);
    }

}