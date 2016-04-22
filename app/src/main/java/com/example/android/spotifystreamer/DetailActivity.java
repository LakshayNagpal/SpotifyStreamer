package com.example.android.spotifystreamer;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.spotifystreamer.data.MovieContract;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Vector;

public class DetailActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
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

    public static String movie_id;
    static String[] resultStr;
    static String[] resultStr1;
    static String url;
    ArrayAdapter<String> adapter;
    ArrayList<String> urls;
    static boolean isfavorite = false;
    private static long movieId;

    public static class DetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

        public final String LOG_TAG = DetailFragment.class.getSimpleName();
        private final static int MOVIE_DETAIL_LOADER = 0;
        private final static int FAVORITE_LOADER = 1;


        private final String[] MOVIE_COLUMNS = {

                MovieContract.MovieEntry.TABLE_NAME + "." + MovieContract.MovieEntry._ID,
                MovieContract.MovieEntry.COLUMN_TITLE,
                MovieContract.MovieEntry.COLUMN_MOVIE_POSTER,
                MovieContract.MovieEntry.COLUMN_MOVIE_OVERVIEW,
                MovieContract.MovieEntry.COLUMN_RELEASE_DATE,
                MovieContract.MovieEntry.COLUMN_USER_RATING,
                MovieContract.MovieEntry.COLUMN_MOVIE_ID
        };

        static final int COL_TITLE = 1;
        static final int COL_MOVIE_POSTER = 2;
        static final int COL_MOVIE_OVERVIEW = 3;
        static final int COL_RELEASE_DATE = 4;
        static final int COL_USER_RATING = 5;
        static final int COL_MOVIE_ID = 6;

        private final String[] FAVORITE_COLUMNS = {

                MovieContract.FavoriteEntry.TABLE_NAME + "." + MovieContract.FavoriteEntry._ID,
                MovieContract.FavoriteEntry.COLUMN_TITLE,
                MovieContract.FavoriteEntry.COLUMN_MOVIE_POSTER,
                MovieContract.FavoriteEntry.COLUMN_MOVIE_OVERVIEW,
                MovieContract.FavoriteEntry.COLUMN_RELEASE_DATE,
                MovieContract.FavoriteEntry.COLUMN_USER_RATING,
                MovieContract.FavoriteEntry.COLUMN_MOVIE_ID
        };

        static final int COL_FAV_TITLE = 1;
        static final int COL_FAV_MOVIE_POSTER = 2;
        static final int COL_FAV_MOVIE_OVERVIEW = 3;
        static final int COL_FAV_RELEASE_DATE = 4;
        static final int COL_FAV_USER_RATING = 5;
        static final int COL_FAV_MOVIE_ID = 6;

        public DetailFragment() {
            setHasOptionsMenu(true);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            Intent intent = getActivity().getIntent();
            View rootview =  inflater.inflate(R.layout.fragment_detail, container, false);

            if(intent!=null){
//                String overview = intent.getStringExtra("overview");
//                ((TextView) rootview.findViewById(R.id.detail_text)).setText(overview);
//
//                String release_date = intent.getStringExtra("release_date");
//                ((TextView) rootview.findViewById(R.id.detail_text1)).setText(release_date);
//
//                String title = intent.getStringExtra("title");
//                ((TextView) rootview.findViewById(R.id.detail_text2)).setText(title);
//
//                String votes = intent.getStringExtra("vote_average");
//                ((TextView) rootview.findViewById(R.id.detail_text3)).setText(votes);
//
//                movie_id = intent.getStringExtra("id");
                movieId = intent.getLongExtra(Intent.EXTRA_TEXT,0);
                Log.v(LOG_TAG, "Movie iD" + movieId);
                //url = intent.getStringExtra("url");
            }

//            String final_url = "http://image.tmdb.org/t/p/w185/" + url;
//            ImageView i = (ImageView) rootview.findViewById(R.id.image_view1);
//            Picasso.with(getActivity()).load(final_url).into(i);

            return rootview;
        }

        @Override
        public void onActivityCreated(Bundle savedInstanceState){
            getLoaderManager().initLoader(MOVIE_DETAIL_LOADER, null, this);
            getLoaderManager().initLoader(FAVORITE_LOADER, null, this);
            super.onActivityCreated(savedInstanceState);
        }

        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {

            switch (id){
                case MOVIE_DETAIL_LOADER:{
                    Uri uri = MovieContract.MovieEntry.buildMovieUri(movieId);
                    return new CursorLoader(
                            getActivity(),
                            uri,
                            MOVIE_COLUMNS,
                            null,
                            null,
                            null
                    );
                }
                case FAVORITE_LOADER:{
                    Uri uri = MovieContract.FavoriteEntry.buildFavouriteUri(movieId);
                    return new CursorLoader(
                            getActivity(),
                            uri,
                            FAVORITE_COLUMNS,
                            null,
                            null,
                            null
                    );
                }
            }
            return null;
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

            if(data.moveToFirst() == false){
                return ;
            }

            switch(loader.getId()){
                case MOVIE_DETAIL_LOADER:{

                    ((TextView) getView().findViewById(R.id.detail_text)).setText(data.getString(COL_MOVIE_OVERVIEW));
                    ((TextView) getView().findViewById(R.id.detail_text1)).setText(data.getString(COL_RELEASE_DATE));
                    ((TextView) getView().findViewById(R.id.detail_text2)).setText(data.getString(COL_TITLE));
                    ((TextView) getView().findViewById(R.id.detail_text3)).setText(data.getString(COL_USER_RATING));
                    ImageView i = (ImageView) getView().findViewById(R.id.image_view1);
                    Picasso .with(getActivity()).load(data.getString(COL_MOVIE_POSTER)).into(i);

                    final String title = data.getString(COL_TITLE);
                    final String poster = data.getString(COL_MOVIE_POSTER);
                    final String mov_id = data.getString(COL_MOVIE_ID);
                    final String overview = data.getString(COL_MOVIE_OVERVIEW);
                    final String rel_date = data.getString(COL_RELEASE_DATE);
                    final String user_rat = data.getString(COL_USER_RATING);

                    final Vector<ContentValues> vectorvalues = new Vector<ContentValues>(2);

                    Button fav_button = (Button) getView().findViewById(R.id.fav_button);
                    fav_button.setOnClickListener(new View.OnClickListener(){
                        @Override
                        public void onClick(View v){
                            if(isfavorite == false){
                                ContentValues c = new ContentValues();
                                c.put(MovieContract.FavoriteEntry.COLUMN_MOVIE_ID, mov_id);
                                Log.v(LOG_TAG, "movie Id inserted" + mov_id);
                                c.put(MovieContract.FavoriteEntry.COLUMN_MOVIE_OVERVIEW, overview);
                                Log.v(LOG_TAG, "oevrview inserted" + overview);
                                c.put(MovieContract.FavoriteEntry.COLUMN_MOVIE_POSTER, poster);
                                Log.v(LOG_TAG, "poster inserted" + poster);
                                c.put(MovieContract.FavoriteEntry.COLUMN_RELEASE_DATE, rel_date);
                                Log.v(LOG_TAG, "release date inserted" + rel_date);
                                c.put(MovieContract.FavoriteEntry.COLUMN_TITLE, title);
                                Log.v(LOG_TAG, "title inserted" + title);
                                c.put(MovieContract.FavoriteEntry.COLUMN_USER_RATING, user_rat);
                                Log.v(LOG_TAG, "User Rating  inserted" + user_rat);

                                vectorvalues.add(c);

                                int inserted = 0;
                                if(vectorvalues.size()>0){
                                    ContentValues[] cvarray = new ContentValues[vectorvalues.size()];
                                    vectorvalues.toArray(cvarray);

                                    inserted = getActivity().getContentResolver().bulkInsert(MovieContract.FavoriteEntry.CONTENT_URI, cvarray);
                                    //isfavorite = true;
                                    Toast.makeText(getActivity(), "Movie Added to Favorites Section", Toast.LENGTH_SHORT).show();

                                    Log.v(LOG_TAG, "Favorites complete" + inserted + "Inserted.");

                                }
                            }
                        }
                    });
                }
                case FAVORITE_LOADER:{

                }
            }
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {

        }
    }

    @Override
    public void onStart(){
        super.onStart();
        FetchTrailerTask moviesTask = new FetchTrailerTask();
        moviesTask.execute(String.valueOf(movieId));

        FetchReviewDetail reviews = new FetchReviewDetail();
        reviews.execute(String.valueOf(movieId));
    }

    public class FetchTrailerTask extends AsyncTask<String, Void, String[]>{
        private final String LOG_TAG = FetchTrailerTask.class.getSimpleName();

        private String[] getMovieDetailFromJSON(String detailjson)
                throws JSONException {

            JSONObject moviejson = new JSONObject(detailjson);
            JSONObject trailers = moviejson.getJSONObject("trailers");
            JSONArray youtube = trailers.getJSONArray("youtube");

            resultStr = new String[youtube.length()];

            for(int i=0;i<youtube.length();++i){
                JSONObject movie = youtube.getJSONObject(i);

                String url_id = movie.getString("source");
                resultStr[i] = url_id;
            }
            for(String s:resultStr){
                Log.v(LOG_TAG, "URL:" + s);
            }

            return resultStr;
        }

        @Override
        protected String[] doInBackground(String... params){
            if(params.length == 0){
                return null;
            }
            HttpURLConnection urlconnection = null;
            BufferedReader reader = null;
            String detailjson = null;


            try{
                String my_api_key = "36cc663e1070334ca21c1c6627d76ad7";
                Uri.Builder builder = new Uri.Builder();
                builder.scheme("http")
                        .authority("api.themoviedb.org")
                        .appendPath("3")
                        .appendPath("movie")
                        .appendPath(params[0])
                        .appendQueryParameter("api_key", my_api_key)
                        .appendQueryParameter("append_to_response", "trailers");

                String myurl = builder.build().toString();
                URL url = new URL(myurl);
                Log.v(LOG_TAG, "Built url" + myurl);

                urlconnection = (HttpURLConnection) url.openConnection();
                urlconnection.setRequestMethod("GET");
                urlconnection.connect();

                InputStream input = urlconnection.getInputStream();
                StringBuffer buffer = new StringBuffer();

                if(input == null){
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(input));
                String line;

                while((line = reader.readLine())!=null){
                    buffer.append(line+"\n");
                }

                if(buffer.length() == 0){
                    return null;
                }
                detailjson = buffer.toString();

                Log.v(LOG_TAG, "Movies JSON String" + detailjson);
            }catch (IOException e){
                Log.e(LOG_TAG,"Error" , e);
                return null;
            }finally {
                if(urlconnection!=null){
                    urlconnection.disconnect();
                }
                if(reader!=null){
                    try{
                        reader.close();
                    }catch(final IOException e){
                        Log.e(LOG_TAG,"Error closing stream", e);
                    }
                }
            }
            try{
                return getMovieDetailFromJSON(detailjson);
            }catch(JSONException e){
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }
            return null;
        }

        @Override
        public void onPostExecute(String[] result){
                ImageView imageView = (ImageView) findViewById(R.id.trailer1);
                if(result.length!=0){
                    Picasso.with(getBaseContext()).load("http://img.youtube.com/vi/" + result[0] + "/hqdefault.jpg").into(imageView);
                }
                else{
                    //https://i.ytimg.com/vi/DH3ItsuvtQg/hqdefault.jpg
                    Picasso.with(getBaseContext()).load(R.drawable.hqdefault).into(imageView);
                }

//                urls = new ArrayList<String>(result.length);
//                for(int i=0;i<result.length;++i){
//                    urls.add("http://img.youtube.com/vi/" + result[i] + "/1.jpg");
//                }
//                gridview1 = (GridView) findViewById(R.id.gridview1);
//                ImageAdapter imageAdapter = new ImageAdapter(getBaseContext(),urls);
//                gridview1.setAdapter(imageAdapter);

        }
    }

    public class FetchReviewDetail extends AsyncTask<String, Void, String[]>{
        private final String LOG_TAG = FetchReviewDetail.class.getSimpleName();

        private String[] getMovieDetailFromJSON(String detailjson)
                throws JSONException {

            JSONObject moviejson = new JSONObject(detailjson);
            JSONObject reviews = moviejson.getJSONObject("reviews");
            JSONArray people = reviews.getJSONArray("results");

            resultStr1 = new String[people.length()];

            for(int i=0;i<people.length();++i){
                JSONObject movie = people.getJSONObject(i);

                String content = movie.getString("content");

                content = content.replaceAll("(\\r|\\n)", "");
                resultStr1[i] = content;
            }
            for(String s:resultStr1){
                Log.v(LOG_TAG, "content:" + s);
            }

            return resultStr1;
        }

        @Override
        protected String[] doInBackground(String... params){
            if(params.length == 0){
                return null;
            }
            HttpURLConnection urlconnection = null;
            BufferedReader reader = null;
            String detailjson = null;

            try{
                String my_api_key = "36cc663e1070334ca21c1c6627d76ad7";
                Uri.Builder builder = new Uri.Builder();
                builder.scheme("http")
                        .authority("api.themoviedb.org")
                        .appendPath("3")
                        .appendPath("movie")
                        .appendPath(params[0])
                        .appendQueryParameter("api_key", my_api_key)
                        .appendQueryParameter("append_to_response", "reviews");

                String myurl = builder.build().toString();
                URL url = new URL(myurl);
                Log.v(LOG_TAG, "Built url" + myurl);

                urlconnection = (HttpURLConnection) url.openConnection();
                urlconnection.setRequestMethod("GET");
                urlconnection.connect();

                InputStream input = urlconnection.getInputStream();
                StringBuffer buffer = new StringBuffer();

                if(input == null){
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(input));
                String line;

                while((line = reader.readLine())!=null){
                    buffer.append(line+"\n");
                }

                if(buffer.length() == 0){
                    return null;
                }
                detailjson = buffer.toString();

                Log.v(LOG_TAG, "Movies JSON String" + detailjson);
            }catch (IOException e){
                Log.e(LOG_TAG,"Error" , e);
                return null;
            }finally {
                if(urlconnection!=null){
                    urlconnection.disconnect();
                }
                if(reader!=null){
                    try{
                        reader.close();
                    }catch(final IOException e){
                        Log.e(LOG_TAG,"Error closing stream", e);
                    }
                }
            }
            try{
                return getMovieDetailFromJSON(detailjson);
            }catch(JSONException e){
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }
            return null;
        }

        @Override
        public void onPostExecute(String[] result){
                TextView textView = (TextView) findViewById(R.id.review1);
                if(result.length!=0) {
                    textView.setText(result[0]);
                }
                else{
                    textView.setText("No Reviews Found");
                }
//                adapter = new ArrayAdapter<String>(getBaseContext(), R.layout.listview_textview, R.id.listview_textview, result);
//
//                listview = (ListView) findViewById(R.id.listview1);
//                listview.setAdapter(adapter);

        }
    }

//    public class ImageAdapter extends BaseAdapter
//    {
//        private Context context;
//        private ArrayList<String> movies;
//        private Picasso mpicasso;;
//
//        public ImageAdapter(Context c, ArrayList<String> movies)
//        {
//            this.context = c;
//            this.movies = movies;
//            mpicasso = Picasso.with(context);
//        }
//
//        //---returns the number of images---
//        public int getCount() {
//            return movies.size();
//        }
//
//        //---returns the ID of an item---
//        public Object getItem(int position) {
//            return position;
//        }
//
//        public long getItemId(int position) {
//            return position;
//        }
//
//        //---returns an ImageView view---
//        public View getView(int position, View convertView, ViewGroup parent)
//        {   View view;
//            ImageView imageView;
//            if (convertView == null) {
//                view = LayoutInflater.from(context).inflate(R.layout.grid_item_movies_detail, parent, false);
//                imageView = (ImageView) view.findViewById(R.id.grid_item_movies_detail);
//            } else {
//                imageView = (ImageView) convertView;
//            }
//            mpicasso.load(movies.get(position)).into(imageView);
//            return imageView;
//        }
//    }

    public void open_trailer(View view){
        if(resultStr.length!=0) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v=" + resultStr[0])));
        }
    }

    public void trailers_all(View view){
        if(resultStr.length!=0){

            Intent intent = new Intent(getBaseContext(),all_trailers.class);
            intent.putExtra("trailers", resultStr);
            startActivity(intent);
        }
        else{
            Toast.makeText(getBaseContext(), "Sorry, No Trailers Found!", Toast.LENGTH_SHORT).show();
        }
    }

    public void reviews_all(View view){
        if(resultStr1.length!=0){

            Intent intent = new Intent(getBaseContext(), all_reviews.class);
            intent.putExtra("reviews",resultStr1);
            startActivity(intent);
        }
        else{
            Toast.makeText(getBaseContext(), "Sorry, No Reviews Found!", Toast.LENGTH_SHORT).show();
        }
    }
}
