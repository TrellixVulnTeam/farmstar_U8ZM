package mayank.example.zendor.landingPageFragment;


import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import mayank.example.zendor.ApplicationQueue;
import mayank.example.zendor.LoadingClass;
import mayank.example.zendor.MainActivity;
import mayank.example.zendor.R;
import mayank.example.zendor.URLclass;

import static mayank.example.zendor.MainActivity.showError;

/**
 * A simple {@link Fragment} subclass.
 */
public class picked extends Fragment {

    private ArrayList<pickedClass> pickedList;
    private SharedPreferences sharedPreferences;
    private RecyclerView pickedRecyclerView;
    private LinearLayoutManager llm;
    private LoadingClass lc;
    private SwipeRefreshLayout swipeRefreshLayout;


    public picked() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_picked, container, false);
        sharedPreferences = getActivity().getSharedPreferences("details", Context.MODE_PRIVATE);
        pickedRecyclerView = view.findViewById(R.id.pickedRecyclerView);
        swipeRefreshLayout = view.findViewById(R.id.swipe);


        lc = new LoadingClass(getActivity());
        llm = new LinearLayoutManager(getActivity());

        pickedRecyclerView.setLayoutManager(llm);
        pickedRecyclerView.setHasFixedSize(true);

        pickedList = new ArrayList<>();


        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                getPickedData();
            }
        });

        getPickedData();

        return view;
    }

    public void getPickedData(){
        lc.showDialog();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLclass.PICKED_DATA, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("presponse", response);

                pickedList.clear();
                try {
                    JSONObject pickedParse = new JSONObject(response);
                    JSONArray pickedArray = pickedParse.getJSONArray("picked");
                    for(int i=0;i<pickedArray.length();i++){
                        JSONObject pickedDetails = pickedArray.getJSONObject(i);
                        String commodities = pickedDetails.getString("commodities");
                        String zname = pickedDetails.getString("zname");
                        String purchase_id = pickedDetails.getString("purchase_id");
                        String actual_weight = pickedDetails.getString("actual_weight");
                        String rate = pickedDetails.getString("rate");
                        String picked_ts = pickedDetails.getString("picked_ts");
                        String sellername = pickedDetails.getString("sellername");
                        String booker = pickedDetails.getString("booker");
                        pickedList.add(new pickedClass(commodities, purchase_id,sellername, zname, actual_weight, rate, picked_ts));
                    }

                } catch (JSONException e) {
                    Log.e("pexception",e+"");
                    lc.dismissDialog();

                }

                pickedAdapter adapter = new pickedAdapter(getActivity(), pickedList);
                pickedRecyclerView.setAdapter(adapter);
                MainActivity.tabLayout.getTabAt(2).setText("PICKED"+"("+pickedList.size()+")");

                lc.dismissDialog();
                swipeRefreshLayout.setRefreshing(false);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                if (error instanceof TimeoutError) {
                    Toast.makeText(getActivity(), "Time out. Reload.", Toast.LENGTH_SHORT).show();
                } else
                    showError(error, picked.class.getName(), getActivity());


                swipeRefreshLayout.setRefreshing(false);
                lc.dismissDialog();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                String pos = sharedPreferences.getString("position", "0");
                String id = sharedPreferences.getString("id","");
                String zoneid = sharedPreferences.getString("zid","");
                HashMap<String, String> map = new HashMap();
                map.put("pos", pos);
                map.put("id", id);
                map.put("zid",zoneid);
                map.put("flag","pk");
                return map;
            }
        };
        ApplicationQueue.getInstance(getActivity().getApplicationContext()).addToRequestQueue(stringRequest);
    }
}
