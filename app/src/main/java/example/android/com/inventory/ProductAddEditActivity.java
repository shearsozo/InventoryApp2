package example.android.com.inventory;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


/**
 * This activity is used for product add page and product edit pages
 * The user given data is validated and updated to the database
 */
public class ProductAddEditActivity extends AppCompatActivity {

    private ProductDetails product;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_edit_product_form);

        final TextView status_message = (TextView) findViewById(R.id.status_message);
        status_message.setVisibility(View.INVISIBLE);

        Button save = (Button) findViewById(R.id.save_new);
        final ContentProvider dbops = ContentProvider.getInstance(this);

        // Input controls to get user data
        final EditText editText = (EditText) findViewById(R.id.new_name);
        final EditText priceText = (EditText) findViewById(R.id.new_price);
        final EditText quantityText = (EditText) findViewById(R.id.new_quantity);
        final EditText supplierText = (EditText) findViewById(R.id.new_supplier);
        final EditText supplierPhone = (EditText) findViewById(R.id.new_phone);

        if (product == null) {
            product = getIntent().getParcelableExtra("edit_this_product");
            if (product != null) {
                // Product edit page. Prepopulating input controls with the data of the product
                getIntent().removeExtra("edit_this_product");
                editText.setText(product.getProduct_name());
                priceText.setText(product.getPrice().toString());
                quantityText.setText(product.getQuantity().toString());
                supplierText.setText(product.getSupplier_name());
                supplierPhone.setText(product.getPhone_number());
            }
        }

        View.OnClickListener addNewItem = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    status_message.setVisibility(View.INVISIBLE);

                    // Read the input fields and validate them
                    // If values are invalid show toast with error message

                    String newName = editText.getText().toString().trim();
                    if (showToastIfEmpty(newName.isEmpty(), R.string.product_name_empty)) return;

                    String priceString = priceText.getText().toString().trim();
                    if (showToastIfEmpty(newName.isEmpty(), R.string.price_invalid)) return;

                    Double price = Double.parseDouble(priceString);
                    if (showToastIfEmpty(price <= 0, R.string.price_invalid)) return;

                    String quantityString = quantityText.getText().toString();
                    if (showToastIfEmpty(newName.isEmpty(), R.string.quantity_invalid)) return;

                    Integer quantity = Integer.parseInt(quantityString, 10);
                    if (showToastIfEmpty(quantity < 0, R.string.quantity_invalid)) return;

                    String newSupplierName = supplierText.getText().toString().trim();
                    if (showToastIfEmpty(newSupplierName.isEmpty(), R.string.supplier_name_empty)) return;

                    String supplierPhoneNumber = supplierPhone.getText().toString().trim();
                    if (showToastIfEmpty(supplierPhoneNumber.isEmpty(), R.string.supplier_number_empty)) return;


                    if (ProductAddEditActivity.this.product != null) {
                        //Update existing product for edit product action
                        product.setQuantity(quantity);
                        product.setPhone_number(supplierPhoneNumber);
                        product.setProduct_name(newName);
                        product.setPrice(price);
                        product.setSupplier_name(newSupplierName);
                        product.setPhone_number(supplierPhoneNumber);
                        long rowsupdated = dbops.updateProductDetails(product);
                        if (rowsupdated == 0) {
                            status_message.setTextColor(Color.RED);
                            status_message.setText(R.string.updateFail);
                        } else {
                            status_message.setTextColor(Color.BLUE);
                            status_message.setText(R.string.updateSuccess);
                        }
                    } else {
                        //Insert a new row into the database for add product action
                        long rowId = dbops.insertData(new ProductDetails(-1, newName, price, quantity, newSupplierName, supplierPhoneNumber));
                        if (rowId == -1) {
                            status_message.setTextColor(Color.RED);
                            status_message.setText(R.string.additionFail);
                        } else {
                            status_message.setTextColor(Color.BLUE);
                            status_message.setText(R.string.additionSuccess);
                            editText.getText().clear();
                            priceText.getText().clear();
                            quantityText.getText().clear();
                            supplierText.getText().clear();
                            supplierPhone.getText().clear();
                        }
                    }
                    status_message.setVisibility(View.VISIBLE);
                } catch (Exception e) {
                    e.printStackTrace();
                    status_message.setTextColor(Color.RED);
                    status_message.setText(e.getMessage());
                }
            }
        };
        save.setOnClickListener(addNewItem);
    }

    /**
     * Shows the error message in view when validation fails
     * @param isInvalid - true if the validation failed
     * @param errorMessage - error message to be shown in the toast
     * @return
     */
    private boolean showToastIfEmpty(boolean isInvalid, int errorMessage) {
        if (isInvalid) {
            Toast toast = Toast.makeText(ProductAddEditActivity.this, errorMessage, Toast.LENGTH_LONG);
            toast.show();
            return true;
        }
        return false;
    }

}
