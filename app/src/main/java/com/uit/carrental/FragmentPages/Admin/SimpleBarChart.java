package com.uit.carrental.FragmentPages.Admin;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import androidx.annotation.Nullable;

public class SimpleBarChart extends View {
    private final Paint paint;
    private float[] values = {800, 2000, 600, 1200, 400, 3000, 100};

    public SimpleBarChart(Context context) {
        super(context);
        paint = new Paint();
        paint.setAntiAlias(true);
    }

    public SimpleBarChart(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        paint = new Paint();
        paint.setAntiAlias(true);
    }

    public SimpleBarChart(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        paint = new Paint();
        paint.setAntiAlias(true);
    }

    public void setValues(float[] newValues) {
        this.values = newValues;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float width = getWidth();
        float height = getHeight();
        float barWidth = width / 10f;
        float maxHeight = height * 0.8f;

        int[] colors = {Color.parseColor("#3DD34C"), Color.parseColor("#6D62F7")};

        for (int i = 0; i < values.length; i++) {
            paint.setColor(colors[i % 2]);
            float left = i * barWidth * 1.2f + barWidth * 0.1f;
            float top = height - (values[i] / 3000 * maxHeight);
            float right = left + barWidth;
            float bottom = height;
            canvas.drawRect(left, top, right, bottom, paint);
        }
    }
}