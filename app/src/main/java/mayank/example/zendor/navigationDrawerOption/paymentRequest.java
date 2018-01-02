package mayank.example.zendor.navigationDrawerOption;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
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
import mayank.example.zendor.R;
import mayank.example.zendor.URLclass;
import mayank.example.zendor.landingPageFragment.picked;
import mayank.example.zendor.onClickSeller.OnClickSellerCard;
import mayank.example.zendor.onClickSeller.sellerDetails;
import mayank.example.zendor.onClickSeller.sellerLedger;
import mayank.example.zendor.onClickSeller.sellerPurchases;

import static mayank.example.zendor.MainActivity.showError;

public class paymentRequest extends AppCompatActivity {


    TabLayout header;
    ViewPager paymentRequestViewpager;
    public static TextView check;
    private static ArrayList<requestClass> requestList;
    private static ArrayList<requestClass> approvedList;
    private static ArrayList<requestClass> rejectedList;
    LoadingClass lc;
    private ImageView back;
    private SharedPreferences sharedPreferences;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_request);

        paymentRequestViewpager = findViewById(R.id.paymentRequestViewpager);
        header = findViewById(R.id.header);
        check = findViewById(R.id.click);
        lc = new LoadingClass(this);
        back = findViewById(R.id.back);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getRequests(paymentRequest.this);
            }
        });

        requestList = new ArrayList<>();
        approvedList = new ArrayList<>();
        rejectedList = new ArrayList<>();

        sharedPreferences = getSharedPreferences("details", MODE_PRIVATE);


        header.setupWithViewPager(paymentRequestViewpager);


        createpager();
        getRequests(this);
    }

    private void createpager(){
        viewPagerAdapter adapter = new viewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(new pendingFragment(requestList), "Pending");
        adapter.addFrag(new processedFragment(approvedList), "Processed");
        adapter.addFrag(new rejectedFragment(rejectedList), "Rejected");
        paymentRequestViewpager.setAdapter(adapter);
    }

    public static class viewPagerAdapter extends FragmentPagerAdapter {
        ArrayList<Fragment> fragmentArrayList = new ArrayList<>();
        ArrayList<String> titleList = new ArrayList<>();
        public viewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return fragmentArrayList.get(position);
        }

        @Override
        public int getCount() {
            return fragmentArrayList.size();
        }

        public void addFrag(Fragment fragment, String title){
            fragmentArrayList.add(fragment);
            titleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return titleList.get(position);
        }
    }


    public void getRequests(Context context){

        lc.showDialog();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLclass.PAYMENT_REQUEST, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    Log.e("request", response);
                    rejectedList.clear();
                    requestList.clear();
                    approvedList.clear();
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("details");
                    for(int i =0;i<jsonArray.length();i++){
                        JSONObject request = jsonArray.getJSONObject(i);
                        String pid = request.getString("sid");
                        String sname = request.getString("seller_name");
                        String date = request.getString("date");
                        String amount = request.getString("Amount");
                        String sender = request.getString("sender");
                        String receiver = request.getString("receiver");
                        String raj = request.getString("raj");
                        String processedAt = request.getString("processed_date");
                        String rejectedBy = request.getString("rejected_by");
                        String rejectedAt = request.getString("rejected_date");
                        String remark = request.getString("transfer_details");
                        String flag = request.getString("flag");
                        String sender_id =request.getString("sender_id");
                        String seller_id = request.getString("seller_id");

                        switch (raj){
                            case "r":
                                requestList.add(new requestClass(pid, sender, amount, sname, date,flag, raj, sender_id, seller_id));
                                break;
                            case "a":
                                approvedList.add(new requestClass(pid, sender, processedAt,amount,flag, sname, raj));
                                break;
                            case "j":
                                rejectedList.add(new requestClass(pid, rejectedBy, rejectedAt, flag, remark, raj));
                                break;

                        }
                    }

                } catch (JSONException e) {
                   Log.e("error", e+"");
                }

                createpager();
                lc.dismissDialog();


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                if (error instanceof TimeoutError) {
                    Toast.makeText(paymentRequest.this, "Time out. Reload.", Toast.LENGTH_SHORT).show();
                } else
                    showError(error, paymentRequest.this.getClass().getName(), paymentRequest.this);


            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                String zid = sharedPreferences.getString("zid","");
                String pos = sharedPreferences.getString("position","");



                Map<String, String> params = new HashMap<>();
                params.put("zid", zid);
                params.put("pos", pos);
                return params;
            }
        };
        ApplicationQueue.getInstance(context.getApplicationContext()).addToRequestQueue(stringRequest);
    }



    public static class requestClass {

        private String date;
        private String amount;
        private String sender;
        private String receiver;
        private String td;
        private String dc;
        private String raj;
        private String seller_name;
        private String flag;
        private String pid;
        private String requestedBy;
        private String rejectedBy;
        private String rejDate;
        private String remark;
        private String processedBy;
        private String processedAt;
        private String sflag;
        private String seller_id;
        private String sid;



        public requestClass(String pid,String processedBy, String processedAt, String amount,String sflag, String seller_name, String flag){
            this.processedBy = processedBy;
            this.processedAt = processedAt;
            this.amount = amount;
            this.seller_name = seller_name;
            this.flag = flag;
            this.pid = pid;
            this.sflag = sflag;
        }

        public requestClass(String pid,String rejectedBy, String rejDate,String sflag, String remark, String flag){
            this.rejectedBy = rejectedBy;
            this.rejDate = rejDate;
            this.flag = flag;
            this.remark = remark;
            this.pid = pid;
            this.sflag = sflag;

        }

        public requestClass(String pid, String requestedBy, String amount, String seller_name, String date,String sflag, String flag, String seller_id, String sid){
            this.pid = pid;
            this.requestedBy = requestedBy;
            this.amount = amount;
            this.date = date;
            this.seller_name = seller_name;
            this.flag = flag;
            this.sflag = sflag;
            this.seller_id = seller_id;
            this.sid = sid;

        }

        public String getSid() {
            return sid;
        }

        public String getSeller_id() {
            return seller_id;
        }

        public String getSflag() {
            return sflag;
        }

        public String getFlag() {
            return flag;
        }

        public String getPid() {
            return pid;
        }

        public String getProcessedAt() {
            return processedAt;
        }

        public String getProcessedBy() {
            return processedBy;
        }

        public String getRejDate() {
            return rejDate;
        }

        public String getRejectedBy() {
            return rejectedBy;
        }

        public String getRemark() {
            return remark;
        }

        public String getRequestedBy() {
            return requestedBy;
        }

        public String getSeller_name() {
            return seller_name;
        }
        public String getDate() {
            return date;
        }

        public String getAmount() {
            return amount;
        }

        public String getDc() {
            return dc;
        }

        public String getRaj() {
            return raj;
        }

        public String getReceiver() {
            return receiver;
        }

        public String getSender() {
            return sender;
        }

        public String getTd() {
            return td;
        }
    }
}
