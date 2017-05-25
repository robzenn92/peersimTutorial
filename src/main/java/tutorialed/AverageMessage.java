package tutorialed;

import peersim.core.Node;

/**
 * The type of a message. It contains a value of type double and the
 * sender node of type {@link peersim.core.Node}.
 */
public class AverageMessage {

    final double value;

    /**
     * If not null, this has to be answered, otherwise this is the answer.
     */
    final Node sender;

    public AverageMessage( double value, Node sender )
    {
        this.value = value;
        this.sender = sender;
    }
}
