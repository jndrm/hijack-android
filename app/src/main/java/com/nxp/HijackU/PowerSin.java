/************************************************
 * PowerSin 正弦波产生类，通过正弦波向目标板供电
 */

package com.nxp.HijackU;


import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;



public class PowerSin {
	public static final int Sample_Rate=44100;//采样频率
	public static final float MAXVOLUME=100f;
	public static final int LEFT=1;
	public static final int RIGHT=2;
	public static final int DOUBLE=3;
	
	AudioTrack audioTrackLight;
	/** 音量**/
	float volume;
	/** 声道**/
	int channel=RIGHT;
	/** 总长度**/
	int length;
	/** 一个正弦波的长度**/
	int waveLen;
	/** 频率**/
	int Hz;
	/** 正弦波**/
//	byte[] wave;
	short[] wave;

//	public PowerSin(){
//	
//	}

	/**
	 * 设置频率，初始化硬件  建立audioTrackLight对象
	 * @param rate
	 */
	public void start(int rate){
		stop();
		if(rate>0){
			Hz=rate;
			waveLen = Sample_Rate / Hz;                         
			int minbufsize=AudioTrack.getMinBufferSize(Sample_Rate, 
					                        AudioFormat.CHANNEL_OUT_MONO, //单声道
					                         AudioFormat.ENCODING_PCM_16BIT);//16位PCM
			length =(minbufsize/waveLen)*waveLen;//wave是一个正弦波的长度，minbufsize是系统规定的buffer大小，整数除法，确保length是wavelen的整数倍，不会在衔接处出现杂波,确保lenth>minbuffersize
//			wave=new byte[length];
			wave=new short[length];
			
			audioTrackLight=new AudioTrack(AudioManager.STREAM_MUSIC, Sample_Rate,
					AudioFormat.CHANNEL_OUT_MONO,
					AudioFormat.ENCODING_PCM_16BIT, length*2, AudioTrack.MODE_STATIC);//数据较大不能用static,可以用setLoopPoints循环播放
			//生成正弦波
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
	 * 写入数据，声道得到正弦波
	 */
	public void play(){
		if(audioTrackLight!=null){
			audioTrackLight.write(wave, 0, length);
		}
	}

	/**
	 * 停止播放
	 */
	public void stop(){
		if(audioTrackLight!=null){
			audioTrackLight.stop();
			audioTrackLight.release();
			audioTrackLight=null;
		}
	}

	/**
	 * 设置音量
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
	 * 设置声道
	 * @param channel
	 */
	public void setChannel(int channel){
		this.channel=channel;
		setVolume(volume);
	}

}
