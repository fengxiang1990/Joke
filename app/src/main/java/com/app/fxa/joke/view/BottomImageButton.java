package com.app.fxa.joke.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.app.fxa.joke.R;


public class BottomImageButton extends LinearLayout {

    private ImageView imageView;

    private TextView textView;

    public BottomImageButton(Context context) {
        super(context);
    }

    public void init(Context context, String title, int imgId) {
        this.setOrientation(VERTICAL);
        textView = new TextView(context);
        textView.setText(title);
        imageView = new ImageView(context);
        imageView.setBackgroundResource(imgId);
        addView(imageView, new LayoutParams(
                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        LayoutParams layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        layoutParams.gravity = Gravity.CENTER_HORIZONTAL;
        addView(textView, layoutParams);
    }

    public BottomImageButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.setOrientation(VERTICAL);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.BottomImageButton);

        textView = new TextView(context);
        String text = typedArray.getString(R.styleable.BottomImageButton_text);
        int textcolor = typedArray.getColor(R.styleable.BottomImageButton_textColor, Color.BLACK);
        float textSize = typedArray.getDimension(R.styleable.BottomImageButton_textSize, 15);
        textView.setText(text);
        textView.setTextSize(textSize);
        textView.setTextColor(textcolor);

        imageView = new ImageView(context);
        int backgroudResId = typedArray.getResourceId(R.styleable.BottomImageButton_imageBackgroundResId, 0);
        int imgResId = typedArray.getResourceId(R.styleable.BottomImageButton_imageResId, 0);
        if (imgResId != 0) {
            imageView.setBackgroundResource(imgResId);
        } else if (backgroudResId != 0) {
            imageView.setBackgroundResource(backgroudResId);
        }

        LayoutParams layoutParams1 = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        LayoutParams layoutParams2 = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

        layoutParams1.gravity = Gravity.CENTER;
        layoutParams2.gravity = Gravity.CENTER;
        layoutParams1.topMargin = 5;
        layoutParams2.leftMargin = 30;
        layoutParams2.rightMargin = 30;
        addView(imageView, layoutParams1);
        addView(textView, layoutParams2);
    }

    public ImageView getImageView() {
        return imageView;
    }

    public void setImageView(ImageView imageView) {
        this.imageView = imageView;
    }

    public TextView getTextView() {
        return textView;
    }

    public void setTextView(TextView textView) {
        this.textView = textView;
    }

}
