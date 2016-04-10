package com.example.android.spotifystreamer;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

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

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    GridView gridview;
    //ArrayList<Uri> list;
    //String category = "popular";
    String category;
    Integer[] imageIDs = {
            R.drawable.sample_0,
            R.drawable.sample_1,
            R.drawable.sample_2,
            R.drawable.sample_3,
            R.drawable.sample_4,
            R.drawable.sample_5,
            R.drawable.sample_6,
            R.drawable.sample_7
    };

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        gridview = (GridView) rootView.findViewById(R.id.gridview);
        //gridview.setAdapter(new ImageAdapter(this.getContext()));

        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent,
                                    View v, int position, long id) {
                Toast.makeText(getActivity(),
                        "pic" + (position + 1) + " selected",
                        Toast.LENGTH_SHORT).show();
            }
        });
        return rootView;
    }

    private void updateweather(){

        SharedPreferences shared = PreferenceManager.getDefaultSharedPreferences(getActivity());
        category = shared.getString(getString(R.string.movies_key), "popular");
        if(category.equals("top_rated")){
            category = "top_rated";
        }
        FetchMoviesTask moviesTask = new FetchMoviesTask();
        moviesTask.execute(category);
    }

    @Override
    public void onStart(){
        super.onStart();
        updateweather();
    }

    public class FetchMoviesTask extends AsyncTask<String, Void, String[]>{

        private final String LOG_TAG = FetchMoviesTask.class.getSimpleName();
        private String[] getMovieFromJSON(String moviesjson)
                throws JSONException{

            JSONObject moviejson = new JSONObject(moviesjson);
            JSONArray moviesarray = moviejson.getJSONArray("results");

            String[] resultStr = new String[moviesarray.length()];

            for(int i=0;i<moviesarray.length();++i){
                JSONObject movie = moviesarray.getJSONObject(i);

                String poster_path = movie.getString("poster_path");
                String movieoverview = movie.getString("overview");
                String split_release_date = movie.getString("release_date");
                String title = movie.getString("title");
                Double vote_average = movie.getDouble("vote_average");
                String[] parts = split_release_date.split("-");
                String release_date = parts[2] + "-" + parts[1] + "-" + parts[0];
                resultStr[i] = poster_path + "-" + movieoverview + "-" + release_date + "-" + title + "-" + vote_average;
            }
            for(String s:resultStr){
                Log.v(LOG_TAG, "Movies entry:" + s);
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
            String moviesjson = null;

            //String baseurl = "http://api.themoviedb.org/3/movie/";
            try{
                String my_api_key = "abc";
                Uri.Builder builder = new Uri.Builder();
                builder.scheme("http")
                        .authority("api.themoviedb.org")
                        .appendPath("3")
                        .appendPath("movie")
                        .appendPath(params[0])
                        .appendQueryParameter("api_key", my_api_key);

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
                moviesjson = buffer.toString();

                Log.v(LOG_TAG, "Movies JSON String" + moviesjson);
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
                return getMovieFromJSON(moviesjson);
            }catch (JSONException e){
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }
            return null;
        }

        @Override
        public void onPostExecute(String[] result){
            if(result!=null){
                ArrayList<String> urls = new ArrayList<String>(result.length);
                for(int i=0;i<result.length;++i){
                    String s = result[i].split("-")[0];
                    urls.add("http://image.tmdb.org/t/p/w185/" + s);
                }
                ImageAdapter imageAdapter = new ImageAdapter(getActivity(),urls);
                gridview.setAdapter(imageAdapter);
            }
        }
    }
    public class ImageAdapter extends BaseAdapter
    {
        private Context context;
        private ArrayList<String> movies;
        private Picasso mpicasso;
        //private LayoutInflater inflater;

        public ImageAdapter(Context c, ArrayList<String> movies)
        {
            this.context = c;
            this.movies = movies;
            mpicasso = Picasso.with(context);
        }

        //---returns the number of images---
        public int getCount() {
            return movies.size();
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
            mpicasso.load(movies.get(position)).into(imageView);
            //imageView.setImageResource(imageIDs[position]);
            return imageView;
        }
    }
}
