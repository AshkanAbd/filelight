package ir.ashkanabd.filelight.storage;

import java.io.File;
import java.io.Serializable;

import androidx.annotation.NonNull;

public class Storage implements Serializable {

    public static int B = 0;
    public static int KB = 1;
    public static int MB = 2;
    public static int GB = 3;
    public static int TB = 4;

    private long totalSpace;
    private long freeSpace;
    private long usedSpace;
    private String name;
    private String path;

    public Storage(long totalSpace, long freeSpace, String name, String path) {
        this.totalSpace = totalSpace;
        this.freeSpace = freeSpace;
        this.usedSpace = totalSpace - freeSpace;
        this.name = name;
        this.path = path;
    }

    public Storage(File file) {
        this.name = file.getName();
        this.freeSpace = file.getFreeSpace();
        this.totalSpace = file.getTotalSpace();
        this.usedSpace = totalSpace - freeSpace;
        this.path = file.getAbsolutePath();
    }

    public double getPercent() {
        return 100 / (totalSpace / (double) usedSpace);
    }

    public long getUsedSpace() {
        return usedSpace;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @NonNull
    @Override
    public String toString() {
        return "Storage{" +
                "totalSpace=" + totalSpace +
                ", freeSpace=" + freeSpace +
                ", name='" + name + '\'' +
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
        result = 31 * result + name.hashCode();
        return result;
    }

    public static String getInBestFormat(double size) {
        int best = getStorageType(size);
        double d = round(size / Math.pow(10, best * 3));
        String s = Double.toString(d);
        switch (best) {
            case 0:
            default:
                return s + " B";
            case 1:
                return s + " KB";
            case 2:
                return s + " MB";
            case 3:
                return s + " GB";
            case 4:
                return s + " TB";
        }
    }

    public static double getInBestFormatDouble(double size) {
        int best = getStorageType(size);
        return round(size / Math.pow(10, best * 3));
    }

    public static int getStorageType(double d) {
        for (int i = 1; i < 5; i++) {
            if (Math.pow(10, i * 3) > d) {
                return i - 1;
            }
        }
        return 0;
    }

    private static double round(double d) {
        d = d * 100;
        d = Math.round(d);
        return d / 100;
    }
}
