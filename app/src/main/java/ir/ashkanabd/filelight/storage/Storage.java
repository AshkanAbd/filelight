package ir.ashkanabd.filelight.storage;

import java.io.File;
import java.io.Serializable;

import androidx.annotation.NonNull;

public class Storage implements Serializable {

    private long totalSpace;
    private long freeSpace;
    private String partitionName;
    private String path;

    public Storage(long totalSpace, long freeSpace, String partitionName, String path) {
        this.totalSpace = totalSpace;
        this.freeSpace = freeSpace;
        this.partitionName = partitionName;
        this.path = path;
    }

    public Storage(File file) {
        this.partitionName = file.getName();
        this.freeSpace = file.getFreeSpace();
        this.totalSpace = file.getTotalSpace();
        this.path = file.getAbsolutePath();
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
        return "Storage{" +
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

        Storage that = (Storage) o;
        return path.equals(that.path);
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
