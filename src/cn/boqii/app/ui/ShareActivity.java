package cn.boqii.app.ui;

import java.util.HashMap;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.framework.utils.UIHandler;
import cn.sharesdk.onekeyshare.OnekeyShare;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler.Callback;
import android.os.Message;
import android.view.View;
import android.widget.Toast;

public class ShareActivity extends Activity implements Callback {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ShareSDK.initSDK(this);
		setContentView(R.layout.share_layout);
	}

	public String TEST_IMAGE = "";

	public void onAction(View v) {
		switch (v.getId()) {
		case R.id.shareViewBtn:

			OnekeyShare oks = new OnekeyShare();
			// 分享时 Notification 的图标和文字
			oks.setNotification(R.drawable.ic_launcher,
					this.getString(R.string.app_name));

			oks.setAddress("15803017438");// 仅在信息和邮箱时使用
			oks.setTitle("分享功能测试");// 标题
			oks.setTitleUrl("http://bbs.boqii.com/content/viewthread-3130655.html");// 标题超链接
			oks.setText("这是为了实现分享功能的测试Demo");// 内容
			oks.setImagePath(TEST_IMAGE);// 本地图片
			oks.setImageUrl("http://bbs.boqii.com/content/viewthread-3130655.html");// 图片指向的链接
			oks.setComment("( ^_^ )不错嘛");// 分享的评论，仅在人人网和 QQ 空间使用
			oks.setSite("www.boqii.com");// site 是分享此内容的网站名称，仅在 QQ 空间使用
			// siteUrl 是分享此内容的网站地址，仅在 QQ 空间使用
			oks.setSiteUrl("http://bbs.boqii.com/content/viewthread-3130655.html");
			// latitude 是维度数据，仅在新浪微博、腾讯微博和 Foursquare 使用
			oks.setLatitude(31.238068f);
			// longitude 是经度数据，仅在新浪微博、腾讯微博和 Foursquare 使用
			oks.setLongitude(121.501654f);
			// 是否直接分享（true 则直接分享）
			//oks.setSilent(true);
			
			//监听快捷分享的处理结果
			oks.setCallback(new OneKeyShareCallback());
			oks.show(this);

			break;
		}
	}

	class OneKeyShareCallback implements PlatformActionListener {

		@Override
		public void onCancel(Platform arg0, int arg1) {
			UIHandler.sendEmptyMessage(0, ShareActivity.this);
		}

		@Override
		public void onComplete(Platform arg0, int arg1,
				HashMap<String, Object> arg2) {
			UIHandler.sendEmptyMessage(1, ShareActivity.this);
		}

		@Override
		public void onError(Platform arg0, int arg1, Throwable arg2) {
			Message msg=new Message();
			msg.what=-1;
			msg.obj=arg2;
			UIHandler.sendMessage(msg, ShareActivity.this);
		}

	}

	@Override
	public boolean handleMessage(Message msg) {
		switch (msg.what) {
		case -1:
			Toast.makeText(ShareActivity.this, "分享失败，原因："+msg.obj, Toast.LENGTH_LONG).show();
			break;
		case 1:
			Toast.makeText(ShareActivity.this, "分享成功", Toast.LENGTH_SHORT).show();
			break;
		case 0:
			Toast.makeText(ShareActivity.this, "取消分享", Toast.LENGTH_SHORT).show();
			break;
		}
		return false;
	}
	
	@Override
	protected void onDestroy() {
		ShareSDK.stopSDK(this);
		super.onDestroy();
	}
}
