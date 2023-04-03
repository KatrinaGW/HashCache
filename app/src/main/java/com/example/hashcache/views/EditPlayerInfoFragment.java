package com.example.hashcache.views;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.hashcache.R;
import com.example.hashcache.appContext.AppContext;
import com.example.hashcache.controllers.UpdateContactInfoCommand;
import com.example.hashcache.models.ContactInfo;
import com.example.hashcache.models.database.Database;

/**
 * Fragment to allow users to edit their contact information
 */
public class EditPlayerInfoFragment extends Fragment {
    EditText emailEditText;
    EditText phoneNumberEditText;
    ImageButton xButton;
    Button confirmButton;
    EditPlayerInfoFragmentDismisser dismisser;
    TextView invalidInputText;

    interface EditPlayerInfoFragmentDismisser{
        void dismissFragment(ContactInfo newContactInfo);
    }

    /**
     * Called when the view is created
     * @param inflater The LayoutInflater object that can be used to inflate
     * any views in the fragment,
     * @param container If non-null, this is the parent view that the fragment's
     * UI should be attached to.  The fragment should not add the view itself,
     * but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     *
     * @return the created View object
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        super.onCreateView(inflater, container, savedInstanceState);

        return inflater.inflate(R.layout.fragment_edit_player_info,
                container, false);
    }

    /**
     * Called when the view is attached to an activity
     * @param context the context that the fragment is attached to
     */
    @Override
    public void onAttach(@NonNull Context context){
        super.onAttach(context);

        if(context instanceof EditPlayerInfoFragmentDismisser){
            dismisser = (EditPlayerInfoFragmentDismisser) context;
        }
    }

    /**
     * Called after the view is created
     * @param view The View returned by {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){
        init();
        setListeners();
    }

    private void init(){
        ContactInfo contactInfo = AppContext.get().getCurrentPlayer().getContactInfo();
        confirmButton = getView().findViewById(R.id.confirm_player_info_button);
        xButton = getView().findViewById(R.id.x_button);
        emailEditText = getView().findViewById(R.id.email_edit_text);
        phoneNumberEditText = getView().findViewById(R.id.edit_phone_number);
        invalidInputText = getView().findViewById(R.id.invalid_input_text);
        invalidInputText.setVisibility(View.GONE);
        if(!contactInfo.getEmail().equals("")){
            emailEditText.setText(contactInfo.getEmail());
        }
        if(!contactInfo.getPhoneNumber().equals("")){
            phoneNumberEditText.setText(contactInfo.getPhoneNumber());
        }
    }

    private void setListeners(){
        xButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismisser.dismissFragment(null);
            }
        });

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onConfirmClicked();
            }
        });
    }

    private void onConfirmClicked(){
        String emailText = emailEditText.getText().toString();
        String phoneNumberText = phoneNumberEditText.getText().toString();
        ContactInfo newContactInfo = new ContactInfo();

        if(newContactInfo.testValidEmail(emailText) && newContactInfo.testValidPhoneNumber(phoneNumberText)){
            newContactInfo.setPhoneNumber(phoneNumberText);
            newContactInfo.setEmail(emailText);

            dismisser.dismissFragment(newContactInfo);
        }else{
            invalidInputText.setVisibility(View.VISIBLE);
        }
    }


}
