package ir.ashkanabd.filelight.view;

import com.github.mikephil.charting.data.PieEntry;

import ir.ashkanabd.filelight.storage.Storage;
import ir.ashkanabd.filelight.storage.explore.Node;

public class StorageEntry extends PieEntry {
    private int storageType;
    private Node node;

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

    public Node getNode() {
        return node;
    }

    public void setNode(Node node) {
        this.node = node;
    }
}
