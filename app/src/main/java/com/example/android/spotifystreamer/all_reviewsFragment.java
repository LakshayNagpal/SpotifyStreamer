package com.example.android.spotifystreamer;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

/**
 * A placeholder fragment containing a simple view.
 */
public class all_reviewsFragment extends Fragment {

    public all_reviewsFragment() {
    }
    String[] reviews;
    ListView listView;
    ArrayAdapter<String> adapter;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Intent intent = getActivity().getIntent();
        View rootView = inflater.inflate(R.layout.fragment_all_reviews, container, false);
        listView = (ListView) rootView.findViewById(R.id.listview1);

        if(intent!=null && intent.hasExtra("reviews")){
            reviews = intent.getStringArrayExtra("reviews");
        }
        adapter = new ArrayAdapter<String>(getActivity(),R.layout.listview_textview, R.id.listview_textview, reviews);
        listView.setAdapter(adapter);

        return rootView;
    }
}
