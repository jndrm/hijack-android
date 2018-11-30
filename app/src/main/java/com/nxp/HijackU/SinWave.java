///////////////////////SinWave.java
/////////////////Éú³ÉÓÃÓÚ¹©µçµÄÕýÏÒ²¨,
//¹«Ê½=1.01-sin(wt)Ã¿ÖÖandroidÊÖ»úÊý¾Ý¸ñÊ½²»Ò»Ñù£¬÷È×åM9:Êý¾ÝÖµÎª1Êä³öÕýÏÒ²¨²¨·å£¬-1Êä³öÕýÏÒ²¨²¨¹È¡£Êý¾Ý-128ºÍ127ÎªÕýÏÒ²¨0µã£¬ÕýºÃÓë³£ÀíÏà·´£¬ÓÉÓÚÎÞ·¨»ñµÃÊÖ»úÓ²¼þ£¬Ô­ÒòÎÞ·¨²é¾¿
package com.nxp.HijackU;

public class SinWave {
	/** ÕýÏÒ²¨µÄ¸ß¶È**/
	public static final int HEIGHT = 32767;	//16bit
	/** 2PI**/
	public static final double TWOPI = 2 * 3.1415;
	public static boolean powersinflag = false;
	public static final short constfeq= 8;
	public static short[] powersin= new short[constfeq];
	public static short counter_j= 0;

	/**
	 * Éú³ÉÕýÏÒ²¨  PCMÊý¾Ý
	 * @param wave
	 * @param waveLen Ã¿¶ÎÕýÏÒ²¨µÄ³¤¶È
	 * @param length ×Ü³¤¶È
	 * @return ·µ»Ø¶ÔÓ¦ÕýÏÒ²¨ËùÐèµÄPCMÊý¾Ý
	 */
//	public static byte[] sin(byte[] wave, int waveLen, int length) {//ÓÉÓÚ»ái++ËùÒÔlength ÖÁÉÙ±Èwavelen´ó1
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
	public static short[] sin(short[] wave, int length) {//ÓÉÓÚ»ái++ËùÒÔlength ÖÁÉÙ±Èwavelen´ó1
		if(powersinflag==false )
		{
			for (int i = 0; i < constfeq; i++){
				powersin[i]=(short) (HEIGHT * (Math.sin(Math.PI * i /constfeq * 2)));
			}
			powersinflag=true;
			counter_j =0;
		}
		for (int i = 0; i < length; i++) {
			wave[i]=powersin[counter_j];
			counter_j++;
			if(counter_j==constfeq) counter_j=0;
		}
		return wave;
	}
}

