package cn.boqii.app.ui;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ScrollView;
import android.widget.Toast;

public class RelocationActivity extends Activity implements OnTouchListener {

	View top, bottom;
	ScrollView sv;
	int[] location = new int[2];
	int[] location2 = new int[2];
	int lastY = 0;
	int touchEventId = -9983761;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.relocation_layout);
		intit();
	}

	private void intit() {
		sv = (ScrollView) findViewById(R.id.sv);
		sv.setOnTouchListener(this);
		top = (View) findViewById(R.id.top);
		bottom = (View) findViewById(R.id.bottom);
		bottom.setVisibility(View.INVISIBLE); // 隐藏停靠的View但还是占用空间
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// 必须两个都搞上，不然会有瑕疵。
		switch (event.getAction()) {
		case MotionEvent.ACTION_MOVE:// 没有这段，手指按住拖动的时候没有效果
			handler.sendMessageDelayed(handler.obtainMessage(touchEventId, v),
					5);
		case MotionEvent.ACTION_UP:// 没有这段，手指松开scroll继续滚动的时候，没有效果
			handler.sendMessageDelayed(handler.obtainMessage(touchEventId, v),
					5);
			break;
		}
		return false;
	}

	public void onAction(View v) {
		switch (v.getId()) {
		case R.id.bottomBtn:
				Toast.makeText(this, "您点击了停靠按钮", Toast.LENGTH_SHORT).show();
			break;
		case R.id.topBtn:
			Toast.makeText(this, "您点击的是正常的按钮", Toast.LENGTH_SHORT).show();
			break;
		}
	}

	Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			super.handleMessage(msg);
			if (msg.what == touchEventId) {
				if (lastY != sv.getScrollY()) {
					// scrollview 一直在滚动，会触发
					handler.sendMessageDelayed(
							handler.obtainMessage(touchEventId, sv), 5);
					lastY = sv.getScrollY();
					//得到在屏幕的坐标，方法给传入的参数赋值(x,y)
					top.getLocationOnScreen(location);
					bottom.getLocationOnScreen(location2);
					// top到bottom位置时，bottom显示。top的实际上还是向滚动，但我们看到的是bottom
					if (location[1] <= location2[1]) {
						// 显现bottom
						bottom.setVisibility(View.VISIBLE);
					} else {
						// 把bottom隐藏了，不占用空间
						bottom.setVisibility(View.GONE);
					}
				}
			}

		}
	};
}
