package io.github.rizafu.coachmark;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.graphics.Rect;
import android.support.annotation.IdRes;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPropertyAnimatorListenerAdapter;
import android.support.v7.widget.CardView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.animation.Animation;
import android.widget.FrameLayout;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;

import io.github.rizafu.coachmark.databinding.WidgetCoachTooltipBinding;

/**
 * Created by RizaFu on 11/7/16.
 */

public class CoachMark {
    private int animDuration;
    private Activity activity;
    private FrameLayout container;

    private @TooltipAlignment int tooltipAlignment;
    private @PointerTooltipAlignment int tooltipPointerAlignment;
    private int overlayPadding;
    private int tooltipMargin;
    private int backgroundColorResource;
    private int radius;
    private boolean dismissible;
    private boolean isCircleMark;
    private boolean isShow;
    private float backgroundAlpha;
    private WidgetCoachTooltipViewModel tooltipViewModel;
    private WidgetCoachTooltipBinding tooltipBinding;
    private CoachMarkOverlay coachMarkOverlay;
    private View targetView;
    private View.OnClickListener targetOnClick;
    private Runnable onDismissListener;
    private Animation tooltipShowAnimation;
    private Animation tooltipDismissAnimation;

    private final double CIRCLE_ADDITIONAL_RADIUS_RATIO = 1.5f;

    public static final int ROOT_TOP = 1;
    public static final int ROOT_BOTTOM = 2;
    public static final int TARGET_TOP = 3;
    public static final int TARGET_BOTTOM = 4;

    @IntDef({ROOT_TOP,ROOT_BOTTOM,TARGET_TOP,TARGET_BOTTOM})
    @Retention(RetentionPolicy.SOURCE)
    public @interface TooltipAlignment {}

    public static final int POINTER_RIGHT = 1;
    public static final int POINTER_MIDDLE = 2;
    public static final int POINTER_LEFT = 3;
    public static final int POINTER_GONE = 4;

    @IntDef({POINTER_RIGHT,POINTER_MIDDLE,POINTER_LEFT,POINTER_GONE})
    @Retention(RetentionPolicy.SOURCE)
    public @interface PointerTooltipAlignment{}

    private CoachMark(final Builder builder) {
        this.activity = builder.activity;
        this.container = new FrameLayout(activity);
        this.tooltipViewModel = new WidgetCoachTooltipViewModel();
        this.tooltipBinding = DataBindingUtil.inflate(activity.getLayoutInflater(),R.layout.widget_coach_tooltip,container,false);
        this.tooltipBinding.setViewModel(this.tooltipViewModel);

        this.backgroundColorResource = android.R.color.black;
        this.isCircleMark = builder.isCircleMark;
        this.targetView = builder.target;
        this.overlayPadding = builder.markerPadding;
        this.dismissible = builder.dismissible;
        this.tooltipAlignment = builder.tooltipAlignment;
        this.tooltipMargin = ViewUtils.dpToPx(builder.tooltipMargin);
        this.onDismissListener = builder.onDismissListener;
        this.tooltipShowAnimation = builder.tooltipShowAnimation;
        this.tooltipDismissAnimation = builder.tooltipDismissAnimation;
        this.radius = builder.radius;
        tooltipViewModel.tooltipChild.addAll(builder.tooltipChilds);

        Window window = activity.getWindow();
        if (window != null) {
            ViewGroup decorView = (ViewGroup) window.getDecorView();
            if (decorView != null) {
                ViewGroup content = (ViewGroup) decorView.findViewById(android.R.id.content);
                if (content != null) {
                    FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
                    container.setBackgroundColor(Color.TRANSPARENT);

                    decorView.addView(container, layoutParams);
                    coachMarkOverlay = new CoachMarkOverlay(activity);
                    coachMarkOverlay.setBackgroundResource(backgroundColorResource);
                    coachMarkOverlay.setAlpha(backgroundAlpha);
                    container.addView(coachMarkOverlay,layoutParams);
                    container.addView(tooltipBinding.getRoot());
                }
            }
        }
        animDuration = container.getResources().getInteger(android.R.integer.config_longAnimTime);
        container.setClickable(true);
        container.setVisibility(View.GONE);
        ViewCompat.setAlpha(container,0f);

        addTarget();
    }

    private void setTargetOnClick(View.OnClickListener targetOnClick) {
        this.targetOnClick = targetOnClick;
    }

    private void setTooltipAlignment(@TooltipAlignment final int tooltipAlignment, @PointerTooltipAlignment final int pointerTooltipAlignment){
        this.tooltipAlignment = tooltipAlignment;
        this.tooltipPointerAlignment = pointerTooltipAlignment;
        tooltipBinding.getRoot().getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                relocationTooltip(targetView, tooltipAlignment);
                pointerTooltipAlignment(targetView, pointerTooltipAlignment);
                tooltipBinding.getRoot().getViewTreeObserver().removeOnPreDrawListener(this);
                return false;
            }
        });
    }

    private void addTarget(){
        if (targetView!=null)
        targetView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                if (isCircleMark){
                    addCircleRect(targetView);
                } else {
                    addRoundRect(targetView);
                }
                targetView.getViewTreeObserver().removeOnPreDrawListener(this);
                return false;
            }
        });
    }

    private void addRoundRect(View view){
        Rect rect = new Rect();
        view.getGlobalVisibleRect(rect);
        int radius = this.radius;
        if (view instanceof CardView){
            CardView cardView = (CardView) view;
            radius = (int) cardView.getRadius();
        }

        final int x = rect.left;
        final int y = rect.top;
        final int width = rect.width();
        final int height = rect.height();
        addTargetClick(rect,view);
        coachMarkOverlay.setBackgroundResource(backgroundColorResource);
        coachMarkOverlay.setAlpha(backgroundAlpha);
        coachMarkOverlay.addRect(x,y,width,height, radius,overlayPadding,isCircleMark);
        coachMarkOverlay.postInvalidate();
    }

    private void addCircleRect(View view){
        Rect rect = new Rect();
        view.getGlobalVisibleRect(rect);
        int cx = rect.centerX();
        int cy = rect.centerY();

        int radius = (int) (Math.max(rect.width(), rect.height()) / 2f * CIRCLE_ADDITIONAL_RADIUS_RATIO);
        addTargetClick(rect,view);
        coachMarkOverlay.setBackgroundResource(backgroundColorResource);
        coachMarkOverlay.setAlpha(backgroundAlpha);
        coachMarkOverlay.addRect(cx,cy,0,0, radius, overlayPadding,isCircleMark);
        coachMarkOverlay.postInvalidate();
    }

    private void relocationTooltip(View view, @TooltipAlignment int alignment){
        View tooltipView = tooltipBinding.getRoot();

        final int tooltipHeight = tooltipView.getHeight();
        final int defaultPadding = 10;
        final int padding = ViewUtils.dpToPx(overlayPadding + defaultPadding);
        final int triangleHeight = ViewUtils.dpToPx(12);

        if (view!=null) {
            Rect rect = new Rect();
            view.getGlobalVisibleRect(rect);

            final int y = rect.top;
            final int height = rect.height();
            float result;
            if (alignment == TARGET_BOTTOM) {
                tooltipBinding.triangleTop.setVisibility(View.VISIBLE);
                result = y + height + padding;
                result = (float) (result + (isCircleMark? defaultPadding * CIRCLE_ADDITIONAL_RADIUS_RATIO : 0));
                tooltipView.setY(result);
            } else if (alignment == TARGET_TOP){
                tooltipBinding.triangleBottom.setVisibility(View.VISIBLE);
                result = y - tooltipHeight - padding - triangleHeight;
                result = (float) (result - (isCircleMark? defaultPadding * CIRCLE_ADDITIONAL_RADIUS_RATIO : 0));
                tooltipView.setY(result);
            }
        }

        if (alignment == ROOT_TOP){
            tooltipView.setY(0);
        } else if (alignment == ROOT_BOTTOM){
            tooltipView.setY(getScreenHeight() - tooltipHeight);
        }
        tooltipView.postInvalidate();
    }

    private void pointerTooltipAlignment(View view, @PointerTooltipAlignment int pointerTooltipAlignment){
        if (view==null)return;
        Rect rect = new Rect();
        view.getGlobalVisibleRect(rect);

        final int x = rect.left;
        final int width = rect.width();
        final int margin = ViewUtils.dpToPx(overlayPadding + 16);
        final int triangleWidth = ViewUtils.dpToPx(24);
        int result = 0;

        if (pointerTooltipAlignment == POINTER_LEFT){
            result = x + margin;
        } else if (pointerTooltipAlignment == POINTER_MIDDLE){
            result = x + (width/2);
        } else if (pointerTooltipAlignment == POINTER_RIGHT){
            result = x + (width-margin);
        }

        View triangle;
        if (this.tooltipAlignment == TARGET_TOP){
            triangle = tooltipBinding.triangleBottom;
            triangle.setX(result - (triangleWidth/2));
        } else if (this.tooltipAlignment == TARGET_BOTTOM){
            triangle = tooltipBinding.triangleTop;
            triangle.setX(result - (triangleWidth/2));
        }
        tooltipBinding.getRoot().postInvalidate();
    }

    private int getStatusBarHeight() {
        int result = 0;
        Context context = activity;
        Resources resources = context.getResources();
        int resourceId = resources.getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = resources.getDimensionPixelSize(resourceId);
        }
        return result;
    }

    private int getScreenHeight(){
        int result;
        Context context = activity;
        Resources resources = context.getResources();
        result = resources.getDisplayMetrics().heightPixels;
        return result;
    }

    private int getScreenWidth(){
        int result;
        Context context = activity;
        Resources resources = context.getResources();
        result = resources.getDisplayMetrics().heightPixels;
        return result;
    }

    private void addTargetClick(Rect rect, View view){
        View clickableView = new View(view.getContext());
        int width = rect.width();
        int height = rect.height();
        int x = rect.left - (width - rect.width()) / 2;
        int y = rect.top - (height - rect.height()) / 2;
        clickableView.setLayoutParams(new ViewGroup.MarginLayoutParams(width, height));
        ViewCompat.setTranslationY(clickableView, y);
        ViewCompat.setTranslationX(clickableView, x);
        clickableView.setOnClickListener(targetOnClick);
        clickableView.setBackgroundColor(Color.TRANSPARENT);
        container.addView(clickableView);
        container.invalidate();
    }

    private void animateTooltipShow(){
        tooltipBinding.getRoot().setVisibility(tooltipViewModel.isEmptyValue() ? View.GONE : View.VISIBLE);
        if (!tooltipViewModel.isEmptyValue() && tooltipShowAnimation!=null) {
            tooltipBinding.getRoot().startAnimation(tooltipShowAnimation);
        }
    }

    private void animateTooltipDismiss(){
        if (!tooltipViewModel.isEmptyValue() && tooltipDismissAnimation!=null) {
            tooltipDismissAnimation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    if (tooltipBinding.getRoot().getVisibility() == View.VISIBLE) {
                        tooltipBinding.getRoot().setVisibility(View.GONE);
                    }
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            tooltipBinding.getRoot().startAnimation(tooltipDismissAnimation);
        }
    }

    public CoachMark show(){
        container.setVisibility(View.VISIBLE);
        animateTooltipShow();
        ViewCompat.animate(container)
                .alpha(1f)
                .setDuration(animDuration)
                .start();

        isShow = true;
        container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (dismissible) dismiss();
            }
        });
        return this;
    }

    public void dismiss(){
        dismiss(null);
    }

    public void dismiss(final Runnable afterDismiss) {
        if (onDismissListener !=null) onDismissListener.run();
        animateTooltipDismiss();
        ViewCompat.animate(container)
                .alpha(0f)
                .setDuration(animDuration)
                .setListener(new ViewPropertyAnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(View view) {
                        super.onAnimationEnd(view);
                        if (container.getAlpha()== 0f) {
                            container.setVisibility(View.GONE);
                            isShow = false;
                            if (afterDismiss!=null)afterDismiss.run();
                        }
                    }
                }).start();
    }

    public void destroy(){
        destroy(null);
    }

    public void destroy(final Runnable afterDestroy){
        if (onDismissListener !=null) onDismissListener.run();
        animateTooltipDismiss();
        ViewCompat.animate(container)
                .alpha(0f)
                .setDuration(animDuration)
                .setListener(new ViewPropertyAnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(View view) {
                        super.onAnimationEnd(view);
                        if (container.getAlpha()== 0f) {
                            ViewParent parent = view.getParent();
                            if (parent instanceof ViewGroup) {
                                ((ViewGroup) parent).removeView(view);
                            }
                            isShow = false;
                            if (afterDestroy != null) afterDestroy.run();
                        }
                    }
                }).start();
    }

    public boolean isShow(){
        return isShow;
    }

    public static class Builder{
        private Activity activity;
        private View target;
        private ArrayList<View> tooltipChilds;
        private int markerPadding;
        private OnClick onClickTarget;
        private boolean tooltipMatchWidth;
        private float backgroundAlpha;
        private boolean dismissible;
        private boolean isCircleMark;
        private int tooltipMargin;
        private int tooltipBackgroundColor;
        private int tooltipAlignment;
        private int pointerTooltipAlignment;
        private int radius;
        private Runnable onDismissListener;
        private Animation tooltipShowAnimation;
        private Animation tooltipDismissAnimation;

        public interface OnClick{
            void onClick(CoachMark coachMark);
        }

        /**
         * this constructor for initial default value
         * @param activity for parent view
         */
        public Builder(Activity activity) {
            this.activity = activity;
            this.tooltipChilds = new ArrayList<>();
            this.tooltipAlignment = CoachMark.ROOT_BOTTOM;
            this.pointerTooltipAlignment = CoachMark.POINTER_MIDDLE;
            this.tooltipMargin = 5;
            this.markerPadding = 0;
            this.radius = 5;
            this.backgroundAlpha = 0.5f;
            this.tooltipMatchWidth = true;
        }

        @Nullable
        private View findViewById(@IdRes int viewId) {
            View view;
            view = activity.findViewById(viewId);
            return view;
        }

        public Builder setTarget(View target){
            this.target = target;
            return this;
        }

        public Builder setTarget(@IdRes int itemViewId){
            this.target = findViewById(itemViewId);
            return this;
        }

        public Builder setCircleMark() {
            isCircleMark = true;
            return this;
        }

        public Builder setMarkerPadding(int dp) {
            this.markerPadding = dp;
            return this;
        }

        public Builder setOnClickTarget(OnClick onClickTarget){
            this.onClickTarget = onClickTarget;
            return this;
        }

        public Builder setDismissible(){
            this.dismissible = true;
            return this;
        }

        public Builder setTooltipAlignment(@TooltipAlignment int tooltipAlignment) {
            this.tooltipAlignment = tooltipAlignment;
            return this;
        }

        public Builder setTooltipPointer(@PointerTooltipAlignment int pointerTooltipAlignment) {
            this.pointerTooltipAlignment = pointerTooltipAlignment;
            return this;
        }


        public Builder setTooltipBackgroundColor(int colorResource) {
            this.tooltipBackgroundColor = colorResource;
            return this;
        }

        public Builder setTooltipMatchWidth(boolean matchWidthTooltip) {
            this.tooltipMatchWidth = matchWidthTooltip;
            return this;
        }

        public Builder setTooltipChilds(ArrayList<View> tooltipChilds) {
            this.tooltipChilds = tooltipChilds;
            return this;
        }

        public Builder setTooltipMargin(int dp) {
            this.tooltipMargin = dp;
            return this;
        }

        public Builder addTooltipChild(View tooltipChild) {
            if (this.tooltipChilds==null) {
                this.tooltipChilds = new ArrayList<>();
            }
            this.tooltipChilds.add(tooltipChild);
            return this;
        }

        public Builder setOnDismissListener(Runnable onDismiss) {
            this.onDismissListener = onDismiss;
            return this;
        }

        public Builder setTooltipShowAnimation(Animation tooltipShowAnimation) {
            this.tooltipShowAnimation = tooltipShowAnimation;
            return this;
        }

        public Builder setTooltipDismissAnimation(Animation tooltipDismissAnimation) {
            this.tooltipDismissAnimation = tooltipDismissAnimation;
            return this;
        }

        public Builder setBackgroundAlpha(float backgroundAlpha) {
            this.backgroundAlpha = backgroundAlpha;
            return this;
        }

        public Builder setRadius(int radius) {
            this.radius = radius;
            return this;
        }

        public CoachMark build(){
            final CoachMark coachMark = new CoachMark(this);
            coachMark.setTargetOnClick(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (onClickTarget!=null) {
                        onClickTarget.onClick(coachMark);
                    } else {
                        coachMark.destroy();
                    }
                }
            });
            return coachMark;
        }

        public CoachMark show(){
            return build().show();
        }
    }
}
