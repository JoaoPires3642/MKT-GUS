package com.mktgus.autoatendimento.util.Scanner;

import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import com.mktgus.autoatendimento.Service.produto.*;
import com.mktgus.autoatendimento.dto.produtoDTO.*;
import com.mktgus.autoatendimento.util.som.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;

@Component
@EnableScheduling
public class ScannerCodigoDeBarra {

    private static final String DROIDCAM_URL = System.getenv("DROIDCAM_URL");
    private InputStream videoStream;
    private long lastScanTime = 0;
    private static final long SCAN_DELAY_MS = 5000;

    @Autowired
    private ProdutoApiService produtoApiService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate; // Para enviar mensagens WebSocket

    public ScannerCodigoDeBarra() {
        try {
            URL url = new URL(DROIDCAM_URL);
            URLConnection connection = url.openConnection();
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
            videoStream = connection.getInputStream();
            System.out.println("Conectado ao stream de vídeo.");
        } catch (IOException e) {
            System.err.println("Erro ao conectar ao stream de vídeo: " + e.getMessage());
        }
    }

    @Scheduled(fixedRate = 5)
    public void captureAndProcessFrames() {
        try {
            if (videoStream == null) {
               // System.err.println("Stream de vídeo não disponível.");
                return;
            }

            BufferedImage frame = readMJPEGFrame(videoStream);
            if (frame == null) {
              //  System.err.println("Frame não capturado corretamente.");
                return;
            }

            String barcode = decodeBarcode(frame);
            long currentTime = System.currentTimeMillis();

            if (barcode != null && (currentTime - lastScanTime > SCAN_DELAY_MS)) {
                System.out.println("Código de barras detectado: " + barcode);
                lastScanTime = currentTime;
                Som.beep();

                // Chama o ProductInfoService
                ProdutoDto produtoDTO = produtoApiService.getProdutoPorCodigoDeBarras(barcode);
                if (produtoDTO != null) {
                    System.out.println("title: " + produtoDTO.nome() +
                            "Produto: " + produtoDTO.ean() +
                            ", Preço: " + produtoDTO.valor() +
                            ", ImagemURL: " + produtoDTO.urlImagem() +
                            ", MAIOR DE IDADE: " + produtoDTO.produtoMaiorDeIdade());

                    // Enviar o ProdutoDto para o frontend via WebSocket
                    messagingTemplate.convertAndSend("/topic/scanned-product", produtoDTO);
                } else {
                    System.out.println("Produto não encontrado.");
                    // Enviar mensagem de erro via WebSocket
                    messagingTemplate.convertAndSend("/topic/scanned-product",
                            new ErrorMessage("Produto não encontrado para o código de barras: " + barcode));
                }
            }
        } catch (Exception e) {
            System.err.println("Erro ao processar o frame: " + e.getMessage());
        }
    }

    private BufferedImage readMJPEGFrame(InputStream in) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int prev = 0, cur;
        while ((cur = in.read()) != -1) {
            if (prev == 0xFF && cur == 0xD8) {
                buffer.write(0xFF);
                buffer.write(0xD8);
                break;
            }
            prev = cur;
        }
        if (cur == -1) return null;

        prev = 0;
        while ((cur = in.read()) != -1) {
            buffer.write(cur);
            if (prev == 0xFF && cur == 0xD9) break;
            prev = cur;
        }
        byte[] imageBytes = buffer.toByteArray();
        return ImageIO.read(new ByteArrayInputStream(imageBytes));
    }

    private String decodeBarcode(BufferedImage image) {
        try {
            BufferedImageLuminanceSource source = new BufferedImageLuminanceSource(image);
            BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
            Result result = new MultiFormatReader().decode(bitmap);
            return result.getText();
        } catch (NotFoundException e) {
            return null;
        }
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