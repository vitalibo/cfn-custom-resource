package com.github.vitalibo.cfn.resource.facade;

import com.github.vitalibo.cfn.resource.Facade;
import com.github.vitalibo.cfn.resource.ResourceProvisionException;
import com.github.vitalibo.cfn.resource.model.*;
import com.github.vitalibo.cfn.resource.util.StackUtils;

public interface DeleteFacade<Properties extends ResourceProperties, Data extends ResourceData<Data>> extends Facade {

    @Override
    @SuppressWarnings("unchecked")
    default ResourceProvisionResponse process(ResourceProvisionRequest request) throws ResourceProvisionException {
        final Properties resourceProperties = (Properties) request.getResourceProperties();

        if (StackUtils.hasDefaultPhysicalResourceId(request)) {

            return new ResourceProvisionResponse()
                .withStatus(Status.SUCCESS)
                .withLogicalResourceId(request.getLogicalResourceId())
                .withRequestId(request.getRequestId())
                .withStackId(request.getStackId())
                .withPhysicalResourceId(request.getPhysicalResourceId());
        }

        final Data resourceData = delete(resourceProperties, request.getPhysicalResourceId());

        return new ResourceProvisionResponse()
            .withStatus(Status.SUCCESS)
            .withLogicalResourceId(request.getLogicalResourceId())
            .withRequestId(request.getRequestId())
            .withStackId(request.getStackId())
            .withPhysicalResourceId(resourceData.getPhysicalResourceId())
            .withData(resourceData);
    }

    Data delete(Properties properties, String physicalResourceId) throws ResourceProvisionException;

}