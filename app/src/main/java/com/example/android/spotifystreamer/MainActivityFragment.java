package com.example.android.spotifystreamer;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.example.android.spotifystreamer.data.MovieContract;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{

    GridView gridview;
    String category;
    String category1;
    String[] resultStr;
    MovieGridViewAdapter movieadapter;
    MovieGridViewAdapter movieadapter1;

//    Integer[] imageIDs = {
//            R.drawable.sample_0,
//            R.drawable.sample_1,
//            R.drawable.sample_2,
//            R.drawable.sample_3,
//            R.drawable.sample_4,
//            R.drawable.sample_5,
//            R.drawable.sample_6,
//            R.drawable.sample_7
//    };

    private final String LOG_TAG = MainActivityFragment.class.getSimpleName();
    private int mPosition;

    private static final int MOVIE_LOADER = 0;

    private static final String[] MOVIE_COLUMNS = {

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

    private static final String[] FAVORITE_COLUMNS = {

            MovieContract.FavoriteEntry.TABLE_NAME + "." + MovieContract.FavoriteEntry._ID,
            MovieContract.FavoriteEntry.COLUMN_TITLE,
            MovieContract.FavoriteEntry.COLUMN_MOVIE_POSTER,
            MovieContract.FavoriteEntry.COLUMN_MOVIE_OVERVIEW,
            MovieContract.FavoriteEntry.COLUMN_RELEASE_DATE,
            MovieContract.FavoriteEntry.COLUMN_USER_RATING,
            MovieContract.FavoriteEntry.COLUMN_MOVIE_ID
    };


    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        gridview = (GridView) rootView.findViewById(R.id.gridview);
        //gridview.setAdapter(new ImageAdapter(this.getContext()));

        movieadapter = new MovieGridViewAdapter(getActivity(), null, 0);
        gridview.setAdapter(movieadapter);

        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent,
                                    View v, int position, long id) {
                Cursor cursor = (Cursor) parent.getItemAtPosition(position);
                if(cursor!=null) {
                    ((Callback)getActivity()).onItemSelected(cursor.getLong(COL_MOVIE_ID));
                    mPosition = position;

//                    String url = resultStr[position].split("%")[0];
//                    String movieoverview = resultStr[position].split("%")[1];
//                    String release_date = resultStr[position].split("%")[2];
//                    String title = resultStr[position].split("%")[3];
//                    String vote_average = resultStr[position].split("%")[4];
//                    String movie_id = resultStr[position].split("%")[5];
//
//                    Intent intent = new Intent(getActivity(), DetailActivity.class);
//                    intent.putExtra("url", url);
//                    intent.putExtra("overview", movieoverview);
//                    intent.putExtra("release_date", release_date);
//                    intent.putExtra("title", title);
//                    intent.putExtra("vote_average", vote_average);
//                    intent.putExtra("id", movie_id);
//
//                    startActivity(intent);
                }
            }
        });
        return rootView;
    }

    private void updateweather() {

        Log.v(LOG_TAG, "updateweather function called");
        SharedPreferences shared = PreferenceManager.getDefaultSharedPreferences(getActivity());
        category = shared.getString(getString(R.string.movies_key), "popular");
//        Log.v(LOG_TAG, "update weather called");
//        if(!category.equals("favorites")) {
//            Log.v(LOG_TAG, "if condition in update weather called" + category);
//
//        FetchMoviesTask moviesTask = new FetchMoviesTask(getActivity());
//        moviesTask.execute(category);
//        movieadapter.notifyDataSetChanged();
//        }

        switch (category){
            case "favorites":{
                Log.v(LOG_TAG, "Favorites settings called");
                Cursor favcursor = getContext().getContentResolver().query(
                        MovieContract.FavoriteEntry.CONTENT_URI,
                        null,
                        null,
                        null,
                        null
                );

                Log.v(LOG_TAG, "count of fav cursor" + favcursor.getCount());

                movieadapter = new MovieGridViewAdapter(getActivity(),favcursor,0);
                gridview.setAdapter(movieadapter);

//                while(favcursor.moveToNext()){
//
//                }

                break;
            }
            case "popular":{
                Log.v(LOG_TAG, "Popular settings called");
                FetchMoviesTask moviesTask = new FetchMoviesTask(getActivity());
                moviesTask.execute("popular");

                Cursor popcursor = getContext().getContentResolver().query(
                        MovieContract.MovieEntry.CONTENT_URI,
                        null,
                        null,
                        null,
                        null
                );
                Log.v(LOG_TAG, "count of pop cursor" + popcursor.getCount());

                movieadapter = new MovieGridViewAdapter(getActivity(),popcursor,0);
                gridview.setAdapter(movieadapter);

                break;
            }
            case "top_rated":{
                Log.v(LOG_TAG, "Top Rated settings called");
                FetchMoviesTask moviesTask = new FetchMoviesTask(getActivity());
                moviesTask.execute("top_rated");

                Cursor topcursor = getContext().getContentResolver().query(
                        MovieContract.MovieEntry.CONTENT_URI,
                        null,
                        null,
                        null,
                        null
                );
                Log.v(LOG_TAG, "count of top cursor" + topcursor.getCount());

                movieadapter = new MovieGridViewAdapter(getActivity(),topcursor,0);
                gridview.setAdapter(movieadapter);
                break;
            }
        }
        movieadapter.notifyDataSetChanged();
    }

    @Override
    public void onStart(){
        super.onStart();
        Log.v(LOG_TAG, "on start of Main Activity called");
        Toast.makeText(getActivity(), "Loading... Please Wait", Toast.LENGTH_SHORT).show();
        updateweather();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        getLoaderManager().initLoader(MOVIE_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.v(LOG_TAG, "oncreateloader function called");
        SharedPreferences shared = PreferenceManager.getDefaultSharedPreferences(getActivity());
        category1 = shared.getString(getString(R.string.movies_key), "popular");
        Uri favoriteMovieUri = MovieContract.FavoriteEntry.CONTENT_URI;
        if(category1.equals("favorites")){
            return new CursorLoader(
                    getActivity(),
                    favoriteMovieUri,
                    FAVORITE_COLUMNS,
                    null,
                    null,
                    null
            );
        }
        Uri movieURI = MovieContract.MovieEntry.CONTENT_URI;

        return new CursorLoader(
                getActivity(),
                movieURI,
                MOVIE_COLUMNS,
                null,
                null,
                null
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.v(LOG_TAG, "OnLoadFinished function called");
        movieadapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        Log.v(LOG_TAG, "onLoadReset function called");
        movieadapter.swapCursor(null);
    }

    @Override
    public void onResume(){
        Log.v(LOG_TAG, "Resume function in Main Activity called");
        getLoaderManager().restartLoader(MOVIE_LOADER, null, this);
        super.onResume();
    }

    public interface Callback {
        /**
         * DetailFragmentCallback for when an item has been selected.
         * @param movieId
         */
        void onItemSelected(long movieId);
    }


//    public class FetchMoviesTask extends AsyncTask<String, Void, String[]>{
//
//        private final String LOG_TAG = FetchMoviesTask.class.getSimpleName();
//        private String[] getMovieFromJSON(String moviesjson)
//                throws JSONException{
//
//            JSONObject moviejson = new JSONObject(moviesjson);
//            JSONArray moviesarray = moviejson.getJSONArray("results");
//
//            resultStr = new String[moviesarray.length()];
//
//            for(int i=0;i<moviesarray.length();++i){
//                JSONObject movie = moviesarray.getJSONObject(i);
//
//                String poster_path = movie.getString("poster_path");
//                String movieoverview = movie.getString("overview");
//                String split_release_date = movie.getString("release_date");
//                String title = movie.getString("original_title");
//                Double vote_average = movie.getDouble("vote_average");
//                Double id = movie.getDouble("id");
//                String[] parts = split_release_date.split("-");
//                String release_date = parts[2] + "/" + parts[1] + "/" + parts[0];
//                resultStr[i] = poster_path + "%" + movieoverview + "%" + release_date + "%" + title + "%" + vote_average + "%" + id;
//            }
//            for(String s:resultStr){
//                Log.v(LOG_TAG, "Movies entry:" + s);
//            }
//            return resultStr;
//        }
//
//        @Override
//        protected String[] doInBackground(String... params){
//            if(params.length == 0){
//                return null;
//            }
//            HttpURLConnection urlconnection = null;
//            BufferedReader reader = null;
//            String moviesjson = null;
//
//            //String baseurl = "http://api.themoviedb.org/3/movie/";
//            try{
//                String my_api_key = "36cc663e1070334ca21c1c6627d76ad7";
//                Uri.Builder builder = new Uri.Builder();
//                builder.scheme("http")
//                        .authority("api.themoviedb.org")
//                        .appendPath("3")
//                        .appendPath("movie")
//                        .appendPath(params[0])
//                        .appendQueryParameter("api_key", my_api_key);
//
//                String myurl = builder.build().toString();
//                URL url = new URL(myurl);
//                Log.v(LOG_TAG, "Built url" + myurl);
//
//                urlconnection = (HttpURLConnection) url.openConnection();
//                urlconnection.setRequestMethod("GET");
//                urlconnection.connect();
//
//                InputStream input = urlconnection.getInputStream();
//                StringBuffer buffer = new StringBuffer();
//
//                if(input == null){
//                    return null;
//                }
//                reader = new BufferedReader(new InputStreamReader(input));
//                String line;
//
//                while((line = reader.readLine())!=null){
//                    buffer.append(line+"\n");
//                }
//
//                if(buffer.length() == 0){
//                    return null;
//                }
//                moviesjson = buffer.toString();
//
//                Log.v(LOG_TAG, "Movies JSON String" + moviesjson);
//            }catch (IOException e){
//                Log.e(LOG_TAG,"Error" , e);
//                return null;
//            }finally {
//                if(urlconnection!=null){
//                    urlconnection.disconnect();
//                }
//                if(reader!=null){
//                    try{
//                        reader.close();
//                    }catch(final IOException e){
//                        Log.e(LOG_TAG,"Error closing stream", e);
//                    }
//                }
//            }
//            try{
//                return getMovieFromJSON(moviesjson);
//            }catch (JSONException e){
//                Log.e(LOG_TAG, e.getMessage(), e);
//                e.printStackTrace();
//            }
//            return null;
//        }
//
//        @Override
//        public void onPostExecute(String[] result){
//            if(result!=null){
//                ArrayList<String> urls = new ArrayList<String>(result.length);
//                for(int i=0;i<result.length;++i){
//                    String s = result[i].split("%")[0];
//                    urls.add("http://image.tmdb.org/t/p/w185/" + s);
//                }
//                ImageAdapter imageAdapter = new ImageAdapter(getActivity(),urls);
//                gridview.setAdapter(imageAdapter);
//            }
//        }
//    }





//    public class ImageAdapter extends BaseAdapter
//    {
//        private Context context;
//        private ArrayList<String> movies;
//        private Picasso mpicasso;
//        //private LayoutInflater inflater;
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
//                view = LayoutInflater.from(context).inflate(R.layout.grid_item_movies, parent, false);
//                imageView = (ImageView) view.findViewById(R.id.grid_item_movies);
//            } else {
//                imageView = (ImageView) convertView;
//            }
//            mpicasso.load(movies.get(position)).into(imageView);
//            //imageView.setImageResource(imageIDs[position]);
//            return imageView;
//        }
//    }


}
