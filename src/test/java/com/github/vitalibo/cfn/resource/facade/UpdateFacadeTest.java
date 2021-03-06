package com.github.vitalibo.cfn.resource.facade;

import com.amazonaws.services.lambda.runtime.Context;
import com.github.vitalibo.cfn.resource.ResourceProvisionException;
import com.github.vitalibo.cfn.resource.model.*;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class UpdateFacadeTest {

    @Mock
    private Context mockContext;
    @Spy
    private UpdateFacade<ResourceProperties, ? extends ResourceData> spyUpdateFacade;

    @BeforeMethod
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testProcess() {
        ResourceProvisionRequest request = makeResourceProvisionRequest();
        ResourceProperties resourceProperties = request.getResourceProperties();
        ResourceProperties oldResourceProperties = request.getOldResourceProperties();
        ResourceData resourceData = makeResourceData();
        Mockito.doReturn(resourceData)
            .when(spyUpdateFacade).update(Mockito.any(), Mockito.any(), Mockito.anyString(), Mockito.eq(mockContext));

        ResourceProvisionResponse actual = spyUpdateFacade.process(request, mockContext);

        Assert.assertNotNull(actual);
        Assert.assertNotNull(actual);
        Assert.assertEquals(actual.getStatus(), Status.SUCCESS);
        Assert.assertEquals(actual.getLogicalResourceId(), "logical resource id");
        Assert.assertEquals(actual.getRequestId(), "request id");
        Assert.assertEquals(actual.getStackId(), "stack id");
        Assert.assertEquals(actual.getPhysicalResourceId(), "new physical resource id");
        Assert.assertEquals(actual.getData(), resourceData);
        Mockito.verify(spyUpdateFacade).verify(resourceProperties);
        Mockito.verify(spyUpdateFacade).verify(oldResourceProperties);
        Mockito.verify(spyUpdateFacade)
            .update(resourceProperties, oldResourceProperties, "physical resource id", mockContext);
    }

    @Test
    public void testUpdateRollbackInProgressAfterFail() {
        ResourceProvisionRequest request = makeResourceProvisionRequest();
        ResourceData resourceData = makeResourceData();
        Mockito.doThrow(ResourceProvisionException.class)
            .when(spyUpdateFacade).verify(request.getOldResourceProperties());
        Mockito.doReturn(resourceData)
            .when(spyUpdateFacade).update(Mockito.any(), Mockito.any(), Mockito.anyString(), Mockito.eq(mockContext));

        ResourceProvisionResponse actual = spyUpdateFacade.process(request, mockContext);

        Assert.assertNotNull(actual);
        Assert.assertNotNull(actual);
        Assert.assertEquals(actual.getStatus(), Status.SUCCESS);
        Assert.assertEquals(actual.getLogicalResourceId(), "logical resource id");
        Assert.assertEquals(actual.getRequestId(), "request id");
        Assert.assertEquals(actual.getStackId(), "stack id");
        Assert.assertEquals(actual.getPhysicalResourceId(), "physical resource id");
        Assert.assertNull(actual.getData());
        Mockito.verify(spyUpdateFacade).verify(request.getResourceProperties());
        Mockito.verify(spyUpdateFacade).verify(request.getOldResourceProperties());
        Mockito.verify(spyUpdateFacade, Mockito.never())
            .update(Mockito.any(), Mockito.any(), Mockito.anyString(), Mockito.eq(mockContext));
    }

    @Test
    public void testFailVerify() {
        ResourceProvisionRequest request = makeResourceProvisionRequest();
        Mockito.doThrow(ResourceProvisionException.class)
            .when(spyUpdateFacade).verify(request.getResourceProperties());

        Assert.expectThrows(ResourceProvisionException.class, () -> spyUpdateFacade.process(request, mockContext));

        Mockito.verify(spyUpdateFacade).verify(request.getResourceProperties());
        Mockito.verify(spyUpdateFacade, Mockito.never()).verify(request.getOldResourceProperties());
        Mockito.verify(spyUpdateFacade, Mockito.never())
            .update(Mockito.any(), Mockito.any(), Mockito.anyString(), Mockito.eq(mockContext));
    }

    private static ResourceProvisionRequest makeResourceProvisionRequest() {
        ResourceProvisionRequest request = new ResourceProvisionRequest();
        request.setRequestType(RequestType.Update);
        request.setResponseUrl("http://foo.bar");
        request.setStackId("stack id");
        request.setRequestId("request id");
        request.setLogicalResourceId("logical resource id");
        request.setPhysicalResourceId("physical resource id");
        ResourceProperties resourceProperties = new ResourceProperties();
        resourceProperties.setServiceToken("service token 1");
        request.setResourceProperties(resourceProperties);
        ResourceProperties oldResourceProperties = new ResourceProperties();
        oldResourceProperties.setServiceToken("service token 2");
        request.setOldResourceProperties(oldResourceProperties);
        return request;
    }

    private static ResourceData makeResourceData() {
        ResourceData resourceData = new ResourceData();
        resourceData.setPhysicalResourceId("new physical resource id");
        return resourceData;
    }

}