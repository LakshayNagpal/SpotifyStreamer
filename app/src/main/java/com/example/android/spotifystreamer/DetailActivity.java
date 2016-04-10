package com.example.android.spotifystreamer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class DetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public static class DetailFragment extends Fragment {

        private static final String LOG_TAG = DetailFragment.class.getSimpleName();

        public DetailFragment() {
            setHasOptionsMenu(true);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            Intent intent = getActivity().getIntent();
            View rootview =  inflater.inflate(R.layout.fragment_detail, container, false);

            if(intent!=null && intent.hasExtra("overview") && intent.hasExtra("release_date") && intent.hasExtra("title") && intent.hasExtra("vote_average")){
                String overview = intent.getStringExtra("overview");
                ((TextView) rootview.findViewById(R.id.detail_text)).setText(overview);

                String release_date = intent.getStringExtra("release_date");
                ((TextView) rootview.findViewById(R.id.detail_text1)).setText(release_date);

                String title = intent.getStringExtra("title");
                ((TextView) rootview.findViewById(R.id.detail_text2)).setText(title);

                String votes = intent.getStringExtra("vote_average");
                ((TextView) rootview.findViewById(R.id.detail_text3)).setText(votes);

                String url = intent.getStringExtra("url");
                String final_url = "http://image.tmdb.org/t/p/w342/" + url;
                ImageView i = (ImageView) rootview.findViewById(R.id.image_view1);
                Picasso.with(getActivity()).load(final_url).into(i);
            }
            return rootview;
        }
    }
}
