package com.github.vitalibo.cfn.resource.util;

import com.github.vitalibo.cfn.resource.ResourceProvisionException;
import com.github.vitalibo.cfn.resource.model.ResourceProperties;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static com.github.vitalibo.cfn.resource.util.Rules.Rule;

public class RulesTest {

    @Mock
    private Rule<ResourceProperties> mockRule;
    @Mock
    private ResourceProperties mockResourceProperties;

    private Rules<ResourceProperties> rules;

    @BeforeMethod
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        rules = new Rules<>(mockRule);
    }

    @Test
    public void testVerify() {
        rules.verify(mockResourceProperties);

        Mockito.verify(mockRule).accept(mockResourceProperties);
    }

    @Test(expectedExceptions = ResourceProvisionException.class)
    public void testFailVerify() {
        Mockito.doThrow(ResourceProvisionException.class)
            .when(mockRule).accept(Mockito.any());

        rules.verify(mockResourceProperties);
    }

}