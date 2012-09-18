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

package oripa.view;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JFrame;
import javax.swing.JLabel;

import oripa.viewsetting.render.RenderFrameSettingDB;


public class RenderFrame extends JFrame implements ActionListener, Observer {

	private RenderFrameSettingDB setting = RenderFrameSettingDB.getInstance();

	RenderScreen2 screen;
    RenderUI ui;
    public JLabel hintLabel;

    public RenderFrame() {
    	setting.addObserver(this);
    	
        setTitle("Folded Origami");
        screen = new RenderScreen2();
        ui = new RenderUI();
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
