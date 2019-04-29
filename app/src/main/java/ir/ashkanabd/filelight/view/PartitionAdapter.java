package ir.ashkanabd.filelight.view;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.github.lzyzsd.circleprogress.ArcProgress;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import ir.ashkanabd.filelight.R;
import ir.ashkanabd.filelight.partition.PartitionStatus;

public class PartitionAdapter extends RecyclerView.Adapter<PartitionViewHolder> {
    private Context context;
    private List<PartitionStatus> partitionStatusList;

    public PartitionAdapter(Context context, List<PartitionStatus> partitionStatusList) {
        this.context = context;
        this.partitionStatusList = partitionStatusList;
    }

    @NonNull
    @Override
    public PartitionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RelativeLayout layout = (RelativeLayout) LayoutInflater.from(context).inflate(R.layout.partition_layout, parent, false);
        ArcProgress arcProgress = layout.findViewById(R.id.arc_progress);
        PartitionViewHolder viewHolder = new PartitionViewHolder(layout);
        viewHolder.setArcProgress(arcProgress);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull PartitionViewHolder holder, int position) {
        ArcProgress arcProgress = holder.getArcProgress();
        PartitionStatus status = partitionStatusList.get(position);
        arcProgress.setBottomText(status.getPartitionName());
        arcProgress.setProgress((int) status.getPercent());
    }

    @Override
    public int getItemCount() {
        return partitionStatusList.size();
    }

    public List<PartitionStatus> getPartitionStatusList() {
        return partitionStatusList;
    }

    public void setPartitionStatusList(List<PartitionStatus> partitionStatusList) {
        this.partitionStatusList = partitionStatusList;
    }
}
