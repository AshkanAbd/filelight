package ir.ashkanabd.filelight;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import es.dmoral.toasty.Toasty;
import ir.ashkanabd.filelight.background.BackgroundTask;
import ir.ashkanabd.filelight.storage.Storage;
import ir.ashkanabd.filelight.view.StorageAdapter;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;

import java.io.File;
import java.util.ArrayList;

public class StartActivity extends AppCompatActivity {

    private boolean backPress = false;
    private static String LOGGER = "FileLight";
    private RecyclerView recyclerView;
    private ArrayList<Storage> statusList;
    private SwipeRefreshLayout refreshStorage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start_activity);
        BackgroundTask backgroundTask = new BackgroundTask();
        backgroundTask.setTaskExecute(_1 -> startTask());
        backgroundTask.setPostExecute(_1 -> postExecute());
        backgroundTask.execute();
    }

    private Void startTask() {
        checkPermission();
        findPartition();
        return null;
    }

    private void postExecute() {
        findViews();
        setupPartitionList();
    }

    private void findPartition() {
        statusList = new ArrayList<>();
        statusList.add(getInternalStorage());
        for (File file : ContextCompat.getExternalFilesDirs(this, null)) {
            if (file == null) continue;
            File file1 = file.getParentFile().getParentFile().getParentFile().getParentFile();
            Storage storage = new Storage(file1);
            if (!statusList.contains(storage)) {
                statusList.add(storage);
            }
        }
    }

    private Storage getInternalStorage() {
        File file = Environment.getExternalStorageDirectory();
        Storage storage = new Storage(file);
        storage.setName("Internal");
        return storage;
    }

    private void setupPartitionList() {
        StorageAdapter storageAdapter = new StorageAdapter(this, statusList, this::onStorageClicked);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(storageAdapter);
        recyclerView.setHasFixedSize(true);
    }

    private void onStorageClicked(Storage storage) {
        Intent intent = new Intent(this, ScanActivity.class);
        intent.putExtra("storage", storage);
        startActivity(intent);
    }

    private void findViews() {
        recyclerView = findViewById(R.id.recycle_view);
        refreshStorage = findViewById(R.id.refresh_storage);
        refreshStorage.setColorSchemeColors(Color.parseColor("#408AF8"), Color.parseColor("#D8433C")
                , Color.parseColor("#F2AF3A"), Color.parseColor("#279B5E"));
        refreshStorage.setOnRefreshListener(() -> {
            findPartition();
            setupPartitionList();
            refreshStorage.setRefreshing(false);
        });
    }

    private void checkPermission() {
        if (!checkStoragePermission()) {
            requestStoragePermission();
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            while (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                ;
        }
    }

    private boolean checkStoragePermission() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        return this.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            this.requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }
    }

    @Override
    public void onBackPressed() {
        if (backPress) {
            super.onBackPressed();
        } else {
            backPress = true;
            Toasty.warning(this, "Press back again", Toasty.LENGTH_SHORT, true).show();
            new Handler().postDelayed(() -> backPress = false, 2000);
        }
    }
}
