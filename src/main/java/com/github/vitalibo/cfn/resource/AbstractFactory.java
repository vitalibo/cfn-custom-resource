package com.github.vitalibo.cfn.resource;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.github.vitalibo.cfn.resource.model.ResourceProvisionRequest;
import com.github.vitalibo.cfn.resource.model.ResourceType;
import com.github.vitalibo.cfn.resource.model.transform.ResourceProvisionRequestDeserializer;
import com.github.vitalibo.cfn.resource.util.PreSignedUrl;
import lombok.AllArgsConstructor;

import java.net.MalformedURLException;

@AllArgsConstructor
public abstract class AbstractFactory<Type extends Enum<?> & ResourceType> {

    private final Class<Type> typeClass;

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

    public PreSignedUrl createPreSignedUrl(String responseUrl) throws MalformedURLException {
        return new PreSignedUrl(createJackson(), responseUrl);
    }

    public ObjectMapper createJackson() {
        ObjectMapper mapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addDeserializer(
            ResourceProvisionRequest.class,
            new ResourceProvisionRequestDeserializer(typeClass));
        mapper.registerModule(module);
        return mapper;
    }

}