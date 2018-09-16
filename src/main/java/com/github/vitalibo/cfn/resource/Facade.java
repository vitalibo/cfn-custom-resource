package com.github.vitalibo.cfn.resource;

import com.github.vitalibo.cfn.resource.model.ResourceProvisionRequest;
import com.github.vitalibo.cfn.resource.model.ResourceProvisionResponse;

public interface Facade {

    ResourceProvisionResponse process(ResourceProvisionRequest request) throws ResourceProvisionException;

}