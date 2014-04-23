package cn.boqii.app.ui;

import java.util.ArrayList;
import java.util.HashMap;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.Platform.ShareParams;
import cn.sharesdk.framework.utils.UIHandler;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.netease.microblog.NetEaseMicroBlog;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.qzone.QZone;
import cn.sharesdk.tencent.weibo.TencentWeibo;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.PorterDuff.Mode;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler.Callback;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class ShareWeiboActivity extends Activity implements TextWatcher,
		OnCheckedChangeListener, Callback {

	private EditText mETxt;// 输入编辑框
	private TextView mTotalTxt;// 字数统计文本
	private int num = 140;// 字数限制
	private ArrayList<CheckBox> cbLst;// 所有checkbox分享平台
	public static String[] allName = new String[] { SinaWeibo.NAME,
			TencentWeibo.NAME, QZone.NAME, NetEaseMicroBlog.NAME };// checkbox的分享平台对应的名字

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// ShareSDK.initSDK(this);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.share_weibo_layout);

		init();
	}

	private void init() {
		mETxt = (EditText) findViewById(R.id.shareETxt);
		mTotalTxt = (TextView) findViewById(R.id.totalNumber);

		cbLst = new ArrayList<CheckBox>();
		cbLst.add((CheckBox) findViewById(R.id.sinaCB));
		cbLst.add((CheckBox) findViewById(R.id.tencentCB));
		cbLst.add((CheckBox) findViewById(R.id.qZoneCB));
		cbLst.add((CheckBox) findViewById(R.id.netEaseMicroCB));
		// 注册监听
		for (int i = 0; i < cbLst.size(); i++) {
			CheckBox cb = cbLst.get(i);
			cb.setOnCheckedChangeListener(this);
			cb.setTag(allName[i]);// 设置tag为分享平台的名字
			cb.getBackground().setColorFilter(Color.GRAY, Mode.MULTIPLY);
		}

		// 减掉设置的文本
		mTotalTxt.setText("还可输入" + (num - mETxt.getEditableText().length())
				+ "个字");
		// 添加编辑文本状态监听
		mETxt.addTextChangedListener(this);
	}

	public void onAction(View v) {
		switch (v.getId()) {
		case R.id.shareBtn:
			for (int i = 0; i < cbLst.size(); i++) {// 判断并分享到选中的平台
				if (cbLst.get(i).isChecked()) {
					ShareParams sp = null;
					switch (i) {
					case 0:
						sp = new ShareParams();
						sp.setText("测试分享的文本");
						// sp.setImagePath(ShareActivity.TEST_IMAGE);

						break;
					case 1:
						sp = new ShareParams();
						sp.setText("测试分享的文本");
						// sp.setImagePath(ShareActivity.TEST_IMAGE);
						break;
					case 2:
						sp = new ShareParams();
						sp.setTitle("测试");
						sp.setTitleUrl("www.boqii.com");
						sp.setText("测试分享的文本");
						// sp.setImagePath(ShareActivity.TEST_IMAGE);
						sp.setSite("波奇宠物生活馆");
						sp.setSiteUrl("www.boqii.com");
						sp.setComment("评论测试");
						
						break;
					case 3:
						sp = new ShareParams();
						sp.setText("测试分享的文本");					
						//sp.setImagePath(ShareActivity.TEST_IMAGE);
						break;
					}
					if (sp != null) {
						Platform weibo = ShareSDK.getPlatform(this, cbLst
								.get(i).getTag() + "");
												
						weibo.setPlatformActionListener(new shareListener()); // 设置分享事件回调
						// 执行分享					
						weibo.share(sp);
					}
				}
			}
			break;
		}
	}

	/**
	 * 判读并授权；
	 * 
	 * @param name
	 * @return
	 */
	private void isValid(CheckBox box) {
		String name = box.getTag().toString();// 得到存储的名字
		boolean b = false;// 用于判读是否授权
		Platform p = null;// 数据实体
		if (name.equals(allName[1])) {
			p = new TencentWeibo(this);
			b = p.isValid();
		} else if (name.equals(allName[2])) {
			p = new QZone(this);
			b = p.isValid();
		} else if (name.equals(allName[3])) {
			p = new NetEaseMicroBlog(this);
			b = p.isValid();
		} else {
			p = new SinaWeibo(this);
			b = p.isValid();
		}
		if (!b) {// 如果没有授权则授权授权
			p.authorize();
			p.setPlatformActionListener(new authorizeListener());
		}
	}

	/********************************** 文本的监听 ***********************/
	private CharSequence temp;
	private int selectionStart;
	private int selectionEnd;

	@Override
	public void afterTextChanged(Editable s) {
		int number = num - s.length();// 得到还可以输入的字数
		mTotalTxt.setText("还可输入" + number + "个字");
		selectionStart = mETxt.getSelectionStart();
		selectionEnd = mETxt.getSelectionEnd();
		if (temp.length() > num) {
			s.delete(selectionStart - 1, selectionEnd);
			int tempSelection = selectionEnd;
			mETxt.setText(s);
			mETxt.setSelection(tempSelection);// 设置光标在最后
		} else {
			mETxt.setSelection(selectionEnd);
		}

	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		temp = s;// 将改变了的文本赋给temp
	}

	/************************* 复选框选择改变事件 **************************/

	public CheckBox lastCheckBox;// 记录选择的复选框

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		Drawable drawable = buttonView.getBackground();
		if (isChecked) {
			drawable.clearColorFilter();
			buttonView.setBackgroundDrawable(drawable);
			lastCheckBox = (CheckBox) buttonView;
			// 判断是否授权，如果没有授权则授权，否则什么也不做
			isValid((CheckBox) buttonView);

		} else {
			drawable.setColorFilter(Color.GRAY, Mode.MULTIPLY);
			buttonView.setBackgroundDrawable(drawable);
		}

	}

	/***************************** 回调 ******************************************/
	/**
	 * 分享的回调
	 * 
	 * @author Administrator
	 * 
	 */
	class shareListener implements PlatformActionListener {
		@Override
		public void onCancel(Platform arg0, int arg1) {
			UIHandler.sendEmptyMessage(SHARE_CANCEL, ShareWeiboActivity.this);
		}

		@Override
		public void onComplete(Platform arg0, int arg1,
				HashMap<String, Object> arg2) {
			UIHandler.sendEmptyMessage(SHARE_OK, ShareWeiboActivity.this);
		}

		@Override
		public void onError(Platform arg0, int arg1, Throwable arg2) {
			Message msg = new Message();
			msg.obj = arg2;
			msg.what = SHARE_ERROR;
			UIHandler.sendMessage(msg, ShareWeiboActivity.this);
		}
	}

	/**
	 * 授权的回调
	 * 
	 * @author Administrator
	 * 
	 */
	class authorizeListener implements PlatformActionListener {
		@Override
		public void onCancel(Platform arg0, int arg1) {
			UIHandler.sendEmptyMessage(AUTHOEIZE_CANCEL,
					ShareWeiboActivity.this);
		}

		@Override
		public void onComplete(Platform arg0, int arg1,
				HashMap<String, Object> arg2) {
			UIHandler.sendEmptyMessage(AUTHOEIZE_OK, ShareWeiboActivity.this);
		}

		@Override
		public void onError(Platform arg0, int arg1, Throwable arg2) {
			Message msg = new Message();
			msg.obj = arg2;
			msg.what = AUTHOEIZE_CANCEL;
			UIHandler.sendMessage(msg, ShareWeiboActivity.this);
		}
	}

	/******************************* 回调的消息接受 ********************************************/
	@Override
	public boolean handleMessage(Message msg) {

		switch (msg.what) {
		case SHARE_ERROR:
			Toast.makeText(ShareWeiboActivity.this, "分享失败！", Toast.LENGTH_SHORT)
					.show();
			System.out.println("失败原因：" + msg.obj);
			break;
		case SHARE_CANCEL:
			Toast.makeText(ShareWeiboActivity.this, "取消分享！", Toast.LENGTH_SHORT)
					.show();
			break;
		case SHARE_OK:
			Toast.makeText(ShareWeiboActivity.this, "分享成功！", Toast.LENGTH_SHORT)
					.show();
			break;
		case AUTHOEIZE_ERROR:
			Toast.makeText(ShareWeiboActivity.this, "授权失败！", Toast.LENGTH_SHORT)
					.show();
			System.out.println("失败原因：" + msg.obj);
			lastCheckBox.setChecked(false);
			break;
		case AUTHOEIZE_OK:
			Toast.makeText(ShareWeiboActivity.this, "授权成功！", Toast.LENGTH_SHORT)
					.show();
			break;
		case AUTHOEIZE_CANCEL:
			Toast.makeText(ShareWeiboActivity.this, "取消授权！", Toast.LENGTH_SHORT)
					.show();
			lastCheckBox.setChecked(false);
			break;
		}

		return false;
	}

	public static final int SHARE_ERROR = -1;// 分享失败
	public static final int SHARE_OK = 1;// 分享成功
	public static final int SHARE_CANCEL = 0;// 分享取消
	public static final int AUTHOEIZE_ERROR = -11;// 授权失败
	public static final int AUTHOEIZE_OK = 11;// 授权成功
	public static final int AUTHOEIZE_CANCEL = 10;// 授权取消

	@Override
	protected void onDestroy() {
		// ShareSDK.stopSDK(this);
		super.onDestroy();
	}
}
