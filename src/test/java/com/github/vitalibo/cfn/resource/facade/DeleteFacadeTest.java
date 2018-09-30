package com.github.vitalibo.cfn.resource.facade;

import com.github.vitalibo.cfn.resource.model.*;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class DeleteFacadeTest {

    @Spy
    private DeleteFacade<ResourceProperties, ? extends ResourceData> spyDeleteFacade;

    @BeforeMethod
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testProcess() {
        ResourceProvisionRequest request = makeResourceProvisionRequest();
        ResourceProperties resourceProperties = request.getResourceProperties();
        ResourceData resourceData = makeResourceData();

        Mockito.doReturn(resourceData)
            .when(spyDeleteFacade).delete(Mockito.eq(resourceProperties), Mockito.anyString());

        ResourceProvisionResponse actual = spyDeleteFacade.process(request);

        Assert.assertNotNull(actual);
        Assert.assertNotNull(actual);
        Assert.assertEquals(actual.getStatus(), Status.SUCCESS);
        Assert.assertEquals(actual.getLogicalResourceId(), "logical resource id");
        Assert.assertEquals(actual.getRequestId(), "request id");
        Assert.assertEquals(actual.getStackId(), "stack id");
        Assert.assertEquals(actual.getPhysicalResourceId(), "physical resource id new");
        Assert.assertEquals(actual.getData(), resourceData);
        Mockito.verify(spyDeleteFacade).delete(resourceProperties, "physical resource id");
    }

    @Test
    public void testProcessWithDefaultPhysicalResourceId() {
        ResourceProvisionRequest request = makeResourceProvisionRequest();
        request.setPhysicalResourceId("default");
        ResourceData resourceData = makeResourceData();

        Mockito.doReturn(resourceData)
            .when(spyDeleteFacade).delete(Mockito.any(), Mockito.anyString());

        ResourceProvisionResponse actual = spyDeleteFacade.process(request);

        Assert.assertNotNull(actual);
        Assert.assertNotNull(actual);
        Assert.assertEquals(actual.getStatus(), Status.SUCCESS);
        Assert.assertEquals(actual.getLogicalResourceId(), "logical resource id");
        Assert.assertEquals(actual.getRequestId(), "request id");
        Assert.assertEquals(actual.getStackId(), "stack id");
        Assert.assertEquals(actual.getPhysicalResourceId(), "default");
        Assert.assertNull(actual.getData());
        Mockito.verify(spyDeleteFacade, Mockito.never()).delete(Mockito.any(), Mockito.anyString());
    }

    private static ResourceProvisionRequest makeResourceProvisionRequest() {
        ResourceProvisionRequest request = new ResourceProvisionRequest();
        request.setRequestType(RequestType.Delete);
        request.setResponseUrl("http://foo.bar");
        request.setStackId("stack id");
        request.setRequestId("request id");
        request.setLogicalResourceId("logical resource id");
        request.setPhysicalResourceId("physical resource id");
        ResourceProperties resourceProperties = new ResourceProperties();
        resourceProperties.setServiceToken("service token");
        request.setResourceProperties(resourceProperties);
        return request;
    }

    private static ResourceData makeResourceData() {
        ResourceData resourceData = new ResourceData();
        resourceData.setPhysicalResourceId("physical resource id new");
        return resourceData;
    }

}