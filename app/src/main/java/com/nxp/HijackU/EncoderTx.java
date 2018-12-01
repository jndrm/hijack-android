/**********************************
 * Encoder 这是一个编码类，用于把要发送的数据进行编码编为16位的PCM数据格式
 * 公式=1.01-sin(wt)每种android手机数据格式不一样，魅族M9:数据值为1输出正弦波波峰，-1输出正弦波波谷。数据-128和127为正弦波0点，正好与常理相反，由于无法获得手机硬件，原因无法查究
 */

package com.nxp.HijackU;

public class EncoderTx {
    public static int sampleRate = 44100;
    public static float sampleBaud = 1378.125f;
    public static int sampleBit = 32;
    public static int bitTxLength = 17; //frame header: 3bits(1) + start bit: 1bit(0) + Data: 8bits + parity bit: 1bit + stop bit: 1bit(1) + frame tail: 3bits(1)
    public static int dataLength = 0;
    public static double W_PI2 = 2 * 3.1415f;
    public static double Hifreq = 1378.125f;
    public static double Lofreq = Hifreq / 2.0f;
    public int audioAM = 32767;
    public int audioTxBufLength = 0;
    public static short[] highLevel = new short[sampleBit];
    public static short[] lowLevel = new short[sampleBit];
    private int counter_i = 0;
    private int counter_j = 0;
    private int counter_k = 0;

    public int getaudioTxBufsize() {
        audioTxBufLength = bitTxLength * sampleBit;//6 ones（包头3个ons,包尾3个ones） ,8bit数据,0start ,1stop,1parity
        return audioTxBufLength;
    }

    /***********************************************
     initiate transmit 1 and 0 basic data
     */
    public void initEncoderTxData() {
        for (counter_i = 0; counter_i < sampleBit; counter_i++) {
            highLevel[counter_i] = (short) (audioAM * (-Math.sin(Math.PI * counter_i / sampleBit * 2)));
            lowLevel[counter_i] = (short) (audioAM * (Math.sin(Math.PI * counter_i / sampleBit * 2)));
        }
    }

    /***********************************************
     update audio transmit buffer basic data
     */
    public short[] updateAudioTxBuf(byte audioTxData) {
        audioTxBufLength = sampleBit * bitTxLength;
        short[] audioTxBuf = new short[audioTxBufLength];
        byte[] audioTxBit = new byte[bitTxLength];
        byte paritybit = 0;
        counter_k = 0;
        /* frame header */
        audioTxBit[0] = 1;
        audioTxBit[1] = 1;
        audioTxBit[2] = 1;
        /* start bit */
        audioTxBit[3] = 0;
        /* data */
        initEncoderTxData();
        for (counter_i = 4; counter_i < 12; counter_i++) {
            if (((audioTxData >> (counter_i - 4)) & 0x01) == 0x01)
                audioTxBit[counter_i] = 1;
            else
                audioTxBit[counter_i] = 0;
            paritybit += audioTxBit[counter_i];
        }
        /* parity bit */
        if ((paritybit & 0x01) == 0x01) {
            audioTxBit[12] = 1;
        } else
            audioTxBit[12] = 0;
        /* stop bit */
        audioTxBit[13] = 1;
        /* frame tail */
        audioTxBit[14] = 1;
        audioTxBit[15] = 1;
        audioTxBit[16] = 1;
        /* update audio transmit buffer */
        for (counter_i = 0; counter_i < bitTxLength; counter_i++) {
            if ((audioTxBit[counter_i] & 0x01) == 0x01) {
                for (counter_j = 0; counter_j < sampleBit; counter_j++) {
                    audioTxBuf[counter_k] = highLevel[counter_j];    //Samsung, Xiaomi(MIUI)
//					audioTxBuf[counter_k] = lowLevel[counter_j];	//else
                    counter_k++;
                }
            } else {
                for (counter_j = 0; counter_j < sampleBit; counter_j++) {
                    audioTxBuf[counter_k] = lowLevel[counter_j];    //Samsung, Xiaomi(MIUI)
//					audioTxBuf[counter_k] = highLevel[counter_j];	//else
                    counter_k++;
                }
            }

        }
        return audioTxBuf;
    }

}
