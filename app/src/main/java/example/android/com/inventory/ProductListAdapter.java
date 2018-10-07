package example.android.com.inventory;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

// Populates the list view in MainActivity
class ProductListAdapter extends BaseAdapter {

    private final Context context;
    private final Intent detailPageIntent;
    private final ContentProvider dbOps;
    private List<ProductDetails> products;

    ProductListAdapter(Context context, List<ProductDetails> products, Intent detailPageIntent, ContentProvider dbOps) {
        this.context = context;
        this.products = products;
        this.detailPageIntent = detailPageIntent;
        this.dbOps = dbOps;
    }

    @Override
    public int getCount() {
        return products.size();
    }

    @Override
    public Object getItem(int i) {
        return products.get(i);
    }

    @Override
    public long getItemId(int i) {
        return products.get(i).getId().hashCode();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup viewGroup) {
        final ViewHolder holder;
        View view = convertView;
        final ProductDetails product = ProductListAdapter.this.products.get(position);

        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.list_item, viewGroup, false);
            holder = new ViewHolder(view);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        //populate the contents of list item
        holder.product_name.setText(product.getProduct_name());
        final String price = context.getResources().getString(R.string.product_price_string, product.getPrice());
        holder.price.setText(price);
        final String quantityLeft = context.getResources().getString(R.string.quantity_left, product.getQuantity());
        holder.quantity.setText(quantityLeft);

        //Animation effect to highlight the updated quantity value
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

        //Event listener for the sale button
        View.OnClickListener sellProduct = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Update the quantity of the product
                int updatedRowsCount = ProductListAdapter.this.dbOps.reduceQuantity(product);
                if(updatedRowsCount > 0) {
                    if(product.getQuantity() <= 0) {
                        holder.sale.setEnabled(false);
                    }
                    final String newQuantity = context.getResources().getString(R.string.quantity_left, product.getQuantity());
                    holder.quantity.setText(newQuantity);
                    //Animation effect
                    colorAnimation.start();
                }
            }
        };

        holder.sale.setOnClickListener(sellProduct);

        // Open the details page through intent when user clicks on the container
        View.OnClickListener showDetailsPage = new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                ProductDetails item = ProductListAdapter.this.products.get(position);
                ProductListAdapter.this.detailPageIntent.putExtra("this_product", item);
                ProductListAdapter.this.context.startActivity(ProductListAdapter.this.detailPageIntent);
            }
        };
        holder.container.setOnClickListener(showDetailsPage);

        if(product.getQuantity() <= 0) {
            holder.sale.setEnabled(false);
        } else {
            holder.sale.setEnabled(true);
        }

        return view;
    }

    public void setProducts(List<ProductDetails> products) {
        this.products = products;
    }

    public class ViewHolder {
        private final View layout;
         final Button sale;
        // UI controls from the news_item_row layout xml file
        TextView product_name;
        TextView price;
        TextView quantity;
        View container;

        ViewHolder(View v) {
            layout = v;
            product_name = (TextView) v.findViewById(R.id.product_name);
            price = (TextView) v.findViewById(R.id.price);
            container = v.findViewById(R.id.item_card);
            quantity = (TextView) v.findViewById(R.id.quantity);
            sale = (Button) v.findViewById(R.id.sale);
        }
    }

}
