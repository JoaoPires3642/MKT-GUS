package com.mktgus.autoatendimento.controller.produto;

import com.mktgus.autoatendimento.Service.produto.ProdutoApiService;
import com.mktgus.autoatendimento.dto.produtoDTO.ProdutoDto;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Controller
@RequestMapping("/produtos")
@CrossOrigin(origins = "http://localhost:3000")
public class ProdutoController {

    private final ProdutoApiService produtoApiService;
    private final SimpMessagingTemplate messagingTemplate;

    public ProdutoController(ProdutoApiService produtoApiService, SimpMessagingTemplate messagingTemplate) {
        this.produtoApiService = produtoApiService;
        this.messagingTemplate = messagingTemplate;
    }

    // Endpoint para listar todos os produtos
    @GetMapping("/listar")
    public List<ProdutoDto> listarProdutos() {
        return produtoApiService.getTodosProdutos();
    }

    // Endpoint GET para buscar produto por código de barras
    @GetMapping("/buscar/{barcode}")
    public ResponseEntity<ProdutoDto> buscarProdutoPorCodigo(@PathVariable String barcode) {
        ProdutoDto produto = produtoApiService.getProdutoPorCodigoDeBarras(barcode);
        if (produto == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(produto);
    }

    // Endpoint POST para buscar produto por código de barras
    @PostMapping("/buscar")
    public ResponseEntity<ProdutoDto> buscarProdutoPorCodigoPost(@RequestBody BarcodeRequest barcodeRequest) {
        String barcode = barcodeRequest.getBarcode();
        ProdutoDto produto = produtoApiService.getProdutoPorCodigoDeBarras(barcode);
        if (produto == null) {
            // Enviar mensagem de erro via WebSocket
            messagingTemplate.convertAndSend("/topic/scanned-product",
                    new ErrorMessage("Produto não encontrado para o código de barras: " + barcode));
            return ResponseEntity.notFound().build();
        }
        // Enviar produto via WebSocket
        messagingTemplate.convertAndSend("/topic/scanned-product", produto);
        return ResponseEntity.ok(produto);
    }
}

// Classe auxiliar para o corpo da requisição POST
class BarcodeRequest {
    private String barcode;

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }
}

// Classe auxiliar para mensagens de erro
class ErrorMessage {
    private String message;

    public ErrorMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}