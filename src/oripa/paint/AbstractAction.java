package oripa.paint;

import java.awt.geom.Point2D;

import javax.vecmath.Vector2d;

import oripa.paint.ActionState;

/**
 * a frame work of State pattern with undo, 
 * which can get back to previous state.
 * 
 * Call doAction() to perform the action of the state.
 * The flow of processing is:
 *  doAction(): onAct() -> quit proccessing if onAct() returns false -> onResult() -> finish!;
 *  unDo(): undoAction() -> finish!
 * both method returns ActionState to be used next time.
 * @author koji
 *
 */
public abstract class AbstractAction implements ActionState {
	private Class<? extends ActionState> next, prev;
	
//	/**
//	 * defines the final action of doAction().
//	 * usually you do nothing or push the result of action into ORIPA document class.
//	 * @param context
//	 */
//	public interface OnResultListener{
//
//		public void onResult(MouseContext context);
//	}
//	
//	private OnResultListener resultAction = null;
//
//	
//	
//	public OnResultListener getResultAction() {
//		return resultAction;
//	}
//
//	public void setResultAction(OnResultListener resultAction) {
//		this.resultAction = resultAction;
//	}
//
//	public PickingVertex(OnResultListener l){
//		resultAction = l;
//	}

	
	public AbstractAction(){
		initialize();
	}

	/**
	 * set next state class and previous state class here.
	 */
	protected abstract void initialize();	
	
	protected void setNextClass(Class<? extends ActionState> next){
		this.next = next;
	}
	
	protected void setPreviousClass(Class<? extends ActionState> prev){
		this.prev = prev;
	}

	/**
	 * Picks the nearest vertex and push it into context.
	 * @return Next state if vertex is found, else itself.
	 */
	@Override
	public final ActionState doAction(MouseContext context, 
			Point2D.Double currentPoint, boolean freeSelection) {

		boolean success = onAct(context, currentPoint, freeSelection);
				
		if(success == false){
			return this.cloneForNext();
		}

		onResult(context);

		if(next == null){
			System.out.println("null next state class");
			return this.cloneForNext();
		}

		ActionState nextState = null;

		try {
			nextState = next.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		
		return nextState;
	}
	
	protected abstract void onResult(MouseContext context);

	/**
	 * 
	 * @param context
	 * @param currentPoint
	 * @param freeSelection
	 * @return true if the action succeeded, otherwise false.
	 */
	protected abstract boolean onAct(MouseContext context, 
			Point2D.Double currentPoint, boolean freeSelection);
	
	/**
	 * cancel the current actions and returns previous state.
	 * @return Previous state
	 */
	@Override
	public final ActionState undo(MouseContext context) {
		
		undoAction(context);
		
		ActionState prevState = null;
		try {
			prevState = prev.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		
		return prevState;
	}


	/**
	 * implement undo action. clean up the garbages!
	 * (and change previous state class if you need.)
	 * @param context
	 */
	protected abstract void undoAction(MouseContext context);
	
	@Override
	public void setNextState(ActionState state){
		next = state.getClass();
	}

	@Override
	public void setPreviousState(ActionState state){
		prev = state.getClass();
	}

	@Override
	public ActionState getNextState() {
		ActionState state = null;

		try {
			state = next.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		
		return state;
	}

	@Override
	public ActionState getPreviousState() {
		ActionState state = null;

		try {
			state = prev.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		
		return state;
	}

	@Override
	public ActionState cloneForNext() {

//		ActionState state = null;
//		
//		try {
//			state = create(this.getClass(), getPreviousState(), getNextState());
//
//		} catch (InstantiationException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IllegalAccessException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
		ActionState state = null;
		try {
			state = this.getClass().newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		
		return state;
	}

//	public static ActionState create(
//			Class<? extends AbstractAction> stateClass, 
//			ActionState prev, ActionState next)
//					throws InstantiationException, IllegalAccessException{
//
//		AbstractAction state = stateClass.newInstance();
//
//		state.setNextState(next);
//		state.setPreviousState(prev);
//		
//		return state;
//	}
}
