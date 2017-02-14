package com.song.sjweibo.api;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;

import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboParameters;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.openapi.legacy.WeiboAPI;
import com.song.sjweibo.constants.AccessTokenKeeper;
import com.song.sjweibo.constants.URLS;
import com.song.sjweibo.utils.Logger;
/**
 * 
 * 微博SDK接口的调用类
 * 
 * 网络请求放在子线程里，主线程不做这种限时操作，但子线程无法更新主线程
 * 通过handler将需要的信息传给主线程，在主线程的handler类重写handlerMessage方法进行接受处理
 * 在这里进行handler主线程的封装，
 * @author lenovo
 *
 */
public class BoreWeiboAPI extends WeiboAPI{
	
	//新建handler对象并传入主线程循环器
	private Handler mainLooperHandler = new Handler(Looper.getMainLooper());

	//在创建对象时传入Oauth2AccessToken对象
	private BoreWeiboAPI(Oauth2AccessToken oauth2AccessToken) {
		super(oauth2AccessToken);
	}
	//利用AccessTokenKeeper得到对应的Token信息，然后调用构造方法得到BoreWeiboAPI对象
	public BoreWeiboAPI(Context context) {
		this(AccessTokenKeeper.readAccessToken(context));
	}
	
	//URL接口地址，params接口提交参数，httpMethod是HTTP方法get或post，listener请求监听
	@Override
	protected void request(String url, WeiboParameters params, String httpMethod, RequestListener listener) {
		super.request(url, params, httpMethod, listener);
	}
	
	/* 实现主线程中的handler的处理
	 * 创建公共方法调用weiboApi的request方法，
	 * 然后主线程的handler对象调用post()方法将监听传到主线程中
	 */
	public void requestInMainLooper(String url, WeiboParameters params, String httpMethod, 
			final RequestListener listener) {
		Logger.show("API", "url = " + parseGetUrlWithParams(url, params));
		// 主线程处理
		request(url, params, httpMethod, new RequestListener() {
			@Override
			public void onIOException(final IOException e) {
				mainLooperHandler.post(new Runnable() {
		             @Override
		             public void run() {
		            	 listener.onIOException(e);
		             }
		         });
			}
			
			@Override
			public void onError(final WeiboException e) {
				mainLooperHandler.post(new Runnable() {
		             @Override
		             public void run() {
		            	 listener.onError(e);
		             }
		         });
			}
			
			@Override
			public void onComplete4binary(final ByteArrayOutputStream responseOS) {
				mainLooperHandler.post(new Runnable() {
		             @Override
		             public void run() {
		            	 listener.onComplete4binary(responseOS);
		             }
		         });
			}
			
			@Override
			public void onComplete(final String response) {
				mainLooperHandler.post(new Runnable() {
		             @Override
		             public void run() {
		            	 listener.onComplete(response);
		             }
		         });
			}
		});
		Log.i("将request方法放入主循环中", "将request方法放入主循环中");
	}
	
	/**
	 * 转换get方式的url,将get参数与url拼接
	 * 
	 * @param url
	 *            原url
	 * @param getParams
	 *            需要拼接的参数map集合
	 * @return 拼装完成的url
	 */
	public static String parseGetUrlWithParams(String url, WeiboParameters getParams) {
		StringBuilder newUrl = new StringBuilder(url);
		if (getParams != null && getParams.size() > 0) {
			newUrl.append("?");
			for (int i=0; i<getParams.size(); i++) {
				newUrl.append(getParams.getKey(i) + "=" + getParams.getValue(i) + "&");
			}
			newUrl.substring(0, newUrl.length() - 2);
		}
		Log.i("BoreWeiboApi_转换get方式的url,将get参数与url拼接", "BoreWeiboApi_转换get方式的url,将get参数与url拼接");
		return newUrl.toString();
	}

	/**
	 * 根据用户ID获取用户信息(uid和screen_name二选一)
	 * 
	 * @param uid
	 *            根据用户ID获取用户信息
	 * @param screen_name
	 *            需要查询的用户昵称。
	 * @param listener
	 */
	public void usersShow(String uid, String screen_name, RequestListener listener) {
		WeiboParameters params = new WeiboParameters();
		if(!TextUtils.isEmpty(uid)) {
			params.add("uid", uid);
		} else if(!TextUtils.isEmpty(screen_name)) {
			//使用screen_name昵称去查询
			params.add("screen_name", screen_name);
		}
		requestInMainLooper(URLS.usersShow, params , WeiboAPI.HTTPMETHOD_GET, listener);
		Log.i("BoreWeiboApi_根据用户ID获取用户信息(uid和screen_name二选一)", "BoreWeiboApi_根据用户ID获取用户信息(uid和screen_name二选一)");
	}
	
	/**
	 * 上传图片并发布一条新微博
	 * 
	 * @param context
	 * @param status
	 *            要发布的微博文本内容。
	 * @param imgFilePath
	 *            要上传的图片绝对路径。
	 * @param listener
	 */
	public void statusesUpload(String status, String imgFilePath, RequestListener listener) {
		WeiboParameters params = new WeiboParameters();
		params.add("status", status);
		params.add("pic", imgFilePath);
		requestInMainLooper(URLS.statusesUpload, params, WeiboAPI.HTTPMETHOD_POST, listener);
		Log.i("BoreWeiboApi_上传图片并发布一条新微博", "BoreWeiboApi_上传图片并发布一条新微博");
	}
	
	/**
	 * 获取当前登录用户及其所关注用户的最新微博
	 * 
	 * @param page
	 * @param listener
	 */
	public void statusesHome_timeline(long page, RequestListener listener) {
		WeiboParameters params = new WeiboParameters();
		params.add("page", page);
		requestInMainLooper(URLS.statusesHome_timeline, params , WeiboAPI.HTTPMETHOD_GET, listener);
		Log.i("BoreWeiboApi_statusesHome_timeline", "BoreWeiboApi_statusesHome_timeline");
	}
	public void statuse_metions(long page, RequestListener listener){

		WeiboParameters params = new WeiboParameters();
		params.add("page", page);
		requestInMainLooper(URLS.statuses_mentions,params , WeiboAPI.HTTPMETHOD_GET, listener);
		Log.i("BoreWeiboApi_statuse_metions", "BoreWeiboApi_statuse_metions");
	
	}
	
	public void statuse_timeline_batch(long uid,long page, RequestListener listener){
		WeiboParameters params = new WeiboParameters();
		params.add("page", page);
		params.add("uid",uid);
		requestInMainLooper(URLS.statuses_timeline_batch,params , WeiboAPI.HTTPMETHOD_GET, listener);
		Log.i("BoreWeiboApi_statuse_timeline_batch", "BoreWeiboApi_statuse_timeline_batch");
	}
	
	public void statuser_bilateral_timeline(long uid, String screen_name, long page, RequestListener listener){
		WeiboParameters params = new WeiboParameters();
		if(uid > 0) {
			params.add("uid", uid);
		} else if(!TextUtils.isEmpty(screen_name)) {
			params.add("screen_name", screen_name);
		}
		params.add("page", page);
		requestInMainLooper(URLS.statuses_bilateral_timeline, params , WeiboAPI.HTTPMETHOD_GET, listener);
		Log.i("BoreWeioboApi_获取某个用户最新发表的微博列表(uid和screen_name二选一)", "BoreWeioboApi_获取某个用户最新发表的微博列表(uid和screen_name二选一)");
	
	}
	
	/**
	 * 获取某个用户最新发表的微博列表(uid和screen_name二选一)
	 * 
	 * @param uid
	 *            需要查询的用户ID。
	 * @param screen_name
	 *            需要查询的用户昵称。
	 * @param page
	 *            返回结果的页码。(单页返回的记录条数，默认为20。)
	 * @param listener
	 */
	public void statusesUser_timeline(long uid, String screen_name, long page, RequestListener listener) {
		WeiboParameters params = new WeiboParameters();
		if(uid > 0) {
			params.add("uid", uid);
		} else if(!TextUtils.isEmpty(screen_name)) {
			params.add("screen_name", screen_name);
		}
		params.add("page", page);
		requestInMainLooper(URLS.statusesUser_timeline, params , WeiboAPI.HTTPMETHOD_GET, listener);
		Log.i("BoreWeioboApi_获取某个用户最新发表的微博列表(uid和screen_name二选一)", "BoreWeioboApi_获取某个用户最新发表的微博列表(uid和screen_name二选一)");
	}
	/**
	 * 
	 */
	public void friendships_create(Oauth2AccessToken oauth2AccessToken,long uid,String screen_name,RequestListener listener){
		WeiboParameters params = new WeiboParameters();
		if(uid>0){
			params.add("uid", uid);
		}else if(!TextUtils.isEmpty(screen_name)) {
			params.add("screen_name", screen_name);
		}
		params.add("rip", "255.255.255.0");
		requestInMainLooper(URLS.friendships_create, params , WeiboAPI.HTTPMETHOD_POST, listener);
	}
	
	/**
	 * 转发微博
	 * 
	 * @param id
	 *            要转发的微博ID。
	 * @param status
	 *            添加的转发文本，必须做URLencode，内容不超过140个汉字，不填则默认为“转发微博”。
	 * @param is_comment
	 *            是否在转发的同时发表评论，0：否、1：评论给当前微博、2：评论给原微博、3：都评论，默认为0 。
	 * @param listener
	 */
	public void statusesRepost(long id, String status, int is_comment, RequestListener listener) {
		WeiboParameters params = new WeiboParameters();
		params.add("id", id);
		params.add("status", status);
		params.add("is_comment", is_comment);
		requestInMainLooper(URLS.statusesRepost, params , WeiboAPI.HTTPMETHOD_POST, listener);
		Log.i("BoreWeiboApi_要转发的微博ID", "BoreWeiboApi_要转发的微博ID");
	}
	
	/**
	 * 根据微博ID返回某条微博的评论列表
	 * 
	 * @param id
	 *            需要查询的微博ID。
	 * @param page
	 *            返回结果的页码。(单页返回的记录条数，默认为50。)
	 * @param listener
	 */
	public void commentsShow(long id, long page, RequestListener listener) {
		WeiboParameters params = new WeiboParameters();
		params.add("id", id);
		params.add("page", page);
		requestInMainLooper(URLS.commentsShow, params , WeiboAPI.HTTPMETHOD_GET, listener);
		Log.i("BoreWeiboApi_根据微博ID返回某条微博的评论列表", "BoreWeiboApi_根据微博ID返回某条微博的评论列表");
	}
	
	/**
	 * 对一条微博进行评论
	 * 
	 * @param id
	 *            需要评论的微博ID。
	 * @param comment
	 *            评论内容，必须做URLencode，内容不超过140个汉字。
	 * @param listener
	 */
	public void commentsCreate(long id, String comment, RequestListener listener) {
		WeiboParameters params = new WeiboParameters();
		params.add("id", id);
		params.add("comment", comment);
		requestInMainLooper(URLS.commentsCreate, params , WeiboAPI.HTTPMETHOD_POST, listener);
		Log.i("BoreWeiboApi_对一条微博进行评论", "BoreWeiboApi_对一条微博进行评论");
	}
	
	/**
	 * 对一条微博进行点赞
	 * 
	 * @param url
	 * 				----string	需要取消赞的长链接，需要URLencoded。
	 * @param listener
	 */
	public void likeCreat(RequestListener listener){
		WeiboParameters params = new WeiboParameters();
		requestInMainLooper(URLS.interest_like, params , WeiboAPI.HTTPMETHOD_POST, listener);
		Log.i("BoreWeiboApi_likeCreat", "BoreWeiboApi_likeCreat");
	
	}
	
	/**
	 * 对一天微博取消点赞
	 * @param url
	 * 				----string	赞的长链接，需要URLencoded。
	 * @param listener
	 * 
	 */
	public void likeCancle(RequestListener listener){
		WeiboParameters params = new WeiboParameters();
		requestInMainLooper(URLS.interest_unlike, params , WeiboAPI.HTTPMETHOD_POST, listener);
		Log.i("BoreWeiboApi_likeCancle", "BoreWeiboApi_likeCancle");
	
	
	}
	
	/**
	 * 发布或转发一条微博
	 * 
	 * @param context
	 * @param status
	 *            要发布的微博文本内容。
	 * @param imgFilePath
	 *            要上传的图片文件路径(为空则代表发布无图微博)。
	 * @param retweetedStatsId
	 *            要转发的微博ID(<=0时为原创微博)。
	 * @param listener
	 */
	public void statusesSend(String status, String imgFilePath, long retweetedStatsId, RequestListener listener) {
		String url;
		WeiboParameters params = new WeiboParameters();
		//加入文本信息
		params.add("status", status);
		if(retweetedStatsId > 0) {
			// 如果是转发微博,设置被转发者的id和URL
			params.add("id", retweetedStatsId);
			url = URLS.statusesRepost;
		} else if(!TextUtils.isEmpty(imgFilePath)) {
			// 如果有图片,则调用upload接口且设置图片路径
			params.add("pic", imgFilePath);
			url = URLS.statusesUpload;
		} else {
			// 如果无图片,则调用update接口
			url = URLS.statusesUpdate;
		}
		//调用提交方法
		requestInMainLooper(url, params, WeiboAPI.HTTPMETHOD_POST, listener);
		Log.i("BoreWeiboApi_要发布的微博文本内容。", "BoreWeiboApi_要发布的微博文本内容。");
	}

	/**
	 * 获取指定微博的转发微博列表
	 * 
	 * @param id
	 *            要转发的微博ID。
	 * @param page
	 *            返回结果的页码(单页返回的记录条数，默认为20。)
	 * @param listener
	 */
	public void statusesRepostTimeline(long id, int page, RequestListener listener) {
		WeiboParameters params = new WeiboParameters();
		params.add("id", id);
		params.add("page", page);
		requestInMainLooper(URLS.statusesRepost_timeline, params , WeiboAPI.HTTPMETHOD_GET, listener);
		Log.i("BoreWeiboApi_获取指定微博的转发微博列表", "BoreWeiboApi_获取指定微博的转发微博列表");
	}
	
}
