package ir.ashkanabd.filelight.view.barchart;

import android.graphics.Canvas;

import com.github.mikephil.charting.animation.ChartAnimator;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.dataprovider.BarDataProvider;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.renderer.BarChartRenderer;
import com.github.mikephil.charting.utils.ViewPortHandler;

import ir.ashkanabd.filelight.ScanActivity;
import ir.ashkanabd.filelight.storage.Storage;

public class StorageBarChartRenderer extends BarChartRenderer {
    private BarChartClickListener clickListener;
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

    }

    @Override
    public void drawValue(Canvas c, String valueText, float x, float y, int color) {
        valueText = valueText.replace(",", "");
        valueText = Storage.getInBestFormat(Double.parseDouble(valueText));
        super.drawValue(c, valueText, x, y, color);
    }

    @Override
    public void drawExtras(Canvas c) {
        super.drawExtras(c);
    }

    public void setClickListener(BarChartClickListener clickListener) {
        this.clickListener = clickListener;
    }
}
