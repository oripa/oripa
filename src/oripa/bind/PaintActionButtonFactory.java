package oripa.bind;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractButton;
import javax.swing.JMenuItem;
import javax.swing.JRadioButton;

import oripa.appstate.ApplicationState;
import oripa.bind.binder.ApplicationStateButtonBinder;
import oripa.bind.state.PaintBoundState;
import oripa.bind.state.PaintBoundStateFactory;
import oripa.paint.EditMode;
import oripa.paint.PaintContext;
import oripa.paint.addvertex.AddVertexAction;
import oripa.paint.bisector.AngleBisectorAction;
import oripa.paint.byvalue.LineByValueAction;
import oripa.paint.deleteline.DeleteLineAction;
import oripa.paint.deletevertex.DeleteVertexAction;
import oripa.paint.line.TwoPointLineAction;
import oripa.paint.linetype.ChangeLineTypeAction;
import oripa.paint.mirror.MirrorCopyAction;
import oripa.paint.pbisec.TwoPointBisectorAction;
import oripa.paint.segment.TwoPointSegmentAction;
import oripa.paint.selectline.SelectAllLineAction;
import oripa.paint.selectline.SelectLineAction;
import oripa.paint.symmetric.SymmetricalLineAction;
import oripa.paint.triangle.TriangleSplitAction;
import oripa.paint.vertical.VerticalLineAction;
import oripa.resource.StringID;
import oripa.viewsetting.ChangeViewSetting;
import oripa.viewsetting.ViewChangeListener;
import oripa.viewsetting.uipanel.ChangeOnAlterTypeButtonSelected;
import oripa.viewsetting.uipanel.ChangeOnByValueButtonSelected;
import oripa.viewsetting.uipanel.ChangeOnPaintInputButtonSelected;
import oripa.viewsetting.uipanel.ChangeOnOtherCommandButtonSelected;
import oripa.viewsetting.uipanel.ChangeOnSelectButtonSelected;


/**
 * A class for application-specific binding of state actions and buttons.
 * @author koji
 *
 */
public class PaintActionButtonFactory implements ButtonFactory {

	PaintContext context = PaintContext.getInstance();

	/* (non-Javadoc)
	 * @see oripa.bind.ButtonFactory#create(java.awt.Component, java.lang.Class, java.lang.String)
	 */
	@Override
	public AbstractButton create(Component parent,
			Class<? extends AbstractButton> buttonClass, String id){

		PaintBoundStateFactory stateFactory = 
				new PaintBoundStateFactory(parent, null);


		ApplicationState<EditMode> state = null;

		switch(id){
		case StringID.SELECT_ID:
			state = stateFactory.create(
					new SelectLineAction(context), id, 
					new ActionListener[] {new ViewChangeListener(new ChangeOnSelectButtonSelected())});
			break;
			
		case StringID.DELETE_LINE_ID:
			state =	stateFactory.create(
					new DeleteLineAction(), id, 
					new ActionListener[] {new ViewChangeListener(new ChangeOnOtherCommandButtonSelected())});		 
			break;

		case StringID.CHANGE_LINE_TYPE_ID:
			state = stateFactory.create(
					new ChangeLineTypeAction(), id, 
					new ActionListener[] {new ViewChangeListener(new ChangeOnAlterTypeButtonSelected())});
			break;
			
		case StringID.ADD_VERTEX_ID:
			state =	stateFactory.create(new AddVertexAction(), id, 
					new ActionListener[] {new ViewChangeListener(new ChangeOnOtherCommandButtonSelected())});
			break;
			
		case StringID.DELETE_VERTEX_ID:
			state =	stateFactory.create(new DeleteVertexAction(), id, 
					new ActionListener[] {new ViewChangeListener(new ChangeOnOtherCommandButtonSelected())});
			break;

		case StringID.EDIT_CONTOUR_ID:
			state = stateFactory.create(
					new EditOutlineActionWrapper(),	id, 
					new ActionListener[] {new ViewChangeListener(new ChangeOnOtherCommandButtonSelected())});
			break;
			
		case StringID.SELECT_ALL_LINE_ID:
			state = stateFactory.create(
					new SelectAllLineAction(context), id, 
					new ActionListener[] {new ViewChangeListener(new ChangeOnSelectButtonSelected())});
			break;
			
		case StringID.COPY_PASTE_ID:
			state = stateFactory.create(
					new CopyAndPasteActionWrapper(false),
					new CopyPasteErrorListener(), id, 
					new ActionListener[] {new ViewChangeListener(new ChangeOnSelectButtonSelected())});
			break;
			
		case StringID.CUT_PASTE_ID:
			state = stateFactory.create(
					new CopyAndPasteActionWrapper(true),
					new CopyPasteErrorListener(), id, 
					new ActionListener[] {new ViewChangeListener(new ChangeOnSelectButtonSelected())});
			break;


		default:
			state = createLineInputState(parent, buttonClass, id);
		}

		if(state == null){
			throw new NullPointerException("Wrong ID for creating state");
		}

		ApplicationStateButtonBinder paintBinder = 
				new  ApplicationStateButtonBinder();
		AbstractButton button = paintBinder.createButton(buttonClass, state, id);

		return button;
	}

	private ApplicationState<EditMode> createLineInputState(
			Component parent, 
			Class<? extends AbstractButton> buttonClass, String id){

		PaintBoundStateFactory stateFactory = new PaintBoundStateFactory(parent, 
				new ActionListener[] {new ViewChangeListener(new ChangeOnPaintInputButtonSelected())} );


		ApplicationState<EditMode> state = null;
		switch(id){
		case StringID.DIRECT_V_ID:

			state = stateFactory.create(new TwoPointSegmentAction(), 
					id, null);
			break;
			
		case StringID.ON_V_ID:
			state =	stateFactory.create(new TwoPointLineAction(), 
					id, null);
			break;
		case StringID.VERTICAL_ID:
			state = stateFactory.create(new VerticalLineAction(), 
					id, null);
			break;
			
		case StringID.BISECTOR_ID:
			state = stateFactory.create(new AngleBisectorAction(), 
					id, null);
			break;
			
		case StringID.TRIANGLE_ID:
			state = stateFactory.create(new TriangleSplitAction(), 
					id, null);

			break;

		case StringID.SYMMETRIC_ID:
			state = stateFactory.create(new SymmetricalLineAction(), 
					id, null);

			break;
		case StringID.MIRROR_ID:
			state = stateFactory.create(new MirrorCopyAction(context), 
					id, null);

			break;

		case StringID.BY_VALUE_ID:
			PaintBoundStateFactory byValueFactory = new PaintBoundStateFactory(
					parent, new ActionListener[] {new ViewChangeListener(new ChangeOnByValueButtonSelected())});

			state = byValueFactory.create(new LineByValueAction(), 
					id,	null );

			break;

		case StringID.PERPENDICULAR_BISECTOR_ID:
			state = stateFactory.create(new TwoPointBisectorAction(), 
					id, null);

		}

		return state;
	}
}
