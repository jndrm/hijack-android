///////////////////////SinWave.java 
/////////////////�������ڹ�������Ҳ�,
//��ʽ=1.01-sin(wt)ÿ��android�ֻ����ݸ�ʽ��һ��������M9:����ֵΪ1������Ҳ����壬-1������Ҳ����ȡ�����-128��127Ϊ���Ҳ�0�㣬�����볣���෴�������޷�����ֻ�Ӳ����ԭ���޷��龿
package com.nxp.HijackU;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.widget.Toast;

public class HeadsetDetect extends BroadcastReceiver {  
		  
	    public static boolean hdflag=false;
	    @Override  
	    public void onReceive(Context context, Intent intent) {  
	          if (intent.hasExtra("state")){  
	               if (intent.getIntExtra("state", 0) == 0){
	            	   hdflag=false;
	            	   msg_IC_num(hdflag);
	                   Toast.makeText(context, "headset not connected", Toast.LENGTH_SHORT).show();  
	               }  
	               else if (intent.getIntExtra("state", 0) == 1){ 
	            	   hdflag=true;  
	            	   msg_IC_num(hdflag);
	            	   Toast.makeText(context, "headset connected", Toast.LENGTH_SHORT).show();  
	               }  
	          }  
	          
	 } 
	    /***************************************************
	     * msg_IC_num ���ڷ�����Ϣ�ĺ�������string ���͵���Ϣ���͵�activity������ʾ
	     * @param str
	     */
	        public void msg_IC_num(boolean str){//����ϵͳ��Ϣ
	        	Message msg=new Message();
	        	Bundle b=new Bundle();
	        	b.putBoolean("IC_num", hdflag);
	        	msg.setData(b);
	        	HijackU.myHDHandler.sendMessage(msg);
	        }
	  
}  

