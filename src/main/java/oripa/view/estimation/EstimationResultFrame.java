/**
 * ORIPA - Origami Pattern Editor 
 * Copyright (C) 2005-2009 Jun Mitani http://mitani.cs.tsukuba.ac.jp/

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package oripa.view.estimation;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JFrame;
import javax.swing.JLabel;

import oripa.viewsetting.estimation.RenderFrameSettingDB;


public class EstimationResultFrame extends JFrame implements ActionListener, Observer {

	private final RenderFrameSettingDB setting = RenderFrameSettingDB.getInstance();

	final FoldedModelScreen screen;
    final EstimationResultUI ui;
    public final JLabel hintLabel;

    public EstimationResultFrame() {
    	setting.addObserver(this);
    	
        setTitle("Folded Origami");
        screen = new FoldedModelScreen();
        ui = new EstimationResultUI();
        ui.setScreen(screen);
        hintLabel = new JLabel("L: Rotate / Wheel: Zoom");
        setBounds(0, 0, 800, 600);
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(ui, BorderLayout.WEST);
        getContentPane().add(screen, BorderLayout.CENTER);
        getContentPane().add(hintLabel, BorderLayout.SOUTH);

    }

    @Override
    public void actionPerformed(ActionEvent arg0) {
        // TODO Auto-generated method stub
    }
    
    @Override
    public void update(Observable o, Object arg) {

    	
    	if(setting.isFrameVisible()){
	    	screen.resetViewMatrix();
			screen.redrawOrigami();
			ui.updateLabel();
			setVisible(true);
    	}    	
    }
}
