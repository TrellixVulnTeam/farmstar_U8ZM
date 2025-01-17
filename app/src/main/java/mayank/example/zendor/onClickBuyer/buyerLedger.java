package mayank.example.zendor.onClickBuyer;


import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import mayank.example.zendor.ApplicationQueue;
import mayank.example.zendor.LoadingClass;
import mayank.example.zendor.R;
import mayank.example.zendor.URLclass;
import mayank.example.zendor.onClickSeller.ledgerAdapter;
import mayank.example.zendor.onClickSeller.sellerLedger;
import xendorp1.application_classes.AppController;

import static android.content.Context.MODE_PRIVATE;
import static mayank.example.zendor.MainActivity.showError;

/**
 * A simple {@link Fragment} subclass.
 */
public class buyerLedger extends Fragment {

    public static String BUYER_ID = "buyer_id";
    private String buyer_id;
    private ListView buyerListView;
    private TextView cb;
    private ArrayList<sellerLedger.ledgerClass> ledgerList;
    private TextView buyerNameAndZone;
    private ImageView back;
    private LoadingClass lc;
    public static TextView click;
    private TextView transfer;
    private SharedPreferences sharedPreferences;
    private double ucb;
    public static double buyercb;
    private int count = 0;
    private DatabaseReference mDatabase;


    public buyerLedger() {

    }

    public static buyerLedger newInstance(String buyer_id){
        buyerLedger fragment = new buyerLedger();
        Bundle bundle = new Bundle();
        bundle.putString(BUYER_ID, buyer_id);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            buyer_id = getArguments().getString(BUYER_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_buyer_ledger, container, false);

        buyerListView = view.findViewById(R.id.ledgerView);
        transfer = view.findViewById(R.id.transfer);
        cb = view.findViewById(R.id.cb);
        click = view.findViewById(R.id.clickItBuyer);
        buyerNameAndZone = view.findViewById(R.id.sellerNameAndZone);

        ledgerList = new ArrayList<>();
        lc = new LoadingClass(getActivity());
        mDatabase = FirebaseDatabase.getInstance().getReference().child("buyers").child(buyer_id).child("Ledger");


        mDatabase.addValueEventListener(valueEventListener);

        sharedPreferences = getActivity().getSharedPreferences("details", MODE_PRIVATE);
        final String pos = sharedPreferences.getString("position","");

        getBuyerLedger();
        getBuyerCb();
        getUserCb();

        buyerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String click = ledgerList.get(position).getFlag();
                if(!click.equals("0")){
                    Intent intent1 = new Intent(getActivity(), onClickDeliveredCard.class);
                    intent1.putExtra("sid", click);
                    intent1.putExtra("f", 2 + "");
                    startActivity(intent1);
                }
            }
        });




        click.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getBuyerLedger();
                getBuyerCb();
                getUserCb();
            }
        });

        return view;
    }

    public void removeListener(){
        mDatabase.removeEventListener(valueEventListener);
    }
    private ValueEventListener valueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            if(dataSnapshot.exists() && count != 0){
                getBuyerLedger();
                getBuyerCb();
                getUserCb();
            }
            count++;
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };

    private void getUserCb(){
        lc.showDialog();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLclass.SELLER_CB, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject json = new JSONObject(response);
                    String CB = json.getString("current_balance");
                    ucb = Double.parseDouble(CB);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                lc.dismissDialog();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                if (error instanceof TimeoutError) {
                    Toast.makeText(getActivity(), "Time out. Reload.", Toast.LENGTH_SHORT).show();
                } else
                    showError(error, buyerLedger.class.getName(), getActivity());


                lc.dismissDialog();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                String id = sharedPreferences.getString("id","");


                Map<String, String> parameters = new HashMap<>();
                parameters.put("id",id);
                return parameters;
            }
        };

        AppController.getInstance().addToRequestQueue(stringRequest);
    }


    private void getBuyerCb(){

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLclass.SELLER_CB, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject json = new JSONObject(response);
                    String CB = json.getString("current_balance");
                    cb.setText('\u20B9'+CB);
                    buyercb = Double.parseDouble(CB);
                    buyerDetails.amountDue.setText('\u20B9'+" "+CB);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {


                if (error instanceof TimeoutError) {
                    Toast.makeText(getActivity(), "Time out. Reload.", Toast.LENGTH_SHORT).show();
                } else
                    showError(error, buyerLedger.class.getName(), getActivity());


            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Log.e("buyer id", buyer_id);
                Map<String, String> parameters = new HashMap<>();
                parameters.put("id",buyer_id);
                return parameters;
            }
        };
        AppController.getInstance().addToRequestQueue(stringRequest);
    }

    private void getBuyerLedger(){

        lc.showDialog();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLclass.BUYER_LEDGER, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    ledgerList.clear();

                    JSONObject json = new JSONObject(response);
                    JSONArray ledgerArray = json.getJSONArray("ledger");
                    for(int i =0;i<ledgerArray.length();i++){
                        JSONObject ledger = ledgerArray.getJSONObject(i);
                        String date = ledger.getString("date");
                        String pid = ledger.getString("pid");
                        String dc = ledger.getString("dc");
                        String balance = ledger.getString("balance");
                        String click = ledger.getString("click");
                        try {
                            if (dc.substring(dc.lastIndexOf(" ")).equals(" cr") || dc.substring(dc.lastIndexOf(" ")).equals(" dr"))
                                dc = '\u20B9' + dc;
                        }catch (Exception e){}
                        ledgerList.add(new sellerLedger.ledgerClass(date, pid, dc, '\u20B9'+balance, click, ""));
                    }
                    String name = json.getString("buyer_name");
                    buyerNameAndZone.setText(name);
                } catch (JSONException e) {
                    e.printStackTrace();
                    lc.dismissDialog();
                }

                ledgerAdapter adapter = new ledgerAdapter(getActivity(),0, ledgerList);
                buyerListView.setAdapter(adapter);
                lc.dismissDialog();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {


                if (error instanceof TimeoutError) {
                    Toast.makeText(getActivity(), "Time out. Reload.", Toast.LENGTH_SHORT).show();
                } else
                    showError(error, buyerLedger.class.getName(), getActivity());


                lc.dismissDialog();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parameters = new HashMap<>();
                parameters.put("id",buyer_id);
                return parameters;
            }
        };

        AppController.getInstance().addToRequestQueue(stringRequest);
    }


}
