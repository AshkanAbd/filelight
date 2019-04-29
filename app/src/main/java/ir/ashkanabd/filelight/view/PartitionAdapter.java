package ir.ashkanabd.filelight.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

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
        TextView freeSpace = layout.findViewById(R.id.free_space_text_view);
        TextView totalSpace = layout.findViewById(R.id.total_space_text_view);
        PartitionViewHolder viewHolder = new PartitionViewHolder(layout);
        viewHolder.setArcProgress(arcProgress);
        viewHolder.setFreeSpaceTextView(freeSpace);
        viewHolder.setTotalSpaceTextView(totalSpace);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull PartitionViewHolder holder, int position) {
        ArcProgress arcProgress = holder.getArcProgress();
        PartitionStatus status = partitionStatusList.get(position);
        arcProgress.setBottomText(status.getPartitionName());
        arcProgress.setProgress((int) status.getPercent());
        double freeSpaceToGB = status.getFreeSpace() / (2 * Math.pow(10, 9));
        freeSpaceToGB = round(freeSpaceToGB);
        double totalSpaceToGB = status.getTotalSpace() / (2 * Math.pow(10, 9));
        totalSpaceToGB = round(totalSpaceToGB);
        holder.getFreeSpaceTextView().setText("Free : " + freeSpaceToGB + " GB");
        holder.getTotalSpaceTextView().setText("Total : " + totalSpaceToGB + " GB");
    }

    private double round(double d) {
        String s = Double.toString(d);
        int dotIndex = s.indexOf('.');
        StringBuilder builder = new StringBuilder(s.substring(0, dotIndex));
        s = s.substring(dotIndex, s.length());
        for (int i = 0; i < 3; i++) {
            builder.append(s.charAt(i));
        }
        return Double.parseDouble(builder.toString());
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
