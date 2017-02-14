package com.song.sjweibo.constants;

public interface URLS {
	// host
	String BASE_URL = "https://api.weibo.com/2/";
	
	// 首页微博列表
	String statusesHome_timeline = BASE_URL + "statuses/home_timeline.json";
	// 微博评论列表
	String commentsShow = BASE_URL + "comments/show.json";
	// 对一条微博进行评论
	String commentsCreate = BASE_URL + "comments/create.json";
	// 转发一条微博
	String statusesRepost = BASE_URL + "statuses/repost.json";
	// 发布一条微博(带图片)
	String statusesUpload = BASE_URL + "statuses/upload.json";
	// 发布一条微博(不带图片)
	String statusesUpdate = BASE_URL + "statuses/update.json";
	//关注一个用户
	String friendships_create="https://api.weibo.com/2/friendships/create.json";
	//取消关注一个用户
	String friendships_destroy="https://api.weibo.com/2/friendships/destroy.json";
	//获取最新的提到登录用户的微博列表，即@我的微博
	String statuses_mentions="https://api.weibo.com/2/statuses/mentions.json";
	//批量获取指定的一批用户的微博列表
	String statuses_timeline_batch="https://api.weibo.com/2/statuses/timeline_batch.json";
	//获取双向关注用户的最新微博
	String statuses_bilateral_timeline="https://api.weibo.com/2/statuses/bilateral_timeline.json";
	//通过URL赞某个对象
	String interest_like="https://api.weibo.com/2/interest/like.json";
	//通过URL取消赞某个对象
	String interest_unlike="https://api.weibo.com/2/interest/unlike.json";
	
	
	String usersShow = BASE_URL + "users/show.json";
	String statusesUser_timeline = BASE_URL + "statuses/user_timeline.json";
	String statusesRepost_timeline = BASE_URL + "statuses/repost_timeline.json";
	
}
