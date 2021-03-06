package com.coen390.abreath.ui.home;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.RectF;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.coen390.abreath.common.Utility;
import com.github.mikephil.charting.animation.ChartAnimator;
import com.github.mikephil.charting.buffer.BarBuffer;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.highlight.Range;
import com.github.mikephil.charting.interfaces.dataprovider.BarDataProvider;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.model.GradientColor;
import com.github.mikephil.charting.renderer.BarChartRenderer;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.Transformer;
import com.github.mikephil.charting.utils.Utils;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * Overrides the current Rendering class used the Horizontal chart
 * Renders curved horizontal bars and manages the state of bars when clicked
 */
public class RoundBarRender extends BarChartRenderer {
    private final RectF barShadowRectBuffer = new RectF();

    private static List<Integer> mArrayGradient;


    public float getThreashold() {
        return m_threashold;
    }

    public void setThreashold(float m_threashold) {
        this.m_threashold = m_threashold;

    }

    private float m_threashold;

    /**
     * Initializes the BarRenderer and sets the gradient colors used for horizontal bars
     */
    public RoundBarRender(BarDataProvider chart, ChartAnimator animator, ViewPortHandler viewPortHandler){
        super(chart, animator, viewPortHandler);
        mArrayGradient = new ArrayList<>();
        Collections.addAll(mArrayGradient, Color.rgb(25, 156, 23), Color.rgb(99, 137, 17), Color.rgb(145, 125, 5), Color.rgb(173, 117,1), Color.rgb(155, 90, 3), Color.rgb(136, 54, 6), Color.rgb(100, 10, 10));
        m_threashold = 0.08f;
    }

    /**
     * Both draw function overrides rendering of the graph when clicked and rendered the first time.
     * Implementation based on https://gist.github.com/xanscale/e971cc4f2f0712a8a3bcc35e85325c27
     */

    @Override
    public void drawHighlighted(Canvas c, Highlight[] indices) {

        BarData barData = mChart.getBarData();
        float m_radius = mChart.getBarData().getBarWidth()/2 * 100;


        for (Highlight high : indices) {

            IBarDataSet set = barData.getDataSetByIndex(high.getDataSetIndex());

            if (set == null || !set.isHighlightEnabled())
                continue;

            BarEntry e = set.getEntryForXValue(high.getX(), high.getY());

            if (!isInBoundsX(e, set))
                continue;

            Transformer trans = mChart.getTransformer(set.getAxisDependency());

            mHighlightPaint.setColor(set.getHighLightColor());
            mHighlightPaint.setAlpha(set.getHighLightAlpha());

            boolean isStack = high.getStackIndex() >= 0 && e.isStacked();

            final float y1;
            final float y2;

            if (isStack) {

                if (mChart.isHighlightFullBarEnabled()) {

                    y1 = e.getPositiveSum();
                    y2 = -e.getNegativeSum();

                } else {
                    Range range = e.getRanges()[high.getStackIndex()];

                    y1 = range.from;
                    y2 = range.to;
                }

            } else {
                y1 = e.getY();
                y2 = 0.f;
            }


            prepareBarHighlight(e.getX(), y1, y2, barData.getBarWidth() / 2f, trans);

            setHighlightDrawPos(high, mBarRect);

            c.drawRoundRect(mBarRect, m_radius, m_radius, mHighlightPaint);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void drawDataSet(Canvas c, IBarDataSet dataSet, int index) {
        Transformer trans = mChart.getTransformer(dataSet.getAxisDependency());

        float m_radius = mChart.getBarData().getBarWidth()/2 * 100;



//        ((BarDataSet) dataSet).setColor(ColorTemplate.rgb("#0288d1"));
        mBarBorderPaint.setColor(dataSet.getBarBorderColor());
        mBarBorderPaint.setStrokeWidth(Utils.convertDpToPixel(dataSet.getBarBorderWidth()));

        final boolean drawBorder = dataSet.getBarBorderWidth() > 0.f;

        float phaseX = mAnimator.getPhaseX();
        float phaseY = mAnimator.getPhaseY();

        // initialize the buffer
        BarBuffer buffer = mBarBuffers[index];
        buffer.setPhases(phaseX, phaseY);
        buffer.setDataSet(index);
        buffer.setInverted(mChart.isInverted(dataSet.getAxisDependency()));
        buffer.setBarWidth(mChart.getBarData().getBarWidth());

        buffer.feed(dataSet);

        trans.pointValuesToPixel(buffer.buffer);

        mRenderPaint.setColor(Color.rgb(2, 136, 206));


        for (int j = 0; j < buffer.size(); j += 4) {

            if (!mViewPortHandler.isInBoundsLeft(buffer.buffer[j + 2]))
                continue;

            if (!mViewPortHandler.isInBoundsRight(buffer.buffer[j]))
                break;

                // Set the color for the currently drawn value. If the index
                // is out of bounds, reuse colors.
                BarEntry e = dataSet.getEntryForIndex(j/4);
                float y = e.getY();

                /*
                Sets color based on y-value
                 */

                mRenderPaint.setColor(mArrayGradient.get((int)Utility.map(y, m_threashold* 0.4f,m_threashold*0.7f,0,mArrayGradient.size()-1)) );

//            if(y > m_threashold/2){
//                    mRenderPaint.setColor(Color.rgb((int)Utility.map(y, m_threashold* 0.2f,m_threashold*0.8f,2,209), 136, 206));

//            }else{

//                mRenderPaint.setColor(Color.rgb(2, 136, 206));
//            }

            c.drawRoundRect(buffer.buffer[j], buffer.buffer[j + 1], buffer.buffer[j + 2],
                    buffer.buffer[j + 3], m_radius, m_radius, mRenderPaint);

            if (drawBorder) {
                c.drawRoundRect(buffer.buffer[j], buffer.buffer[j + 1], buffer.buffer[j + 2],
                        buffer.buffer[j + 3], m_radius,m_radius, mBarBorderPaint);
            }
        }
    }
}
