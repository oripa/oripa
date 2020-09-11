package oripa.appstate;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import oripa.bind.state.PaintBoundState;

public class StatePusher implements ActionListener {
	private final StateManager stateManager;
	private final PaintBoundState state;

	public StatePusher(final PaintBoundState s, final StateManager stateManager) {
		state = s;
		this.stateManager = stateManager;
	}

	@Override
	public void actionPerformed(final ActionEvent e) {
		stateManager.push(state);

	}
}