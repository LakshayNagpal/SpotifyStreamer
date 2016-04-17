package com.example.android.spotifystreamer;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

/**
 * A placeholder fragment containing a simple view.
 */
public class all_trailersFragment extends Fragment {

    public all_trailersFragment() {
    }

    String[] trailers;
    GridView gridView;
    String[] xyz;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Intent intent = getActivity().getIntent();
        View rootview = inflater.inflate(R.layout.fragment_all_trailers, container, false);
        gridView = (GridView) rootview.findViewById(R.id.gridview_trailer);

        if(intent!=null && intent.hasExtra("trailers")){
            xyz = intent.getStringArrayExtra("trailers");
        }
        trailers = new String[xyz.length];
        for(int i=0;i<xyz.length;++i){
            trailers[i] = "http://img.youtube.com/vi/" + xyz[i] + "/hqdefault.jpg";
        }

        ImageAdapter imageAdapter = new ImageAdapter(getContext(),trailers);
        gridView.setAdapter(imageAdapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent,
                                    View v, int position, long id) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v=" + xyz[position])));
            }
        });

        return rootview;
    }

    public class ImageAdapter extends BaseAdapter
    {
        private Context context;
        private String[] movies;
        private Picasso mpicasso;
        //private LayoutInflater inflater;

        public ImageAdapter(Context c, String[] movies)
        {
            this.context = c;
            this.movies = movies;
            mpicasso = Picasso.with(context);
        }

        //---returns the number of images---
        public int getCount() {
            return movies.length;
        }

        //---returns the ID of an item---
        public Object getItem(int position) {
            return position;
        }

        public long getItemId(int position) {
            return position;
        }

        //---returns an ImageView view---
        public View getView(int position, View convertView, ViewGroup parent)
        {   View view;
            ImageView imageView;
            if (convertView == null) {
                view = LayoutInflater.from(context).inflate(R.layout.grid_item_movies, parent, false);
                imageView = (ImageView) view.findViewById(R.id.grid_item_movies);
            } else {
                imageView = (ImageView) convertView;
            }
            mpicasso.load(movies[position]).into(imageView);
            //imageView.setImageResource(imageIDs[position]);
            return imageView;
        }
    }


}
