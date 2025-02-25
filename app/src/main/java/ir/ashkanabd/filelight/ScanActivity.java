package ir.ashkanabd.filelight;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import es.dmoral.toasty.Toasty;
import ir.ashkanabd.filelight.background.BackgroundTask;
import ir.ashkanabd.filelight.storage.Storage;
import ir.ashkanabd.filelight.storage.explore.Explorer;
import ir.ashkanabd.filelight.storage.explore.Node;
import ir.ashkanabd.filelight.view.MeasUtils;
import ir.ashkanabd.filelight.view.barchart.BarChartGenerator;
import ir.ashkanabd.filelight.view.barchart.StorageBarEntry;
import ir.ashkanabd.filelight.view.piechart.StoragePieEntry;
import ir.ashkanabd.filelight.view.piechart.PieChartGenerator;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.anychart.chart.common.dataentry.DataEntry;
import com.github.mikephil.charting.utils.Utils;
import com.rey.material.widget.Spinner;

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
    private Node currentNode;
    private boolean isOther = false;
    private int chartMode = PIE;
    private PieChartGenerator pieChartGenerator;
    private BarChartGenerator barChartGenerator;
    private TextView dirTextView;
    private AppCompatButton backDirButton;

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
        setupActionbar();
    }

    private void setupMaterialDialog() {
        loadingDialog = new MaterialDialog(this);
        loadingDialog.setContentView(R.layout.scan_loading);
        loadingDialog.setCancelable(false);
        loadingDialog.show();
    }

    private void setupActionbar() {
        RelativeLayout layout = new RelativeLayout(this);

        Button refreshButton = new Button(this);
        refreshButton.setBackground(getResources().getDrawable(R.drawable.refresh_icon));
        RelativeLayout.LayoutParams refreshParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, -2);
        refreshParams.width = MeasUtils.dpToPx(25, this);
        refreshParams.height = MeasUtils.dpToPx(25, this);
        refreshParams.addRule(RelativeLayout.ALIGN_PARENT_END, RelativeLayout.TRUE);
        refreshParams.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
        refreshParams.setMarginEnd(MeasUtils.dpToPx(10, this));
        refreshButton.setLayoutParams(refreshParams);
        refreshButton.setOnClickListener(_1 -> refreshTree());
        layout.addView(refreshButton);

        TextView appTitleTextView = new TextView(this);
        appTitleTextView.setGravity(Gravity.CENTER);
        appTitleTextView.setTextColor(Color.WHITE);
        appTitleTextView.setText(getString(R.string.app_name));
        appTitleTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
        RelativeLayout.LayoutParams appTitleParams = new RelativeLayout.LayoutParams(-2, -2);
        appTitleParams.addRule(RelativeLayout.ALIGN_PARENT_START, RelativeLayout.TRUE);
        appTitleParams.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
        appTitleTextView.setLayoutParams(appTitleParams);
        layout.addView(appTitleTextView);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setCustomView(layout);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
    }

    private void refreshTree() {
        BackgroundTask task = new BackgroundTask();
        task.setPreExecute(loadingDialog::show);
        task.setPostExecute(_1 -> refreshPostExecute());
        task.setTaskExecute(_1 -> startTask());
        task.execute();
    }

    private void refreshPostExecute() {
        loadingDialog.dismiss();
        setupChart(currentNode.getChildren(), false);
    }

    private void findViews() {
        mainLayout = findViewById(R.id.scan_main_layout);
        dirTextView = findViewById(R.id.dir_text_view);
        backDirButton = findViewById(R.id.back_dir_btn);
        Spinner chartModeSpinner = findViewById(R.id.chart_mode_spinner);
        ArrayAdapter<String> chartModeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        chartModeAdapter.addAll("Pie", "Bar"/*, "Sunburst"*/);
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
        pieChartGenerator = new PieChartGenerator(this);
        pieChartGenerator.setPieChartClickListener(this::onPieChartClicked);
        barChartGenerator = new BarChartGenerator(this);
        barChartGenerator.setBarChartClickListener(this::onBarChartClicked);
        currentNode = rootNode;
        setupChart(currentNode.getChildren(), false);
    }

    private void setupChart(List<Node> nodeList, boolean showHidden) {
        changeView();

        if (chartMode == PIE) {
            pieChartGenerator.setupPieChart(nodeList, showHidden);
        } else if (chartMode == BAR) {
            barChartGenerator.setupBarChart(nodeList, showHidden);
        } else if (chartMode == SUNBURST) {
//            sunBurst(nodeList);
        }
    }

    /*
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
        readTree(parent, data, 1, 3);

        sunburst.data(data, TreeFillingMethod.AS_TABLE);

        sunburst.calculationMode(SunburstCalculationMode.ORDINAL_FROM_LEAVES);

        sunburst.labels().hAlign(HAlign.CENTER);
        sunburst.palette(new String[]{"#408AF8", "#D8433C", "#F2AF3A", "#279B5E"});

        sunburst.fill("function () {" +
                "return this.parent ? anychart.color.darken(this.parentColor, 0.15) : this.mainColor" +
                "}");

        sunburst.tooltip().format("Employee: {%leavesSum}");

        anyChartView.setChart(sunburst);
    }*/

    private void readTree(Node parent, List<DataEntry> dataEntries, int deep, int maxDeep) {
        if (parent.isAllFiles()) return;
        if (parent.getChildren().isEmpty()) return;
        if (deep > maxDeep) return;
        for (Node node : parent.getChildren()) {
            if (!node.getFile().isDirectory()) continue;
            CustomDataEntry entry = new CustomDataEntry(node.getFile().getName(), node.getFile().getAbsolutePath());
            entry.setValue("parent", node.getParent().getFile().getAbsolutePath());
            entry.setValue("value", (int) node.getLength());
            dataEntries.add(entry);
            readTree(node, dataEntries, deep + 1, maxDeep);
        }
    }

    public TextView getDirTextView() {
        return dirTextView;
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
            isOther = true;
            selectedNode = null;
            setupChart(storageEntry.getNodeList(), storageEntry.getLabel().equals(".Hidden"));
        } else {
            if (!storageEntry.getNode().getChildren().isEmpty() && !storageEntry.getNode().isAllFiles()) {
                currentNode = storageEntry.getNode();
                setupChart(currentNode.getChildren(), false);
                selectedNode = null;
                isOther = false;
            } else {
                selectedNode = storageEntry.getNode();
            }
        }
    }

    private void onBarChartClicked(StorageBarEntry storageEntry) {
        if (storageEntry.getNode() == null) {
            setupChart(storageEntry.getNodeList(), storageEntry.getLabel().equals(".Hidden"));
            isOther = true;
            selectedNode = null;
        } else {
            if (!storageEntry.getNode().getChildren().isEmpty() && !storageEntry.getNode().isAllFiles()) {
                currentNode = storageEntry.getNode();
                setupChart(currentNode.getChildren(), false);
                selectedNode = null;
                isOther = false;
            } else {
                selectedNode = storageEntry.getNode();
            }
        }
    }

    public void backToParent(View view) {
        if (isOther) {
            isOther = false;
            setupChart(currentNode.getChildren(), false);
            return;
        } else if (currentNode.getParent() != null && currentNode.getParent().getFile() != null) {
            currentNode = currentNode.getParent();
            setupChart(currentNode.getChildren(), false);
        } else
            Toasty.warning(this, "You are in root folder", Toasty.LENGTH_SHORT, true).show();
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

    private void changeView() {
        if ((currentNode.getParent() != null && currentNode.getParent().getFile() != null) || isOther) {
            backDirButton.setVisibility(View.VISIBLE);
        } else {
            backDirButton.setVisibility(View.GONE);
        }
        if (pieChartGenerator.getPieChart() != null) {
            pieChartGenerator.getPieChart().setVisibility(View.GONE);
            dirTextView.setVisibility(View.GONE);
        }
        if (barChartGenerator.getBarChart() != null) {
            barChartGenerator.getBarChart().setVisibility(View.GONE);
            dirTextView.setVisibility(View.GONE);
        }
        /*if (anyChartView != null) {
            anyChartView.setVisibility(View.GONE);
        }*/
    }

    private void changeChartMode(int mode) {
        if (chartMode == mode) return;
        chartMode = mode;
        setupChart(currentNode.getChildren(), false);
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

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }
}
