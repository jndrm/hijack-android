/********************************************
 * MessageOut.java 是一个由android发送数据到目标板的一个类，发送一个Byte。
 */


package com.nxp.HijackU;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;

public class AudioTrackTx {
    private static final int sampleRate = 44100;
    private static final int audioTrackTxStreamType = AudioManager.STREAM_MUSIC;
    private static final int audioTrackTxChannel = AudioFormat.CHANNEL_OUT_MONO;
    private static final int audioTrackTxFormat = AudioFormat.ENCODING_PCM_16BIT;
    private static final int audioTrackTxMode = AudioTrack.MODE_STATIC;
    public static AudioTrack audioTrack;
    public int minAudioTrackTxBufSize;
    public int audioTrackTxBufSize;
    public boolean issending = false;
    public boolean sendresult = false;
    public EncoderTx encoderTx;

    /***********************************
     * 构造函数，初始化硬件，建立audiotrack对象，建立解码类Encoder对象
     */
    public AudioTrackTx() {

        minAudioTrackTxBufSize = AudioTrack.getMinBufferSize(sampleRate, audioTrackTxChannel, audioTrackTxFormat);
        encoderTx = new EncoderTx();
    }

    /********************************
     * get_state 获取audiotrack对象的状态
     * @return
     */
    public int get_state() {
        return audioTrack.getPlayState();
    }

    /***********************************
     * msgIsSending 检查是否有信息正在发送
     * @return
     */
    public boolean msgIsSending() {
        return issending;
    }

    /*************************************
     * msg_byte 发送一个byte类型的数据到目标板
     * @param 所要发送的数据 msg
     * @return 返回发送是否成功的标志
     */
    public boolean msg_byte(byte msg) {
        int sendedsize = 0;
        short[] msg_PCM;
        if (issending) {
            msgStop();
        }

        int audioTrackTxsize;
        if (audioTrack != null) {
            audioTrack.release();
            audioTrack = null;
        }
        audioTrackTxBufSize = encoderTx.getaudioTxBufsize();
        if (audioTrackTxBufSize > minAudioTrackTxBufSize) {
            audioTrackTxsize = audioTrackTxBufSize;
        } else {
            audioTrackTxsize = minAudioTrackTxBufSize;
        }
        audioTrack = new AudioTrack(audioTrackTxStreamType,//use music channel
            sampleRate,
            audioTrackTxChannel,
            audioTrackTxFormat,
            audioTrackTxsize * 2,
            audioTrackTxMode);//用STATIC模式延时慢，必须用这种模式
        if (audioTrack.getState() != AudioTrack.STATE_UNINITIALIZED) {
            msg_PCM = new short[audioTrackTxBufSize];
            msg_PCM = encoderTx.updateAudioTxBuf((byte) msg);

            issending = true;
            sendedsize = audioTrack.write(msg_PCM, 0, msg_PCM.length);
            audioTrack.flush();
            audioTrack.setStereoVolume(1.0f, 0f);//设置左右声道音量
            audioTrack.play();
            if (sendedsize == audioTrackTxBufSize) {
                sendresult = true;

//		 System.out.println("success send write PCM_Byte: "+ sendedsize);
            } else {
                sendresult = false;

//		 System.out.println("fail send write PCM_Byte: "+ sendedsize);

            }
        } else {
//		 System.out.println("audio initial fail");
            audioTrack.release();
            audioTrack = null;
        }
        issending = false;

        return sendresult;

    }

    /**********************************
     * msg_string  发送一个string类型的数据到目标板
     * @param 所要发送的数据str
     * @return
     */
    public boolean msg_string(String str) {
        boolean sendresult = false;
        for (int i = 0; i < str.length(); i++) {
            if (!msg_byte((byte) str.charAt(i))) {
                sendresult = false;
                return sendresult;
            }
        }
        sendresult = true;
        return sendresult;

    }

    /*********************************
     * msgStop 释放硬件资源
     */
    public void msgStop() {
        if (audioTrack != null) {
            audioTrack.release();
            audioTrack = null;
        }
        issending = false;
    }
}
