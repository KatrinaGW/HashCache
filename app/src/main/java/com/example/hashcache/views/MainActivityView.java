package com.example.hashcache.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.hashcache.R;

public class MainActivityView extends RelativeLayout {
    public MainActivityView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setStartBtnListener(OnClickListener listeners) {
        findViewById(R.id.start_button);
    }
    public String getUsernameField(){
        TextView tx = this.findViewById(R.id.username_edittext);
        return tx.getText().toString();
    }

    public void setUsernameField(String username){
        TextView tx = this.findViewById(R.id.username_edittext);
        tx.setText(username);
    }
}
