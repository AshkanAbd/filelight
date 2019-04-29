package ir.ashkanabd.filelight.partition;

import androidx.annotation.NonNull;

public class PartitionStatus {

    private long totalSpace;
    private long freeSpace;
    private String partitionName;
    private String path;

    public PartitionStatus(long totalSpace, long freeSpace, String partitionName) {
        this.totalSpace = totalSpace;
        this.freeSpace = freeSpace;
        this.partitionName = partitionName;
    }

    public PartitionStatus() {
    }

    public double getPercent() {
        long nonfree = totalSpace - freeSpace;
        return 100 / (totalSpace / (double) nonfree);
    }

    public long getTotalSpace() {
        return totalSpace;
    }

    public void setTotalSpace(long totalSpace) {
        this.totalSpace = totalSpace;
    }

    public long getFreeSpace() {
        return freeSpace;
    }

    public void setFreeSpace(long freeSpace) {
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
                ", path='" + path + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PartitionStatus that = (PartitionStatus) o;

        if (totalSpace != that.totalSpace) return false;
        if (freeSpace != that.freeSpace) return false;
        return partitionName.equals(that.partitionName);
    }

    @Override
    public int hashCode() {
        int result = (int) (totalSpace ^ (totalSpace >>> 32));
        result = 31 * result + (int) (freeSpace ^ (freeSpace >>> 32));
        result = 31 * result + partitionName.hashCode();
        return result;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
