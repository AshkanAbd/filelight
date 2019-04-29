package ir.ashkanabd.filelight;

import androidx.appcompat.app.AppCompatActivity;
import ir.ashkanabd.filelight.background.BackgroundTask;
import ir.ashkanabd.filelight.storage.Storage;
import ir.ashkanabd.filelight.storage.explore.Explorer;

import android.os.Bundle;

import com.afollestad.materialdialogs.MaterialDialog;

import java.util.Objects;

public class ScanActivity extends AppCompatActivity {

    private Storage storage;
    private MaterialDialog loadingDialog;

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
    }

    private Void startTask() {
        storage = (Storage) Objects.requireNonNull(getIntent().getExtras()).get("storage");
        Explorer explorer = new Explorer(storage);
        explorer.startExploring();
        System.out.println(explorer.getRoot());
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void postExecute() {
        loadingDialog.dismiss();
    }

}
