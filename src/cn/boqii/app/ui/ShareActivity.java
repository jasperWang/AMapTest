package cn.boqii.app.ui;

import java.util.HashMap;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.framework.utils.UIHandler;
import cn.sharesdk.onekeyshare.OnekeyShare;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.tencent.qzone.QZone;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler.Callback;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

public class ShareActivity extends Activity implements Callback {

	private TextView tv;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ShareSDK.initSDK(this);

		setContentView(R.layout.share_layout);
		tv = (TextView) findViewById(R.id.showTxt);
	}

	public static String TEST_IMAGE = Environment.getExternalStorageDirectory()
			.getParent() + "/img1_color.png";

	public void onAction(View v) {
		switch (v.getId()) {
		case R.id.shareViewBtn:
			share();// 快捷分享
			break;
		case R.id.qqLogin:
			authorize(new QZone(this));
			break;
		case R.id.sianLogin:
			authorize(new SinaWeibo(this));
			break;

		}
	}

	/**
	 * 快捷分享的方法
	 */
	private void share() {
		OnekeyShare oks = new OnekeyShare();
		// 分享时 Notification 的图标和文字
		oks.setNotification(R.drawable.ic_launcher,
				this.getString(R.string.app_name));

		oks.setAddress("15803017438");// 仅在信息和邮箱时使用
		oks.setTitle("分享测试");// 标题
		oks.setTitleUrl("http://bbs.boqii.com/content/viewthread-3130655.html");// 标题超链接
		oks.setText("波奇宠物生活馆：http://www.boqii.com");// 内容
		// oks.setImagePath(TEST_IMAGE);// 本地图片
		// oks.setImageUrl("http://bbs.boqii.com/content/viewthread-3130655.html");//
		// 图片指向的链接
		oks.setComment("还能不能让宠物自由的玩耍？");// 分享的评论，仅在人人网和 QQ 空间使用
		oks.setSite("http://www.boqii.com");// site 是分享应用的名称，仅在 QQ 空间使用
		// siteUrl 是分享此内容的网站地址，仅在 QQ 空间使用
		oks.setSiteUrl("http://bbs.boqii.com/content/viewthread-3130655.html");
		// latitude 是维度数据，仅在新浪微博、腾讯微博和 Foursquare 使用
		oks.setLatitude(31.238068f);
		// longitude 是经度数据，仅在新浪微博、腾讯微博和 Foursquare 使用
		oks.setLongitude(121.501654f);
		// oks.setImagePath(TEST_IMAGE);
		// oks.setImageUrl("http://www.boqii.com");
		// 是否直接分享（true 则直接分享）
		// oks.setSilent(true);

		// 自定义图标
		Bitmap logo = BitmapFactory.decodeResource(getResources(),
				R.drawable.u53_normal);
		// 图标点击后会通过 Toast 提示消息
		final OnekeyShare fOKS = oks;
		OnClickListener listener = new OnClickListener() {
			public void onClick(View v) {
				startActivity(new Intent().setClass(ShareActivity.this,
						ShareWeiboActivity.class));
				fOKS.finish();
			}
		};
		oks.setCustomerLogo(logo, "分享到微博", listener);

		// 监听快捷分享的处理结果
		oks.setCallback(new OneKeyShareCallback());
		oks.show(this);
	}

	/**
	 * 登陆授权
	 */
	private void authorize(Platform plat) {
		if (plat == null) {
			return;
		}
		System.out.println("授权。。。。");
		if (plat.isValid()) {// 判断是否授权
			String userId = plat.getDb().getUserId();// 得到用户id
			if (userId != null) {// 判断id是否存在
				UIHandler.sendEmptyMessage(2, lcb);
				login(plat.getName(), userId, null);// 执行登陆
				return;
			}
		}
		plat.setPlatformActionListener(new LoginListener());	
		//plat.SSOSetting(true);	
		plat.showUser(null);
	}

	private void login(String plat, String userId,
			HashMap<String, Object> userInfo) {
		Message msg = new Message();
		msg.what = 3;
		msg.obj = plat;
		UIHandler.sendMessage(msg, lcb);
	}

	/*********************************** 分享的监听和回调 ************************************/

	/**
	 * 快捷分享的监听
	 */
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
			Message msg = new Message();
			msg.what = -1;
			msg.obj = arg2;
			UIHandler.sendMessage(msg, ShareActivity.this);
		}

	}

	/**
	 * 快捷分享的方回调
	 */
	@Override
	public boolean handleMessage(Message msg) {
		switch (msg.what) {
		case -1:
			Toast.makeText(ShareActivity.this, "分失败", Toast.LENGTH_LONG).show();
			System.out.println("分享失败，原因：" + msg.obj);
			break;
		case 1:

			break;
		case 0:
			Toast.makeText(ShareActivity.this, "取消分享", Toast.LENGTH_SHORT)
					.show();
			break;
		}
		return false;
	}

	/*********************************** 授权的监听和回调 *************************************************/
	LoginCallBack lcb = new LoginCallBack();
	/**
	 * 登陆的监听
	 */
	class LoginListener implements PlatformActionListener {
		

		@Override
		public void onCancel(Platform platform, int action) {
			if (action == Platform.ACTION_USER_INFOR) {
				UIHandler.sendEmptyMessage(0, lcb);
			}
		}

		@Override
		public void onComplete(Platform platform, int action,
				HashMap<String, Object> res) {
			if (action == Platform.ACTION_USER_INFOR) {
				Message message = new Message();
				message.what = 1;
				message.obj = res;
				UIHandler.sendMessage(message, lcb);
				login(platform.getName(), platform.getDb().getUserId(), res);
			}
			System.out.println(res);
		}

		@Override
		public void onError(Platform arg0, int action, Throwable t) {
			if (action == Platform.ACTION_USER_INFOR) {
				Message msg = new Message();
				msg.what = -1;
				msg.obj = t;
				UIHandler.sendMessage(msg, lcb);
			}
			t.printStackTrace();
		}

	}

	/**
	 * 登陆的回调
	 */
	class LoginCallBack implements Callback {
		@Override
		public boolean handleMessage(Message msg) {
			switch (msg.what) {
			case -1:
				Toast.makeText(ShareActivity.this, "登陆失败", Toast.LENGTH_LONG)
						.show();
				System.out.println("分享失败，原因：" + msg.obj);
				break;
			case 1:
				Toast.makeText(ShareActivity.this, "登陆成功", Toast.LENGTH_SHORT)
						.show();
				tv.setText(msg.obj + "");
				break;

			case 2:
				Toast.makeText(ShareActivity.this, "用户信息已存在，正在跳转登录操作",
						Toast.LENGTH_SHORT).show();
				break;
			case 3:		
				Toast.makeText(ShareActivity.this, "使用 " + msg.obj + " 账号登陆",
						Toast.LENGTH_SHORT).show();
				break;
			case 0:
				Toast.makeText(ShareActivity.this, "取消登陆", Toast.LENGTH_SHORT)
						.show();
				break;
			}
			return false;
		}
	}

	@Override
	protected void onDestroy() {
		ShareSDK.stopSDK(this);
		super.onDestroy();
	}
}
