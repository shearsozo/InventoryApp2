package example.android.com.inventory;

import android.provider.BaseColumns;

/**
 * InventoryContract - Wrapper class for tables in the inventory database
 */

public final class InventoryContract {

    /*
     * Inventory - BaseColumns class for inventory table
     */
    public static class Inventory implements BaseColumns {
        public static final String TABLE_NAME = "inventory";

        //columns in inventory table
        public static final String COLUMN_NAME_PRODUCT_NAME = "product_name";
        public static final String COLUMN_NAME_PRICE = "price";
        public static final String COLUMN_NAME_QUANTITY = "quantity";
        public static final String COLUMN_NAME_SUPPLIER_NAME = "supplier_name";
        public static final String COLUMN_NAME_SUPPLIER_PHONE_NUMBER = "phone_number";
    }

}
