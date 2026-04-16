package com.mktgus.autoatendimento.infrastructure.external.barcode;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import com.mktgus.autoatendimento.application.model.FindProductByBarcodeInput;
import com.mktgus.autoatendimento.application.usecase.ProcessBarcodeScanUseCase;
import com.mktgus.autoatendimento.infrastructure.device.audio.SoundPlayer;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
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
@EnableScheduling
public class DroidCamBarcodeScanner {
    private static final long SCAN_DELAY_MS = 5000;

    private final ProcessBarcodeScanUseCase processBarcodeScanUseCase;
    private final String droidCamUrl = System.getenv("DROIDCAM_URL");
    private InputStream videoStream;
    private long lastScanTime;

    public DroidCamBarcodeScanner(ProcessBarcodeScanUseCase processBarcodeScanUseCase) {
        this.processBarcodeScanUseCase = processBarcodeScanUseCase;
        this.videoStream = connectToStream();
    }

    @Scheduled(fixedRate = 5)
    public void captureAndProcessFrames() {
        try {
            if (videoStream == null) {
                videoStream = connectToStream();
                return;
            }

            BufferedImage frame = readFrame(videoStream);
            String barcode = frame == null ? null : decodeBarcode(frame);
            if (barcode == null || System.currentTimeMillis() - lastScanTime <= SCAN_DELAY_MS) {
                return;
            }

            lastScanTime = System.currentTimeMillis();
            SoundPlayer.beep();
            processBarcodeScanUseCase.execute(new FindProductByBarcodeInput(barcode));
        } catch (com.mktgus.autoatendimento.application.exception.NotFoundException ignored) {
        } catch (Exception ignored) {
            videoStream = null;
        }
    }

    private InputStream connectToStream() {
        if (droidCamUrl == null || droidCamUrl.isBlank()) {
            return null;
        }

        try {
            URLConnection connection = new URL(droidCamUrl).openConnection();
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
            return connection.getInputStream();
        } catch (IOException exception) {
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
