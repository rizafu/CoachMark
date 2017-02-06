package com.rizafu.coachmark;

import android.databinding.BindingAdapter;
import android.databinding.ObservableArrayList;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableInt;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.ArrayList;

import io.github.rizafu.coachmark.R;

/**
 * Created by RizaFu on 11/9/16.
 */

public class WidgetCoachTooltipViewModel {
    public ObservableArrayList<View> tooltipChild;
    public ObservableInt backgroundColor;
    public ObservableBoolean matchWidth;

    public WidgetCoachTooltipViewModel() {
        this.tooltipChild = new ObservableArrayList<>();
        this.backgroundColor = new ObservableInt();
        this.matchWidth = new ObservableBoolean();
    }

    @BindingAdapter({"tooltipBackground","tooltipMatchWidth"})
    public static void setBackground(LinearLayout layout, int color, boolean matchWidth){
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(matchWidth ? ViewGroup.LayoutParams.MATCH_PARENT : ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layout.setLayoutParams(params);

        if (matchWidth){
            if (color==0 || color<0){
                layout.setBackgroundColor(ContextCompat.getColor(layout.getContext(),android.R.color.white));
            } else {
                layout.setBackgroundColor(ContextCompat.getColor(layout.getContext(),color));
            }
        } else {// for rounded corner
            Drawable drawable = ContextCompat.getDrawable(layout.getContext(), R.drawable.shp_card);
            if (color==0 || color<0){
                drawable.setColorFilter(ContextCompat.getColor(layout.getContext(),android.R.color.white), PorterDuff.Mode.MULTIPLY);
            } else {
                drawable.setColorFilter(ContextCompat.getColor(layout.getContext(),color), PorterDuff.Mode.MULTIPLY);
            }
            layout.setBackground(drawable);
        }


        layout.invalidate();
    }

    @BindingAdapter("tooltipTint")
    public static void setTint(ImageView view, int color){
        if (color==0 || color<0){
            view.setColorFilter(ContextCompat.getColor(view.getContext(),android.R.color.white));
        } else {
            view.setColorFilter(ContextCompat.getColor(view.getContext(),color));
        }
        view.invalidate();
    }

    @BindingAdapter("tooltipChild")
    public static void setChild(LinearLayout layout, ArrayList<View> views){
        layout.removeAllViews();
        if (views!=null && views.size()>0){
            for (int i = 0; i < views.size(); i++) {
                View child = views.get(i);
                child.setTag(i);
                layout.addView(child);
            }
        }
        layout.invalidate();
    }

    boolean isEmptyValue() {
        return tooltipChild == null || tooltipChild.isEmpty();
    }
}
