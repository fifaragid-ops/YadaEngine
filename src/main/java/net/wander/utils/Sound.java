package net.wander.utils;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Простой менеджер одного звукового ресурса.
 * Поддерживает:
 *  - play() / play(gain, pitch, offsetSec)
 *  - stop()
 *  - destroy()
 *  - статический stopAll() для остановки всех активных звуков.
 *
 * ВНИМАНИЕ: Java Sound из коробки нормально работает с WAV (PCM).
 * MP3 не поддерживается без внешних библиотек.
 */
public class Sound {

    // Глобальный список всех "живых" и потенциально играющих звуков.
    private static final List<Sound> activeSounds =
            Collections.synchronizedList(new ArrayList<>());

    private final Clip clip;
    private final AudioFormat format;
    private final FloatControl gainControl;         // громкость (если доступна)
    private final FloatControl sampleRateControl;   // для pitch (если поддерживается микшером)

    private boolean destroyed = false;

    /**
     * @param path путь до звукового файла (желательно .wav)
     */
    public Sound(String path) {
        try {
            File file = new File(path);
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(file);

            Clip c = AudioSystem.getClip();
            c.open(audioIn);

            this.clip = c;
            this.format = c.getFormat();

            // Пытаемся получить управление громкостью
            FloatControl gc = null;
            if (clip.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
                gc = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            }
            this.gainControl = gc;

            // Пытаемся получить управление частотой сэмплирования для pitch
            FloatControl src = null;
            if (clip.isControlSupported(FloatControl.Type.SAMPLE_RATE)) {
                src = (FloatControl) clip.getControl(FloatControl.Type.SAMPLE_RATE);
            }
            this.sampleRateControl = src;

            activeSounds.add(this);
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            throw new RuntimeException("Не удалось загрузить звук: " + path, e);
        }
    }

    /** Проиграть звук с настройками по умолчанию: громкость=1, pitch=1, offset=0 сек. */
    public void play() {
        play(1.0, 1.0, 0.0);
    }

    /**
     * Проиграть звук с настройками.
     *
     * @param gain        линейная громкость (0..1 обычно; >1 можно, но может быть искажение)
     * @param pitch       множитель высоты/скорости (1.0 = оригинал; 2.0 = в 2 раза быстрее/выше)
     * @param offsetSec   смещение старта в секундах от начала звука
     */
    public synchronized void play(double gain, double pitch, double offsetSec) {
        if (destroyed) return;

        // Громкость
        if (gainControl != null) {
            setGainLinear(gain);
        }

        // Pitch (если поддерживается)
        if (sampleRateControl != null) {
            setPitch(pitch);
        }

        // Смещение по времени
        int totalFrames = clip.getFrameLength();
        float frameRate = format.getFrameRate();
        int offsetFrames = (int) (offsetSec * frameRate);

        if (offsetFrames < 0) offsetFrames = 0;
        if (offsetFrames >= totalFrames) offsetFrames = totalFrames - 1;

        clip.stop();
        clip.flush();
        clip.setFramePosition(offsetFrames);
        clip.start();
    }

    /** Остановить именно этот звук, если он сейчас играет. */
    public synchronized void stop() {
        if (destroyed) return;
        if (clip.isRunning() || clip.isActive()) {
            clip.stop();
            clip.flush();
            clip.setFramePosition(0);
        }
    }

    /** Играет ли сейчас этот звук. */
    public boolean isPlaying() {
        return clip.isRunning() || clip.isActive();
    }

    /**
     * "Уничтожить" звук: остановить, освободить ресурсы, убрать из глобального списка.
     * После destroy() использовать объект нельзя.
     */
    public synchronized void destroy() {
        if (destroyed) return;
        stop();
        clip.close();
        destroyed = true;
        activeSounds.remove(this);
    }

    /** Статический метод: остановить ВСЕ звуки, которые сейчас играют. */
    public static void stopAll() {
        // Копию списка, чтобы избежать ConcurrentModification при stop()/destroy()
        List<Sound> snapshot;
        synchronized (activeSounds) {
            snapshot = new ArrayList<>(activeSounds);
        }
        for (Sound s : snapshot) {
            s.stop();
        }
    }

    // ====== Внутренние вспомогательные методы ======

    /** Установить громкость в линейной шкале 0..1 (перевод в dB для FloatControl). */
    private void setGainLinear(double gain) {
        if (gainControl == null) return;

        // 0 -> минимум
        if (gain <= 0.0) {
            gainControl.setValue(gainControl.getMinimum());
            return;
        }

        // Переводим линейное значение в децибелы
        float dB = (float) (20.0 * Math.log10(gain));

        // Клэмп по допустимому диапазону
        dB = Math.max(gainControl.getMinimum(), Math.min(dB, gainControl.getMaximum()));
        gainControl.setValue(dB);
    }

    /** Установить pitch как множитель частоты дискретизации (если поддерживается). */
    private void setPitch(double pitch) {
        if (sampleRateControl == null) return;
        if (pitch <= 0) pitch = 0.01;

        float baseRate = format.getSampleRate();
        float newRate = (float) (baseRate * pitch);

        float min = sampleRateControl.getMinimum();
        float max = sampleRateControl.getMaximum();

        newRate = Math.max(min, Math.min(newRate, max));
        sampleRateControl.setValue(newRate);
    }
}