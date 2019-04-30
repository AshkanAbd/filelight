package ir.ashkanabd.filelight;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatRadioButton;
import es.dmoral.toasty.Toasty;
import ir.ashkanabd.filelight.background.BackgroundTask;
import ir.ashkanabd.filelight.storage.Storage;
import ir.ashkanabd.filelight.storage.explore.Explorer;
import ir.ashkanabd.filelight.storage.explore.Node;
import ir.ashkanabd.filelight.view.StorageEntry;
import ir.ashkanabd.filelight.view.piechart.PieChartGenerator;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

import com.afollestad.materialdialogs.MaterialDialog;
import com.github.mikephil.charting.utils.Utils;

import java.util.List;
import java.util.Objects;

public class ScanActivity extends AppCompatActivity {

    public static int PIE = 1;
    public static int OTHER = 2;

    private Storage storage;
    private MaterialDialog loadingDialog;
    private RelativeLayout mainLayout;
    private Node rootNode;
    private Node selectedNode;
    private boolean isOther = false;
    private int chartMode = PIE;
    private AppCompatRadioButton pieRadioButton, otherRadioButton;
    private PieChartGenerator pieChartGenerator;

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
        pieRadioButton = findViewById(R.id.pie_chart_radio_btn);
        otherRadioButton = findViewById(R.id.other_chart_radio_btn);
        pieRadioButton.setOnClickListener(_1 -> changeChartMode(PIE));
        otherRadioButton.setOnClickListener(_1 -> changeChartMode(OTHER));
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
        pieChartGenerator = new PieChartGenerator(this, rootNode);
        pieChartGenerator.setChartClickListener(this::onChartClicked);
        setupChart(rootNode.getChildren());
    }

    private void setupChart(List<Node> nodeList) {
        removeChart();
        if (chartMode == PIE) {
            pieChartGenerator.setupPieChart(nodeList);
        } else {

        }
    }

    private void onChartClicked(StorageEntry storageEntry) {
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
