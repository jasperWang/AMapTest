package cn.boqii.app.adaper;


import cn.sharesdk.framework.authorize.AuthorizeAdapter;

public class MyAuthorizeAdaper extends AuthorizeAdapter{
	
	@Override
	public void onCreate() {		
		super.onCreate();
		this.hideShareSDKLogo();
		
	}

}
