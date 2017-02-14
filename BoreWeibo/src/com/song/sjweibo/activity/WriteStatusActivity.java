package com.song.sjweibo.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.song.sjweibo.R;
import com.song.sjweibo.BaseActivity;
import com.song.sjweibo.activity.imgfilter.ImageFilterActivity;
import com.song.sjweibo.adapter.EmotionGvAdapter;
import com.song.sjweibo.adapter.EmotionPagerAdapter;
import com.song.sjweibo.adapter.WriteStatusGridImgsAdapter;
import com.song.sjweibo.api.SimpleRequestListener;
import com.song.sjweibo.entity.Emotion;
import com.song.sjweibo.entity.Status;
import com.song.sjweibo.utils.DialogUtils;
import com.song.sjweibo.utils.DisplayUtils;
import com.song.sjweibo.utils.ImageUtils;
import com.song.sjweibo.utils.StringUtils;
import com.song.sjweibo.utils.TitleBuilder;
import com.song.sjweibo.widget.WrapHeightGridView;
/**
 * @+id/et_write_status--------------评论微博正文，设置最小行数和最大输入长度
 * @+id/gv_write_status--------------高度自适应GridView
 * include_retweeted_status_card-----引用微博部分，分为一个Imageview和两个textview
 * 
 * @+id/iv_image-------加入图片按钮选项
 * @+id/iv_at----------@他人选项
 * @+id/iv_topic-------#话题选项
 * @+id/iv_emoji-------表情选项
 * @+id/iv_add---------加入其他选项
 * 
 * @+id/ll_emotion_dashboard-------包含如下整个的linearlayout
 * 		@+id/vp_emotion_dashboard------------viewPage组件
 * 
 * 		@+id/rg_emotion_dashboard------------radioGroup包含下面4个radiobutton
 * 		@+id/rb_emotion_dashboard_recently---最近的表情栏
 * 		@+id/rb_emotion_dashboard_default----默认的表情栏
 * 		@+id/rb_emotion_dashboard_emoji------emoji表情栏
 * 		@+id/rb_emotion_dashboard_langxiaohua-浪小花表情栏
 * 
 * @author lenovo
 * 写评论界面
 */
public class WriteStatusActivity extends BaseActivity implements OnClickListener, OnItemClickListener {

	// 输入框
	private EditText et_write_status;
	// 添加的九宫格图片
	private WrapHeightGridView gv_write_status;
	// 转发微博内容
	private View include_retweeted_status_card;
	private ImageView iv_rstatus_img;;
	private TextView tv_rstatus_username;;
	private TextView tv_rstatus_content;;
	// 底部添加栏
	private ImageView iv_image;
	private ImageView iv_at;
	private ImageView iv_topic;
	private ImageView iv_emoji;
	private ImageView iv_add;
	// 表情选择面板
	private LinearLayout ll_emotion_dashboard;
	private ViewPager vp_emotion_dashboard;
	// 进度框
	private ProgressDialog progressDialog;

	//写入微博九宫格适配器
	private WriteStatusGridImgsAdapter statusImgsAdapter;
	
	private ArrayList<Uri> imgUris = new ArrayList<Uri>();
	private EmotionPagerAdapter emotionPagerGvAdapter;
	
	// 引用的微博
	private Status retweeted_status;
	// 显示在页面中,实际需要转发内容的微博(需要写入的内容)
	private Status cardStatus;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_write_status);
		//将序列化内容转成微博对象
		retweeted_status = (Status) getIntent().getSerializableExtra("status");
		
		initView();
	}

	private void initView() {
		// 标题栏
		new TitleBuilder(this)
				.setTitleText("发微博")
				.setLeftText("取消")
				.setLeftOnClickListener(this)
				.setRightText("发送")
				.setRightOnClickListener(this)
				.build();
		// 输入框
		et_write_status = (EditText) findViewById(R.id.et_write_status);
		// 添加的九宫格图片
		gv_write_status = (WrapHeightGridView) findViewById(R.id.gv_write_status);
		// 转发微博内容
		include_retweeted_status_card = findViewById(R.id.include_retweeted_status_card);
		iv_rstatus_img = (ImageView) findViewById(R.id.iv_rstatus_img);
		tv_rstatus_username = (TextView) findViewById(R.id.tv_rstatus_username);
		tv_rstatus_content = (TextView) findViewById(R.id.tv_rstatus_content);
		// 底部添加栏
		iv_image = (ImageView)findViewById(R.id.iv_image);
		iv_at = (ImageView) findViewById(R.id.iv_at);
		iv_topic = (ImageView) findViewById(R.id.iv_topic);
		iv_emoji = (ImageView)findViewById(R.id.iv_emoji);
		iv_add = (ImageView) findViewById(R.id.iv_add);
		// 表情选择面板
		ll_emotion_dashboard = (LinearLayout) findViewById(R.id.ll_emotion_dashboard);
		vp_emotion_dashboard = (ViewPager) findViewById(R.id.vp_emotion_dashboard);
		// 进度框
		progressDialog = new ProgressDialog(this);
		progressDialog.setMessage("微博发布中...");

		//给GridView设置适配器，并传入图片地址
		statusImgsAdapter = new WriteStatusGridImgsAdapter(this, imgUris, gv_write_status);
		gv_write_status.setAdapter(statusImgsAdapter);
		gv_write_status.setOnItemClickListener(this);

		iv_image.setOnClickListener(this);
		iv_at.setOnClickListener(this);
		iv_topic.setOnClickListener(this);
		iv_emoji.setOnClickListener(this);
		iv_add.setOnClickListener(this);

		initRetweetedStatus();
		initEmotion();
	}
	
	/**
	 * 发送微博接口处理(受制于微博API，只能发送单图)
	 */
	private void sendStatus() {
		String comment = et_write_status.getText().toString();
		if(TextUtils.isEmpty(comment)) {
			showToast("微博内容不能为空");
			return;
		}
		
		String imgFilePath = null;
		if (imgUris.size() > 0) {
			// 微博API中只支持上传一张图片
			Uri uri = imgUris.get(0);
			imgFilePath = ImageUtils.getImageAbsolutePath(this, uri);
		}

		// 转发微博的id
		long retweetedStatsId = cardStatus == null ? -1 : cardStatus.getId();
		// 上传微博api接口
		progressDialog.show();
		weiboApi.statusesSend(et_write_status.getText().toString(), imgFilePath, retweetedStatsId,
				new SimpleRequestListener(this, progressDialog) {

					@Override
					public void onComplete(String response) {
						super.onComplete(response);

						setResult(RESULT_OK);
						
						showToast("微博发送成功");
						WriteStatusActivity.this.finish();
					}
				});
	}

	/**
	 * 初始化引用微博内容
	 */
	private void initRetweetedStatus() {
		// 转发微博特殊处理
		if(retweeted_status != null) {
			Status rrStatus = retweeted_status.getRetweeted_status();
			if(rrStatus != null) {
				// 判断转发的微博是否包含转发内容
				
				//将拼成的信息设置到输入框上
				String content = "//@" + retweeted_status.getUser().getName() 
						+ ":" + retweeted_status.getText();
				et_write_status.setText(StringUtils.getWeiboContent(this, et_write_status, content));
				
				// 如果引用的为转发微博,则使用它转发的内容
				cardStatus = rrStatus;
			} else {
				//如果不包含则是原创微博
				et_write_status.setText("转发微博");	
				// 如果引用的为原创微博,则使用它自己的内容
				cardStatus = retweeted_status;
			}
			
			// 设置转发图片内容
			String imgUrl = cardStatus.getThumbnail_pic();
			if(TextUtils.isEmpty(imgUrl)) {
				iv_rstatus_img.setVisibility(View.GONE);
			} else {
				iv_rstatus_img.setVisibility(View.VISIBLE);
				imageLoader.displayImage(cardStatus.getThumbnail_pic(), iv_rstatus_img);
			}
			// 设置转发文字内容
			tv_rstatus_username.setText("@" + cardStatus.getUser().getName());
			tv_rstatus_content.setText(cardStatus.getText());
			
			// 转发微博时,不能添加图片
			iv_image.setVisibility(View.GONE);
			include_retweeted_status_card.setVisibility(View.VISIBLE);
		}
	}

	/**
	 *  初始化表情面板内容
	 */
	private void initEmotion() {
		// 获取屏幕宽度
		int gvWidth = DisplayUtils.getScreenWidthPixels(this);
		// 表情边距
		int spacing = DisplayUtils.dp2px(this, 8);
		
		// GridView中item的宽度高度--每个表情图片的宽度高度	
		int itemWidth = (gvWidth - spacing * 8) / 7;
		int gvHeight = itemWidth * 3 + spacing * 4;

		List<GridView> gvs = new ArrayList<GridView>();
		List<String> emotionNames = new ArrayList<String>();
		
		
		// 遍历所有的表情名字
		for (String emojiName : Emotion.emojiMap.keySet()) {
			emotionNames.add(emojiName);
			// 每20个表情作为一组,同时添加到ViewPager对应的view集合中
			if (emotionNames.size() == 20) {
				GridView gv = createEmotionGridView(emotionNames, gvWidth, spacing, itemWidth, gvHeight);
				gvs.add(gv);
				// 添加完一组表情,重新创建一个表情名字集合
				emotionNames = new ArrayList<String>();
			}
		}

		// 检查最后是否有不足20个表情的剩余情况
		if (emotionNames.size() > 0) {
			GridView gv = createEmotionGridView(emotionNames, gvWidth, spacing, itemWidth, gvHeight);
			gvs.add(gv);
		}

		// 将多个GridView添加显示到ViewPager中
		emotionPagerGvAdapter = new EmotionPagerAdapter(gvs);
		//设置适配器
		vp_emotion_dashboard.setAdapter(emotionPagerGvAdapter);
		//设置宽高
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(gvWidth, gvHeight);
		vp_emotion_dashboard.setLayoutParams(params);
	}

	/**
	 * 创建显示表情的GridView
	 */
	private GridView createEmotionGridView(List<String> emotionNames, int gvWidth, int padding, int itemWidth, int gvHeight) {
		// 创建GridView
		GridView gv = new GridView(this);
		gv.setBackgroundResource(R.color.bg_gray);
		gv.setSelector(R.color.transparent);
		gv.setNumColumns(7);
		gv.setPadding(padding, padding, padding, padding);
		gv.setHorizontalSpacing(padding);
		gv.setVerticalSpacing(padding);
		LayoutParams params = new LayoutParams(gvWidth, gvHeight);
		gv.setLayoutParams(params);
		
		// 给GridView设置表情图片
		EmotionGvAdapter adapter = new EmotionGvAdapter(this, emotionNames, itemWidth);
		gv.setAdapter(adapter);
		gv.setOnItemClickListener(this);
		return gv;
	}

	/**
	 * 更新图片显示
	 */
	private void updateImgs() {
		if(imgUris.size() > 0) {
			// 如果有图片则显示GridView,同时更新内容
			gv_write_status.setVisibility(View.VISIBLE);
			statusImgsAdapter.notifyDataSetChanged();
		} else {
			// 无图则不显示GridView
			gv_write_status.setVisibility(View.GONE);
		}
	}
	
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.titlebar_tv_left:
			finish();
			break;
		case R.id.titlebar_tv_right:
			sendStatus();
			break;
		case R.id.iv_image:
			/**
			 * 底部图片按钮点击时弹出图片按钮选择对话框
			 */
			DialogUtils.showImagePickDialog(this);
			break;
		case R.id.iv_at:
			break;
		case R.id.iv_topic:
			break;
		case R.id.iv_emoji:
			if(ll_emotion_dashboard.getVisibility() == View.VISIBLE) {
				// 显示表情面板时点击,将按钮图片设为笑脸按钮,同时隐藏面板
				iv_emoji.setImageResource(R.drawable.btn_insert_emotion);
				ll_emotion_dashboard.setVisibility(View.GONE);
			} else {
				// 未显示表情面板时点击,将按钮图片设为键盘,同时显示面板
				iv_emoji.setImageResource(R.drawable.btn_insert_keyboard);
				ll_emotion_dashboard.setVisibility(View.VISIBLE);
			}
			break;
		case R.id.iv_add:
			break;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		//获取到当前被点击item的适配器
		Object itemAdapter = parent.getAdapter();

		if (itemAdapter instanceof WriteStatusGridImgsAdapter) {
			//判断是否是写入微博九宫格适配器 
			if (position == statusImgsAdapter.getCount() - 1) {
				// 如果点击了最后一个加号图标,则显示选择图片对话框
				DialogUtils.showImagePickDialog(this);
			}
		} else if (itemAdapter instanceof EmotionGvAdapter) {
			//判断是否是表情GridView适配器
			EmotionGvAdapter emotionGvAdapter = (EmotionGvAdapter) itemAdapter;

			if (position == emotionGvAdapter.getCount() - 1) {
				// 如果点击了最后一个回退按钮,则调用删除键事件
				//给某个控件直接分配一个KeyEvent按钮事件
				//new KeyEvent创建一个按钮事件，action为按下操作，ACTION_DOWN;code 为按下的那个按钮，这里是回退操作KEYCODE_DEL
				et_write_status.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL));
			} else {
				/** 如果点击了表情,则添加到输入框中
				 *  将表情图片插入到EditText中
				 */
				
				//先获取到当前位置的表情名称
				String emotionName = emotionGvAdapter.getItem(position);
				
				// 获取当前光标位置，调用EditText的方法getSelectionStart()
				int curPosition = et_write_status.getSelectionStart();
				//在指定位置上添加表情图片文本(通过StringBuilder拼装字符串)
				StringBuilder sb = new StringBuilder(et_write_status.getText().toString());
				sb.insert(curPosition, emotionName);
				
				// 特殊文字处理,将表情文字转换为表情图片
					et_write_status.setText(StringUtils.getWeiboContent(
							this, et_write_status, sb.toString()));
				
				// 将光标设置到新增表情的右侧(原来的光标位置加上现在的图片表情对应的文本长度)
				et_write_status.setSelection(curPosition + emotionName.length());
			}
		}
	}
	
	private void showIfNeedEditDialog(final Uri imageUri) {
		DialogUtils.showListDialog(this, "是否需要编辑图片?", new String[]{"编辑图片", "使用原图"}, 
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						if(which == 0) {
							Intent intent = new Intent(WriteStatusActivity.this, ImageFilterActivity.class);
							intent.putExtra("path", ImageUtils.getImageAbsolutePath(WriteStatusActivity.this, imageUri));
							startActivityForResult(intent, 1234);
						} else {
							imgUris.add(imageUri);
							updateImgs();
						}
					}
				});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		switch (requestCode) {
		case ImageUtils.GET_IMAGE_BY_CAMERA:
			if(resultCode == RESULT_CANCELED) {
				// 如果拍照取消,将之前新增的图片地址删除
				ImageUtils.deleteImageUri(this, ImageUtils.imageUriFromCamera);
			} else {
//				拍照后将图片添加到页面上
//				imgUris.add(ImageUtils.imageUriFromCamera);
//				updateImgs();
				
				// crop
				ImageUtils.cropImage(this, ImageUtils.imageUriFromCamera);
			}
		case ImageUtils.CROP_IMAGE:
			if(resultCode != RESULT_CANCELED) {
				/**
				 * 将图片地址传入总的图片地址集合中
				 * 调用更新方法
				 */
				imgUris.add(ImageUtils.cropImageUri);
				updateImgs();
			}
			break;
		case ImageUtils.GET_IMAGE_FROM_PHONE:
			if(resultCode != RESULT_CANCELED) {
				// 本地相册选择完后将图片添加到页面上
				imgUris.add(data.getData());
				updateImgs();
			}
			break;
		default:
			break;
		}
	}

}
