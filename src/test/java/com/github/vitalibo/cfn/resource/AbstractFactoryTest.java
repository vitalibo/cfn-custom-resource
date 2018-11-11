package com.github.vitalibo.cfn.resource;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.vitalibo.cfn.resource.model.RequestType;
import com.github.vitalibo.cfn.resource.model.ResourceProperties;
import com.github.vitalibo.cfn.resource.model.ResourceProvisionRequest;
import com.github.vitalibo.cfn.resource.model.ResourceType;
import com.github.vitalibo.cfn.resource.util.PreSignedUrl;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

public class AbstractFactoryTest {

    @Mock
    private Facade mockFacade;

    private TestFactory spyFactory;

    @BeforeMethod
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        spyFactory = Mockito.spy(TestFactory.class);
    }

    @DataProvider
    public Object[][] samples() {
        return new Object[][]{
            sample(AbstractFactory::createCreateFacade),
            sample(AbstractFactory::createUpdateFacade),
            sample(AbstractFactory::createDeleteFacade)
        };
    }

    @Test(dataProvider = "samples",
        expectedExceptions = ResourceProvisionException.class,
        expectedExceptionsMessageRegExp = "Unsupported Resource Type")
    public void testVerifyUnsupportedType(BiFunction<AbstractFactory, ResourceProvisionRequest, Facade> function) {
        function.apply(spyFactory, new ResourceProvisionRequest());
    }

    @Test
    public void testCreateCreateFacade() {
        ResourceProvisionRequest request = new ResourceProvisionRequest();
        request.setResourceType(TestResourceType.TypeOne);
        Mockito.doReturn(mockFacade)
            .when(spyFactory).createCreateFacade(TestResourceType.TypeOne);

        Facade actual = spyFactory.createCreateFacade(request);

        Assert.assertNotNull(actual);
        Assert.assertEquals(actual, mockFacade);
        Mockito.verify(spyFactory).createCreateFacade(TestResourceType.TypeOne);
    }

    @Test
    public void testCreateDeleteFacade() {
        ResourceProvisionRequest request = new ResourceProvisionRequest();
        request.setResourceType(TestResourceType.TypeOne);
        Mockito.doReturn(mockFacade)
            .when(spyFactory).createDeleteFacade(TestResourceType.TypeOne);

        Facade actual = spyFactory.createDeleteFacade(request);

        Assert.assertNotNull(actual);
        Assert.assertEquals(actual, mockFacade);
        Mockito.verify(spyFactory).createDeleteFacade(TestResourceType.TypeOne);
    }

    @Test
    public void testCreateUpdateFacade() {
        ResourceProvisionRequest request = new ResourceProvisionRequest();
        request.setResourceType(TestResourceType.TypeOne);
        Mockito.doReturn(mockFacade)
            .when(spyFactory).createUpdateFacade(TestResourceType.TypeOne);

        Facade actual = spyFactory.createUpdateFacade(request);

        Assert.assertNotNull(actual);
        Assert.assertEquals(actual, mockFacade);
        Mockito.verify(spyFactory).createUpdateFacade(TestResourceType.TypeOne);
    }

    @Test
    public void testCreatePreSignedUrl() throws MalformedURLException {
        PreSignedUrl actual = spyFactory.createPreSignedUrl("http://foo.bar");

        Assert.assertNotNull(actual);
    }

    @Test
    public void testCreateJackson() throws IOException {
        ObjectMapper actual = spyFactory.createJackson();

        Assert.assertNotNull(actual);
        ResourceProvisionRequest request = actual.readValue(
            TestHelper.resourceAsString("/sample/RequestTypeOne.json"), ResourceProvisionRequest.class);
        Assert.assertEquals(request.getRequestType(), RequestType.Update);
        Assert.assertEquals(request.getResponseUrl(), "pre-signed-url-for-update-response");
        Assert.assertTrue(request.getResourceProperties() instanceof TypeOne);
    }

    private static Object[] sample(BiFunction<AbstractFactory, ResourceProvisionRequest, Facade> function) {
        return new Object[]{function};
    }

    @Getter
    @AllArgsConstructor
    private enum TestResourceType implements ResourceType {

        TypeOne("Custom::TypeOne", TypeOne.class);

        private final String typeName;
        private final Class<? extends ResourceProperties> typeClass;

    }

    @Data
    private static class TypeOne extends ResourceProperties {

        private String key1;
        private List<String> key2;
        private Map<String, String> key3;

    }

    private abstract static class TestFactory extends AbstractFactory<TestResourceType> {

        public TestFactory() {
            super(TestResourceType.class);
        }

    }

}