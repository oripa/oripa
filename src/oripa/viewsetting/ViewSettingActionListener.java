package oripa.viewsetting;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ViewSettingActionListener implements ActionListener{

	private ChangeViewSetting command;
	
	public ViewSettingActionListener(ChangeViewSetting command) {
		this.command = command;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		command.changeViewSetting();
	}
}
