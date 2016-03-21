package com.ypacm.edu.lanya;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by DB on 2016/3/17.
 */

public class FragmentList extends Fragment {

    List<DeviceBean> data = new ArrayList<>();
    static final String Tag = "FragmentList";
    TextView textView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_data, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //在这里添加list  坑了一天啊一天
        ListView listView = (ListView) getActivity().findViewById(R.id.list);
        listView.setAdapter(new MyAdapter(MainActivity.myThis, data));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(MainActivity.myThis, "" + position, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void setData( List<DeviceBean> itemBeanList) {
        for (DeviceBean item : itemBeanList) {
            data.add(item);
        }
    }

}