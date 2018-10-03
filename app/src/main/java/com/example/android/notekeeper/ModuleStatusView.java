package com.example.android.notekeeper;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat;
import android.support.v4.widget.ExploreByTouchHelper;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;

import java.util.List;

/**
 * TODO: document your custom view class.
 */
public class ModuleStatusView extends View {
    private static final int EDIT_MODE_MODULE_COUNT = 7;
    private static final int INVALID_MODULE_INDEX = -1;
    private static final int MODULE_CIRCLE = 0;
    private static final float DEFAULT_OUTLINE_WIDTH_DP = 2f;
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
    private int mShape;
    private ModuleStatusAccessiblityHelper mAccessiblityHelper;


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
        setFocusable(true);
        mAccessiblityHelper = new ModuleStatusAccessiblityHelper(this);
        ViewCompat.setAccessibilityDelegate(this , mAccessiblityHelper);

        DisplayMetrics dm = getContext().getResources().getDisplayMetrics();
        float displayDensity = dm.density;
        float defaultOutlineWidth = displayDensity * DEFAULT_OUTLINE_WIDTH_DP;

        final TypedArray a = getContext().obtainStyledAttributes(
                attrs, R.styleable.ModuleStatusView, defStyle, 0);

        mOutlineColor = a.getColor(R.styleable.ModuleStatusView_outlineColor , Color.BLACK);
        mShape = a.getInt(R.styleable.ModuleStatusView_shape , MODULE_CIRCLE);
        mOutlineWidth = a.getDimension(R.styleable.ModuleStatusView_outlineWidth , defaultOutlineWidth);

        a.recycle();


        mShapeSize = displayDensity * 144f/3;
        mShapSpacing = displayDensity * 10f;

        mRadius = (mShapeSize - mOutlineWidth)/2;

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

    private void setupModuleRectangles(int width) {

        int availableWidth = width - getPaddingLeft() - getPaddingRight();
        int horizontalModulesThatCanFit = (int) (availableWidth / (mShapSpacing + mShapeSize));

        int maxHorizontalModules = Math.min(horizontalModulesThatCanFit , mModuleStatus.length);

        mModuleRectangles = new Rect[mModuleStatus.length];

        for(int moduleIndex = 0 ; moduleIndex < mModuleRectangles.length ; moduleIndex ++){
            int column = moduleIndex % maxHorizontalModules ;

            int row = moduleIndex / maxHorizontalModules;


            int x =  getPaddingLeft() + (int)( column * (mShapeSize + mShapSpacing));
            int y = getPaddingTop() + (int)( row * (mShapeSize + mShapSpacing));

            mModuleRectangles[moduleIndex] = new Rect(x, y ,
                    x +(int) mShapeSize , y + (int) mShapeSize );

        }
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()){

            case MotionEvent.ACTION_DOWN:
                return true;

            case MotionEvent.ACTION_UP:
                int moduleIndex = findItemAtPoint(event.getX() , event.getY());
                onModuleSelected(moduleIndex);
                return true;

        }

        return super.onTouchEvent(event);
    }

    private void onModuleSelected(int moduleIndex) {
        if(moduleIndex == INVALID_MODULE_INDEX){return;}

        mModuleStatus[moduleIndex] = ! mModuleStatus[moduleIndex];
        invalidate();
    }

    private int findItemAtPoint(float x, float y) {
        int moduleIndex = INVALID_MODULE_INDEX;

        for(int i = 0 ; i < mModuleRectangles.length ; i++){
            if(mModuleRectangles[i].contains((int)x ,(int)y)){
                return i;
            }
        }
        return moduleIndex;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        setupModuleRectangles(w);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int desiredWidth = 0;
        int desiredHeight = 0;

        int specWidth = MeasureSpec.getSize(widthMeasureSpec);

        int availableWidth = specWidth - getPaddingLeft()  - getPaddingRight();
        int horizontalModulesThatCanFit = (int) (availableWidth / (mShapSpacing + mShapeSize));

        mMaxHorizontalModules = Math.min(horizontalModulesThatCanFit , mModuleStatus.length);

        desiredWidth = (int) ((mMaxHorizontalModules * (mShapeSize + mShapSpacing)) - mShapSpacing);
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

            if(mShape == MODULE_CIRCLE) {
                if (mModuleStatus[moduleIndex]) {
                    canvas.drawCircle(x, y, mRadius, mPaintFill);
                }

                canvas.drawCircle(x, y, mRadius, mPaintOutline);
            }
            else{
                drawSquar(canvas , moduleIndex);
            }
        }
    }

    private void drawSquar(Canvas canvas, int moduleIndex) {
        Rect moduleRect = mModuleRectangles[moduleIndex];

        if(mModuleStatus[moduleIndex]){
            canvas.drawRect(moduleRect , mPaintFill);
        }

        canvas.drawRect(moduleRect.left + mOutlineWidth/2 ,
                moduleRect.top + mOutlineWidth/2 ,
                moduleRect.right + mOutlineWidth/2 ,
                moduleRect.bottom + mOutlineWidth/2 ,mPaintOutline);

    }

    @Override
    protected void onFocusChanged(boolean gainFocus, int direction, @Nullable Rect previouslyFocusedRect) {
        super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
        mAccessiblityHelper.onFocusChanged(gainFocus , direction , previouslyFocusedRect);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        return mAccessiblityHelper.dispatchKeyEvent(event) || super.dispatchKeyEvent(event);
    }

    @Override
    protected boolean dispatchHoverEvent(MotionEvent event) {
        return mAccessiblityHelper.dispatchHoverEvent(event) || super.dispatchHoverEvent(event);
    }

    private class ModuleStatusAccessiblityHelper extends ExploreByTouchHelper{
       /**
        * Constructs a new helper that can expose a virtual view hierarchy for the
        * specified host view.
        *
        * @param host view whose virtual view hierarchy is exposed by this helper
        */
       public ModuleStatusAccessiblityHelper(@NonNull View host) {
           super(host);
       }

       @Override
       protected int getVirtualViewAt(float x, float y) {
           int moduleIndex = findItemAtPoint(x ,y);
           return moduleIndex == INVALID_MODULE_INDEX ? ExploreByTouchHelper.INVALID_ID : moduleIndex;
       }

       @Override
       protected void getVisibleVirtualViews(List<Integer> virtualViewIds) {
           if(virtualViewIds == null)
               return;

           for(int moduleIndex = 0 ; moduleIndex < mModuleRectangles.length ; moduleIndex ++ ){
               virtualViewIds.add(moduleIndex);
           }
       }

       @Override
       protected void onPopulateNodeForVirtualView(int virtualViewId, @NonNull AccessibilityNodeInfoCompat node) {
            node.setFocusable(true);
            node.setBoundsInParent(mModuleRectangles[virtualViewId]);
            node.setContentDescription("module " + virtualViewId);

            node.setCheckable(true);
            node.setChecked(mModuleStatus[virtualViewId]);
       }

       @Override
       protected boolean onPerformActionForVirtualView(int virtualViewId, int action, @Nullable Bundle arguments) {
           return false;
       }
   }
}
