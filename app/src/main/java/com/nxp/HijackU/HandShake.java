/*******************************
 * SoundRecord.java ¼������ ͨ��¼����Ŀ��巢�������ķ�������ת��ΪPCM���ݣ������������
 */

package com.nxp.HijackU;
import android.os.Bundle;
import android.os.Message;
public class HandShake {
	
	public static boolean hsFlag=false;
	public boolean hsexit=false;
	HandShakeThread hsThread=null;	
	AudioTrackTx msgohs =new AudioTrackTx();//���ǻ�����Խ��ʱ����Խ�ã��������߼�Ȩ��
	
/********************************
 * ���ú��ж��¼�����ʼ��Ӳ������handshake����
 */
    public void start(){
    	hsexit=false;
    	if(hsThread ==null){  
    		HijackU.sensordataDebug =6;
//    		audioRecord=new AudioRecord(audioSource,recSampleRate,recChannel,recAudioFormat,minRecBufSize*4);
//	    	audioRecord.setPositionNotificationPeriod(minRecBufSize); //���Ǽ���������������ΪminRecBufSize���ʱ�ж� 		
//	    	audioRecord.setRecordPositionUpdateListener(mreclistener);//�жϷ�����
    		hsFlag=true;
	    	hsThread = new HandShakeThread();//minRecBufSize
	    	hsThread.start();
	    	HijackU.sensordataDebug = 23;
    	}
    }
/******************************
 * ֹͣ¼�����ͷ���Դ
 */
    @SuppressWarnings("deprecation")
	public void stop(){
    	hsexit=true;
    	hsFlag=false;
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
    	try{
    	hsThread.stop();
    	}catch(SecurityException e)
    	{
    		
    	}    	
    	hsThread =null;    	

    }
 /********************************
  * ¼���߳� ���϶�ȡ¼��PCM���ݽ��н���
  * @author Administrator
  *
  */
    public class HandShakeThread extends Thread{
    	public HandShakeThread(){
    	}
    	@Override
    	public void run(){
    		while(HijackU.hsenableflag)
    		{
    	    			if(HijackU.funcmode!=255){
    		    			if((HijackU.handshakeOK==false) && (HijackU.handshakeC <10)){
    		    				switch (HijackU.funcmode) {
    		    				case 0:
    		    					msgohs.msg_byte((byte)129);	//mzh:0x81
    		    					HijackU.handshakeC++;
    		    					break;
    		    				case 1:
    		    					msgohs.msg_byte((byte)130);	//mzh:0x82
    		    					HijackU.handshakeC++;
    		    					break;
    		    				case 2:
    		    					msgohs.msg_byte((byte)131);	//mzh:0x83
    		    					HijackU.handshakeC++;
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
    		    			}
    		    			else if((HijackU.handshakeOK==false) && (HijackU.handshakeC >9)){
    		    				HijackU.handshakeC=0;
    		    				HijackU.funcmode=255;
    		    				HijackU.handshakeflag = false;
    		    				HijackU.handshakefailedflag = true;
    		    				HijackU.hsenableflag = false;
    		    				String str=" ";
    		    				msg_IC_num(str);
    		    			}
    	    			}
    	    		}
    			}
    	    }
    public void msg_IC_num(String str)
    {
    	Message msg=new Message();
    	Bundle b=new Bundle();
    	b.putString("IC_num", str);
    	msg.setData(b);
    	HijackU.myHsHandler.sendMessage(msg);
    }
}
