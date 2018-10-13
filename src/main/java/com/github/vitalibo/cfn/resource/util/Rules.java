package com.github.vitalibo.cfn.resource.util;

import com.github.vitalibo.cfn.resource.ResourceProvisionException;
import com.github.vitalibo.cfn.resource.model.ResourceProperties;

import java.util.Arrays;
import java.util.Collection;
import java.util.function.Consumer;

public class Rules<Properties extends ResourceProperties> {

    private final Collection<Rule<Properties>> rules;

    @SafeVarargs
    public Rules(Rule<Properties>... rules) {
        this.rules = Arrays.asList(rules);
    }

    public void forEach(Properties properties) throws ResourceProvisionException {
        rules.forEach(rule -> rule.accept(properties));
    }

    public interface Verifier<T extends ResourceProperties> {

        default void verify(T properties) throws ResourceProvisionException {
            if (properties.hasDeserializationError()) {
                throw new ResourceProvisionException(properties.getDeserializationError());
            }

            forEach(properties);
        }

        default void forEach(T properties) throws ResourceProvisionException {
        }

    }

    @FunctionalInterface
    public interface Rule<T extends ResourceProperties> extends Consumer<T> {
    }
}