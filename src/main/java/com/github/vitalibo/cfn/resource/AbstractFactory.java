package com.github.vitalibo.cfn.resource;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.github.vitalibo.cfn.resource.model.ResourceProvisionRequest;
import com.github.vitalibo.cfn.resource.model.ResourceType;
import com.github.vitalibo.cfn.resource.model.transform.ResourceProvisionRequestDeserializer;
import com.github.vitalibo.cfn.resource.util.PreSignedUrl;

import java.net.MalformedURLException;

public abstract class AbstractFactory<Type extends Enum<?> & ResourceType> {

    private final ObjectMapper jackson;

    public AbstractFactory(Class<Type> typeClass) {
        this.jackson = createJacksonMapper(typeClass);
    }

    @SuppressWarnings("unchecked")
    Facade createCreateFacade(ResourceProvisionRequest request) {
        return createCreateFacade((Type) request.getResourceType());
    }

    public abstract Facade createCreateFacade(Type resourceType);

    @SuppressWarnings("unchecked")
    Facade createDeleteFacade(ResourceProvisionRequest request) {
        return createDeleteFacade((Type) request.getResourceType());
    }

    public abstract Facade createDeleteFacade(Type resourceType);

    @SuppressWarnings("unchecked")
    Facade createUpdateFacade(ResourceProvisionRequest request) {
        return createUpdateFacade((Type) request.getResourceType());
    }

    public abstract Facade createUpdateFacade(Type resourceType);

    public ObjectMapper createJackson() {
        return jackson;
    }

    public PreSignedUrl createPreSignedUrl(String responseUrl) throws MalformedURLException {
        return new PreSignedUrl(jackson, responseUrl);
    }

    private ObjectMapper createJacksonMapper(Class<Type> typeClass) {
        final ObjectMapper mapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addDeserializer(
            ResourceProvisionRequest.class,
            new ResourceProvisionRequestDeserializer(typeClass));
        mapper.registerModule(module);
        mapper.setSerializationInclusion(Include.NON_NULL);
        return mapper;
    }

}