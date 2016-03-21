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
 * Created by DB on 2016/3/21.
 */
public class FragmentDirection extends Fragment {

    private Matrix matrix = new Matrix();
    private float Rotate;
    private Bitmap bitmap;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View mView = inflater.inflate(R.layout.fragment_direction, container, false);
        ImageView direction = (ImageView) mView.findViewById(R.id.iv_direction);
        bitmap = ((BitmapDrawable) getResources().getDrawable(R.drawable.direction)).getBitmap();

        ImageView north = (ImageView) mView.findViewById(R.id.iv_north);
        north.setImageResource(R.drawable.north);
        matrix.setRotate(Rotate);
        DisplayMetrics dm = new DisplayMetrics();
        bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        direction.setImageBitmap(bitmap);
        return mView;
    }

    public void setRotate(float rotate) {
        Rotate = rotate;
    }
}
