package sample.android.veritrans.co.id.sampleandroidapi.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import sample.android.veritrans.co.id.sampleandroidapi.R;
import sample.android.veritrans.co.id.sampleandroidapi.model.VTCheckoutModel;
import sample.android.veritrans.co.id.sampleandroidapi.model.VTItem;

/**
 * Created by Anis on 11/14/2014.
 */
public class CheckoutAdapter extends ArrayAdapter<VTCheckoutModel> {

    private ArrayList<VTCheckoutModel> checkoutItems;
    private Context context;

    public CheckoutAdapter(Context context, List<VTCheckoutModel> checkoutItems){
        super(context, R.layout.checkout_adapter, checkoutItems);
        this.checkoutItems = new ArrayList<VTCheckoutModel>();
        this.checkoutItems.addAll(checkoutItems);
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater layoutInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = layoutInflater.inflate(R.layout.checkout_adapter, null);
        TextView productName = (TextView) rowView.findViewById(R.id.ch_product_name);
        TextView productQuantity = (TextView) rowView.findViewById(R.id.ch_product_quantity);
        TextView productPrice = (TextView) rowView.findViewById(R.id.ch_product_price);
        TextView productTotal = (TextView) rowView.findViewById(R.id.ch_total);

        VTCheckoutModel model = checkoutItems.get(position);
        productName.setText(model.getItem().getName());
        productQuantity.setText(Integer.toString(model.getQuantity()));
        productPrice.setText(Integer.toString(model.getItem().getPrice()));
        productTotal.setText(Integer.toString(model.getItem().getPrice() * model.getQuantity()));

        return rowView;



    }
}
