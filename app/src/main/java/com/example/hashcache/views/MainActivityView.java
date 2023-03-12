package com.example.hashcache.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.hashcache.R;
/**
 * MainActivityView
 *
 * The MainActivityView class is a custom view that provides a layout for the MainActivity.
 *
 * It allows the MainActivity to access the start button and username field, and provides methods to set and get the
 * username entered by the user.
 *
 * @see RelativeLayout
 * @see TextView
 */

public class MainActivityView extends RelativeLayout {

    /**
     * Constructs a new MainActivityView.
     *
     * @param context the context in which the view is created
     * @param attrs the set of attributes associated with the view
     */
    public MainActivityView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    /**
     * Sets the listener for the start button.
     *
     * @param listeners the listener to be set for the start button
     * @see OnClickListener
     */
    public void setStartBtnListener(OnClickListener listeners) {
        findViewById(R.id.start_button);
    }
    /**
     * Gets the username entered by the user.
     *
     * @return the username entered by the user
     * @see TextView
     */
    public String getUsernameField(){
        TextView tx = this.findViewById(R.id.username_edittext);
        return tx.getText().toString();
    }
    /**
     * Sets the username entered by the user.
     *
     * @param username the username to be set
     * @see TextView
     */
    public void setUsernameField(String username){
        TextView tx = this.findViewById(R.id.username_edittext);
        tx.setText(username);
    }
}
