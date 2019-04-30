package ir.ashkanabd.filelight;

import androidx.appcompat.app.AppCompatActivity;
import ir.ashkanabd.filelight.background.BackgroundTask;
import ir.ashkanabd.filelight.storage.Storage;
import ir.ashkanabd.filelight.storage.explore.Explorer;
import ir.ashkanabd.filelight.storage.explore.Node;
import ir.ashkanabd.filelight.view.MeasUtils;
import ir.ashkanabd.filelight.view.StorageEntry;
import ir.ashkanabd.filelight.view.StoragePieChart;
import ir.ashkanabd.filelight.view.StorageRenderer;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

import com.afollestad.materialdialogs.MaterialDialog;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.Utils;


import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ScanActivity extends AppCompatActivity {

    private Storage storage;
    private MaterialDialog loadingDialog;
    private RelativeLayout mainLayout;
    private StoragePieChart pieChart;
    private Node rootNode;
    private Node currentNode;

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
        List<PieEntry> entryList = new ArrayList<>();
        StorageEntry other = new StorageEntry(0, "Other");
        for (Node child : nodeList) {
            if (child.getLength() < Math.pow(10, 3)) {
                other.setY(other.getY() + child.getLength());
                other.addNode(child);
                continue;
            }
            StorageEntry entry = new StorageEntry(child.getLength(), child.getFile().getName());
            entry.setStorageType(Storage.getStorageType(child.getLength()));
            entry.setNode(child);
            entryList.add(entry);
        }
        if (other.getY() != 0) {
            other.setStorageType(Storage.getStorageType(other.getY()));
            entryList.add(other);
        }
        PieDataSet pieDataSet = new PieDataSet(entryList, null);
        pieDataSet.setColors(Color.parseColor("#408AF8"), Color.parseColor("#D8433C")
                , Color.parseColor("#F2AF3A"), Color.parseColor("#279B5E"));
        pieDataSet.setSliceSpace(2);
        pieDataSet.setValueTextColor(Color.WHITE);
        pieDataSet.setValueTextSize(15);
        pieDataSet.setSelectionShift(10f);
        pieDataSet.setAutomaticallyDisableSliceSpacing(true);

        PieData pieData = new PieData(pieDataSet);

        pieChart = createChart();
        StorageRenderer renderer = new StorageRenderer(pieChart, pieChart.getAnimator(), pieChart.getViewPortHandler());
        renderer.setChartClickListener(this::onChartClicked);
        pieChart.setRenderer(renderer);
        pieChart.getLegend().setEnabled(false);
        pieChart.setData(pieData);
        pieChart.setCenterTextSize(20);
        pieChart.setCenterText(storage.getName() + " storage");
    }

    private void onChartClicked(StorageEntry storageEntry) {
        if (storageEntry.getNode() == null) {
            pieChart.setVisibility(View.GONE);
            setupChart(storageEntry.getNodeList());
        } else {
            if (!storageEntry.getNode().getChildren().isEmpty()) {
                currentNode = storageEntry.getNode();
                pieChart.setVisibility(View.GONE);
                setupChart(currentNode.getChildren());
            }
        }
    }

    private StoragePieChart createChart() {
        StoragePieChart pieChart = new StoragePieChart(this);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(-1, -1);
        int margin = MeasUtils.pxToDp(20, this);
        params.setMargins(margin, margin, margin, margin);
        params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
        pieChart.setLayoutParams(params);
        mainLayout.addView(pieChart);
        return pieChart;
    }

    public void backToParent(View view) {
        if (currentNode.getParent() != null) {
            pieChart.setVisibility(View.GONE);
            currentNode = currentNode.getParent();
            setupChart(currentNode.getChildren());
        }
    }
}
