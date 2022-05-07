package oripa.gui.bind;

///**
// * A class for application-specific binding of state actions and buttons.
// *
// * @author koji
// *
// */
//@Deprecated
//public class PaintActionButtonFactory implements ButtonFactory {
//
//	private final PaintBoundStateFactory stateFactory;
//	private final PaintContext context;
//	private final MouseActionHolder actionHolder;
//	private final ScreenUpdater screenUpater;
//
//	public PaintActionButtonFactory(
//			final PaintBoundStateFactory stateFactory,
//			final PaintContext aContext,
//			final MouseActionHolder actionHolder,
//			final ScreenUpdater screenUpater) {
//		this.stateFactory = stateFactory;
//		context = aContext;
//		this.actionHolder = actionHolder;
//		this.screenUpater = screenUpater;
//
//	}
//
//	/*
//	 * (non-Javadoc)
//	 *
//	 * @see oripa.bind.ButtonFactory#create(java.awt.Component, java.lang.Class,
//	 * java.lang.String)
//	 */
//	@Override
//	public <TButton extends AbstractButton> TButton create(final Component parent,
//			final Class<TButton> buttonClass,
//			final String id,
//			final KeyListener keyListener) throws IllegalArgumentException {
//
//		ApplicationState<EditMode> state = stateFactory.create(
//				parent, actionHolder, context, screenUpater, id);
//
//		if (state == null) {
//			throw new IllegalArgumentException("Wrong ID for creating state");
//		}
//
//		ApplicationStateButtonBinder paintBinder = new ApplicationStateButtonBinder();
//		TButton button = paintBinder.createButton(buttonClass, state, id, keyListener);
//
//		return button;
//	}
//
//}
