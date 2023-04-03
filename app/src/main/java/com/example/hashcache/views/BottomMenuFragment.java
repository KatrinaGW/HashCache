package com.example.hashcache.views;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.example.hashcache.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

/**

 Represents the main navigation menu for the app.

 Slides up from bottom of the screen to display navigation buttons for the app's main pages
 (Map, Leaderboard, Scan QR, My Profile, and Community).
 Can be hidden by clicking away anywhere on the screen.
 */

public class BottomMenuFragment extends BottomSheetDialogFragment {

    public BottomMenuFragment() {
    }

    /**
     * Called when the activity is started
     * @param savedInstanceState If the fragment is being re-created from
     * a previous saved state, this is the state.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    /**
     * Called after the view is created
     * @param inflater The LayoutInflater object that can be used to inflate
     * any views in the fragment,
     * @param viewGroup If non-null, this is the parent view that the fragment's
     * UI should be attached to.  The fragment should not add the view itself,
     * but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     *
     * @return view the created view
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bottom_menu, viewGroup, false);

        // add functionality to map button
        ImageButton mapButton = view.findViewById(R.id.map_button);
        mapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), AppHome.class));
            }
        });

        // add functionality to leaderboard button
        ImageButton leaderboardButton = view.findViewById(R.id.leaderboard_button);
        leaderboardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), LeaderboardScoreActivity.class));
            }
        });

        // add functionality to scan QR button
        ImageButton scanQRButton = view.findViewById(R.id.scan_qr_button);
        scanQRButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), QRScanActivity.class));
            }
        });

        // add functionality to my QR codes button
        ImageButton myCodesButton = view.findViewById(R.id.my_codes_button);
        myCodesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), MyProfile.class));
            }
        });

        // add functionality to community button
        ImageButton communityButton = view.findViewById(R.id.community_button);
        communityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), Community.class));
            }
        });

        return view;
    }
}

