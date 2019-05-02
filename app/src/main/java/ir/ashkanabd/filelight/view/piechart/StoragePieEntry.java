package ir.ashkanabd.filelight.view.piechart;

import com.github.mikephil.charting.data.PieEntry;

import java.util.ArrayList;
import java.util.List;

import ir.ashkanabd.filelight.storage.explore.Node;

public class StoragePieEntry extends PieEntry {
    private int storageType;
    private Node node;
    private List<Node> nodeList;

    public StoragePieEntry(float value, String label) {
        super(value, label);
    }

    @Override
    public String getLabel() {
        return super.getLabel();
    }

    public int getStorageType() {
        return storageType;
    }

    public void addNode(Node node) {
        if (nodeList == null) {
            nodeList = new ArrayList<>();
        }
        nodeList.add(node);
    }

    public List<Node> getNodeList() {
        return nodeList;
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
