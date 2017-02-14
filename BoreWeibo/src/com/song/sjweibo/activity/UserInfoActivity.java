package com.song.sjweibo.activity;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLayoutChangeListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

import com.song.sjweibo.R;
import com.google.gson.Gson;
import com.handmark.pulltorefresh.library.ILoadingLayout;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnLastItemVisibleListener;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;
import com.song.sjweibo.BaseActivity;
import com.song.sjweibo.adapter.StatusAdapter;
import com.song.sjweibo.api.SimpleRequestListener;
import com.song.sjweibo.constants.AccessTokenKeeper;
import com.song.sjweibo.entity.Status;
import com.song.sjweibo.entity.User;
import com.song.sjweibo.entity.response.StatusTimeLineResponse;
import com.song.sjweibo.utils.ImageOptHelper;
import com.song.sjweibo.utils.TitleBuilder;
import com.song.sjweibo.widget.Pull2RefreshListView;
import com.song.sjweibo.widget.UnderlineIndicatorView;
import com.song.sjweibo.widget.Pull2RefreshListView.OnPlvScrollListener;
/**
 * StatusAdapter类中点击一条微博item 用户头像跳转到此类
 * StatusAdapter类中点击一条微博item 用户姓名跳转到此类
 * StringUtils类中点击点击	"@用户名"部分跳转到此类
 * @author lenovo
 *
 */
public class UserInfoActivity extends BaseActivity implements 
	OnClickListener, OnItemClickListener, OnCheckedChangeListener {
	
	// 
	private View title;
	private ImageView titlebar_iv_left;
	private TextView titlebar_tv;
	
	private View user_info_head;
	private ImageView iv_avatar;
	private TextView tv_name;
	private TextView tv_follows;
	private TextView tv_fans;
	private TextView tv_sign;
	
	private View shadow_user_info_tab;
	private RadioGroup shadow_rg_user_info;
	private UnderlineIndicatorView shadow_uliv_user_info;
	private View user_info_tab;
	private RadioGroup rg_user_info;
	private UnderlineIndicatorView uliv_user_info;
	
	private ImageView iv_user_info_head;
	private Pull2RefreshListView plv_user_info;
	private View footView;
	
	private boolean isCurrentUser;
	private User user;
	private String userName;
	private String userid;
	
	private List<Status> statuses = new ArrayList<Status>();
	private StatusAdapter statusAdapter;
	private long curPage = 1;
	private boolean isLoadingMore;
	private int curScrollY;
	
	private int minImageHeight = -1;
	private int maxImageHeight = -1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_user_info);

		userid=intent.getStringExtra("userid");
		userName = intent.getStringExtra("userName");
		if(TextUtils.isEmpty(userName)) {
			isCurrentUser = true;
			user = application.currentUser;
		}

		initView();
		
		loadData();
	}

	private void initView() {
		title = new TitleBuilder(this)
			.setTitleBgRes(R.drawable.userinfo_navigationbar_background)
			.setLeftImage(R.drawable.navigationbar_back_sel)
			.setLeftOnClickListener(this)
			.build();
		//获取标题栏信息，需要时进行修改
		titlebar_iv_left = (ImageView) title.findViewById(R.id.titlebar_iv_left);
		titlebar_tv = (TextView) title.findViewById(R.id.titlebar_tv);
		
		initInfoHead();
		initTab();
		initListView();
	}

	private void initInfoHead() {
		iv_user_info_head = (ImageView) findViewById(R.id.iv_user_info_head);
		
		user_info_head = View.inflate(this, R.layout.user_info_head, null);
		iv_avatar = (ImageView) user_info_head.findViewById(R.id.iv_avatar);
		tv_name = (TextView) user_info_head.findViewById(R.id.tv_name);
		tv_follows = (TextView) user_info_head.findViewById(R.id.tv_follows);
		tv_fans = (TextView) user_info_head.findViewById(R.id.tv_fans);
		tv_sign = (TextView) user_info_head.findViewById(R.id.tv_sign);
		
		iv_avatar.setOnClickListener(this);
	}

	private void initTab() {
		//悬浮显示的菜单栏
		shadow_user_info_tab = findViewById(R.id.user_info_tab);
		shadow_rg_user_info = (RadioGroup) findViewById(R.id.rg_user_info);
		shadow_uliv_user_info = (UnderlineIndicatorView) findViewById(R.id.uliv_user_info);
		
		shadow_rg_user_info.setOnCheckedChangeListener(this);
		//初始化时选中微博的位置，对应的选项为1。不需要动画
		shadow_uliv_user_info.setCurrentItemWithoutAnim(1);
		
		//添加到列表中的菜单栏
		user_info_tab = View.inflate(this, R.layout.user_info_tab, null);
		rg_user_info = (RadioGroup) user_info_tab.findViewById(R.id.rg_user_info);
		uliv_user_info = (UnderlineIndicatorView) user_info_tab.findViewById(R.id.uliv_user_info);
		
		rg_user_info.setOnCheckedChangeListener(this);
		//初始化时选中微博的位置，对应的选项为1。不需要动画
		uliv_user_info.setCurrentItemWithoutAnim(1);
	}
	
	@SuppressLint("NewApi")
	private void initListView() {
		plv_user_info = (Pull2RefreshListView) findViewById(R.id.plv_user_info);
		//设置下拉刷新控件样式
		initLoadingLayout(plv_user_info.getLoadingLayoutProxy());
		
		footView = View.inflate(this, R.layout.footview_loading, null);
		final ListView lv = plv_user_info.getRefreshableView();
		statusAdapter = new StatusAdapter(this, statuses);
		plv_user_info.setAdapter(statusAdapter);
		//添加用户信息
		lv.addHeaderView(user_info_head);
		//添加菜单栏部分
		lv.addHeaderView(user_info_tab);
		//添加下拉刷新回调
		plv_user_info.setOnRefreshListener(new OnRefreshListener<ListView>() {

			@Override
			public void onRefresh(PullToRefreshBase<ListView> refreshView) {
				loadStatuses(1);
			}
		});
		//添加底部item回调
		plv_user_info.setOnLastItemVisibleListener(
				new OnLastItemVisibleListener() {

					@Override
					public void onLastItemVisible() {
						loadStatuses(curPage + 1);
					}
				});
		
		plv_user_info.setOnPlvScrollListener(new OnPlvScrollListener() {
			
			@Override
			public void onScrollChanged(int l, int t, int oldl, int oldt) {
				int scrollY = curScrollY = t;
				
				//如果是-1刚开始，就进行一次获取，否则没必要重复获取
				if(minImageHeight == -1) {
					minImageHeight = iv_user_info_head.getHeight();
				}
				
				if(maxImageHeight == -1) {
					//获取图片高度
					Rect rect = iv_user_info_head.getDrawable().getBounds();
					maxImageHeight = rect.bottom - rect.top;
				}
//				minImageHeight = DisplayUtils.dp2px(UserInfoActivity.this, 244);
//				maxImageHeight = DisplayUtils.dp2px(UserInfoActivity.this, 360);
				
				int scaleImageDistance = maxImageHeight - minImageHeight;
				
				//重新规定控件的位置以及size大小
				if(-scrollY < scaleImageDistance) {
					//图片没有显示完全
					iv_user_info_head.layout(0, 0, 
							iv_user_info_head.getWidth(), 
							minImageHeight - scrollY);
				} else {
					iv_user_info_head.layout(0, - scaleImageDistance - scrollY, 
							iv_user_info_head.getWidth(), 
							- scaleImageDistance - scrollY + iv_user_info_head.getHeight());
				}
			}
		});
		iv_user_info_head.addOnLayoutChangeListener(new OnLayoutChangeListener() {
			@Override
			public void onLayoutChange(View v, int left, int top, int right, int bottom, 
					int oldLeft, int oldTop, int oldRight, int oldBottom) {
				if(curScrollY == bottom - oldBottom) {
					iv_user_info_head.layout(0, 0, 
							iv_user_info_head.getWidth(), 
							oldBottom);
				}
			}
		});
		
		//监听listView的滚动事件
		plv_user_info.setOnScrollListener(new OnScrollListener() {
			
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				
			}
			
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
				iv_user_info_head.layout(0,
						user_info_head.getTop(), 
						iv_user_info_head.getWidth(), 
						user_info_head.getTop() + iv_user_info_head.getHeight());
				//如果菜单栏部分移动到顶部的时候，就将visible
				if(user_info_head.getBottom() < title.getBottom()) {
					shadow_user_info_tab.setVisibility(View.VISIBLE);
					//修改标题栏
					title.setBackgroundResource(R.drawable.navigationbar_background);
					titlebar_iv_left.setImageResource(R.drawable.navigationbar_back_sel);
					titlebar_tv.setVisibility(View.VISIBLE);
				} else {
					shadow_user_info_tab.setVisibility(View.GONE);
					title.setBackgroundResource(R.drawable.userinfo_navigationbar_background);
					titlebar_iv_left.setImageResource(R.drawable.userinfo_navigationbar_back_sel);
					titlebar_tv.setVisibility(View.GONE);
				}
			}
		});
	}

	private void initLoadingLayout(ILoadingLayout loadingLayout) {
		//将一个颜色设置为drawable对象
		loadingLayout.setLoadingDrawable(new ColorDrawable(R.color.transparent));
		loadingLayout.setPullLabel("");
		loadingLayout.setReleaseLabel("");
		loadingLayout.setRefreshingLabel("");
	}

	private void loadData() {
		if(isCurrentUser) {
			// 如果是当前授权用户,直接设置信息
			setUserInfo();
			loadStatuses(1);
		} else {
			
			loadS_friendships_create();
			// 如果是查看他人,调用获取用户信息接口
			loadUserInfo();
			setUserInfo();
//			loadStatus_home(1);
//			loadStatus_mention(1);
//			loadstatus_timeline_batch(1);
			load_bilateral_timeline(1);
		}
		// 加载用户所属微博列表
//		loadStatuses(1);
	}

	private void setUserInfo() {
		if(user == null) {
			return;
		}
		// set data
		tv_name.setText(user.getName());
		titlebar_tv.setText(user.getName());
		imageLoader.displayImage(user.getAvatar_large(), new ImageViewAware(iv_avatar),
				ImageOptHelper.getAvatarOptions());
		tv_follows.setText("关注 " + user.getFriends_count());
		tv_fans.setText("粉丝 " + user.getFollowers_count());
		tv_sign.setText("简介:" + user.getDescription());
	}
	
	private void loadUserInfo() {
		weiboApi.usersShow(userid, userName,
				new SimpleRequestListener(this, progressDialog){

					@Override
					public void onComplete(String response) {
						super.onComplete(response);
						// 获取用户信息并设置
						user = new Gson().fromJson(response, User.class);
						
						setUserInfo();
					}
			
		});
	}
	
	private void loadStatuses(final long requestPage) {
		if(isLoadingMore) {
			return;
		}
		
		isLoadingMore = true;
		weiboApi.statusesUser_timeline(
				user == null ? -1 : user.getId(), 
//				Long.valueOf(userid),
				userName, 
				requestPage,
				new SimpleRequestListener(this, progressDialog) {

					@Override
					public void onComplete(String response) {
						super.onComplete(response);
						
						showLog("status comments = " + response);
						
						if(requestPage == 1) {
							statuses.clear();
						}
						addStatus(gson.fromJson(response, StatusTimeLineResponse.class));
					}
					
					@Override
					public void onAllDone() {
						super.onAllDone();
						
						isLoadingMore = false;
						plv_user_info.onRefreshComplete();
					}

				});
	}
	//关注用户
	private void loadS_friendships_create() {
		weiboApi.friendships_create(
				AccessTokenKeeper.readAccessToken(this), 
				user == null ? -1 : user.getId(),
				userName, 
				new SimpleRequestListener(this, progressDialog){

					@Override
					public void onComplete(String response) {
						// TODO Auto-generated method stub
						super.onComplete(response);
						addStatus(gson.fromJson(response, StatusTimeLineResponse.class));
					}

					@Override
					public void onAllDone() {
						// TODO Auto-generated method stub
						super.onAllDone();
						plv_user_info.onRefreshComplete();
					}
					
				});
	}
	
	private void loadStatus_home(final long requestPage) {
		// TODO Auto-generated method stub
		weiboApi.statusesHome_timeline(requestPage, new SimpleRequestListener(this, progressDialog){

			@Override
			public void onComplete(String response) {
				// TODO Auto-generated method stub
				super.onComplete(response);
				addStatus(gson.fromJson(response, StatusTimeLineResponse.class));
			}

			@Override
			public void onAllDone() {
				// TODO Auto-generated method stub
				super.onAllDone();
				plv_user_info.onRefreshComplete();
			}
			
		});
	}

	private void loadStatus_mention(final long requestPage) {
		// TODO Auto-generated method stub
		weiboApi.statuse_metions(requestPage, new SimpleRequestListener(this, progressDialog){

			@Override
			public void onComplete(String response) {
				// TODO Auto-generated method stub
				super.onComplete(response);
				addStatus(gson.fromJson(response, StatusTimeLineResponse.class));
			}

			@Override
			public void onAllDone() {
				// TODO Auto-generated method stub
				super.onAllDone();
				plv_user_info.onRefreshComplete();
			}
			
		});
	}
	
	private void loadstatus_timeline_batch(final long requestPage) {
		// TODO Auto-generated method stub
		weiboApi.statuser_bilateral_timeline(Long.valueOf(userid),userName,requestPage, new SimpleRequestListener(this, progressDialog){

			@Override
			public void onComplete(String response) {
				// TODO Auto-generated method stub
				super.onComplete(response);
				addStatus(gson.fromJson(response, StatusTimeLineResponse.class));
			}

			@Override
			public void onAllDone() {
				// TODO Auto-generated method stub
				super.onAllDone();
				plv_user_info.onRefreshComplete();
			}
			
		});
	}
	

	private void load_bilateral_timeline(final long requestPage) {
		// TODO Auto-generated method stub
		weiboApi.statuse_timeline_batch(Long.valueOf(userid),requestPage, new SimpleRequestListener(this, progressDialog){

			@Override
			public void onComplete(String response) {
				// TODO Auto-generated method stub
				super.onComplete(response);
				addStatus(gson.fromJson(response, StatusTimeLineResponse.class));
			}

			@Override
			public void onAllDone() {
				// TODO Auto-generated method stub
				super.onAllDone();
				plv_user_info.onRefreshComplete();
			}
			
		});
	}
	
	private void addStatus(StatusTimeLineResponse response) {
		for(Status status : response.getStatuses()) {
			if(!statuses.contains(status)) {
				//添加到当前适配器对应的数据集合中
				statuses.add(status);
			}
		}
		statusAdapter.notifyDataSetChanged();
		
		if(curPage < response.getTotal_number()) {
			addFootView(plv_user_info, footView);
		} else {
			removeFootView(plv_user_info, footView);
		}
	}
	
	private void addFootView(PullToRefreshListView plv, View footView) {
		ListView lv = plv.getRefreshableView();
		if(lv.getFooterViewsCount() == 1) {
			lv.addFooterView(footView);
		}
	}
	
	private void removeFootView(PullToRefreshListView plv, View footView) {
		ListView lv = plv.getRefreshableView();
		if(lv.getFooterViewsCount() > 1) {
			lv.removeFooterView(footView);
		}
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.titlebar_iv_left:
			UserInfoActivity.this.finish();
			break;
		case R.id.iv_avatar:
			break;
		case R.id.ll_share_bottom:
			break;
		case R.id.ll_comment_bottom:
			break;
		case R.id.ll_like_bottom:
			break;

		default:
			break;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		Intent intent = new Intent(this, ImageBrowserActivity.class);
//		intent.putExtra("status", status);
		intent.putExtra("position", position);
		startActivity(intent);
	}
	
	private void syncRadioButton(RadioGroup group, int checkedId) {
		//通过findviewbyid获取到radiobutton选中的控件
		//通过控件得到它所属父控件中的位置
		int index = group.indexOfChild(group.findViewById(checkedId));
		
		//判断当前悬浮菜单栏的显示状态
		if(shadow_user_info_tab.getVisibility() == View.VISIBLE) {
			shadow_uliv_user_info.setCurrentItem(index);
			
			//同步随着列表添加的headview部分
			((RadioButton) rg_user_info.findViewById(checkedId)).setChecked(true);
			uliv_user_info.setCurrentItemWithoutAnim(index);
		} else {
			uliv_user_info.setCurrentItem(index);
			
			((RadioButton) shadow_rg_user_info.findViewById(checkedId)).setChecked(true);
			shadow_uliv_user_info.setCurrentItemWithoutAnim(index);
		}
	}
	
	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		//同步悬浮的标题栏和列表中的标题栏的状态
		syncRadioButton(group, checkedId);
		
//		switch (checkedId) {
//		case R.id.rb_info:
//			if(shadow_user_info_tab.getVisibility() == View.VISIBLE) {
//				rb_info.setChecked(true);
//				uliv_user_info.setCurrentItemWithoutAnim(0);
//				
//				shadow_rb_info.setChecked(true);
//				shadow_uliv_user_info.setCurrentItem(0);
//			} else {
//				rb_info.setChecked(true);
//				uliv_user_info.setCurrentItem(0);
//				
//				shadow_rb_info.setChecked(true);
//				shadow_uliv_user_info.setCurrentItemWithoutAnim(0);
//			}
//			break;
//		case R.id.rb_status:
//			if(shadow_user_info_tab.getVisibility() == View.VISIBLE) {
//				rb_status.setChecked(true);
//				uliv_user_info.setCurrentItemWithoutAnim(1);
//				
//				shadow_rb_status.setChecked(true);
//				shadow_uliv_user_info.setCurrentItem(1);
//			} else {
//				rb_status.setChecked(true);
//				uliv_user_info.setCurrentItem(1);
//				
//				shadow_rb_status.setChecked(true);
//				shadow_uliv_user_info.setCurrentItemWithoutAnim(1);
//			}
//			break;
//		case R.id.rb_photos:
//			if(shadow_user_info_tab.getVisibility() == View.VISIBLE) {
//				rb_photos.setChecked(true);
//				uliv_user_info.setCurrentItemWithoutAnim(2);
//				
//				shadow_rb_photos.setChecked(true);
//				shadow_uliv_user_info.setCurrentItem(2);
//			} else {
//				rb_photos.setChecked(true);
//				uliv_user_info.setCurrentItem(2);
//				
//				shadow_rb_photos.setChecked(true);
//				shadow_uliv_user_info.setCurrentItemWithoutAnim(2);
//			}
//			break;
//		case R.id.rb_manager:
//			if(shadow_user_info_tab.getVisibility() == View.VISIBLE) {
//				rb_manager.setChecked(true);
//				uliv_user_info.setCurrentItemWithoutAnim(3);
//				
//				shadow_rb_manager.setChecked(true);
//				shadow_uliv_user_info.setCurrentItem(3);
//			} else {
//				rb_manager.setChecked(true);
//				uliv_user_info.setCurrentItem(3);
//				
//				shadow_rb_manager.setChecked(true);
//				shadow_uliv_user_info.setCurrentItemWithoutAnim(3);
//			}
//			break;
//
//		default:
//			break;
//		}
	}

}
