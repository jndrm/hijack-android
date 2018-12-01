/************************************************
 * PowerSin ÕýÏÒ²¨²úÉúÀà£¬Í¨¹ýÕýÏÒ²¨ÏòÄ¿±ê°å¹©µç
 */

package com.nxp.HijackU;


import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;


public class PowerSin {
    public static final int Sample_Rate = 44100;//²ÉÑùÆµÂÊ
    public static final float MAXVOLUME = 100f;
    public static final int LEFT = 1;
    public static final int RIGHT = 2;
    public static final int DOUBLE = 3;

    AudioTrack audioTrackLight;
    /**
     * ÒôÁ¿
     **/
    float volume;
    /**
     * ÉùµÀ
     **/
    int channel = RIGHT;
    /**
     * ×Ü³¤¶È
     **/
    int length;
    /**
     * Ò»¸öÕýÏÒ²¨µÄ³¤¶È
     **/
    int waveLen;
    /**
     * ÆµÂÊ
     **/
    int Hz;
    /**
     * ÕýÏÒ²¨
     **/
//	byte[] wave;
    short[] wave;

//	public PowerSin(){
//
//	}

    /**
     * ÉèÖÃÆµÂÊ£¬³õÊ¼»¯Ó²¼þ  ½¨Á¢audioTrackLight¶ÔÏó
     *
     * @param rate
     */
    public void start(int rate) {
        stop();
        if (rate > 0) {
            Hz = rate;
            waveLen = Sample_Rate / Hz;
            int minbufsize = AudioTrack.getMinBufferSize(Sample_Rate,
                AudioFormat.CHANNEL_OUT_MONO, //µ¥ÉùµÀ
                AudioFormat.ENCODING_PCM_16BIT);//16Î»PCM
            length = (minbufsize / waveLen) * waveLen;//waveÊÇÒ»¸öÕýÏÒ²¨µÄ³¤¶È£¬minbufsizeÊÇÏµÍ³¹æ¶¨µÄbuffer´óÐ¡£¬ÕûÊý³ý·¨£¬È·±£lengthÊÇwavelenµÄÕûÊý±¶£¬²»»áÔÚÏÎ½Ó´¦³öÏÖÔÓ²¨,È·±£lenth>minbuffersize
//			wave=new byte[length];
            wave = new short[length];

            audioTrackLight = new AudioTrack(AudioManager.STREAM_MUSIC, Sample_Rate,
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT, length * 2, AudioTrack.MODE_STATIC);//Êý¾Ý½Ï´ó²»ÄÜÓÃstatic,¿ÉÒÔÓÃsetLoopPointsÑ­»·²¥·Å
            //Éú³ÉÕýÏÒ²¨
//			wave=SinWave.sin(wave, waveLen, length);
            wave = SinWave.sin(wave, length);
            audioTrackLight.write(wave, 0, length);
            audioTrackLight.flush();
            setVolume(100);
            audioTrackLight.setLoopPoints(0, wave.length, -1);
            new RecordPlayThread().start();
//			if(audioTrackLight!=null){
//				audioTrackLight.play();
//			}
        } else {
            return;
        }

    }

    class RecordPlayThread extends Thread {
        public void run() {
            try {
                if (audioTrackLight != null) {
                    audioTrackLight.play();
                }
//					audioTrackLight.write(wave, 0, length);

            } catch (Throwable t) {

            }
        }
    }


    /**
     * Ð´ÈëÊý¾Ý£¬ÉùµÀµÃµ½ÕýÏÒ²¨
     */
    public void play() {
        if (audioTrackLight != null) {
            audioTrackLight.write(wave, 0, length);
        }
    }

    /**
     * Í£Ö¹²¥·Å
     */
    public void stop() {
        if (audioTrackLight != null) {
            audioTrackLight.stop();
            audioTrackLight.release();
            audioTrackLight = null;
        }
    }

    /**
     * ÉèÖÃÒôÁ¿
     *
     * @param volume
     */
    public void setVolume(float volume) {
        this.volume = volume;
        if (audioTrackLight != null) {
            switch (channel) {
                case LEFT:
                    audioTrackLight.setStereoVolume(volume / MAXVOLUME, 0f);
                    break;
                case RIGHT: {
                    audioTrackLight.setStereoVolume(0f, volume / MAXVOLUME);
//				System.out.println("RIGHT");
                    break;
                }
                case DOUBLE:
                    audioTrackLight.setStereoVolume(volume / MAXVOLUME, volume / MAXVOLUME);
                    break;
            }
        }
    }

    /**
     * ÉèÖÃÉùµÀ
     *
     * @param channel
     */
    public void setChannel(int channel) {
        this.channel = channel;
        setVolume(volume);
    }

}
