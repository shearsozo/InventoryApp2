package example.android.com.inventory;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

// This activity powers the Product details page
public class ProductDetailsActivity extends AppCompatActivity {

    private ProductDetails product;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.product_details);
        if (product == null) {
            product = getIntent().getParcelableExtra("this_product");
            getIntent().removeExtra("this_product");
        }
    }

    //Update the details when the activity is resumed after product edit
    @Override
    protected void onResume() {
        super.onResume();
        ContentProvider.getInstance(this).reReadProductDetails(product);
        setupDetailsPage();
    }

    // Populates the details page view and adds event listeners to action buttons
    private void setupDetailsPage() {
        final ViewHolder holder = new ViewHolder();
        holder.status_message.setVisibility(View.INVISIBLE);

        holder.product_name.setText(product.getProduct_name());
        holder.supplier_name.setText(product.getSupplier_name());
        holder.supplier_number.setText(product.getPhone_number());
        final String price = getResources().getString(R.string.product_price_string, product.getPrice());
        holder.price.setText(price);
        final String quantityLeft = getResources().getString(R.string.quantity_left, product.getQuantity());
        holder.quantity.setText(quantityLeft);

        final ContentProvider dbops = ContentProvider.getInstance(this);

        // Delete button listener
        View.OnClickListener deleteProductFromInventory = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.status_message.setVisibility(View.INVISIBLE);
                holder.delete.setEnabled(false);
                holder.edit.setEnabled(false);
                holder.reorder.setEnabled(false);
                holder.increase_quantity.setEnabled(false);
                holder.decrease_quantity.setEnabled(false);
                int deletedRowCount = dbops.deleteProductFromInventory(product);
                if (deletedRowCount > 0) {
                    holder.status_message.setTextColor(Color.BLUE);
                    holder.status_message.setText(R.string.detail_product_delete_success);
                } else {
                    holder.status_message.setTextColor(Color.RED);
                    holder.status_message.setText(R.string.detail_product_delete_fail);
                    holder.delete.setEnabled(true);
                    holder.edit.setEnabled(true);
                    holder.reorder.setEnabled(true);
                    holder.increase_quantity.setEnabled(true);
                    holder.decrease_quantity.setEnabled(true);
                }
                holder.status_message.setVisibility(View.VISIBLE);
            }
        };
        holder.delete.setOnClickListener(deleteProductFromInventory);

        // Invoke dial intent if reorder button is clicked
        View.OnClickListener callSupplier = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phoneNumberURI = "tel:" + product.getPhone_number();
                Intent intent = new Intent(Intent.ACTION_DIAL,
                        Uri.parse(phoneNumberURI));
                startActivity(intent);
            }
        };
        holder.reorder.setOnClickListener(callSupplier);

        // Open edit page when edit button is clicked
        final Intent addProductIntent = new Intent(ProductDetailsActivity.this, ProductAddEditActivity.class);
        View.OnClickListener showEditScreen = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addProductIntent.putExtra("edit_this_product", product);
                ProductDetailsActivity.this.startActivity(addProductIntent);
            }
        };
        holder.edit.setOnClickListener(showEditScreen);

        if(product.getQuantity() <= 0) {
            holder.decrease_quantity.setEnabled(false);
        } else {
            holder.decrease_quantity.setEnabled(true);
        }

        int colorFrom = Color.YELLOW;
        int colorTo = Color.TRANSPARENT;
        final ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
        colorAnimation.setDuration(400); // milliseconds
        colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                holder.quantity.setBackgroundColor((int) animator.getAnimatedValue());
            }

        });

        //Decrease and increase activity listeners and corresponding effects
        View.OnClickListener decreaseQuantity = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int updatedRows = dbops.reduceQuantity(product);
                updateQuantity(updatedRows, holder, colorAnimation);
            }
        };
        holder.decrease_quantity.setOnClickListener(decreaseQuantity);

        View.OnClickListener increaseQuantity = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int updatedRows = dbops.increaseQuantity(product);
                updateQuantity(updatedRows, holder, colorAnimation);
            }
        };
        holder.increase_quantity.setOnClickListener(increaseQuantity);
    }

    // Update the quantity in the view and show animation
    private void updateQuantity(int updatedRows, ViewHolder holder, ValueAnimator colorAnimation) {
        if(updatedRows > 0) {
            if (product.getQuantity() <= 0) {
                holder.decrease_quantity.setEnabled(false);
            } else {
                holder.decrease_quantity.setEnabled(true);
            }
            String newQuantity = getResources().getString(R.string.quantity_left, product.getQuantity());
            holder.quantity.setText(newQuantity);
            colorAnimation.start();
        }
    }

    public class ViewHolder {
        final Button edit;
        final Button delete;
        final Button reorder;
        final TextView status_message;
        final Button decrease_quantity;
        final Button increase_quantity;
        TextView product_name;
        TextView supplier_name;
        TextView supplier_number;
        TextView price;
        TextView quantity;
        View container;

        ViewHolder() {
            product_name = (TextView) findViewById(R.id.detail_product_name);
            supplier_name = (TextView) findViewById(R.id.detail_supplier_name);
            supplier_number = (TextView) findViewById(R.id.detail_supplier_number);
            price = (TextView) findViewById(R.id.detail_price);
            container = findViewById(R.id.detail_item_card);
            quantity = (TextView) findViewById(R.id.detail_quantity);
            edit = (Button) findViewById(R.id.detail_edit);
            delete = (Button) findViewById(R.id.detail_delete);
            reorder = (Button) findViewById(R.id.detail_reorder);
            status_message = (TextView) findViewById(R.id.detail_status_message);
            decrease_quantity = (Button) findViewById(R.id.decrease_quantity);
            increase_quantity = (Button) findViewById(R.id.increase_quantity);
        }
    }
}
