///////////////////////SinWave.java 
/////////////////�������ڹ�������Ҳ�,
//��ʽ=1.01-sin(wt)ÿ��android�ֻ����ݸ�ʽ��һ��������M9:����ֵΪ1������Ҳ����壬-1������Ҳ����ȡ�����-128��127Ϊ���Ҳ�0�㣬�����볣���෴�������޷�����ֻ�Ӳ����ԭ���޷��龿
package com.nxp.HijackU;

public class SinWave {
	/** ���Ҳ��ĸ߶�**/
	public static final int HEIGHT = 32767;	//16bit
	/** 2PI**/
	public static final double TWOPI = 2 * 3.1415;
	public static boolean powersinflag = false;
	public static final short constfeq= 8;
	public static short[] powersin= new short[constfeq];
	public static short counter_j= 0;
	
	/**
	 * �������Ҳ�  PCM����
	 * @param wave
	 * @param waveLen ÿ�����Ҳ��ĳ���
	 * @param length �ܳ���
	 * @return ���ض�Ӧ���Ҳ������PCM����
	 */
//	public static byte[] sin(byte[] wave, int waveLen, int length) {//���ڻ�i++����length ���ٱ�wavelen��1
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
	public static short[] sin(short[] wave, int length) {//���ڻ�i++����length ���ٱ�wavelen��1
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

