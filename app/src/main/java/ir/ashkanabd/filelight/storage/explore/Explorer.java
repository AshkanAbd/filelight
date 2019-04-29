package ir.ashkanabd.filelight.storage.explore;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import ir.ashkanabd.filelight.storage.Storage;

public class Explorer {

    private Storage storage;
    private Node root;

    public Explorer(Storage storage) {
        this.storage = storage;
        root = new Node();
    }

    public void startExploring() {
        File rootFile = new File(storage.getPath());
        Node node = new Node(rootFile, root);
        DFS(node);
        root.addChild(node);
    }

    private void DFS(Node node) {
        File base = node.getFile();
        if (!base.isDirectory()) {
            return;
        }
        for (File file : base.listFiles()) {
            Node node1 = new Node(file, node);
            DFS(node1);
            node.setLength(node.getLength() + node1.getLength());
            node.addChild(node1);
        }
    }

    public Node getRoot() {
        return root;
    }
}
