package ir.ashkanabd.filelight.view.barchart;

import android.graphics.Canvas;

import com.github.mikephil.charting.animation.ChartAnimator;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.dataprovider.BarDataProvider;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.renderer.BarChartRenderer;
import com.github.mikephil.charting.utils.ViewPortHandler;

import ir.ashkanabd.filelight.storage.Storage;

public class StorageBarChartRenderer extends BarChartRenderer {
    public StorageBarChartRenderer(BarDataProvider chart, ChartAnimator animator, ViewPortHandler viewPortHandler) {
        super(chart, animator, viewPortHandler);
    }

    @Override
    public void drawData(Canvas c) {
        super.drawData(c);
    }

    @Override
    protected void drawDataSet(Canvas c, IBarDataSet dataSet, int index) {
        super.drawDataSet(c, dataSet, index);
    }

    @Override
    public void drawValues(Canvas c) {
        super.drawValues(c);
    }

    @Override
    public void drawValue(Canvas c, String valueText, float x, float y, int color) {
        valueText = valueText.replace(",", "");
        valueText = Storage.getInBestFormat(Double.parseDouble(valueText));
        super.drawValue(c, valueText, x, y, color);
    }

    @Override
    public void drawHighlighted(Canvas c, Highlight[] indices) {
        super.drawHighlighted(c, indices);
    }

    @Override
    public void drawExtras(Canvas c) {
        super.drawExtras(c);
    }
}
