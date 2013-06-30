package oripa.bind.state;

import java.awt.Component;
import java.awt.event.ActionListener;

import oripa.appstate.ApplicationState;
import oripa.paint.EditMode;
import oripa.paint.GraphicMouseAction;
import oripa.paint.PaintContext;

class LocalPaintBoundStateFactory {

	private ActionListener[] basicActions;
	private Component parent = null;

	PaintContext context = PaintContext.getInstance();

	/**
	 * 
	 * @param parent		A parent component. {@code null} indicates to avoid error on performActions() of created state.
	 * @param basicActions	Actions for all created states.
	 */
	public LocalPaintBoundStateFactory(Component parent, ActionListener[] basicActions) {
		this.basicActions = basicActions;
		this.parent = parent;
	}

	

	/**
	 * Create a state with error handler.
	 * @param mouseAction		Action for painting
	 * @param errorListener		For managing error on {@code performActions()} of created state.
	 * @param textID			ID for hint of painting.
	 * @param actions			Additional actions.
	 * @return
	 */
	public ApplicationState<EditMode> create(
			GraphicMouseAction mouseAction,
			ErrorListener errorListener, 
			String textID,
			ActionListener[] actions){
		
		PaintBoundState state = new PaintBoundState(
				parent, errorListener, mouseAction, textID, basicActions);

		state.addActions(actions);
		state.setErrorListener(errorListener);
		
		return state;
	}
	
	/**
	 * 
	 * Create a state.
	 * @param mouseAction		Action for painting
	 * @param textID			ID for hint of painting.
	 * @param actions			Additional actions.
	 * @return
	 */
	public ApplicationState<EditMode> create(
			GraphicMouseAction mouseAction,
			String textID,
			ActionListener[] actions){

		ApplicationState<EditMode> state = new PaintBoundState(
				mouseAction, textID, basicActions);

		state.addActions(actions);
		
		return state;
		
	}
	
	

}
