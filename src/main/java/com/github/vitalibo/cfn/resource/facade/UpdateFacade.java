package com.github.vitalibo.cfn.resource.facade;

import com.github.vitalibo.cfn.resource.Facade;
import com.github.vitalibo.cfn.resource.ResourceProvisionException;
import com.github.vitalibo.cfn.resource.model.*;

public interface UpdateFacade<Properties extends ResourceProperties, Data extends ResourceData> extends Facade {

    @Override
    @SuppressWarnings("unchecked")
    default ResourceProvisionResponse process(ResourceProvisionRequest request) throws ResourceProvisionException {
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
            resourceProperties, oldResourceProperties, request.getPhysicalResourceId());

        return new ResourceProvisionResponse()
            .withStatus(Status.SUCCESS)
            .withLogicalResourceId(request.getLogicalResourceId())
            .withRequestId(request.getRequestId())
            .withStackId(request.getStackId())
            .withPhysicalResourceId(resourceData.getPhysicalResourceId())
            .withData(resourceData);
    }

    Data update(Properties properties, Properties oldProperties, String physicalResourceId) throws ResourceProvisionException;

    void verify(Properties properties) throws ResourceProvisionException;

}