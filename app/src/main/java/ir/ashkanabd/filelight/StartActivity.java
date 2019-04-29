package ir.ashkanabd.filelight;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import es.dmoral.toasty.Toasty;
import ir.ashkanabd.filelight.background.BackgroundTask;
import ir.ashkanabd.filelight.partition.PartitionStatus;
import ir.ashkanabd.filelight.view.PartitionAdapter;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;


import java.util.ArrayList;
import java.util.List;

public class StartActivity extends AppCompatActivity {

    private boolean backPress = false;
    private static String LOGGER = "FileLight";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start_activity);
        BackgroundTask backgroundTask = new BackgroundTask();
        backgroundTask.setTaskExecute((_1) -> checkPermission());
        backgroundTask.setPostExecute((_1) -> postExecute());
        backgroundTask.execute();
    }

    private void postExecute() {
        findViews();

    }

    private void findViews() {
        List<PartitionStatus> statusList = new ArrayList<>();
        statusList.add(new PartitionStatus(1000, 250, "internal"));
        statusList.add(new PartitionStatus(1000, 640, "sdcard"));
        PartitionAdapter photosAdapter = new PartitionAdapter(this, statusList);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        RecyclerView recyclerView = findViewById(R.id.recycle_view);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(photosAdapter);
        recyclerView.setHasFixedSize(true);
    }

    private Void checkPermission() {
        if (!checkStoragePermission()) {
            requestStoragePermission();
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            while (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                ;
        }
        return null;
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
