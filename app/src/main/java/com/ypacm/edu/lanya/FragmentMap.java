package com.ypacm.edu.lanya;

import android.app.Fragment;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.graphics.drawable.Drawable;
import android.widget.Toast;

import com.ypacm.edu.lanya.photoview.PhotoViewAttacher;
import com.ypacm.edu.lanya.photoview.PhotoViewAttacher.OnPhotoTapListener;
import com.ypacm.edu.lanya.photoview.PhotoViewAttacher.OnMatrixChangedListener;;

/**
 * Created by DB on 2016/3/21.
 * 背景假设用按比例的图片
 * 人的位置一直在屏幕中间，接下来可以改变图片角度以显示方向
 * 图片自动移动问题？
 */
public class FragmentMap extends Fragment {

    static final String PHOTO_TAP_TOAST_STRING = "Photo Tap! X: %.2f %% Y:%.2f %% ID: %d";
    static final String SCALE_TOAST_STRING = "Scaled to: %.2ff";
    static final String FLING_LOG_STRING = "Fling velocityX: %.2f, velocityY: %.2f";
    static final String TAG = "FragmentList";
    private PhotoViewAttacher mAttacher;
    private Canvas canvas;
    private Paint paint;
    private Toast mCurrentToast;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View mView = inflater.inflate(R.layout.fragment_map, container, false);


        ImageView mImageView = (ImageView) mView.findViewById(R.id.iv_map);
//        mImageView.setImageResource(R.drawable.shulai);
        Drawable bitmap = getResources().getDrawable(R.drawable.table);
        mImageView.setImageDrawable(bitmap);
        // The MAGIC happens here!
        mAttacher = new PhotoViewAttacher(mImageView);

        mAttacher.setOnMatrixChangeListener(new MatrixChangeListener());
        mAttacher.setOnPhotoTapListener(new PhotoTapListener());
//        mAttacher.setOnSingleFlingListener(new SingleFlingListener());
        return mView;
    }

    private class PhotoTapListener implements OnPhotoTapListener {

        @Override
        public void onPhotoTap(View view, float x, float y) {
            float xPercentage = x * 100f;
            float yPercentage = y * 100f;

            Log.i(TAG, "" + mAttacher.getDisplayMatrix());
            showToast(String.format(PHOTO_TAP_TOAST_STRING, xPercentage, yPercentage, view == null ? 0 : view.getId()));
        }

        @Override
        public void onOutsidePhotoTap() {
            showToast("You have a tap event on the place where out of the photo.");
        }
    }

    private void showToast(CharSequence text) {
        if (null != mCurrentToast) {
            mCurrentToast.cancel();
        }

        mCurrentToast = Toast.makeText(MainActivity.myThis, text, Toast.LENGTH_SHORT);
        mCurrentToast.show();
    }

    private class MatrixChangeListener implements OnMatrixChangedListener {

        @Override
        public void onMatrixChanged(RectF rect) {
            showToast(String.format(SCALE_TOAST_STRING, mAttacher.getScale()));
        }
    }
}
