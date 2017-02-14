package com.song.sjweibo.activity;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.song.sjweibo.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.song.sjweibo.BaseActivity;
import com.song.sjweibo.entity.BrowserPic;
import com.song.sjweibo.entity.PicUrls;
import com.song.sjweibo.entity.Status;
import com.song.sjweibo.utils.DisplayUtils;
import com.song.sjweibo.utils.ImageUtils;


public class ImageBrowserActivity extends BaseActivity implements OnClickListener {
	private ViewPager vp_image_brower;
	private TextView tv_image_index;
	private Button btn_save;
	private Button btn_original_image;
//	private TextView tv_like;

	private Status status;
	private ImageBrowserAdapter adapter;
	private ArrayList<PicUrls> imgUrls;
	private int position;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_image_brower);
		initData();
		initView();
		setData();
	}

	private void initData() {
		status = (Status) intent.getSerializableExtra("status");
		position = intent.getIntExtra("position", -1);
		if(position == -1) {
			imgUrls = new ArrayList<PicUrls>();
			PicUrls url = new PicUrls();
			url.setThumbnail_pic(status.getThumbnail_pic());
			url.setOriginal_pic(status.getOriginal_pic());
			imgUrls.add(url);
			position = 0;
		} else {
			imgUrls = status.getPic_urls();
		}
	}

	private void initView() {
		vp_image_brower = (ViewPager) findViewById(R.id.vp_image_brower);
		tv_image_index = (TextView) findViewById(R.id.tv_image_index);
		btn_save = (Button) findViewById(R.id.btn_save);
		btn_original_image = (Button) findViewById(R.id.btn_original_image);
//		tv_like = (TextView) findViewById(R.id.tv_like);

		btn_save.setOnClickListener(this);
		btn_original_image.setOnClickListener(this);
	}
	
	private void setData() {
		tv_image_index.setVisibility(imgUrls.size() > 1 ? View.VISIBLE : View.GONE);
		
		adapter = new ImageBrowserAdapter(this, imgUrls);
		vp_image_brower.setAdapter(adapter);
		
		final int size = status.getPic_urls().size();
		tv_image_index.setText((position % size + 1) + "/" + status.getPic_urls().size());
		vp_image_brower.setCurrentItem(Integer.MAX_VALUE / size / 2 * size + position);
		vp_image_brower.setOnPageChangeListener(new OnPageChangeListener() {
			@Override
			public void onPageSelected(int arg0) {
				tv_image_index.setText((arg0 % size + 1) + "/" + status.getPic_urls().size());
				BrowserPic pic = adapter.getPic(arg0 % size);
				btn_original_image.setVisibility(pic.isOriginalPic() ? View.GONE : View.VISIBLE);
			}
			
			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				
			}
			
			@Override
			public void onPageScrollStateChanged(int arg0) {
				
			}
		});
	}

	@Override
	public void onClick(View v) {
		BrowserPic pic = adapter.getPic(vp_image_brower.getCurrentItem() % status.getPic_urls().size());
		switch (v.getId()) {
		case R.id.btn_save:
			Bitmap bitmap = pic.getBitmap();
			PicUrls picUrl = pic.getPic();
			String oriUrl = picUrl.getOriginal_pic();
			String midUrl = picUrl.getBmiddle_pic();
			//设置保存的图片名
			String fileName = "img-" + (pic.isOriginalPic() ? 
					"ori-" + oriUrl.substring(oriUrl.lastIndexOf("/") + 1)
					: "mid-" + midUrl.substring(midUrl.lastIndexOf("/") + 1));
			
//			String insertImage = MediaStore.Images.Media.insertImage(
//				getContentResolver(), bitmap, fileName, "BoreWeiboImg");
			
			if(bitmap != null) {
				try {
					ImageUtils.saveFile(this, bitmap, fileName);
					showToast("图片保存成功");
				} catch (IOException e) {
					e.printStackTrace();
					showToast("图片保存失败");
				}
			}
			break;
		case R.id.btn_original_image:
			//需要显示原图，并通知适配器进行数据更新
			pic.setOriginalPic(true);
			adapter.notifyDataSetChanged();
			//隐藏原图按钮
			btn_original_image.setVisibility(View.GONE);
			break;
		}
	}
	
	private class ImageBrowserAdapter extends PagerAdapter {

		private Activity context;
		private ArrayList<BrowserPic> pics;
		private ImageLoader mImageLoader;

		public ImageBrowserAdapter(Activity context, ArrayList<PicUrls> picUrls) {
			this.context = context;
			this.mImageLoader = ImageLoader.getInstance();
			initImgs(picUrls);
		}

		private void initImgs(ArrayList<PicUrls> picUrls) {
			pics = new ArrayList<BrowserPic>();
			BrowserPic browserPic;
			for(PicUrls picUrl : picUrls) {
				browserPic = new BrowserPic();
				browserPic.setPic(picUrl);
				
				//得到原图bitmap
				Bitmap oBm = mImageLoader.getMemoryCache().get(picUrl.getOriginal_pic());
				//得到原图file文件
				File discCache = mImageLoader.getDiscCache().get(picUrl.getOriginal_pic());
				browserPic.setOriginalPic(oBm != null || 
						(discCache != null && discCache.exists() && discCache.length() > 0));
				
				//回收图片，释放与此位图相关联的本机对象，并清除对像素数据的引用。
				if(oBm != null && !oBm.isRecycled()) {
					oBm.recycle();
				}
				discCache = null;
				
				pics.add(browserPic);
			}
			//如果已经显示原图则隐藏原图按钮
			btn_original_image.setVisibility(pics.get(position).isOriginalPic() ? View.GONE : View.VISIBLE);
		}
		
		public BrowserPic getPic(int position) {
			return pics.get(position);
		}

		@Override
		public int getCount() {
			if (pics.size() > 1) {
				return Integer.MAX_VALUE;
			}
			return pics.size();
		}
		
		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view == object;
		}

		@Override
		public View instantiateItem(ViewGroup container, int position) {
			//这里的imageAdapter没有通过view.inflate()方法得到view所以通过代码声明
			//声明可滚动scrollview并设置位置参数
			ScrollView sv = new ScrollView(context);
			FrameLayout.LayoutParams svParams = new FrameLayout.LayoutParams(
					FrameLayout.LayoutParams.MATCH_PARENT, 
					FrameLayout.LayoutParams.MATCH_PARENT);
			sv.setLayoutParams(svParams);
			
			//声明LinearLayout并设置位置参数
			LinearLayout ll = new LinearLayout(context);
			LinearLayout.LayoutParams llParams = new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.MATCH_PARENT, 
					LinearLayout.LayoutParams.WRAP_CONTENT);
			ll.setLayoutParams(llParams);
			//将LinearLayout添加到ScrollView
			sv.addView(ll);
			//得到当前屏幕宽高
			final int screenHeight = DisplayUtils.getScreenHeightPixels(context);
			final int screenWidth = DisplayUtils.getScreenWidthPixels(context);
			
			//创建Imageview并设置缩放类型为居中显示
			final ImageView iv = new ImageView(context);
			iv.setScaleType(ScaleType.FIT_CENTER);
			//将ImageView添加到linearlayout中
			ll.addView(iv);
			
			final BrowserPic browserPic = pics.get(position % pics.size());
			PicUrls picUrls = browserPic.getPic();
			
			//如果需要显示原图就不显示中等质量的图片
			String url = browserPic.isOriginalPic() ? picUrls.getOriginal_pic() : picUrls.getBmiddle_pic();
			ImageLoadingListener  mylistner = new ImageLoadingListener(){

				
				@Override
				public void onLoadingStarted(String imageUri, View view) {
					
				}
				
				@Override
				public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
					
				}
				
				//图片的动态计算部分
				//加载完成，然后进行显示处理，先进行比例判断，然后动态显示ImageVeiw控件
				@Override
				public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
					browserPic.setBitmap(loadedImage);
					
					//得到比例
					float scale = (float) loadedImage.getHeight() / loadedImage.getWidth();
					//比较高度宽度取最大值为高度
					int height = Math.max((int) (screenWidth * scale), screenHeight);
					LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(screenWidth, height);
					iv.setLayoutParams(params);
					//将图片显示到控件上
					iv.setImageBitmap(loadedImage);
				}
				
				@Override
				public void onLoadingCancelled(String imageUri, View view) {
					
				}
			
			};
			//动态计算图片，loadimage不会直接将图片显示在控件中，会执行ImageLoadingListener回调返回图片，然后在判断是否进行操作
			mImageLoader.loadImage(url,mylistner);
			
			iv.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					context.finish();
				}
			});
			//将总scrollview添加到总布局中
			container.addView(sv, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
			return sv;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView((View) object);
		}

		@Override
		public int getItemPosition(Object object) {
			return POSITION_NONE;
		}
	}

}
