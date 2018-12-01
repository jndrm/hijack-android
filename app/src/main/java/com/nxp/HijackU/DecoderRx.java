
/*************************************
 * Decoder 类是一个解码类，把录音得到的PCM文件进行解码，这和硬件解慢切斯特码思想一样，就是：“一个解码时钟到来时当前电平就是码元”
 *
 */
package com.nxp.HijackU;

import java.util.ArrayList;

import android.media.AudioRecord;
import android.os.Bundle;
import android.os.Message;

public class DecoderRx {
    public final byte startBitCheckFlag = 1;
    public final byte startBitFlag = 2;
    public final byte dataBitFlag = 3;
    public final byte parityBitFlag = 4;
    public final byte stopBitFlag = 5;
    public static int sampleBit = 32;
    private int counter_i = 0;
    public short highValue = 200;    //1阀值
    public short lowValue = -200;    //0阀值
    public byte longCounter = (byte) (sampleBit + sampleBit / 2);
    public byte shortCounter = (byte) (sampleBit / 2 + sampleBit / 4);
    public byte frameHeaderCounter = 0;
    public byte errorSampleBitCounter = 0;

    public final byte STARTBIT = 0;
    public final byte SAMEBIT = 1;
    public final byte NEXTBIT = 2;
    public final byte STOPBIT = 3;
    public final byte STARTBIT_FALL = 4;
    public final byte DECODE = 5;

    public int samplerate = 44100;//sample rate
    public byte samplesperbit = 32;
    public byte SHORT = (byte) (samplesperbit / 2 + samplesperbit / 4);//24
    public byte LONG = (byte) (samplesperbit + samplesperbit / 2);//48
    public static byte ones = 0;

    public void decoderAudioRxbuf() {
        ArrayList<Byte> dataRxByteList = new ArrayList<Byte>();
        short currentSampleBit = 0;
        short lastSampleBit = 0;
        short currentCounter = 0;
        short lastCounter = 0;
        int diffCounter = 0;
        short parityBit = 0;
        byte dataRxByte = 0;
        byte dataBitCounter = 0;
        byte decoderState = startBitCheckFlag;
        int audioRxBufLength = AudioRecordRx.minAudioRecordRxBufSize;
        short[] audioRxBuf = new short[audioRxBufLength];
//    	byte[][] testRxBuf = new byte [2][audioRxBufLength];
        while (AudioRecordRx.audioRecordFlag) {
            if (AudioRecordRx.audioReachedFlag) {
                AudioRecordRx.audioReachedFlag = false;  //clear flag
                audioRxBufLength = AudioRecordRx.audioRecord.read(audioRxBuf, 0, AudioRecordRx.minAudioRecordRxBufSize);
                if (audioRxBufLength == AudioRecord.ERROR_BAD_VALUE) {
                    //reserved
                } else {
                    for (counter_i = 0; counter_i < audioRxBufLength; counter_i++) {
                        short sampleValue = audioRxBuf[counter_i];
                        currentCounter += 1;
                        if (sampleValue > highValue) {
                            currentSampleBit = 1;//Galaxy SII(Samsung)
//    						currentSampleBit = 0;//XiaoMi(MIUI)
                            errorSampleBitCounter = 0;
                        } else if (sampleValue < lowValue) {
                            currentSampleBit = 0;//Galaxy SII(Samsung)
//    						currentSampleBit = 1;//XiaoMi(MIUI)
                            errorSampleBitCounter = 0;
                        } else errorSampleBitCounter++;
                        HijackU.phoneRxError = errorSampleBitCounter;
                        /* decoder */
                        if (currentSampleBit != lastSampleBit) {
                            diffCounter = currentCounter - lastCounter;
//    						testRxBuf[0][counter_i] = (byte)currentSampleBit;
//    						testRxBuf[1][counter_i] = (byte)diffCounter;
                            switch (decoderState) {
                                case startBitCheckFlag:
                                    if (lastSampleBit == 0 && currentSampleBit == 1) {
                                        decoderState = startBitFlag; //下一个状态为检测起始位
                                    }
                                    break;
                                case startBitFlag:
                                    if ((shortCounter < diffCounter) && (diffCounter < longCounter)) {
                                        if (frameHeaderCounter < 2) {
                                            frameHeaderCounter = 0;
                                            decoderState = startBitCheckFlag;
                                        } else {
                                            dataBitCounter = 0;
                                            parityBit = 0;
                                            dataRxByte = 0;
                                            decoderState = dataBitFlag;//起始位检测成功，下一个状态为解码转台，开始解码
                                        }
                                    } else {
                                        decoderState = startBitCheckFlag; //边沿间隔太短，证明不是起始位，而是“1”
                                        if (shortCounter > diffCounter) {
                                            frameHeaderCounter++;
                                        }
                                    }
                                    break;
                                case dataBitFlag:
                                case parityBitFlag:
                                case stopBitFlag:
                                    if ((shortCounter < diffCounter) && (diffCounter < longCounter)) {
                                        if (dataBitCounter < 8) {
                                            dataRxByte |= (currentSampleBit << dataBitCounter);
                                            dataBitCounter += 1;
                                            parityBit += currentSampleBit;
                                        } else if (dataBitCounter == 8) //第九位，奇偶校验位
                                        {
                                            if (currentSampleBit != (parityBit & 0x01)) {
                                                decoderState = startBitCheckFlag;
                                            } else {
                                                dataBitCounter += 1;
                                                decoderState = stopBitFlag;
                                            }
                                        } else if (dataBitCounter == 9) {
                                            if (currentSampleBit == 1)//停止位，证明数据接收成功
                                            {
                                                dataRxByteList.add(dataRxByte);
                                                dataBitCounter = 0;
                                                String str = " ";
//												if(HijackU.isrecord3==true)
//												{
//												  char strc;
//												  strc=(char)dataRxByte;
//												  str+=Character.toString(strc);
//												}
//												else
//												{
                                                if (dataRxByte < 16 && dataRxByte > -16) str += '0';
                                                str += Integer.toHexString((int) (dataRxByte & 0xff));
//												}
                                                msg_IC_num(str);//发送系统消息，发送到控件上显示出来

                                            } else {

                                            }
                                            decoderState = startBitCheckFlag;
                                        } else decoderState = startBitCheckFlag;
                                    } else if (diffCounter > longCounter) {  //边沿间隔太长，不正常，所以把状态变为STARTBIT
                                        frameHeaderCounter = 0;
                                        decoderState = startBitCheckFlag;
                                    } else {
                                        lastSampleBit = currentSampleBit;
                                        continue;
                                    }
                                    break;
                                default:
                                    break;
                            }
                            lastCounter = currentCounter;
                        }
                        lastSampleBit = currentSampleBit;
                    }
                    currentCounter = (short) (currentCounter - lastCounter);
                    lastCounter = 0;
                    audioRxBufLength = 0;
                }
            } else ;
        }
    }

    /***************************************************
     * msg_IC_num 用于发送消息的函数，把string 类型的消息发送到activity进行显示
     * @param str
     */
    public void msg_IC_num(String str) {//发送系统消息
        Message msg = new Message();
        Bundle b = new Bundle();
        b.putString("IC_num", str);
        msg.setData(b);
        HijackU.myICHandler.sendMessage(msg);
    }

}
