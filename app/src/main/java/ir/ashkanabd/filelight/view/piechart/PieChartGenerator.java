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
import ir.ashkanabd.filelight.view.ChartGenerator;

public class PieChartGenerator extends ChartGenerator {
    private PieChart pieChart;
    private PieChartClickListener pieChartClickListener;

    public PieChartGenerator(ScanActivity scanActivity, Node currentNode) {
        super(scanActivity, currentNode);
    }

    public void setupPieChart(List<Node> nodeList, boolean showHidden) {
        Runtime.getRuntime().gc();
        List<PieEntry> entryList = createEntryList(nodeList, showHidden);
        PieDataSet pieDataSet = new PieDataSet(entryList, null);
        pieDataSet.setColors(Color.parseColor("#408AF8"), Color.parseColor("#D8433C")
                , Color.parseColor("#F2AF3A"), Color.parseColor("#279B5E"));
        Collections.shuffle(pieDataSet.getColors());
        pieDataSet.setSliceSpace(2);
        pieDataSet.setValueTextColor(Color.DKGRAY);
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

    private List<PieEntry> createEntryList(List<Node> nodeList, boolean showHidden) {
        List<Node> removeList = new ArrayList<>();
        List<PieEntry> entryList = new ArrayList<>();
        List<Node> notHiddenList = new ArrayList<>(nodeList);
        if (!showHidden) {
            StoragePieEntry hidden = new StoragePieEntry(0, ".Hidden");
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
            notHiddenList.removeAll(removeList);
        }
        LinkedHashMap<Node, Long> childrenMap = getChildrenMap(notHiddenList);
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
        params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
        params.addRule(RelativeLayout.BELOW, R.id.chart_mode_spinner);
        params.addRule(RelativeLayout.ABOVE, R.id.open_dir_btn);
        pieChart.setLayoutParams(params);
        scanActivity.getMainLayout().addView(pieChart);
        pieChart.setDescription(null);
        return pieChart;
    }

    public PieChart getPieChart() {
        return pieChart;
    }

    public void setPieChartClickListener(PieChartClickListener pieChartClickListener) {
        this.pieChartClickListener = pieChartClickListener;
    }
}
