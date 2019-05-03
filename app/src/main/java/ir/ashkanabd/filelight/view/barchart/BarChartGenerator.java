package ir.ashkanabd.filelight.view.barchart;

import android.graphics.Color;
import android.widget.RelativeLayout;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import ir.ashkanabd.filelight.R;
import ir.ashkanabd.filelight.ScanActivity;
import ir.ashkanabd.filelight.storage.explore.Node;
import ir.ashkanabd.filelight.view.MeasUtils;

public class BarChartGenerator {
    private BarChart barChart;
    private Node currentNode;
    private BarChartClickListener barChartClickListener;
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
        StorageBarChartRenderer renderer = new StorageBarChartRenderer(barChart, barChart.getAnimator()
                , barChart.getViewPortHandler(), scanActivity);
        renderer.setBarChartClickListener(barChartClickListener);

        barChart.setRenderer(renderer);
        barChart.getLegend().setEnabled(false);
        barChart.setData(barData);

        XAxis xAxis = barChart.getXAxis();
        xAxis.setLabelRotationAngle(-40);
        xAxis.setLabelCount(entryList.size(), false);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setValueFormatter(new StorageBarXValueFormatter(entryList));

        barChart.getAxisRight().setEnabled(false);

        YAxis yAxis = barChart.getAxisLeft();
        yAxis.setValueFormatter(new StorageBarYValueFormatter());
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
            hidden.setLabel(".Hidden");
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
                storageEntry.setLabel(entry.getKey().getFile().getName());
                entryList.add(storageEntry);
            } else {
                other.setY(other.getY() + entry.getValue());
                other.addNode(entry.getKey());
            }
        }
        if (other.getY() != 0) {
            other.setLabel("Other");
            entryList.add(other);
        }
        Collections.sort(entryList, (o1, o2) -> Float.compare(o1.getY(), o2.getY()));
        return entryList;
    }

    private BarChart createChart() {
        BarChart barChart = new BarChart(scanActivity);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(-1, -2);
        int margin = MeasUtils.dpToPx(10, scanActivity);
        params.setMargins(0, margin, 0, MeasUtils.dpToPx(20, scanActivity));
        params.setMarginEnd(MeasUtils.dpToPx(10, scanActivity));
        params.setMarginStart(MeasUtils.dpToPx(10, scanActivity));
        params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
        params.addRule(RelativeLayout.BELOW, R.id.chart_mode_spinner);
        params.addRule(RelativeLayout.ABOVE, R.id.open_dir_btn);
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

    public BarChart getBarChart() {
        return barChart;
    }

    public Node getCurrentNode() {
        return currentNode;
    }

    public void setCurrentNode(Node currentNode) {
        this.currentNode = currentNode;
    }

    public void setBarChartClickListener(BarChartClickListener barChartClickListener) {
        this.barChartClickListener = barChartClickListener;
    }
}
