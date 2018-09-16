package com.github.vitalibo.cfn.resource.model;

public interface ResourceType {

    String getTypeName();

    Class<? extends ResourceProperties> getTypeClass();

}