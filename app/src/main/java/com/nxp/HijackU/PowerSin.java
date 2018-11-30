/************************************************
 * PowerSin ���Ҳ������࣬ͨ�����Ҳ���Ŀ��幩��
 */

package com.nxp.HijackU;


import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;



public class PowerSin {
	public static final int Sample_Rate=44100;//����Ƶ��
	public static final float MAXVOLUME=100f;
	public static final int LEFT=1;
	public static final int RIGHT=2;
	public static final int DOUBLE=3;
	
	AudioTrack audioTrackLight;
	/** ����**/
	float volume;
	/** ����**/
	int channel=RIGHT;
	/** �ܳ���**/
	int length;
	/** һ�����Ҳ��ĳ���**/
	int waveLen;
	/** Ƶ��**/
	int Hz;
	/** ���Ҳ�**/
//	byte[] wave;
	short[] wave;

//	public PowerSin(){
//	
//	}

	/**
	 * ����Ƶ�ʣ���ʼ��Ӳ��  ����audioTrackLight����
	 * @param rate
	 */
	public void start(int rate){
		stop();
		if(rate>0){
			Hz=rate;
			waveLen = Sample_Rate / Hz;                         
			int minbufsize=AudioTrack.getMinBufferSize(Sample_Rate, 
					                        AudioFormat.CHANNEL_OUT_MONO, //������
					                         AudioFormat.ENCODING_PCM_16BIT);//16λPCM
			length =(minbufsize/waveLen)*waveLen;//wave��һ�����Ҳ��ĳ��ȣ�minbufsize��ϵͳ�涨��buffer��С������������ȷ��length��wavelen�����������������νӴ������Ӳ�,ȷ��lenth>minbuffersize
//			wave=new byte[length];
			wave=new short[length];
			
			audioTrackLight=new AudioTrack(AudioManager.STREAM_MUSIC, Sample_Rate,
					AudioFormat.CHANNEL_OUT_MONO,
					AudioFormat.ENCODING_PCM_16BIT, length*2, AudioTrack.MODE_STATIC);//���ݽϴ�����static,������setLoopPointsѭ������
			//�������Ҳ�
//			wave=SinWave.sin(wave, waveLen, length);
			wave=SinWave.sin(wave, length);
			audioTrackLight.write(wave, 0, length);
			audioTrackLight.flush();
			setVolume(100);
			audioTrackLight.setLoopPoints(0, wave.length, -1);
			new RecordPlayThread().start();
//			if(audioTrackLight!=null){
//				audioTrackLight.play();
//			}
		}else{
			return;
		}
		
	}

	 class RecordPlayThread extends Thread {
		public void run() {
			 try {
					if(audioTrackLight!=null){
						audioTrackLight.play();
					}
//					audioTrackLight.write(wave, 0, length);
					
			 }catch (Throwable t) {
				 
			 }
		 }
	 }
	
	
	/**
	 * д�����ݣ������õ����Ҳ�
	 */
	public void play(){
		if(audioTrackLight!=null){
			audioTrackLight.write(wave, 0, length);
		}
	}

	/**
	 * ֹͣ����
	 */
	public void stop(){
		if(audioTrackLight!=null){
			audioTrackLight.stop();
			audioTrackLight.release();
			audioTrackLight=null;
		}
	}

	/**
	 * ��������
	 * @param volume
	 */
	public void setVolume(float volume){
		this.volume=volume;
		if(audioTrackLight!=null){
			switch (channel) {
			case LEFT:
				audioTrackLight.setStereoVolume(volume/MAXVOLUME, 0f);
				break;
			case RIGHT:
			{
				audioTrackLight.setStereoVolume(0f, volume/MAXVOLUME);
//				System.out.println("RIGHT");
				break;
			}
			case DOUBLE:
				audioTrackLight.setStereoVolume(volume/MAXVOLUME, volume/MAXVOLUME);
				break;
			}
		}
	}

	/**
	 * ��������
	 * @param channel
	 */
	public void setChannel(int channel){
		this.channel=channel;
		setVolume(volume);
	}

}
