package sample.android.veritrans.co.id.sampleandroidapi.model;

/**
 * Created by Anis on 11/14/2014.
 */
public class VTCheckoutModel {
    private VTItem item;
    private int quantity;

    public VTCheckoutModel(VTItem item, int quantity) {
        this.item = item;
        this.quantity = quantity;
    }

    public VTItem getItem() {
        return item;
    }

    public void setItem(VTItem item) {
        this.item = item;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
