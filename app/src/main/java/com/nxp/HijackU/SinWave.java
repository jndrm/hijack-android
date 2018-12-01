///////////////////////SinWave.java
/////////////////生成用于供电的正弦波,
//公式=1.01-sin(wt)每种android手机数据格式不一样，魅族M9:数据值为1输出正弦波波峰，-1输出正弦波波谷。数据-128和127为正弦波0点，正好与常理相反，由于无法获得手机硬件，原因无法查究
package com.nxp.HijackU;

public class SinWave {
    /**
     * 正弦波的高度
     **/
    public static final int HEIGHT = 32767;    //16bit
    /**
     * 2PI
     **/
    public static final double TWOPI = 2 * 3.1415;
    public static boolean powersinflag = false;
    public static final short constfeq = 8;
    public static short[] powersin = new short[constfeq];
    public static short counter_j = 0;

    /**
     * 生成正弦波  PCM数据
     *
     * @param wave
     * @param waveLen 每段正弦波的长度
     * @param length  总长度
     * @return 返回对应正弦波所需的PCM数据
     */
//	public static byte[] sin(byte[] wave, int waveLen, int length) {//由于会i++所以length 至少比wavelen大1
//		for (int i = 0; i < length; i++) {
//			if(waveLen<3){
//				if(i%2==0){
//					wave[i]=1;
//					}
//				else{
//					wave[i]=-1;
//				}
//
//			}else{
//			wave[i] = (byte) (HEIGHT * (1.01 - Math.sin(TWOPI
//					* ((i % waveLen) * 1.00 / waveLen))));
//			//  System.out.println("sin "+ i + wave[i]);
//			}
//		}
//		return wave;
//	}
    public static short[] sin(short[] wave, int length) {//由于会i++所以length 至少比wavelen大1
        if (powersinflag == false) {
            for (int i = 0; i < constfeq; i++) {
                powersin[i] = (short) (HEIGHT * (Math.sin(Math.PI * i / constfeq * 2)));
            }
            powersinflag = true;
            counter_j = 0;
        }
        for (int i = 0; i < length; i++) {
            wave[i] = powersin[counter_j];
            counter_j++;
            if (counter_j == constfeq) counter_j = 0;
        }
        return wave;
    }
}

