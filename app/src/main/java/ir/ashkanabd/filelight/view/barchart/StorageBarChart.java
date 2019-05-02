package ir.ashkanabd.filelight.view.barchart;

import android.content.Context;
import android.util.AttributeSet;

import com.github.mikephil.charting.charts.BarChart;

public class StorageBarChart extends BarChart {
    public StorageBarChart(Context context) {
        super(context);
    }

    public StorageBarChart(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public StorageBarChart(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    /**
     * Allows the view to resize
     * {@inheritDoc}
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        setMeasuredDimension(width, width); //setting height same as width here
    }
}
