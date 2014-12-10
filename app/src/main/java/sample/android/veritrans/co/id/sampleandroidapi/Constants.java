package sample.android.veritrans.co.id.sampleandroidapi;

import id.co.veritrans.android.api.VTUtil.VTConfig;

/**
 * Created by muhammadanis on 12/10/14.
 */
public class Constants {

    public final static String PAYMENT_API = "https://api.veritrans.co.id/v2/token";

    public final static String PAYMENT_API_SANDBOX = "https://api.sandbox.veritrans.co.id/v2/token";

    public static String getPaymentApiUrl(){
        if(VTConfig.VT_IsProduction){
            return PAYMENT_API;
        }
        return PAYMENT_API_SANDBOX;
    }
}
