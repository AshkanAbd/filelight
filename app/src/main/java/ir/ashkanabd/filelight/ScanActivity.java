package ir.ashkanabd.filelight;

import androidx.appcompat.app.AppCompatActivity;
import es.dmoral.toasty.Toasty;
import ir.ashkanabd.filelight.background.BackgroundTask;
import ir.ashkanabd.filelight.storage.Storage;
import ir.ashkanabd.filelight.storage.explore.Explorer;
import ir.ashkanabd.filelight.storage.explore.Node;
import ir.ashkanabd.filelight.view.barchart.BarChartGenerator;
import ir.ashkanabd.filelight.view.barchart.StorageBarEntry;
import ir.ashkanabd.filelight.view.piechart.StoragePieEntry;
import ir.ashkanabd.filelight.view.piechart.PieChartGenerator;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;

import com.afollestad.materialdialogs.MaterialDialog;
import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.charts.Sunburst;
import com.anychart.enums.SunburstCalculationMode;
import com.anychart.enums.TreeFillingMethod;
import com.anychart.graphics.vector.text.HAlign;
import com.github.mikephil.charting.utils.Utils;
import com.rey.material.widget.Spinner;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ScanActivity extends AppCompatActivity {

    public static int PIE = 0;
    public static int BAR = 1;
    public static int SUNBURST = 2;

    private Storage storage;
    private MaterialDialog loadingDialog;
    private RelativeLayout mainLayout;
    private Node rootNode;
    private Node selectedNode;
    private boolean isOther = false;
    private int chartMode = BAR;
    private PieChartGenerator pieChartGenerator;
    private BarChartGenerator barChartGenerator;

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
        Spinner chartModeSpinner = findViewById(R.id.chart_mode_spinner);
        ArrayAdapter<String> chartModeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        chartModeAdapter.addAll("Pie", "Bar", "Sunburst");
        chartModeSpinner.setAdapter(chartModeAdapter);
        chartModeSpinner.setOnItemSelectedListener((_1, _2, position, _3) -> changeChartMode(position));
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
        findViews();
        Utils.init(this);
        pieChartGenerator = new PieChartGenerator(this, rootNode);
        pieChartGenerator.setPieChartClickListener(this::onPieChartClicked);
        barChartGenerator = new BarChartGenerator(this, rootNode);
        barChartGenerator.setBarChartClickListener(this::onBarChartClicked);
        setupChart(rootNode.getChildren());
    }

    private void setupChart(List<Node> nodeList) {
        removeChart();
        if (chartMode == PIE) {
            pieChartGenerator.setupPieChart(nodeList);
        } else if (chartMode == BAR) {
            barChartGenerator.setupBarChart(nodeList);
        } else if (chartMode == SUNBURST) {
            sunBurst(nodeList);
        }
    }

    private AnyChartView anyChartView;

    private void sunBurst(List<Node> nodeList) {
        anyChartView = new AnyChartView(this);
        anyChartView.setBackgroundColor(Color.parseColor("#FAFAFA"));
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(-1, -2);
        params.addRule(RelativeLayout.BELOW, R.id.chart_mode_spinner);
        params.addRule(RelativeLayout.ABOVE, R.id.open_dir_btn);
        anyChartView.setLayoutParams(params);

        mainLayout.addView(anyChartView);

        Sunburst sunburst = AnyChart.sunburst();

        List<DataEntry> data = new ArrayList<>();
        Node parent = nodeList.get(0).getParent();
        readTree(parent, data);

        sunburst.data(data, TreeFillingMethod.AS_TABLE);

        sunburst.calculationMode(SunburstCalculationMode.ORDINAL_FROM_LEAVES);

        sunburst.labels().hAlign(HAlign.CENTER);
        sunburst.palette(new String[]{"#408AF8", "#D8433C", "#F2AF3A", "#279B5E"});

        sunburst.fill("function () {" +
                "return this.parent ? anychart.color.darken(this.parentColor, 0.15) : this.mainColor" +
                "}");

        sunburst.tooltip().format("Employee: {%leavesSum}");

        anyChartView.setChart(sunburst);
    }

    private void readTree(Node parent, List<DataEntry> dataEntries) {
        if (parent.isAllFiles()) return;
        if (parent.getChildren().isEmpty()) return;
        for (Node node : parent.getChildren()) {
            if (!node.getFile().isDirectory()) continue;
            CustomDataEntry entry = new CustomDataEntry(node.getFile().getName(), node.getFile().getAbsolutePath());
            entry.setValue("parent", node.getParent().getFile().getAbsolutePath());
            entry.setValue("value", (int) node.getLength());
            dataEntries.add(entry);
            readTree(node, dataEntries);
        }
    }

    class CustomDataEntry extends DataEntry {
        CustomDataEntry(String name, String id) {
            setValue("name", name);
            setValue("id", id);
        }

        CustomDataEntry(String name, String id, String parent) {
            setValue("name", name);
            setValue("id", id);
            setValue("parent", parent);
        }

        CustomDataEntry(String name, String id, String parent, int value) {
            setValue("name", name);
            setValue("id", id);
            setValue("parent", parent);
            setValue("value", value);
        }
    }

    private void onPieChartClicked(StoragePieEntry storageEntry) {
        if (storageEntry.getNode() == null) {
            setupChart(storageEntry.getNodeList());
            isOther = true;
            selectedNode = null;
        } else {
            if (!storageEntry.getNode().getChildren().isEmpty() && !storageEntry.getNode().isAllFiles()) {
                pieChartGenerator.setCurrentNode(storageEntry.getNode());
                setupChart(pieChartGenerator.getCurrentNode().getChildren());
                selectedNode = null;
            } else {
                selectedNode = storageEntry.getNode();
            }
        }
    }

    private void onBarChartClicked(StorageBarEntry storageEntry) {
        if (storageEntry.getNode() == null) {
            setupChart(storageEntry.getNodeList());
            isOther = true;
            selectedNode = null;
        } else {
            if (!storageEntry.getNode().getChildren().isEmpty() && !storageEntry.getNode().isAllFiles()) {
                pieChartGenerator.setCurrentNode(storageEntry.getNode());
                setupChart(pieChartGenerator.getCurrentNode().getChildren());
                selectedNode = null;
            } else {
                selectedNode = storageEntry.getNode();
            }
        }
    }

    public void backToParent(View view) {
        if (isOther) {
            isOther = false;
            setupChart(pieChartGenerator.getCurrentNode().getChildren());
            return;
        }
        if (pieChartGenerator.getCurrentNode().getParent() != null
                && pieChartGenerator.getCurrentNode().getParent().getFile() != null) {
            pieChartGenerator.setCurrentNode(pieChartGenerator.getCurrentNode().getParent());
            setupChart(pieChartGenerator.getCurrentNode().getChildren());
        }
        selectedNode = null;
    }

    public void openInExplorer(View view) {
        Uri selectedUri;
        if (selectedNode != null)
            selectedUri = Uri.parse(selectedNode.getFile().getAbsolutePath());
        else
            selectedUri = Uri.parse(pieChartGenerator.getCurrentNode().getFile().getAbsolutePath());
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(selectedUri, "resource/folder");
        System.out.println(selectedUri);
        if (intent.resolveActivityInfo(getPackageManager(), 0) != null) {
            startActivity(intent);
        } else {
            Toasty.error(this, "Please install a file explorer app", Toasty.LENGTH_LONG, true).show();
        }
    }

    private void removeChart() {
        if (pieChartGenerator.getPieChart() != null) {
            pieChartGenerator.getPieChart().setVisibility(View.GONE);
        }
        if (barChartGenerator.getBarChart() != null) {
            barChartGenerator.getBarChart().setVisibility(View.GONE);
        }
        if (anyChartView != null) {
            anyChartView.setVisibility(View.GONE);
        }
    }

    private void changeChartMode(int mode) {
        if (chartMode == mode) return;
        chartMode = mode;
        setupChart(pieChartGenerator.getCurrentNode().getChildren());
    }

    public Node getSelectedNode() {
        return selectedNode;
    }

    public void setSelectedNode(Node selectedNode) {
        this.selectedNode = selectedNode;
    }

    public RelativeLayout getMainLayout() {
        return mainLayout;
    }
}
