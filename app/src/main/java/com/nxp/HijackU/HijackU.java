/*************************************
 * This UI is simply designed for the NXP Hijacker Project
 * Author:Zhihao Mo
 * LOCATION :NXP ShenZhen
 */
package com.nxp.HijackU;

import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;
import com.nxp.HijackU.R;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.TabActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.IntentFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.Toast;
import android.content.Intent;

@SuppressWarnings("deprecation")
public class HijackU extends TabActivity {
	public static LinkedList<Activity> sAllActivitys = new LinkedList<Activity>();
	//define TabHost object
	private TabHost mTabHost;
    private int power_rate=22050;
    static PowerSin audio=null;
    boolean Powersel=false;
	public static ICHandler myICHandler;
	public static HsHandler myHsHandler;
	public static HDHandler myHDHandler;
    private int tag_id = 0;
    private int lastid = 0;
	int max ;
	static int current;
	static AudioManager mAudioManager ;
	public static int phoneRxError=0;

    private int AKeydata;
    private int lastAKeydata;
	private Timer AKeydatatimer;
	private TimerTask AKeydatatimertask;
	boolean timerflag1 = false;
    Button LED1_onbtn=null;
    Button LED2_onbtn=null;
    Button LED3_onbtn=null;
    Button KEY_onbtn=null;
    SeekBar LED4rate_Bar=null;
    AudioTrackTx msgo =new AudioTrackTx();
	public boolean state_KEY  = false; 	//KEY  off-0   ,  led  on-1
	private int RockerCircleX = 320;
	private int RockerCircleY = 150;
	private int RockerCircleR = 100;
	private float SmallRockerCircleX = 320;
	private float SmallRockerCircleY = 150;
	private float SmallRockerCircleR = 50;
	private SurfaceView surface_draw;
	private SurfaceHolder surfaceholder;
	boolean isrecord0=false;
	AudioRecordRx myrec0=null;
	boolean isrecord1=false;
	AudioRecordRx myrec1=null;
    boolean isrecord2=false;
    AudioRecordRx myrec2=null;
  //Reserved RFID
//    Button readcard_onbtn3=null;
//    public static boolean isrecord3=false;
//    AudioRecordRx myrec3=null;
//    EditText ICcard_text3=null;
//    boolean show3 = false ;

    HandShake hsrun=null;

	private SurfaceView surface_drawsensor;
	private SurfaceHolder surfaceholdersensor;

	private Button timerdraw_btn;
	private Button cleardraw_btn;
	private EditText sendatatext1;

	public static int sensordataDebug = 0;

	private Timer timer;
	private TimerTask timertask;

	private int[] Y; // save Y axis coordinate
	private int y;
	private int sensordata1;
    private int tempstr = 0 , count = 0;
    private int  countFF = 0 ;
    private boolean enoughFF = false ;

    private int oldT = 0 ;

    private int T = 0 , T0 = 0, T1 = 0  , BT0 = 0,BT1 = 0,BT2 = 0;
	private int centerY, oldX, oldY, currentX;// save y axis mid coordinate, save last (x,y), current x coordinate
	private volatile int signal=0;
    boolean timerflag = false ;
    public static int funcmode = 255 ;
    public static boolean handshakeflag = false ;
    public static boolean handshakefailedflag = false ;
    public static int handshakeD = 0 ;
    public static int handshakeC = 0 ;
    public static boolean handshakeOK = false ;
    public static boolean hstimerflag = false ;
    public static boolean hsenableflag = false ;
    Intent headsetintent = new Intent();
    public static boolean headsetflag = false ;
    private HeadsetDetect headsetdetect;
    /** Called when the activity is first created. */
//    @SuppressWarnings("deprecation")
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        sAllActivitys.add(this);
        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        //get TabHost object
        mTabHost=this.getTabHost();
        LayoutInflater inflater_tab1 = LayoutInflater.from(this);
        inflater_tab1.inflate(R.layout.tab1, mTabHost.getTabContentView());
        LayoutInflater inflater_tab2 = LayoutInflater.from(this);
        inflater_tab2.inflate(R.layout.tab2, mTabHost.getTabContentView());
//        LayoutInflater inflater_tab3 = LayoutInflater.from(this);
//        inflater_tab3.inflate(R.layout.tab3, mTabHost.getTabContentView());
        /* 通过addTab添加tab，参数为TabSpec对象实例，再通过TabSpec的方法设置该TabSpec对象的标签，图标和显示的内容*/
        mTabHost.addTab(mTabHost.newTabSpec("tab_1")//创建一个TabSpec对象，设置它的tag，该tag即为标签切换事件中的tabId
        	.setIndicator("LEDs",getResources().getDrawable(R.drawable.led))//设置TabWidget的文字和标签
        	.setContent(R.id.layout1));//设置该tab对应的内容
        mTabHost.addTab(mTabHost.newTabSpec("tab_2")
            	.setIndicator("Sensor", getResources().getDrawable(R.drawable.sensor))
            	.setContent(R.id.layout4));
//Reserved RFID
//        mTabHost.addTab(mTabHost.newTabSpec("tab_3")
//        	.setIndicator("RFID", getResources().getDrawable(R.drawable.rfid))
//        	.setContent(R.id.layout3));
        mTabHost.setBackgroundColor(0xFF434343); //设置TabHost的背景颜色
        mTabHost.setCurrentTab(0);//设置初始选中的tab

        LED1_onbtn=(Button)findViewById(R.id.button1);
        LED1_onbtn.setOnClickListener(new LED_onbtnlistener1());
        LED2_onbtn=(Button)findViewById(R.id.button2);
        LED2_onbtn.setOnClickListener(new LED_onbtnlistener2());
        LED3_onbtn=(Button)findViewById(R.id.button3);
        LED3_onbtn.setOnClickListener(new LED_onbtnlistener3());
        KEY_onbtn=(Button)findViewById(R.id.button4);
        KEY_onbtn.setOnClickListener(new KEY_onbtnlistener4());

		LED1_onbtn.setBackgroundColor(Color.WHITE);
		LED2_onbtn.setBackgroundColor(Color.WHITE);
		LED3_onbtn.setBackgroundColor(Color.WHITE);

		surface_draw = (SurfaceView) findViewById(R.id.surfaceView1);
		surface_draw.setZOrderOnTop(true);
		surface_draw.getHolder().setFormat(PixelFormat.TRANSLUCENT);
		surfaceholder = surface_draw.getHolder();

	    Display display = getWindowManager().getDefaultDisplay();
	    RockerCircleX = display.getWidth()/2;
	    RockerCircleY = surface_draw.getTop()+RockerCircleR+50;
	    SmallRockerCircleX = display.getWidth()/2;
	    SmallRockerCircleY = surface_draw.getTop()+RockerCircleR+50;

        LED4rate_Bar=(SeekBar)findViewById(R.id.LED4rate_Bar);
        LED4rate_Bar.setMax(100);
        LED4rate_Bar.setProgress(100);
        LED4rate_Bar.setOnSeekBarChangeListener(new LED4rate_Barlistener());

        myICHandler = new ICHandler();
        myHsHandler = new HsHandler();
        myHDHandler = new HDHandler();
      //Reserved RFID
//        readcard_onbtn3=(Button)findViewById(R.id.button31);
//        readcard_onbtn3.setOnClickListener(new readcard_onbtnlistener31());
//        ICcard_text3=(EditText)findViewById(R.id.editText31);

        surface_drawsensor = (SurfaceView) findViewById(R.id.surfaceView41);
        surface_drawsensor.setZOrderOnTop(true);
        surface_drawsensor.getHolder().setFormat(PixelFormat.TRANSLUCENT);

		surfaceholdersensor = surface_drawsensor.getHolder();

		timerdraw_btn = (Button) findViewById(R.id.button41);
		timerdraw_btn.setOnClickListener(new timerdraw_btnlistener());
		cleardraw_btn = (Button) findViewById(R.id.button42);
		cleardraw_btn.setOnClickListener(new cleardraw_btnlistener());
		cleardraw_btn.setEnabled(false);

		sendatatext1 = (EditText)findViewById(R.id.editText41);
		sendatatext1.setBackgroundColor(0xffFFB200);

		sendatatext1.setText("T");

		centerY = 450;
		Y = new int[getWindowManager().getDefaultDisplay().getWidth()];
		for (int i = 1; i < Y.length + 1; i++) {
			Y[i - 1] = centerY + (int) (100 * Math.sin(i * 2 * Math.PI / 180));
		}
		registerHeadsetPlugReceiver();
		/* set mobile phone media volume to max */
		volumeset();
		/* enable the mobile phone right channel as power supply */
		Powersel = true;	//set power supply flag
        audio=new PowerSin();	//create
//	    audio.start(power_rate);
		/* create the record */
	    myrec0=new AudioRecordRx();
		myrec0.start();
		funcmode = 255;
		/* send 0x99 to reset MCU */
		msgo.msg_byte((byte)153);	//0x99

		/* select function mode */
        mTabHost.setOnTabChangedListener(new OnTabChangeListener() {	//select mode start
			@Override
			public void onTabChanged(String tabId) {
				lastid = tag_id ;	//save last function mode id
				tag_id = mTabHost.getCurrentTab();	//get new function mode id
				volumeset();
				if(hsenableflag==false){
					/* handshake function */
					hsenableflag=true;
					hsrun=new HandShake();
					hsrun.start();
				}
				switch (tag_id) {
				case 0:
					funcmode = 0;
//					msgo.msg_byte((byte)129);	//0x81
					handshakeOK=false;
					Toast.makeText(HijackU.this, "Handshaking", Toast.LENGTH_SHORT)
					.show();
					break;
				case 1:
					funcmode = 1;
//					msgo.msg_byte((byte)130);	//0x82
					handshakeOK=false;
					Toast.makeText(HijackU.this, "Handshaking", Toast.LENGTH_SHORT)
					.show();
					break;
					//Reserved RFID
//				case 2:
//					funcmode = 2;
////					msgo.msg_byte((byte)131);	//0x83
//					handshakeOK=false;
//					Toast.makeText(HijackU.this, "Handshaking", Toast.LENGTH_SHORT)
//					.show();
//					break;
				default:
					funcmode = 255;
					break;
				}
				switch (lastid) {
				case 0:
					reset1();
					break;
				case 1:
					reset2();
					break;
					//Reserved RFID
//				case 2:
//					reset3();
//					break;
				default:
					break;
				}

			}
		});		//select mode end
//		while(headsetflag==false)
//		{
//
//		}
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_quickjack_all, menu);
        return true;
    }
    /* Received data handler: start */
    @SuppressLint("HandlerLeak")
	public class ICHandler extends Handler{
    	@SuppressLint("HandlerLeak")
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			Bundle b=msg.getData();
			String str=b.getString("IC_num");
			sensordataDebug = 3;
			if(funcmode!=255){
				handshakeD = hex2decimal(str);
				/* handshake verify: LPC should feedback 0xAA and 0x55 */
				if(handshakeD == 85 && handshakeflag == true){
					funcmode = 255;
					handshakeOK=true;
					handshakeflag = false;
					handshakeD = 0;
					handshakeC = 0;
					hsenableflag = false;
					Toast.makeText(HijackU.this, "Handshake OK", Toast.LENGTH_SHORT)
					.show();
				}
				else handshakeflag = false;

				if(handshakeflag ==false && handshakeD == 170){
					handshakeflag = true;
					handshakeD = 0;
				}

			}

			switch (tag_id) {
			case 0:
				if (isrecord1) {
					AKeydata =  hex2decimal(str);
				}
				break;
				//Reserved RFID
//			case 2:
//				if (isrecord3) {
//					ICcard_text3.append(str);
//				}else {
//
//				}
//				if(ICcard_text3.length()>300){
//					ICcard_text3.setText("");
//				}
//				break;

			case 1:
				sensordataDebug = 1 ;
				if (isrecord2) {
					tempstr = hex2decimal(str);
					if (enoughFF==false&&countFF>1){
						enoughFF = true ;
						countFF = 0 ;
					}
					if (enoughFF==false&&tempstr==255&&countFF<2) { //when data0(T0) come , countFF=2
						countFF++;
						break;
					} else if (enoughFF==false&&tempstr != 255) {
						countFF = 0 ;
						enoughFF = false ;
					}

					if (enoughFF) {
						switch (count%2) {
						case 0:
							T1 = tempstr ;
							break;
						case 1:
							T0 = tempstr ;
							break;
						default:
							break;
						}

						if ((T1!=0)||(T0>40)||(T0<0)) {
						     T = 0 ; T0 = 0; T1 = 0  ;
						     count =  0 ;
						     enoughFF = false ;
						     countFF = 0 ;
						     break ;
						}
						count++;
						if (count%2==0 ) {
							enoughFF = false ;
							count = 0 ;
							T = T1*256  + T0;

							BT2 = BT1 ; BT1 = BT0 ; BT0 = T;

								if ((Math.abs(BT1-BT0)>10) && (Math.abs(BT1-BT2)>10)) {
									BT1 = BT0;
								}
							sensordata1 = BT2*10  ;
						if (BT2!=oldT) {
							sendatatext1.setText("T:"+String.valueOf(BT2));
						}
						oldT = BT2;
						sensordataDebug = 2 ;
						}
					}
				}
				break;
			default:
				break;
			}
			super.handleMessage(msg);
		}
    }
   /* Received data handler: end*/


    /* Received data handler: start */
    @SuppressLint("HandlerLeak")
	public class HsHandler extends Handler{
    	@SuppressLint("HandlerLeak")
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
//    		Bundle c=msg.getData();
			if(HijackU.handshakefailedflag = true)
			{
				if(phoneRxError < 20)
				{
					Toast.makeText(HijackU.this, "Handshake failed or Phone received error", Toast.LENGTH_SHORT)
					.show();
				}
				else
				{
					Toast.makeText(HijackU.this, "Handshake failed or Headset connection issue", Toast.LENGTH_SHORT)
					.show();
				}
			}
			super.handleMessage(msg);
		}
    }

	public class HDHandler extends Handler{
    	@SuppressLint("HandlerLeak")
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
//    		Bundle c=msg.getData();
    		Bundle b=msg.getData();
			boolean str=b.getBoolean("IC_num");
			if(str == true)
			{
				audio.start(power_rate);
				volumeset();
			}
			else
			{
				audio.stop();
			}
			super.handleMessage(msg);
		}
    }

    public  void  volumeset() {
    	max = mAudioManager.getStreamMaxVolume( AudioManager.STREAM_MUSIC);
    	current = mAudioManager.getStreamVolume( AudioManager.STREAM_MUSIC );
    	Log.d("MUSIC", "max : " + max + " current : " + current);
    	mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, max, 0);
	}

    boolean onetime = true ;

    /* LED1 button click event: start */
    public class LED_onbtnlistener1 implements OnClickListener{

		public void onClick(View v) {
			// TODO Auto-generated method stub
			if (onetime) {
				onetime = false ;
				try {
					Thread.currentThread();
					Thread.sleep(1000);
					} catch (InterruptedException e) {
					e.printStackTrace();
					}
			}
				msgo.msg_byte((byte)113);//发送数据0x71到LPC800
				LED1_onbtn.setBackgroundColor(Color.YELLOW);
				LED2_onbtn.setBackgroundColor(Color.WHITE);
				LED3_onbtn.setBackgroundColor(Color.WHITE);
		}
    }
    /* LED1 button click event: end */

    /* LED2 button click event: start */
    public class LED_onbtnlistener2 implements OnClickListener{

		public void onClick(View v) {
			// TODO Auto-generated method stub
			if (onetime) {
				onetime = false ;
				try {
					Thread.currentThread();
					Thread.sleep(1000);
					} catch (InterruptedException e) {
					e.printStackTrace();
					}
			}
				msgo.msg_byte((byte)114);//发送数据0x72到LPC800
				LED2_onbtn.setBackgroundColor(Color.RED);
				LED1_onbtn.setBackgroundColor(Color.WHITE);
				LED3_onbtn.setBackgroundColor(Color.WHITE);
		}
    }
    /* LED2 button click event: end */
    /* LED3 button click event: start */
    public class LED_onbtnlistener3 implements OnClickListener{

		public void onClick(View v) {
			// TODO Auto-generated method stub
			if (onetime) {
				onetime = false ;
				try {
					Thread.currentThread();
					Thread.sleep(1000);
					} catch (InterruptedException e) {
					e.printStackTrace();
					}
			}
				msgo.msg_byte((byte)115);//发送数据0x73到LPC800
				LED3_onbtn.setBackgroundColor(Color.YELLOW);
				LED2_onbtn.setBackgroundColor(Color.WHITE);
				LED1_onbtn.setBackgroundColor(Color.WHITE);
		}
    }
    /* LED3 button click event: end */
    /* enable KEYs button click event: start */
    public class KEY_onbtnlistener4 implements OnClickListener{

		public void onClick(View v) {
			// TODO Auto-generated method stub
			if (state_KEY) {  // CLOSE
				ClearDraw();
				state_KEY = false ;
				KEY_onbtn.setText("Enable KEYs");
				if(isrecord1==true)
				{
						isrecord1=false;
				}
				AKeydatatimer.cancel();
				AKeydatatimertask.cancel();
				timerflag1 = false ;
				lastAKeydata = 9999;
			}
			else    //OPEN
			{
				draw(0);
				state_KEY = true ;
				KEY_onbtn.setText("Stop KEYs");
				if(isrecord1==false)
				{
					isrecord1=true;
				}

				AKeydatatimer=new Timer();
				AKeydatatimertask=new TimerTask() {
					@Override
					public void run() {
						// TODO Auto-generated method stub
						updateAKey();
					}
				};
				AKeydatatimer.schedule(AKeydatatimertask, 0, 100);// 动态绘制正弦图
				timerflag1 = true ;
			}

		}
    }
    /* enable KEYs button click event: end */
    /* sub function: update key display */
    public void updateAKey(){
    	if (lastAKeydata!=AKeydata) {
    		ClearDraw();
			draw(AKeydata);
    	}
    	lastAKeydata = AKeydata;
    }


    /* set LED4 flash frequency: start */
    public class LED4rate_Barlistener implements OnSeekBarChangeListener{

		public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {
			// TODO Auto-generated method stub
			msgo.msg_byte((byte)arg1);
		}

		public void onStartTrackingTouch(SeekBar arg0) {
			// TODO Auto-generated method stub

		}

		public void onStopTrackingTouch(SeekBar arg0) {
			// TODO Auto-generated method stub

		}
    }
    /* set LED4 flash frequency: end */
    /* sub function: draw key display */
	public void draw(int akey) { //Arrow KEY

			Canvas canvas = surfaceholder.lockCanvas();
			canvas.drawColor(0xff92dd22);
//			canvas.drawColor(Color.WHITE);

//			//绘制摇杆背景
			Paint mPaint = new Paint();
			mPaint.setColor(0x70000000);
			canvas.drawCircle(RockerCircleX, RockerCircleY, RockerCircleR, mPaint);

//			//绘制摇杆
			mPaint.setColor(0x70ff0000);
			switch (akey) {
			case 0x1://up
				canvas.drawCircle(SmallRockerCircleX, SmallRockerCircleY-SmallRockerCircleR, SmallRockerCircleR, mPaint);
				break;

			case 0x2://down
				canvas.drawCircle(SmallRockerCircleX, SmallRockerCircleY+SmallRockerCircleR, SmallRockerCircleR, mPaint);
				break;

			case 0x3://left
				canvas.drawCircle(SmallRockerCircleX-SmallRockerCircleR, SmallRockerCircleY, SmallRockerCircleR, mPaint);
				break;

			case 0x4://right
				canvas.drawCircle(SmallRockerCircleX+SmallRockerCircleR, SmallRockerCircleY, SmallRockerCircleR, mPaint);
				break;

			case 0x5://center
				mPaint.setColor(0x7000ffff);//center
				canvas.drawCircle(SmallRockerCircleX, SmallRockerCircleY, SmallRockerCircleR, mPaint);
				break;
			default:
				Log.i("errorkey","no match");
				break;
			}

			surfaceholder.unlockCanvasAndPost(canvas);
	}
	/* sub function: clear key display */
	public void ClearDraw() {
		if (state_KEY) {
		Canvas canvas = surfaceholder.lockCanvas(null);
		Paint paint = new Paint();
		paint.setXfermode(new PorterDuffXfermode(Mode.CLEAR));
		canvas.drawPaint(paint);
		paint.setXfermode(new PorterDuffXfermode(Mode.SRC));
		surfaceholder.unlockCanvasAndPost(canvas);
		}
	}

	public void reset1(){
		KEY_onbtn.setText("Enable KEYs");
		ClearDraw();
		state_KEY = false ;
		LED2_onbtn.setBackgroundColor(Color.WHITE);
		LED1_onbtn.setBackgroundColor(Color.WHITE);
		LED3_onbtn.setBackgroundColor(Color.WHITE);

		if(isrecord1){
		}
		isrecord1=false;
		if (timerflag1) {
			AKeydatatimer.cancel();
			AKeydatatimertask.cancel();
			timerflag1 = false ;
			lastAKeydata = 9999;
		}
	}

	//Reserved RFID
//	public class readcard_onbtnlistener31 implements OnClickListener{
//		public void onClick(View v) {
//			// TODO Auto-generated method stub
//				if(isrecord3==false)
//				{
//					isrecord3=true;
//					msgo.msg_byte((byte)239);	//0xEF
//					readcard_onbtn3.setText(R.string.BT2);
//				}
//				else
//				{
//						isrecord3=false;
//						readcard_onbtn3.setText(R.string.BT1);
//				}
//		}
//    }
	//Reserved RFID
//	public void reset3() {
//		ICcard_text3.setText("");
//		if(isrecord3){
//		}
//			isrecord3=false;
//			readcard_onbtn3.setText(R.string.BT1);
//	}

	public void reset2() {
	     T = 0 ; T0 = 0; T1 = 0  ;
			oldT = 0;
		    BT0 = 0 ; BT1 = 0 ; BT2 = 0;
		sendatatext1.setText("T");
		if (timerflag) {
			timertask.cancel();
			timer.cancel();
			ClearDraw4();
		}
		timerdraw_btn.setEnabled(true);
		timerdraw_btn.setText(R.string.BT41);
		cleardraw_btn.setEnabled(false);
		if(isrecord2){

		}
			isrecord2=false;
	}

    public class timerdraw_btnlistener implements OnClickListener{

		public void onClick(View v) {
			// TODO Auto-generated method stub
			if(isrecord2==false)
			{
				isrecord2=true;
			}
			oldX=0;
			oldY=centerY-sensordata1;
			currentX=0;
			timer=new Timer();
			timertask=new TimerTask() {
				@Override
				public void run() {
					// TODO Auto-generated method stub
					complexdraw(currentX);
					currentX=currentX+2;
					if(currentX>=getWindowManager().getDefaultDisplay().getWidth()){
						currentX = 0;
						oldX=0;
						oldY=centerY-sensordata1;
						y=centerY-sensordata1;
						ClearDraw4();
					}
				}
			};
			timer.schedule(timertask, 0, 200);// 动态绘制正弦图
			signal=2;
			timerflag = true ;
			timerdraw_btn.setEnabled(false);
			timerdraw_btn.setText(R.string.BT412);
			cleardraw_btn.setEnabled(true);
		}
    }


    public class cleardraw_btnlistener implements OnClickListener{

    	public void onClick(View v) {
   	     T = 0 ; T0 = 0; T1 = 0  ;
		    BT0 = 0 ; BT1 = 0 ; BT2 = 0;
			oldT = 0;
   		sendatatext1.setText("T");
			if(isrecord2==true)
			{
					isrecord2=false;
			}

			if(signal==2){
				timertask.cancel();
				timer.cancel();
				timerflag = false ;
				ClearDraw4();
				timerdraw_btn.setEnabled(true);
				timerdraw_btn.setText(R.string.BT41);
				cleardraw_btn.setEnabled(false);
			}
    	}
    }


	public void ClearDraw4() {
		Canvas canvas = surfaceholdersensor.lockCanvas(null);
		Paint paint = new Paint();
		paint.setXfermode(new PorterDuffXfermode(Mode.CLEAR));
		canvas.drawPaint(paint);
		paint.setXfermode(new PorterDuffXfermode(Mode.SRC));
		surfaceholdersensor.unlockCanvasAndPost(canvas);
	}

	void complexdraw(int current){
//		@SuppressWarnings("deprecation")
		Canvas canvas = surfaceholdersensor
				.lockCanvas(new Rect(oldX, 0, current,
						getWindowManager().getDefaultDisplay().getHeight()));// 关键:获取画布

		Paint mPaint = new Paint();
		mPaint.setStrokeWidth(6);// 设置画笔粗细

		mPaint.setColor(0xffFFB200);
		oldY = y; 	//温度曲线反相
		y = centerY - sensordata1; 	//温度曲线反相
		canvas.drawLine(oldX, oldY, current, y, mPaint);
		oldX = current;
		oldY = y;
		surfaceholdersensor.unlockCanvasAndPost(canvas);
		sensordataDebug = 4;
	}

    public static int hex2decimal(String s) {
        String digits = "0123456789ABCDEF";
        s = s.toUpperCase();
        int val = 0;
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            Byte d = (byte) (digits.indexOf(c)&0x0FF);
            val = 16*val + d;
        }
        return val&0xff;
    }

//    @SuppressWarnings("deprecation")
	@Override
    protected void onPause() {
        super.onPause();

        MediaButtonDisabler.unregister(this);
    }

//    @SuppressWarnings("deprecation")
	@Override
    protected void onResume() {
        super.onResume();

        MediaButtonDisabler.register(this);
    }
    @Override
    public void onDestroy() {
        unregisterReceiver(headsetdetect);
        super.onDestroy();
        sAllActivitys.remove(this);
    }

    public static void finishAll() {
        for(Activity activity : sAllActivitys) {
            activity.finish();
        }

        sAllActivitys.clear();
    }

    public static void exit() {
    	mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, current, 0);
        finishAll();
        System.exit(0);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_VOLUME_UP) || (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN)) {
           return true;
        }
        return super.onKeyUp(keyCode, event);
    }

    @Override
    public boolean onKeyDown(int keyCode,KeyEvent event)

    {
    	if(keyCode==KeyEvent.KEYCODE_BACK)
        {
    		new AlertDialog.Builder(this)
    		.setTitle("NXP Quick-Jack App")
    		.setMessage("Do you want to exit the App？")
    		.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
    			@Override
    			public void onClick(DialogInterface dialog, int which) {
    				if (Powersel) {
    	    			audio.stop();
    	    			myrec0.stop();
    	    			myrec0 = null;
    				}
    	            HijackU.exit();
    			}
    		})
    		.setNegativeButton("No", new DialogInterface.OnClickListener() {
    			@Override
    			public void onClick(DialogInterface dialog, int which) {

    			}
    		})
    		.show();
    		return true;
        } else if (keyCode==KeyEvent.KEYCODE_HEADSETHOOK) {
//			Toast.makeText(HijackU.this, "Headset Hook", Toast.LENGTH_SHORT)
//			.show();
			return true;
		}
        else if (keyCode==KeyEvent.KEYCODE_VOLUME_UP) {
//			Toast.makeText(HijackU.this, "Volume+", Toast.LENGTH_SHORT)
//			.show();
			mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, current++, 0);
			return true;
		}
        else if (keyCode==KeyEvent.KEYCODE_VOLUME_DOWN) {
//			Toast.makeText(HijackU.this, "Volume-", Toast.LENGTH_SHORT)
//			.show();
			mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, current--, 0);
			return true;
		}
        else if (keyCode==KeyEvent.KEYCODE_HOME) {
    		new AlertDialog.Builder(this)
    		.setTitle("NXP Quick-Jack App")
    		.setMessage("Do you want to exit the App？")
    		.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
    			@Override
    			public void onClick(DialogInterface dialog, int which) {
    			}
    		})
    		.setNegativeButton("No", new DialogInterface.OnClickListener() {
    			@Override
    			public void onClick(DialogInterface dialog, int which) {

    			}
    		})
    		.show();
    		return true;
        }

        else
    	{
        	 return super.onKeyDown(keyCode, event);
    	}

    }
    private void registerHeadsetPlugReceiver() {
    	headsetdetect = new HeadsetDetect();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.HEADSET_PLUG");
        registerReceiver(headsetdetect, intentFilter);
    }
}
