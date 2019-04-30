package ir.ashkanabd.filelight.view;

import com.github.mikephil.charting.data.PieEntry;

public class StorageEntry extends PieEntry {
    private int storageType;

    public StorageEntry(float value, String label) {
        super(value, label);
    }

    @Override
    public String getLabel() {
        return super.getLabel();
    }

    public int getStorageType() {
        return storageType;
    }

    public void setStorageType(int storageType) {
        this.storageType = storageType;
    }
}
