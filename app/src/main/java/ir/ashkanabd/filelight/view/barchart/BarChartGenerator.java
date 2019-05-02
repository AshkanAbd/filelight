package ir.ashkanabd.filelight.view.barchart;

import android.graphics.Color;
import android.widget.RelativeLayout;

import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.renderer.BarChartRenderer;
import com.github.mikephil.charting.renderer.XAxisRenderer;
import com.github.mikephil.charting.utils.Transformer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import ir.ashkanabd.filelight.ScanActivity;
import ir.ashkanabd.filelight.storage.explore.Node;
import ir.ashkanabd.filelight.view.ChartClickListener;
import ir.ashkanabd.filelight.view.MeasUtils;

public class BarChartGenerator {
    private StorageBarChart barChart;
    private Node currentNode;
    private ChartClickListener chartClickListener;
    private ScanActivity scanActivity;

    public BarChartGenerator(ScanActivity scanActivity, Node currentNode) {
        this.scanActivity = scanActivity;
        this.currentNode = currentNode;
    }

    public void setupBarChart(List<Node> nodeList) {
        Runtime.getRuntime().gc();
        List<BarEntry> entryList = createEntryList(nodeList);
        for (int i = 0; i < entryList.size(); i++) {
            entryList.get(i).setX(i * 2);
        }
        BarDataSet barDataSet = new BarDataSet(entryList, null);
        barDataSet.setColors(Color.parseColor("#408AF8"), Color.parseColor("#D8433C")
                , Color.parseColor("#F2AF3A"), Color.parseColor("#279B5E"));
        Collections.shuffle(barDataSet.getColors());
        barDataSet.setValueTextColor(Color.BLACK);
        barDataSet.setValueTextSize(10);

        BarData barData = new BarData(barDataSet);

        barChart = createChart();
        BarChartRenderer renderer = new BarStorageRenderer(barChart, barChart.getAnimator(), barChart.getViewPortHandler());
        barChart.setRenderer(renderer);
        barChart.getLegend().setEnabled(false);
        barChart.setData(barData);
        XAxisRenderer xAxisRenderer = new XAxisRenderer(barChart.getViewPortHandler(), new XAxis(), new Transformer(barChart.getViewPortHandler()));
        barChart.setXAxisRenderer(xAxisRenderer);

    }

    private List<BarEntry> createEntryList(List<Node> nodeList) {
        StorageBarEntry hidden = new StorageBarEntry(0, 0);
        List<Node> removeList = new ArrayList<>();
        List<BarEntry> entryList = new ArrayList<>();
        for (Node node : nodeList) {
            if (node.getFile().isHidden() && node.getFile().isDirectory()) {
                hidden.setY(hidden.getY() + node.getLength());
                hidden.addNode(node);
                removeList.add(node);
            }
        }
        if (hidden.getY() != 0) {
            entryList.add(hidden);
        }
        nodeList.removeAll(removeList);
        LinkedHashMap<Node, Long> childrenMap = getChildrenMap(nodeList);
        StorageBarEntry other = new StorageBarEntry(0, 0);
        int left = 7 - entryList.size();
        for (Map.Entry<Node, Long> entry : childrenMap.entrySet()) {
            if (entry.getKey().getFile().isFile()) continue;
            if (left != 0) {
                left--;
                StorageBarEntry storageEntry = new StorageBarEntry(0, entry.getValue());
                storageEntry.setNode(entry.getKey());
                entryList.add(storageEntry);
            } else {
                other.setY(other.getY() + entry.getValue());
                other.addNode(entry.getKey());
            }
        }
        if (other.getY() != 0) {
            entryList.add(other);
        }
        Collections.sort(entryList, (o1, o2) -> Float.compare(o1.getY(), o2.getY()));
        return entryList;
    }

    private StorageBarChart createChart() {
        StorageBarChart barChart = new StorageBarChart(scanActivity);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(-1, -1);
        int margin = MeasUtils.pxToDp(30, scanActivity);
        params.setMargins(margin, margin, margin, margin);
        params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
        barChart.setLayoutParams(params);
        scanActivity.getMainLayout().addView(barChart);
        return barChart;
    }

    private LinkedHashMap<Node, Long> getChildrenMap(List<Node> nodeList) {
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

    public StorageBarChart getBarChart() {
        return barChart;
    }

    public Node getCurrentNode() {
        return currentNode;
    }

    public void setCurrentNode(Node currentNode) {
        this.currentNode = currentNode;
    }

    public void setChartClickListener(ChartClickListener chartClickListener) {
        this.chartClickListener = chartClickListener;
    }
}
