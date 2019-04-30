package ir.ashkanabd.filelight;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import es.dmoral.toasty.Toasty;
import ir.ashkanabd.filelight.background.BackgroundTask;
import ir.ashkanabd.filelight.storage.Storage;
import ir.ashkanabd.filelight.storage.explore.Explorer;
import ir.ashkanabd.filelight.storage.explore.Node;
import ir.ashkanabd.filelight.view.MeasUtils;
import ir.ashkanabd.filelight.view.StorageEntry;
import ir.ashkanabd.filelight.view.StoragePieChart;
import ir.ashkanabd.filelight.view.StorageRenderer;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

import com.afollestad.materialdialogs.MaterialDialog;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.Utils;


import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ScanActivity extends AppCompatActivity {

    private Storage storage;
    private MaterialDialog loadingDialog;
    private RelativeLayout mainLayout;
    private StoragePieChart pieChart;
    private Node rootNode;
    private Node currentNode;
    private Node selectedNode;
    private boolean isOther = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scan_activity);
        BackgroundTask backgroundTask = new BackgroundTask();
        backgroundTask.setTaskExecute(_1 -> startTask());
        backgroundTask.setPostExecute(_1 -> postExecute());
        backgroundTask.setPreExecute(this::preExecute);
        backgroundTask.execute();
    }

    private void preExecute() {
        storage = (Storage) Objects.requireNonNull(getIntent().getExtras()).get("storage");
        findViews();
        setupMaterialDialog();
    }

    private void setupMaterialDialog() {
        loadingDialog = new MaterialDialog(this);
        loadingDialog.setContentView(R.layout.scan_loading);
        loadingDialog.setCancelable(false);
        loadingDialog.show();
    }

    private void findViews() {
        mainLayout = findViewById(R.id.scan_main_layout);
    }

    private Void startTask() {
        Explorer explorer = new Explorer(storage);
        explorer.startExploring();
        rootNode = explorer.getRoot().getChildren().get(0);
        try {
            Thread.sleep(500);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void postExecute() {
        loadingDialog.dismiss();
        Utils.init(this);
        currentNode = rootNode;
        setupChart(rootNode.getChildren());
    }

    private void setupChart(List<Node> nodeList) {
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
        StorageRenderer renderer = new StorageRenderer(pieChart, pieChart.getAnimator(), pieChart.getViewPortHandler(), this);
        renderer.setChartClickListener(this::onChartClicked);
        pieChart.setRenderer(renderer);
        pieChart.getLegend().setEnabled(false);
        pieChart.setData(pieData);
        pieChart.setCenterTextSize(15);
        pieChart.setCenterText(currentNode.getFile().getAbsolutePath() + "\n\nsize: " + Storage.getInBestFormat(currentNode.getLength()));
    }

    private List<PieEntry> createEntryList(List<Node> nodeList) {
        StorageEntry hidden = new StorageEntry(0, ".Hidden");
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
        StorageEntry other = new StorageEntry(0, "Other");
        int left = 7 - entryList.size();
        for (Map.Entry<Node, Long> entry : childrenMap.entrySet()) {
            if (entry.getKey().getFile().isFile()) continue;
            if (left != 0) {
                left--;
                StorageEntry storageEntry = new StorageEntry(entry.getValue(), entry.getKey().getFile().getName());
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

    private void onChartClicked(StorageEntry storageEntry) {
        if (storageEntry.getNode() == null) {
            pieChart.setVisibility(View.GONE);
            setupChart(storageEntry.getNodeList());
            isOther = true;
            selectedNode = null;
        } else {
            if (!storageEntry.getNode().getChildren().isEmpty() && !storageEntry.getNode().isAllFiles()) {
                currentNode = storageEntry.getNode();
                pieChart.setVisibility(View.GONE);
                setupChart(currentNode.getChildren());
                selectedNode = null;
            } else {
                selectedNode = storageEntry.getNode();
            }
        }
    }

    private StoragePieChart createChart() {
        StoragePieChart pieChart = new StoragePieChart(this);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(-1, -1);
        int margin = MeasUtils.pxToDp(30, this);
        params.setMargins(margin, margin, margin, margin);
        params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
        pieChart.setLayoutParams(params);
        mainLayout.addView(pieChart);
        return pieChart;
    }

    public void backToParent(View view) {
        if (isOther) {
            isOther = false;
            pieChart.setVisibility(View.GONE);
            setupChart(currentNode.getChildren());
            return;
        }
        if (currentNode.getParent() != null && currentNode.getParent().getFile() != null) {
            pieChart.setVisibility(View.GONE);
            currentNode = currentNode.getParent();
            setupChart(currentNode.getChildren());
        }
        selectedNode = null;
    }

    public void openInExplorer(View view) {
        Uri selectedUri;
        if (selectedNode != null)
            selectedUri = Uri.parse(selectedNode.getFile().getAbsolutePath());
        else
            selectedUri = Uri.parse(currentNode.getFile().getAbsolutePath());
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(selectedUri, "resource/folder");
        System.out.println(selectedUri);
        if (intent.resolveActivityInfo(getPackageManager(), 0) != null) {
            startActivity(intent);
        } else {
            Toasty.error(this, "Please install a file explorer app", Toasty.LENGTH_LONG, true).show();
        }
    }

    public Node getSelectedNode() {
        return selectedNode;
    }

    public void setSelectedNode(Node selectedNode) {
        this.selectedNode = selectedNode;
    }
}
