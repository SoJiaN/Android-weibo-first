package com.song.sjweibo.adapter;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.text.SpannableString;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.song.sjweibo.R;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.song.sjweibo.activity.UserInfoActivity;
import com.song.sjweibo.entity.Comment;
import com.song.sjweibo.entity.User;
import com.song.sjweibo.utils.DateUtils;
import com.song.sjweibo.utils.StringUtils;
/**
 * 设置单条微博评论的样式
 * @param item_comment.xml
 * 
 * @param ll_comments	整条微博的线性布局
 * @param iv_avatar 	头像
 * @param tv_name		评论人名称
 * @param tv_time		评论时间
 * @param tv_comment	评论内容
 * @author lenovo
 *
 */
public class StatusCommentAdapter extends BaseAdapter {
	
	private Context context;
	private List<Comment> comments;
	private ImageLoader imageLoader;

	public StatusCommentAdapter(Context context, List<Comment> comments) {
		this.context = context;
		this.comments = comments;
		this.imageLoader = ImageLoader.getInstance();
	}
	
	@Override
	public int getCount() {
		return comments.size();
	}

	@Override
	public Comment getItem(int position) {
		return comments.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolderList holder;
		if (convertView == null) {
			holder = new ViewHolderList();
			convertView = View.inflate(context, R.layout.item_comment, null);
			holder.ll_comments = (LinearLayout) convertView
					.findViewById(R.id.ll_comments);
			holder.iv_avatar = (ImageView) convertView
					.findViewById(R.id.iv_avatar);
			holder.tv_name = (TextView) convertView
					.findViewById(R.id.tv_name);
			holder.tv_time = (TextView) convertView
					.findViewById(R.id.tv_time);
			holder.tv_comment = (TextView) convertView
					.findViewById(R.id.tv_comment);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolderList) convertView.getTag();
		}

		// 得到用户信息内容
		Comment comment = getItem(position);
		final User user = comment.getUser();
		//整个评论栏的处理
		//头像点击事件
		imageLoader.displayImage(user.getProfile_image_url(), holder.iv_avatar);
		holder.iv_avatar.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(context, UserInfoActivity.class);
				intent.putExtra("userName", user.getName());
				intent.putExtra("userid", user.getIdstr());
				context.startActivity(intent);
			}
		});
		//用户名称
		holder.tv_name.setText(user.getName());
		holder.tv_name.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(context, UserInfoActivity.class);
				intent.putExtra("userName", user.getName());
				intent.putExtra("userid", user.getIdstr());
				context.startActivity(intent);
			}
		});
		//本条微博评论发送时间
		holder.tv_time.setText(DateUtils.getDateTime(comment.getCreated_at()));
		//本条微博评论内容
		SpannableString weiboContent = StringUtils.getWeiboContent(
				context, holder.tv_comment, comment.getText());
		holder.tv_comment.setText(weiboContent);
		
		holder.ll_comments.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				
			}
		});
		
		return convertView;
	}
	
	public static class ViewHolderList {
		public LinearLayout ll_comments;
		public ImageView iv_avatar;
		public TextView tv_name;
		public TextView tv_time;
		public TextView tv_comment;
	}

}
