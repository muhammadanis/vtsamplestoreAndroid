package sample.android.veritrans.co.id.sampleandroidapi;



import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

import id.co.veritrans.android.api.VTDirect;
import id.co.veritrans.android.api.VTInterface.ITokenCallback;
import id.co.veritrans.android.api.VTModel.VTCardDetails;
import id.co.veritrans.android.api.VTModel.VTToken;
import id.co.veritrans.android.api.VTUtil.VTConfig;
import sample.android.veritrans.co.id.sampleandroidapi.adapter.CheckoutAdapter;
import sample.android.veritrans.co.id.sampleandroidapi.model.VTCheckoutModel;
import sample.android.veritrans.co.id.sampleandroidapi.model.VTItem;
import sample.android.veritrans.co.id.sampleandroidapi.view.CustomWebView;


/**
 * A simple {@link Fragment} subclass.
 *
 */
public class FragmentCheckout extends Fragment implements  View.OnClickListener{

    public static String FRAGMENT_CHECKOUT_TAG = "fragment_checkout";

    public FragmentCheckout() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View v = inflater.inflate(R.layout.fragment_checkout, container,false);
        ListView l = (ListView) v.findViewById(R.id.list_bought);

        ArrayList<VTCheckoutModel> items = new ArrayList<VTCheckoutModel>();
        HashMap<VTItem,Integer> cart = ((MainActivity)getActivity()).getCart();
        int totalPrice = 0;
        for(VTItem item : cart.keySet()){
            items.add(new VTCheckoutModel(item,cart.get(item)));
            totalPrice += item.getPrice() * cart.get(item);
        }
        CheckoutAdapter adapter = new CheckoutAdapter(getActivity(),items);
        l.setAdapter(adapter);

        TextView txtTotal = (TextView) v.findViewById(R.id.grandTotal);
        txtTotal.setText(Integer.toString(totalPrice));

        //set button click listener
        ImageButton ccButton = (ImageButton) v.findViewById(R.id.ccButton);
        ccButton.setOnClickListener(this);
        return v;
    }


    @Override
    public void onClick(View view) {
        //set production settings
        switch (view.getId()){
            case R.id.ccButton:{
                //set environment
                VTConfig.VT_IsProduction = false;
                //set client key
                VTConfig.CLIENT_KEY = "VT-client-3YUXFj6X0XBpeDgf";

                VTDirect vtDirect = new VTDirect();
                //set using 3dsecure or not
                CheckBox rb3dSecure = (CheckBox) getView().findViewById(R.id.rb_3d_secure);
                VTCardDetails cardDetails = null;
                if(rb3dSecure.isChecked()){
                    cardDetails = CardFactory(true);
                }else{
                    cardDetails = CardFactory(false);
                }
                vtDirect.setCard_details(cardDetails);

                //set loading dialog
                final ProgressDialog loadingDialog = ProgressDialog.show(getActivity(),"","Loading, Please Wait...",true);
                vtDirect.getToken(new ITokenCallback() {
                    @Override
                    public void onSuccess(VTToken token) {
                        loadingDialog.cancel();
                        if(token.getRedirect_url() != null){
                            //using 3d secure
                            //show it to user using webview
                            CustomWebView webView = new CustomWebView(getActivity());
                            webView.getSettings().setJavaScriptEnabled(true);
                            webView.setOnTouchListener(new View.OnTouchListener()
                            {
                                @Override
                                public boolean onTouch(View v, MotionEvent event)
                                {
                                    switch (event.getAction())
                                    {
                                        case MotionEvent.ACTION_DOWN:
                                        case MotionEvent.ACTION_UP:
                                            if (!v.hasFocus())
                                            {
                                                v.requestFocus();
                                            }
                                            break;
                                    }
                                    return false;
                                }
                            });
                            webView.loadUrl(token.getRedirect_url());

                            


                            AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
                            alert.setTitle("3D Secure Veritrans");
                            alert.setView(webView);
                            webView.requestFocus(View.FOCUS_DOWN);
                            alert.setNegativeButton("Close", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.dismiss();
                                }
                            });

                            alert.show();

                        }
                        //print token
                        TextView tokenText = (TextView) getView().findViewById(R.id.txt_token);
                        tokenText.setText(token.getToken_id());

                    }

                    @Override
                    public void onError(Exception e) {
                        loadingDialog.cancel();
                        Toast.makeText(getActivity(),e.getMessage(),Toast.LENGTH_SHORT);
                    }
                });

            }
                break;
        }

    }

    private VTCardDetails CardFactory(boolean secure){
        VTCardDetails cardDetails = new VTCardDetails();
        cardDetails.setCard_number("4811111111111114");
        cardDetails.setCard_cvv("123");
        cardDetails.setCard_exp_month(1);
        cardDetails.setCard_exp_year(2020);
        cardDetails.setSecure(secure);
        cardDetails.setGross_amount("1000000");
        return cardDetails;
    }
}
