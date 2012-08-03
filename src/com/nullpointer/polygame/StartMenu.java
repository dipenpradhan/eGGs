package com.nullpointer.polygame;

import android.app.Activity;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

public class StartMenu extends Activity implements OnClickListener,OnTouchListener{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
		setContentView(R.layout.startmenu);
		Typeface titlefont = Typeface.createFromAsset(getAssets(), "font/AltamonteNF.ttf");
		Typeface menuFont = Typeface.createFromAsset(getAssets(), "font/ANKLEPAN.TTF");
		
	TextView txv_start,txv_exit,txv_about;
	txv_start= (TextView) findViewById(R.id.txv_start);
	txv_exit=  (TextView) findViewById(R.id.txv_exit);
	txv_about= (TextView) findViewById(R.id.txv_about);
	TextView txv_title = (TextView) findViewById(R.id.txv_title);

	txv_title.setTextColor(Color.DKGRAY);
	txv_start.setTextColor(Color.BLACK);
	txv_about.setTextColor(Color.BLACK);
	txv_exit.setTextColor(Color.BLACK);
	
	txv_title.setTypeface(titlefont);
	txv_start.setTypeface(menuFont);
	txv_about.setTypeface(menuFont);
	txv_exit.setTypeface(menuFont);
	
	txv_start.setOnTouchListener(this);
	txv_exit.setOnTouchListener(this);
	txv_about.setOnTouchListener(this);
	
	txv_start.setOnClickListener(this);
	txv_exit.setOnClickListener(this);
	txv_about.setOnClickListener(this);
	
	}

	@Override
	public void onClick(View view) {
		switch(view.getId()){
		case R.id.txv_start:
			Intent startIntent=new Intent(this,GameActivity.class);
			startActivity(startIntent);break;
	
		case R.id.txv_exit:
			finish();break;
		
		case R.id.txv_about:
			Intent aboutIntent=new Intent(this,AboutView.class);
			startActivity(aboutIntent);
		}
		
	}

	@Override
	public boolean onTouch(View view, MotionEvent motionEvent) {
	
		switch(motionEvent.getAction()){            
        case MotionEvent.ACTION_DOWN:
         ((TextView) view).setTextColor(0xFF6A5ceD); 
            break;          
        case MotionEvent.ACTION_CANCEL:             
        case MotionEvent.ACTION_UP:
        ((TextView) view).setTextColor(0xF9f9f9f9);
            break;
    } // TODO Auto-generated method stub
		return false;
	}
	
	

}
