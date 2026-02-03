package oripa.appstate;

import java.util.Optional;

/**
 * An action listener which pops the last state from given state manager.
 *
 * @author OUCHI Koji
 *
 */
public class StatePopper<GroupEnum> implements Runnable {
    private final StateManager<GroupEnum> stateManager;

    /**
     * Constructor
     */
    public StatePopper(final StateManager<GroupEnum> stateManager) {
        this.stateManager = stateManager;
    }

    @Override
    public void run() {
        Optional<ApplicationState<GroupEnum>> currentOpt = stateManager.pop();

        currentOpt.ifPresent(current -> current.performActions());
    }
}
