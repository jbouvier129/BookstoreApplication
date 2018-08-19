package com.example.android.bookstoreapplication;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.example.android.bookstoreapplication.data.BooksContract.BooksEntry;
import com.example.android.bookstoreapplication.data.BooksDbHelper;

public class InventoryEditor extends AppCompatActivity {

    private EditText mProductNameField;
    private EditText mPriceField;
    private EditText mQuantityField;
    private EditText mSupplierField;
    private EditText mSupplierPhoneNumberField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory_editor);

        //find editable fields
        mProductNameField = findViewById(R.id.product_name_field);
        mPriceField = findViewById(R.id.price_field);
        mQuantityField = findViewById(R.id.quantity_field);
        mSupplierField = findViewById(R.id.supplier_name_field);
        mSupplierPhoneNumberField = findViewById(R.id.supplier_phone_number_field);
    }

    private void insertProduct() {

        //get user input
        String productName = mProductNameField.getText().toString().trim();
        String priceAsString = mPriceField.getText().toString().trim();
        String quantityAsString = mQuantityField.getText().toString().trim();
        String supplierName = mSupplierField.getText().toString().trim();
        String supplierPhoneNumber = mSupplierPhoneNumberField.getText().toString().trim();

        //convert number columns from strings
        int price = Integer.parseInt(priceAsString);
        int quantity = Integer.parseInt(quantityAsString);

        //Create db helper
        BooksDbHelper mDbHelper = new BooksDbHelper(this);

        //Open db in write mode
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        //Create object key value pairs to pass into an insert statement
        ContentValues values = new ContentValues();
        values.put(BooksEntry.COLUMN_PRODUCT_NAME, productName);
        values.put(BooksEntry.COLUMN_PRICE, price);
        values.put(BooksEntry.COLUMN_QUANTITY, quantity);
        values.put(BooksEntry.COLUMN_SUPPLIER_NAME, supplierName);
        values.put(BooksEntry.COLUMN_SUPPLIER_PHONE_NUMBER, supplierPhoneNumber);

        //insert and get row _id to display on successful insert
        Uri newUri = getContentResolver().insert(BooksEntry.CONTENT_URI, values);
        if (newUri == null) {
            Toast.makeText(this, getString(R.string.insert_failed), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, getString(R.string.insert_successful), Toast.LENGTH_SHORT).show();
        }
    }

    private void deleteProduct() {
        //TODO: Get _id of selected item

        //TODO: String to delete * from inventory where _id = selected item

        //TODO: db.sqlexec(deleteProduct)

        Toast.makeText(this, "Currently working on this. \n Not a requirement so incomplete at this time", Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_inventory_editor, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Overflow menu selection
        switch (item.getItemId()) {
            case R.id.save:
                insertProduct();
                finish();
                return true;
            case R.id.delete:
                deleteProduct();
                finish();
                return true;
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
