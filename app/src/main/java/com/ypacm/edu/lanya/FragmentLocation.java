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

/**
 * Created by DB on 2016/3/31.
 */
public class FragmentLocation extends Fragment {

    private Matrix matrix;
    private Bitmap bitmap;
    private ImageView direction;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View mView = inflater.inflate(R.layout.fragment_location, container, false);
        direction = (ImageView) mView.findViewById(R.id.iv_location);

        return mView;
    }

    public void upDateLocation(float rotate, float x, float y) {
        if (null == direction)
            return;
        matrix = new Matrix();
        matrix.setRotate(rotate);
        bitmap = ((BitmapDrawable) getResources().getDrawable(R.drawable.direction)).getBitmap();
        bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        direction.setImageBitmap(bitmap);
        direction.setX(x);
        direction.setY(y);
    }
}