package com.github.vitalibo.cfn.resource.facade;

import com.github.vitalibo.cfn.resource.Facade;
import com.github.vitalibo.cfn.resource.ResourceProvisionException;
import com.github.vitalibo.cfn.resource.model.*;
import com.github.vitalibo.cfn.resource.util.StackUtils;

public interface CreateFacade<Properties extends ResourceProperties, Data extends ResourceData<Data>> extends Facade {

    @Override
    @SuppressWarnings("unchecked")
    default ResourceProvisionResponse process(ResourceProvisionRequest request) throws ResourceProvisionException {
        request.setPhysicalResourceId(
            StackUtils.makeDefaultPhysicalResourceId(request));

        final Properties resourceProperties = (Properties) request.getResourceProperties();
        verify(resourceProperties);

        final Data resourceData = create(resourceProperties);

        return new ResourceProvisionResponse()
            .withStatus(Status.SUCCESS)
            .withLogicalResourceId(request.getLogicalResourceId())
            .withRequestId(request.getRequestId())
            .withStackId(request.getStackId())
            .withPhysicalResourceId(resourceData.getPhysicalResourceId())
            .withData(resourceData);
    }

    Data create(Properties properties) throws ResourceProvisionException;

    default void verify(Properties properties) throws ResourceProvisionException {
    }

}