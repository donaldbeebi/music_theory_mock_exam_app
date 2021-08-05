package com.donald.musictheoryapp;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class FeedReaderDbHelper extends SQLiteOpenHelper
{
    Context m_Context;

    private static final String SQL_CREATE_ENTRIES = "CREATE TABLE " +
        FeedReaderContract.FeedEntry.TABLE_NAME + " (" + FeedReaderContract.FeedEntry._ID +
        "INTEGER PRIMARY KEY," + FeedReaderContract.FeedEntry.COLUMN_NAME_TITLE + " TEXT," +
        FeedReaderContract.FeedEntry.COLUMN_NAME_SUBTITLE + " TEXT)";
    private static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + FeedReaderContract.FeedEntry.TABLE_NAME;
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "FeedReader.db";

    public FeedReaderDbHelper(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        m_Context = context.getApplicationContext();
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        //db.execSQL(SQL_CREATE_ENTRIES);
        Log.d("db helper", "onCreate called");
        db.beginTransaction();
        try
        {
            executeFromFile(db, "database_initialization.sql");
            db.setTransactionSuccessful();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        finally
        {
            db.endTransaction();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        onUpgrade(db, oldVersion, newVersion);
    }

    private void executeFromFile(SQLiteDatabase db, String fileName) throws IOException
    {
        InputStream inputStream = m_Context.getAssets().open(fileName);
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

        String line;
        while(reader.ready())
        {
            line = reader.readLine();
            db.execSQL(line);
        }
        reader.close();
    }
}

