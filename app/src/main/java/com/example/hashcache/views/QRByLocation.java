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

public class QRByLocation extends AppCompatActivity {
    private ListView locationListView;
    private TextView qrNearTextView;
    private RadioGroup rangeChoices;
    private RadioButton oneKmRadioButton;
    private RadioButton fiveKmRadioButton;
    private RadioButton tenKmRadioButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        init();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.qr_by_location);

        // add functionality to menu button
        ImageButton menuButton = findViewById(R.id.menu_button);
        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // create menu
                PopupMenu menu = new PopupMenu(QRByLocation.this, menuButton);
                menu.getMenuInflater()
                        .inflate(R.menu.fragment_popup_menu, menu.getMenu());

                // navigate to different activities based on menu item selected
                menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        int id = item.getItemId();

                        if (id == R.id.menu_home) {                 // go to AppHome page
                            startActivity(new Intent(QRByLocation.this, AppHome.class));
                            return true;

                        } else if (id == R.id.menu_stats) {         // go to QRStats page
                            startActivity(new Intent(QRByLocation.this, QRStats.class));
                            return true;

                        } else if (id == R.id.menu_profile) {       // go to MyProfile
                            startActivity(new Intent(QRByLocation.this, MyProfile.class));
                            return true;

                        } else if (id == R.id.menu_community) {     // go to Community
                            startActivity(new Intent(QRByLocation.this, Community.class));
                            return true;
                        }
                        return QRByLocation.super.onOptionsItemSelected(item);
                    }
                });
                menu.show();
            }
        });
    }

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
