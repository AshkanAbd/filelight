package ir.ashkanabd.filelight.view;

import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.lzyzsd.circleprogress.ArcProgress;

import androidx.recyclerview.widget.RecyclerView;
import ir.ashkanabd.filelight.storage.Storage;

public class StorageViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private ArcProgress arcProgress;
    private RelativeLayout layout;
    private TextView totalSpace;
    private TextView name;
    private TextView freeSpace;
    private TextView usedSpace;
    private Storage storage;
    private ItemClickListener clickListener;

    public StorageViewHolder(RelativeLayout layout, ItemClickListener clickListener) {
        super(layout);
        this.layout = layout;
        this.clickListener = clickListener;
    }

    public ArcProgress getArcProgress() {
        return arcProgress;
    }

    public RelativeLayout getLayout() {
        return layout;
    }

    public void setLayout(RelativeLayout layout) {
        this.layout = layout;
    }

    public void setArcProgress(ArcProgress arcProgress) {
        this.arcProgress = arcProgress;
        this.arcProgress.setOnClickListener(this);
    }

    public TextView getTotalSpaceTextView() {
        return totalSpace;
    }

    public void setTotalSpaceTextView(TextView totalSpace) {
        this.totalSpace = totalSpace;
    }

    public TextView getFreeSpaceTextView() {
        return freeSpace;
    }

    public void setFreeSpaceTextView(TextView freeSpace) {
        this.freeSpace = freeSpace;
    }

    public TextView getNameTextView() {
        return name;
    }

    public void setNameTextView(TextView name) {
        this.name = name;
    }

    public Storage getStorage() {
        return storage;
    }

    public void setStorage(Storage storage) {
        this.storage = storage;
    }

    public TextView getUsedSpace() {
        return usedSpace;
    }

    public void setUsedSpace(TextView usedSpace) {
        this.usedSpace = usedSpace;
    }

    @Override
    public void onClick(View v) {
        clickListener.onItemClicked(storage);
    }
}
