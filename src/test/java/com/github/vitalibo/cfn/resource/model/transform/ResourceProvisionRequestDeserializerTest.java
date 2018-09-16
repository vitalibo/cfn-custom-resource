package com.github.vitalibo.cfn.resource.model.transform;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.github.vitalibo.cfn.resource.TestHelper;
import com.github.vitalibo.cfn.resource.model.RequestType;
import com.github.vitalibo.cfn.resource.model.ResourceProperties;
import com.github.vitalibo.cfn.resource.model.ResourceProvisionRequest;
import com.github.vitalibo.cfn.resource.model.ResourceType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class ResourceProvisionRequestDeserializerTest {

    private ObjectMapper jackson;

    @BeforeMethod
    public void setUp() {
        jackson = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addDeserializer(
            ResourceProvisionRequest.class,
            new ResourceProvisionRequestDeserializer(SampleResourceType.class));
        jackson.registerModule(module);
    }

    @Test
    public void testDeserializeTypeOne() throws IOException {
        String json = TestHelper.resourceAsString("/RequestTypeOne.json");

        ResourceProvisionRequest actual = jackson.readValue(json, ResourceProvisionRequest.class);

        Assert.assertNotNull(actual);
        Assert.assertEquals(actual.getRequestType(), RequestType.Update);
        Assert.assertEquals(actual.getResponseUrl(), "pre-signed-url-for-update-response");
        Assert.assertEquals(actual.getStackId(), "arn:aws:cloudformation:us-east-2:namespace:stack/stack-name/guid");
        Assert.assertEquals(actual.getRequestId(), "unique id for this update request");
        Assert.assertEquals(actual.getResourceType(), SampleResourceType.TypeOne);
        Assert.assertEquals(actual.getLogicalResourceId(), "name of resource in template");
        Assert.assertEquals(actual.getPhysicalResourceId(), "custom resource provider-defined physical id");
        Assert.assertTrue(actual.getResourceProperties() instanceof TypeOne);
        TypeOne resourceProperties = (TypeOne) actual.getResourceProperties();
        Assert.assertEquals(resourceProperties.getKey1(), "new-string");
        Assert.assertEquals(resourceProperties.getKey2(), Collections.singletonList("new-list"));
        Assert.assertEquals(resourceProperties.getKey3(), Collections.singletonMap("key4", "new-map"));
        Assert.assertTrue(actual.getOldResourceProperties() instanceof TypeOne);
        TypeOne oldResourceProperties = (TypeOne) actual.getOldResourceProperties();
        Assert.assertEquals(oldResourceProperties.getKey1(), "string");
        Assert.assertEquals(oldResourceProperties.getKey2(), Collections.singletonList("list"));
        Assert.assertEquals(oldResourceProperties.getKey3(), Collections.singletonMap("key4", "map"));
    }

    @Test
    public void testDeserializeTypeTwo() throws IOException {
        String json = TestHelper.resourceAsString("/RequestTypeTwo.json");

        ResourceProvisionRequest actual = jackson.readValue(json, ResourceProvisionRequest.class);

        Assert.assertNotNull(actual);
        Assert.assertEquals(actual.getRequestType(), RequestType.Create);
        Assert.assertEquals(actual.getResponseUrl(), "pre-signed-url-for-update-response");
        Assert.assertEquals(actual.getStackId(), "arn:aws:cloudformation:us-east-2:namespace:stack/stack-name/guid");
        Assert.assertEquals(actual.getRequestId(), "unique id for this update request");
        Assert.assertEquals(actual.getResourceType(), SampleResourceType.TypeTwo);
        Assert.assertEquals(actual.getLogicalResourceId(), "name of resource in template");
        Assert.assertEquals(actual.getPhysicalResourceId(), "custom resource provider-defined physical id");
        Assert.assertTrue(actual.getResourceProperties() instanceof TypeTwo);
        TypeTwo resourceProperties = (TypeTwo) actual.getResourceProperties();
        Assert.assertEquals(resourceProperties.getString(), "value");
        Assert.assertEquals(resourceProperties.getList(), Arrays.asList(1, 2, 3));
    }


    @AllArgsConstructor
    private enum SampleResourceType implements ResourceType {

        TypeOne("Custom::TypeOne", TypeOne.class),
        TypeTwo("Custom::TypeTwo", TypeTwo.class);

        @Getter
        private final String typeName;

        @Getter
        private final Class<? extends ResourceProperties> typeClass;

    }

    @Data
    private static class TypeOne extends ResourceProperties {

        private String key1;
        private List<String> key2;
        private Map<String, String> key3;

    }

    @Data
    private static class TypeTwo extends ResourceProperties {

        private String string;
        private List<Integer> list;

    }


}