package com.example.android.spotifystreamer;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.example.android.spotifystreamer.data.MovieContract;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Vector;

/**
 * Created by lakshay on 19/4/16.
 */
public class FetchMoviesTask extends AsyncTask<String, Void, Void> {

    private final String LOG_TAG = FetchMoviesTask.class.getSimpleName();
    Context context;

    public FetchMoviesTask(Context mcontext){
        this.context = mcontext;
    }

    private void getMoviesDataFromJson(String moviesjson) throws JSONException{

        try{
            JSONObject moviejson = new JSONObject(moviesjson);
            JSONArray moviesarray = moviejson.getJSONArray("results");

            Vector<ContentValues> cvvector = new Vector<ContentValues>(moviesarray.length());
            final String BASE_URL = "http://image.tmdb.org/t/p/w185/" ;

            for(int i=0;i<moviesarray.length();++i){
                JSONObject movie = moviesarray.getJSONObject(i);
                String poster_path = movie.getString("poster_path");
                String movieoverview = movie.getString("overview");
                String split_release_date = movie.getString("release_date");
                String title = movie.getString("original_title");
                Double vote_average = movie.getDouble("vote_average");
                Double id = movie.getDouble("id");
                String[] parts = split_release_date.split("-");
                String release_date = parts[2] + "/" + parts[1] + "/" + parts[0];


                ContentValues movievalues = new ContentValues();

                movievalues.put(MovieContract.MovieEntry.COLUMN_TITLE, title);
                movievalues.put(MovieContract.MovieEntry.COLUMN_MOVIE_POSTER, BASE_URL + poster_path);
                movievalues.put(MovieContract.MovieEntry.COLUMN_MOVIE_OVERVIEW, movieoverview);
                movievalues.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE, release_date);
                movievalues.put(MovieContract.MovieEntry.COLUMN_USER_RATING, vote_average);
                movievalues.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID, id);

                cvvector.add(movievalues);
            }
            int inserted = 0;
            if(cvvector.size()>0){
                ContentValues[] cvarray = new ContentValues[cvvector.size()];
                cvvector.toArray(cvarray);
                inserted = context.getContentResolver().bulkInsert(MovieContract.MovieEntry.CONTENT_URI, cvarray);
            }
            Log.v(LOG_TAG, "FetchMovieTask Complete. " + inserted + " Inserted");
        }catch (JSONException e){
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }
    }

    @Override
    protected Void doInBackground(String... params){
//        if(params.length == 0){
//            return null;
//        }

        HttpURLConnection urlconnection = null;
        BufferedReader reader = null;
        String moviesjson = null;


        try{
            //String baseurl = "http://api.themoviedb.org/3/discover/movie?";
            String my_api_key = "36cc663e1070334ca21c1c6627d76ad7";

            Uri.Builder builder = new Uri.Builder();
            builder.scheme("http")
                    .authority("api.themoviedb.org")
                    .appendPath("3")
                    .appendPath("movie")
                    .appendPath(params[0])
                    .appendQueryParameter("api_key", my_api_key);

            String myurl = builder.build().toString();

           // URL url = new URL(baseurl + "&api_key=" + my_api_key);
            URL url = new URL(myurl);
            Log.v(LOG_TAG, "Built url" + url);

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
            Log.v(LOG_TAG, "Movie JSON string: " + moviesjson);
            getMoviesDataFromJson(moviesjson);

        }catch (IOException e){
            Log.e(LOG_TAG, "Error", e);
            return null;
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        } finally {
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
        return null;
    }
}
