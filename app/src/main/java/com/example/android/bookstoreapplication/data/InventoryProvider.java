package com.example.android.bookstoreapplication.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import com.example.android.bookstoreapplication.R;
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
                CharSequence text = "Cannot query this URI: " + uri;
                Context context = getContext();
                int duration = Toast.LENGTH_SHORT;
                Toast toast = Toast.makeText(context, text, duration);
                toast.show();
                return null;
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

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

                CharSequence text = "Insert not allowed from " + uri + " uri.";
                Context context = getContext();
                int duration = Toast.LENGTH_SHORT;
                Toast toast = Toast.makeText(context, text, duration);
                toast.show();
                return null;
        }
    }

    //Insert Inventory helper method
    private Uri insertInventory(Uri uri, ContentValues values) {

        //validate product name has a value
        String name = values.getAsString(BooksEntry.COLUMN_PRODUCT_NAME);
        if (name.isEmpty() || name == null) {
            CharSequence text = Integer.toString(R.string.name_required);
            Context context = getContext();
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
            return null;
        }

        //validate price is not a negative number and is actually a number
        int price = values.getAsInteger(BooksEntry.COLUMN_PRICE);
        String priceString = values.getAsInteger(BooksEntry.COLUMN_PRICE).toString();
        if (price < 0) {
            CharSequence text = Integer.toString(R.string.price_required);
            Context context = getContext();
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
            return null;
        }
        if (!priceString.matches("^[0-9]+$")) {
            CharSequence text = Integer.toString(R.string.number_required);
            Context context = getContext();
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
            return null;
        }

        //validate quantity is not a negative number and is actually a number
        int quantity = values.getAsInteger(BooksEntry.COLUMN_QUANTITY);
        String quantityString = values.getAsInteger(BooksEntry.COLUMN_QUANTITY).toString();
        if (quantity < 0) {
            CharSequence text = Integer.toString(R.string.quantity_required);
            Context context = getContext();
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
            return null;
        }
        if (!quantityString.matches("^[0-9]+$")) {
            CharSequence text = Integer.toString(R.string.number_required);
            Context context = getContext();
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
            return null;
        }

        //validate supplier name has a value
        String supplierName = values.getAsString(BooksEntry.COLUMN_SUPPLIER_NAME);
        if (supplierName.isEmpty() || supplierName == null) {
            CharSequence text = Integer.toString(R.string.supplier_required);
            Context context = getContext();
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
            return null;
        }

        //validate supplier phone number has a value and is 10 or 11 characters in length then validates it is numeric
        String supplierPhoneNumber = values.getAsString(BooksEntry.COLUMN_SUPPLIER_PHONE_NUMBER);
        if (supplierPhoneNumber.isEmpty() || supplierPhoneNumber == null || supplierPhoneNumber.length() < 10
                || supplierPhoneNumber.length() > 11) {
            CharSequence text = Integer.toString(R.string.supplier_phone_required);
            Context context = getContext();
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
            return null;
        }
        if (!supplierPhoneNumber.matches("^[0-9]+$")) {
            CharSequence text = Integer.toString(R.string.number_required);
            Context context = getContext();
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
            return null;
        }


        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        long id = db.insert(BooksEntry.TABLE_NAME, null, values);

        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri + " uri");
            return null;
        }

        getContext().getContentResolver().notifyChange(uri, null);

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
                CharSequence text = "Update not supported for " + uri + " uri.";
                Context context = getContext();
                int duration = Toast.LENGTH_SHORT;
                Toast toast = Toast.makeText(context, text, duration);
                toast.show();
                return -1;
        }
    }

    public int updateInventory(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        if (values.containsKey(BooksEntry.COLUMN_PRODUCT_NAME)) {
            String name = values.getAsString(BooksEntry.COLUMN_PRODUCT_NAME);
            if (name.isEmpty() || name == null) {

                CharSequence text = Integer.toString(R.string.name_required);
                Context context = getContext();
                int duration = Toast.LENGTH_SHORT;
                Toast toast = Toast.makeText(context, text, duration);
                toast.show();
                return 0;
            }
        }

        if (values.containsKey(BooksEntry.COLUMN_PRICE)) {
            int price = values.getAsInteger(BooksEntry.COLUMN_PRICE);
            String priceString = values.getAsInteger(BooksEntry.COLUMN_PRICE).toString();
            if (price < 0) {

                CharSequence text = Integer.toString(R.string.price_required);
                Context context = getContext();
                int duration = Toast.LENGTH_SHORT;
                Toast toast = Toast.makeText(context, text, duration);
                toast.show();
                return 0;
            }
            if (!priceString.matches("^[0-9]+$")) {
                CharSequence text = Integer.toString(R.string.number_required);
                Context context = getContext();
                int duration = Toast.LENGTH_SHORT;
                Toast toast = Toast.makeText(context, text, duration);
                toast.show();
                return 0;
            }
        }

        //validate quantity is not a negative number and is actually a number
        if (values.containsKey(BooksEntry.COLUMN_QUANTITY)) {
            int quantity = values.getAsInteger(BooksEntry.COLUMN_QUANTITY);
            String quantityString = values.getAsInteger(BooksEntry.COLUMN_QUANTITY).toString();
            if (quantity < 0) {
                CharSequence text = Integer.toString(R.string.quantity_required);
                Context context = getContext();
                int duration = Toast.LENGTH_SHORT;
                Toast toast = Toast.makeText(context, text, duration);
                toast.show();
                return 0;
            }
            if (!quantityString.matches("^[0-9]+$")) {
                CharSequence text = Integer.toString(R.string.number_required);
                Context context = getContext();
                int duration = Toast.LENGTH_SHORT;
                Toast toast = Toast.makeText(context, text, duration);
                toast.show();
                return 0;
            }
        }

        //validate supplier name has a value
        if (values.containsKey(BooksEntry.COLUMN_SUPPLIER_NAME)) {
            String supplierName = values.getAsString(BooksEntry.COLUMN_SUPPLIER_NAME);
            if (supplierName.isEmpty() || supplierName == null) {
                CharSequence text = Integer.toString(R.string.supplier_required);
                Context context = getContext();
                int duration = Toast.LENGTH_SHORT;
                Toast toast = Toast.makeText(context, text, duration);
                toast.show();
                return 0;
            }
        }

        //validate supplier phone number has a value and is 10 or 11 characters in length then validates it is numeric
        if (values.containsKey(BooksEntry.COLUMN_SUPPLIER_PHONE_NUMBER)) {
            String supplierPhoneNumber = values.getAsString(BooksEntry.COLUMN_SUPPLIER_PHONE_NUMBER);
            if (supplierPhoneNumber.isEmpty() || supplierPhoneNumber == null || supplierPhoneNumber.length() < 10
                    || supplierPhoneNumber.length() > 11) {
                CharSequence text = Integer.toString(R.string.supplier_phone_required);
                Context context = getContext();
                int duration = Toast.LENGTH_SHORT;
                Toast toast = Toast.makeText(context, text, duration);
                toast.show();
                return 0;
            }
            if (!supplierPhoneNumber.matches("^[0-9]+$")) {
                CharSequence text = Integer.toString(R.string.number_required);
                Context context = getContext();
                int duration = Toast.LENGTH_SHORT;
                Toast toast = Toast.makeText(context, text, duration);
                toast.show();
                return 0;
            }

        }
        if (values.size() == 0) {
            return 0;
        }
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        int rowUpdated = db.update(BooksEntry.TABLE_NAME, values, selection, selectionArgs);

        if (rowUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowUpdated;
    }

    //delete using parameters
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        int rowDeleted;

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case INVENTORY:
                rowDeleted = db.delete(BooksEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case INVENTORY_ID:
                selection = BooksEntry._ID + "=?";
                selectionArgs = new String[]{
                        String.valueOf(ContentUris.parseId(uri))
                };
                rowDeleted = db.delete(BooksEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                CharSequence text = "Deletion is not supported for " + uri + " uri";
                Context context = getContext();
                int duration = Toast.LENGTH_SHORT;
                Toast toast = Toast.makeText(context, text, duration);
                toast.show();
                return 0;
        }
        if (rowDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowDeleted;
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
                CharSequence text = "Unknown uri " + uri + " with match " + match;
                Context context = getContext();
                int duration = Toast.LENGTH_SHORT;
                Toast toast = Toast.makeText(context, text, duration);
                toast.show();
                return null;
        }
    }
}