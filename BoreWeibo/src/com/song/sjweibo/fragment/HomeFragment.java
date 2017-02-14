package com.song.sjweibo.fragment;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.os.Bundle;
import android.provider.AlarmClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.song.sjweibo.R;
import com.google.gson.Gson;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnLastItemVisibleListener;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;
import com.song.sjweibo.BaseFragment;
import com.song.sjweibo.adapter.StatusAdapter;
import com.song.sjweibo.api.BoreWeiboAPI;
import com.song.sjweibo.api.SimpleRequestListener;
import com.song.sjweibo.entity.Status;
import com.song.sjweibo.entity.response.StatusTimeLineResponse;
import com.song.sjweibo.utils.TitleBuilder;
import com.song.sjweibo.utils.ToastUtils;
/**
 * 
 * @author lenovo
 * 由MainInterface类到这里设置单个fragment
 * 实现单个Fargment的titlebar，和PullToRefreshListView控件
 * 设置适配器StatusAdapter,传入数据
 * @param
 * 1. "@layout/include_titlebar"    include  使用顶部标题栏
 * 2. "@+id/plv_home"               com.handmark.pulltorefresh.library.PullToRefreshListView 使用下拉刷新控件
 */
public class HomeFragment extends BaseFragment {

	private View view;
	private PullToRefreshListView plv_home;
	private List<Status> statuses=new ArrayList<Status>();
	private int curPage=1;
	private View footView;
	private StatusAdapter adapter;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		
		initView();
		loadData(1);
		return view;
	}

	private void initView() {
		// TODO Auto-generated method stub
		view=View.inflate(activity, R.layout.frag_home, null);
		
		/* 创建适配器，将数据集statuses传入StatusAdapter类中
		 * 将适配器Adapter和这个HomeFragment寄宿的Activity绑定在一起
		 */
		adapter = new StatusAdapter(activity, statuses);
		
		//标题栏设置
		new TitleBuilder(view).setTitleText("首页");
		//下拉刷新组件的设置
		plv_home=(PullToRefreshListView)view.findViewById(R.id.plv_home);
		//绑定数据域——适配器，和显示区域
		plv_home.setAdapter(adapter);
		
		//处理在刷新时进行的操作，---重新加载列表的第一页
		plv_home.setOnRefreshListener(new OnRefreshListener<ListView>() {
			@Override
			public void onRefresh(PullToRefreshBase<ListView> refreshView) {
				// TODO Auto-generated method stub
				loadData(1);
			}
		});
		//上拉刷新，最后一个item的回调方法去-----加载当前页的下一页
		plv_home.setOnLastItemVisibleListener(new OnLastItemVisibleListener() {
			@Override
			public void onLastItemVisible() {
				// TODO Auto-generated method stub
				loadData(curPage+1);
			}
		});
		
		/**
		 * 内容为加载的文字和进度条
		 */
		footView=view.inflate(activity, R.layout.footview_loading, null);
	}
	
	//使用微博SDK的接口
	private void loadData(final int page) {
		BoreWeiboAPI api = new BoreWeiboAPI(activity);
		//调用查询方法
		api.statusesHome_timeline(page,
				new SimpleRequestListener(activity, null) {
					@Override
					public void onComplete(String response) {
						super.onComplete(response);
						if(page == 1) {
							//清空当前对象
							statuses.clear();
						}
						//更新当前页数据
						curPage = page;
						/* 将json字符串转换为第二个参数的StatusTimeLineResponse类对象
						 * 利用addData()方法将当前页数据添加到数据集合中进行数据绑定
						 */
						addData(new Gson().fromJson(response, StatusTimeLineResponse.class));
					}
					@Override
					public void onAllDone() {
						super.onAllDone();
						//手动告诉下拉刷新控件我们已经完成刷新
						plv_home.onRefreshComplete();
					}
				});
	}
	
	/**
	 * 防止重新new一次适配器，这样会让他的当前位置跳到数据集合第一页
	 * 我们应在加载下一页数据后还停留在当前页的位置
	 * 将新的数据添加到原有的数据集合中，防止适配器重新创建，
	 * @param 正确做法是
	 * 1.创建空的微博数据集合
	 * 2.new一个适配器绑定到列表上
	 * 3.在数据返回的时候，把新的数据添加到原有的数据集合中
	 * 4.利用BaseAdapter的notifyDataSetChanged()进行数据更新
	 * @author 保证整个列表对应的Adapter一直是同一个，Adapter中的数据集合也一直是同一个。改变集合中的数据
	 */
	private void addData(StatusTimeLineResponse fromJson) {
		// TODO Auto-generated method stub
		for(Status status:fromJson.getStatuses()){
			if(!statuses.contains(status)){
				statuses.add(status);
			}
		}
		
		
		//通知StatusAdapter进行数据更新，并将反映数据集的任何视图进行刷新
		adapter.notifyDataSetChanged();
		
		
		//如果当前页小于总页数，加上footview否则就去掉footview
		if(curPage<fromJson.getTotal_number()){
			addFootView(plv_home,footView);
		}else{
			removeFootView(plv_home,footView);
		}
	}
	private void addFootView(PullToRefreshListView plv,View footView){
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
}
