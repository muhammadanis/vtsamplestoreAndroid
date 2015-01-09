package sample.android.veritrans.co.id.sampleandroidapi;



import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import id.co.veritrans.android.api.VTDirect;
import id.co.veritrans.android.api.VTInterface.ITokenCallback;
import id.co.veritrans.android.api.VTModel.VTCardDetails;
import id.co.veritrans.android.api.VTModel.VTToken;
import id.co.veritrans.android.api.VTUtil.VTConfig;
import sample.android.veritrans.co.id.sampleandroidapi.adapter.CheckoutAdapter;
import sample.android.veritrans.co.id.sampleandroidapi.model.VTCheckoutModel;
import sample.android.veritrans.co.id.sampleandroidapi.model.VTItem;
import sample.android.veritrans.co.id.sampleandroidapi.model.VTRestResponse;
import sample.android.veritrans.co.id.sampleandroidapi.view.CustomWebView;


/**
 * A simple {@link Fragment} subclass.
 *
 */
public class FragmentCheckout extends Fragment implements  View.OnClickListener{

    public static String FRAGMENT_CHECKOUT_TAG = "fragment_checkout";

    AlertDialog dialog3ds;
    ProgressDialog sendServerProgress;
    int totalPrice;


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
        totalPrice = 0;
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
                            Log.d("VtLog",token.getToken_id());

                            CustomWebView webView = new CustomWebView(getActivity());
                            webView.getSettings().setJavaScriptEnabled(true);
                            webView.setOnTouchListener(new View.OnTouchListener() {
                                @Override
                                public boolean onTouch(View v, MotionEvent event) {
                                    switch (event.getAction()) {
                                        case MotionEvent.ACTION_DOWN:
                                        case MotionEvent.ACTION_UP:
                                            if (!v.hasFocus()) {
                                                v.requestFocus();
                                            }
                                            break;
                                    }
                                    return false;
                                }
                            });
                            webView.setWebChromeClient(new WebChromeClient());
                            webView.setWebViewClient(new VtWebViewClient(token.getToken_id(),totalPrice+""));
                            webView.loadUrl(token.getRedirect_url());

                            AlertDialog.Builder alertBuilder = new AlertDialog.Builder(getActivity());
                            dialog3ds = alertBuilder.create();


                            dialog3ds.setTitle("3D Secure Veritrans");
                            dialog3ds.setView(webView);
                            webView.requestFocus(View.FOCUS_DOWN);
                            alertBuilder.setNegativeButton("Close", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.dismiss();
                                }
                            });

                            dialog3ds.show();

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
        cardDetails.setGross_amount(totalPrice+"");
        return cardDetails;
    }

    private class SendTokenAsync extends AsyncTask<String, Void, String>{

        @Override
        protected String doInBackground(String... strings) {
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost("http://128.199.141.15:9091/index.php");

            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
            try {
                nameValuePairs.add(new BasicNameValuePair("token-id", URLEncoder.encode(strings[0],"UTF-8")));
                nameValuePairs.add(new BasicNameValuePair("price",strings[1]));
                httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                HttpResponse response = httpClient.execute(httpPost);
                String jsonResponse = EntityUtils.toString(response.getEntity()).toString();
                Log.d("VtLog", jsonResponse);
                return jsonResponse;
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }catch (IOException e) {
                e.printStackTrace();
            }


            return "";

        }

        @Override
        protected void onPostExecute(String restResponse) {
            //dismiss dialog
            if(dialog3ds != null){
                dialog3ds.dismiss();
            }
            if(sendServerProgress.isShowing()){
                sendServerProgress.dismiss();
            }

            try{
                VTRestResponse response = new Gson().fromJson(restResponse,VTRestResponse.class);
                if(response.status.equalsIgnoreCase("success")){
                    Toast.makeText(getActivity(),"Success To Add payment: "+response.body.order_id,Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(getActivity(),"Failed to Pay",Toast.LENGTH_SHORT).show();
                }
            }catch (Exception ex){
                Toast.makeText(getActivity(),"Failed to Pay",Toast.LENGTH_SHORT).show();
            }

        }
    }

    private class VtWebViewClient extends WebViewClient {

        String token;
        String price;

        public VtWebViewClient(String token, String price){
            this.token = token;
            this.price = price;
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);

            Log.d("VtLog", url);

            if (url.startsWith(Constants.getPaymentApiUrl() + "/callback/")) {
                //send token to server
                SendTokenAsync sendTokenAsync = new SendTokenAsync();
                sendTokenAsync.execute(token,price);
                //close web dialog
                dialog3ds.dismiss();
                //show loading dialog
                sendServerProgress = ProgressDialog.show(getActivity(),"","Sending Data to Server. Please Wait...",true);

            } else if (url.startsWith(Constants.getPaymentApiUrl() + "/redirect/") || url.contains("3dsecure")) {
                /* Do nothing */
            } else {
                if(dialog3ds != null){
                    dialog3ds.dismiss();
                }
            }
        }

    }
}
