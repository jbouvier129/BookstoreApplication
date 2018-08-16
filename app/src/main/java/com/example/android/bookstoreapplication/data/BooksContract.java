package com.example.android.bookstoreapplication.data;

import android.provider.BaseColumns;

public final class BooksContract {

    private BooksContract() {}

    //Create constants used for database calls and table column names
    public static final class BooksEntry implements BaseColumns{

        //Product Name, Price, Quantity, Supplier Name, Supplier Phone Number
        public final static String TABLE_NAME = "books";
        public final static String COLUMN_PRODUCT_NAME = "Product";
        public final static String COLUMN_PRICE = "Price";
        public final static String COLUMN_QUANTITY = "Quantity";
        public final static String COLUMN_SUPPLIER_NAME = "Supplier";
        public final static String COLUMN_SUPPLIER_PHONE_NUMBER = "Supplier_Phone_Number";
    }
}
