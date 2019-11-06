package com.paperflow.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.media.Image;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.paperflow.R;

/** * Created by Mehul Shah on 6/13/2016. */public class CircularProgressbar extends View {
    Paint paint = new Paint();
    Paint textPaint = new Paint();
    float center_x, center_y;
    float width ;
    float height,rotateAngle=0 ;
    float radius,startAngle=0,sweepAngle=360,angleIncrement=0;
    private String mProgressText="";

    private int one=0,two =0,three=0,fourth=0;


    public CircularProgressbar(Context context) {
        super(context);
        init();
    }
    public CircularProgressbar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CircularProgressbar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public void setValues(int first,int sec,int third,int fourth,String text){
        this.one=first;
        this.two = sec;
        this.three = third;
        this.fourth = fourth;
        this.mProgressText = text;

        invalidate();
    }

    private void init(){
        paint.setAntiAlias(true);
        paint.setStrokeWidth(15);
        paint.setStyle(Paint.Style.FILL);

        textPaint.setAntiAlias(true);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        rotateAngle+= angleIncrement;
        width = (float)getWidth();
        height = (float)getHeight();
        center_x = width/2;
        center_y = height/2;

        if (width > height){
            radius = height/3;

        }else{
            radius = width/3;

        }

        final RectF oval = new RectF();
        paint.setStyle(Paint.Style.STROKE);
        center_x = width/2;
        center_y = height/2;
        oval.set(center_x - radius, center_y - radius, center_x + radius, center_y + radius);


//            sweepAngle += 6;//4
//            angleIncrement = 8;//5
            paint.setColor(getResources().getColor(R.color.selected_button_bg));
            canvas.drawArc(oval, startAngle, sweepAngle, false, paint);
            if(sweepAngle > one){
                paint.setColor(getResources().getColor(R.color.red));
                canvas.drawArc(oval, startAngle+one, (sweepAngle -one), false, paint);

            }
            if(sweepAngle > (one+two)){
                paint.setColor(getResources().getColor(R.color.yellow));
                canvas.drawArc(oval, startAngle +  (one+two), (sweepAngle -(one+two)), false, paint);

            }
            if(sweepAngle > (one+two+three)){
                paint.setColor(getResources().getColor(R.color.green));
                canvas.drawArc(oval, startAngle + (one+two+three), (sweepAngle - (one+two+three)), false, paint);

            }

       // textPaint.setTextSize(getResources().getDimension(R.dimen.sp_14));
        textPaint.setColor(getResources().getColor(R.color.title_color));
        textPaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
            textPaint.setTextSize(getWidth() /6);
            drawCenter(canvas,textPaint,mProgressText);



    }
    private Rect r = new Rect();

    private void drawCenter(Canvas canvas, Paint paint, String text) {
        canvas.getClipBounds(r);
        int cHeight = r.height();
        int cWidth = r.width();
        paint.setTextAlign(Paint.Align.LEFT);
        paint.getTextBounds(text, 0, text.length(), r);
        float x = cWidth / 2f - r.width() / 2f - r.left;
        float y = cHeight / 2f + r.height() / 2f - r.bottom;
        canvas.drawText(text, x, y, paint);
    }





}