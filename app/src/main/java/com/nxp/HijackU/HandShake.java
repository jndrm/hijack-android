/*******************************
 * SoundRecord.java 录音程序 通过录音把目标板发送上来的方波数据转换为PCM数据，供解码程序用
 */

package com.nxp.HijackU;

import android.os.Bundle;
import android.os.Message;

public class HandShake {

    public static boolean hsFlag = false;
    public boolean hsexit = false;
    HandShakeThread hsThread = null;
    AudioTrackTx msgohs = new AudioTrackTx();//但是缓冲区越大，时间间隔越久，所以两者间权衡

    /********************************
     * 设置好中断事件，初始化硬件建立handshake对象
     */
    public void start() {
        hsexit = false;
        if (hsThread == null) {
            HijackUActivity.sensordataDebug = 6;
//    		audioRecord=new AudioRecord(audioSource,recSampleRate,recChannel,recAudioFormat,minRecBufSize*4);
//	    	audioRecord.setPositionNotificationPeriod(minRecBufSize); //这是监听器，当缓冲区为minRecBufSize溢出时中断
//	    	audioRecord.setRecordPositionUpdateListener(mreclistener);//中断服务函数
            hsFlag = true;
            hsThread = new HandShakeThread();//minRecBufSize
            hsThread.start();
            HijackUActivity.sensordataDebug = 23;
        }
    }

    /******************************
     * 停止录音，释放资源
     */
    @SuppressWarnings("deprecation")
    public void stop() {
        hsexit = true;
        hsFlag = false;
        try {
            HandShakeThread.sleep(20);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        hsThread.interrupt();
        try {
            hsThread.join();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        try {
            hsThread.stop();
        } catch (SecurityException e) {

        }
        hsThread = null;

    }

    /********************************
     * 录音线程 不断读取录音PCM数据进行解码
     * @author Administrator
     *
     */
    public class HandShakeThread extends Thread {
        public HandShakeThread() {
        }

        @Override
        public void run() {
            while (HijackUActivity.hsenableflag) {
                if (HijackUActivity.funcmode != 255) {
                    if ((HijackUActivity.handshakeOK == false) && (HijackUActivity.handshakeC < 10)) {
                        switch (HijackUActivity.funcmode) {
                            case 0:
                                msgohs.msg_byte((byte) 129);    //mzh:0x81
                                HijackUActivity.handshakeC++;
                                break;
                            case 1:
                                msgohs.msg_byte((byte) 130);    //mzh:0x82
                                HijackUActivity.handshakeC++;
                                break;
                            case 2:
                                msgohs.msg_byte((byte) 131);    //mzh:0x83
                                HijackUActivity.handshakeC++;
                                break;
                            default:
                                break;
                        }
                        try {
                            Thread.currentThread();
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    } else if ((HijackUActivity.handshakeOK == false) && (HijackUActivity.handshakeC > 9)) {
                        HijackUActivity.handshakeC = 0;
                        HijackUActivity.funcmode = 255;
                        HijackUActivity.handshakeflag = false;
                        HijackUActivity.handshakefailedflag = true;
                        HijackUActivity.hsenableflag = false;
                        String str = " ";
                        msg_IC_num(str);
                    }
                }
            }
        }
    }

    public void msg_IC_num(String str) {
        Message msg = new Message();
        Bundle b = new Bundle();
        b.putString("IC_num", str);
        msg.setData(b);
        HijackUActivity.myHsHandler.sendMessage(msg);
    }
}
