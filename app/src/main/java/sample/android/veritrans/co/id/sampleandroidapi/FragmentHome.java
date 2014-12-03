package sample.android.veritrans.co.id.sampleandroidapi;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;

import sample.android.veritrans.co.id.sampleandroidapi.adapter.ShoppingItemAdapter;
import sample.android.veritrans.co.id.sampleandroidapi.model.VTItem;


public class FragmentHome extends Fragment implements View.OnClickListener{

    public static String FRAGMENT_HOME_TAG = "fragment_home";

    public static FragmentHome newInstance() {
        FragmentHome fragment = new FragmentHome();                ;
        return fragment;
    }
    public FragmentHome() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        super.onCreate(savedInstanceState);
        View v = inflater.inflate(R.layout.fragment_home, container, false);
        ListView l = (ListView) v.findViewById(R.id.list_nearby);

        v.findViewById(R.id.checkout).setOnClickListener(this);

        ArrayList<VTItem> items = new ArrayList<VTItem>();
        items.add(new VTItem(1,R.drawable.motor1,"Ducati Corse", 5000000));
        items.add(new VTItem(2,R.drawable.motor2,"Nicky Hayden", 3000000));
        items.add(new VTItem(3,R.drawable.motor3,"Mach I", 2000000));

        ShoppingItemAdapter adapter = new ShoppingItemAdapter(getActivity(),items);
        l.setAdapter(adapter);

        return v;
    }

    public void updateCheckoutVisibility(boolean visible) {
        getView().findViewById(R.id.checkout).setVisibility(visible? View.VISIBLE : View.INVISIBLE);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.checkout:
                Fragment fr = new FragmentCheckout();
                FragmentManager fm = getFragmentManager();
                FragmentTransaction fragTrans = fm.beginTransaction();
                fragTrans.addToBackStack(FRAGMENT_HOME_TAG);
                fragTrans.add(R.id.frame_container, fr, FragmentCheckout.FRAGMENT_CHECKOUT_TAG);
                fragTrans.commit();
                break;
        }
    }
}

