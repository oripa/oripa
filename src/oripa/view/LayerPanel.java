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

import java.awt.Rectangle;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class LayerPanel extends JPanel {
    private static final long serialVersionUID = 1L;
    private JCheckBox jCheckBoxView0 = null;
    private JLabel jLabel = null;
    private JLabel jLabel1 = null;
    private JCheckBox jCheckBoxEdit0 = null;
    private JCheckBox jCheckBoxView1 = null;
    private JCheckBox jCheckBoxEdit1 = null;
    private JLabel jLabel2 = null;
    private JLabel jLabel3 = null;
    private JLabel jLabel4 = null;

    public LayerPanel() {
        super();
        initialize();
    }

    /**
     * This method initializes this
     * 
     * @return void
     */
    private void initialize() {
        jLabel4 = new JLabel();
        jLabel4.setBounds(new Rectangle(80, 40, 56, 21));
        jLabel4.setText("Aux Line");
        jLabel3 = new JLabel();
        jLabel3.setBounds(new Rectangle(80, 0, 38, 16));
        jLabel3.setText("Layer");
        jLabel2 = new JLabel();
        jLabel2.setBounds(new Rectangle(80, 20, 57, 21));
        jLabel2.setText("M/V Line");
        jLabel1 = new JLabel();
        jLabel1.setBounds(new Rectangle(40, 0, 31, 16));
        jLabel1.setText("Edit");
        jLabel = new JLabel();
        jLabel.setBounds(new Rectangle(0, 0, 38, 16));
        jLabel.setText("Show");
        this.setSize(300, 200);
        this.setLayout(null);
        this.add(getJCheckBoxView0(), null);
        this.add(jLabel, null);
        this.add(jLabel1, null);
        this.add(getJCheckBoxEdit0(), null);
        this.add(getJCheckBoxView1(), null);
        this.add(getJCheckBoxEdit1(), null);
        this.add(jLabel2, null);
        this.add(jLabel3, null);
        this.add(jLabel4, null);
    }

    /**
     * This method initializes jCheckBoxView0	
     * 	
     * @return javax.swing.JCheckBox	
     */
    private JCheckBox getJCheckBoxView0() {
        if (jCheckBoxView0 == null) {
            jCheckBoxView0 = new JCheckBox();
            jCheckBoxView0.setBounds(new Rectangle(5, 20, 26, 21));
        }
        return jCheckBoxView0;
    }

    /**
     * This method initializes jCheckBoxEdit0	
     * 	
     * @return javax.swing.JCheckBox	
     */
    private JCheckBox getJCheckBoxEdit0() {
        if (jCheckBoxEdit0 == null) {
            jCheckBoxEdit0 = new JCheckBox();
            jCheckBoxEdit0.setBounds(new Rectangle(45, 20, 26, 21));
        }
        return jCheckBoxEdit0;
    }

    /**
     * This method initializes jCheckBoxView1	
     * 	
     * @return javax.swing.JCheckBox	
     */
    private JCheckBox getJCheckBoxView1() {
        if (jCheckBoxView1 == null) {
            jCheckBoxView1 = new JCheckBox();
            jCheckBoxView1.setBounds(new Rectangle(5, 40, 26, 21));
        }
        return jCheckBoxView1;
    }

    /**
     * This method initializes jCheckBoxEdit1	
     * 	
     * @return javax.swing.JCheckBox	
     */
    private JCheckBox getJCheckBoxEdit1() {
        if (jCheckBoxEdit1 == null) {
            jCheckBoxEdit1 = new JCheckBox();
            jCheckBoxEdit1.setBounds(new Rectangle(45, 40, 26, 21));
        }
        return jCheckBoxEdit1;
    }
}
