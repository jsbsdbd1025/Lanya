package com.ypacm.edu.lanya;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by DB on 2016/3/21.
 */
public class FragmentDirection extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View mView = inflater.inflate(R.layout.fragment_direction, container, false);

        ImageView north = (ImageView) mView.findViewById(R.id.iv_north);
        north.setImageResource(R.drawable.north);

        LinearLayout ll = (LinearLayout) mView.findViewById(R.id.layout1);


        return mView;
    }
}
