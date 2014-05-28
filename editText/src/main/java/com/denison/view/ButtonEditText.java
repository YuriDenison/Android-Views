package com.denison.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;

/**
 * @author Yuri Denison
 * @since 28.05.2014
 */
public class ButtonEditText extends EditText {
    private final OnClickListener DEFAULT_BUTTON_LISTENER = new OnClickListener() {
        @Override
        public void onClick(View v) {
            setText("");
            requestFocus();
        }
    };
    private Drawable mDrawableRight;
    private OnClickListener mClickListener;
    private ClearButtonMode mButtonMode;

    public ButtonEditText(Context context) {
        super(context);
        setDrawableClickListener(DEFAULT_BUTTON_LISTENER);
    }

    public ButtonEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        setDrawableClickListener(DEFAULT_BUTTON_LISTENER);
        init(attrs);
    }

    public ButtonEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setDrawableClickListener(DEFAULT_BUTTON_LISTENER);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        final TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.ButtonEditText);
        mButtonMode = ClearButtonMode.fromInt(
                a.getInt(R.styleable.ButtonEditText_clearButtonMode,
                        ClearButtonMode.FOCUSED_IF_NOT_EMPTY.getId())
        );

        addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (mButtonMode == ClearButtonMode.FOCUSED_IF_NOT_EMPTY) {
                    setCompoundDrawables(null, null, !TextUtils.isEmpty(s.toString()) ? mDrawableRight : null, null);
                }
            }
        });
    }

    @Override
    public void setCompoundDrawables(Drawable left, Drawable top, Drawable right, Drawable bottom) {
        if (right != null && mDrawableRight == null) {
            right.setBounds(0, 0, right.getIntrinsicWidth(), right.getIntrinsicHeight());
            mDrawableRight = right;

            if (mButtonMode != ClearButtonMode.ALWAYS) {
                right = null;
            }
        }
        super.setCompoundDrawables(left, top, right, bottom);
    }

    @Override
    protected void onFocusChanged(boolean focused, int direction, Rect previouslyFocusedRect) {
        super.onFocusChanged(focused, direction, previouslyFocusedRect);
        if (mDrawableRight != null) {
            // Due to inability to control compound drawables visibility we should update container
            switch (mButtonMode) {
                case ALWAYS:
                    setCompoundDrawables(null, null, mDrawableRight, null);
                    break;
                case FOCUSED_IF_NOT_EMPTY:
                    setCompoundDrawables(null, null, focused && !TextUtils.isEmpty(getText().toString()) ? mDrawableRight : null, null);
                    break;
                case FOCUSED:
                    setCompoundDrawables(null, null, focused ? mDrawableRight : null, null);
                    break;
                default:
                    setCompoundDrawables(null, null, null, null);
                    break;
            }
        }
    }

    @Override
    protected void finalize() throws Throwable {
        mDrawableRight = null;
        super.finalize();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mDrawableRight != null && mClickListener != null && event.getAction() == MotionEvent.ACTION_DOWN) {
            int actionX = (int) event.getX();
            int actionY = (int) event.getY();

            int width = Math.max(mDrawableRight.getIntrinsicWidth(), this.getHeight());
            Rect bounds = new Rect(this.getWidth() - width, 0, this.getWidth(), this.getHeight());
            if (bounds.contains(actionX, actionY)) {
                mClickListener.onClick(this);
                return true;
            }
        }
        return super.onTouchEvent(event);
    }

    public void setButtonVisibilityMode(ClearButtonMode mode) {
        this.mButtonMode = mode;
        invalidate();
    }

    public void setDrawableClickListener(OnClickListener listener) {
        this.mClickListener = listener;
    }

    public enum ClearButtonMode {
        ALWAYS(0), FOCUSED_IF_NOT_EMPTY(1), FOCUSED(2), NEVER(3);

        private int id;

        ClearButtonMode(int id) {
            this.id = id;
        }

        public static ClearButtonMode fromInt(int value) {
            for (ClearButtonMode mode : values()) {
                if (mode.getId() == value) {
                    return mode;
                }
            }
            return NEVER;
        }

        public int getId() {
            return id;
        }

    }
}
