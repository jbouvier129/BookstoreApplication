package com.example.android.bookstoreapplication;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.bookstoreapplication.data.BooksContract.BooksEntry;
import com.example.android.bookstoreapplication.data.BooksDbHelper;

public class MainActivity extends AppCompatActivity {

    //variable to access inventory db
    private BooksDbHelper mDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //setup FAB to launch inventory editor
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent launchEditorActivity = new Intent(MainActivity.this, InventoryEditor.class);
                startActivity(launchEditorActivity);
            }
        });

        mDbHelper = new BooksDbHelper(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        showInventory();
    }

    //Helper method to populate TextView with inventory data
    private void showInventory() {

        //projection parameters
        String[] projection = {
                BooksEntry._ID,
                BooksEntry.COLUMN_PRODUCT_NAME,
                BooksEntry.COLUMN_PRICE,
                BooksEntry.COLUMN_QUANTITY,
                BooksEntry.COLUMN_SUPPLIER_NAME,
                BooksEntry.COLUMN_SUPPLIER_PHONE_NUMBER
        };

        Cursor cursor = getContentResolver().query(BooksEntry.CONTENT_URI, projection, null, null, null);

        //Create view to display cursor results
        TextView cursorView = findViewById(R.id.store_inventory);

        try {
            cursorView.setText("Table is active and has " + cursor.getCount() + " items currently. \n\n");
            cursorView.append(BooksEntry._ID + " - " +
                    BooksEntry.COLUMN_PRODUCT_NAME + " - " +
                    BooksEntry.COLUMN_PRICE + " - " +
                    BooksEntry.COLUMN_QUANTITY + " - " +
                    BooksEntry.COLUMN_SUPPLIER_NAME + " - " +
                    BooksEntry.COLUMN_SUPPLIER_PHONE_NUMBER + "\n"

            );
            //figure out column indexes then iterate through them so that every result from the query (cursor) is displayed
            int idIndex = cursor.getColumnIndex(BooksEntry._ID);
            int productIndex = cursor.getColumnIndex(BooksEntry.COLUMN_PRODUCT_NAME);
            int priceIndex = cursor.getColumnIndex(BooksEntry.COLUMN_PRICE);
            int quantityIndex = cursor.getColumnIndex(BooksEntry.COLUMN_QUANTITY);
            int supplierIndex = cursor.getColumnIndex(BooksEntry.COLUMN_SUPPLIER_NAME);
            int supplierPhoneNumberIndex = cursor.getColumnIndex(BooksEntry.COLUMN_SUPPLIER_PHONE_NUMBER);

            while (cursor.moveToNext()) {
                int currentId = cursor.getInt(idIndex);
                String currentProduct = cursor.getString(productIndex);
                int currentPrice = cursor.getInt(priceIndex);
                int currentQuantity = cursor.getInt(quantityIndex);
                String currentSupplier = cursor.getString(supplierIndex);
                String currentSupplierPhoneNumber = cursor.getString(supplierPhoneNumberIndex);

                cursorView.append("\n" + currentId + " - " + currentProduct + " - " + currentPrice + " - " +
                        currentQuantity + " - " + currentSupplier + " - " + currentSupplierPhoneNumber);
            }
        } finally {
            cursor.close();
        }
    }

    private void deleteEntries() {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        String deleteEntries = "DELETE FROM " + BooksEntry.TABLE_NAME;
        db.execSQL(deleteEntries);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_catalog.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_main_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //if user selects delete all from overflow do the below
        if (item.getItemId() == R.id.delete_all_entries) {
            // delete all items logic. THIS IS A TRIAL AND NOT REQUIRED
            deleteEntries();
            Toast.makeText(this, "This is a trial at deletion, not required!", Toast.LENGTH_SHORT).show();
            showInventory();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
