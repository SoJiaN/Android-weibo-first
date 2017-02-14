
package com.song.sjweibo.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.text.SpannableString;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.ScaleAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.song.sjweibo.R;
import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.song.sjweibo.activity.ImageBrowserActivity;
import com.song.sjweibo.activity.StatusDetailActivity;
import com.song.sjweibo.activity.UserInfoActivity;
import com.song.sjweibo.activity.WriteCommentActivity;
import com.song.sjweibo.activity.WriteStatusActivity;
import com.song.sjweibo.api.BoreWeiboAPI;
import com.song.sjweibo.api.SimpleRequestListener;
import com.song.sjweibo.entity.PicUrls;
import com.song.sjweibo.entity.Status;
import com.song.sjweibo.entity.User;
import com.song.sjweibo.entity.response.StatusTimeLineResponse;
import com.song.sjweibo.utils.DateUtils;
import com.song.sjweibo.utils.StringUtils;
import com.song.sjweibo.utils.ToastUtils;
/**
 * HomeFragment的BoreweiboApi.statusesHome_timeline接口返回的数据工具类
 * 封装得到HomeFragment的适配器类
 * 设置单个微博的UI布局
 * @author lenovo
 * 显示微博正文时用到StringUtils，实现可点击文字和表情的处理
 * 显示微博发表时间时用到DateUtils，实现微博时间格式处理
 * @see
 * 点击整个微博item-StatusDetialActivity
 * 		点击头像-UserinfoActivity
 * 		点击姓名-UserinfoActivity
 *		 微博正文单图、多图-ImageBrowserActivity
 * 		点击引用微博-StatusDetialActivity
 * 		引用微博正文单图、多图-ImageBrowserActivity
 * 		操作栏评论-1.有-StatusDetialActivity
 * 		        2.无-WriteStatusActivity
 * 		操作栏转发-StausDetialActivity
 * 		操作栏点赞-设置动画
 */
public class StatusAdapter extends BaseAdapter {

	private Context context;
	private List<Status> datas;
	private ImageLoader imageLoader;

	public StatusAdapter(Context context, List<Status> datas) {
		this.context = context;
		this.datas = datas;
		imageLoader = ImageLoader.getInstance();
	}

	@Override
	public int getCount() {
		return datas.size();
	}

	@Override
	public Status getItem(int position) {
		return datas.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	/** 
     * @param position      position就是位置从0开始 
     * @param convertView   convertView是Spinner,ListView中每一项要显示的view 
     * @param parent        parent就是父窗体了，也就是Spinner,ListView,GridView了 
     * @return              通常return 的view也就是convertView 
     * 绘制的内容均在此实现 
     */  
	public View getView(final int position, View convertView, ViewGroup parent) {
		final ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
			/**
			 * 这个convertView是item_status是微博中每个item的样式，就是每一条微博的样式
			 * item_status.xml文件包含如下
			 * 1.这个include_avatar.xml-------------------是convertView的一个inculde文件，是头像和名字和签名的部分
			 * 		1.1 "@+id/iv_avatar" 是头像
			 * 		1.2 "@+id/tv_subhead" 是名字
			 * 		1.3 "@+id/tv_caption" 是本条微博发表时间+来源
			 * 2."@+id/tv_content"-----------------------是微博文本内容的显示部分
			 * 		一个TextView文件，用于微博的文本内容
			 * 3.这个include_status_image.xml -----------为图片的显示部分，为Framelayout。中的
			 * 		3.1 "@+id/gv_images"
			 * 				是com.song.sjweibo.widget.WrapHeightGridView自定义组件用于显示多图，设置为3列
			 * 		3.2 "@+id/iv_image"
			 * 				Imageview组件用于单图的显示
			 * 4.这个include_retweeted_status.xml -------为引用微博部分
			 * 		4.1 "@+id/tv_retweeted_content"
			 * 				是textView用于显示文本部分
			 * 		4.2 "@+id/include_status_image"
			 * 				是引用图片的显示部分
			 * 5.这个include_status_controlbar.xml--------为底部操作栏布局
			 * 		5.1 "@+id/ll_share_bottom"
			 * 				5.1.1 "@+id/iv_share_bottom"
			 * 				5.1.2 "@+id/tv_share_bottom"
			 * 		5.2 "@+id/ll_comment_bottom"
			 * 				5.2.1 "@+id/iv_comment_bottom"
			 * 				5.2.2 "@+id/tv_comment_bottom"
			 * 		5.3 "@+id/ll_like_bottom"
			 * 				5.3.1 "@+id/cb_like_bottom"  是com.song.sjweibo.widget.InsideCheckBox
			 * 				5.3.2 "@+id/tv_like_bottom"
			 */
			convertView = View.inflate(context, R.layout.item_status, null);
			holder.ll_card_content = (LinearLayout) convertView
					.findViewById(R.id.ll_card_content);
			holder.iv_avatar = (ImageView) convertView
					.findViewById(R.id.iv_avatar);
			holder.rl_content = (RelativeLayout) convertView
					.findViewById(R.id.rl_content);
			holder.tv_subhead = (TextView) convertView
					.findViewById(R.id.tv_subhead);
			holder.tv_caption = (TextView) convertView
					.findViewById(R.id.tv_caption);

			holder.tv_content = (TextView) convertView
					.findViewById(R.id.tv_content);
			holder.include_status_image = (FrameLayout) convertView
					.findViewById(R.id.include_status_image);
			holder.gv_images = (GridView) holder.include_status_image
					.findViewById(R.id.gv_images);
			holder.iv_image = (ImageView) holder.include_status_image
					.findViewById(R.id.iv_image);

			holder.include_retweeted_status = (LinearLayout) convertView
					.findViewById(R.id.include_retweeted_status);
			holder.tv_retweeted_content = (TextView) holder.include_retweeted_status
					.findViewById(R.id.tv_retweeted_content);
			holder.include_retweeted_status_image = (FrameLayout) holder.include_retweeted_status
					.findViewById(R.id.include_status_image);
			holder.gv_retweeted_images = (GridView) holder.include_retweeted_status_image
					.findViewById(R.id.gv_images);
			holder.iv_retweeted_image = (ImageView) holder.include_retweeted_status_image
					.findViewById(R.id.iv_image);

			holder.ll_share_bottom = (LinearLayout) convertView
					.findViewById(R.id.ll_share_bottom);
			holder.iv_share_bottom = (ImageView) convertView
					.findViewById(R.id.iv_share_bottom);
			holder.tv_share_bottom = (TextView) convertView
					.findViewById(R.id.tv_share_bottom);
			holder.ll_comment_bottom = (LinearLayout) convertView
					.findViewById(R.id.ll_comment_bottom);
			holder.iv_comment_bottom = (ImageView) convertView
					.findViewById(R.id.iv_comment_bottom);
			holder.tv_comment_bottom = (TextView) convertView
					.findViewById(R.id.tv_comment_bottom);
			holder.ll_like_bottom = (LinearLayout) convertView
					.findViewById(R.id.ll_like_bottom);
			holder.cb_like_bottom = (CheckBox) convertView
					.findViewById(R.id.cb_like_bottom);
			holder.tv_like_bottom = (TextView) convertView
					.findViewById(R.id.tv_like_bottom);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		// set data
		/* 在HomeFragment中调用statusesHome_timeline接口得到StatusTimeLineResponse.class对象
		 * 调用对象的getStatuses方法得到Status类的对象 status
		 * 利用position得到该位置的status对象
		 * 
		 */
		final Status status = getItem(position);
		//从Status类中得到用户信息类
		final User user = status.getUser();
		
		/* 设置头像图片与姓名的点击事件。跳转到个人中心页面
		 * 将显示图像任务添加到执行池。当轮到时，图像将被设置为ImageView。
		 */
		imageLoader.displayImage(user.getProfile_image_url(), holder.iv_avatar);
		holder.iv_avatar.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(context, UserInfoActivity.class);
				//将用户姓名和用户id传递给个人中心页面
				intent.putExtra("userName", user.getName());
				intent.putExtra("userid", String.valueOf(user.getId()));
				context.startActivity(intent);
			}
		});
		
		//用户姓名的点击事件。跳转到个人中心页面
		holder.tv_subhead.setText(user.getName());
		holder.tv_subhead.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(context, UserInfoActivity.class);
				//将用户姓名和用户id传递给个人中心页面
				intent.putExtra("userName", user.getName());
				intent.putExtra("userid", String.valueOf(user.getId()));
				context.startActivity(intent);
			}
		});
		
		//用户本条微博发表时间+来源的内容设置
		holder.tv_caption.setText(DateUtils.getShortTime(status.getCreated_at()) +
				"  来自" + Html.fromHtml(status.getSource()));
		
		
		//微博正文信息的设置，并完成可点击文字和表情的处理
		SpannableString weiboContent = StringUtils.getWeiboContent(
				context, holder.tv_content, status.getText());
		holder.tv_content.setText(weiboContent);

		//微博单图或多图的设置
		setImages(status, holder.include_status_image, holder.gv_images, holder.iv_image);

		/*
		 * 处理引用微博部分
		 */
		final Status retweetedStatus = status.getRetweeted_status();
		if (retweetedStatus != null) {
			//如果应用微博部分非空，就显示引用微博控件
			holder.include_retweeted_status.setVisibility(View.VISIBLE);
			//判断引用微博是否为空，不为空就设置用户名称
			String rStatusUser = retweetedStatus.getUser() == null ?
					"" : "@" + retweetedStatus.getUser().getName() + ":";
			/* 引用微博分为两部分
			 * 前面为rStatusUser后面是引用微博文本内容retweetedStatus.getText()
			 */
			String retweetContent = rStatusUser + retweetedStatus.getText();
			
			//将引用微博文本内容显示到控件上，并完成可点击文字和表情的处理
			SpannableString retweetWeiboContent = StringUtils.getWeiboContent(
					context, holder.tv_retweeted_content, retweetContent);
			holder.tv_retweeted_content.setText(retweetWeiboContent);
			
			//显示引用微博图片部分，使用封装好的方法
			setImages(retweetedStatus, holder.include_retweeted_status_image,
					holder.gv_retweeted_images, holder.iv_retweeted_image);
			//对整个引用微博组件，设置点击事件
			holder.include_retweeted_status.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(context, StatusDetailActivity.class);
					intent.putExtra("status", retweetedStatus);
					context.startActivity(intent);
				}
			});
		} else {
			/*
			 * 如果为空则设置为不显示
			 */
			holder.include_retweeted_status.setVisibility(View.GONE);
		}

		/*
		 * 操作栏的转发，评论，赞的人数
		 * 有数值就显示数值，没有就显示对应文字
		 */
		//转发栏文本显示
		holder.tv_share_bottom.setText(status.getReposts_count() == 0 ?
				"转发" : status.getReposts_count() + "");
		//转发栏点击事件
		holder.ll_share_bottom.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(context, WriteStatusActivity.class);
				intent.putExtra("status", status);
				context.startActivity(intent);
			}
		});

		//评论栏文本设置
		holder.tv_comment_bottom.setText(status.getComments_count() == 0 ?
				"评论" : status.getComments_count() + "");
		//评论栏点击事件
		holder.ll_comment_bottom.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(status.getComments_count() > 0) {
					Intent intent = new Intent(context, StatusDetailActivity.class);
					intent.putExtra("status", status);
					/**
					 * 跳转到微博详情界面时，自动将列表滑动到评论部分
					 * listview.setSelection()方法定位到某个位置
					 */
					intent.putExtra("scroll2Comment", true);
					context.startActivity(intent);
				} else {
					Intent intent = new Intent(context, WriteCommentActivity.class);
					intent.putExtra("status", status);
					context.startActivity(intent);
				}
			}
		});

		//赞栏更改此按钮的选中状态。
		holder.cb_like_bottom.setChecked(status.isLiked());
		//赞栏文本显示
		holder.tv_like_bottom.setText(status.getAttitudes_count() == 0 ?
				"赞" : status.getAttitudes_count() + "");
		//赞栏点击事件
		holder.ll_like_bottom.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				final ScaleAnimation scaleAnimation2 = new ScaleAnimation(1.5f, 1f, 1.5f, 1f,
						Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
				scaleAnimation2.setDuration(150);

				ScaleAnimation scaleAnimation1 = new ScaleAnimation(1f, 1.5f, 1f, 1.5f,
						Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
				scaleAnimation1.setDuration(200);
				scaleAnimation1.setAnimationListener(new AnimationListener() {
					@Override
					public void onAnimationStart(Animation animation) {

					}

					@Override
					public void onAnimationRepeat(Animation animation) {

					}

					@Override
					public void onAnimationEnd(Animation animation) {
						holder.cb_like_bottom.setChecked(!holder.cb_like_bottom.isChecked());
						holder.cb_like_bottom.setAnimation(scaleAnimation2);
					}
				});
				holder.cb_like_bottom.setAnimation(scaleAnimation1);
				
				try{
					BoreWeiboAPI weiboApi=new BoreWeiboAPI(context);
					weiboApi.likeCreat(new SimpleRequestListener(context, null) {
						@Override
						public void onComplete(String response) {
							super.onComplete(response);
							Toast.makeText(context, "赞成功", Toast.LENGTH_LONG).show();;
							Log.i("like _song1111", "lick_song11111");
						}
					});
					
					//赞栏文本显示
					holder.tv_like_bottom.setText(status.getAttitudes_count() == 0 ?
							"赞" : status.getAttitudes_count() + "");
				}catch(Exception e){
					Toast.makeText(context, e.toString()+"宋建", Toast.LENGTH_SHORT).show();
				}

				
			}
		});

		//点击整个微博item跳转到StatusDetialActivity微博详情洁面 
		holder.ll_card_content.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(context, StatusDetailActivity.class);
				intent.putExtra("status", status);
				context.startActivity(intent);
			}
		});

		return convertView;
	}

	//显示图片的方法设置
	private void setImages(final Status status, ViewGroup vgContainer, GridView gvImgs, final ImageView ivImg) {
		if (status == null) {
			return;
		}
		//获取多图集合Url
		ArrayList<PicUrls> picUrls = status.getPic_urls();
		//获取单图Url
		String picUrl = status.getBmiddle_pic();
		//判断单图和多图，如果没有图片则将整个item设置为gone
		if (picUrls != null && picUrls.size() == 1) {
			
			vgContainer.setVisibility(View.VISIBLE);
			gvImgs.setVisibility(View.GONE);
			ivImg.setVisibility(View.VISIBLE);
			//单图就显示ivImg
			imageLoader.displayImage(picUrl, ivImg);
			//点击跳转到ImageBrowserActivity中
			ivImg.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(context, ImageBrowserActivity.class);
					intent.putExtra("status", status);
					intent.putExtra("position", -1);
					context.startActivity(intent);
				}
			});
		} else if (picUrls != null && picUrls.size() > 1) {
			vgContainer.setVisibility(View.VISIBLE);
			gvImgs.setVisibility(View.VISIBLE);
			ivImg.setVisibility(View.GONE);

			//context参数，数据集合，需要显示到的控件
			StatusGridImgsAdapter imagesAdapter = new StatusGridImgsAdapter(
					context, picUrls, gvImgs);
			//设置Adapter
			gvImgs.setAdapter(imagesAdapter);

			gvImgs.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					Intent intent = new Intent(context, ImageBrowserActivity.class);
					intent.putExtra("status", status);
					intent.putExtra("position", position);
					context.startActivity(intent);
				}
			});
		} else {
			vgContainer.setVisibility(View.GONE);
		}
	}

	public static class ViewHolder {
		public LinearLayout ll_card_content;
		public ImageView iv_avatar;
		public RelativeLayout rl_content;
		public TextView tv_subhead;
		public TextView tv_caption;

		public TextView tv_content;
		public FrameLayout include_status_image;
		public GridView gv_images;
		public ImageView iv_image;

		public LinearLayout include_retweeted_status;
		public TextView tv_retweeted_content;
		public FrameLayout include_retweeted_status_image;
		public GridView gv_retweeted_images;
		public ImageView iv_retweeted_image;

		public LinearLayout ll_share_bottom;
		public ImageView iv_share_bottom;
		public TextView tv_share_bottom;
		public LinearLayout ll_comment_bottom;
		public ImageView iv_comment_bottom;
		public TextView tv_comment_bottom;
		public LinearLayout ll_like_bottom;
		public CheckBox cb_like_bottom;
		public TextView tv_like_bottom;
	}

}
