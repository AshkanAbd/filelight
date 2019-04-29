package ir.ashkanabd.filelight.partition;

import androidx.annotation.NonNull;

public class PartitionStatus {

    private int totalSpace;
    private int freeSpace;
    private String partitionName;

    public PartitionStatus(int totalSpace, int freeSpace, String partitionName) {
        this.totalSpace = totalSpace;
        this.freeSpace = freeSpace;
        this.partitionName = partitionName;
    }

    public double getPercent() {
        return 100 / (totalSpace / (double) freeSpace);
    }

    public int getTotalSpace() {
        return totalSpace;
    }

    public void setTotalSpace(int totalSpace) {
        this.totalSpace = totalSpace;
    }

    public int getFreeSpace() {
        return freeSpace;
    }

    public void setFreeSpace(int freeSpace) {
        this.freeSpace = freeSpace;
    }

    public String getPartitionName() {
        return partitionName;
    }

    public void setPartitionName(String partitionName) {
        this.partitionName = partitionName;
    }

    @NonNull
    @Override
    public String toString() {
        return "PartitionStatus{" +
                "totalSpace=" + totalSpace +
                ", freeSpace=" + freeSpace +
                ", partitionName='" + partitionName + '\'' +
                '}';
    }
}
