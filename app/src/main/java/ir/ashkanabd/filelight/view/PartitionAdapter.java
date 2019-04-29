package ir.ashkanabd.filelight.view;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.fivehundredpx.greedolayout.GreedoLayoutSizeCalculator;
import com.github.lzyzsd.circleprogress.ArcProgress;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import ir.ashkanabd.filelight.MeasUtils;
import ir.ashkanabd.filelight.PartitionStatus;

public class PartitionAdapter extends RecyclerView.Adapter<PartitionViewHolder> implements GreedoLayoutSizeCalculator.SizeCalculatorDelegate {
    private Context context;
    private List<PartitionStatus> partitionStatusList;

    public PartitionAdapter(Context context, List<PartitionStatus> partitionStatusList) {
        this.context = context;
        this.partitionStatusList = partitionStatusList;
    }

    @Override
    public double aspectRatioForIndex(int i) {
        return 1;
    }

    @NonNull
    @Override
    public PartitionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ArcProgress arcProgress = new ArcProgress(context);
        return new PartitionViewHolder(arcProgress);
    }

    @Override
    public void onBindViewHolder(@NonNull PartitionViewHolder holder, int position) {
        ArcProgress arcProgress = holder.getArcProgress();
        PartitionStatus status = partitionStatusList.get(position);
        arcProgress.setBottomText(status.getPartitionName());
        arcProgress.setProgress(status.getPercent());
        arcProgress.setBackgroundColor(Color.parseColor("#214193"));
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                MeasUtils.dpToPx(100, context), MeasUtils.dpToPx(100, context));
        layoutParams.setMarginStart(MeasUtils.dpToPx(50, context));
        layoutParams.setMarginEnd(MeasUtils.dpToPx(50, context));
        arcProgress.setLayoutParams(layoutParams);
        System.out.println();
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
