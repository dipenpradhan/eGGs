package com.nullpointer.polygame;

import java.net.URI;
import java.net.URL;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;


public class AboutView extends Activity {

	private WebView mWView;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		String mURL= new String("http://www.polymerupdate.com/general/about-us.aspx");
		mWView=new WebView(this);
		mWView.loadUrl(mURL);
		setContentView(mWView);
		
	}

	
}
