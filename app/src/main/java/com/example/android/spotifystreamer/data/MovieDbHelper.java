package com.example.android.spotifystreamer.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.android.spotifystreamer.data.MovieContract.MovieEntry;
import com.example.android.spotifystreamer.data.MovieContract.FavoriteEntry;
/**
 * Created by lakshay on 18/4/16.
 */

public class MovieDbHelper extends SQLiteOpenHelper {

    private final String LOG_TAG = MovieDbHelper.class.getSimpleName();

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "movie.db";

    public MovieDbHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase){
        final String SQL_CREATE_MOVIE_TABLE = "CREATE TABLE " + MovieEntry.TABLE_NAME + " (" +
                MovieEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                MovieEntry.COLUMN_TITLE + " TEXT NOT NULL, " +
                MovieEntry.COLUMN_MOVIE_POSTER + " TEXT NOT NULL, " +
                MovieEntry.COLUMN_MOVIE_OVERVIEW + " TEXT NOT NULL, " +
                MovieEntry.COLUMN_RELEASE_DATE + " TEXT NOT NULL, " +
                MovieEntry.COLUMN_USER_RATING + " REAL NOT NULL, "+
                MovieEntry.COLUMN_MOVIE_ID+ " INTEGER UNIQUE ON CONFLICT REPLACE NOT NULL ON CONFLICT ABORT "
                +  " );" ;


        final String SQL_CREATE_FAVOURITE_TABLE = "CREATE TABLE " + FavoriteEntry.TABLE_NAME + " (" +
                FavoriteEntry._ID+ " INTEGER PRIMARY KEY AUTOINCREMENT, "+
                FavoriteEntry.COLUMN_MOVIE_ID+ " INTEGER UNIQUE ON CONFLICT REPLACE NOT NULL ON CONFLICT ABORT, " +
                FavoriteEntry.COLUMN_TITLE+" TEXT NOT NULL, "+
                FavoriteEntry.COLUMN_MOVIE_POSTER+" TEXT NOT NULL, "+
                FavoriteEntry.COLUMN_MOVIE_OVERVIEW+" TEXT NOT NULL, "+
                FavoriteEntry.COLUMN_RELEASE_DATE+" TEXT NOT NULL, "+
                FavoriteEntry.COLUMN_USER_RATING+" REAL NOT NULL "+
                " );";

        sqLiteDatabase.execSQL(SQL_CREATE_MOVIE_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_FAVOURITE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion){

        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS" + MovieEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS" + FavoriteEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
