package com.example.android.notekeeper;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;

/**
 * TODO: document your custom view class.
 */
public class ModuleStatusView extends View {
    private static final int EDIT_MODE_MODULE_COUNT = 7;
    private String mExampleString; // TODO: use a default from R.string...
    private int mExampleColor = Color.RED; // TODO: use a default from R.color...
    private float mExampleDimension = 0; // TODO: use a default from R.dimen...
    private Drawable mExampleDrawable;
    private float mShapSpacing;
    private float mShapeSize;
    private float mOutlineWidth;
    private int mOutlineColor;
    private Paint mPaintOutline;
    private Paint mPaintFill;
    private Rect[] mModuleRectangles;
    private float mRadius;
    private int mMaxHorizontalModules;


    public boolean[] getModuleStatus() {
        return mModuleStatus;
    }

    public void setModuleStatus(boolean[] moduleStatus) {
        mModuleStatus = moduleStatus;
    }

    private boolean[] mModuleStatus;

    public ModuleStatusView(Context context) {
        super(context);
        init(null, 0);
    }

    public ModuleStatusView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public ModuleStatusView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {
        // Load attributes
        if(isInEditMode()){
            setupEditMOdeValues();
        }
        final TypedArray a = getContext().obtainStyledAttributes(
                attrs, R.styleable.ModuleStatusView, defStyle, 0);

        a.recycle();

        mOutlineWidth = 6f;
        mShapeSize = 144f;
        mShapSpacing = 30f;

        mRadius = (mShapeSize - mOutlineWidth)/2;

        setupModuleRectangles();

        mOutlineColor = Color.BLACK;
        mPaintOutline = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintOutline.setStyle(Paint.Style.STROKE);
        mPaintOutline.setStrokeWidth(mOutlineWidth);
        mPaintOutline.setColor(mOutlineColor);

        mPaintFill = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintFill.setStyle(Paint.Style.FILL);
        mPaintFill.setColor(getContext().getResources().getColor(R.color.colorAccent));

     }

    private void setupEditMOdeValues() {
        boolean[] exampleModuleValues = new boolean[EDIT_MODE_MODULE_COUNT];
        int middle = EDIT_MODE_MODULE_COUNT /2 ;
        for(int moduleIndex = 0 ;  moduleIndex< middle ; moduleIndex ++){
            exampleModuleValues[moduleIndex] = true;
        }
        setModuleStatus(exampleModuleValues);
    }

    private void setupModuleRectangles() {
        mModuleRectangles = new Rect[mModuleStatus.length];

        for(int moduleIndex = 0 ; moduleIndex < mModuleStatus.length ; moduleIndex ++){
            int x =  getPaddingLeft() + (int)( moduleIndex * (mShapeSize + mShapSpacing));
            int y = getPaddingTop();

            mModuleRectangles[moduleIndex] = new Rect(x, y ,
                    x +(int) mShapeSize , y + (int) mShapeSize );

        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int desiredWidth = 0;
        int desiredHeight = 0;

        int specWidth = MeasureSpec.getSize(widthMeasureSpec);

        int availableWidth = specWidth - getPaddingLeft()  - getPaddingRight();
        int horizontalModulesThatCanFit = (int) (mModuleStatus.length / (mShapSpacing - mShapeSize));

        mMaxHorizontalModules = Math.min(horizontalModulesThatCanFit , mModuleStatus.length);

        desiredWidth = (int) ((mMaxHorizontalModules * (mShapeSize + mShapSpacing) - mShapSpacing));
        desiredWidth += getPaddingLeft() + getPaddingRight();


        int row = ((mModuleStatus.length -1 )/ mMaxHorizontalModules) +1;

        desiredHeight = (int) ((row * (mShapeSize  + mShapSpacing)) - mShapSpacing);
        desiredHeight = desiredHeight + getPaddingTop() + getPaddingBottom();

        int width = resolveSizeAndState(desiredWidth , widthMeasureSpec , 0);
        int height = resolveSizeAndState(desiredHeight , heightMeasureSpec , 0);

        setMeasuredDimension(width , height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        for(int moduleIndex = 0 ; moduleIndex < mModuleStatus.length ; moduleIndex ++){
            float x = mModuleRectangles[moduleIndex].centerX();
            float y = mModuleRectangles[moduleIndex].centerY();

            if(mModuleStatus[moduleIndex]){
            canvas.drawCircle(x , y , mRadius , mPaintFill);
            }

            canvas.drawCircle(x,y,mRadius , mPaintOutline);
        }
    }

    /**
     * Gets the example string attribute value.
     *
     * @return The example string attribute value.
     */
    public String getExampleString() {
        return mExampleString;
    }

    /**
     * Sets the view's example string attribute value. In the example view, this string
     * is the text to draw.
     *
     * @param exampleString The example string attribute value to use.
     */
    public void setExampleString(String exampleString) {
        mExampleString = exampleString;

    }

    /**
     * Gets the example color attribute value.
     *
     * @return The example color attribute value.
     */
    public int getExampleColor() {
        return mExampleColor;
    }

    /**
     * Sets the view's example color attribute value. In the example view, this color
     * is the font color.
     *
     * @param exampleColor The example color attribute value to use.
     */
    public void setExampleColor(int exampleColor) {
        mExampleColor = exampleColor;

    }

    /**
     * Gets the example dimension attribute value.
     *
     * @return The example dimension attribute value.
     */
    public float getExampleDimension() {
        return mExampleDimension;
    }

    /**
     * Sets the view's example dimension attribute value. In the example view, this dimension
     * is the font size.
     *
     * @param exampleDimension The example dimension attribute value to use.
     */
    public void setExampleDimension(float exampleDimension) {
        mExampleDimension = exampleDimension;

    }

    /**
     * Gets the example drawable attribute value.
     *
     * @return The example drawable attribute value.
     */
    public Drawable getExampleDrawable() {
        return mExampleDrawable;
    }

    /**
     * Sets the view's example drawable attribute value. In the example view, this drawable is
     * drawn above the text.
     *
     * @param exampleDrawable The example drawable attribute value to use.
     */
    public void setExampleDrawable(Drawable exampleDrawable) {
        mExampleDrawable = exampleDrawable;
    }
}
