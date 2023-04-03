package com.example.hashcache.views;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.hashcache.R;
import com.example.hashcache.models.ScannableCode;

import java.util.List;

/**
 * An array adapter to display scannable code names and scores
 */
public class ScannableCodesArrayAdapter extends ArrayAdapter<ScannableCode> {
    public ScannableCodesArrayAdapter(Context context, List<ScannableCode> scannableCodes) {
        super(context, 0, scannableCodes);
    }

    /**
     * Gets the view for a certain scannable code
     * @param position The position of the item within the adapter's data set of the item whose view
     *        we want.
     * @param convertView The old view to reuse, if possible. Note: You should check that this view
     *        is non-null and of an appropriate type before using. If it is not possible to convert
     *        this view to display the correct data, this method can create a new view.
     *        Heterogeneous lists can specify their number of view types, so that this View is
     *        always of the right type (see {@link #getViewTypeCount()} and
     *        {@link #getItemViewType(int)}).
     * @param parent The parent that this view will eventually be attached to
     * @return view the created View object
     */
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup
            parent) {
        View view;
        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.scannable_code_listview_content,
                    parent, false);
        } else {
            view = convertView;
        }

        ScannableCode scannableCode = getItem(position);

        TextView scannableCodeName = view.findViewById(R.id.scannable_code_name_listview_item);
        TextView scannableCodeScore = view.findViewById(R.id.scannable_code_score_listview_item);

        scannableCodeName.setText(scannableCode.getHashInfo().getGeneratedName());
        scannableCodeScore.setText(Long.toString(scannableCode.getHashInfo().getGeneratedScore()));

        return view;
    }
}
