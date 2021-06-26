package com.xdreamaker.demo.axon.domain.upcaster;

import com.fasterxml.jackson.databind.JsonNode;
import org.axonframework.messaging.MetaData;
import org.axonframework.serialization.SerializedType;
import org.axonframework.serialization.SimpleSerializedType;
import org.axonframework.serialization.upcasting.event.IntermediateEventRepresentation;
import org.axonframework.serialization.upcasting.event.SingleEventUpcaster;

/**
 * @author darkness
 * @date 2021/6/26 23:17
 * @version 1.0
 */
public abstract class SameEventUpCaster extends SingleEventUpcaster {

    protected boolean canUpcast(IntermediateEventRepresentation intermediateRepresentation) {

        return outputType(intermediateRepresentation.getType()) != null;
    }

    @Override
    protected IntermediateEventRepresentation doUpcast(IntermediateEventRepresentation intermediateRepresentation) {
        return intermediateRepresentation.upcast(
            outputType(intermediateRepresentation.getType()),
            JsonNode.class,
            d -> this.doUpCastPayload(d, intermediateRepresentation),
            metaData -> this.doUpCastMetaData(metaData, intermediateRepresentation)
        );
    }

    public SimpleSerializedType outputType(SerializedType originType) {
        return new SimpleSerializedType(eventTypeName(), outputRevision(originType.getRevision()));
    }

    public abstract String eventTypeName();

    public abstract String outputRevision(String originRevision);

    public abstract JsonNode doUpCastPayload(JsonNode document, IntermediateEventRepresentation intermediateEventRepresentation);

    public abstract MetaData doUpCastMetaData(MetaData document, IntermediateEventRepresentation intermediateEventRepresentation);

}
