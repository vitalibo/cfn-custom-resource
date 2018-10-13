package com.github.vitalibo.cfn.resource.util;

import com.github.vitalibo.cfn.resource.ResourceProvisionException;
import com.github.vitalibo.cfn.resource.model.ResourceProperties;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class VerifierTest {

    @Mock
    private ResourceProperties mockResourceProperties;
    @Spy
    private Rules.Verifier<ResourceProperties> spyVerifier;

    @BeforeMethod
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testVerify() {
        spyVerifier.verify(mockResourceProperties);

        Mockito.verify(mockResourceProperties).hasDeserializationError();
        Mockito.verify(spyVerifier).forEach(mockResourceProperties);
    }

    @Test
    public void testFailWhenHasDeserializationError() {
        Mockito.when(mockResourceProperties.hasDeserializationError())
            .thenReturn(true);
        Mockito.when(mockResourceProperties.getDeserializationError())
            .thenReturn("error");

        ResourceProvisionException actual = Assert.expectThrows(ResourceProvisionException.class,
            () -> spyVerifier.verify(mockResourceProperties));

        Assert.assertEquals(actual.getMessage(), "error");
        Mockito.verify(mockResourceProperties).hasDeserializationError();
        Mockito.verify(spyVerifier, Mockito.never()).forEach(mockResourceProperties);
    }

}