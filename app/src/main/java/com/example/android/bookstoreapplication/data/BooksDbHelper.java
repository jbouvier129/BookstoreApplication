package com.example.android.bookstoreapplication.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.android.bookstoreapplication.data.BooksContract.BooksEntry;

public class BooksDbHelper extends SQLiteOpenHelper {

    //db name constant
    private static final String DATABASE_NAME = "inventory.db";

    //db version
    private static final int DATABASE_VERSION = 1;

    //Constructor to create new class instance

    public BooksDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //String creation to be used to Create the table
        String SQLCreateTable = "CREATE TABLE " + BooksEntry.TABLE_NAME + " ("
                + BooksEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + BooksEntry.COLUMN_PRODUCT_NAME + " TEXT NOT NULL, "
                + BooksEntry.COLUMN_PRICE + " INTEGER NOT NULL DEFAULT 0, "
                + BooksEntry.COLUMN_QUANTITY + " INTEGER NOT NULL DEFAULT 0, "
                + BooksEntry.COLUMN_SUPPLIER_NAME + " TEXT NOT NULL, "
                + BooksEntry.COLUMN_SUPPLIER_PHONE_NUMBER + " TEXT NOT NULL);";

        //Execute CREATE Statement
        db.execSQL(SQLCreateTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //DO NOTHING, NOT REQUIRED YET
    }
}
