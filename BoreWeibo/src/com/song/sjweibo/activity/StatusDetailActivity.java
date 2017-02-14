package com.song.sjweibo.activity;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.SpannableString;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

import com.song.sjweibo.R;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnLastItemVisibleListener;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.song.sjweibo.BaseActivity;
import com.song.sjweibo.adapter.StatusCommentAdapter;
import com.song.sjweibo.adapter.StatusGridImgsAdapter;
import com.song.sjweibo.api.SimpleRequestListener;
import com.song.sjweibo.entity.Comment;
import com.song.sjweibo.entity.PicUrls;
import com.song.sjweibo.entity.Status;
import com.song.sjweibo.entity.User;
import com.song.sjweibo.entity.response.CommentsResponse;
import com.song.sjweibo.utils.DateUtils;
import com.song.sjweibo.utils.StringUtils;
import com.song.sjweibo.utils.TitleBuilder;
import com.song.sjweibo.widget.WrapHeightGridView;
/**
 * @param 
 * 主页面-------layout.activity_status_detail.xml
 * 1.include_titlebar.xml-------include------使用顶部标题栏
 * 2."@+id/rl_body"-------RelativeLayout 
 *		2.1 "@+id/plv_status_detail"-------com.song.sjweibo.widget.Pull2RefreshListView----存放他的评论列表信息
 *		微博信息和微博信息下的操作栏(菜单栏)将以headview的形式添加到里面，并随着这个列表的滚动而滚动
 *		2.2 "@+id/status_detail_tab"-------include------需要悬浮的操作栏(菜单栏)样式，在xml文件中设置为gone		
 * 3. include_status_controlbar.xml------include------为底部操作栏布局，转发评论赞列表，和HomeFragment中的底部操作栏相同，不同于微博信息下的和需要悬浮的操作栏
 * @author lenovo
 * 每个微博的详情界面
 */
public class StatusDetailActivity extends BaseActivity implements
		OnClickListener, OnCheckedChangeListener {
	// 跳转到写评论页面code
	private static final int REQUEST_CODE_WRITE_COMMENT = 2333;

	// listView headerView - 微博信息
	private View status_detail_info;
	private ImageView iv_avatar;
	private TextView tv_subhead;
	private TextView tv_caption;
	private FrameLayout include_status_image;
	private WrapHeightGridView gv_images;
	private ImageView iv_image;
	private TextView tv_content;
	private View include_retweeted_status;
	private TextView tv_retweeted_content;
	private FrameLayout fl_retweeted_imageview;
	private GridView gv_retweeted_images;
	private ImageView iv_retweeted_image;
	// shadow_tab - 顶部悬浮的菜单栏
	private View shadow_status_detail_tab;
	private RadioGroup shadow_rg_status_detail;
	private RadioButton shadow_rb_retweets;
	private RadioButton shadow_rb_comments;
	private RadioButton shadow_rb_likes;
	// listView headerView - 添加至列表中作为header的菜单栏，它随着滚动而移动
	private View status_detail_tab;
	private RadioGroup rg_status_detail;
	private RadioButton rb_retweets;
	private RadioButton rb_comments;
	private RadioButton rb_likes;
	// listView - 下拉刷新控件
	private PullToRefreshListView plv_status_detail;
	// footView - 加载更多
	private View footView;
	// bottom_control - 底部互动栏,包括转发/评论/点赞
	private LinearLayout ll_bottom_control;
	private LinearLayout ll_share_bottom;
	private TextView tv_share_bottom;
	private LinearLayout ll_comment_bottom;
	private TextView tv_comment_bottom;
	private LinearLayout ll_like_bottom;
	private TextView tv_like_bottom;

	// 详情页的微博信息
	private Status status;
	// 是否需要滚动至评论部分
	private boolean scroll2Comment;
	// 评论当前已加载至的页数
	private long curPage = 1;
	
	private List<Comment> comments = new ArrayList<Comment>();
	private StatusCommentAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_status_detail);

		// 获取intent传入的信息
		status = (Status) getIntent().getSerializableExtra("status");
		scroll2Comment = getIntent().getBooleanExtra("scroll2Comment", false);

		// 初始化View
		initView();

		// 设置数据信息
		setData();
		
		// 开始加载第一页评论数据
		addFootView(plv_status_detail, footView);
		loadComments(1);
	}

	private void initView() {
		// title - 标题栏
		initTitle();
		// listView headerView - 微博信息 
		initDetailHead();
		// tab - 菜单栏
		initTab();
		// listView - 下拉刷新控件
		initListView();
		// bottom_control - 底部互动栏,包括转发/评论/点赞
		initControlBar();
	}

	private void initTitle() {
		new TitleBuilder(this)
			.setTitleText("微博正文")
			.setLeftImage(R.drawable.navigationbar_back_sel)
			.setLeftOnClickListener(this);
	}

	private void initDetailHead() {
		/** 得到整个微博item样式，将背景设置为白色，由于详情页面单独添加操作栏，所以将底部操作栏设置为不显示
		 *  将整个原item设置为headview
		 */
		status_detail_info = View.inflate(this, R.layout.item_status, null);
		status_detail_info.setBackgroundResource(R.color.white);
		status_detail_info.findViewById(R.id.ll_bottom_control).setVisibility(View.GONE);
		
		iv_avatar = (ImageView) status_detail_info.findViewById(R.id.iv_avatar);
		tv_subhead = (TextView) status_detail_info.findViewById(R.id.tv_subhead);
		tv_caption = (TextView) status_detail_info.findViewById(R.id.tv_caption);
		include_status_image = (FrameLayout) status_detail_info.findViewById(R.id.include_status_image);
		gv_images = (WrapHeightGridView) status_detail_info.findViewById(R.id.gv_images);
		iv_image = (ImageView) status_detail_info.findViewById(R.id.iv_image);
		tv_content = (TextView) status_detail_info.findViewById(R.id.tv_content);
		include_retweeted_status = status_detail_info.findViewById(R.id.include_retweeted_status);
		tv_retweeted_content = (TextView) status_detail_info.findViewById(R.id.tv_retweeted_content);
		fl_retweeted_imageview = (FrameLayout) include_retweeted_status.findViewById(R.id.include_status_image);
		gv_retweeted_images = (GridView) fl_retweeted_imageview.findViewById(R.id.gv_images);
		iv_retweeted_image = (ImageView) fl_retweeted_imageview.findViewById(R.id.iv_image);
		iv_image.setOnClickListener(this);
	}

	private void initTab() {
		/** shadow，微博item的显示layout，微博正文listview显示的地方
		 *  这里用于得到隐藏菜单栏的控件
		 */
		shadow_status_detail_tab = findViewById(R.id.status_detail_tab);
		
		
		/**
		 * 这里是隐藏的悬浮菜单栏
		 */
		//操作栏 没有图片的
		shadow_rg_status_detail = (RadioGroup) shadow_status_detail_tab
				.findViewById(R.id.rg_status_detail);
		//微博详情页面，转发栏 没有图片的
		shadow_rb_retweets = (RadioButton) shadow_status_detail_tab
				.findViewById(R.id.rb_retweets);
		shadow_rb_comments = (RadioButton) shadow_status_detail_tab
				.findViewById(R.id.rb_comments);
		shadow_rb_likes = (RadioButton) shadow_status_detail_tab
				.findViewById(R.id.rb_likes);
		shadow_rg_status_detail.setOnCheckedChangeListener(this);
		
		
		
		/** header  headview的初始化，之后将这个view使用addheadview方法加入到总布局中
		 *  原本就有菜单栏
		 */
		status_detail_tab = View.inflate(this, R.layout.status_detail_tab, null);
		rg_status_detail = (RadioGroup) status_detail_tab
				.findViewById(R.id.rg_status_detail);
		rb_retweets = (RadioButton) status_detail_tab
				.findViewById(R.id.rb_retweets);
		rb_comments = (RadioButton) status_detail_tab
				.findViewById(R.id.rb_comments);
		rb_likes = (RadioButton) status_detail_tab
				.findViewById(R.id.rb_likes);
		rg_status_detail.setOnCheckedChangeListener(this);
		
		
	}

	//初始化下拉刷新控件，并通过listview.addHeadview的方式将微博详情页和底部操作栏(菜单栏)加载到视图中
	private void initListView() {
		// listView - 下拉刷新控件
		plv_status_detail = (PullToRefreshListView) findViewById(R.id.plv_status_detail);
		adapter = new StatusCommentAdapter(this, comments);
		plv_status_detail.setAdapter(adapter);
		
		// footView - 加载更多
		footView = View.inflate(this, R.layout.footview_loading, null);
		
		/* Refresh View - ListView
		 * 获取包装的可刷新视图。此处返回的任何内容已添加到内容视图中。
		 */
		final ListView lv = plv_status_detail.getRefreshableView();
		
		//将微博详情页和底部操作栏加载到视图中
		lv.addHeaderView(status_detail_info);
		lv.addHeaderView(status_detail_tab);
		
		// 下拉刷新监听
		plv_status_detail.setOnRefreshListener(new OnRefreshListener<ListView>() {

			@Override
			public void onRefresh(PullToRefreshBase<ListView> refreshView) {
				loadComments(1);
			}
		});
		
		// 滑动到底部最后一个item监听
		plv_status_detail.setOnLastItemVisibleListener(
				new OnLastItemVisibleListener() {
					@Override
					public void onLastItemVisible() {
						loadComments(curPage + 1);
					}
				});
		
		
		// 滚动状态监听
		plv_status_detail.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {

			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
				/** 
				 *  0-pullHead-----刷新头
				 *  1-detailHead---详细的微博   
				 *  2-tab----------底部菜单栏
				 */
				// 如果滑动到tab为第一个item时,则显示顶部隐藏的shadow_tab,作为悬浮菜单栏
				shadow_status_detail_tab.setVisibility(firstVisibleItem >= 2 ?
						View.VISIBLE : View.GONE);
			}
		});
	}

	private void initControlBar() {
		ll_bottom_control = (LinearLayout) findViewById(R.id.status_detail_controlbar);
		ll_share_bottom = (LinearLayout) ll_bottom_control.findViewById(R.id.ll_share_bottom);
		tv_share_bottom = (TextView) ll_bottom_control.findViewById(R.id.tv_share_bottom);
		ll_comment_bottom = (LinearLayout) ll_bottom_control.findViewById(R.id.ll_comment_bottom);
		tv_comment_bottom = (TextView) ll_bottom_control.findViewById(R.id.tv_comment_bottom);
		ll_like_bottom = (LinearLayout) ll_bottom_control.findViewById(R.id.ll_like_bottom);
		tv_like_bottom = (TextView) ll_bottom_control.findViewById(R.id.tv_like_bottom);
		ll_bottom_control.setBackgroundResource(R.color.white);
		ll_share_bottom.setOnClickListener(this);
		ll_comment_bottom.setOnClickListener(this);
		ll_like_bottom.setOnClickListener(this);
	}

	private void setData() {
		// listView headerView - 微博信息
		User user = status.getUser();
		imageLoader.displayImage(user.getProfile_image_url(), iv_avatar);
		tv_subhead.setText(user.getName());
		tv_caption.setText(DateUtils.getShortTime(status.getCreated_at()) +
				"  来自" + Html.fromHtml(status.getSource()));
		//微博图片
		setImages(status, include_status_image, gv_images, iv_image);
		//设置@用户#话题和表情，
		if (TextUtils.isEmpty(status.getText())) {
			tv_content.setVisibility(View.GONE);
		} else {
			tv_content.setVisibility(View.VISIBLE);
			SpannableString weiboContent = StringUtils.getWeiboContent(
					this, tv_content, status.getText());
			tv_content.setText(weiboContent);
		}

		//设置转发部分的微博详情和图片显示
		Status retweetedStatus = status.getRetweeted_status();
		if (retweetedStatus != null) {
			include_retweeted_status.setVisibility(View.VISIBLE);
			String retweetContent = "@" + retweetedStatus.getUser().getName()
					+ ":" + retweetedStatus.getText();
			SpannableString weiboContent = StringUtils.getWeiboContent(
					this, tv_retweeted_content, retweetContent);
			tv_retweeted_content.setText(weiboContent);
			setImages(retweetedStatus, fl_retweeted_imageview,
					gv_retweeted_images, iv_retweeted_image);
		} else {
			include_retweeted_status.setVisibility(View.GONE);
		}
		
		// shadow_tab - 顶部悬浮的操作栏(菜单栏)
		shadow_rb_retweets.setText("转发 " + status.getReposts_count());
		shadow_rb_comments.setText("评论 " + status.getComments_count());
		shadow_rb_likes.setText("赞 " + status.getAttitudes_count());
		// listView headerView - 添加至列表中作为header的菜单栏
		rb_retweets.setText("转发 " + status.getReposts_count());
		rb_comments.setText("评论 " + status.getComments_count());
		rb_likes.setText("赞 " + status.getAttitudes_count());
		
		// bottom_control - 底部操作栏(菜单栏),包括转发+图片/评论+图片/点赞+图片
		tv_share_bottom.setText(status.getReposts_count() == 0 ?
				"转发" : status.getReposts_count() + "");
		tv_comment_bottom.setText(status.getComments_count() == 0 ?
				"评论" : status.getComments_count() + "");
		tv_like_bottom.setText(status.getAttitudes_count() == 0 ?
				"赞" : status.getAttitudes_count() + "");
	}

	private void setImages(final Status status, ViewGroup vgContainer, GridView gvImgs, final ImageView ivImg) {
		if (status == null) {
			return;
		}

		ArrayList<PicUrls> picUrls = status.getPic_urls();
		String picUrl = status.getBmiddle_pic();

		if (picUrls != null && picUrls.size() == 1) {
			vgContainer.setVisibility(View.VISIBLE);
			gvImgs.setVisibility(View.GONE);
			ivImg.setVisibility(View.VISIBLE);

			imageLoader.displayImage(picUrl, ivImg);
		} else if (picUrls != null && picUrls.size() > 1) {
			vgContainer.setVisibility(View.VISIBLE);
			gvImgs.setVisibility(View.VISIBLE);
			ivImg.setVisibility(View.GONE);

			StatusGridImgsAdapter imagesAdapter = new StatusGridImgsAdapter(this, picUrls, gvImgs);
			gvImgs.setAdapter(imagesAdapter);
		} else {
			vgContainer.setVisibility(View.GONE);
		}
	}

	/**
	 * 根据微博ID返回某条微博的评论列表
	 * 
	 * @param requestPage
	 *            页数
	 */
	private void loadComments(final long requestPage) {
		weiboApi.commentsShow(status.getId(), requestPage,
				new SimpleRequestListener(this, null) {

					@Override
					public void onComplete(String response) {
						super.onComplete(response);
						
						showLog("status comments = " + response);
						
						//如果是加载第一页(第一次进入,下拉刷新)时,先清空已有数据
						if (requestPage == 1) {
							comments.clear();
						}
						
						// 解析返回数据 string - > object
						CommentsResponse commentsResponse = gson.fromJson(response, CommentsResponse.class);
						
						// 更新评论数信息
						tv_comment_bottom.setText(commentsResponse.getTotal_number() == 0 ? 
								"评论" : commentsResponse.getTotal_number() + "");
						shadow_rb_comments.setText("评论 " + commentsResponse.getTotal_number());
						rb_comments.setText("评论 " + commentsResponse.getTotal_number());
						
						// 将获取的评论信息添加到列表上
						addData(commentsResponse);
						
						// 判断是否需要滚动至评论部分
						if(scroll2Comment) {
							plv_status_detail.getRefreshableView().setSelection(2);
							scroll2Comment = false;
						}
					}

					@Override
					public void onAllDone() {
						super.onAllDone();
						
						// 通知下拉刷新控件完成刷新
						plv_status_detail.onRefreshComplete();
					}

				});
	}

	private void addData(CommentsResponse response) {
		// 将获取到的数据添加至列表中,重复数据不添加
		for (Comment comment : response.getComments()) {
			if (!comments.contains(comment)) {
				comments.add(comment);
			}
		}
		// 添加完后,通知ListView刷新页面数据
		adapter.notifyDataSetChanged();
		
		// 用条数判断,当前评论数是否达到总评论数,未达到则添加更多加载footView,反之移除
		if (comments.size() < response.getTotal_number()) {
			addFootView(plv_status_detail, footView);
		} else {
			removeFootView(plv_status_detail, footView);
//			//通知下拉刷新控件已经完成刷新
//			plv_status_detail.onRefreshComplete();
		}
	}

	private void addFootView(PullToRefreshListView plv, View footView) {
		ListView lv = plv.getRefreshableView();
		if (lv.getFooterViewsCount() == 1) {
			lv.addFooterView(footView);
		}
	}

	private void removeFootView(PullToRefreshListView plv, View footView) {
		ListView lv = plv.getRefreshableView();
		if (lv.getFooterViewsCount() > 1) {
			lv.removeFooterView(footView);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.titlebar_iv_left:
			StatusDetailActivity.this.finish();
			break;
		case R.id.iv_image:
			break;
		case R.id.ll_share_bottom:
			//跳转是转发界面
			Intent intentWriteStatus = new Intent(this, WriteStatusActivity.class);
			intentWriteStatus.putExtra("status", status);
			startActivity(intentWriteStatus);
			break;
		case R.id.ll_comment_bottom:
			// 跳转至写评论页面
			Intent intent = new Intent(this, WriteCommentActivity.class);
			intent.putExtra("status", status);
			startActivityForResult(intent, REQUEST_CODE_WRITE_COMMENT);
			break;
		case R.id.ll_like_bottom:
			break;

		default:
			break;
		}
	}

	//选中事件监听
	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {

		// 更新tab菜单栏某个选项时
		switch (checkedId) {
		case R.id.rb_retweets:
			//注意header的操作栏和shadow操作栏的选中状态同步，当控件被选中是更新连个
			//转发栏的点击事件
			rb_retweets.setChecked(true);
			//微博详情页面，转发栏
			shadow_rb_retweets.setChecked(true);
			break;
		case R.id.rb_comments:
			rb_comments.setChecked(true);
			shadow_rb_comments.setChecked(true);
			break;
		case R.id.rb_likes:
			rb_likes.setChecked(true);
			shadow_rb_likes.setChecked(true);
			break;
		default:
			break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		// 如果Back键返回,取消发评论等情况,则直接return,不做后续处理
		if(resultCode == RESULT_CANCELED) {
			return;
		}
		switch (requestCode) {
		case REQUEST_CODE_WRITE_COMMENT:
			// 如果是评论发送成功的返回结果,则重新加载最新评论,同时要求滚动至评论部分
			boolean sendCommentSuccess = data.getBooleanExtra("sendCommentSuccess", false);
			if(sendCommentSuccess) {
				scroll2Comment = true;
				loadComments(1);
			}
			break;

		default:
			break;
		}
	}
	
}
