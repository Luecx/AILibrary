package neuralnetwork.builder;

import neuralnetwork.nodes.Node;

public class BuildException extends RuntimeException {

    public BuildException(Node node, String message) {
        super("[" + message + "] at Layer: " + node.getIdentifier());
    }

    public BuildException(algebra.nodes.Node node, String message) {
        super("[" + message + "] at Layer: " + node.getIdentifier());
    }
}
