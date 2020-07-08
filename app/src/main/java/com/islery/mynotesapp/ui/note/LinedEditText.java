package com.islery.mynotesapp.ui.note;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;

import androidx.appcompat.widget.AppCompatEditText;

import com.islery.mynotesapp.R;

public class LinedEditText extends AppCompatEditText {
    private static final String TAG = "LineEditText";
    private Rect rect;
    private Paint paint;


    public LinedEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        rect = new Rect();
        paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(2);
        paint.setColor(getResources().getColor(R.color.colorPrimaryDark));
    }

    public LinedEditText(Context context) {
        super(context);
        rect = new Rect();
        paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(2);
        paint.setColor(getResources().getColor(R.color.colorPrimaryDark));
    }

    public LinedEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        rect = new Rect();
        paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(2);
        paint.setColor(getResources().getColor(R.color.colorPrimaryDark));
    }

    @Override
    protected void onDraw(Canvas canvas) {
//        int height = ((View)this.getParent()).getHeight();
        int height = getHeight();

        int lineHeight = getLineHeight();
        int numberOfLines = height / lineHeight;
        Log.d(TAG, "onDraw: number of lines "+ numberOfLines);

        if (getLineCount() > numberOfLines) {
            numberOfLines = getLineCount();
        }

        Rect r = rect;
        Paint pt = paint;

        int baseline = getLineBounds(0,r);

        for (int i = 0; i < numberOfLines; i++) {
            canvas.drawLine(r.left,baseline+2,r.right,baseline+2,pt);
            baseline += lineHeight;
        }
        super.onDraw(canvas);
    }
}
