package neuralnetwork.builder;

import neuralnetwork.nodes.Node;

public class BuildException extends Exception {

    public BuildException(Node node, String message) {
        super("[" + message + "] at Layer: " + node.getIdentifier());
    }
}
