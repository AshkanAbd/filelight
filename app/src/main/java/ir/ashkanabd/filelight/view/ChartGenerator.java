package ir.ashkanabd.filelight.view;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import ir.ashkanabd.filelight.ScanActivity;
import ir.ashkanabd.filelight.storage.explore.Node;

public abstract class ChartGenerator {
    protected ScanActivity scanActivity;

    protected ChartGenerator(ScanActivity scanActivity) {
        this.scanActivity = scanActivity;
    }

    protected LinkedHashMap<Node, Long> getChildrenMap(List<Node> nodeList) {
        LinkedHashMap<Node, Long> map = new LinkedHashMap<>();
        for (Node node : nodeList) {
            map.put(node, node.getLength());
        }
        List<Map.Entry<Node, Long>> entryList = new ArrayList<>(map.entrySet());
        Collections.sort(entryList, (o1, o2) -> Long.compare(o2.getValue(), o1.getValue()));
        map.clear();
        for (Map.Entry<Node, Long> entry : entryList) {
            map.put(entry.getKey(), entry.getValue());
        }
        return map;
    }
}
