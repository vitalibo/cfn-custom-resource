package com.github.vitalibo.cfn.resource.facade;

import com.amazonaws.services.lambda.runtime.Context;
import com.github.vitalibo.cfn.resource.Facade;
import com.github.vitalibo.cfn.resource.ResourceProvisionException;
import com.github.vitalibo.cfn.resource.model.*;
import com.github.vitalibo.cfn.resource.util.Rules;

public interface UpdateFacade<Properties extends ResourceProperties, Data extends ResourceData<Data>> extends Facade, Rules.Verifier<Properties> {

    @Override
    @SuppressWarnings("unchecked")
    default ResourceProvisionResponse process(ResourceProvisionRequest request, Context context) throws ResourceProvisionException {
        final Properties resourceProperties = (Properties) request.getResourceProperties();
        verify(resourceProperties);

        final Properties oldResourceProperties;
        try {
            oldResourceProperties = (Properties) request.getOldResourceProperties();
            verify(oldResourceProperties);

        } catch (ResourceProvisionException ignored) {
            // When status UPDATE_ROLLBACK_IN_PROGRESS
            return new ResourceProvisionResponse()
                .withStatus(Status.SUCCESS)
                .withLogicalResourceId(request.getLogicalResourceId())
                .withRequestId(request.getRequestId())
                .withStackId(request.getStackId())
                .withPhysicalResourceId(request.getPhysicalResourceId());
        }

        final Data resourceData = update(
            resourceProperties, oldResourceProperties, request.getPhysicalResourceId(), context);

        return new ResourceProvisionResponse()
            .withStatus(Status.SUCCESS)
            .withLogicalResourceId(request.getLogicalResourceId())
            .withRequestId(request.getRequestId())
            .withStackId(request.getStackId())
            .withPhysicalResourceId(resourceData.getPhysicalResourceId())
            .withData(resourceData);
    }

    Data update(Properties properties, Properties oldProperties, String physicalResourceId, Context context) throws ResourceProvisionException;

}