package edu.westminstercollege.cmpt355.minijava.node;

import java.util.List;
import java.util.Optional;

public record DeclarationItem(String name, Optional<Expression> initializer) implements Node {

    @Override
    public String getNodeDescription() {
        return String.format("DeclarationItem [name: %s]", name);
    }

    @Override
    public List<? extends Node> children() {
        return initializer.stream().toList();
    }
}
