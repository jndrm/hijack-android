/*******************************
 * SoundRecord.java Â¼Òô³ÌÐò Í¨¹ýÂ¼Òô°ÑÄ¿±ê°å·¢ËÍÉÏÀ´µÄ·½²¨Êý¾Ý×ª»»ÎªPCMÊý¾Ý£¬¹©½âÂë³ÌÐòÓÃ
 */

package com.nxp.HijackU;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.AudioRecord.OnRecordPositionUpdateListener;
import android.media.MediaRecorder;
import android.util.Log;
public class AudioRecordRx {
	private static final int sampleRate=44100;
	private static final int audioRecordRxChannel = AudioFormat.CHANNEL_IN_MONO;
	private static final int audioRecordRxFormat = AudioFormat.ENCODING_PCM_16BIT;
	private static final int audioSource = MediaRecorder.AudioSource.MIC;
	public static boolean audioReachedFlag = false;
	public static boolean audioRecordFlag = false;
	public static int audioRecordReadFlag;
	AudioRecordRxThread audioRecordRxThread = null;
	DecoderRx decoderRx = new DecoderRx();
	public static AudioRecord audioRecord;
	public static int minAudioRecordRxBufSize = AudioRecord.getMinBufferSize(sampleRate, audioRecordRxChannel,audioRecordRxFormat);
	public static int audioRecordRxBufSize = minAudioRecordRxBufSize * 4;
	short[] audioRecordRxBuf=new short[minAudioRecordRxBufSize];
	
	private OnRecordPositionUpdateListener audioRecordListener=new OnRecordPositionUpdateListener(){//»º³åÇøÒç³öÖÐ¶Ïº¯Êý
		@Override
		public void onMarkerReached(AudioRecord recorder) 
		{
			// TODO Auto-generated method stub
			Log.d(null, "AudioMarkerReached");
		}
		@Override
		public void onPeriodicNotification(AudioRecord recorder) 
		{
			// TODO Auto-generated method stub
			audioReachedFlag=true;
		    Log.d(null, "AudioRecordReached");
		    //»º³åÇøÒç³öÖÐ¶Ïµ½À´£¬±ÈÈçread»º³åÇøÊý¾Ý²Å¿ÉÒÔ½ÓÐøÂ¼Òô£¬·ñÔò»á×èÈû
		     //ÔÚÕâÀï²¢²»readÊý¾Ý£¬·ñÔò»áÕ¼ÓÃÖ÷Ïß³ÌµÄ×ÊÔ´£¬Ôì³Éactivity·´Ó¦ºÜÂý£¬readº¯ÊýÔÚDecoder.javaÄÇÀï
		    //  System.out.println("record interrupt " + System.currentTimeMillis());
		}
		
	};
	
	/********************************
	 * ÉèÖÃºÃÖÐ¶ÏÊÂ¼þ£¬³õÊ¼»¯Ó²¼þ½¨Á¢AudioRecordRx¶ÔÏó
	 */
	    public void start(){
	    	audioRecordFlag=false;
	    	if(audioRecordRxThread ==null){  
	    		audioRecordFlag=true;
		    	audioRecordRxThread = new AudioRecordRxThread(audioRecord,minAudioRecordRxBufSize);//minRecaudioRecordRxBufSize
		    	audioRecordRxThread.start();
	    	}
	    }
	/******************************
	 * Í£Ö¹Â¼Òô£¬ÊÍ·Å×ÊÔ´
	 */
	    public void stop(){
	    	audioRecordFlag=false;
	    	try {
	    		AudioRecordRxThread.sleep(20);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    	audioRecordRxThread.interrupt();
	    	try {
	    		audioRecordRxThread.join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 	
	    	audioRecordRxThread =null;    	
	    	if(audioRecord!=null)
	    		{
		    		audioRecord.release();
		    		audioRecord=null;
	    		}
	    }
	 /********************************
	  * Â¼ÒôÏß³Ì ²»¶Ï¶ÁÈ¡Â¼ÒôPCMÊý¾Ý½øÐÐ½âÂë
	  * @author Administrator
	  *
	  */
	    public class AudioRecordRxThread extends Thread{
	    	int bufSize;
	    	String bytetostring;
	    	public AudioRecordRxThread(AudioRecord audioRecord,int bufferSize){
	    		this.bufSize=bufferSize;
	    	}
	    	public void run(){
	    		audioRecord=new AudioRecord(audioSource,sampleRate, audioRecordRxChannel,audioRecordRxFormat,audioRecordRxBufSize);
		    	audioRecord.setPositionNotificationPeriod(minAudioRecordRxBufSize); //ÕâÊÇ¼àÌýÆ÷£¬µ±»º³åÇøÎªminRecBufSizeÒç³öÊ±ÖÐ¶Ï 		
		    	audioRecord.setRecordPositionUpdateListener(audioRecordListener);//ÖÐ¶Ï·þÎñº¯Êý
		    	audioRecord.startRecording();
		    	audioRecordReadFlag = audioRecord.read(audioRecordRxBuf, 0,minAudioRecordRxBufSize);
		    	decoderRx.decoderAudioRxbuf();
	    	}
	    }
	

/*******************************
 * ´òÓ¡Â¼ÒôPCMÊý¾Ý£¬ÓÃÓÚ³ÌÐòµÄµ÷ÊÔ
 * @param tmpBuf
 * @param ret
 */
   public void println_PCM(short[] tmpBuf,int ret )
    {
    	for(int i=0;i<ret;i++)
		{
			System.out.println("PCM short " + i + "is" + tmpBuf[i]);
		}
    }

}
