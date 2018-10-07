package example.android.com.inventory;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;


/**
 * InventoryDBHelper - Responsible for setting up tables required for this app
 */
public class InventoryDBHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "BookStore.db";

    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + InventoryContract.Inventory.TABLE_NAME + " (" +
                    InventoryContract.Inventory._ID + " INTEGER PRIMARY KEY," +
                    InventoryContract.Inventory.COLUMN_NAME_PRODUCT_NAME + " TEXT," +
                    InventoryContract.Inventory.COLUMN_NAME_PRICE + " REAL, " +
                    InventoryContract.Inventory.COLUMN_NAME_QUANTITY + " INTEGER, " +
                    InventoryContract.Inventory.COLUMN_NAME_SUPPLIER_NAME + " TEXT, " +
                    InventoryContract.Inventory.COLUMN_NAME_SUPPLIER_PHONE_NUMBER + " TEXT)"
            ;

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + InventoryContract.Inventory.TABLE_NAME;

    InventoryDBHelper(Context context) {
        this(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    private InventoryDBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }

}
