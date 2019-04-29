package ir.ashkanabd.filelight.view;

import com.github.lzyzsd.circleprogress.ArcProgress;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class PartitionViewHolder extends RecyclerView.ViewHolder {

    private ArcProgress arcProgress;

    public PartitionViewHolder(@NonNull ArcProgress itemView) {
        super(itemView);
        this.arcProgress = itemView;
    }

    public ArcProgress getArcProgress() {
        return arcProgress;
    }
}
