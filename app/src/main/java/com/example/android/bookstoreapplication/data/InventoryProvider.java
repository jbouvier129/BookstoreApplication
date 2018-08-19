package com.example.android.bookstoreapplication.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import com.example.android.bookstoreapplication.data.BooksContract.BooksEntry;

public class InventoryProvider extends ContentProvider {

    public static final String LOG_TAG = InventoryProvider.class.getSimpleName();

    //Uri match variable for entire table
    private static final int INVENTORY = 1000;

    //Uri match variable for specific row in inventory table
    private static final int INVENTORY_ID = 1001;

    //Uri matcher object to determine query
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {

        //Uri for entire table query
        sUriMatcher.addURI(BooksContract.CONTENT_AUTHORITY, BooksContract.PATH_INVENTORY, INVENTORY);

        //Uri for specific row
        sUriMatcher.addURI(BooksContract.CONTENT_AUTHORITY, BooksContract.PATH_INVENTORY + "/#", INVENTORY_ID);
    }

    private BooksDbHelper mDbHelper;

    @Override
    public boolean onCreate() {
        mDbHelper = new BooksDbHelper(getContext());
        return true;
    }

    //URI Query using projection
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {

        //Get Readable database and set up cursor
        SQLiteDatabase database = mDbHelper.getReadableDatabase();
        Cursor cursor;

        //set integer variable to determine uri match
        int match = sUriMatcher.match(uri);

        //switch statement to determine if passed uri is acceptable and direct accordingly returning
        //query results via a cursor
        switch (match) {
            case INVENTORY:
                cursor = database.query(BooksEntry.TABLE_NAME, projection, selection,
                        selectionArgs, null, null, sortOrder);
                break;

            case INVENTORY_ID:
                selection = BooksEntry._ID + "=?";
                selectionArgs = new String[]{
                        String.valueOf(ContentUris.parseId(uri))
                };
                cursor = database.query(BooksEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;

            default:
                throw new IllegalArgumentException("Cannot query this URI: " + uri);
        }
        return cursor;
    }

    //Insert using content values
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case INVENTORY:
                return insertInventory(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insert not allowed from " + uri + " uri.");
        }
    }

    //Insert Inventory helper method
    private Uri insertInventory(Uri uri, ContentValues values) {

        //validate product name has a value
        String name = values.getAsString(BooksEntry.COLUMN_PRODUCT_NAME);
        if (name.isEmpty() || name == null) {
            throw new IllegalArgumentException("Product requires a name");
        }

        //validate price is not a negative number and is actually a number
        int price = values.getAsInteger(BooksEntry.COLUMN_PRICE);
        String priceString = values.getAsInteger(BooksEntry.COLUMN_PRICE).toString();
        if (price < 0) {
            throw new IllegalArgumentException("Price is required");
        }
        if (!priceString.matches("^[0-9]+$")) {
            throw new IllegalArgumentException("Price must be a number");
        }

        //validate quantity is not a negative number and is actually a number
        int quantity = values.getAsInteger(BooksEntry.COLUMN_QUANTITY);
        String quantityString = values.getAsInteger(BooksEntry.COLUMN_QUANTITY).toString();
        if (quantity < 0) {
            throw new IllegalArgumentException("Cannot enter negative quantity");
        }
        if (!quantityString.matches("^[0-9]+$")) {
            throw new IllegalArgumentException("Quantity must be a number");
        }

        //validate supplier name has a value
        String supplierName = values.getAsString(BooksEntry.COLUMN_SUPPLIER_NAME);
        if (supplierName.isEmpty() || supplierName == null) {
            throw new IllegalArgumentException("Supplier name is required.");
        }

        //validate supplier phone number has a value and is 10 or 11 characters in length then validates it is numeric
        String supplierPhoneNumber = values.getAsString(BooksEntry.COLUMN_SUPPLIER_PHONE_NUMBER);
        if (supplierPhoneNumber.isEmpty() || supplierPhoneNumber == null || supplierPhoneNumber.length() < 10
                || supplierPhoneNumber.length() > 11) {
            throw new IllegalArgumentException("Phone number is required");
        }
        if (!supplierPhoneNumber.matches("^[0-9]+$")) {
            throw new IllegalArgumentException("Phone number must be numeric");
        }


        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        long id = db.insert(BooksEntry.TABLE_NAME, null, values);

        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri + " uri");
            return null;
        }

        return ContentUris.withAppendedId(uri, id);
    }

    //Update using content values
    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case INVENTORY:
                return updateInventory(uri, contentValues, selection, selectionArgs);
            case INVENTORY_ID:
                selection = BooksEntry._ID + "=?";
                selectionArgs = new String[]{
                        String.valueOf(ContentUris.parseId(uri))
                };
                return updateInventory(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update not supported for " + uri + " uri.");
        }
    }

    public int updateInventory(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        if (values.containsKey(BooksEntry.COLUMN_PRODUCT_NAME)) {
            String name = values.getAsString(BooksEntry.COLUMN_PRODUCT_NAME);
            if (name.isEmpty() || name == null) {
                throw new IllegalArgumentException("Product requires a name");
            }
        }

        if (values.containsKey(BooksEntry.COLUMN_PRICE)) {
            int price = values.getAsInteger(BooksEntry.COLUMN_PRICE);
            String priceString = values.getAsInteger(BooksEntry.COLUMN_PRICE).toString();
            if (price < 0) {
                throw new IllegalArgumentException("Price is required");
            }
            if (!priceString.matches("^[0-9]+$")) {
                throw new IllegalArgumentException("Price must be a number");
            }
        }

        //validate quantity is not a negative number and is actually a number
        if (values.containsKey(BooksEntry.COLUMN_QUANTITY)) {
            int quantity = values.getAsInteger(BooksEntry.COLUMN_QUANTITY);
            String quantityString = values.getAsInteger(BooksEntry.COLUMN_QUANTITY).toString();
            if (quantity < 0) {
                throw new IllegalArgumentException("Cannot enter negative quantity");
            }
            if (!quantityString.matches("^[0-9]+$")) {
                throw new IllegalArgumentException("Quantity must be a number");
            }
        }

        //validate supplier name has a value
        if (values.containsKey(BooksEntry.COLUMN_PRICE)) {
            String supplierName = values.getAsString(BooksEntry.COLUMN_SUPPLIER_NAME);
            if (supplierName.isEmpty() || supplierName == null) {
                throw new IllegalArgumentException("Supplier name is required.");
            }
        }

        //validate supplier phone number has a value and is 10 or 11 characters in length then validates it is numeric
        if (values.containsKey(BooksEntry.COLUMN_SUPPLIER_PHONE_NUMBER)) {
            String supplierPhoneNumber = values.getAsString(BooksEntry.COLUMN_SUPPLIER_PHONE_NUMBER);
            if (supplierPhoneNumber.isEmpty() || supplierPhoneNumber == null || supplierPhoneNumber.length() < 10
                    || supplierPhoneNumber.length() > 11) {
                throw new IllegalArgumentException("Phone number is required");
            }
            if (!supplierPhoneNumber.matches("^[0-9]+$")) {
                throw new IllegalArgumentException("Phone number must be numeric");
            }

        }
        if (values.size() == 0) {
            return 0;
        }
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        return db.update(BooksEntry.TABLE_NAME, values, selection, selectionArgs);
    }

    //delete using parameters
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case INVENTORY:
                return db.delete(BooksEntry.TABLE_NAME, selection, selectionArgs);
            case INVENTORY_ID:
                selection = BooksEntry._ID + "=?";
                selectionArgs = new String[]{
                        String.valueOf(ContentUris.parseId(uri))
                };
                return db.delete(BooksEntry.TABLE_NAME, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Deletion is not support for " + uri + " uri");
        }
    }

    //return data type gtom uti
    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case INVENTORY:
                return BooksEntry.CONTENT_LIST_TYPE;
            case INVENTORY_ID:
                return BooksEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalArgumentException("Unknown uri " + uri + " with match " + match);
        }
    }
}