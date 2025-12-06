import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

/**
 * 사운드 관리 클래스
 * 백그라운드 음악, 레버 사운드, 스핀 사운드 재생 담당
 */
public class SoundManager {
    private Clip backgroundMusic;
    private Clip leverSound;
    private Clip spinSound;
    
    private boolean isLeverPlaying = false;
    private boolean isSpinPlaying = false;
    private Runnable leverSoundFinishedCallback = null;
    
    /**
     * 오디오 형식을 지원되는 형식으로 변환
     * 24비트 오디오를 16비트로 변환하여 호환성 확보
     */
    private AudioInputStream convertToSupportedFormat(AudioInputStream audioStream) {
        AudioFormat format = audioStream.getFormat();
        AudioFormat targetFormat = new AudioFormat(
            AudioFormat.Encoding.PCM_SIGNED,
            format.getSampleRate(),
            16,
            format.getChannels(),
            format.getChannels() * 2,
            format.getSampleRate(),
            false
        );
        
        if (!format.matches(targetFormat)) {
            return AudioSystem.getAudioInputStream(targetFormat, audioStream);
        }
        return audioStream;
    }
    
    /**
     * 백그라운드 음악 재생
     * 볼륨을 5%로 설정하여 반복 재생
     */
    public void playBackgroundMusic() {
        try {
            stopBackgroundMusic();
            File soundFile = new File("sound/background_music.wav");
            if (!soundFile.exists()) {
                System.err.println("백그라운드 음악 파일을 찾을 수 없습니다: " + soundFile.getAbsolutePath());
                return;
            }
            
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(soundFile);
            audioStream = convertToSupportedFormat(audioStream);
            
            backgroundMusic = AudioSystem.getClip();
            backgroundMusic.open(audioStream);
            
            if (backgroundMusic.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
                FloatControl gainControl = (FloatControl) backgroundMusic.getControl(FloatControl.Type.MASTER_GAIN);
                float minGain = gainControl.getMinimum();
                float maxGain = gainControl.getMaximum();
                float targetGain = (float) (Math.log10(0.05) * 20);
                gainControl.setValue(Math.max(minGain, Math.min(maxGain, targetGain)));
            } else if (backgroundMusic.isControlSupported(FloatControl.Type.VOLUME)) {
                FloatControl volumeControl = (FloatControl) backgroundMusic.getControl(FloatControl.Type.VOLUME);
                float minVolume = volumeControl.getMinimum();
                float maxVolume = volumeControl.getMaximum();
                float targetVolume = minVolume + (maxVolume - minVolume) * 0.05f;
                volumeControl.setValue(targetVolume);
            }
            
            backgroundMusic.loop(Clip.LOOP_CONTINUOUSLY);
        } catch (UnsupportedAudioFileException e) {
            System.err.println("백그라운드 음악 재생 실패 (지원하지 않는 파일 형식): " + e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            System.err.println("백그라운드 음악 재생 실패 (파일 읽기 오류): " + e.getMessage());
            e.printStackTrace();
        } catch (LineUnavailableException e) {
            System.err.println("백그라운드 음악 재생 실패 (오디오 라인 사용 불가): " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("백그라운드 음악 재생 실패: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    public void stopBackgroundMusic() {
        if (backgroundMusic != null && backgroundMusic.isRunning()) {
            backgroundMusic.stop();
            backgroundMusic.close();
            backgroundMusic = null;
        }
    }
    
    public void playLeverSound() {
        playLeverSound(null);
    }
    
    /**
     * 레버 사운드 재생
     * @param onFinished 사운드 재생 완료 시 실행할 콜백
     */
    public void playLeverSound(Runnable onFinished) {
        if (isLeverPlaying) return;
        
        try {
            stopLeverSound();
            File soundFile = new File("sound/lever_sound.wav");
            if (soundFile.exists()) {
                AudioInputStream audioStream = AudioSystem.getAudioInputStream(soundFile);
                audioStream = convertToSupportedFormat(audioStream);
                leverSound = AudioSystem.getClip();
                leverSound.open(audioStream);
                leverSound.start();
                isLeverPlaying = true;
                
                final Runnable callback = onFinished;
                leverSound.addLineListener(new LineListener() {
                    @Override
                    public void update(LineEvent event) {
                        if (event.getType() == LineEvent.Type.STOP) {
                            isLeverPlaying = false;
                            if (leverSound != null) {
                                leverSound.close();
                                leverSound = null;
                            }
                            if (callback != null) {
                                callback.run();
                            }
                            if (leverSoundFinishedCallback != null) {
                                leverSoundFinishedCallback.run();
                                leverSoundFinishedCallback = null;
                            }
                        }
                    }
                });
            } else if (onFinished != null) {
                onFinished.run();
            }
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            System.err.println("레버 사운드 재생 실패: " + e.getMessage());
            e.printStackTrace();
            if (onFinished != null) {
                onFinished.run();
            }
        }
    }
    
    public void stopLeverSound() {
        isLeverPlaying = false;
        leverSoundFinishedCallback = null;
        if (leverSound != null && leverSound.isRunning()) {
            leverSound.stop();
            leverSound.close();
            leverSound = null;
        }
    }
    
    public void setLeverSoundFinishedCallback(Runnable callback) {
        this.leverSoundFinishedCallback = callback;
    }
    
    public boolean isLeverSoundPlaying() {
        return isLeverPlaying && leverSound != null && leverSound.isRunning();
    }
    
    /**
     * 스핀 사운드 재생
     * 룰렛이 돌아가는 동안 반복 재생
     */
    public void playSpinSound() {
        if (isSpinPlaying) return;
        
        try {
            stopSpinSound();
            File soundFile = new File("sound/spin_sound.wav");
            if (soundFile.exists()) {
                AudioInputStream audioStream = AudioSystem.getAudioInputStream(soundFile);
                audioStream = convertToSupportedFormat(audioStream);
                spinSound = AudioSystem.getClip();
                spinSound.open(audioStream);
                spinSound.loop(Clip.LOOP_CONTINUOUSLY);
                isSpinPlaying = true;
            }
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            System.err.println("스핀 사운드 재생 실패: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    public void stopSpinSound() {
        isSpinPlaying = false;
        if (spinSound != null && spinSound.isRunning()) {
            spinSound.stop();
            spinSound.close();
            spinSound = null;
        }
    }
}

