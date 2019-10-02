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
import java.util.Collection;

import javax.swing.JFrame;

import oripa.fold.OrigamiModel;
import oripa.value.OriLine;

public class FoldabilityCheckFrame extends JFrame implements ActionListener {

    final FoldabilityScreen screen;

    public FoldabilityCheckFrame() {
        // Called when the "Check window" button is pressed.
        setTitle("Check Inputed data");
        screen = new FoldabilityScreen();
        setBounds(0, 0, 800, 800);
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(screen, BorderLayout.CENTER);

    }

    @Override
    public void actionPerformed(ActionEvent arg0) {
        // TODO Auto-generated method stub
    }

    public void setModel(
    		OrigamiModel origamiModel, 
    		Collection<OriLine> creasePattern  //, FoldedModelInfo foldedModelInfo
    		) {
    	screen.showModel(
    			origamiModel, creasePattern //, foldedModelInfo
    			);
    	//sthis.setVisible(true);
    }

}
