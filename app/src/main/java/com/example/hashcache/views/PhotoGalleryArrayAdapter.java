package com.example.hashcache.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Base64;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.hashcache.R;
import com.example.hashcache.models.CodeMetadata;

import java.util.ArrayList;
import java.util.HashMap;

/**

 An ArrayAdapter for displaying location photos in a ListView.
 This adapter is used to populate a ListView with location photo information.
 This adapter inflates the photo_gallery_content layout for each row in the ListView
 and sets the location image and location text.
 */
public class PhotoGalleryArrayAdapter extends ArrayAdapter<HashMap<String, Object>> {
     Context context;

    /**
     * Constructs a new PhotoGalleryArrayAdapter.
     *
     * @param context The context in which this adapter is being used.
     * @param photoStuff The location image and text to be displayed in the ListView.
     */
    public PhotoGalleryArrayAdapter(Context context, ArrayList<HashMap<String, Object>> photoStuff) {
        super(context, 0, photoStuff);
        this.context = context;
    }


    /**
     * Returns the View for the specified position in the ListView.
     *
     * @param position The position of the item within the adapter's data set.
     * @param convertView The old view to reuse, if possible.
     * @param parent The parent that this view will eventually be attached to.
     * @return The View corresponding to the specified position in the ListView.
     */
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup
            parent) {
        View view;
        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.photo_gallery_content,
                    parent, false);
        } else {
            view = convertView;
        }

        // Pair<photo str, location str>
        HashMap<String, Object> photoData = getItem(position);

        CodeMetadata cm = (CodeMetadata) photoData.get("codeMetadata");

        LinearLayout locationLayout = view.findViewById(R.id.location_layout);
        TextView locationTextView = view.findViewById(R.id.location_text);
        if(cm.hasLocation()){

            locationTextView.setText((String)photoData.get("locationText"));
        }
        else{
            // If it doesn't have a location we will only show the image with the username of who took it
//            locationLayout.setVisibility(View.GONE);
            locationTextView.setText("No location available.\nGeolocation recording was disabled.");
        }
        TextView usernameTextView = view.findViewById(R.id.username_text);
        usernameTextView.setText((String)photoData.get("userName"));

        // set location photo
        ImageView locationPhotoView = view.findViewById(R.id.location_photo);
        Drawable drawable = makeDrawable((String)photoData.get("base64Image"));
        locationPhotoView.setImageDrawable(drawable);

        return view;
    }

    private Drawable makeDrawable(String base64Image) {
        byte[] decodedImage = Base64.decode(base64Image, Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(decodedImage, 0, decodedImage.length);
        Bitmap rotatedBitmap = RotateBitmap(bitmap, 90);
        Drawable drawable = new BitmapDrawable(context.getResources(), rotatedBitmap);

        return drawable;
    }

    private static Bitmap RotateBitmap(Bitmap bitmap, float angle)
    {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }
}
