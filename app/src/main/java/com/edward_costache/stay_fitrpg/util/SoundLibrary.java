package com.edward_costache.stay_fitrpg.util;

import android.content.Context;
import android.media.MediaPlayer;

/**
 * Created by Edward Costache
 */
public abstract class SoundLibrary {

    private static MediaPlayer mediaPlayer;

    /**
     * A static function that plays a sound when called
     * @param context The context from where the function is called
     * @param resource The sound file that should be played
     */
    public static void playSound(Context context, int resource) {
        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer.create(context, resource);
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    stopSound();
                }
            });
        }
        mediaPlayer.start();
    }

    /**
     * A static function that makes sure a mediaPlayer is cleared from memory after a sound is played
     * NOTE: Needs to be called when a sound should be stopped. i.e before playing a new sound
     */
    public static void stopSound() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    /**
     * A function that loops a sound when called
     * NOTE: Automatically releases the mediaPlayer when the loop is over
     * @param context The context at which the method is called
     * @param resource The sound to be looped
     * @param loopAmount the amount of times to loop the sound
     */
    public static void playLoopSound(Context context, int resource, int loopAmount) {
        if (mediaPlayer == null) {
            final int[] count = {0};
            mediaPlayer = MediaPlayer.create(context, resource);
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    count[0]++;
                    if (count[0] < loopAmount) {
                        playSound(context, resource);
                    } else {
                        stopSound();
                    }
                }
            });
        }
        mediaPlayer.start();
    }
}
