package cn.boqii.app.adaper;


import cn.sharesdk.framework.TitleLayout;
import cn.sharesdk.framework.authorize.AuthorizeAdapter;

public class MyAuthorizeAdaper extends AuthorizeAdapter{
	
	@Override
	public void onCreate() {		
		//super.onCreate();
		
		hideShareSDKLogo();
		TitleLayout tl=getTitleLayout();
		tl.getBtnRight().setText("波奇");
		
		
	}

}
