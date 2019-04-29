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
import ir.ashkanabd.filelight.partition.Storage;

public class PartitionAdapter extends RecyclerView.Adapter<PartitionViewHolder> {
    private Context context;
    private List<Storage> storageList;

    public PartitionAdapter(Context context, List<Storage> storageList) {
        this.context = context;
        this.storageList = storageList;
    }

    @NonNull
    @Override
    public PartitionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RelativeLayout layout = (RelativeLayout) LayoutInflater.from(context).inflate(R.layout.partition_layout, parent, false);
        ArcProgress arcProgress = layout.findViewById(R.id.arc_progress);
        TextView freeSpace = layout.findViewById(R.id.free_space_text_view);
        TextView totalSpace = layout.findViewById(R.id.total_space_text_view);
        TextView nameStorage = layout.findViewById(R.id.storage_name_text_view);
        PartitionViewHolder viewHolder = new PartitionViewHolder(layout);
        viewHolder.setArcProgress(arcProgress);
        viewHolder.setFreeSpaceTextView(freeSpace);
        viewHolder.setTotalSpaceTextView(totalSpace);
        viewHolder.setNameTextView(nameStorage);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull PartitionViewHolder holder, int position) {
        Storage storage = storageList.get(position);
        ArcProgress arcProgress = holder.getArcProgress();
        arcProgress.setProgress((int) storage.getPercent());
        double freeSpaceToGB = storage.getFreeSpace() / (2 * Math.pow(10, 9));
        freeSpaceToGB = round(freeSpaceToGB);
        double totalSpaceToGB = storage.getTotalSpace() / (2 * Math.pow(10, 9));
        totalSpaceToGB = round(totalSpaceToGB);
        holder.getFreeSpaceTextView().setText("Free : " + freeSpaceToGB + " GB");
        holder.getTotalSpaceTextView().setText("Total : " + totalSpaceToGB + " GB");
        holder.getNameTextView().setText("Name : " + storage.getPartitionName());
        holder.setStorage(storage);
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
        return storageList.size();
    }

    public List<Storage> getStorageList() {
        return storageList;
    }

    public void setStorageList(List<Storage> storageList) {
        this.storageList = storageList;
    }
}
