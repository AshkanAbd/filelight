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
import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.DefaultValueFormatter;
import com.github.mikephil.charting.formatter.LargeValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.renderer.DataRenderer;
import com.github.mikephil.charting.renderer.PieChartRenderer;


import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ScanActivity extends AppCompatActivity {

    private Storage storage;
    private MaterialDialog loadingDialog;
    private RelativeLayout mainLayout;
    private StoragePieChart mainChart;
    private Node rootNode;

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
        mainChart = findViewById(R.id.main_chart);
        mainChart.setVisibility(View.INVISIBLE);
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
        setupChart(rootNode);
    }

    private void setupChart(Node node) {
        List<PieEntry> entryList = new ArrayList<>();
        StorageEntry other = new StorageEntry(0, "Other");
        for (Node child : node.getChildren()) {
            if (child.getLength() < 10 * Math.pow(10, 3)) {
                other.setY(other.getY() + child.getLength());
                continue;
            }
            StorageEntry entry = new StorageEntry((float) Storage.getInBestFormatDouble(child.getLength()), child.getFile().getName());
            entry.setStorageType(Storage.getStorageType(child.getLength()));
            entry.setNode(child);
            entryList.add(entry);
        }
        if (other.getY() != 0) {
            other.setStorageType(Storage.getStorageType(other.getY()));
            other.setY((float) Storage.getInBestFormatDouble(other.getY()));
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

        StorageRenderer renderer = new StorageRenderer(mainChart, mainChart.getAnimator(), mainChart.getViewPortHandler());
        renderer.setChartClickListener(this::onChartClicked);
        mainChart.setRenderer(renderer);
        mainChart.getLegend().setEnabled(false);
        mainChart.setData(pieData);
        mainChart.setCenterTextSize(20);
        mainChart.setCenterText(storage.getName() + " storage");
        mainChart.setVisibility(View.VISIBLE);
    }

    private void onChartClicked(StorageEntry storageEntry) {
        if (storageEntry != null)
            System.out.println(storageEntry.getNode());
    }
}
