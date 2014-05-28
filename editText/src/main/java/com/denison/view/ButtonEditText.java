package com.denison.view;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;

/**
 * @author Yuri Denison
 * @since 28.05.2014
 */
public class ButtonEditText extends EditText {

    private Drawable drawableRight;
    private OnClickListener clickListener;
    private int clearButtonMode;

    private final OnClickListener DEFAULT_BUTTON_LISTENER = new OnClickListener() {
        @Override
        public void onClick(View v) {
            setText("");
            requestFocus();
        }
    };

    public ButtonEditText(Context context) {
        super(context);
        setDrawableClickListener(DEFAULT_BUTTON_LISTENER);
    }

    public ButtonEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        setDrawableClickListener(DEFAULT_BUTTON_LISTENER);
        clearButtonMode = attrs.getAttributeIntValue(R.styleable.ButtonEditText_clearButtonMode, 0);
    }

    public ButtonEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setDrawableClickListener(DEFAULT_BUTTON_LISTENER);
        clearButtonMode = attrs.getAttributeIntValue(R.styleable.ButtonEditText_clearButtonMode, 0);
    }

    @Override
    public void setCompoundDrawables(Drawable left, Drawable top, Drawable right, Drawable bottom) {
        if (right != null) {
            drawableRight = right;
        }
        super.setCompoundDrawables(left, top, right, bottom);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (drawableRight != null && clickListener != null && event.getAction() == MotionEvent.ACTION_DOWN) {
            int actionX = (int) event.getX();
            int actionY = (int) event.getY();

            int width = Math.max(drawableRight.getIntrinsicWidth(), this.getHeight());
            Rect bounds = new Rect(this.getWidth() - width, 0, this.getWidth(), this.getHeight());
            if (bounds.contains(actionX, actionY)) {
                clickListener.onClick(this);
                return true;
            } else {
                return super.onTouchEvent(event);
            }
        } else {
            return super.onTouchEvent(event);
        }
    }

    public void setDrawableClickListener(OnClickListener listener) {
        this.clickListener = listener;
    }
}
