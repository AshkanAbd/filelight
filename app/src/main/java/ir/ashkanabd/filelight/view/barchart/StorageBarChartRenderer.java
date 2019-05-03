package ir.ashkanabd.filelight.view.barchart;

import android.graphics.Canvas;

import com.github.mikephil.charting.animation.ChartAnimator;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.highlight.Range;
import com.github.mikephil.charting.interfaces.dataprovider.BarDataProvider;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.renderer.BarChartRenderer;
import com.github.mikephil.charting.utils.Transformer;
import com.github.mikephil.charting.utils.ViewPortHandler;

import ir.ashkanabd.filelight.ScanActivity;
import ir.ashkanabd.filelight.storage.Storage;

public class StorageBarChartRenderer extends BarChartRenderer {
    private BarChartClickListener barChartClickListener;
    private ScanActivity scanActivity;
    private BarEntry currentEntry;

    public StorageBarChartRenderer(BarDataProvider chart, ChartAnimator animator, ViewPortHandler viewPortHandler, ScanActivity scanActivity) {
        super(chart, animator, viewPortHandler);
        this.scanActivity = scanActivity;
    }

    @Override
    public void drawData(Canvas c) {
        super.drawData(c);
    }

    @Override
    public void drawValues(Canvas c) {
        super.drawValues(c);
    }

    @Override
    protected void drawDataSet(Canvas c, IBarDataSet dataSet, int index) {
        if (scanActivity.getSelectedNode() != null) {
            scanActivity.setSelectedNode(null);
        }
        super.drawDataSet(c, dataSet, index);
    }

    @Override
    public void drawHighlighted(Canvas c, Highlight[] indices) {
        mDrawHighlighted(c, indices);
        StorageBarEntry barEntry = (StorageBarEntry) currentEntry;
        barChartClickListener.onChartClicked(barEntry);
    }

    private void mDrawHighlighted(Canvas c, Highlight[] indices) {
        BarData barData = mChart.getBarData();

        for (Highlight high : indices) {

            IBarDataSet set = barData.getDataSetByIndex(high.getDataSetIndex());

            if (set == null || !set.isHighlightEnabled())
                continue;

            BarEntry e = set.getEntryForXValue(high.getX(), high.getY());
            currentEntry = e;

            if (!isInBoundsX(e, set))
                continue;

            Transformer trans = mChart.getTransformer(set.getAxisDependency());

            mHighlightPaint.setColor(set.getHighLightColor());
            mHighlightPaint.setAlpha(set.getHighLightAlpha());

            boolean isStack = (high.getStackIndex() >= 0 && e.isStacked()) ? true : false;

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

            c.drawRect(mBarRect, mHighlightPaint);
        }

    }

    @Override
    public void drawValue(Canvas c, String valueText, float x, float y, int color) {
        valueText = valueText.replace(",", "");
        valueText = valueText.replace("٬", "");
        valueText = valueText.replace("۱", "1");
        valueText = valueText.replace("۲", "2");
        valueText = valueText.replace("۳", "3");
        valueText = valueText.replace("۴", "4");
        valueText = valueText.replace("۵", "5");
        valueText = valueText.replace("۶", "6");
        valueText = valueText.replace("۷", "7");
        valueText = valueText.replace("۸", "8");
        valueText = valueText.replace("۹", "9");
        valueText = valueText.replace("۰", "0");
        valueText = Storage.getInBestFormat(Double.parseDouble(valueText));
        super.drawValue(c, valueText, x, y, color);
    }

    @Override
    public void drawExtras(Canvas c) {
        super.drawExtras(c);
    }

    public void setBarChartClickListener(BarChartClickListener barChartClickListener) {
        this.barChartClickListener = barChartClickListener;
    }
}
