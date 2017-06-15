package epto.utils;

import java.util.HashMap;
import java.util.UUID;

/**
 * The main insight behind EpTO is a balls-and-bins approach to dissemination [19].
 * A balls-and-bins model abstracts processes as bins and messages (events) as balls, and studies how many balls
 * need to be thrown such that each bin receives at least a ball with arbitrarily high probability.
 */
public class Ball extends HashMap<UUID, Event> {

}
