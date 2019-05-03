package ir.ashkanabd.filelight.view.barchart;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.util.List;

public class StorageBarXValueFormatter extends ValueFormatter {
    private List<BarEntry> entryList;

    public StorageBarXValueFormatter(List<BarEntry> entryList) {
        super();
        this.entryList = entryList;
    }

    @Override
    public String getAxisLabel(float value, AxisBase axis) {
        StorageBarEntry entry = (StorageBarEntry) entryList.get((int) (value / 2));
        return entry.getLabel();
    }
}
