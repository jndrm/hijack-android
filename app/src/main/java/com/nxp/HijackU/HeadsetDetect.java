///////////////////////SinWave.java
/////////////////Éú³ÉÓÃÓÚ¹©µçµÄÕýÏÒ²¨,
//¹«Ê½=1.01-sin(wt)Ã¿ÖÖandroidÊÖ»úÊý¾Ý¸ñÊ½²»Ò»Ñù£¬÷È×åM9:Êý¾ÝÖµÎª1Êä³öÕýÏÒ²¨²¨·å£¬-1Êä³öÕýÏÒ²¨²¨¹È¡£Êý¾Ý-128ºÍ127ÎªÕýÏÒ²¨0µã£¬ÕýºÃÓë³£ÀíÏà·´£¬ÓÉÓÚÎÞ·¨»ñµÃÊÖ»úÓ²¼þ£¬Ô­ÒòÎÞ·¨²é¾¿
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
	     * msg_IC_num ÓÃÓÚ·¢ËÍÏûÏ¢µÄº¯Êý£¬°Ñstring ÀàÐÍµÄÏûÏ¢·¢ËÍµ½activity½øÐÐÏÔÊ¾
	     * @param str
	     */
	        public void msg_IC_num(boolean str){//·¢ËÍÏµÍ³ÏûÏ¢
	        	Message msg=new Message();
	        	Bundle b=new Bundle();
	        	b.putBoolean("IC_num", hdflag);
	        	msg.setData(b);
	        	HijackU.myHDHandler.sendMessage(msg);
	        }

}

