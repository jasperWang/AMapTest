package cn.boqii.app.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class MainActivity extends Activity implements OnItemClickListener {

	private String[] strName = new String[] { "高德地图", "停靠效果","分享功能" };
	private ListView lst;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mian_layout);

		lst = (ListView) findViewById(R.id.lst);

		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.test_list_item, android.R.id.text1, strName);
		lst.setAdapter(adapter);
		lst.setOnItemClickListener(this);
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View v, int index, long arg3) {
		Intent mIntent = new Intent();
		if (index == 0) {
			mIntent.setClass(this, LocationActivity.class);
		} else if (index == 1) {
			mIntent.setClass(this, RelocationActivity.class);
		}else{
			mIntent.setClass(this, ShareActivity.class);
		}
		startActivity(mIntent);

	}
}
