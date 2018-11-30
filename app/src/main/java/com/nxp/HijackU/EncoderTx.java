/**********************************
 * Encoder ����һ�������࣬���ڰ�Ҫ���͵����ݽ��б����Ϊ16λ��PCM���ݸ�ʽ
 * ��ʽ=1.01-sin(wt)ÿ��android�ֻ����ݸ�ʽ��һ��������M9:����ֵΪ1������Ҳ����壬-1������Ҳ����ȡ�����-128��127Ϊ���Ҳ�0�㣬�����볣���෴�������޷�����ֻ�Ӳ����ԭ���޷��龿
 */

package com.nxp.HijackU;

public class EncoderTx {
	public static int sampleRate=44100;
	public static float sampleBaud=1378.125f;
	public static int sampleBit=32;
	public static int bitTxLength=17; //frame header: 3bits(1) + start bit: 1bit(0) + Data: 8bits + parity bit: 1bit + stop bit: 1bit(1) + frame tail: 3bits(1)
	public static int dataLength=0;
	public static double W_PI2=2*3.1415f;
	public static double Hifreq=1378.125f; 
	public static double Lofreq=Hifreq/2.0f;
	public int audioAM = 32767;
	public int audioTxBufLength = 0; 
	public static short[] highLevel = new short[ sampleBit ];
	public static short[] lowLevel  = new short[ sampleBit ];
	private int  counter_i = 0;
	private int  counter_j = 0;
	private int  counter_k = 0;
	
	public int getaudioTxBufsize()
	{
		audioTxBufLength = bitTxLength * sampleBit;//6 ones����ͷ3��ons,��β3��ones�� ,8bit����,0start ,1stop,1parity
		return audioTxBufLength;
	}
	
/***********************************************
 initiate transmit 1 and 0 basic data 
 */
	public void initEncoderTxData()
	{
		for(counter_i=0;counter_i<sampleBit;counter_i++)
		{
			highLevel[counter_i] = (short) (audioAM * (-Math.sin(Math.PI * counter_i /sampleBit * 2)));
			lowLevel[counter_i]  = (short) (audioAM * (Math.sin(Math.PI * counter_i /sampleBit * 2)));
		}
	}
	
/***********************************************
 update audio transmit buffer basic data 
 */
	public short[] updateAudioTxBuf(byte audioTxData)
	{
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
		for(counter_i=4;counter_i<12;counter_i++)
		{
			if(((audioTxData>>(counter_i-4)) & 0x01) == 0x01)
				audioTxBit[counter_i] = 1;
			else
				audioTxBit[counter_i] = 0;
			paritybit += audioTxBit[counter_i];
		}
		/* parity bit */
		if((paritybit & 0x01) == 0x01)
		{
			audioTxBit[12] = 1;
		}
		else
			audioTxBit[12] = 0;
		/* stop bit */
		audioTxBit[13] = 1;
		/* frame tail */
		audioTxBit[14] = 1;
		audioTxBit[15] = 1;
		audioTxBit[16] = 1;
		/* update audio transmit buffer */
		for(counter_i=0;counter_i<bitTxLength;counter_i++)
		{
			if( (audioTxBit[counter_i] & 0x01) == 0x01)
			{
				for(counter_j=0;counter_j<sampleBit;counter_j++)
				{
					audioTxBuf[counter_k] = highLevel[counter_j];	//Samsung, Xiaomi(MIUI)
//					audioTxBuf[counter_k] = lowLevel[counter_j];	//else
					counter_k++;
				}
			}
			else
			{
				for(counter_j=0;counter_j<sampleBit;counter_j++)
				{
					audioTxBuf[counter_k] = lowLevel[counter_j];	//Samsung, Xiaomi(MIUI)
//					audioTxBuf[counter_k] = highLevel[counter_j];	//else
					counter_k++;
				}
			}
			
		}
		return audioTxBuf;
	}

}
