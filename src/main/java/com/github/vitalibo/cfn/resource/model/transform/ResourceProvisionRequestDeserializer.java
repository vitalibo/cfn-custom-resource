package com.github.vitalibo.cfn.resource.model.transform;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.github.vitalibo.cfn.resource.model.RequestType;
import com.github.vitalibo.cfn.resource.model.ResourceProperties;
import com.github.vitalibo.cfn.resource.model.ResourceProvisionRequest;
import com.github.vitalibo.cfn.resource.model.ResourceType;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.Delegate;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public class ResourceProvisionRequestDeserializer extends JsonDeserializer<ResourceProvisionRequest> {

    private final Map<String, ResourceType> resourceTypes;

    public ResourceProvisionRequestDeserializer(Class<? extends Enum<?>> enumClass) {
        this.resourceTypes = enumConstantsToResourceType(enumClass);
    }

    @Override
    public ResourceProvisionRequest deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        return deserialize(new JsonNodeParser(parser), new DeserializationContextWrapper(context));
    }

    private ResourceProvisionRequest deserialize(JsonNodeParser parser, DeserializationContextWrapper context) throws IOException {
        final ResourceProvisionRequest request = new ResourceProvisionRequest();

        request.setRequestType(RequestType.valueOf(parser.text("RequestType")));
        request.setResponseUrl(parser.text("ResponseURL"));
        request.setStackId(parser.text("StackId"));
        request.setRequestId(parser.text("RequestId"));

        final ResourceType resourceType = resourceTypes.get(parser.text("ResourceType"));
        request.setResourceType(resourceType);

        request.setLogicalResourceId(parser.text("LogicalResourceId"));
        request.setPhysicalResourceId(parser.text("PhysicalResourceId"));

        final JsonParser resourcePropertiesParser = parser.traverse("ResourceProperties");
        if (Objects.nonNull(resourceType) && Objects.nonNull(resourcePropertiesParser)) {
            request.setResourceProperties(context.readResourceProperties(
                resourcePropertiesParser, resourceType.getTypeClass()));
        }

        final JsonParser oldResourcePropertiesParser = parser.traverse("OldResourceProperties");
        if (Objects.nonNull(resourceType) && Objects.nonNull(oldResourcePropertiesParser)) {
            request.setOldResourceProperties(context.readResourceProperties(
                oldResourcePropertiesParser, resourceType.getTypeClass()));
        }

        return request;
    }

    private static Map<String, ResourceType> enumConstantsToResourceType(Class<? extends Enum<?>> enumClass) {
        return Arrays.stream(enumClass.getEnumConstants())
            .map(o -> (ResourceType) o)
            .collect(Collectors.toMap(ResourceType::getTypeName, o -> o));
    }

    public static class JsonNodeParser {

        private final JsonNode root;

        JsonNodeParser(JsonParser parser) throws IOException {
            root = parser.getCodec().readTree(parser);
        }

        String text(String fieldName) {
            return node(fieldName).map(JsonNode::asText).orElse(null);
        }

        JsonParser traverse(String fieldName) throws IOException {
            Optional<JsonParser> optionalJsonParser = node(fieldName).map(TreeNode::traverse);

            if (!optionalJsonParser.isPresent()) {
                return null;
            }

            JsonParser jsonParser = optionalJsonParser.get();
            jsonParser.nextToken();
            return jsonParser;
        }

        private Optional<JsonNode> node(String fieldName) {
            return Optional.ofNullable(root.get(fieldName));
        }

    }

    @RequiredArgsConstructor
    private static class DeserializationContextWrapper {

        @Delegate
        private final DeserializationContext context;

        @SneakyThrows
        public ResourceProperties readResourceProperties(JsonParser resourcePropertiesParser,
                                                         Class<? extends ResourceProperties> typeClass) {
            try {
                return context.readValue(resourcePropertiesParser, typeClass);
            } catch (IOException e) {
                return typeClass.newInstance()
                    .withDeserializationError(e.getMessage());
            }
        }

    }

}