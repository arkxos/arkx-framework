package com.xdreamaker.demo.axon.domain.upcaster;

import com.xdreamaker.demo.axon.domain.event.ContractUpdatedEvent;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.xdreamaker.demo.axon.domain.upcaster.SameEventUpCaster;
import org.axonframework.messaging.MetaData;
import org.axonframework.serialization.upcasting.event.IntermediateEventRepresentation;

import java.util.HashMap;

/**
 * @author darkness
 * @date 2021/6/26 23:21
 * @version 1.0
 */
public class ContractUpdatedEventUpCaster extends SameEventUpCaster {

    @Override
    public String eventTypeName() {
        return ContractUpdatedEvent.class.getTypeName();
    }

    @Override
    public String outputRevision(String originRevision) {
        final HashMap<String, String> revisionConvertMpp = new HashMap<>();
        revisionConvertMpp.put(null, "1.0.0");

        return revisionConvertMpp.get(originRevision);
    }

    @Override
    public JsonNode doUpCastPayload(JsonNode document, IntermediateEventRepresentation intermediateEventRepresentation) {

        // 每个版本只有一种升级方案，然后链式一步一步升级
        if (intermediateEventRepresentation.getType().getRevision() == null) {
            ((ObjectNode) document).put("industryName", "互联网");
        }

        return document;
    }

    @Override
    public MetaData doUpCastMetaData(MetaData document, IntermediateEventRepresentation intermediateEventRepresentation) {
        return document;
    }

}
