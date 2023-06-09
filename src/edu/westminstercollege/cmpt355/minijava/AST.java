package edu.westminstercollege.cmpt355.minijava;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import edu.westminstercollege.cmpt355.minijava.node.*;

public class AST {

    @FunctionalInterface
    public interface NodeConsumer<E extends Exception> {
        void consume(Node node) throws E;
    }

    public static void print(Node root) {
        print(root, new PrintWriter(System.out));
    }

    public static void print(Node root, PrintWriter out) {
        out.println(root.getNodeDescription());
        var it = root.children().iterator();
        while (it.hasNext())
            print(it.next(), out, List.of(it.hasNext()));
        out.flush();
    }

    private static void print(Node root, PrintWriter out, List<Boolean> levels) {
        for (boolean b : levels.subList(0, levels.size() - 1))
            out.printf("%3s", b ? "│ " : "");
        out.printf("%3s ", levels.get(levels.size() - 1) ? "├─" : "└─");

        String nodeDescription = (root == null) ? "<< null >>" : root.getNodeDescription();
        String[] lines = nodeDescription.split("\n");
        out.println(lines[0]);
        for (int i = 1; i < lines.length; ++i) {
            for (boolean b: levels)
                out.printf("%3s", b ? "│ " : "");
            out.printf(" %s\n", lines[i]);
        }

        if (root == null)
            return;

        var children = root.children();
        if (children == null) {
            for (boolean b: levels)
                out.printf("%3s", b ? "│ " : "");
            out.println(" << null children! >>");
            return;
        }

        var it = root.children().iterator();
        while (it.hasNext()) {
            var childLevels = new ArrayList<>(levels);
            var child = it.next();
            childLevels.add(it.hasNext());
            print(child, out, childLevels);
        }
    }

    public static <E extends Exception> void preOrder(Node root, NodeConsumer<E> c) throws E {
        c.consume(root);

        for (var child : root.children())
            preOrder(child, c);
    }

    public static <E extends Exception> void postOrder(Node root, NodeConsumer<E> c) throws E {
        for (var child : root.children())
            postOrder(child, c);

        c.consume(root);
    }

    public static void checkForNulls(Node root) {
        checkForNulls(List.of(), root);
    }

    private static void checkForNulls(List<Node> path, Node next) {
        var descriptions = path.stream().map(Node::getNodeDescription).toList();
        var pathString = String.join(" → ", descriptions) + " → null";
        if (path.isEmpty())
            pathString = "root node is null";

        if (next == null)
            throw new NullPointerException("Null child found in AST: " + pathString);

        var children = next.children();
        if (children == null)
            throw new NullPointerException("Node's children() returns null: " + pathString);

        var subPath = new ArrayList<>(path);
        subPath.add(next);

        children.forEach(child -> checkForNulls(subPath, child));
    }
}
