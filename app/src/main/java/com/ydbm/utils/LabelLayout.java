package com.ydbm.utils;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.FrameLayout;

import com.ydbm.R;

public class LabelLayout  extends FrameLayout {
    private int mLabelDistance;
    private int mLabelHeight;
    private Drawable mLabelBackground;
    private Gravity mLabelGravity;

    private String mLabelText;
    private int mLabelTextSize;
    private int mLabelTextColor;
    private TextDirection mLabelTextDirection;

    private final Paint mTextPaint;

    public LabelLayout(Context context) {
        this(context, null);
    }

    public LabelLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LabelLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        // Enable 'onDraw()'
        setWillNotDraw(false);

        TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.LabelLayout, defStyleAttr, 0);
        mLabelDistance = typedArray.getDimensionPixelSize(R.styleable.LabelLayout_labelDistance, 0);
        mLabelHeight = typedArray.getDimensionPixelSize(R.styleable.LabelLayout_labelHeight, 0);
        mLabelBackground = typedArray.getDrawable(R.styleable.LabelLayout_labelBackground);
        mLabelGravity = Gravity.values()[typedArray.getInt(R.styleable.LabelLayout_labelGravity, Gravity.TOP_LEFT.ordinal())];
        mLabelText = typedArray.getString(R.styleable.LabelLayout_labelText);
        mLabelTextSize = typedArray.getDimensionPixelSize(R.styleable.LabelLayout_labelTextSize, (int) new Paint().getTextSize());
        mLabelTextColor = typedArray.getColor(R.styleable.LabelLayout_labelTextColor, Color.BLACK);
        mLabelTextDirection = TextDirection.values()[typedArray.getInt(R.styleable.LabelLayout_labelTextDirection, TextDirection.LEFT_TO_RIGHT.ordinal())];
        typedArray.recycle();

        // Setup text paint
        mTextPaint = new Paint();
        mTextPaint.setDither(true);
        mTextPaint.setAntiAlias(true);
        mTextPaint.setStrokeJoin(Paint.Join.ROUND);
        mTextPaint.setStrokeCap(Paint.Cap.SQUARE);
    }

    @Override
    public void onDrawForeground(Canvas canvas) {
        super.onDrawForeground(canvas);

        drawSelf(canvas);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);

        drawSelf(canvas);
    }




    public int getLabelDistance() {
        return mLabelDistance;
    }




    public void setLabelDistance(int labelDistance) {
        mLabelDistance = labelDistance;
        invalidate();
    }




    public int getLabelHeight() {
        return mLabelHeight;
    }




    public void setLabelHeight(int labelHeight) {
        mLabelHeight = labelHeight;
        invalidate();
    }




    public Drawable getLabelBackground() {
        return mLabelBackground;
    }



    public void setLabelBackground(Drawable labelBackground) {
        mLabelBackground = labelBackground;
        invalidate();
    }


    public Gravity getLabelGravity() {
        return mLabelGravity;
    }




    public void setLabelGravity(Gravity labelGravity) {
        mLabelGravity = labelGravity;
        invalidate();
    }



    public String getLabelText() {
        return mLabelText;
    }



    public void setLabelText(String labelText) {
        mLabelText = labelText;
        invalidate();
    }


    public int getLabelTextSize() {
        return mLabelTextSize;
    }




    public void setLabelTextSize(int labelTextSize) {
        mLabelTextSize = labelTextSize;
        invalidate();
    }



    public int getLabelTextColor() {
        return mLabelTextColor;
    }



    public void setLabelTextColor(int labelTextColor) {
        mLabelTextColor = labelTextColor;
        invalidate();
    }




    public TextDirection getLabelTextDirection() {
        return mLabelTextDirection;
    }



    public void setLabelTextDirection(TextDirection textDirection) {
        mLabelTextDirection = textDirection;
        invalidate();
    }

    // Calculate the absolute position of point intersecting between canvas edge and bisector
    private int calculateBisectorIntersectAbsolutePosition(int distance, int height) {
        return (int) (Math.sqrt(2) * (distance + (height / 2)));
    }

    // Calculate the starting and ending points coordinates of bisector line
    private int[] calculateBisectorCoordinates(int distance, int height, Gravity gravity) {
        final int bisectorIntersectAbsolutePosition = calculateBisectorIntersectAbsolutePosition(distance, height);
        int[] results = new int[4];

        int bisectorStartX;
        int bisectorStartY;
        int bisectorEndX;
        int bisectorEndY;
        switch (gravity) {
            case TOP_RIGHT:
                bisectorStartY = 0;
                bisectorStartX = getMeasuredWidth() - bisectorIntersectAbsolutePosition;
                bisectorEndX = getMeasuredWidth();
                bisectorEndY = bisectorIntersectAbsolutePosition;
                break;

            case BOTTOM_RIGHT:
                bisectorStartX = getMeasuredWidth() - bisectorIntersectAbsolutePosition;
                bisectorStartY = getMeasuredHeight();
                bisectorEndX = getMeasuredWidth();
                bisectorEndY = getMeasuredHeight() - bisectorIntersectAbsolutePosition;
                break;

            case BOTTOM_LEFT:
                bisectorStartX = 0;
                bisectorStartY = getMeasuredHeight() - bisectorIntersectAbsolutePosition;
                bisectorEndX = bisectorIntersectAbsolutePosition;
                bisectorEndY = getMeasuredHeight();
                break;

            default:
                bisectorStartX = 0;
                bisectorStartY = bisectorIntersectAbsolutePosition;
                bisectorEndX = bisectorIntersectAbsolutePosition;
                bisectorEndY = 0;
                break;
        }

        results[0] = bisectorStartX;
        results[1] = bisectorStartY;
        results[2] = bisectorEndX;
        results[3] = bisectorEndY;

        return results;
    }

    // Calculate text horizontal and vertical offset
    private float[] calculateTextOffsets(String text, Paint paint, int distance, int height, Gravity gravity) {
        float[] offsets = new float[2];

        Rect textBounds = new Rect();
        paint.getTextBounds(text, 0, text.length(), textBounds);

        float hOffset = (float) (calculateBisectorIntersectAbsolutePosition(distance, height) / Math.sqrt(2) - textBounds.width() / 2.0);
        float vOffset;
        if (distance >= height) {
            vOffset = (textBounds.height() * 0.5f);
        } else {
            if (gravity.equals(Gravity.TOP_LEFT) || gravity.equals(Gravity.TOP_RIGHT)) {
                vOffset = (textBounds.height() * (0.5f + (height - distance) / (float) height * 0.5f));
            } else {
                vOffset = (textBounds.height() * (0.5f - (height - distance) / (float) height * 0.5f));
            }
        }

        Log.d(LabelLayout.class.getSimpleName(), String.format("%d, %d, %f", distance, height, vOffset));

        offsets[0] = hOffset;
        offsets[1] = vOffset;

        return offsets;
    }

    private int[] calculateCenterCoordinate(int distance, int height, Gravity gravity) {
        int[] results = new int[2];
        int x;
        int y;

        int centerAbsolutePosition = calculateCenterAbsolutePosition(distance, height);
        switch (gravity) {
            default:
                x = centerAbsolutePosition;
                y = centerAbsolutePosition;
                break;

            case TOP_RIGHT:
                x = getMeasuredWidth() - centerAbsolutePosition;
                y = centerAbsolutePosition;
                break;

            case BOTTOM_RIGHT:
                x = getMeasuredWidth() - centerAbsolutePosition;
                y = getMeasuredHeight() - centerAbsolutePosition;
                break;

            case BOTTOM_LEFT:
                x = centerAbsolutePosition;
                y = getMeasuredHeight() - centerAbsolutePosition;
                break;

        }

        results[0] = x;
        results[1] = y;
        return results;
    }

    private int calculateCenterAbsolutePosition(int distance, int height) {
        return (int) ((distance + height / 2) / Math.sqrt(2));
    }

    private int calculateWidth(int distance, int height) {
        return 2 * (distance + height);
    }

    private Rect calculateBackgroundBounds(Drawable drawable, Rect labelRect) {
        Rect rect;

        if (drawable instanceof ColorDrawable) {
            rect = new Rect(labelRect);
        } else {
            rect = new Rect();

            if (drawable.getIntrinsicWidth() <= labelRect.width() && drawable.getIntrinsicHeight() <= labelRect.height()) {
                // No need to scale
                rect.left = labelRect.centerX() - drawable.getIntrinsicWidth() / 2;
                rect.top = labelRect.centerY() - drawable.getIntrinsicHeight() / 2;
                rect.right = labelRect.centerX() + drawable.getIntrinsicWidth() / 2;
                rect.bottom = labelRect.centerY() + drawable.getIntrinsicHeight() / 2;
            } else {
                // Need to scale
                int width;
                int height;
                int ratio = drawable.getIntrinsicWidth() / drawable.getIntrinsicHeight();
                if (drawable.getIntrinsicWidth() / drawable.getIntrinsicHeight() >= labelRect.width() / labelRect.height()) {
                    // Scale to fill width
                    width = labelRect.width();
                    height = labelRect.width() / ratio;
                } else {
                    // Scale to fill height
                    width = labelRect.height() * ratio;
                    height = labelRect.height();
                }

                rect.left = labelRect.centerX() - width / 2;
                rect.top = labelRect.centerY() - height / 2;
                rect.right = labelRect.centerX() + width / 2;
                rect.bottom = labelRect.centerY() + height / 2;
            }
        }

        return rect;
    }

    private float calculateRotateDegree(Gravity gravity) {
        float degree;

        switch (gravity) {
            case BOTTOM_RIGHT:
            default:
                degree = -45;
                break;

            case TOP_RIGHT:
            case BOTTOM_LEFT:
                degree = 45;
                break;
        }

        return degree;
    }

    private void drawSelf(Canvas canvas) {
        // Draw background
        int[] centerCoordinate = calculateCenterCoordinate(mLabelDistance, mLabelHeight, mLabelGravity);
        int labelHalfWidth = calculateWidth(mLabelDistance, mLabelHeight) / 2;
        int labelHalfHeight = mLabelHeight / 2;
        Rect labelRect = new Rect(centerCoordinate[0] - labelHalfWidth, centerCoordinate[1] - labelHalfHeight, centerCoordinate[0] + labelHalfWidth, centerCoordinate[1] + labelHalfHeight);
        mLabelBackground.setBounds(calculateBackgroundBounds(mLabelBackground, labelRect));

        canvas.save();
        canvas.rotate(calculateRotateDegree(mLabelGravity), centerCoordinate[0], centerCoordinate[1]);
        mLabelBackground.draw(canvas);
        canvas.restore();

        // Draw text
        Path bisectorPath = new Path();
        int[] bisectorCoordinates = calculateBisectorCoordinates(mLabelDistance, mLabelHeight, mLabelGravity);
        bisectorPath.moveTo(bisectorCoordinates[0], bisectorCoordinates[1]);
        bisectorPath.lineTo(bisectorCoordinates[2], bisectorCoordinates[3]);

        mTextPaint.setTextSize(mLabelTextSize);
        mTextPaint.setColor(mLabelTextColor);
        float[] offsets = calculateTextOffsets(mLabelText, mTextPaint, mLabelDistance, mLabelHeight, mLabelGravity);
        String displayText;
        if (mLabelTextDirection == TextDirection.LEFT_TO_RIGHT) {
            displayText = mLabelText;
        } else {
            displayText = new StringBuffer(mLabelText).reverse().toString();
        }
        canvas.drawTextOnPath(displayText, bisectorPath, offsets[0], offsets[1], mTextPaint);
    }

    public enum Gravity {
        TOP_LEFT, TOP_RIGHT, BOTTOM_RIGHT, BOTTOM_LEFT
    }

    public enum TextDirection {
        LEFT_TO_RIGHT, RIGHT_TO_LEFT
    }
}
