package com.ypacm.edu.lanya;

import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by DB on 2016/3/16.
 */
public class ViewPagerTest extends AppCompatActivity {

    private ViewPager mViewPager;
    private int[] mImgIds = new int[]{R.drawable.ic_action_settings, R.drawable.ic_action_location, R.drawable.ic_action_refresh};
    private List<ImageView> mImages = new ArrayList<ImageView>();


    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_pager);

        mViewPager = (ViewPager) findViewById(R.id.id_viewPager);
        //为viewPager添加动画效果
        mViewPager.setPageTransformer(true, new DepthPageTransformer());
        mViewPager.setAdapter(new PagerAdapter() {

            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                ImageView imageView = new ImageView(getBaseContext());
                imageView.setImageResource(mImgIds[position]);
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                container.addView(imageView);
                mImages.add(imageView);
                return imageView;
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                container.removeView(mImages.get(position));
            }

            @Override
            public int getCount() {
                return mImgIds.length;
            }

            @Override
            public boolean isViewFromObject(View view, Object object) {
                return view == object;
            }
        });
    }
}
