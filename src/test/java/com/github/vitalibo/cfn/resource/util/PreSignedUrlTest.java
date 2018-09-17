package com.github.vitalibo.cfn.resource.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class PreSignedUrlTest {

    @Mock
    private ObjectMapper mockObjectMapper;
    @Mock
    private HttpURLConnection mockHttpURLConnection;

    private PreSignedUrl preSignedURL;

    @BeforeMethod
    public void setUp() throws MalformedURLException {
        MockitoAnnotations.initMocks(this);
        preSignedURL = new PreSignedUrl(mockObjectMapper, new URL("http://foo.bar"));
    }

    @Test
    public void testUpload() throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Mockito.when(mockHttpURLConnection.getOutputStream()).thenReturn(outputStream);
        Mockito.when(mockHttpURLConnection.getResponseCode()).thenReturn(200);

        preSignedURL.upload(mockHttpURLConnection, "response");

        Mockito.verify(mockHttpURLConnection).setDoOutput(true);
        Mockito.verify(mockHttpURLConnection).setRequestMethod("PUT");
        Assert.assertEquals(new String(outputStream.toByteArray()).trim(), "response");
    }

    @Test(expectedExceptions = IOException.class)
    public void testFailUpload() throws IOException {
        Mockito.when(mockHttpURLConnection.getOutputStream()).thenReturn(new ByteArrayOutputStream());
        Mockito.when(mockHttpURLConnection.getResponseCode()).thenReturn(403);

        preSignedURL.upload(mockHttpURLConnection, "response");
    }

}