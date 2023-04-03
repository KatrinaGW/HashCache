package com.example.hashcache.views;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.hashcache.R;
import com.example.hashcache.appContext.AppContext;
import com.example.hashcache.controllers.AddCommentCommand;
import com.example.hashcache.models.Comment;

/**
 * Allows the user to add a comment to a QR code if they have scanned it
 */
public class AddCommentActivity extends AppCompatActivity {
    private Button confirmButtom;
    private TextView bodyEditText;
    boolean belongToCurrentUser;

    /**
     * Called whenever the activity starts
     * @param savedInstanceState If the activity is being re-initialized after
     *     previously being shut down then this Bundle contains the data it most
     *     recently supplied in {@link #onSaveInstanceState}.  <b><i>Note: Otherwise it is null.</i></b>
     *
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_comment);
        Intent intent = getIntent();
        belongToCurrentUser = intent.getBooleanExtra("belongsToCurrentUser", false);
    }

    /**
     * Called whenever the activity resumes
     */
    @Override
    protected void onResume() {
        super.onResume();
        confirmButtom = findViewById(R.id.confirm_add_comment_button);
        bodyEditText = findViewById(R.id.comment_body_edit_text);

        confirmButtom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onConfirmButtonClicked();
            }
        });
    }

    private void onConfirmButtonClicked(){
        String body = bodyEditText.getText().toString();
        String scannableCodeId = AppContext.get().getCurrentScannableCode().getScannableCodeId();
        String userId = AppContext.get().getCurrentPlayer().getUserId();
        AddCommentCommand.AddCommentCommand(new Comment(body, userId), scannableCodeId)
                .thenAccept(voidObject -> {
                    finish();
        });

    }
}
