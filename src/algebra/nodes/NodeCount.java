package algebra.nodes;

public enum NodeCount{

        NONE(0),
        ONE(1),
        TWO(2),
        UNLIMITED(Integer.MAX_VALUE);

        public final int nodes;

        NodeCount(int nodes) {
            this.nodes = nodes;
        }

        public int getNodes() {
            return nodes;
        }
    }
