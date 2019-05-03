package ir.ashkanabd.filelight.view.barchart;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.ValueFormatter;

import ir.ashkanabd.filelight.storage.Storage;

public class StorageBarYValueFormatter extends ValueFormatter {

    @Override
    public String getAxisLabel(float value, AxisBase axis) {
        return Storage.getInBestFormat(value);
    }
}
