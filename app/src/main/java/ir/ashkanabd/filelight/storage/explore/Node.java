package ir.ashkanabd.filelight.storage.explore;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;

public class Node {
    private boolean root;
    private File file;
    private long length;
    private List<Node> children;
    private Node parent;

    public Node() {
        root = true;
        parent = null;
        children = new ArrayList<>();
    }

    public Node(File file, Node parent) {
        root = false;
        this.file = file;
        this.parent = parent;
        children = new ArrayList<>();
        length = file.length();
    }

    public void addChild(Node node) {
        children.add(node);
    }

    public List<Node> getChildren() {
        return children;
    }

    public boolean isRoot() {
        return root;
    }

    public Node getParent() {
        return parent;
    }

    public File getFile() {
        return file;
    }

    public void setLength(long length) {
        this.length = length;
    }

    public long getLength() {
        return length;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Node node = (Node) o;

        if (file != null ? !file.equals(node.file) : node.file != null) return false;
        return parent != null ? parent.equals(node.parent) : node.parent == null;
    }

    @Override
    public int hashCode() {
        return file != null ? file.hashCode() : 0;
    }

    @NonNull
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder("Node:{ ");
        builder.append("file= ").append(file);
        builder.append(", length= ").append(length);
        if (root) {
            builder.append(", root");
        }
        if (!children.isEmpty()) {
            builder.append(", children= ").append(children);
        }
        builder.append(" }");
        return builder.toString();
    }
}
