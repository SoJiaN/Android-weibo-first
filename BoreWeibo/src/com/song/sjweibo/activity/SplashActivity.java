package com.song.sjweibo.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.song.sjweibo.R;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.song.sjweibo.BaseActivity;

public class SplashActivity extends BaseActivity {

	private static final int WHAT_INTENT2MAIN = 2;
	private static final long SPLASH_DUR_TIME = 1000;
	private static final int WHAT_INTENT2MAIN_INTERFACE = 1;
	private Oauth2AccessToken accessToken;
	private Handler handler=new Handler(){

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch(msg.what){	
			case WHAT_INTENT2MAIN_INTERFACE:
				intent2Activity(MainInterface.class);
				finish();
				break;
			case WHAT_INTENT2MAIN:
				intent2Activity(MainActivity.class);
				finish();
				break;
			default:
			}
		};
		
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		//要选择自己的项目中的R文件
		setContentView(R.layout.activity_splash);
		//得到accessToken对象
		accessToken = AccessTokenKeeper.readAccessToken(this);
		if(accessToken.isSessionValid()){
			handler.sendEmptyMessageDelayed(WHAT_INTENT2MAIN_INTERFACE, SPLASH_DUR_TIME);
		}else {
			handler.sendEmptyMessageDelayed(WHAT_INTENT2MAIN, SPLASH_DUR_TIME);
		}
	}

}
