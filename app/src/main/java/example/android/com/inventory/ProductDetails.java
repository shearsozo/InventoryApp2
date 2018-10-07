package example.android.com.inventory;

import android.os.Parcel;
import android.os.Parcelable;


// Represents the fields in the table in our inventory database
class ProductDetails  implements Parcelable {
    private Integer id;
    private String product_name;
    private Double price;
    private Integer quantity;
    private String supplier_name;
    private String phone_number;

    ProductDetails(Integer id, String product_name, Double price, Integer quantity, String supplier_name, String phone_number) {
        this.id = id;
        this.product_name = product_name;
        this.price = price;
        this.quantity = quantity;
        this.supplier_name = supplier_name;
        this.phone_number = phone_number;
    }

    public String getProduct_name() {
        return product_name;
    }

    public Double getPrice() {
        return price;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public String getSupplier_name() {
        return supplier_name;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public Integer getId() {
        return id;
    }

    public void setProduct_name(String product_name) {
        this.product_name = product_name;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public void setSupplier_name(String supplier_name) {
        this.supplier_name = supplier_name;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    @Override
    public int describeContents() {
      return 0;
    }

    private ProductDetails(Parcel in) {
        this.id = in.readInt();
        this.product_name = in.readString();
        this.price = in.readDouble();
        this.quantity = in.readInt();
        this.supplier_name = in.readString();
        this.phone_number = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(product_name);
        dest.writeDouble(price);
        dest.writeInt(quantity);
        dest.writeString(supplier_name);
        dest.writeString(phone_number);
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public static final Creator<ProductDetails> CREATOR = new Creator<ProductDetails>() {
        @Override
        public ProductDetails createFromParcel(Parcel in) {
            return new ProductDetails(in);
        }

        @Override
        public ProductDetails[] newArray(int size) {
            return new ProductDetails[size];
        }
    };


}
