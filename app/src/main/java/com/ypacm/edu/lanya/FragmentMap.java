package com.ypacm.edu.lanya;

import android.app.Fragment;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.graphics.drawable.Drawable;
import android.widget.Toast;

import com.ypacm.edu.lanya.photoview.PhotoViewAttacher;


/**
 * Created by DB on 2016/3/21.
 */
public class FragmentMap extends Fragment {

    static final String PHOTO_TAP_TOAST_STRING = "Photo Tap! X: %.2f %% Y:%.2f %% ID: %d";
    static final String SCALE_TOAST_STRING = "Scaled to: %.2ff";
    static final String FLING_LOG_STRING = "Fling velocityX: %.2f, velocityY: %.2f";
    static final String Tag = "FragmentList";
    private PhotoViewAttacher mAttacher;

    private Toast mCurrentToast;

    private Matrix mCurrentDisplayMatrix = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View mView = inflater.inflate(R.layout.fragment_map, container, false);
        ImageView mImageView = (ImageView) mView.findViewById(R.id.imageView);
        mImageView.setImageResource(R.drawable.shulai);
        Drawable bitmap = getResources().getDrawable(R.drawable.shulai);
        mImageView.setImageDrawable(bitmap);

        // The MAGIC happens here!
        mAttacher = new PhotoViewAttacher(mImageView);

        // Lets attach some listeners, not required though!
//        mAttacher.setOnMatrixChangeListener(new MatrixChangeListener());
//        mAttacher.setOnPhotoTapListener(new PhotoTapListener());
//        mAttacher.setOnSingleFlingListener(new SingleFlingListener());
        return mView;
    }
}
