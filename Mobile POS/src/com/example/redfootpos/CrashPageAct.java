package com.example.redfootpos;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class CrashPageAct extends Activity{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.act_crash_page);
		
		String val = getIntent().getStringExtra("error");
		
		((EditText)findViewById(R.id.etErrorView)).setText(val);
		((Button)findViewById(R.id.btnErrrExit)).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});

	}

}
