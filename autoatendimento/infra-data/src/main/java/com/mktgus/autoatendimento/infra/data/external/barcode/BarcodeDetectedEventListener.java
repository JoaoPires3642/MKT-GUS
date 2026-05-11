package com.mktgus.autoatendimento.infra.data.external.barcode;

import com.mktgus.autoatendimento.application.exception.NotFoundException;
import com.mktgus.autoatendimento.application.product.FindProductByBarcodeInput;
import com.mktgus.autoatendimento.application.usecase.ProcessBarcodeScanUseCase;
import com.mktgus.autoatendimento.infra.data.external.barcode.events.BarcodeDetectedEvent;
import org.springframework.context.event.EventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class BarcodeDetectedEventListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(BarcodeDetectedEventListener.class);

    private final ProcessBarcodeScanUseCase processBarcodeScanUseCase;

    public BarcodeDetectedEventListener(ProcessBarcodeScanUseCase processBarcodeScanUseCase) {
        this.processBarcodeScanUseCase = processBarcodeScanUseCase;
    }

    @EventListener
    public void onBarcodeDetected(BarcodeDetectedEvent event) {
        try {
            LOGGER.info("Enviando EAN capturado pela DroidCam para o fluxo de busca de produtos: {}", event.ean());
            processBarcodeScanUseCase.execute(new FindProductByBarcodeInput(event.ean()));
        } catch (NotFoundException ignored) {
            LOGGER.info("Produto nao encontrado para EAN capturado pela DroidCam: {}", event.ean());
        }
    }
}
