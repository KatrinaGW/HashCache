package com.example.hashcache.views;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.hashcache.R;

import java.util.ArrayList;

/**

 An ArrayAdapter for displaying location photos in a ListView.
 This adapter is used to populate a ListView with location photo information.
 This adapter inflates the photo_gallery_content layout for each row in the ListView
 and sets the location image and location text.
 */
public class PhotoGalleryArrayAdapter extends ArrayAdapter<Pair<String, Drawable>> {

    /**
     * Constructs a new PhotoGalleryArrayAdapter.
     *
     * @param context The context in which this adapter is being used.
     * @param photoStuff The location text and image to be displayed in the ListView.
     */
    public PhotoGalleryArrayAdapter(Context context, ArrayList<Pair<String, Drawable>> photoStuff) {
        super(context, 0, photoStuff);
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

        Pair<String, Drawable> photoData = getItem(position);

        TextView locationTextView = view.findViewById(R.id.location_text);
        ImageView locationPhotoView = view.findViewById(R.id.location_photo);
        locationTextView.setText(photoData.first);
        locationPhotoView.setImageDrawable(photoData.second);


        return view;
    }
}
