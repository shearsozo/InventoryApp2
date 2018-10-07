package example.android.com.inventory;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

import java.util.ArrayList;
import java.util.List;

import static example.android.com.inventory.InventoryContract.Inventory.COLUMN_NAME_PRICE;
import static example.android.com.inventory.InventoryContract.Inventory.COLUMN_NAME_PRODUCT_NAME;
import static example.android.com.inventory.InventoryContract.Inventory.COLUMN_NAME_QUANTITY;
import static example.android.com.inventory.InventoryContract.Inventory.COLUMN_NAME_SUPPLIER_NAME;
import static example.android.com.inventory.InventoryContract.Inventory.COLUMN_NAME_SUPPLIER_PHONE_NUMBER;
import static example.android.com.inventory.InventoryContract.Inventory.TABLE_NAME;


// All the db operations are performed through this class
public class ContentProvider {

    private final InventoryDBHelper inventoryDBHelper;

    private static ContentProvider instance = null;

    private ContentProvider(Context context) {
        inventoryDBHelper = new InventoryDBHelper(context);
    }

    // Maintains a single copy of the contentprovider instance for the application
    public static ContentProvider getInstance(Context context) {
        if(instance == null) {
            instance = new ContentProvider(context);
        }
        return instance;
    }

    // Reads the latest details of the product from database and updates the object
    public void reReadProductDetails(ProductDetails product) {
        SQLiteDatabase db = inventoryDBHelper.getReadableDatabase();

        //Read all the fields
        String[] projection = {
                BaseColumns._ID,
                COLUMN_NAME_PRODUCT_NAME,
                COLUMN_NAME_PRICE,
                COLUMN_NAME_QUANTITY,
                COLUMN_NAME_SUPPLIER_NAME,
                COLUMN_NAME_SUPPLIER_PHONE_NUMBER
        };

        //Query based on the _ID field
        String selection = BaseColumns._ID + " = ?";
        String[] selectionArgs = { product.getId().toString() };

        Cursor cursor = db.query(
                TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        //Read the retrieved data and update the product object
        while(cursor.moveToNext()) {
            String productName = cursor.getString(cursor.getColumnIndexOrThrow(InventoryContract.Inventory.COLUMN_NAME_PRODUCT_NAME));
            String supplierName = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME_SUPPLIER_NAME));
            String supplierNumber = cursor.getString(cursor.getColumnIndexOrThrow(InventoryContract.Inventory.COLUMN_NAME_SUPPLIER_PHONE_NUMBER));
            Double price = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_NAME_PRICE));
            Integer quantity = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_NAME_QUANTITY));
            Integer id = cursor.getInt(cursor.getColumnIndexOrThrow(BaseColumns._ID));
            assert (product.getId().equals(id));
            product.setQuantity(quantity);
            product.setPhone_number(supplierNumber);
            product.setProduct_name(productName);
            product.setPrice(price);
            product.setSupplier_name(supplierName);
            product.setPhone_number(supplierNumber);
        }
        cursor.close();
    }

    // Inserts a row of data into inventory table
    public long insertData(ProductDetails productDetails) {
        SQLiteDatabase db = inventoryDBHelper.getWritableDatabase();
        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(InventoryContract.Inventory.COLUMN_NAME_PRODUCT_NAME, productDetails.getProduct_name());
        values.put(COLUMN_NAME_PRICE, productDetails.getPrice());
        values.put(COLUMN_NAME_QUANTITY, productDetails.getQuantity());
        values.put(COLUMN_NAME_SUPPLIER_NAME, productDetails.getSupplier_name());
        values.put(COLUMN_NAME_SUPPLIER_PHONE_NUMBER, productDetails.getPhone_number());
        return db.insertOrThrow(TABLE_NAME, null, values);
    }

    public List<ProductDetails> getAllProducts() {
        SQLiteDatabase db = inventoryDBHelper.getReadableDatabase();

        //Read all the fields
        String[] projection = {
                BaseColumns._ID,
                COLUMN_NAME_PRODUCT_NAME,
                COLUMN_NAME_PRICE,
                COLUMN_NAME_QUANTITY,
                COLUMN_NAME_SUPPLIER_NAME,
                COLUMN_NAME_SUPPLIER_PHONE_NUMBER
        };

        //Read the products in descending order of creation of the rows
        Cursor cursor = db.query(
                TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                BaseColumns._ID + " DESC"
        );

        List<ProductDetails> details = new ArrayList<>();
        while(cursor.moveToNext()) {
            String productName = cursor.getString(cursor.getColumnIndexOrThrow(InventoryContract.Inventory.COLUMN_NAME_PRODUCT_NAME));
            String supplierName = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME_SUPPLIER_NAME));
            String supplierNumber = cursor.getString(cursor.getColumnIndexOrThrow(InventoryContract.Inventory.COLUMN_NAME_SUPPLIER_PHONE_NUMBER));
            Double price = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_NAME_PRICE));
            Integer quantity = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_NAME_QUANTITY));
            Integer id = cursor.getInt(cursor.getColumnIndexOrThrow(BaseColumns._ID));
            details.add(new ProductDetails(id, productName, price, quantity, supplierName, supplierNumber));
        }
        cursor.close();

        return details;
    }

    /**
     * This function will reduce the quantity of the given product by 1
     * @param product - the product for which quantity should be reduced
     * @return - the number of rows updated in the table
     */
    public int reduceQuantity(ProductDetails product) {
        SQLiteDatabase db = inventoryDBHelper.getWritableDatabase();
        String strFilter = BaseColumns._ID + "=" + product.getId();
        ContentValues args = new ContentValues();
        args.put(COLUMN_NAME_QUANTITY, product.getQuantity() - 1);
        int updatedRows = db.update(TABLE_NAME, args, strFilter, null);
        reReadProductDetails(product);
        return updatedRows;
    }

    /**
     * This function will increase the quantity of the given product by 1
     * @param product - the product for which quantity should be increased
     * @return - the number of rows updated in the table
     */
    public int increaseQuantity(ProductDetails product) {
        SQLiteDatabase db = inventoryDBHelper.getWritableDatabase();
        String strFilter = "_id=" + product.getId();
        ContentValues args = new ContentValues();
        args.put(COLUMN_NAME_QUANTITY, product.getQuantity() + 1);
        int updatedRows = db.update(TABLE_NAME, args, strFilter, null);
        reReadProductDetails(product);
        return updatedRows;
    }

    /**
     * This function will update the fields of the product in the table
     * @param product - the product details are read from this parameter
     * @return - the number of rows updated in the table
     */
    public int updateProductDetails(ProductDetails product) {
        SQLiteDatabase db = inventoryDBHelper.getWritableDatabase();
        String strFilter = "_id=" + product.getId();
        ContentValues args = new ContentValues();
        args.put(COLUMN_NAME_PRODUCT_NAME, product.getProduct_name());
        args.put(COLUMN_NAME_PRICE, product.getPrice());
        args.put(COLUMN_NAME_QUANTITY, product.getQuantity());
        args.put(COLUMN_NAME_SUPPLIER_NAME, product.getSupplier_name());
        args.put(COLUMN_NAME_SUPPLIER_PHONE_NUMBER, product.getPhone_number());
        int updatedRows = db.update(TABLE_NAME, args, strFilter, null);
        reReadProductDetails(product);
        return updatedRows;
    }

    /**
     * Deletes the product row from the table
     * @param product - the id of the product that needs to be deleted is read from this object
     * @return
     */
    public int deleteProductFromInventory(ProductDetails product) {
        SQLiteDatabase db = inventoryDBHelper.getWritableDatabase();
        String strFilter = BaseColumns._ID + "=" +product.getId();
        return db.delete(TABLE_NAME, strFilter, null);
    }
}
