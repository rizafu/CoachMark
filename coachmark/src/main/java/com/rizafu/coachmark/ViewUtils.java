package com.rizafu.coachmark;

import android.content.res.Resources;
import android.view.View;
import android.view.ViewTreeObserver;

/**
 * Created by RizaFu on 11/12/16.
 */

class ViewUtils {

    public static int pxToDp(int px) {
        return (int) (px / Resources.getSystem().getDisplayMetrics().density);
    }

    static int dpToPx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

    public static void doAfterLayout(final View view, final Runnable runnable) {
        ViewTreeObserver.OnGlobalLayoutListener listener = new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                ViewTreeObserver viewTreeObserver = view.getViewTreeObserver();
                viewTreeObserver.removeOnGlobalLayoutListener(this);
                runnable.run();
            }
        };
        view.getViewTreeObserver().addOnGlobalLayoutListener(listener);
    }
}
