/********************************************
 * MessageOut.java ÊÇÒ»¸öÓÉandroid·¢ËÍÊý¾Ýµ½Ä¿±ê°åµÄÒ»¸öÀà£¬·¢ËÍÒ»¸öByte¡£
 */


package com.nxp.HijackU;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;

public class AudioTrackTx {
	private static final int sampleRate=44100;
	private static final int audioTrackTxStreamType = AudioManager.STREAM_MUSIC;
	private static final int audioTrackTxChannel = AudioFormat.CHANNEL_OUT_MONO;
	private static final int audioTrackTxFormat = AudioFormat.ENCODING_PCM_16BIT;
	private static final int audioTrackTxMode = AudioTrack.MODE_STATIC;
	public static AudioTrack audioTrack;
	public int minAudioTrackTxBufSize;
	public int audioTrackTxBufSize;
	public boolean issending=false;
	public boolean sendresult=false;
	public EncoderTx encoderTx;
/***********************************
 * ¹¹Ôìº¯Êý£¬³õÊ¼»¯Ó²¼þ£¬½¨Á¢audiotrack¶ÔÏó£¬½¨Á¢½âÂëÀàEncoder¶ÔÏó
 */
	public AudioTrackTx()
	{

		 minAudioTrackTxBufSize=AudioTrack.getMinBufferSize(sampleRate, audioTrackTxChannel, audioTrackTxFormat);
		 encoderTx=new EncoderTx();
	 }
 /********************************
  * get_state »ñÈ¡audiotrack¶ÔÏóµÄ×´Ì¬
  * @return
  */
 public int get_state()
 {
	 return audioTrack.getPlayState();
 }
 /***********************************
  * msgIsSending ¼ì²éÊÇ·ñÓÐÐÅÏ¢ÕýÔÚ·¢ËÍ
  * @return
  */
 public boolean msgIsSending(){
	return issending;
  }
 /*************************************
  * msg_byte ·¢ËÍÒ»¸öbyteÀàÐÍµÄÊý¾Ýµ½Ä¿±ê°å
  * @param ËùÒª·¢ËÍµÄÊý¾Ý msg
  * @return ·µ»Ø·¢ËÍÊÇ·ñ³É¹¦µÄ±êÖ¾
  */
 public boolean msg_byte(byte msg){
	 int sendedsize=0;
	 short[] msg_PCM;
	 if(issending){
		   msgStop();
	 }

	 int audioTrackTxsize;
	 if(audioTrack!=null)
		 {
		 audioTrack.release();
		 audioTrack=null;
		 }
	 audioTrackTxBufSize=encoderTx.getaudioTxBufsize();
	 if(audioTrackTxBufSize > minAudioTrackTxBufSize)
	 {
		 audioTrackTxsize=audioTrackTxBufSize;
	 }
	 else
	 {
		 audioTrackTxsize = minAudioTrackTxBufSize;
	 }
	 audioTrack=new AudioTrack(audioTrackTxStreamType,//use music channel
			 sampleRate,
			 audioTrackTxChannel,
			 audioTrackTxFormat,
			 audioTrackTxsize*2,
             audioTrackTxMode);//ÓÃSTATICÄ£Ê½ÑÓÊ±Âý£¬±ØÐëÓÃÕâÖÖÄ£Ê½
	 if(audioTrack.getState()!=AudioTrack.STATE_UNINITIALIZED)
	 {
	 msg_PCM=new short[audioTrackTxBufSize];
	 msg_PCM=encoderTx.updateAudioTxBuf((byte)msg);

	 issending=true;
			 sendedsize=audioTrack.write(msg_PCM, 0, msg_PCM.length);
			 audioTrack.flush();
			 audioTrack.setStereoVolume(1.0f, 0f);//ÉèÖÃ×óÓÒÉùµÀÒôÁ¿
			 audioTrack.play();
	 if(sendedsize==audioTrackTxBufSize)
	 {
		 sendresult=true;

//		 System.out.println("success send write PCM_Byte: "+ sendedsize);
	 }
	 else
	 {
		 sendresult=false;

//		 System.out.println("fail send write PCM_Byte: "+ sendedsize);

	 }
	 }
	 else
	 {
//		 System.out.println("audio initial fail");
		 audioTrack.release();
		 audioTrack=null;
	 }
	 issending=false;

	 return sendresult;

 }
 /**********************************
  * msg_string  ·¢ËÍÒ»¸östringÀàÐÍµÄÊý¾Ýµ½Ä¿±ê°å
  * @param ËùÒª·¢ËÍµÄÊý¾Ýstr
  * @return
  */
 public boolean msg_string(String str)
 {
	 boolean sendresult=false;
	 for(int i=0;i<str.length();i++)
	 {
		 if(!msg_byte((byte) str.charAt(i)))
		 {
			 sendresult=false;
			 return sendresult;
		 }
	 }
	 sendresult=true;
	return sendresult;

 }
 /*********************************
  * msgStop ÊÍ·ÅÓ²¼þ×ÊÔ´
  */
 public void msgStop(){
	   if(audioTrack!=null)
	   {
		   audioTrack.release();
		   audioTrack=null;
	   }
	   issending=false;
 }
}
