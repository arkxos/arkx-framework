package com.xdreamaker.demo.axon.domain.upcaster;

import org.axonframework.serialization.upcasting.event.IntermediateEventRepresentation;
import org.axonframework.serialization.upcasting.event.SingleEventUpcaster;

import java.util.Arrays;
import java.util.List;

/**
 * @author darkness
 * @date 2021/6/26 23:18
 * @version 1.0
 */
public class ContractEventUpCaster extends SingleEventUpcaster {

    private static List<SameEventUpCaster> upCasters = Arrays.asList(
        new ContractCreatedEventUpCaster(),
        new ContractUpdatedEventUpCaster()
    );

    @Override
    protected boolean canUpcast(IntermediateEventRepresentation intermediateRepresentation) {
        return upCasters.stream().anyMatch(o -> o.canUpcast(intermediateRepresentation));
    }

    @Override
    protected IntermediateEventRepresentation doUpcast(IntermediateEventRepresentation intermediateRepresentation) {
        SameEventUpCaster upCaster = upCasters.stream()
            .filter(o -> o.canUpcast(intermediateRepresentation))
            .findAny().orElseThrow(RuntimeException::new);
        return upCaster.doUpcast(intermediateRepresentation);
    }

}
