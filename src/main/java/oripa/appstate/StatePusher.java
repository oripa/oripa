package oripa.appstate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An action listener which pushes given state into a state manager.
 *
 * @author OUCHI Koji
 *
 */
public class StatePusher<GroupEnum> implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(StatePusher.class);

    private final StateManager<GroupEnum> stateManager;
    private final ApplicationState<GroupEnum> state;

    public StatePusher(final ApplicationState<GroupEnum> s, final StateManager<GroupEnum> stateManager) {
        state = s;
        this.stateManager = stateManager;
    }

    @Override
    public void run() {
        logger.debug("push paint bound state: " + state);
        stateManager.push(state);
    }
}