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

package oripa.view.main;

import java.awt.Frame;
import java.awt.Rectangle;
import javax.swing.*;

import oripa.ORIPA;

public class CircleCopyDialog extends JDialog {
    private static final long serialVersionUID = 1L;
    private JPanel jContentPane = null;
    private JButton jButtonOK = null;
    private JButton jButtonCancel = null;
    private JLabel jLabel = null;
    private JLabel jLabel1 = null;
    private JLabel jLabel2 = null;
    private JTextField jTextFieldCX = null;
    private JTextField jTextFieldCY = null;
    private JTextField jTextFieldAngle = null;
    private JLabel jLabel3 = null;
    private JTextField jTextFieldNum = null;
    
    private double m_cx = 0;
    private double m_cy = 0;
    private double m_angleDeg = 30;
    private int m_num = 1;

    /**
     * @param owner
     */
    public CircleCopyDialog(Frame owner) {
        super(owner);
        initialize();
    }

    /**
     * This method initializes this
     * 
     * @return void
     */
    private void initialize() {
        this.setSize(160, 171);
        this.setTitle("CircleCoppy");
        this.setLocation(ORIPA.mainFrame.getLocation().x + 200, ORIPA.mainFrame.getLocation().y + 100);
        this.setContentPane(getJContentPane());
    }

    /**
     * This method initializes jContentPane
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getJContentPane() {
        if (jContentPane == null) {
            jLabel3 = new JLabel();
            jLabel3.setBounds(new Rectangle(5, 80, 71, 21));
            jLabel3.setText("Number");
            jLabel2 = new JLabel();
            jLabel2.setBounds(new Rectangle(5, 55, 71, 21));
            jLabel2.setText("Angle(deg)");
            jLabel1 = new JLabel();
            jLabel1.setBounds(new Rectangle(5, 30, 61, 21));
            jLabel1.setText("Center Y");
            jLabel = new JLabel();
            jLabel.setBounds(new Rectangle(5, 5, 61, 21));
            jLabel.setToolTipText("");
            jLabel.setText("Center X");
            jContentPane = new JPanel();
            jContentPane.setLayout(null);
            jContentPane.add(getJButtonOK(), null);
            jContentPane.add(getJButtonCancel(), null);
            jContentPane.add(jLabel, null);
            jContentPane.add(jLabel1, null);
            jContentPane.add(jLabel2, null);
            jContentPane.add(getJTextFieldCX(), null);
            jContentPane.add(getJTextFieldCY(), null);
            jContentPane.add(getJTextFieldAngle(), null);
            jContentPane.add(jLabel3, null);
            jContentPane.add(getJTextFieldNum(), null);
        }
        return jContentPane;
    }

    /**
     * This method initializes jButtonOK	
     * 	
     * @return javax.swing.JButton	
     */
    private JButton getJButtonOK() {
        if (jButtonOK == null) {
            jButtonOK = new JButton();
            jButtonOK.setBounds(new Rectangle(5, 110, 56, 21));
            jButtonOK.setText("OK");
            jButtonOK.addActionListener(new java.awt.event.ActionListener() {
                @Override
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    System.out.println("actionPerformed()");
                    
                    try{
                        m_cx = Double.valueOf(jTextFieldCX.getText());
                    } catch(Exception ex) {
                        m_cx = 0;
                    }
                    
                    try {
                        m_cy = Double.valueOf(jTextFieldCY.getText());
                    } catch(Exception ex) {
                        m_cy = 0;
                    }
                    
                    try {
                        m_angleDeg = Double.valueOf(jTextFieldAngle.getText());
                    } catch(Exception ex) {
                        m_angleDeg = 0;
                    }
                    
                    try{ 
                        m_num = Integer.valueOf(jTextFieldNum.getText());
                    } catch(Exception ex) {
                        m_num = 0;
                    }
                                           
                    if(m_num <= 0) {
                        JOptionPane.showMessageDialog(
                                ORIPA.mainFrame , "Specify positive integer to Number.", "ArrayCopy" ,
                                JOptionPane.INFORMATION_MESSAGE
                            );            
                        
                    } else {
                        ORIPA.doc.CircleCopy(m_cx, m_cy, m_angleDeg, m_num);
                        ORIPA.mainFrame.repaint();
                        setVisible(false);
                    }
                }
            });
        }
        return jButtonOK;
    }

    /**
     * This method initializes jButtonCancel	
     * 	
     * @return javax.swing.JButton	
     */
    private JButton getJButtonCancel() {
        if (jButtonCancel == null) {
            jButtonCancel = new JButton();
            jButtonCancel.setBounds(new Rectangle(65, 110, 81, 21));
            jButtonCancel.setText("Cancel");
            jButtonCancel.addActionListener(new java.awt.event.ActionListener() {
                @Override
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    System.out.println("actionPerformed()"); 
                    setVisible(false);
                }
            });
        }
        return jButtonCancel;
    }

    /**
     * This method initializes jTextFieldCX	
     * 	
     * @return javax.swing.JTextField	
     */
    private JTextField getJTextFieldCX() {
        if (jTextFieldCX == null) {
            jTextFieldCX = new JTextField();
            jTextFieldCX.setBounds(new Rectangle(80, 5, 66, 21));
            jTextFieldCX.setText("0");
            jTextFieldCX.setHorizontalAlignment(JTextField.RIGHT);
        }
        return jTextFieldCX;
    }

    /**
     * This method initializes jTextFieldCY	
     * 	
     * @return javax.swing.JTextField	
     */
    private JTextField getJTextFieldCY() {
        if (jTextFieldCY == null) {
            jTextFieldCY = new JTextField();
            jTextFieldCY.setBounds(new Rectangle(80, 30, 66, 21));
            jTextFieldCY.setText("0");
            jTextFieldCY.setHorizontalAlignment(JTextField.RIGHT);
        }
        return jTextFieldCY;
    }

    /**
     * This method initializes jTextFieldAngle	
     * 	
     * @return javax.swing.JTextField	
     */
    private JTextField getJTextFieldAngle() {
        if (jTextFieldAngle == null) {
            jTextFieldAngle = new JTextField();
            jTextFieldAngle.setBounds(new Rectangle(80, 55, 66, 21));
            jTextFieldAngle.setText("30");
            jTextFieldAngle.setHorizontalAlignment(JTextField.RIGHT);
        }
        return jTextFieldAngle;
    }

    /**
     * This method initializes jTextFieldNum	
     * 	
     * @return javax.swing.JTextField	
     */
    private JTextField getJTextFieldNum() {
        if (jTextFieldNum == null) {
            jTextFieldNum = new JTextField();
            jTextFieldNum.setBounds(new Rectangle(80, 80, 66, 21));
            jTextFieldNum.setText("1");
            jTextFieldNum.setHorizontalAlignment(JTextField.RIGHT);
        }
        return jTextFieldNum;
    }
}  //  @jve:decl-index=0:visual-constraint="10,10"
