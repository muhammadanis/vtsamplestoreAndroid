package sample.android.veritrans.co.id.sampleandroidapi;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.HashMap;

import sample.android.veritrans.co.id.sampleandroidapi.model.VTItem;


public class MainActivity extends Activity {

    private HashMap<VTItem, Integer> cart;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // hide statusbar of Android
        // could also be done later
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main);
        cart = new HashMap<VTItem, Integer>();

        Fragment fr = new FragmentHome();
        FragmentManager fm = getFragmentManager();
        FragmentTransaction fragTrans = fm.beginTransaction();
        fragTrans.replace(R.id.frame_container, fr,FragmentHome.FRAGMENT_HOME_TAG);
        fragTrans.commit();
        if (savedInstanceState == null) {

        }

    }

    public HashMap<VTItem,Integer> getCart(){
        return cart;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void updatePrice() {
        int totalPrice = 0;
        for(VTItem item : cart.keySet()){
            int quantity = cart.get(item);
            int itemPrice = item.getPrice() * quantity;
            totalPrice+=itemPrice;
        }
        TextView txtGross = (TextView) findViewById(R.id.gross_amount);
        txtGross.setText(Integer.toString(totalPrice));
        //update checkout button
        FragmentHome fragmentHome = (FragmentHome) getFragmentManager().findFragmentByTag(FragmentHome.FRAGMENT_HOME_TAG);
        if(fragmentHome.isVisible()){
            fragmentHome.updateCheckoutVisibility(totalPrice == 0? false : true);
        }

    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            return rootView;
        }
    }
}
