package ir.ashkanabd.filelight.view;

import android.widget.RelativeLayout;

import com.github.lzyzsd.circleprogress.ArcProgress;

import androidx.recyclerview.widget.RecyclerView;

public class PartitionViewHolder extends RecyclerView.ViewHolder {

    private ArcProgress arcProgress;
    private RelativeLayout layout;

    public PartitionViewHolder(RelativeLayout layout) {
        super(layout);
        this.layout = layout;
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
    }
}
