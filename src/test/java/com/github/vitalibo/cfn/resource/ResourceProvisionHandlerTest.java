package com.github.vitalibo.cfn.resource;

import com.amazonaws.services.lambda.runtime.Context;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.vitalibo.cfn.resource.model.RequestType;
import com.github.vitalibo.cfn.resource.model.ResourceProvisionRequest;
import com.github.vitalibo.cfn.resource.model.ResourceProvisionResponse;
import com.github.vitalibo.cfn.resource.model.Status;
import com.github.vitalibo.cfn.resource.util.PreSignedUrl;
import org.mockito.*;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class ResourceProvisionHandlerTest {

    @Mock
    private AbstractFactory<?> mockAbstractFactory;
    @Mock
    private ObjectMapper mockObjectMapper;
    @Mock
    private Context mockContext;
    @Mock
    private Facade mockFacade;
    @Mock
    private PreSignedUrl mockPreSignedUrl;
    @Captor
    private ArgumentCaptor<ResourceProvisionResponse> captorResourceProvisionResponse;

    private ResourceProvisionHandler<?> spyResourceProvisionHandler;

    @BeforeMethod
    @SuppressWarnings("unchecked")
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        spyResourceProvisionHandler = Mockito.spy(new ResourceProvisionHandler(mockAbstractFactory, mockObjectMapper));
    }

    @Test
    public void testHandleRequestStream() throws IOException {
        ResourceProvisionRequest request = makeResourceProvisionRequest();
        ResourceProvisionResponse resource = makeResourceProvisionResponse();

        Mockito.doReturn(resource)
            .when(spyResourceProvisionHandler).handleRequest(Mockito.any(), Mockito.eq(mockContext));
        Mockito.when(mockObjectMapper.readValue(Mockito.any(InputStream.class), Mockito.eq(ResourceProvisionRequest.class)))
            .thenReturn(request);
        Mockito.when(mockObjectMapper.writeValueAsString(Mockito.any()))
            .thenReturn("response json");

        ByteArrayOutputStream output = new ByteArrayOutputStream();
        spyResourceProvisionHandler.handleRequest(
            new ByteArrayInputStream("sample".getBytes()), output, mockContext);

        String actual = new String(output.toByteArray());
        Assert.assertNotNull(actual);
        Assert.assertFalse(actual.isEmpty());
        Assert.assertEquals(actual, "response json");
        Mockito.verify(spyResourceProvisionHandler).handleRequest(request, mockContext);
    }

    @Test
    public void testHandleCreateRequest() throws IOException {
        Mockito.when(mockAbstractFactory.createCreateFacade(Mockito.any(ResourceProvisionRequest.class)))
            .thenReturn(mockFacade);
        Mockito.when(mockAbstractFactory.createPreSignedUrl(Mockito.anyString()))
            .thenReturn(mockPreSignedUrl);
        ResourceProvisionRequest request = makeResourceProvisionRequest();
        request.setRequestType(RequestType.Create);
        ResourceProvisionResponse response = makeResourceProvisionResponse();
        Mockito.when(mockFacade.process(request)).thenReturn(response);

        ResourceProvisionResponse actual = spyResourceProvisionHandler.handleRequest(request, mockContext);

        Assert.assertNotNull(actual);
        Assert.assertEquals(actual.getStatus(), Status.SUCCESS);
        Assert.assertEquals(actual.getRequestId(), "request id");
        Mockito.verify(mockAbstractFactory).createCreateFacade(request);
        Mockito.verify(mockFacade).process(request);
        Mockito.verify(mockPreSignedUrl).upload(response);
    }

    @Test
    public void testHandleDeleteRequest() throws IOException {
        Mockito.when(mockAbstractFactory.createDeleteFacade(Mockito.any(ResourceProvisionRequest.class)))
            .thenReturn(mockFacade);
        Mockito.when(mockAbstractFactory.createPreSignedUrl(Mockito.anyString()))
            .thenReturn(mockPreSignedUrl);
        ResourceProvisionRequest request = makeResourceProvisionRequest();
        request.setRequestType(RequestType.Delete);
        ResourceProvisionResponse response = makeResourceProvisionResponse();
        Mockito.when(mockFacade.process(request)).thenReturn(response);

        ResourceProvisionResponse actual = spyResourceProvisionHandler.handleRequest(request, mockContext);

        Assert.assertNotNull(actual);
        Assert.assertEquals(actual.getStatus(), Status.SUCCESS);
        Assert.assertEquals(actual.getRequestId(), "request id");
        Mockito.verify(mockAbstractFactory).createDeleteFacade(request);
        Mockito.verify(mockFacade).process(request);
        Mockito.verify(mockPreSignedUrl).upload(response);
    }

    @Test
    public void testHandleUpdateRequest() throws IOException {
        Mockito.when(mockAbstractFactory.createUpdateFacade(Mockito.any(ResourceProvisionRequest.class)))
            .thenReturn(mockFacade);
        Mockito.when(mockAbstractFactory.createPreSignedUrl(Mockito.anyString()))
            .thenReturn(mockPreSignedUrl);
        ResourceProvisionRequest request = makeResourceProvisionRequest();
        request.setRequestType(RequestType.Update);
        ResourceProvisionResponse response = makeResourceProvisionResponse();
        Mockito.when(mockFacade.process(request)).thenReturn(response);

        ResourceProvisionResponse actual = spyResourceProvisionHandler.handleRequest(request, mockContext);

        Assert.assertNotNull(actual);
        Assert.assertEquals(actual.getStatus(), Status.SUCCESS);
        Assert.assertEquals(actual.getRequestId(), "request id");
        Mockito.verify(mockAbstractFactory).createUpdateFacade(request);
        Mockito.verify(mockFacade).process(request);
        Mockito.verify(mockPreSignedUrl).upload(response);
    }

    @Test
    public void testFailHandleRequest() throws IOException {
        Mockito.when(mockAbstractFactory.createCreateFacade(Mockito.any(ResourceProvisionRequest.class)))
            .thenReturn(mockFacade);
        Mockito.when(mockAbstractFactory.createPreSignedUrl(Mockito.anyString()))
            .thenReturn(mockPreSignedUrl);
        ResourceProvisionRequest request = makeResourceProvisionRequest();
        request.setRequestType(RequestType.Create);
        Mockito.when(mockFacade.process(request)).thenThrow(new ResourceProvisionException("exception message"));

        ResourceProvisionResponse actual = spyResourceProvisionHandler.handleRequest(request, mockContext);

        Assert.assertNotNull(actual);
        Assert.assertEquals(actual.getStatus(), Status.FAILED);
        Assert.assertEquals(actual.getRequestId(), "request id");
        Mockito.verify(mockAbstractFactory).createCreateFacade(request);
        Mockito.verify(mockFacade).process(request);
        Mockito.verify(mockPreSignedUrl).upload(captorResourceProvisionResponse.capture());
        ResourceProvisionResponse response = captorResourceProvisionResponse.getValue();
        Assert.assertEquals(response.getStatus(), Status.FAILED);
        Assert.assertEquals(response.getReason(), "exception message");
        Assert.assertEquals(response.getLogicalResourceId(), request.getLogicalResourceId());
        Assert.assertEquals(response.getRequestId(), request.getRequestId());
        Assert.assertEquals(response.getStackId(), request.getStackId());
        Assert.assertEquals(response.getPhysicalResourceId(), request.getPhysicalResourceId());
    }

    private static ResourceProvisionRequest makeResourceProvisionRequest() {
        ResourceProvisionRequest request = new ResourceProvisionRequest();
        request.setRequestId("request id");
        request.setStackId("stack id");
        request.setResponseUrl("request url");
        request.setLogicalResourceId("logical resource id");
        request.setPhysicalResourceId("physical resource id");
        return request;
    }

    private static ResourceProvisionResponse makeResourceProvisionResponse() {
        return new ResourceProvisionResponse()
            .withRequestId("request id")
            .withPhysicalResourceId("physical resource id")
            .withStatus(Status.SUCCESS);
    }

}