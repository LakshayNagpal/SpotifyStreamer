package com.example.android.spotifystreamer;

import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

/**
 * Created by lakshay on 19/4/16.
 */
public class MovieGridViewAdapter extends CursorAdapter {

    private final String LOG_TAG = MovieGridViewAdapter.class.getSimpleName();
    String[] contents;

    public MovieGridViewAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    private String[] convertCursorRowToUXFormat(Cursor cursor){

        contents = new String[6];
        contents[0] = cursor.getString(MainActivityFragment.COL_TITLE);
        contents[1] = cursor.getString(MainActivityFragment.COL_MOVIE_POSTER);
        contents[2] = cursor.getString(MainActivityFragment.COL_MOVIE_OVERVIEW);
        contents[3] = cursor.getString(MainActivityFragment.COL_RELEASE_DATE);
        contents[4] = cursor.getString(MainActivityFragment.COL_USER_RATING);
        contents[5] = cursor.getString(MainActivityFragment.COL_MOVIE_ID);

        return contents;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent){
        Log.v(LOG_TAG, "new view called");
        View newView = LayoutInflater.from(context).inflate(R.layout.grid_item_movies, parent, false);
        return newView;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor){
        DatabaseUtils.dumpCursor(cursor);
        String[] bind = convertCursorRowToUXFormat(cursor);
        ImageView imageView = (ImageView) view.findViewById(R.id.grid_item_movies);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

        if(imageView!=null){
            Uri uri = Uri.parse("http://image.tmdb.org/t/p/w185/" + bind[1]);
            Picasso.with(context).load(uri).into(imageView);
            imageView.setAdjustViewBounds(true);
        }
    }
}
