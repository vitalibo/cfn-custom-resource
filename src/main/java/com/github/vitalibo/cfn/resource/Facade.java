package com.github.vitalibo.cfn.resource;

import com.amazonaws.services.lambda.runtime.Context;
import com.github.vitalibo.cfn.resource.model.ResourceProvisionRequest;
import com.github.vitalibo.cfn.resource.model.ResourceProvisionResponse;

public interface Facade {

    ResourceProvisionResponse process(ResourceProvisionRequest request, Context context) throws ResourceProvisionException;

}