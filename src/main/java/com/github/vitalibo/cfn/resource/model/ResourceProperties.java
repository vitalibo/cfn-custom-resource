package com.github.vitalibo.cfn.resource.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Objects;

@Data
public class ResourceProperties {

    @JsonProperty(value = "ServiceToken")
    private String serviceToken;

    @JsonIgnore
    private String deserializationError;

    public boolean hasDeserializationError() {
        return Objects.nonNull(deserializationError);
    }

    public ResourceProperties withDeserializationError(String deserializationError) {
        this.deserializationError = deserializationError;
        return this;
    }

}