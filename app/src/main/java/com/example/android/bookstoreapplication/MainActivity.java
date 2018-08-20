package com.example.android.bookstoreapplication;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.android.bookstoreapplication.data.BooksContract.BooksEntry;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int INVENTORY_LOADER = 0;

    InventoryCursorAdapter mInventoryCursorAdapter;

    //TODO: Wire up click listener for sales button to update quantity
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

        //find and set empty view for when there is no data
        ListView inventoryListView = findViewById(R.id.list);
        View emptyView = findViewById(R.id.empty_view);
        inventoryListView.setEmptyView(emptyView);

        //set up the adapter
        mInventoryCursorAdapter = new InventoryCursorAdapter(this, null);
        inventoryListView.setAdapter(mInventoryCursorAdapter);

        //set click listener
        inventoryListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                //intent to go to inventory editor
                Intent intent = new Intent(MainActivity.this, InventoryEditor.class);

                //get the uri for the selected uri
                Uri currentProductUri = ContentUris.withAppendedId(BooksEntry.CONTENT_URI, id);

                //pass uri for current product (captured above) so that the particular product information is displayed
                intent.setData(currentProductUri);

                //launch Inventory Editor passing the uri for the current product
                startActivity(intent);
            }
        });

        //start loader
        getLoaderManager().initLoader(INVENTORY_LOADER, null, this);
    }

    private void deleteEntries() {
        int rowDeleted = getContentResolver().delete(BooksEntry.CONTENT_URI, null, null);
        if (rowDeleted == 0) {
            Toast.makeText(this, "Failed to Delete Products", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Successfully Deleted Products", Toast.LENGTH_SHORT).show();
        }
        Log.v("MainActivity", rowDeleted + " rows deleted from database");
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
            showDeleteConfirmationDialog();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {
                BooksEntry._ID,
                BooksEntry.COLUMN_PRODUCT_NAME,
                BooksEntry.COLUMN_PRICE,
                BooksEntry.COLUMN_QUANTITY
        };

        return new CursorLoader(this, BooksEntry.CONTENT_URI, projection, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mInventoryCursorAdapter.swapCursor(data);

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mInventoryCursorAdapter.swapCursor(null);

    }

    private void showDeleteConfirmationDialog() {
        //confirm user wants to delete the entry(ies)
        AlertDialog.Builder deleteDialog = new AlertDialog.Builder(this);
        deleteDialog.setMessage("Are you sure you want to delete this product(s)?");
        deleteDialog.setPositiveButton("Confirm Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteEntries();
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
}
