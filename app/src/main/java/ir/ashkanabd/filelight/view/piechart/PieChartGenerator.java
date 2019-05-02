package ir.ashkanabd.filelight.view.piechart;

import android.graphics.Color;
import android.widget.RelativeLayout;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import ir.ashkanabd.filelight.R;
import ir.ashkanabd.filelight.ScanActivity;
import ir.ashkanabd.filelight.storage.Storage;
import ir.ashkanabd.filelight.storage.explore.Node;
import ir.ashkanabd.filelight.view.MeasUtils;

public class PieChartGenerator {
    private PieChart pieChart;
    private Node currentNode;
    private PieChartClickListener pieChartClickListener;
    private ScanActivity scanActivity;

    public PieChartGenerator(ScanActivity scanActivity, Node currentNode) {
        this.scanActivity = scanActivity;
        this.currentNode = currentNode;
    }

    public void setupPieChart(List<Node> nodeList) {
        Runtime.getRuntime().gc();
        List<PieEntry> entryList = createEntryList(nodeList);
        PieDataSet pieDataSet = new PieDataSet(entryList, null);
        pieDataSet.setColors(Color.parseColor("#408AF8"), Color.parseColor("#D8433C")
                , Color.parseColor("#F2AF3A"), Color.parseColor("#279B5E"));
        Collections.shuffle(pieDataSet.getColors());
        pieDataSet.setSliceSpace(2);
        pieDataSet.setValueTextColor(Color.WHITE);
        pieDataSet.setValueTextSize(15);
        pieDataSet.setSelectionShift(10f);
        pieDataSet.setAutomaticallyDisableSliceSpacing(true);

        PieData pieData = new PieData(pieDataSet);

        pieChart = createChart();
        StoragePieChartRenderer renderer = new StoragePieChartRenderer(pieChart, pieChart.getAnimator(), pieChart.getViewPortHandler(), scanActivity);
        renderer.setPieChartClickListener(pieChartClickListener);
        pieChart.setRenderer(renderer);
        pieChart.getLegend().setEnabled(false);
        pieChart.setData(pieData);
        pieChart.setCenterTextSize(15);
        pieChart.setCenterText(currentNode.getFile().getAbsolutePath() + "\n\nsize: " + Storage.getInBestFormat(currentNode.getLength()));
    }

    private List<PieEntry> createEntryList(List<Node> nodeList) {
        StoragePieEntry hidden = new StoragePieEntry(0, ".Hidden");
        List<Node> removeList = new ArrayList<>();
        List<PieEntry> entryList = new ArrayList<>();
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
        StoragePieEntry other = new StoragePieEntry(0, "Other");
        int left = 7 - entryList.size();
        for (Map.Entry<Node, Long> entry : childrenMap.entrySet()) {
            if (entry.getKey().getFile().isFile()) continue;
            if (left != 0) {
                left--;
                StoragePieEntry storageEntry = new StoragePieEntry(entry.getValue(), entry.getKey().getFile().getName());
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

    private PieChart createChart() {
        PieChart pieChart = new PieChart(scanActivity);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(-1, -1);
        int margin = MeasUtils.pxToDp(30, scanActivity);
        params.setMargins(margin, margin, margin, margin);
        params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
        params.addRule(RelativeLayout.BELOW, R.id.chart_mode_spinner);
        params.addRule(RelativeLayout.ABOVE, R.id.open_dir_btn);
        pieChart.setLayoutParams(params);
        scanActivity.getMainLayout().addView(pieChart);
        return pieChart;
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

    public PieChart getPieChart() {
        return pieChart;
    }

    public Node getCurrentNode() {
        return currentNode;
    }

    public void setCurrentNode(Node currentNode) {
        this.currentNode = currentNode;
    }

    public void setPieChartClickListener(PieChartClickListener pieChartClickListener) {
        this.pieChartClickListener = pieChartClickListener;
    }
}
