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

public class StorageAdapter extends RecyclerView.Adapter<StorageViewHolder> {
    private Context context;
    private List<Storage> storageList;
    private ItemClickListener clickListener;

    public StorageAdapter(Context context, List<Storage> storageList, ItemClickListener clickListener) {
        this.context = context;
        this.storageList = storageList;
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public StorageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RelativeLayout layout = (RelativeLayout) LayoutInflater.from(context).inflate(R.layout.partition_layout, parent, false);
        ArcProgress arcProgress = layout.findViewById(R.id.arc_progress);
        TextView freeSpace = layout.findViewById(R.id.free_space_text_view);
        TextView totalSpace = layout.findViewById(R.id.total_space_text_view);
        TextView nameStorage = layout.findViewById(R.id.storage_name_text_view);
        TextView usedSpace = layout.findViewById(R.id.used_space_text_view);
        StorageViewHolder viewHolder = new StorageViewHolder(layout, clickListener);
        viewHolder.setArcProgress(arcProgress);
        viewHolder.setFreeSpaceTextView(freeSpace);
        viewHolder.setTotalSpaceTextView(totalSpace);
        viewHolder.setNameTextView(nameStorage);
        viewHolder.setUsedSpace(usedSpace);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull StorageViewHolder holder, int position) {
        Storage storage = storageList.get(position);
        ArcProgress arcProgress = holder.getArcProgress();
        arcProgress.setProgress((int) storage.getPercent());
        double freeSpaceToGB = storage.getFreeSpace() / Math.pow(10, 9);
        freeSpaceToGB = round(freeSpaceToGB);
        double totalSpaceToGB = storage.getTotalSpace() / Math.pow(10, 9);
        totalSpaceToGB = round(totalSpaceToGB);
        double usedSpaceToGB = (storage.getTotalSpace() - storage.getFreeSpace()) / Math.pow(10, 9);
        usedSpaceToGB = round(usedSpaceToGB);
        holder.getNameTextView().setText("Name : " + storage.getPartitionName());
        holder.getFreeSpaceTextView().setText("Free : " + freeSpaceToGB + " GB");
        holder.getUsedSpace().setText("Used : " + usedSpaceToGB + " GB");
        holder.getTotalSpaceTextView().setText("Total : " + totalSpaceToGB + " GB");
        holder.setStorage(storage);
    }

    private double round(double d) {
        d = d * 100;
        d = Math.round(d);
        return d / 100;
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
