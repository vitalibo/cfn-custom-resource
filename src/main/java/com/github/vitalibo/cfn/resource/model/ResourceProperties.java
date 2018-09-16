package com.github.vitalibo.cfn.resource.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ResourceProperties {

    @JsonProperty(value = "ServiceToken")
    private String serviceToken;

}