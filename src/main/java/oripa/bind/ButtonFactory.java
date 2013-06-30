package oripa.bind;

import java.awt.Component;

import javax.swing.AbstractButton;

public interface ButtonFactory {

	public abstract AbstractButton create(Component parent,
			Class<? extends AbstractButton> buttonClass, String id);

}