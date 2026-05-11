package com.mktgus.autoatendimento.infra.data.external.barcode;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import com.mktgus.autoatendimento.infra.data.device.audio.SoundPlayer;
import com.mktgus.autoatendimento.infra.data.external.barcode.events.BarcodeDetectedEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

@Component
public class DroidCamBarcodeScanner {
    private static final Logger LOGGER = LoggerFactory.getLogger(DroidCamBarcodeScanner.class);
    private static final long SCAN_DELAY_MS = 5000;
    private static final String DROID_CAM_URL = "http://192.168.100.9:4747/video";

    private final ApplicationEventPublisher publisher;
    private InputStream videoStream;
    private long lastScanTime;

    public DroidCamBarcodeScanner(ApplicationEventPublisher publisher) {
        this.publisher = publisher;
        this.videoStream = connectToStream();
    }

    @Scheduled(fixedDelay = 100)
    public void captureAndProcessFrames() {
        try {
            if (videoStream == null) {
                videoStream = connectToStream();
                return;
            }

            BufferedImage frame = readFrame(videoStream);
            if (frame == null) {
                closeStream();
                return;
            }

            publishBarcodeIfDetected(decodeBarcode(frame));
        } catch (Exception e) {
            LOGGER.warn("Erro ao processar frame da camera DroidCam. O stream sera reconectado.", e);
            closeStream();
        }
    }

    private InputStream connectToStream() {
        try {
            URLConnection connection = new URL(DROID_CAM_URL).openConnection();
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
            InputStream stream = connection.getInputStream();
            LOGGER.info("Conectado ao stream de video DroidCam em {}", DROID_CAM_URL);
            return stream;
        } catch (IOException exception) {
            LOGGER.warn("Stream de video DroidCam indisponivel em {}", DROID_CAM_URL);
            return null;
        }
    }

    private BufferedImage readFrame(InputStream inputStream) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int previous = 0;
        int current;

        while ((current = inputStream.read()) != -1) {
            if (previous == 0xFF && current == 0xD8) {
                buffer.write(0xFF);
                buffer.write(0xD8);
                break;
            }
            previous = current;
        }

        if (current == -1) {
            return null;
        }

        previous = 0;
        while ((current = inputStream.read()) != -1) {
            buffer.write(current);
            if (previous == 0xFF && current == 0xD9) {
                break;
            }
            previous = current;
        }

        return ImageIO.read(new ByteArrayInputStream(buffer.toByteArray()));
    }

    private void publishBarcodeIfDetected(String barcode) {
        String ean = barcode == null ? null : barcode.trim();
        if (ean == null || ean.isBlank()) {
            return;
        }

        long currentTime = System.currentTimeMillis();
        if (currentTime - lastScanTime <= SCAN_DELAY_MS) {
            return;
        }

        lastScanTime = currentTime;
        LOGGER.info("Codigo EAN capturado pela camera DroidCam: {}", ean);
        playCaptureSound();
        publisher.publishEvent(new BarcodeDetectedEvent(ean));
    }

    private void playCaptureSound() {
        try {
            SoundPlayer.beep();
        } catch (RuntimeException exception) {
            LOGGER.warn("Nao foi possivel reproduzir o som de captura do EAN.", exception);
        }
    }

    private void closeStream() {
        if (videoStream == null) {
            return;
        }

        try {
            videoStream.close();
        } catch (IOException exception) {
            LOGGER.debug("Erro ao fechar stream da DroidCam.", exception);
        } finally {
            videoStream = null;
        }
    }

    private String decodeBarcode(BufferedImage image) {
        try {
            Result result = new MultiFormatReader().decode(
                    new BinaryBitmap(new HybridBinarizer(new BufferedImageLuminanceSource(image)))
            );
            return result.getText();
        } catch (NotFoundException exception) {
            return null;
        }
    }
}
