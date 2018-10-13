package com.github.vitalibo.cfn.resource;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.vitalibo.cfn.resource.model.ResourceProvisionRequest;
import com.github.vitalibo.cfn.resource.model.ResourceProvisionResponse;
import com.github.vitalibo.cfn.resource.model.ResourceType;
import com.github.vitalibo.cfn.resource.model.Status;
import com.github.vitalibo.cfn.resource.util.PreSignedUrl;
import lombok.AllArgsConstructor;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

@AllArgsConstructor
public class ResourceProvisionHandler<Type extends Enum<?> & ResourceType> implements RequestStreamHandler {

    private final AbstractFactory<Type> factory;

    @Override
    public void handleRequest(InputStream input, OutputStream output, Context context) throws IOException {
        final ObjectMapper jackson = factory.createJackson();
        ResourceProvisionRequest request = jackson.readValue(input, ResourceProvisionRequest.class);

        ResourceProvisionResponse response = handleRequest(request, context);

        try (OutputStreamWriter writer = new OutputStreamWriter(output)) {
            writer.write(jackson.writeValueAsString(response));
            writer.flush();
        }
    }

    public ResourceProvisionResponse handleRequest(ResourceProvisionRequest request, Context context) throws IOException {
        ResourceProvisionResponse response;
        try {
            Facade facade;
            switch (request.getRequestType()) {
                case Create:
                    facade = factory.createCreateFacade(request);
                    break;
                case Delete:
                    facade = factory.createDeleteFacade(request);
                    break;
                case Update:
                    facade = factory.createUpdateFacade(request);
                    break;
                default:
                    throw new IllegalStateException();
            }

            response = facade.process(request, context);
        } catch (ResourceProvisionException e) {
            response = new ResourceProvisionResponse()
                .withStatus(Status.FAILED)
                .withReason(e.getMessage())
                .withLogicalResourceId(request.getLogicalResourceId())
                .withRequestId(request.getRequestId())
                .withStackId(request.getStackId())
                .withPhysicalResourceId(request.getPhysicalResourceId());
        }

        final PreSignedUrl preSignedUrl = factory.createPreSignedUrl(request.getResponseUrl());
        preSignedUrl.upload(response);

        return response;
    }

}