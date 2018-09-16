package com.github.vitalibo.cfn.resource.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ResourceProvisionResponse {

    @JsonProperty(value = "Status")
    private Status status;

    @JsonProperty(value = "Reason")
    private String reason;

    @JsonProperty(value = "PhysicalResourceId")
    private String physicalResourceId;

    @JsonProperty(value = "StackId")
    private String stackId;

    @JsonProperty(value = "RequestId")
    private String requestId;

    @JsonProperty(value = "LogicalResourceId")
    private String logicalResourceId;

    @JsonProperty(value = "Data")
    private ResourceData data;

    public ResourceProvisionResponse withStatus(Status status) {
        this.status = status;
        return this;
    }

    public ResourceProvisionResponse withReason(String reason) {
        this.reason = reason;
        return this;
    }

    public ResourceProvisionResponse withPhysicalResourceId(String physicalResourceId) {
        this.physicalResourceId = physicalResourceId;
        return this;
    }

    public ResourceProvisionResponse withStackId(String stackId) {
        this.stackId = stackId;
        return this;
    }

    public ResourceProvisionResponse withRequestId(String requestId) {
        this.requestId = requestId;
        return this;
    }

    public ResourceProvisionResponse withLogicalResourceId(String logicalResourceId) {
        this.logicalResourceId = logicalResourceId;
        return this;
    }

    public ResourceProvisionResponse withData(ResourceData data) {
        this.data = data;
        return this;
    }

}