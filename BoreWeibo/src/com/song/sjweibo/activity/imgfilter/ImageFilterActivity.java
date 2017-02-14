package com.song.sjweibo.activity.imgfilter;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.song.sjweibo.R;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.song.sjweibo.BaseActivity;
import com.song.sjweibo.activity.WriteStatusActivity;
import com.song.sjweibo.utils.DisplayUtils;
import com.song.sjweibo.widget.filter.CropImage;
import com.song.sjweibo.widget.filter.CropImageView;

/**
 * 图片编辑类
 */
public class ImageFilterActivity extends BaseActivity implements OnClickListener {
	private static final int REQUESTCODE_IMAGEFILTER_FACE = 1;
	private static final int REQUESTCODE_IMAGEFILTER_FRAME = 2;
	private static final int REQUESTCODE_IMAGEFILTER_CROP = 3;
	private static final int REQUESTCODE_IMAGEFILTER_EFFECT = 4;
	
	private static final int SHOW_PROGRESS = 0;
	private static final int REMOVE_PROGRESS = 1;
	
	private LinearLayout mImageFilterTopBar;
	private Button mCancel;
	private Button mFinish;
	private ImageButton mBack;
	private ImageButton mForward;
	private CropImageView mDisplay;
	private ProgressBar mProgressBar;
	private CropImage mCropImage;
	
	private LinearLayout mImageFilterBottomBar;
	private Button mCut;
	private Button mEffect;
	private Button mFace;
	private Button mFrame;
	
	private LinearLayout mCropBar;
	private Button mLeft;
	private Button mRight;

	private String mOldPath;// 旧图片地址
	private Bitmap mOldBitmap;// 旧图片
	private Bitmap mNewBitmap;// 新图片
	private boolean mIsOld = true;// 是否是选择了旧图片
	private boolean mIsSetResult = false;// 是否是要返回数据
	
	// 0-无编辑模式 1-裁剪 2-滤镜 3-贴图 4-边框
	private int currentFilterType = 0;
	
	/**
	 * crop图片控制进度条
	 */
	Handler cropHandler = new Handler() {

		public void handleMessage(Message msg) {
			switch (msg.what) {
			case SHOW_PROGRESS:
				mProgressBar.setVisibility(View.VISIBLE);
				break;
			case REMOVE_PROGRESS:
				cropHandler.removeMessages(SHOW_PROGRESS);
				mProgressBar.setVisibility(View.INVISIBLE);
				break;
			}
		}
	};

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_imagefilter);
		
		initView();
		init();
	}

	private void initView() {
		mImageFilterTopBar = (LinearLayout) findViewById(R.id.ll_imagefilter_topbar);
		mCancel = (Button) findViewById(R.id.imagefilter_cancel);
		mFinish = (Button) findViewById(R.id.imagefilter_finish);
		mBack = (ImageButton) findViewById(R.id.imagefilter_back);
		mForward = (ImageButton) findViewById(R.id.imagefilter_forward);
		mDisplay = (CropImageView) findViewById(R.id.imagefilter_display);
		mProgressBar = (ProgressBar) findViewById(R.id.imagefilter_crop_progressbar);
		
		mImageFilterBottomBar = (LinearLayout) findViewById(R.id.ll_imagefilter_bar);
		mCut = (Button) findViewById(R.id.imagefilter_cut);
		mEffect = (Button) findViewById(R.id.imagefilter_effect);
		mFace = (Button) findViewById(R.id.imagefilter_face);
		mFrame = (Button) findViewById(R.id.imagefilter_frame);
		
		mCropBar = (LinearLayout) findViewById(R.id.ll_imagefilter_crop);
		mLeft = (Button) findViewById(R.id.imagefilter_crop_left);
		mRight = (Button) findViewById(R.id.imagefilter_crop_right);
		
		mCancel.setOnClickListener(this);
		mFinish.setOnClickListener(this);
		mBack.setOnClickListener(this);
		mForward.setOnClickListener(this);
		mCut.setOnClickListener(this);
		mEffect.setOnClickListener(this);
		mFace.setOnClickListener(this);
		mFrame.setOnClickListener(this);
		mLeft.setOnClickListener(this);
		mRight.setOnClickListener(this);
	}

	private void init() {
		// 初始化界面按钮设为不可用
		mBack.setImageResource(R.drawable.image_action_back_arrow_normal);
		mForward.setImageResource(R.drawable.image_action_forward_arrow_normal);
		mBack.setEnabled(false);
		mForward.setEnabled(false);
		// 获取是否返回数据
		mIsSetResult = getIntent().getBooleanExtra("isSetResult", false);
		// 接收传递的图片地址
		mOldPath = getIntent().getStringExtra("path");
		ImageSize targetImageSize = new ImageSize(DisplayUtils.getScreenWidthPixels(this), 
				DisplayUtils.getScreenHeightPixels(this));
		// 本地图片,同步获取
		mOldBitmap = imageLoader.loadImageSync("file://" + mOldPath, targetImageSize);
		mDisplay.setImageBitmap(mOldBitmap);
		mNewBitmap = mOldBitmap;
	}
	
	/**
	 * 初始化crop图片
	 */
	private void resetImageView(Bitmap b) {
		mDisplay.clear();
		mDisplay.setImageBitmap(b);
		mDisplay.setImageBitmapResetBase(b, true);
		mCropImage = new CropImage(this, mDisplay, cropHandler);
		mCropImage.crop(b);
	}
	
	private void intent2MoreFilterActivity(Class<? extends BaseActivity> activity, int requestCode) {
		Intent intent = new Intent(this, activity);
		intent.putExtra("path", mIsOld ? mOldBitmap : mNewBitmap);
		startActivityForResult(intent, requestCode);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.imagefilter_cancel:
			if(currentFilterType == 1) {
				mCropImage.cropCancel();
			} else {
				finish();
			}
			break;
		case R.id.imagefilter_finish:
			if(currentFilterType == 1) {
				mNewBitmap = mCropImage.cropAndSave();
				mIsOld = false;
			} else {
				// 判断是否要返回数据
				if (mIsSetResult) {
					// 根据是否选择旧图片返回图片地址
					Intent intent = new Intent();
					intent.putExtra("image", mIsOld ? mOldBitmap : mNewBitmap);
					setResult(RESULT_OK, intent);
				} else {
					// 根据是否选择旧图片添加一个新的图片并跳转到上传图片界面
					Intent intent = new Intent(this, WriteStatusActivity.class);
					intent.putExtra("image", mIsOld ? mOldBitmap : mNewBitmap);
					startActivity(intent);
				}
				finish();
			}
			
			break;
		case R.id.imagefilter_back:
			// 选择旧图片
			mIsOld = true;
			mBack.setImageResource(R.drawable.image_action_back_arrow_normal);
			mForward.setImageResource(R.drawable.image_filter_action_forward_arrow);
			mBack.setEnabled(false);
			mForward.setEnabled(true);
			mDisplay.setImageBitmap(mOldBitmap);
			break;
		case R.id.imagefilter_forward:
			// 选择新图片
			mIsOld = false;
			mBack.setImageResource(R.drawable.image_filter_action_back_arrow);
			mForward.setImageResource(R.drawable.image_action_forward_arrow_normal);
			mBack.setEnabled(true);
			mForward.setEnabled(false);
			mDisplay.setImageBitmap(mNewBitmap);
			break;
		case R.id.imagefilter_cut:
			currentFilterType = 1;
			
			mImageFilterTopBar.setVisibility(View.GONE);
			mImageFilterBottomBar.setVisibility(View.GONE);
			mCropBar.setVisibility(View.VISIBLE);
			resetImageView(mOldBitmap);
			break;
		case R.id.imagefilter_crop_left:
			// 左旋转
			mCropImage.startRotate(270f);
			break;
		case R.id.imagefilter_crop_right:
			// 左旋转
			mCropImage.startRotate(90f);
			break;
		case R.id.imagefilter_effect:
			// 跳转到特效界面,并传递图片地址
			intent2MoreFilterActivity(ImageFilterEffectActivity.class,
					REQUESTCODE_IMAGEFILTER_EFFECT);
			break;
		case R.id.imagefilter_face:
			// 跳转到表情界面,并传递图片地址
			intent2MoreFilterActivity(ImageFilterFaceActivity.class, 
					REQUESTCODE_IMAGEFILTER_FACE);
			break;
		case R.id.imagefilter_frame:
			// 跳转到边框界面,并传递图片地址
			intent2MoreFilterActivity(ImageFilterFrameActivity.class, 
					REQUESTCODE_IMAGEFILTER_FRAME);
			break;
		default:
			break;
		}
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			// 接收修改后的图片地址,并更新
			mNewBitmap = data.getParcelableExtra("bitmap");
			mIsOld = false;
			mBack.setImageResource(R.drawable.image_filter_action_back_arrow);
			mForward.setImageResource(R.drawable.image_action_forward_arrow_normal);
			mBack.setEnabled(true);
			mForward.setEnabled(false);
			mDisplay.setImageBitmap(mNewBitmap);

		}
	}
}
