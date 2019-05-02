package ir.ashkanabd.filelight.view.barchart;

import com.github.mikephil.charting.data.BarEntry;

import java.util.ArrayList;
import java.util.List;

import ir.ashkanabd.filelight.storage.explore.Node;

public class StorageBarEntry extends BarEntry {
    private int storageType;
    private Node node;
    private List<Node> nodeList;


    public StorageBarEntry(float x, float y) {
        super(x, y);
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
