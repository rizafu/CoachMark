package io.github.rizafu.coachmark;

import android.databinding.BindingAdapter;
import android.databinding.ObservableField;
import android.databinding.ObservableInt;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by RizaFu on 11/9/16.
 */

public class WidgetCoachTooltipViewModel {
    public ObservableField<String> title;
    public ObservableField<String> description;
    public ObservableField<String> actionName;
    public ObservableInt textColorResource;
    public ObservableInt backgroundColor;
    public View.OnClickListener actionClick;

    public WidgetCoachTooltipViewModel() {
        this.title = new ObservableField<>();
        this.description = new ObservableField<>();
        this.actionName = new ObservableField<>();
        this.textColorResource = new ObservableInt();
        this.backgroundColor = new ObservableInt();
    }

    @BindingAdapter("android:textColor")
    public static void setTextColor(TextView view, int color){
        if (color==0){
            view.setTextColor(ContextCompat.getColor(view.getContext(),android.R.color.primary_text_light));
        } else {
            view.setTextColor(ContextCompat.getColor(view.getContext(),color));
        }
    }

    @BindingAdapter("android:background")
    public static void setBackground(View view, int color){
        if (color==0){
            view.setBackgroundColor(ContextCompat.getColor(view.getContext(),android.R.color.white));
        } else {
            view.setBackgroundColor(ContextCompat.getColor(view.getContext(),color));
        }
    }

    @BindingAdapter("android:tint")
    public static void setTint(ImageView view, int color){
        if (color==0){
            view.setColorFilter(ContextCompat.getColor(view.getContext(),android.R.color.white));
        } else {
            view.setColorFilter(ContextCompat.getColor(view.getContext(),color));
        }
    }

    boolean isEmptyValue(){
        return title.get() == null && description.get() == null && actionName.get() == null;
    }
}
