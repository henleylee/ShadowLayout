package com.henley.shadowlayout;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.RippleDrawable;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;

import java.util.Arrays;
import java.util.List;

/**
 * Android custom shadow layout, can replace your CardView
 *
 * @author Henley
 * @date 2019/4/19 17:49
 */
public class ShadowLayout extends ViewGroup {

    private static final int DEFAULT_CHILD_GRAVITY = Gravity.TOP | Gravity.START;
    private static final int SIZE_UNSET = -1;
    private static final int SIZE_DEFAULT = 0;

    private Drawable foregroundDrawable;
    private Rect selfBounds = new Rect();
    private Rect overlayBounds = new Rect();
    private int foregroundDrawGravity = Gravity.FILL;
    private boolean foregroundDrawInPadding = true;
    private boolean foregroundDrawBoundsChanged = false;

    private Paint bgPaint = new Paint();
    private int shadowColor;
    private int foregroundColor;
    private int backgroundColor;
    private float shadowRadius;
    private float shadowDx;
    private float shadowDy;
    private float cornerRadiusTL;
    private float cornerRadiusTR;
    private float cornerRadiusBL;
    private float cornerRadiusBR;
    private int shadowMarginTop;
    private int shadowMarginLeft;
    private int shadowMarginRight;
    private int shadowMarginBottom;

    public ShadowLayout(Context context) {
        this(context, null);
    }

    public ShadowLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ShadowLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.ShadowLayout, defStyleAttr, 0);
        shadowColor = a.getColor(R.styleable.ShadowLayout_shadowColor
                , ContextCompat.getColor(context, R.color.shadow_view_default_shadow_color));
        foregroundColor = a.getColor(R.styleable.ShadowLayout_foregroundColor
                , ContextCompat.getColor(context, R.color.shadow_view_foreground_color_dark));
        backgroundColor = a.getColor(R.styleable.ShadowLayout_backgroundColor, Color.WHITE);
        shadowDx = a.getFloat(R.styleable.ShadowLayout_shadowDx, 0f);
        shadowDy = a.getFloat(R.styleable.ShadowLayout_shadowDy, 1f);
        shadowRadius = a.getDimensionPixelSize(R.styleable.ShadowLayout_shadowRadius, SIZE_DEFAULT);
        Drawable drawable = a.getDrawable(R.styleable.ShadowLayout_android_foreground);
        if (drawable != null) {
            setForeground(drawable);
        }
        int shadowMargin = a.getDimensionPixelSize(R.styleable.ShadowLayout_shadowMargin, SIZE_UNSET);
        if (shadowMargin >= 0) {
            shadowMarginTop = shadowMargin;
            shadowMarginLeft = shadowMargin;
            shadowMarginRight = shadowMargin;
            shadowMarginBottom = shadowMargin;
        } else {
            shadowMarginTop = a.getDimensionPixelSize(R.styleable.ShadowLayout_shadowMarginTop, SIZE_DEFAULT);
            shadowMarginLeft = a.getDimensionPixelSize(R.styleable.ShadowLayout_shadowMarginLeft, SIZE_DEFAULT);
            shadowMarginRight = a.getDimensionPixelSize(R.styleable.ShadowLayout_shadowMarginRight, SIZE_DEFAULT);
            shadowMarginBottom = a.getDimensionPixelSize(R.styleable.ShadowLayout_shadowMarginBottom, SIZE_DEFAULT);
        }

        float cornerRadius = a.getDimensionPixelSize(R.styleable.ShadowLayout_cornerRadius, SIZE_UNSET);
        if (cornerRadius >= 0) {
            cornerRadiusTL = cornerRadius;
            cornerRadiusTR = cornerRadius;
            cornerRadiusBL = cornerRadius;
            cornerRadiusBR = cornerRadius;
        } else {
            cornerRadiusTL = a.getDimensionPixelSize(R.styleable.ShadowLayout_cornerRadiusTL, SIZE_DEFAULT);
            cornerRadiusTR = a.getDimensionPixelSize(R.styleable.ShadowLayout_cornerRadiusTR, SIZE_DEFAULT);
            cornerRadiusBL = a.getDimensionPixelSize(R.styleable.ShadowLayout_cornerRadiusBL, SIZE_DEFAULT);
            cornerRadiusBR = a.getDimensionPixelSize(R.styleable.ShadowLayout_cornerRadiusBR, SIZE_DEFAULT);
        }
        a.recycle();
        bgPaint.setColor(backgroundColor);
        bgPaint.setAntiAlias(true);
        bgPaint.setStyle(Paint.Style.FILL);
        setLayerType(LAYER_TYPE_SOFTWARE, null);
        setWillNotDraw(false);
        setBackground(null);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int maxHeight = 0;
        int maxWidth = 0;
        int childState = 0;

        ViewGroup.LayoutParams layoutParams = getLayoutParams();
        setMeasuredDimension(getDefaultSize(0, widthMeasureSpec), getDefaultSize(0, heightMeasureSpec));
        boolean shadowMeasureWidthMatchParent = layoutParams.width == ViewGroup.LayoutParams.MATCH_PARENT;
        boolean shadowMeasureHeightMatchParent = layoutParams.height == ViewGroup.LayoutParams.MATCH_PARENT;
        int widthSpec = widthMeasureSpec;
        if (shadowMeasureWidthMatchParent) {
            int childWidthSize = getMeasuredWidth() - shadowMarginRight - shadowMarginLeft;
            widthSpec = MeasureSpec.makeMeasureSpec(childWidthSize, MeasureSpec.EXACTLY);
        }
        int heightSpec = heightMeasureSpec;
        if (shadowMeasureHeightMatchParent) {
            int childHeightSize = getMeasuredHeight() - shadowMarginTop - shadowMarginBottom;
            heightSpec = MeasureSpec.makeMeasureSpec(childHeightSize, MeasureSpec.EXACTLY);
        }
        View child = getChildAt(0);
        if (child.getVisibility() != View.GONE) {
            measureChildWithMargins(child, widthSpec, 0, heightSpec, 0);
            LayoutParams lp = (LayoutParams) child.getLayoutParams();
            if (shadowMeasureWidthMatchParent) {
                maxWidth = Math.max(maxWidth,
                        child.getMeasuredWidth() + lp.leftMargin + lp.rightMargin);
            } else {
                maxWidth = Math.max(maxWidth, child.getMeasuredWidth() + shadowMarginLeft + shadowMarginRight + lp.leftMargin + lp.rightMargin);
            }
            if (shadowMeasureHeightMatchParent) {
                maxHeight = Math.max(maxHeight, child.getMeasuredHeight() + lp.topMargin + lp.bottomMargin);
            } else {
                maxHeight = Math.max(maxHeight, child.getMeasuredHeight() + shadowMarginTop + shadowMarginBottom + lp.topMargin + lp.bottomMargin);
            }
            childState = View.combineMeasuredStates(childState, child.getMeasuredState());
        }
        maxWidth += getPaddingLeft() + getPaddingRight();
        maxHeight += getPaddingTop() + getPaddingBottom();
        maxHeight = Math.max(maxHeight, getSuggestedMinimumHeight());
        maxWidth = Math.max(maxWidth, getSuggestedMinimumWidth());
        Drawable drawable = getForeground();
        if (drawable != null) {
            maxHeight = Math.max(maxHeight, drawable.getMinimumHeight());
            maxWidth = Math.max(maxWidth, drawable.getMinimumWidth());
        }
        setMeasuredDimension(View.resolveSizeAndState(maxWidth, shadowMeasureWidthMatchParent ? widthMeasureSpec : widthSpec, childState),
                View.resolveSizeAndState(maxHeight, shadowMeasureHeightMatchParent ? heightMeasureSpec : heightSpec, childState << View.MEASURED_HEIGHT_STATE_SHIFT));
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        layoutChildren(left, top, right, bottom, false/* no force left gravity */);
        if (changed) {
            foregroundDrawBoundsChanged = changed;
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void layoutChildren(int left, int top, int right, int bottom, boolean forceLeftGravity) {
        int count = getChildCount();

        int parentLeft = getPaddingLeftWithForeground();
        int parentRight = right - left - getPaddingRightWithForeground();

        int parentTop = getPaddingTopWithForeground();
        int parentBottom = bottom - top - getPaddingBottomWithForeground();

        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            if (child.getVisibility() != View.GONE) {
                LayoutParams lp = (LayoutParams) child.getLayoutParams();

                int width = child.getMeasuredWidth();
                int height = child.getMeasuredHeight();

                int childLeft = 0;
                int childTop;

                int gravity = lp.gravity;
                if (gravity == -1) {
                    gravity = DEFAULT_CHILD_GRAVITY;
                }

                int layoutDirection = getLayoutDirection();
                int absoluteGravity = Gravity.getAbsoluteGravity(gravity, layoutDirection);
                int verticalGravity = gravity & Gravity.VERTICAL_GRAVITY_MASK;

                switch (absoluteGravity & Gravity.HORIZONTAL_GRAVITY_MASK) {
                    case Gravity.CENTER_HORIZONTAL:
                        childLeft = parentLeft + (parentRight - parentLeft - width) / 2 +
                                lp.leftMargin - lp.rightMargin + shadowMarginLeft - shadowMarginRight;
                        break;
                    case Gravity.RIGHT:
                        if (!forceLeftGravity) {
                            childLeft = parentRight - width - lp.rightMargin - shadowMarginRight;
                        }
                        break;
                    case Gravity.LEFT:
                        childLeft = parentLeft + lp.leftMargin + shadowMarginLeft;
                        break;
                    default:
                        childLeft = parentLeft + lp.leftMargin + shadowMarginLeft;
                        break;
                }
                switch (verticalGravity) {
                    case Gravity.TOP:
                        childTop = parentTop + lp.topMargin + shadowMarginTop;
                        break;
                    case Gravity.CENTER_VERTICAL:
                        childTop = parentTop + (parentBottom - parentTop - height) / 2 +
                                lp.topMargin - lp.bottomMargin + shadowMarginTop - shadowMarginBottom;
                        break;
                    case Gravity.BOTTOM:
                        childTop = parentBottom - height - lp.bottomMargin - shadowMarginBottom;
                        break;
                    default:
                        childTop = parentTop + lp.topMargin + shadowMarginTop;
                        break;
                }
                child.layout(childLeft, childTop, childLeft + width, childTop + height);
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (canvas != null) {
            int w = getMeasuredWidth();
            int h = getMeasuredHeight();
            Path path = ShapeUtils.roundedRect(shadowMarginLeft, shadowMarginTop, (w - shadowMarginRight), (h - shadowMarginBottom)
                    , cornerRadiusTL
                    , cornerRadiusTR
                    , cornerRadiusBR
                    , cornerRadiusBL);
            canvas.drawPath(path, bgPaint);
            canvas.clipPath(path);
        }
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        if (canvas != null) {
            canvas.save();
            int w = getMeasuredWidth();
            int h = getMeasuredHeight();
            Path path = ShapeUtils.roundedRect(shadowMarginLeft, shadowMarginTop, (w - shadowMarginRight)
                    , (h - shadowMarginBottom)
                    , cornerRadiusTL
                    , cornerRadiusTR
                    , cornerRadiusBR
                    , cornerRadiusBL);
            canvas.clipPath(path);
            drawForeground(canvas);
            canvas.restore();
        }
    }

    private void updatePaintShadow() {
        updatePaintShadow(shadowRadius, shadowDx, shadowDy, shadowColor);
    }

    private void updatePaintShadow(float radius, float dx, float dy, int color) {
        bgPaint.setShadowLayer(radius, dx, dy, color);
        invalidate();
    }

    private float getShadowMarginMax() {
        float max = 0f;
        List<Integer> margins = Arrays.asList(shadowMarginLeft, shadowMarginTop, shadowMarginRight, shadowMarginBottom);
        for (Integer value : margins) {
            max = Math.max(max, value);
        }
        return max;
    }

    private void drawForeground(Canvas canvas) {
        if (foregroundDrawable != null) {
            if (foregroundDrawBoundsChanged) {
                foregroundDrawBoundsChanged = false;
                int w = getRight() - getLeft();
                int h = getBottom() - getTop();
                if (foregroundDrawInPadding) {
                    selfBounds.set(0, 0, w, h);
                } else {
                    selfBounds.set(getPaddingLeft(), getPaddingTop(), w - getPaddingRight(), h - getPaddingBottom());
                }
                Gravity.apply(foregroundDrawGravity, foregroundDrawable.getIntrinsicWidth(),
                        foregroundDrawable.getIntrinsicHeight(), selfBounds, overlayBounds);
                foregroundDrawable.setBounds(overlayBounds);
            }
            foregroundDrawable.draw(canvas);
        }

    }

    @Override
    public Drawable getForeground() {
        return foregroundDrawable;
    }

    @Override
    public void setForeground(Drawable foreground) {
        if (foregroundDrawable != null) {
            foregroundDrawable.setCallback(null);
            unscheduleDrawable(foregroundDrawable);
        }
        foregroundDrawable = foreground;

        updateForegroundColor();

        if (foreground != null) {
            setWillNotDraw(false);
            foreground.setCallback(this);
            if (foreground.isStateful()) {
                foreground.setState(getDrawableState());
            }
            if (foregroundDrawGravity == Gravity.FILL) {
                Rect padding = new Rect();
                foreground.getPadding(padding);
            }
        }
        requestLayout();
        invalidate();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        foregroundDrawBoundsChanged = true;
    }

    @Override
    public int getForegroundGravity() {
        return foregroundDrawGravity;
    }

    @Override
    public void setForegroundGravity(int foregroundGravity) {
        if (foregroundDrawGravity != foregroundGravity) {
            if ((foregroundGravity & Gravity.RELATIVE_HORIZONTAL_GRAVITY_MASK) == 0) {
                foregroundGravity = foregroundGravity | Gravity.START;
            }
            if ((foregroundGravity & Gravity.VERTICAL_GRAVITY_MASK) == 0) {
                foregroundGravity = foregroundGravity | Gravity.TOP;
            }
            foregroundDrawGravity = foregroundGravity;
            if (foregroundDrawGravity == Gravity.FILL && foregroundDrawable != null) {
                Rect padding = new Rect();
                foregroundDrawable.getPadding(padding);
            }
            requestLayout();
        }
    }

    @Override
    protected boolean verifyDrawable(@NonNull Drawable who) {
        return super.verifyDrawable(who) || who == foregroundDrawable;
    }

    @Override
    public void jumpDrawablesToCurrentState() {
        super.jumpDrawablesToCurrentState();
        if (foregroundDrawable != null) {
            foregroundDrawable.jumpToCurrentState();
        }
    }

    @Override
    protected void drawableStateChanged() {
        super.drawableStateChanged();
        if (foregroundDrawable != null) {
            if (foregroundDrawable.isStateful()) {
                foregroundDrawable.setState(getDrawableState());
            }
        }
    }

    private void updateForegroundColor() {
        if (foregroundDrawable != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                if (foregroundDrawable instanceof RippleDrawable) {
                    ((RippleDrawable) foregroundDrawable).setColor(ColorStateList.valueOf(foregroundColor));
                }
            } else {
                foregroundDrawable.setColorFilter(foregroundColor, PorterDuff.Mode.SRC_ATOP);
            }
        }
    }

    @Override
    public void drawableHotspotChanged(float x, float y) {
        super.drawableHotspotChanged(x, y);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (foregroundDrawable != null) {
                foregroundDrawable.setHotspot(x, y);
            }
        }
    }

    /**
     * Gets the shadow color.
     *
     * @attr ref R.styleable#ShadowLayout_shadowColor
     * @see #setShadowColor(int)
     */
    public int getShadowColor() {
        return shadowColor;
    }

    /**
     * Sets the shadow color.
     *
     * @param shadowColor A color value in the form 0xAARRGGBB.
     * @attr ref R.styleable#ShadowLayout_shadowColor
     * @see #getShadowColor()
     */
    public void setShadowColor(@ColorInt int shadowColor) {
        this.shadowColor = shadowColor;
        updatePaintShadow(shadowRadius, shadowDx, shadowDy, shadowColor);
    }

    /**
     * Gets the foreground color.
     *
     * @attr ref R.styleable#ShadowLayout_foregroundColor
     * @see #setForegroundColor(int)
     */
    public int getForegroundColor() {
        return foregroundColor;
    }

    /**
     * Sets the foreground color.
     *
     * @param foregroundColor A color value in the form 0xAARRGGBB.
     * @attr ref R.styleable#ShadowLayout_foregroundColor
     * @see #getForegroundColor()
     */
    public void setForegroundColor(@ColorInt int foregroundColor) {
        this.foregroundColor = foregroundColor;
        updateForegroundColor();
    }

    /**
     * Gets the background color.
     *
     * @attr ref R.styleable#ShadowLayout_backgroundColor
     * @see #setBackgroundColor(int)
     */
    public int getBackgroundColor() {
        return backgroundColor;
    }

    /**
     * Sets the background color.
     *
     * @param backgroundColor A color value in the form 0xAARRGGBB.
     * @attr ref R.styleable#ShadowLayout_backgroundColor
     * @see #getBackgroundColor()
     */
    public void setBackgroundColor(int backgroundColor) {
        this.backgroundColor = backgroundColor;
        invalidate();
    }

    /**
     * Gets the shadow radius in pixels.
     *
     * @attr ref R.styleable#ShadowLayout_shadowRadius
     * @see #setShadowRadius(float)
     */
    public float getShadowRadius() {
        if (shadowRadius > getShadowMarginMax() && getShadowMarginMax() != 0f) {
            return getShadowMarginMax();
        } else {
            return shadowRadius;
        }
    }

    /**
     * Sets the shadow radius in pixels.
     *
     * @param shadowRadius The shadow radius in pixels.
     * @attr ref R.styleable#ShadowLayout_shadowRadius
     * @see #getShadowRadius()
     */
    public void setShadowRadius(float shadowRadius) {
        if (shadowRadius > getShadowMarginMax() && getShadowMarginMax() != 0f) {
            shadowRadius = getShadowMarginMax();
        }
        this.shadowRadius = shadowRadius;
        updatePaintShadow(shadowRadius, shadowDx, shadowDy, shadowColor);
    }

    /**
     * Gets the shadow dx in pixels.
     *
     * @attr ref R.styleable#ShadowLayout_shadowDx
     * @see #setShadowDx(float)
     */
    public float getShadowDx() {
        return shadowDx;
    }

    /**
     * Sets the shadow dx in pixels.
     *
     * @param shadowDx The shadow dx in pixels.
     * @attr ref R.styleable#ShadowLayout_shadowDx
     * @see #getShadowDx()
     */
    public void setShadowDx(float shadowDx) {
        this.shadowDx = shadowDx;
        updatePaintShadow(shadowRadius, shadowDx, shadowDy, shadowColor);
    }

    /**
     * Gets the shadow dy in pixels.
     *
     * @attr ref R.styleable#ShadowLayout_shadowDy
     * @see #setShadowDy(float)
     */
    public float getShadowDy() {
        return shadowDy;
    }

    /**
     * Sets the shadow dy in pixels.
     *
     * @param shadowDy The shadow dy in pixels.
     * @attr ref R.styleable#ShadowLayout_shadowDy
     * @see #getShadowDy()
     */
    public void setShadowDy(float shadowDy) {
        this.shadowDy = shadowDy;
        updatePaintShadow(shadowRadius, shadowDx, shadowDy, shadowColor);
    }

    /**
     * Gets the top shadow margin in pixels.
     *
     * @attr ref R.styleable#ShadowLayout_shadowMarginTop
     * @see #setShadowMarginTop(int)
     */
    public int getShadowMarginTop() {
        return shadowMarginTop;
    }

    /**
     * Sets the top shadow margin in pixels.
     *
     * @param shadowMarginTop The top shadow margin in pixels.
     * @attr ref R.styleable#ShadowLayout_shadowMarginTop
     * @see #getShadowMarginTop()
     */
    public void setShadowMarginTop(int shadowMarginTop) {
        this.shadowMarginTop = shadowMarginTop;
        updatePaintShadow();
    }

    /**
     * Gets the left shadow margin in pixels.
     *
     * @attr ref R.styleable#ShadowLayout_shadowMarginLeft
     * @see #setShadowMarginLeft(int)
     */
    public int getShadowMarginLeft() {
        return shadowMarginLeft;
    }

    /**
     * Sets the left shadow margin in pixels.
     *
     * @param shadowMarginLeft The left shadow margin in pixels.
     * @attr ref R.styleable#ShadowLayout_shadowMarginLeft
     * @see #getShadowMarginLeft()
     */
    public void setShadowMarginLeft(int shadowMarginLeft) {
        this.shadowMarginLeft = shadowMarginLeft;
        updatePaintShadow();
    }

    /**
     * Gets the right shadow margin in pixels.
     *
     * @attr ref R.styleable#ShadowLayout_shadowMarginRight
     * @see #setShadowMarginRight(int)
     */
    public int getShadowMarginRight() {
        return shadowMarginRight;
    }

    /**
     * Sets the right shadow margin in pixels.
     *
     * @param shadowMarginRight The right shadow margin in pixels.
     * @attr ref R.styleable#ShadowLayout_shadowMarginRight
     * @see #getShadowMarginRight()
     */
    public void setShadowMarginRight(int shadowMarginRight) {
        this.shadowMarginRight = shadowMarginRight;
        updatePaintShadow();
    }

    /**
     * Gets the bottom shadow margin in pixels.
     *
     * @attr ref R.styleable#ShadowLayout_shadowMarginBottom
     * @see #setShadowMarginBottom(int)
     */
    public int getShadowMarginBottom() {
        return shadowMarginBottom;
    }

    /**
     * Sets the bottom shadow margin in pixels.
     *
     * @param shadowMarginBottom The bottom shadow margin in pixels.
     * @attr ref R.styleable#ShadowLayout_shadowMarginBottom
     * @see #getShadowMarginBottom()
     */
    public void setShadowMarginBottom(int shadowMarginBottom) {
        this.shadowMarginBottom = shadowMarginBottom;
        updatePaintShadow();
    }

    /**
     * Sets the shadow margin in pixels.
     *
     * @param left   The left shadow margin in pixels.
     * @param top    The top shadow margin in pixels.
     * @param right  The right shadow margin in pixels.
     * @param bottom The bottom shadow margin in pixels.
     * @attr ref R.styleable#ShadowLayout_shadowMargin
     */
    public void setShadowMargin(int left, int top, int right, int bottom) {
        this.shadowMarginLeft = left;
        this.shadowMarginTop = top;
        this.shadowMarginRight = right;
        this.shadowMarginBottom = bottom;
        requestLayout();
        invalidate();
    }

    /**
     * Gets the top-left corner radius in pixels.
     *
     * @attr ref R.styleable#ShadowLayout_cornerRadiusTL
     * @see #setCornerRadiusTL(float)
     */
    public float getCornerRadiusTL() {
        return cornerRadiusTL;
    }

    /**
     * Sets the top-left corner radius in pixels.
     *
     * @param cornerRadiusTL The top-left corner radius in pixels.
     * @attr ref R.styleable#ShadowLayout_cornerRadiusTL
     * @see #getCornerRadiusTL()
     */
    public void setCornerRadiusTL(float cornerRadiusTL) {
        this.cornerRadiusTL = cornerRadiusTL;
        invalidate();
    }

    /**
     * Gets the top-right corner radius in pixels.
     *
     * @attr ref R.styleable#ShadowLayout_cornerRadiusTR
     * @see #setCornerRadiusTR(float)
     */
    public float getCornerRadiusTR() {
        return cornerRadiusTR;
    }

    /**
     * Sets the top-right corner radius in pixels.
     *
     * @param cornerRadiusTR The top-right corner radius in pixels.
     * @attr ref R.styleable#ShadowLayout_cornerRadiusTR
     * @see #getCornerRadiusTR()
     */
    public void setCornerRadiusTR(float cornerRadiusTR) {
        this.cornerRadiusTR = cornerRadiusTR;
        invalidate();
    }

    /**
     * Gets the bottom-left corner radius in pixels.
     *
     * @attr ref R.styleable#ShadowLayout_cornerRadiusBL
     * @see #setCornerRadiusBL(float)
     */
    public float getCornerRadiusBL() {
        return cornerRadiusBL;
    }

    /**
     * Sets the bottom-left corner radius in pixels.
     *
     * @param cornerRadiusBL The bottom-left corner radius in pixels.
     * @attr ref R.styleable#ShadowLayout_cornerRadiusBL
     * @see #getCornerRadiusBL()
     */
    public void setCornerRadiusBL(float cornerRadiusBL) {
        this.cornerRadiusBL = cornerRadiusBL;
        invalidate();
    }

    /**
     * Gets the bottom-right corner radius in pixels.
     *
     * @attr ref R.styleable#ShadowLayout_cornerRadiusBR
     * @see #setCornerRadiusBR(float)
     */
    public float getCornerRadiusBR() {
        return cornerRadiusBR;
    }

    /**
     * Sets the bottom-right corner radius in pixels.
     *
     * @param cornerRadiusBR The bottom-right corner radius in pixels.
     * @attr ref R.styleable#ShadowLayout_cornerRadiusBR
     * @see #getCornerRadiusBR()
     */
    public void setCornerRadiusBR(float cornerRadiusBR) {
        this.cornerRadiusBR = cornerRadiusBR;
        invalidate();
    }

    /**
     * Sets the corner radius in pixels.
     *
     * @param tl The top-left corner radius in pixels.
     * @param tr The top-right corner radius in pixels.
     * @param br The bottom-right corner radius in pixels.
     * @param bl The bottom-left corner radius in pixels.
     * @attr ref R.styleable#ShadowLayout_cornerRadius
     */
    public void setCornerRadius(float tl, float tr, float br, float bl) {
        this.cornerRadiusTL = tl;
        this.cornerRadiusTR = tr;
        this.cornerRadiusBR = br;
        this.cornerRadiusBL = bl;
        invalidate();
    }

    private int getPaddingLeftWithForeground() {
        return getPaddingLeft();
    }

    private int getPaddingRightWithForeground() {
        return getPaddingRight();
    }

    private int getPaddingTopWithForeground() {
        return getPaddingTop();
    }

    private int getPaddingBottomWithForeground() {
        return getPaddingBottom();
    }

    @Override
    public ViewGroup.LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutParams(getContext(), attrs);
    }

    @Override
    protected ViewGroup.LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
        return new LayoutParams(p);
    }

    @Override
    protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
        return p instanceof LayoutParams;
    }

    @Override
    public CharSequence getAccessibilityClassName() {
        return ShadowLayout.class.getName();
    }

    @Override
    public boolean shouldDelayChildPressedState() {
        return false;
    }

    /**
     * Per-child layout information for layouts that support margins.
     *
     * @attr ref android.R.styleable#ShadowLayout_Layout_layout_gravity
     */
    public static class LayoutParams extends ViewGroup.MarginLayoutParams {

        public static final int UNSPECIFIED_GRAVITY = -1;
        public int gravity = UNSPECIFIED_GRAVITY;

        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
            TypedArray a = c.obtainStyledAttributes(attrs, R.styleable.ShadowLayout_Layout);
            gravity = a.getInt(R.styleable.ShadowLayout_Layout_layout_gravity, UNSPECIFIED_GRAVITY);
            a.recycle();
        }

        public LayoutParams(int width, int height) {
            super(width, height);
        }

        /**
         * Creates a new set of layout parameters with the specified width, height and weight.
         *
         * @param width   the width, either {@link #MATCH_PARENT}, {@link #WRAP_CONTENT} or a fixed size in pixels
         * @param height  the height, either {@link #MATCH_PARENT}, {@link #WRAP_CONTENT} or a fixed size in pixels
         * @param gravity the gravity
         * @see android.view.Gravity
         */
        public LayoutParams(int width, int height, int gravity) {
            super(width, height);
            this.gravity = gravity;
        }

        public LayoutParams(@NonNull ViewGroup.LayoutParams source) {
            super(source);
        }

        public LayoutParams(@NonNull ViewGroup.MarginLayoutParams source) {
            super(source);
        }

        /**
         * Copy constructor. Clones the width, height, margin values, and
         * gravity of the source.
         *
         * @param source The layout params to copy from.
         */
        public LayoutParams(@NonNull LayoutParams source) {
            super(source);
            this.gravity = source.gravity;
        }

    }

}
