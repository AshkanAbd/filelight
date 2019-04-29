package ir.ashkanabd.filelight;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import es.dmoral.toasty.Toasty;
import ir.ashkanabd.filelight.background.BackgroundTask;
import ir.ashkanabd.filelight.partition.PartitionStatus;
import ir.ashkanabd.filelight.partition.StorageUtils;
import ir.ashkanabd.filelight.view.PartitionAdapter;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;


import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class StartActivity extends AppCompatActivity {

    private boolean backPress = false;
    private static String LOGGER = "FileLight";
    private RecyclerView recyclerView;
    private List<PartitionStatus> statusList;
    private PartitionAdapter partitionAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start_activity);
        BackgroundTask backgroundTask = new BackgroundTask();
        backgroundTask.setTaskExecute((_1) -> startTask());
        backgroundTask.setPostExecute((_1) -> postExecute());
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
        for (StorageUtils.StorageInfo s : StorageUtils.getStorageList()) {
            System.err.println(s.getDisplayName());
            System.err.println("--------");
        }

    }

    private PartitionStatus getInternalStorage() {
        File file = Environment.getExternalStorageDirectory();
        PartitionStatus partitionStatus = new PartitionStatus();
        partitionStatus.setPartitionName("Internal");
        partitionStatus.setFreeSpace(file.getFreeSpace());
        partitionStatus.setTotalSpace(file.getTotalSpace());
        return partitionStatus;
    }

    private void setupPartitionList() {
        partitionAdapter = new PartitionAdapter(this, statusList);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(partitionAdapter);
        recyclerView.setHasFixedSize(true);
    }

    private void findViews() {
        recyclerView = findViewById(R.id.recycle_view);
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
