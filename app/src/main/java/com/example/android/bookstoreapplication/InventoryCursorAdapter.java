package com.example.android.bookstoreapplication;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.android.bookstoreapplication.data.BooksContract.BooksEntry;

public class InventoryCursorAdapter extends CursorAdapter {

    //create constructor for Inventory cursor adapter being as there isn't a default one
    public InventoryCursorAdapter(Context context, Cursor cursor) {
        super(context, cursor, 0);
    }


    //Create new blank view
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {

        //inflate the list view
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    //bind (populate) data into the blank view created by newView()
    @Override
    public void bindView(View view, final Context context, Cursor cursor) {

        //find views needed to complete the listview
        TextView productNameTextView = view.findViewById(R.id.product_name_text_view);
        TextView priceTextView = view.findViewById(R.id.price_text_view);
        TextView quantityTextView = view.findViewById(R.id.quantity_text_view);
        Button saleButton = view.findViewById(R.id.sale_button);

        //get column index for items specified in the listview
        int idColumnIndex = cursor.getColumnIndex(BooksEntry._ID);
        int productNameIndex = cursor.getColumnIndex(BooksEntry.COLUMN_PRODUCT_NAME);
        int priceIndex = cursor.getColumnIndex(BooksEntry.COLUMN_PRICE);
        int quantityIndex = cursor.getColumnIndex(BooksEntry.COLUMN_QUANTITY);

        //get data for specified(current) list item(product)
        final int id = cursor.getInt(idColumnIndex);
        String productName = cursor.getString(productNameIndex);
        int price = cursor.getInt(priceIndex);
        final int quantity = cursor.getInt(quantityIndex);

        //convert int values to strings for text views
        String priceAsString = Integer.toString(price);
        String quantityAsString = Integer.toString(quantity);

        //Set string for display purposes
        String priceDisplay = "$" + priceAsString + ".00 (all values in usd)";
        String quantityDisplay = quantityAsString + " units available";

        //set the list item text views with information for current item (product)
        productNameTextView.setText(productName);
        priceTextView.setText(priceDisplay);
        quantityTextView.setText(quantityDisplay);


        //Click listener has to be populated in the adapter. Causes failure if placed in the activity because it may not exist yet
        //call sales decrease in Main activity when the sales button is pressed. Passing product id and quantity
        saleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity Sale = (MainActivity) context;
                int currentQuantity = quantity;
                int currentProductId = id;
                Sale.salesDecrease(currentQuantity, currentProductId);
            }
        });
    }
}
