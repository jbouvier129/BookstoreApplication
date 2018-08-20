package com.example.android.bookstoreapplication;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.android.bookstoreapplication.data.BooksContract.BooksEntry;

public class InventoryEditor extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final int EXISTING_INVENTORY_LOADER = 0;

    private Uri mCurrentProductUri;

    private EditText mProductNameField;
    private EditText mPriceField;
    private EditText mQuantityField;
    private EditText mSupplierField;
    private EditText mSupplierPhoneNumberField;
    private boolean hasUpdated = false;

    //Method to determine if a field is likely to have been updated
    private View.OnTouchListener touchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            hasUpdated = true;
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory_editor);

        //grab intent used to launch the activity
        Intent intent = getIntent();
        mCurrentProductUri = intent.getData();

        //Update title pending on how activity is accessed
        if (mCurrentProductUri == null) {
            setTitle(getString(R.string.add_product));
            //disable delete because as the selection isn't for something that exists
            invalidateOptionsMenu();
        } else {
            setTitle(getString(R.string.edit_product));
            //populate screen with data for current product
            getLoaderManager().initLoader(EXISTING_INVENTORY_LOADER, null, this);
        }

        //find editable fields
        mProductNameField = findViewById(R.id.product_name_field);
        mPriceField = findViewById(R.id.price_field);
        mQuantityField = findViewById(R.id.quantity_field);
        mSupplierField = findViewById(R.id.supplier_name_field);
        mSupplierPhoneNumberField = findViewById(R.id.supplier_phone_number_field);

        //set touch listeners
        mProductNameField.setOnTouchListener(touchListener);
        mPriceField.setOnTouchListener(touchListener);
        mQuantityField.setOnTouchListener(touchListener);
        mSupplierField.setOnTouchListener(touchListener);
        mSupplierPhoneNumberField.setOnTouchListener(touchListener);
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

        if (mCurrentProductUri == null && TextUtils.isEmpty(productName) && TextUtils.isEmpty(priceAsString) &&
                TextUtils.isEmpty(quantityAsString) && TextUtils.isEmpty(supplierName) && TextUtils.isEmpty(supplierPhoneNumber)) {
            return;
        }

        //Create object key value pairs to pass into an insert statement
        ContentValues values = new ContentValues();
        values.put(BooksEntry.COLUMN_PRODUCT_NAME, productName);
        values.put(BooksEntry.COLUMN_PRICE, price);
        values.put(BooksEntry.COLUMN_QUANTITY, quantity);
        values.put(BooksEntry.COLUMN_SUPPLIER_NAME, supplierName);
        values.put(BooksEntry.COLUMN_SUPPLIER_PHONE_NUMBER, supplierPhoneNumber);

        //decide if this is an insert or update and respond accordingly

        if (mCurrentProductUri == null) {
            Uri newUri = getContentResolver().insert(BooksEntry.CONTENT_URI, values);
            if (newUri == null) {
                Toast.makeText(this, getString(R.string.insert_failed), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.insert_successful), Toast.LENGTH_SHORT).show();
            }
        } else {
            int rowsUpdated = getContentResolver().update(mCurrentProductUri, values, null, null);

            if (rowsUpdated == 0) {
                Toast.makeText(this, "Failed to update products", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Update Successful", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //inflate menu options
        getMenuInflater().inflate(R.menu.menu_inventory_editor, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        //hide delete menu item if the pet hasn't been created
        if (mCurrentProductUri == null) {
            MenuItem menuItem = menu.findItem(R.id.delete);
            menuItem.setVisible(false);
        }
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
                //make sure user wants to delete the item
                showDeleteConfirmationDialog();
                return true;
            case android.R.id.home:
                if (!hasUpdated) {
                    NavUtils.navigateUpFromSameTask(this);
                    return true;
                }
                //warn user of unsaved changes
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                NavUtils.navigateUpFromSameTask(InventoryEditor.this);
                            }
                        };
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (!hasUpdated) {
            super.onBackPressed();
            return;
        }
        //if data has changed warn user
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                };
        showUnsavedChangesDialog(discardButtonClickListener);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        //projection to be passed into the query
        String[] projection = {
                BooksEntry._ID,
                BooksEntry.COLUMN_PRODUCT_NAME,
                BooksEntry.COLUMN_PRICE,
                BooksEntry.COLUMN_QUANTITY,
                BooksEntry.COLUMN_SUPPLIER_NAME,
                BooksEntry.COLUMN_SUPPLIER_PHONE_NUMBER
        };
        //show results from the query
        return new CursorLoader(this, mCurrentProductUri, projection, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        //make sure there is at least one result returned
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }
        //find and read data from the row of the selected product
        if (cursor.moveToFirst()) {
            //get column indices
            int productColumnIndex = cursor.getColumnIndex(BooksEntry.COLUMN_PRODUCT_NAME);
            int priceIndex = cursor.getColumnIndex(BooksEntry.COLUMN_PRICE);
            int quantityIndex = cursor.getColumnIndex(BooksEntry.COLUMN_QUANTITY);
            int supplierIndex = cursor.getColumnIndex(BooksEntry.COLUMN_SUPPLIER_NAME);
            int supplierPhoneNumberIndex = cursor.getColumnIndex(BooksEntry.COLUMN_SUPPLIER_PHONE_NUMBER);

            //get values of the columns by index
            String product = cursor.getString(productColumnIndex);
            int price = cursor.getInt(priceIndex);
            int quantity = cursor.getInt(quantityIndex);
            String supplier = cursor.getString(supplierIndex);
            String supplierPhoneNumber = cursor.getString(supplierPhoneNumberIndex);

            //Convert ints to strings
            String priceAsString = Integer.toString(price);
            String quantityAsString = Integer.toString(quantity);

            //update text view with cursor values
            mProductNameField.setText(product);
            mPriceField.setText(priceAsString);
            mQuantityField.setText(quantityAsString);
            mSupplierField.setText(supplier);
            mSupplierPhoneNumberField.setText(supplierPhoneNumber);
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        //Clear fields if invalid
        mProductNameField.setText("");
        mPriceField.setText("");
        mQuantityField.setText("");
        mSupplierField.setText("");
        mSupplierPhoneNumberField.setText("");

    }

    private void showUnsavedChangesDialog(DialogInterface.OnClickListener discardButtonClickListener) {
        //Create dialog and listener for accept or cancel
        AlertDialog.Builder unsavedDialog = new AlertDialog.Builder(this);
        unsavedDialog.setMessage("You have unsaved changes. \nConfirm to continue \n Go back to save");
        unsavedDialog.setPositiveButton("Confirm", discardButtonClickListener);
        unsavedDialog.setNegativeButton("Go Back", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        AlertDialog alertDialog = unsavedDialog.create();
        alertDialog.show();
    }

    private void showDeleteConfirmationDialog() {
        //confirm user wants to delete the entry(ies)
        AlertDialog.Builder deleteDialog = new AlertDialog.Builder(this);
        deleteDialog.setMessage("Are you sure you want to delete this product(s)?");
        deleteDialog.setPositiveButton("Confirm Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteRecord();
            }
        });
        deleteDialog.setNegativeButton("Go Back", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        AlertDialog alertDialog = deleteDialog.create();
        alertDialog.show();

    }

    //function to delete
    private void deleteRecord() {
        if (mCurrentProductUri != null) {
            int rowDeleted = getContentResolver().delete(mCurrentProductUri, null, null);

            if (rowDeleted == 0) {
                Toast.makeText(this, "Failed to Delete Product", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Successfully Deleted Product", Toast.LENGTH_SHORT).show();
            }
        }
        finish();
    }
}
