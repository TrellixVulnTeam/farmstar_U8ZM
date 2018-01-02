package mayank.example.zendor.navigationDrawerOption;


import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import mayank.example.zendor.R;
import mayank.example.zendor.onClickBuyer.saleAdapter;

/**
 * A simple {@link Fragment} subclass.
 */
public class saleDelivered extends Fragment {

    ArrayList arrayList;
    private RecyclerView recyclerView;
    private LinearLayoutManager llm;

    public saleDelivered(){

    }
    @SuppressLint("ValidFragment")
    public saleDelivered(ArrayList arrayList) {
       this.arrayList = arrayList;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sale_delivered, container, false);

        llm = new LinearLayoutManager(getActivity());
        recyclerView = view.findViewById(R.id.recycler);
        recyclerView.setLayoutManager(llm);
        recyclerView.setHasFixedSize(true);


        saleAdapter adapter = new saleAdapter(getActivity(), arrayList, 0);
        recyclerView.setAdapter(adapter);

        return view;
    }

}
