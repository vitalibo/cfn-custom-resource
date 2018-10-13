package com.github.vitalibo.cfn.resource.facade;

import com.amazonaws.services.lambda.runtime.Context;
import com.github.vitalibo.cfn.resource.Facade;
import com.github.vitalibo.cfn.resource.ResourceProvisionException;
import com.github.vitalibo.cfn.resource.model.*;
import com.github.vitalibo.cfn.resource.util.StackUtils;

public interface DeleteFacade<Properties extends ResourceProperties, Data extends ResourceData<Data>> extends Facade {

    @Override
    @SuppressWarnings("unchecked")
    default ResourceProvisionResponse process(ResourceProvisionRequest request, Context context) throws ResourceProvisionException {
        final Properties resourceProperties = (Properties) request.getResourceProperties();

        if (StackUtils.hasDefaultPhysicalResourceId(request) || resourceProperties.hasDeserializationError()) {

            return new ResourceProvisionResponse()
                .withStatus(Status.SUCCESS)
                .withLogicalResourceId(request.getLogicalResourceId())
                .withRequestId(request.getRequestId())
                .withStackId(request.getStackId())
                .withPhysicalResourceId(request.getPhysicalResourceId());
        }

        final Data resourceData = delete(resourceProperties, request.getPhysicalResourceId(), context);

        return new ResourceProvisionResponse()
            .withStatus(Status.SUCCESS)
            .withLogicalResourceId(request.getLogicalResourceId())
            .withRequestId(request.getRequestId())
            .withStackId(request.getStackId())
            .withPhysicalResourceId(resourceData.getPhysicalResourceId())
            .withData(resourceData);
    }

    Data delete(Properties properties, String physicalResourceId, Context context) throws ResourceProvisionException;

}