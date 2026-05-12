package com.mktgus.autoatendimento.infra.data.device.audio;

import java.awt.Toolkit;
import javax.sound.sampled.*;
import java.io.IOException;
import java.io.InputStream;

public class SoundPlayer {

    public static void beep() {
        try (InputStream soundStream = SoundPlayer.class.getResourceAsStream("/sounds/beep.wav")) {
            if (soundStream == null) {
                System.err.println("Arquivo de som nao encontrado: /sounds/beep.wav");
                Toolkit.getDefaultToolkit().beep();
                return;
            }

            try (AudioInputStream audioStream = AudioSystem.getAudioInputStream(soundStream)) {
                Clip clip = AudioSystem.getClip();
                clip.addLineListener(event -> {
                    if (event.getType() == LineEvent.Type.STOP) {
                        clip.close();
                    }
                });
                clip.open(audioStream);
                clip.start();
            }
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            System.err.println("Erro ao reproduzir o som: " + e.getMessage());
            Toolkit.getDefaultToolkit().beep();
        }
    }
}
