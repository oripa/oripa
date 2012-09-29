package oripa.viewsetting;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ViewChangeListener implements ActionListener{

	private ChangeViewSetting command;
	
	public ViewChangeListener(ChangeViewSetting command) {
		this.command = command;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		command.changeViewSetting();
	}
}
