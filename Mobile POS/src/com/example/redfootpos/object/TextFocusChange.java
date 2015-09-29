package com.example.redfootpos.object;

import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.widget.EditText;

public class TextFocusChange implements OnFocusChangeListener{

	private EditText et;
	
	public TextFocusChange(EditText edt){
		this.et = edt;
	}
	
	@Override
	public void onFocusChange(View v, boolean hasFocus) {
		
		if(hasFocus){
			if(et.getText().toString().equals("0")){
				et.setText("");
			}
		} else{
			if(et.getText().toString().equals("")){
				et.setText("0");
			}
		}
	}

}
