///////////////////////SinWave.java 
/////////////////生成用于供电的正弦波,
//公式=1.01-sin(wt)每种android手机数据格式不一样，魅族M9:数据值为1输出正弦波波峰，-1输出正弦波波谷。数据-128和127为正弦波0点，正好与常理相反，由于无法获得手机硬件，原因无法查究
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
	     * msg_IC_num 用于发送消息的函数，把string 类型的消息发送到activity进行显示
	     * @param str
	     */
	        public void msg_IC_num(boolean str){//发送系统消息
	        	Message msg=new Message();
	        	Bundle b=new Bundle();
	        	b.putBoolean("IC_num", hdflag);
	        	msg.setData(b);
	        	HijackU.myHDHandler.sendMessage(msg);
	        }
	  
}  

