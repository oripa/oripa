package oripa.bind;

import java.awt.event.ActionListener;
import java.util.Collection;

import javax.swing.AbstractButton;

import oripa.paint.GraphicMouseAction;

public interface BinderInterface<Command> {

	public abstract AbstractButton createButton(
			Class<? extends AbstractButton> buttonClass,
			Command command, String id);

	public abstract AbstractButton createMultiCommandButton(
			Class<? extends AbstractButton> buttonClass,
			Command[] commands, String id);
	
	public abstract Collection<ActionListener> getBoundListeners(
			Command command, String id);

	public abstract Collection<ActionListener> getBoundListeners(
			Command[] commands, String id);
}