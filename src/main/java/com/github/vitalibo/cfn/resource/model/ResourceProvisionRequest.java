package com.github.vitalibo.cfn.resource.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ResourceProvisionRequest {

    @JsonProperty(value = "RequestType")
    private RequestType requestType;

    @JsonProperty(value = "ResponseURL")
    private String responseUrl;

    @JsonProperty(value = "StackId")
    private String stackId;

    @JsonProperty(value = "RequestId")
    private String requestId;

    @JsonProperty(value = "ResourceType")
    private ResourceType resourceType;

    @JsonProperty(value = "LogicalResourceId")
    private String logicalResourceId;

    @JsonProperty(value = "PhysicalResourceId")
    private String physicalResourceId;

    @JsonProperty(value = "ResourceProperties")
    private ResourceProperties resourceProperties;

    @JsonProperty(value = "OldResourceProperties")
    private ResourceProperties oldResourceProperties;

}