package androidx.widget;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.Shader;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;


/**
 * 折线统计图
 */
public class LineChartView extends View {

    public final String TAG = LineChartView.class.getSimpleName();
    //模拟数据
    private boolean simulationData = true;
    //画笔
    private Paint paint;
    //宽高
    private int width, height;
    //线条颜色
    private int markLineColor = Color.parseColor("#707070");
    //线条宽度
    private float markLineStrokeWidth = 1;
    //字体颜色
    private int markTextColor = Color.parseColor("#707070");
    //字体大小
    private float markTextSize = dip(10);
    //折现颜色
    private int chartLintColor = Color.parseColor("#00C4C9");
    //折现填充颜色
    private int chartLintSolidColor = Color.parseColor("#00C2C7");
    //折现宽度
    private float chartLineStrokeWidth = 2;
    //刻度线左边间距
    private float tickMarkMarginLeft = dip(30);
    //刻度线文字间距
    private float tickMarkTextMargin = 10;
    //刻度线底部间距
    private float tickMarkMarginBottom = 70;
    //刻度线右边间距
    private float tickMarkMarginRight = 40;
    //刻度值宽度
    private float tickMarkWH = 10;
    //刻度线（垂直）段数
    private int tickMarkVerticalCount = 10;
    //刻度线（水平）段数
    private int tickMarkHorizontalCount = 31;
    //垂直单位刻度值
    private int unitVerticalTickMarkValue = 200;
    //水平单位刻度值
    private int unitHorizontalTickMarkValue = 1;
    //水平刻度开始值
    private int horizontalTickMarkStartValue = 1;
    //点击范围
    private float coordinateDownRange = dip(10);
    //圆点半径
    private float circleRadius = 6;
    //圆点外圈间距
    private float circleStrokeWidth = 6;
    //圆点外圈颜色
    private int circleStrokeColor = Color.parseColor("#3791C0");
    //圆点填充颜色
    private int circleSolidColor = Color.parseColor("#3ACACC");
    //描述信息背景颜色
    private int descriptionBackgroundColor = Color.parseColor("#8F000000");
    //描述信息背景圆角大小
    private float descriptionBackgroundRadius = dip(3);
    //描述信息文字大小
    private float descriptionTextSize = dip(12);
    //描述信息文字垂直间距
    private float descriptionTextSpace = dip(10);
    //描述信息文字颜色
    private int descriptionTextColor = Color.parseColor("#FFFFFF");
    //描述信息面板宽度
    private float descriptionWidth = dip(95);
    //描述信息面板高度
    private float descriptionHeight = dip(55);
    //描述信息面板距离上方间距
    private float descriptionMarginTop = dip(8);
    //描述信息面板 - x轴名称
    private String descriptionXName = "X";
    //描述信息面板 - x轴单位
    private String descriptionXUnit = "";
    //描述信息面板 - y轴名称
    private String descriptionYName = "Y";
    //描述信息面板 - y轴单位
    private String descriptionYUnit = "";

    //折现数据
    private List<Double> data;
    //线坐标集合
    private List<Polyline> coordinates;
    //点击的坐标点
    private Polyline downPolyline;
    //折现点击监听
    private OnLineChartPolylineClickListener onLineChartPolylineClickListener;

    public LineChartView(Context context) {
        super(context);
        initAttributeSet(context, null);
    }

    public LineChartView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initAttributeSet(context, attrs);
    }

    public LineChartView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttributeSet(context, attrs);
    }

    private void initAttributeSet(Context context, AttributeSet attrs) {
        coordinates = new ArrayList<>();
        if (attrs != null) {
            TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.LineChartView);
            simulationData = array.getBoolean(R.styleable.LineChartView_simulationData, simulationData);
            markLineColor = array.getColor(R.styleable.LineChartView_markLineColor, markLineColor);
            markLineStrokeWidth = array.getDimension(R.styleable.LineChartView_markLineStrokeWidth, markLineStrokeWidth);
            markTextColor = array.getColor(R.styleable.LineChartView_markTextColor, markTextColor);
            markTextSize = array.getDimension(R.styleable.LineChartView_markTextSize, markTextSize);
            chartLintColor = array.getColor(R.styleable.LineChartView_chartLintColor, chartLintColor);
            chartLintSolidColor = array.getColor(R.styleable.LineChartView_chartLintSolidColor, chartLintSolidColor);
            chartLineStrokeWidth = array.getDimension(R.styleable.LineChartView_chartLineStrokeWidth, chartLineStrokeWidth);
            tickMarkMarginLeft = array.getDimension(R.styleable.LineChartView_tickMarkMarginLeft, tickMarkMarginLeft);
            tickMarkTextMargin = array.getDimension(R.styleable.LineChartView_tickMarkTextMargin, tickMarkTextMargin);
            tickMarkMarginBottom = array.getDimension(R.styleable.LineChartView_tickMarkMarginBottom, tickMarkMarginBottom);
            tickMarkMarginRight = array.getDimension(R.styleable.LineChartView_tickMarkMarginRight, tickMarkMarginRight);
            tickMarkWH = array.getDimension(R.styleable.LineChartView_tickMarkWH, tickMarkWH);
            tickMarkVerticalCount = array.getInt(R.styleable.LineChartView_tickMarkVerticalCount, tickMarkVerticalCount);
            tickMarkHorizontalCount = array.getInt(R.styleable.LineChartView_tickMarkHorizontalCount, tickMarkHorizontalCount);
            unitVerticalTickMarkValue = array.getInt(R.styleable.LineChartView_unitVerticalTickMarkValue, unitVerticalTickMarkValue);
            unitHorizontalTickMarkValue = array.getInt(R.styleable.LineChartView_unitHorizontalTickMarkValue, unitHorizontalTickMarkValue);
            horizontalTickMarkStartValue = array.getInt(R.styleable.LineChartView_horizontalTickMarkStartValue, horizontalTickMarkStartValue);
            coordinateDownRange = array.getDimension(R.styleable.LineChartView_coordinateDownRange, coordinateDownRange);
            circleRadius = array.getDimension(R.styleable.LineChartView_circleRadius, circleRadius);
            circleStrokeWidth = array.getDimension(R.styleable.LineChartView_circleStrokeWidth, circleStrokeWidth);
            circleStrokeColor = array.getColor(R.styleable.LineChartView_circleStrokeColor, circleStrokeColor);
            circleSolidColor = array.getColor(R.styleable.LineChartView_circleSolidColor, circleSolidColor);
            descriptionBackgroundColor = array.getColor(R.styleable.LineChartView_descriptionBackgroundColor, descriptionBackgroundColor);
            descriptionBackgroundRadius = array.getDimension(R.styleable.LineChartView_descriptionBackgroundRadius, descriptionBackgroundRadius);
            descriptionTextSize = array.getDimension(R.styleable.LineChartView_descriptionTextSize, descriptionTextSize);
            descriptionTextSpace = array.getDimension(R.styleable.LineChartView_descriptionTextSpace, descriptionTextSpace);
            descriptionTextColor = array.getColor(R.styleable.LineChartView_descriptionTextColor, descriptionTextColor);
            descriptionWidth = array.getDimension(R.styleable.LineChartView_descriptionWidth, descriptionWidth);
            descriptionHeight = array.getDimension(R.styleable.LineChartView_descriptionHeight, descriptionHeight);
            descriptionMarginTop = array.getDimension(R.styleable.LineChartView_descriptionMarginTop, descriptionMarginTop);
            String xName = array.getString(R.styleable.LineChartView_descriptionXName);
            descriptionXName = TextUtils.isEmpty(xName) ? "X" : xName;
            String xUnit = array.getString(R.styleable.LineChartView_descriptionXUnit);
            descriptionXUnit = TextUtils.isEmpty(xUnit) ? "" : xUnit;
            String yName = array.getString(R.styleable.LineChartView_descriptionYName);
            descriptionYName = TextUtils.isEmpty(yName) ? "Y" : yName;
            String yUnit = array.getString(R.styleable.LineChartView_descriptionYUnit);
            descriptionYUnit = TextUtils.isEmpty(yUnit) ? "" : yUnit;
            array.recycle();
        }
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        width = getMeasuredWidth();
        height = getMeasuredHeight();
        //单元测试
        if (simulationData) {
            data = new ArrayList<>();
            for (int i = 0; i < tickMarkHorizontalCount; i++) {
                double value = 1 + new Random().nextInt(1800);
                data.add(value);
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawVerticalTickMark(canvas);
        drawHorizontalTickMark(canvas);
        drawChartLine(canvas);
        showCoordinate(canvas, downPolyline);
    }

    /**
     * @param value px
     * @return dip
     */
    private int dip(int value) {
        return (int) (Resources.getSystem().getDisplayMetrics().density * value);
    }

    /**
     * 设置数据源
     *
     * @param data
     */
    public void setDatasource(List<Double> data) {
        this.data = data;
        simulationData = false;
        invalidate();
    }

    /**
     * 构建刻度画笔
     *
     * @return
     */
    private Paint buildTickMarkPaint() {
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(markLineColor);
        paint.setStrokeWidth(markLineStrokeWidth);
        return paint;
    }

    /**
     * 构建刻度文字画笔
     *
     * @return
     */
    private Paint buildTickMarkTextPaint() {
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(markTextColor);
        paint.setTextSize(markTextSize);
        return paint;
    }

    /**
     * 绘制垂直刻度线
     *
     * @param canvas
     */
    private void drawVerticalTickMark(Canvas canvas) {
        paint = buildTickMarkPaint();
        //刻度
        float tickMarkHeight = height - tickMarkMarginBottom;
        float unitHeight = tickMarkHeight / tickMarkVerticalCount;
        //线
        canvas.drawLine(tickMarkMarginLeft, unitHeight, tickMarkMarginLeft, height - tickMarkMarginBottom, paint);
        //刻度文字画笔
        Paint textPaint = buildTickMarkTextPaint();
        for (int i = 0; i < tickMarkVerticalCount; i++) {
            //刻度线
            canvas.drawLine(tickMarkMarginLeft, tickMarkHeight - i * unitHeight, tickMarkMarginLeft + tickMarkWH, tickMarkHeight - i * unitHeight, paint);
            //刻度文字
            String text = String.valueOf(unitVerticalTickMarkValue * i);
            Rect bounds = new Rect();
            textPaint.getTextBounds(text, 0, text.length(), bounds);
            canvas.drawText(text, tickMarkMarginLeft - bounds.width() - tickMarkTextMargin, tickMarkHeight + bounds.height() / 2 - i * unitHeight, textPaint);
        }
    }

    /**
     * 绘制水平刻度线
     *
     * @param canvas
     */
    private void drawHorizontalTickMark(Canvas canvas) {
        paint = buildTickMarkPaint();
        canvas.drawLine(tickMarkMarginLeft, height - tickMarkMarginBottom, width - tickMarkMarginRight, height - tickMarkMarginBottom, paint);
        //刻度线宽度
        float horizontalTickMarkWidth = width - tickMarkMarginLeft - tickMarkMarginRight;
        float unitWidth = horizontalTickMarkWidth / tickMarkHorizontalCount;
        //刻度文字画笔
        Paint textPaint = buildTickMarkTextPaint();
        for (int i = 1; i <= tickMarkHorizontalCount; i++) {
            //刻度线
            canvas.drawLine(tickMarkMarginLeft + i * unitWidth, height - tickMarkMarginBottom, tickMarkMarginLeft + i * unitWidth, height - tickMarkMarginBottom - tickMarkWH, paint);
            //刻度文字
            String text = String.valueOf(horizontalTickMarkStartValue - 1 + i * unitHorizontalTickMarkValue);
            Rect bounds = new Rect();
            textPaint.getTextBounds(text, 0, text.length(), bounds);
            canvas.drawText(text, tickMarkMarginLeft + i * unitWidth - bounds.width() / 2, height - tickMarkMarginBottom + bounds.height() + tickMarkTextMargin, textPaint);
        }
    }

    /**
     * @return 数据量
     */
    public int getCount() {
        return data == null ? 0 : data.size();
    }

    /**
     * @return 最大值
     */
    public double getMaxValue() {
        List<Double> list = new ArrayList<>();
        for (int i = 0; i < getCount(); i++) {
            list.add(data.get(i));
        }
        return Collections.max(list);
    }

    /**
     * 绘制折线
     *
     * @param canvas
     */
    private void drawChartLine(Canvas canvas) {
        //绘制折现
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(chartLintColor);
        paint.setStrokeWidth(chartLineStrokeWidth);
        //=============数据============
        //获取最大值对应的高度尺寸
        double maxValueHeight = getMaxValue();
        //刻度线单位宽度
        float horizontalTickMarkWidth = width - tickMarkMarginLeft - tickMarkMarginRight;
        float unitWidth = horizontalTickMarkWidth / tickMarkHorizontalCount;
        //刻度线单位高度
        float tickMarkHeight = height - tickMarkMarginBottom;
        float unitHeight = tickMarkHeight / tickMarkVerticalCount;
        //=============绘制折线数据============
        Path path = new Path();
        path.moveTo(tickMarkMarginLeft, height - tickMarkMarginBottom);
        int size = getCount();
        float startX = tickMarkMarginLeft;
        float startY = height - tickMarkMarginBottom;
        for (int i = 0; i < size; i++) {
            double value = data.get(i);
            float valueHeight = (float) (value * unitHeight / unitVerticalTickMarkValue);
            float endX = tickMarkMarginLeft + unitWidth * (i + 1);
            float endY = height - tickMarkMarginBottom - valueHeight;
            addCoordinate(endX, endY, String.valueOf(i + horizontalTickMarkStartValue), String.valueOf(value));
            path.lineTo(endX, endY);
            canvas.drawLine(startX, startY, endX, endY, paint);
            startX = endX;
            startY = endY;
        }
        //=============填充颜色===========
        Paint fillPaint = new Paint();
        fillPaint.setAntiAlias(true);
        fillPaint.setColor(chartLintSolidColor);
        fillPaint.setStyle(Paint.Style.FILL);
        path.lineTo(startX, height - tickMarkMarginBottom);
        path.close();
        float maxY = (float) (height - tickMarkMarginBottom - maxValueHeight);
        LinearGradient linearGradient = new LinearGradient(tickMarkMarginLeft, maxY, tickMarkMarginLeft, height - tickMarkMarginBottom, chartLintSolidColor, Color.TRANSPARENT, Shader.TileMode.CLAMP);
        fillPaint.setShader(linearGradient);
        canvas.drawPath(path, fillPaint);
    }


    /**
     * 添加坐标到坐标集合
     *
     * @param x
     * @param y
     * @param valueX
     * @param valueY
     */
    private void addCoordinate(float x, float y, String valueX, String valueY) {
        Polyline coordinate = new Polyline();
        coordinate.setX(x);
        coordinate.setY(y);
        coordinate.setValueX(valueX);
        coordinate.setValueY(valueY);
        coordinates.add(coordinate);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downPolyline = findPolylineCoordinateByDown(event.getX(), event.getY());
                if (downPolyline != null) {
                    invalidate();
                    if (onLineChartPolylineClickListener != null) {
                        onLineChartPolylineClickListener.onLineChartPolylineClick(this, downPolyline);
                    }
                } else {
                    Log.i(TAG, "->onTouchEvent coordinate null.");
                }
                break;
        }
        return true;
    }

    /**
     * 设置折现点击监听
     *
     * @param onLineChartPolylineClickListener
     */
    public void setOnLineChartPolylineClickListener(OnLineChartPolylineClickListener onLineChartPolylineClickListener) {
        this.onLineChartPolylineClickListener = onLineChartPolylineClickListener;
    }

    public interface OnLineChartPolylineClickListener {

        /**
         * 折现点击事件
         *
         * @param v        折现统计View
         * @param polyline 折现数据对象
         */
        void onLineChartPolylineClick(LineChartView v, Polyline polyline);

    }

    /**
     * 查找点击的坐标点
     *
     * @param downX
     * @param downY
     * @return
     */
    private Polyline findPolylineCoordinateByDown(float downX, float downY) {
        int size = coordinates == null ? 0 : coordinates.size();
        for (int i = 0; i < size; i++) {
            Polyline coordinate = coordinates.get(i);
            float x = coordinate.getX();
            float y = coordinate.getY();
            //&& downY >= y - coordinateDownRange && downY <= y + coordinateDownRange
            if (downX >= x - coordinateDownRange && downX <= x + coordinateDownRange) {
                return coordinate;
            }
        }
        return null;
    }

    /**
     * 显示坐标点信息
     *
     * @param canvas
     * @param coordinate
     */
    private void showCoordinate(Canvas canvas, Polyline coordinate) {
        if (coordinate == null) {
            return;
        }
        float cx = coordinate.getX();
        float cy = coordinate.getY();
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(circleStrokeColor);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(cx, cy, circleRadius + circleStrokeWidth, paint);
        paint.setColor(circleSolidColor);
        canvas.drawCircle(cx, cy, circleRadius, paint);
        //===========文字面板信息=============
        //面板文字参数
        Paint textPaint = new Paint();
        textPaint.setAntiAlias(true);
        textPaint.setColor(descriptionTextColor);
        textPaint.setTextSize(descriptionTextSize);
        //上方文字
        String topText = descriptionXName + "：" + coordinate.getValueX() + descriptionXUnit;
        Rect topBounds = new Rect();
        textPaint.getTextBounds(topText, 0, topText.length(), topBounds);
        //下方文字
        String bottomText = descriptionYName + "：" + coordinate.getValueY().replace(".0", "") + descriptionYUnit;
        Rect bottomBounds = new Rect();
        textPaint.getTextBounds(bottomText, 0, bottomText.length(), bottomBounds);
        float[] widths = new float[]{topBounds.width(), bottomBounds.width()};
        float[] heights = new float[]{topBounds.height(), bottomBounds.height()};
        Arrays.sort(widths);
        Arrays.sort(heights);
        float maxWidth = widths[widths.length - 1];
        float maxHeight = heights[heights.length - 1];
        //面板背景
        paint.setColor(descriptionBackgroundColor);
        float left = cx - maxWidth / 2 - descriptionTextSpace;
        float right = left + maxWidth + descriptionTextSpace + descriptionTextSpace;
        float top = cy + descriptionMarginTop + circleStrokeWidth;
        float bottom = top + descriptionTextSpace + heights[0] + descriptionTextSpace + heights[1] + descriptionTextSpace;
        float moveX = 0, moveY = 0;
        //超出屏幕底部
        if (bottom > height) {
            moveY = -(bottom - top) - descriptionMarginTop - circleStrokeWidth - circleRadius - circleStrokeWidth - descriptionTextSpace;
        }
        //超出屏幕右边
        if (right > width) {
            moveX = -(right - width) - descriptionTextSpace * 3;
        }
        canvas.drawRoundRect(left + moveX, top + moveY, right + moveX, bottom + moveY, descriptionBackgroundRadius, descriptionBackgroundRadius, paint);
        canvas.drawText(topText, left + descriptionTextSpace + moveX, top + descriptionTextSpace + topBounds.height() + moveY, textPaint);
        canvas.drawText(bottomText, left + descriptionTextSpace + moveX, topBounds.height() + top + descriptionTextSpace * 2 + bottomBounds.height() + moveY, textPaint);
    }

    /**
     * @return 线条颜色
     */
    public int getMarkLineColor() {
        return markLineColor;
    }

    /**
     * 设置线条颜色
     * @param markLineColor
     */
    public void setMarkLineColor(int markLineColor) {
        this.markLineColor = markLineColor;
        invalidate();
    }

    /**
     * @return 线条宽度
     */
    public float getMarkLineStrokeWidth() {
        return markLineStrokeWidth;
    }

    /**
     * 设置线条宽度
     * @param markLineStrokeWidth
     */
    public void setMarkLineStrokeWidth(float markLineStrokeWidth) {
        this.markLineStrokeWidth = markLineStrokeWidth;
        invalidate();
    }

    /**
     * @return 字体颜色
     */
    public int getMarkTextColor() {
        return markTextColor;
    }

    /**
     * 设置字体颜色
     * @param markTextColor
     */
    public void setMarkTextColor(int markTextColor) {
        this.markTextColor = markTextColor;
        invalidate();
    }

    /**
     * @return 字体大小
     */
    public float getMarkTextSize() {
        return markTextSize;
    }

    /**
     * 设置字体大小
     * @param markTextSize
     */
    public void setMarkTextSize(float markTextSize) {
        this.markTextSize = markTextSize;
        invalidate();
    }

    /**
     * @return 折现颜色
     */
    public int getChartLintColor() {
        return chartLintColor;
    }

    /**
     * 设置折现颜色
     * @param chartLintColor
     */
    public void setChartLintColor(int chartLintColor) {
        this.chartLintColor = chartLintColor;
        invalidate();
    }

    /**
     * @return 折现填充颜色
     */
    public int getChartLintSolidColor() {
        return chartLintSolidColor;
    }

    /**
     * 设置折现填充颜色
     * @param chartLintSolidColor
     */
    public void setChartLintSolidColor(int chartLintSolidColor) {
        this.chartLintSolidColor = chartLintSolidColor;
        invalidate();
    }

    /**
     * @return 折现宽度
     */
    public float getChartLineStrokeWidth() {
        return chartLineStrokeWidth;
    }

    /**
     * 设置折现宽度
     * @param chartLineStrokeWidth
     */
    public void setChartLineStrokeWidth(float chartLineStrokeWidth) {
        this.chartLineStrokeWidth = chartLineStrokeWidth;
        invalidate();
    }

    /**
     * @return 刻度线左边间距
     */
    public float getTickMarkMarginLeft() {
        return tickMarkMarginLeft;
    }

    /**
     * 设置刻度线左边间距
     * @param tickMarkMarginLeft
     */
    public void setTickMarkMarginLeft(float tickMarkMarginLeft) {
        this.tickMarkMarginLeft = tickMarkMarginLeft;
        invalidate();
    }

    /**
     * @return 刻度线文字间距
     */
    public float getTickMarkTextMargin() {
        return tickMarkTextMargin;
    }

    /**
     * 设置刻度线文字间距
     * @param tickMarkTextMargin
     */
    public void setTickMarkTextMargin(float tickMarkTextMargin) {
        this.tickMarkTextMargin = tickMarkTextMargin;
        invalidate();
    }

    /**
     * @return 刻度线底部间距
     */
    public float getTickMarkMarginBottom() {
        return tickMarkMarginBottom;
    }

    /**
     * 设置刻度线底部间距
     * @param tickMarkMarginBottom
     */
    public void setTickMarkMarginBottom(float tickMarkMarginBottom) {
        this.tickMarkMarginBottom = tickMarkMarginBottom;
        invalidate();
    }

    /**
     * @return 刻度线右边间距
     */
    public float getTickMarkMarginRight() {
        return tickMarkMarginRight;
    }

    /**
     * 设置刻度线右边间距
     * @param tickMarkMarginRight
     */
    public void setTickMarkMarginRight(float tickMarkMarginRight) {
        this.tickMarkMarginRight = tickMarkMarginRight;
        invalidate();
    }

    /**
     * @return 刻度值宽度
     */
    public float getTickMarkWH() {
        return tickMarkWH;
    }

    /**
     * 设置刻度值宽度
     * @param tickMarkWH
     */
    public void setTickMarkWH(float tickMarkWH) {
        this.tickMarkWH = tickMarkWH;
        invalidate();
    }

    /**
     * @return 刻度线（垂直）段数
     */
    public int getTickMarkVerticalCount() {
        return tickMarkVerticalCount;
    }

    /**
     * 设置刻度线（垂直）段数
     * @param tickMarkVerticalCount
     */
    public void setTickMarkVerticalCount(int tickMarkVerticalCount) {
        this.tickMarkVerticalCount = tickMarkVerticalCount;
        invalidate();
    }

    /**
     * @return 刻度线（水平）段数
     */
    public int getTickMarkHorizontalCount() {
        return tickMarkHorizontalCount;
    }

    /**
     * 设置刻度线（水平）段数
     * @param tickMarkHorizontalCount
     */
    public void setTickMarkHorizontalCount(int tickMarkHorizontalCount) {
        this.tickMarkHorizontalCount = tickMarkHorizontalCount;
        invalidate();
    }

    /**
     * @return 垂直单位刻度值
     */
    public int getUnitVerticalTickMarkValue() {
        return unitVerticalTickMarkValue;
    }

    /**
     * 设置垂直单位刻度值
     * @param unitVerticalTickMarkValue
     */
    public void setUnitVerticalTickMarkValue(int unitVerticalTickMarkValue) {
        this.unitVerticalTickMarkValue = unitVerticalTickMarkValue;
        invalidate();
    }

    /**
     * @return 水平单位刻度值
     */
    public int getUnitHorizontalTickMarkValue() {
        return unitHorizontalTickMarkValue;
    }

    /**
     * 设置水平单位刻度值
     * @param unitHorizontalTickMarkValue
     */
    public void setUnitHorizontalTickMarkValue(int unitHorizontalTickMarkValue) {
        this.unitHorizontalTickMarkValue = unitHorizontalTickMarkValue;
        invalidate();
    }

    /**
     * @return 水平刻度开始值
     */
    public int getHorizontalTickMarkStartValue() {
        return horizontalTickMarkStartValue;
    }

    /**
     * 设置水平刻度开始值
     * @param horizontalTickMarkStartValue
     */
    public void setHorizontalTickMarkStartValue(int horizontalTickMarkStartValue) {
        this.horizontalTickMarkStartValue = horizontalTickMarkStartValue;
        invalidate();
    }

    /**
     * @return 数据源
     */
    public List<Double> getDatasource() {
        return data;
    }

    /**
     * @return 折现数据坐标
     */
    public List<Polyline> getCoordinates() {
        return coordinates;
    }

    /**
     * 设置折现数据坐标
     * @param coordinates
     */
    public void setCoordinates(List<Polyline> coordinates) {
        this.coordinates = coordinates;
        invalidate();
    }

    /**
     * @return 按下坐标的范围
     */
    public float getCoordinateDownRange() {
        return coordinateDownRange;
    }

    /**
     * 设置按下坐标的范围
     * @param coordinateDownRange
     */
    public void setCoordinateDownRange(float coordinateDownRange) {
        this.coordinateDownRange = coordinateDownRange;
        invalidate();
    }

    /**
     * @return 按钮的折现坐标
     */
    public Polyline getDownPolyline() {
        return downPolyline;
    }

    /**
     * 设置按钮的折现坐标
     * @param downPolyline
     */
    public void setDownPolyline(Polyline downPolyline) {
        this.downPolyline = downPolyline;
        invalidate();
    }

    /**
     * @return 圆点半径
     */
    public float getCircleRadius() {
        return circleRadius;
    }

    /**
     * 设置圆点半径
     * @param circleRadius
     */
    public void setCircleRadius(float circleRadius) {
        this.circleRadius = circleRadius;
        invalidate();
    }

    /**
     * @return 圆点外圈间距
     */
    public float getCircleStrokeWidth() {
        return circleStrokeWidth;
    }

    /**
     * 设置圆点外圈间距
     * @param circleStrokeWidth
     */
    public void setCircleStrokeWidth(float circleStrokeWidth) {
        this.circleStrokeWidth = circleStrokeWidth;
        invalidate();
    }

    /**
     * @return 圆点外圈颜色
     */
    public int getCircleStrokeColor() {
        return circleStrokeColor;
    }

    /**
     * 设置圆点外圈颜色
     * @param circleStrokeColor
     */
    public void setCircleStrokeColor(int circleStrokeColor) {
        this.circleStrokeColor = circleStrokeColor;
        invalidate();
    }

    /**
     * @return 圆点填充颜色
     */
    public int getCircleSolidColor() {
        return circleSolidColor;
    }

    /**
     * 设置圆点填充颜色
     * @param circleSolidColor
     */
    public void setCircleSolidColor(int circleSolidColor) {
        this.circleSolidColor = circleSolidColor;
        invalidate();
    }

    /**
     * @return 描述信息背景颜色
     */
    public int getDescriptionBackgroundColor() {
        return descriptionBackgroundColor;
    }

    /**
     * 设置描述信息背景颜色
     * @param descriptionBackgroundColor
     */
    public void setDescriptionBackgroundColor(int descriptionBackgroundColor) {
        this.descriptionBackgroundColor = descriptionBackgroundColor;
        invalidate();
    }

    /**
     * @return 描述信息背景圆角大小
     */
    public float getDescriptionBackgroundRadius() {
        return descriptionBackgroundRadius;
    }

    /**
     * 设置描述信息背景圆角大小
     * @param descriptionBackgroundRadius
     */
    public void setDescriptionBackgroundRadius(float descriptionBackgroundRadius) {
        this.descriptionBackgroundRadius = descriptionBackgroundRadius;
        invalidate();
    }

    /**
     * @return 描述信息文字大小
     */
    public float getDescriptionTextSize() {
        return descriptionTextSize;
    }

    /**
     * 设置描述信息文字大小
     * @param descriptionTextSize
     */
    public void setDescriptionTextSize(float descriptionTextSize) {
        this.descriptionTextSize = descriptionTextSize;
        invalidate();
    }

    /**
     * @return 描述信息文字垂直间距
     */
    public float getDescriptionTextSpace() {
        return descriptionTextSpace;
    }

    /**
     * 设置描述信息文字垂直间距
     * @param descriptionTextSpace
     */
    public void setDescriptionTextSpace(float descriptionTextSpace) {
        this.descriptionTextSpace = descriptionTextSpace;
        invalidate();
    }

    /**
     * @return 描述信息文字颜色
     */
    public int getDescriptionTextColor() {
        return descriptionTextColor;
    }

    /**
     * 设置描述信息文字颜色
     * @param descriptionTextColor
     */
    public void setDescriptionTextColor(int descriptionTextColor) {
        this.descriptionTextColor = descriptionTextColor;
        invalidate();
    }

    /**
     * @return 描述信息面板宽度
     */
    public float getDescriptionWidth() {
        return descriptionWidth;
    }

    /**
     * 设置描述信息面板宽度
     * @param descriptionWidth
     */
    public void setDescriptionWidth(float descriptionWidth) {
        this.descriptionWidth = descriptionWidth;
        invalidate();
    }

    /**
     * @return 描述信息面板高度
     */
    public float getDescriptionHeight() {
        return descriptionHeight;
    }

    /**
     * 设置描述信息面板高度
     * @param descriptionHeight
     */
    public void setDescriptionHeight(float descriptionHeight) {
        this.descriptionHeight = descriptionHeight;
        invalidate();
    }

    /**
     * @return 描述信息面板距离上方间距
     */
    public float getDescriptionMarginTop() {
        return descriptionMarginTop;
    }

    /**
     * 设置描述信息面板距离上方间距
     * @param descriptionMarginTop
     */
    public void setDescriptionMarginTop(float descriptionMarginTop) {
        this.descriptionMarginTop = descriptionMarginTop;
        invalidate();
    }

    /**
     * @return 描述信息面板 - x轴名称
     */
    public String getDescriptionXName() {
        return descriptionXName;
    }

    /**
     * 设置描述信息面板 - x轴名称
     * @param descriptionXName
     */
    public void setDescriptionXName(String descriptionXName) {
        this.descriptionXName = descriptionXName;
        invalidate();
    }

    /**
     * @return 描述信息面板 - x轴单位
     */
    public String getDescriptionXUnit() {
        return descriptionXUnit;
    }

    /**
     * 描述信息面板 - x轴单位
     * @param descriptionXUnit
     */
    public void setDescriptionXUnit(String descriptionXUnit) {
        this.descriptionXUnit = descriptionXUnit;
        invalidate();
    }

    /**
     * @return 描述信息面板 - y轴名称
     */
    public String getDescriptionYName() {
        return descriptionYName;
    }

    /**
     * 设置描述信息面板 - y轴名称
     * @param descriptionYName
     */
    public void setDescriptionYName(String descriptionYName) {
        this.descriptionYName = descriptionYName;
        invalidate();
    }

    /**
     * @return 描述信息面板 - y轴单位
     */
    public String getDescriptionYUnit() {
        return descriptionYUnit;
    }

    /**
     * 设置描述信息面板 - y轴单位
     * @param descriptionYUnit
     */
    public void setDescriptionYUnit(String descriptionYUnit) {
        this.descriptionYUnit = descriptionYUnit;
        invalidate();
    }

}

