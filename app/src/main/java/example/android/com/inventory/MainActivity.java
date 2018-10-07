package example.android.com.inventory;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Main activity shows the list of products in the inventory database
 */
public class MainActivity extends AppCompatActivity {

    private ProductListAdapter listAdapter;
    private ContentProvider dbOps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dbOps = ContentProvider.getInstance(this);
        setupListView();
    }

    /**
     * Reads the contents of inventory database and populates the listview using ProductListAdapter
     */
    private void setupListView() {
        Intent detailPageIntent = new Intent(MainActivity.this, ProductDetailsActivity.class);
        ListView listView = (ListView) findViewById(R.id.product_list_view);
        List<ProductDetails> products = new ArrayList<>();
        listAdapter = new ProductListAdapter(this, products, detailPageIntent, dbOps);
        listView.setAdapter(listAdapter);
        createProductAddition();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //Refresh the contents of the list every time the MainActivity is resumed
        List<ProductDetails> products = dbOps.getAllProducts();
        listAdapter.setProducts(products);
        listAdapter.notifyDataSetChanged();
        TextView emptyMessage = (TextView) findViewById(R.id.emptyProductsMessage);
        if(products == null || products.isEmpty()) {
            emptyMessage.setVisibility(View.VISIBLE);
        } else {
            emptyMessage.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * When ADD PRODUCT button is clicked, this method will show the product add activity page using intent
     */
    private void createProductAddition() {
        Button addProduct = (Button) findViewById(R.id.product_add);
        final Intent addProductIntent = new Intent(MainActivity.this, ProductAddEditActivity.class);
        View.OnClickListener showAddProductPage = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.this.startActivity(addProductIntent);
            }
        };
        addProduct.setOnClickListener(showAddProductPage);
    }
}
