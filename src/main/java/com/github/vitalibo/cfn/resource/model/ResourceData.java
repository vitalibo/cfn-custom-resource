package com.github.vitalibo.cfn.resource.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ResourceData<T extends ResourceData> {

    @JsonProperty(value = "PhysicalResourceId")
    private String physicalResourceId;

    @SuppressWarnings("unchecked")
    public T withPhysicalResourceId(String physicalResourceId) {
        this.physicalResourceId = physicalResourceId;
        return (T) this;
    }

}