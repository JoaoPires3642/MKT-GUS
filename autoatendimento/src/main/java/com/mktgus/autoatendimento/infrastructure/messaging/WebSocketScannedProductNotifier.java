package com.mktgus.autoatendimento.infrastructure.messaging;

import com.mktgus.autoatendimento.domain.gateway.ScannedProductNotifier;
import com.mktgus.autoatendimento.domain.model.Product;
import com.mktgus.autoatendimento.interfaces.api.mapper.ProductApiMapper;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
public class WebSocketScannedProductNotifier implements ScannedProductNotifier {
    private final ProductApiMapper productApiMapper;
    private final SimpMessagingTemplate messagingTemplate;

    public WebSocketScannedProductNotifier(ProductApiMapper productApiMapper, SimpMessagingTemplate messagingTemplate) {
        this.productApiMapper = productApiMapper;
        this.messagingTemplate = messagingTemplate;
    }

    @Override
    public void publishProduct(Product product) {
        messagingTemplate.convertAndSend("/topic/scanned-product", productApiMapper.toResponse(product));
    }

    @Override
    public void publishError(String message) {
        messagingTemplate.convertAndSend("/topic/scanned-product", new ErrorMessage(message));
    }

    public record ErrorMessage(String message) {
    }
}
