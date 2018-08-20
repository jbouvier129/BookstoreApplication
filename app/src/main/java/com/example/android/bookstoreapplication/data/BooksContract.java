package com.example.android.bookstoreapplication.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public final class BooksContract {

    //Content authority constant
    public final static String CONTENT_AUTHORITY = "com.example.android.bookstoreapplication";
    //Content provider base URI
    public final static Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    //path variable for determining the table to access
    public final static String PATH_INVENTORY = "inventory";

    private BooksContract() {
    }

    //Create constants used for database calls and table column names
    public static final class BooksEntry implements BaseColumns {

        //Full Uri with path to access data via provider
        public final static Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_INVENTORY);

        //MIME types
        public static final String CONTENT_LIST_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" +
                CONTENT_AUTHORITY + "/" + PATH_INVENTORY;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" +
                CONTENT_AUTHORITY + "/" + PATH_INVENTORY;

        //Table Name, Product Name Column, Price Column, Quantity Column, Supplier Name Column and
        // Supplier Phone Number column constants
        public final static String TABLE_NAME = "books";
        public final static String _ID = BaseColumns._ID;
        public final static String COLUMN_PRODUCT_NAME = "Product";
        public final static String COLUMN_PRICE = "Price";
        public final static String COLUMN_QUANTITY = "Quantity";
        public final static String COLUMN_SUPPLIER_NAME = "Supplier";
        public final static String COLUMN_SUPPLIER_PHONE_NUMBER = "Supplier_Phone_Number";


    }
}
