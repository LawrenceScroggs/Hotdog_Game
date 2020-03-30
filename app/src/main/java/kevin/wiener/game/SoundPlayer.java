package kevin.wiener.game;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;


public class SoundPlayer {

    private AudioAttributes audioAttributes;
    final int SOUND_POOL_MAX = 2;
    private static final int streamType = AudioManager.STREAM_MUSIC;

    private static SoundPool soundPool;
    private static int hitSound;
    private static int overSound;

    public SoundPlayer(Context context) {

        // SoundPool is deprecated in API level 21. (Lollipop)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            audioAttributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_GAME)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build();

            SoundPool.Builder builder = new SoundPool.Builder();
            builder.setAudioAttributes(audioAttributes).setMaxStreams(SOUND_POOL_MAX);
            this.soundPool = builder.build();

        } else {
            //SoundPool (int maxStreams, int streamType, int srcQuality)
            soundPool = new SoundPool(SOUND_POOL_MAX, AudioManager.STREAM_MUSIC, 0);
        }


        this.hitSound = this.soundPool.load(context, R.raw.eat_one, 1);
        this.overSound = this.soundPool.load(context, R.raw.miss_one, 1);

    }

    public void playHitSound() {

        // play(int soundID, float leftVolume, float rightVolume, int priority, int loop, float rate)
        soundPool.play(hitSound, 1.0f, 1.0f, 1, 0, 1.0f);
    }

    public void playOverSound() {
        soundPool.play(overSound, 1.0f, 1.0f, 1, 0, 1.0f);
    }

}