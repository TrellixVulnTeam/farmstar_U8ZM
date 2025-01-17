package mayank.example.zendor.onClickExecutive;

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;

import java.util.ArrayList;

import mayank.example.zendor.R;
import mayank.example.zendor.onClickSeller.OnClickSellerCard;
import mayank.example.zendor.onClickSeller.sellerDetails;
import mayank.example.zendor.onClickSeller.sellerLedger;
import mayank.example.zendor.onClickSeller.sellerPurchases;

public class onClickExecutiveCard extends AppCompatActivity {

    private ViewPager viewPager;
    public static TabLayout header;
    private String eid;
    private String name;
    public static String status;
    private Toolbar toolbar;
    private executiveLedger instance;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_on_click_executive_card);

        viewPager = findViewById(R.id.viewPager);
        header = findViewById(R.id.header);
        header.setupWithViewPager(viewPager);
        toolbar = findViewById(R.id.toolbar);

        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                instance.removeListener();
            }
        });

        viewPager.setOffscreenPageLimit(3);

        Bundle bundle = getIntent().getExtras();
        if(bundle !=null){
            eid = bundle.getString("exec_id");
            name = bundle.getString("name");
            status = bundle.getString("status");
        }


        createpager();

    }

    private void createpager(){
        viewPagerAdapter adapter = new viewPagerAdapter(getSupportFragmentManager());
        instance = executiveLedger.newInstance(eid);
        adapter.addFrag(executive_details.newInstance(eid, name), "Details");
        adapter.addFrag(executive_allpurchases.newInstance(eid), "Purchases");
        adapter.addFrag(instance, "Ledger");
        viewPager.setAdapter(adapter);
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        instance.removeListener();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        instance.removeListener();
    }
}
