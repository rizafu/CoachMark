package io.github.rizafu.coachmark;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by RizaFu on 11/7/16.
 */

public class CoachMarkOverlay extends View {

    private Paint paint;
    private RectF rectF;
    private int radius;
    private int x;
    private int y;

    private boolean isCircle;

    public CoachMarkOverlay(Context context) {
        super(context);
        init();
    }

    public CoachMarkOverlay(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CoachMarkOverlay(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init(){
        setDrawingCacheEnabled(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }
    }

    public void addRect(int x, int y, int width, int height, int radius,int padding, boolean isCircle){
        this.isCircle = isCircle;
        this.radius = radius + padding;
        this.x = x;
        this.y = y;
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(Color.TRANSPARENT);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));

        int r = x + width;
        int b = y + height;

        rectF = new RectF(x - padding, y - padding, r + padding, b + padding);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (rectF!=null){
            if (isCircle){
                canvas.drawCircle(x,y,radius,paint);
            } else {
                canvas.drawRoundRect(rectF, radius, radius, paint);
            }
        }
    }
}
