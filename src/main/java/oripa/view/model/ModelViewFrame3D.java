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

package oripa.view.model;

import java.awt.BorderLayout;
import java.awt.GraphicsConfiguration;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;

import com.sun.j3d.utils.universe.SimpleUniverse;

/**
 * A frame for verifying algorithm.
 * @author Koji
 *
 */
public class ModelViewFrame3D extends JFrame implements ActionListener {
    final ModelViewScreen3D screen;

    public ModelViewFrame3D() {
        setTitle("3D Origami Model Rendering");
        GraphicsConfiguration config = SimpleUniverse.getPreferredConfiguration();
        screen = new ModelViewScreen3D(config);
        
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(screen, BorderLayout.CENTER);
    }
    
    @Override
    public void actionPerformed(ActionEvent arg0) {
        // TODO Auto-generated method stub
    }
}
