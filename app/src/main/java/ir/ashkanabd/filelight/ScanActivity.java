package ir.ashkanabd.filelight;

import androidx.appcompat.app.AppCompatActivity;
import ir.ashkanabd.filelight.background.BackgroundTask;
import ir.ashkanabd.filelight.storage.Storage;
import ir.ashkanabd.filelight.storage.explore.Explorer;

import android.os.Bundle;

import java.util.Objects;

public class ScanActivity extends AppCompatActivity {

    private Storage storage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scan_activity);
        BackgroundTask backgroundTask = new BackgroundTask();
        backgroundTask.setTaskExecute(_1 -> startTask());
        backgroundTask.setPostExecute(_1 -> postExecute());
        backgroundTask.execute();
    }

    private Void startTask() {
        storage = (Storage) Objects.requireNonNull(getIntent().getExtras()).get("storage");
        Explorer explorer = new Explorer(storage);
        explorer.startExploring();
        System.out.println(explorer.getRoot());
        return null;
    }

    private void postExecute() {

    }

}
