/*
 * QRByLocation
 *
 * Page displaying QR codes scanned within a selected radius.
 * Default range is _______.
 * Selecting radio buttons changes the range of displayed QR codes.
 * Selecting a QR code navigates to that monster's info page.
 * Additional buttons permit navigation to other pages.
 */

package com.example.hashcache.views;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;

import com.example.hashcache.R;
/**

 QRByLocation
 This activity displays QR codes that have been scanned within a selected radius.
 The default range is _______ and can be changed by selecting one of three radio buttons.
 Selecting a QR code navigates to that monster's info page. Additional buttons permit navigation to other pages.
 */
public class QRByLocation extends AppCompatActivity {
    private ListView locationListView;
    private TextView qrNearTextView;
    private RadioGroup rangeChoices;
    private RadioButton oneKmRadioButton;
    private RadioButton fiveKmRadioButton;
    private RadioButton tenKmRadioButton;
    /**
     * Called when the activity is created.
     *
     * Initializes the activity by setting up the UI elements and adding functionality to the buttons.
     *
     * @param savedInstanceState saved state of the activity, if it was previously closed.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        init();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.qr_by_location);

        // add functionality to menu button
        ImageButton menuButton = findViewById(R.id.menu_button);
        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BottomMenuFragment bottomMenu = new BottomMenuFragment();
                bottomMenu.show(getSupportFragmentManager(), bottomMenu.getTag());
            }
        });
    }
    /**
     * Adds functionality to radio buttons.
     *
     * Called when a radio button is clicked. Checks which radio button was clicked and
     * displays the appropriate range of QR codes.
     *
     * @param view the radio button that was clicked
     */
    // add functionality for radio buttons
    public void onRadioButtonClicked(View view) {

        boolean checked = ((RadioButton) view).isChecked();

        // check which radio button was clicked
        int id = view.getId();

        if (id == R.id.onekm_radiobutton) {
            if (checked) {
                // show QR codes found within 1 km radius
            }
        } else if (id == R.id.fivekm_radiobutton) {
            if (checked) {
                // show QR codes found within 5 km radius
            }
        } else if (id == R.id.tenkm_radiobutton) {
            if (checked) {
                // show QR codes found within 10 km radius
            }
        }
    }
    private void init() {

        locationListView = findViewById(R.id.location_listview);
        qrNearTextView = findViewById(R.id.qr_near_textview);
        rangeChoices = findViewById(R.id.range_choices);
        oneKmRadioButton = findViewById(R.id.onekm_radiobutton);
        fiveKmRadioButton = findViewById(R.id.fivekm_radiobutton);
        tenKmRadioButton = findViewById(R.id.tenkm_radiobutton);
    }

    public ListView getLocationListView() {
        return locationListView;
    }

    public TextView getQrNearTextView() {
        return qrNearTextView;
    }

    public RadioGroup getRangeChoices() {
        return rangeChoices;
    }

    public RadioButton getOneKmRadioButton() {
        return oneKmRadioButton;
    }

    public RadioButton getFiveKmRadioButton() {
        return fiveKmRadioButton;
    }

    public RadioButton getTenKmRadioButton() {
        return tenKmRadioButton;
    }

    public void setOneKmRadioButtonChecked(boolean checked) {
        oneKmRadioButton.setChecked(checked);
    }

    public void setFiveKmRadioButtonChecked(boolean checked) {
        fiveKmRadioButton.setChecked(checked);
    }

    public void setTenKmRadioButtonChecked(boolean checked) {
        tenKmRadioButton.setChecked(checked);
    }
}
