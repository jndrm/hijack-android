/********************************************
 * MessageOut.java ��һ����android�������ݵ�Ŀ����һ���࣬����һ��Byte��
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
 * ���캯������ʼ��Ӳ��������audiotrack���󣬽���������Encoder���� 
 */
	public AudioTrackTx()
	{   
	
		 minAudioTrackTxBufSize=AudioTrack.getMinBufferSize(sampleRate, audioTrackTxChannel, audioTrackTxFormat);
		 encoderTx=new EncoderTx();
	 }
 /********************************
  * get_state ��ȡaudiotrack�����״̬
  * @return
  */
 public int get_state()
 {
	 return audioTrack.getPlayState();
 }
 /***********************************
  * msgIsSending ����Ƿ�����Ϣ���ڷ���
  * @return
  */
 public boolean msgIsSending(){
	return issending;	 
  }
 /*************************************
  * msg_byte ����һ��byte���͵����ݵ�Ŀ���
  * @param ��Ҫ���͵����� msg
  * @return ���ط����Ƿ�ɹ��ı�־
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
             audioTrackTxMode);//��STATICģʽ��ʱ��������������ģʽ
	 if(audioTrack.getState()!=AudioTrack.STATE_UNINITIALIZED)
	 {
	 msg_PCM=new short[audioTrackTxBufSize];
	 msg_PCM=encoderTx.updateAudioTxBuf((byte)msg);
	
	 issending=true;
			 sendedsize=audioTrack.write(msg_PCM, 0, msg_PCM.length);
			 audioTrack.flush();
			 audioTrack.setStereoVolume(1.0f, 0f);//����������������
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
  * msg_string  ����һ��string���͵����ݵ�Ŀ���
  * @param ��Ҫ���͵�����str
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
  * msgStop �ͷ�Ӳ����Դ
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
