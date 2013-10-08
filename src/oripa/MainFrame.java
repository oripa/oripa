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


package oripa;

import java.awt.BorderLayout;
import java.awt.Rectangle;
import java.awt.event.*;
import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.*;
import java.util.ArrayList;
import javax.swing.*;


class FileFilterEx extends javax.swing.filechooser.FileFilter {

    private String extensions[];
    private String msg;

    public FileFilterEx(String[] extensions, String msg) {
        this.extensions = extensions;
        this.msg = msg;
    }

    @Override
    public boolean accept(java.io.File f) {
        for (int i = 0; i < extensions.length; i++) {
            if (f.isDirectory()) {
                return true;
            }
            if (f.getName().endsWith(extensions[i])) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String getDescription() {
        return msg;
    }
}

public class MainFrame extends JFrame implements ActionListener, ComponentListener, WindowListener {

    MainScreen mainScreen;
    public ArrayList<String> MRUFiles = new ArrayList<>();
    private JMenu menuFile = new JMenu(ORIPA.res.getString("File"));
    private JMenu menuEdit = new JMenu(ORIPA.res.getString("Edit"));
    private JMenu menuHelp = new JMenu(ORIPA.res.getString("Help"));
    private JMenuItem menuItemClear = new JMenuItem(ORIPA.res.getString("New"));
    private JMenuItem menuItemOpen = new JMenuItem(ORIPA.res.getString("Open"));
    private JMenuItem menuItemSave = new JMenuItem(ORIPA.res.getString("Save"));
    private JMenuItem menuItemSaveAs = new JMenuItem(ORIPA.res.getString("SaveAs"));
    private JMenuItem menuItemExportDXF = new JMenuItem("Export DXF");
    private JMenuItem menuItemExportOBJ = new JMenuItem("Export OBJ");
    private JMenuItem menuItemExportCP = new JMenuItem("Export CP");
    private JMenuItem menuItemExportSVG = new JMenuItem("Export SVG");
    private JMenuItem menuItemChangeOutline = new JMenuItem(ORIPA.res.getString("EditContour"));
    private JMenuItem menuItemProperty = new JMenuItem(ORIPA.res.getString("Property"));
    private JMenuItem menuItemExit = new JMenuItem(ORIPA.res.getString("Exit"));
    private JMenuItem menuItemUndo = new JMenuItem(ORIPA.res.getString("Undo"));
    private JMenuItem menuItemAbout = new JMenuItem(ORIPA.res.getString("About"));
    private JMenuItem menuItemRepeatCopy = new JMenuItem("Array Copy");
    private JMenuItem menuItemCircleCopy = new JMenuItem("Circle Copy");
    private JMenuItem menuItemUnSelectAll = new JMenuItem("UnSelect All");
    private JMenuItem menuItemSelectAll = new JMenuItem("Select All");
    private JMenuItem menuItemDeleteSelectedLines = new JMenuItem("Delete Selected Lines");
    private JMenuItem menuItemCopyAndPaste = new JMenuItem("Copy and Paste");
    private JMenuItem[] MRUFilesMenuItem = new JMenuItem[Config.MRUFILE_NUM];
    private String lastPath = "";
    private RepeatCopyDialog arrayCopyDialog;
    private CircleCopyDialog circleCopyDialog;
    public static JLabel hintLabel = new JLabel();
    public UIPanel uiPanel;

    MainFrame() {
        mainScreen = new MainScreen();
        addWindowListener(this);
        uiPanel = new UIPanel(mainScreen);
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(uiPanel, BorderLayout.WEST);
        getContentPane().add(mainScreen, BorderLayout.CENTER);
        getContentPane().add(hintLabel, BorderLayout.SOUTH);
        this.setIconImage(new ImageIcon(getClass().getResource("/icon/oripa.gif")).getImage());

        menuItemOpen.addActionListener(this);
        menuItemOpen.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK));
        menuItemSave.addActionListener(this);
        menuItemSave.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK));
        menuItemSaveAs.addActionListener(this);
        menuItemExit.addActionListener(this);
        menuItemExit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, ActionEvent.CTRL_MASK));
        menuItemUndo.addActionListener(this);
        menuItemUndo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, ActionEvent.CTRL_MASK));
        menuItemClear.addActionListener(this);
        menuItemClear.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, ActionEvent.CTRL_MASK));
        menuItemAbout.addActionListener(this);
        menuItemExportDXF.addActionListener(this);
        menuItemExportOBJ.addActionListener(this);
        menuItemExportCP.addActionListener(this);
        menuItemExportSVG.addActionListener(this);
        menuItemProperty.addActionListener(this);
        menuItemChangeOutline.addActionListener(this);
        menuItemRepeatCopy.addActionListener(this);
        menuItemCircleCopy.addActionListener(this);
        menuItemSelectAll.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, ActionEvent.CTRL_MASK));
        
        menuItemSelectAll.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                ORIPA.doc.selectAllOriLines();
                Globals.editMode = Constants.EditMode.PICK_LINE;
                uiPanel.editModePickLineButton.setSelected(true);
                uiPanel.modeChanged();
                mainScreen.repaint();
            }
        });
        
        menuItemUnSelectAll.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0));
        
        menuItemUnSelectAll.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                ORIPA.doc.resetSelectedOriLines();
                mainScreen.repaint();
            }
        });
        
        menuItemDeleteSelectedLines.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                ORIPA.doc.pushUndoInfo();
                ORIPA.doc.deleteSelectedLines();
                mainScreen.repaint();
            }
        });
        menuItemDeleteSelectedLines.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0));

        menuItemCopyAndPaste.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, ActionEvent.CTRL_MASK));
        
        menuItemCopyAndPaste.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                if (ORIPA.doc.getSelectedLineNum() == 0) {
                    JOptionPane.showMessageDialog(
                            ORIPA.mainFrame, "Select target lines", "Copy and Paste",
                            JOptionPane.WARNING_MESSAGE);

                } else {
                    Globals.editMode = Constants.EditMode.INPUT_LINE;
                    Globals.lineInputMode = Constants.LineInputMode.COPY_AND_PASTE;
                    uiPanel.editModeInputLineButton.setSelected(true);
                    uiPanel.modeChanged();
                    ORIPA.doc.prepareForCopyAndPaste();
                    mainScreen.repaint();
                }
            }
        });

        for (int i = 0; i < Config.MRUFILE_NUM; i++) {
            MRUFilesMenuItem[i] = new JMenuItem();
            MRUFilesMenuItem[i].addActionListener(this);
        }

        loadIniFile();

        // Building the menu bar
        JMenuBar menuBar = new JMenuBar();
        buildMenuFile();

        menuEdit.add(menuItemCopyAndPaste);
        menuEdit.add(menuItemRepeatCopy);
        menuEdit.add(menuItemCircleCopy);
        menuEdit.add(menuItemSelectAll);
        menuEdit.add(menuItemUnSelectAll);
        menuEdit.add(menuItemDeleteSelectedLines);
        menuEdit.add(menuItemUndo);
        menuEdit.add(menuItemChangeOutline);

        menuHelp.add(menuItemAbout);

        menuBar.add(menuFile);
        menuBar.add(menuEdit);
        menuBar.add(menuHelp);
        setJMenuBar(menuBar);
        updateHint();
    }

    public void initialize() {
        arrayCopyDialog = new RepeatCopyDialog(this);
        circleCopyDialog = new CircleCopyDialog(this);
    }

    public static void updateHint() {
        String message = "";

        if (Globals.editMode == Constants.EditMode.INPUT_LINE) {
            if (Globals.lineInputMode == Constants.LineInputMode.DIRECT_V) {
                message = ORIPA.res.getString("Direction_DirectV");
            } else if (Globals.lineInputMode == Constants.LineInputMode.ON_V) {
                message = ORIPA.res.getString("Direction_OnV");
            } else if (Globals.lineInputMode == Constants.LineInputMode.OVERLAP_V) {
            } else if (Globals.lineInputMode == Constants.LineInputMode.OVERLAP_E) {
            } else if (Globals.lineInputMode == Constants.LineInputMode.SYMMETRIC_LINE) {
                message = ORIPA.res.getString("Direction_Symmetric");
            } else if (Globals.lineInputMode == Constants.LineInputMode.TRIANGLE_SPLIT) {
                message = ORIPA.res.getString("Direction_TriangleSplit");
            } else if (Globals.lineInputMode == Constants.LineInputMode.BISECTOR) {
                message = ORIPA.res.getString("Direction_Bisector");
            } else if (Globals.lineInputMode == Constants.LineInputMode.PBISECTOR) {
                message = "Input Perpendicular Bisector of two vertices. Select two vertices by left click.";
            } else if (Globals.lineInputMode == Constants.LineInputMode.VERTICAL_LINE) {
                message = ORIPA.res.getString("Direction_VerticalLine");
            } else if (Globals.lineInputMode == Constants.LineInputMode.MIRROR) {
                message = ORIPA.res.getString("Direction_Mirror");
            } else if (Globals.lineInputMode == Constants.LineInputMode.BY_VALUE) {
                if (Globals.subLineInputMode == Constants.SubLineInputMode.NONE) {
                    message = ORIPA.res.getString("Direction_ByValue");
                } else if (Globals.subLineInputMode == Constants.SubLineInputMode.PICK_LENGTH) {
                    message = ORIPA.res.getString("Direction_PickLength");
                } else if (Globals.subLineInputMode == Constants.SubLineInputMode.PICK_ANGLE) {
                    message = ORIPA.res.getString("Direction_PickAngle");
                }
            } else if (Globals.lineInputMode == Constants.LineInputMode.COPY_AND_PASTE) {
                message = "Left Click for Paste. Right Click for End.";
            }
        } else if (Globals.editMode == Constants.EditMode.CHANGE_LINE_TYPE) {
            message = "Click a line then the type switches (Press Ctrl key for switching to CUT Line).";
        } else if (Globals.editMode == Constants.EditMode.DELETE_LINE) {
            message = ORIPA.res.getString("Direction_DeleteLine");
        } else if (Globals.editMode == Constants.EditMode.PICK_LINE) {
            message = "Select/UnSelect Lines by Left Click or Left Drag";
        } else if (Globals.editMode == Constants.EditMode.ADD_VERTEX) {
            message = ORIPA.res.getString("Direction_AddVertex");
        } else if (Globals.editMode == Constants.EditMode.DELETE_VERTEX) {
            message = ORIPA.res.getString("Direction_DeleteVertex");
        } else if (Globals.editMode == Constants.EditMode.EDIT_OUTLINE) {
            message = ORIPA.res.getString("Direction_EditContour");
        }

        hintLabel.setText("    " + message);
        hintLabel.repaint();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // Check the last opened files
        for (int i = 0; i < Config.MRUFILE_NUM; i++) {
            if (e.getSource() == MRUFilesMenuItem[i]) {
                try {
                    String filePath = MRUFilesMenuItem[i].getText();
                    openFile(filePath);
                    updateMenu(filePath);
                    updateTitleText();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(
                            this, e.toString(), ORIPA.res.getString("Error_FileLoadFailed"),
                            JOptionPane.ERROR_MESSAGE);
                }
                mainScreen.repaint();
                return;
            }
        }

        if (e.getSource() == menuItemOpen) {
            fileOpen();
            mainScreen.repaint();
            updateTitleText();
        } else if (e.getSource() == menuItemSave) {
            if (!(ORIPA.doc.dataFilePath).equals("")) {
                saveFile(ORIPA.doc.dataFilePath);
            } else {
                saveFile();
                updateTitleText();
            }
        } else if (e.getSource() == menuItemSaveAs) {
            saveFile();
            updateTitleText();
        } else if (e.getSource() == menuItemExportDXF) {
            exportFile("dxf");
        } else if (e.getSource() == menuItemExportOBJ) {
            exportFile("obj");
        } else if (e.getSource() == menuItemExportCP) {
            exportFile("cp");
        } else if (e.getSource() == menuItemExportSVG) {
            exportFile("svg");
        } else if (e.getSource() == menuItemChangeOutline) {
            Globals.preEditMode = Globals.editMode;
            Globals.editMode = Constants.EditMode.EDIT_OUTLINE;
            uiPanel.modeChanged();
        } else if (e.getSource() == menuItemExit) {
            saveIniFile();
            System.exit(0);
        } else if (e.getSource() == menuItemUndo) {
            ORIPA.doc.popUndoInfo();
            mainScreen.repaint();
        } else if (e.getSource() == menuItemClear) {
            ORIPA.doc = new Doc(Constants.DEFAULT_PAPER_SIZE);
            ORIPA.modelFrame.repaint();
            mainScreen.modeChanged();
            ORIPA.modelFrame.setVisible(false);
            ORIPA.renderFrame.setVisible(false);
            ORIPA.mainFrame.uiPanel.dispGridCheckBox.setSelected(true);
            ORIPA.mainFrame.mainScreen.setDispGrid(true);
            updateTitleText();
        } else if (e.getSource() == menuItemAbout) {
            JOptionPane.showMessageDialog(
                    this, ORIPA.infoString, ORIPA.res.getString("Title"),
                    JOptionPane.INFORMATION_MESSAGE);
        } else if (e.getSource() == menuItemProperty) {
            PropertyDialog dialog = new PropertyDialog(this);
            dialog.setValue();
            Rectangle rec = getBounds();
            dialog.setLocation((int) (rec.getCenterX() - dialog.getWidth() / 2),
                    (int) (rec.getCenterY() - dialog.getHeight() / 2));
            dialog.setModal(true);
            dialog.setVisible(true);
        } else if (e.getSource() == menuItemRepeatCopy) {
            if (ORIPA.doc.getSelectedLineNum() == 0) {
                JOptionPane.showMessageDialog(
                        this, "Select target lines", "ArrayCopy",
                        JOptionPane.WARNING_MESSAGE);

            } else {
                arrayCopyDialog.setVisible(true);
            }
        } else if (e.getSource() == menuItemCircleCopy) {
            if (ORIPA.doc.getSelectedLineNum() == 0) {
                JOptionPane.showMessageDialog(
                        this, "Select target lines", "ArrayCopy",
                        JOptionPane.WARNING_MESSAGE);

            } else {
                circleCopyDialog.setVisible(true);
            }
        }

    }

    public void updateTitleText() {
        String fileName;
        if ((ORIPA.doc.dataFilePath).equals("")) {
            fileName = ORIPA.res.getString("DefaultFileName");
        } else {
            File file = new File(ORIPA.doc.dataFilePath);
            fileName = file.getName();
        }

        setTitle(fileName + " - " + ORIPA.TITLE);
    }

    public void exportFile(String ext) {
        if ("obj".equals(ext)) {
            if (!ORIPA.doc.hasModel) {
                if (!ORIPA.doc.buildOrigami(true)) {
                    JOptionPane.showConfirmDialog(
                            null, "Warning: Building a set of polygons from crease pattern "
                            + "was failed.", "Warning",
                            JOptionPane.OK_OPTION, JOptionPane.WARNING_MESSAGE);
                }
            }
        }

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.addChoosableFileFilter(new FileFilterEx(new String[]{"." + ext}, 
                "(*." + ext + ")" + ext + ORIPA.res.getString("File")));
        if (JFileChooser.APPROVE_OPTION == fileChooser.showSaveDialog(this)) {
            try {
                String filePath = fileChooser.getSelectedFile().getPath();
                File file = new File(filePath);
                if (file.exists()) {
                    if (JOptionPane.showConfirmDialog(
                            null, ORIPA.res.getString("Warning_SameNameFileExist"), 
                            ORIPA.res.getString("DialogTitle_FileSave"),
                            JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE) 
                            != JOptionPane.YES_OPTION) {
                        return;
                    }
                }

                if (!filePath.endsWith("." + ext)) {
                    filePath += "." + ext;
                }
                switch (ext) {
                    case "dxf":
                        ExporterDXF.export(ORIPA.doc, filePath);
                        break;
                    case "obj":
                        ExporterOBJ.export(ORIPA.doc, filePath);
                        break;
                    case "cp":
                        ExporterCP.export(ORIPA.doc, filePath);
                        break;
                    case "svg":
                        ExporterSVG.exportDotted(ORIPA.doc, filePath);
                        break;
                }
                lastPath = filePath;
            } catch (Exception e) {
                JOptionPane.showMessageDialog(
                        this, e.toString(), ORIPA.res.getString("Error_FileSaveFaild"),
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void buildMenuFile() {
        menuFile.removeAll();

        menuFile.add(menuItemClear);
        menuFile.add(menuItemOpen);
        menuFile.add(menuItemSave);
        menuFile.add(menuItemSaveAs);
        menuFile.add(menuItemExportDXF);
        menuFile.add(menuItemExportOBJ);
        menuFile.add(menuItemExportCP);
        menuFile.add(menuItemExportSVG);
        menuFile.addSeparator();
        menuFile.add(menuItemProperty);
        menuFile.addSeparator();

        for (int i = 0; i < Config.MRUFILE_NUM; i++) {
            int index = MRUFiles.size() - 1 - i;
            if (index >= 0) {
                String path = MRUFiles.get(index);
                MRUFilesMenuItem[i].setText(path);
                menuFile.add(MRUFilesMenuItem[i]);
            } else {
                MRUFilesMenuItem[i].setText("");
            }
        }

        menuFile.addSeparator();
        menuFile.add(menuItemExit);
    }

    public void updateMenu(String filePath) {
        if (MRUFiles.contains(filePath)) {
            return;
        }

        MRUFiles.add(filePath);

        buildMenuFile();
    }

    private void fileOpen() {
        JFileChooser fileChooser = new JFileChooser(lastPath);
        fileChooser.addChoosableFileFilter(
                new FileFilterEx(new String[]{".cp"}, "(*.cp) original Crease Pattern file"));
        fileChooser.addChoosableFileFilter(
                new FileFilterEx(new String[]{".pdf"}, "(*.pdf) PDF file"));
        fileChooser.addChoosableFileFilter(
                new FileFilterEx(new String[]{".dxf"}, "(*.dxf) DXF file"));
        fileChooser.addChoosableFileFilter(
                new FileFilterEx(new String[]{".opx", ".xml"}, "(*.opx, *.xml) " + 
                ORIPA.res.getString("ORIPA_File")));
        if (JFileChooser.APPROVE_OPTION == fileChooser.showOpenDialog(this)) {
            try {
                String filePath = fileChooser.getSelectedFile().getPath();
                openFile(filePath);
                updateMenu(filePath);
                lastPath = filePath;
            } catch (Exception e) {
                JOptionPane.showMessageDialog(
                        this, e.toString(), ORIPA.res.getString("Error_FileLoadFailed"),
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void saveFile() {
        JFileChooser fileChooser = new JFileChooser(lastPath);
        fileChooser.addChoosableFileFilter(new FileFilterEx(new String[]{".opx"}, 
                "(*.opx) " + ORIPA.res.getString("ORIPA_File")));
        if (JFileChooser.APPROVE_OPTION == fileChooser.showSaveDialog(this)) {
            try {
                String filePath = fileChooser.getSelectedFile().getPath();
                if (!filePath.endsWith(".opx")) {
                    filePath += ".opx";
                }
                File file = new File(filePath);
                if (file.exists()) {
                    if (JOptionPane.showConfirmDialog(
                            null, ORIPA.res.getString("Warning_SameNameFileExist"), 
                            ORIPA.res.getString("DialogTitle_FileSave"),
                            JOptionPane.YES_NO_OPTION, 
                            JOptionPane.WARNING_MESSAGE) != JOptionPane.YES_OPTION) {
                        return;
                    }
                }
                saveFile(filePath);
                updateMenu(filePath);
                lastPath = filePath;
            } catch (Exception e) {
                JOptionPane.showMessageDialog(
                        this, e.toString(), ORIPA.res.getString("Error_FileSaveFailed"),
                        JOptionPane.ERROR_MESSAGE);
            }
        }

    }

    private void saveFile(String filePath) {
        ExporterXML exporter = new ExporterXML();
        DataSet data = new DataSet(ORIPA.doc);
        exporter.export(data, filePath);
        ORIPA.doc.dataFilePath = filePath;
    }

    private void openFile(String filePath) {
        ORIPA.modelFrame.setVisible(false);
        ORIPA.renderFrame.setVisible(false);
        ORIPA.mainFrame.uiPanel.dispGridCheckBox.setSelected(false);
        ORIPA.mainFrame.mainScreen.setDispGrid(false);
        if (filePath.endsWith(".dxf")) {
            LoaderDXF loader = new LoaderDXF();
            Doc doc = loader.load(filePath);
            if (doc != null) {
                ORIPA.doc = doc;
                ORIPA.doc.dataFilePath = filePath;
            }
            return;
        } else if (filePath.endsWith(".pdf")) {
            LoaderPDF loader = new LoaderPDF();
            Doc doc = loader.load(filePath);
            if (doc != null) {
                ORIPA.doc = doc;
                ORIPA.doc.dataFilePath = filePath;
            }
            return;
        } else if (filePath.endsWith(".cp")) {
            LoaderLines loader = new LoaderLines();
            Doc doc = loader.load(filePath);
            if (doc != null) {
                ORIPA.doc = doc;
                ORIPA.doc.dataFilePath = filePath;
            }
            return;
        }

        LoaderXML loader = new LoaderXML();
        DataSet data = loader.load(filePath);
        if (data == null) {
            return;
        }

        if (data.getMainVersion() > ORIPA.FILE_MAJOR_VERSION) {
            JOptionPane.showMessageDialog(
                    this, "This file is compatible with a new version. "
                    + "Please obtain the latest version of ORIPA", "Failed to load the file",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (data.getSubVersion() > ORIPA.FILE_MINOR_VERSION) {
            JOptionPane.showMessageDialog(
                    this, "This file is compatible with a new version. "
                    + "Please obtain the latest version of ORIPA", "Failed to load the file",
                    JOptionPane.ERROR_MESSAGE);
        }

        Doc doc = new Doc(ORIPA.doc.size);
        data.recover(doc);
        ORIPA.doc = doc;
        ORIPA.doc.dataFilePath = filePath;
    }

    private void saveIniFile() {
        String fileNames[] = new String[Config.MRUFILE_NUM];
        for (int i = 0; i < Config.MRUFILE_NUM; i++) {
            fileNames[i] = MRUFilesMenuItem[i].getText();
        }

        InitData initData = new InitData();

        initData.setMRUFiles(fileNames);
        initData.setLastUsedFile(lastPath);

        try {
            XMLEncoder enc = new XMLEncoder(
                    new BufferedOutputStream(
                    new FileOutputStream(System.getProperty("user.home") + "\\oripa.ini")));
            enc.writeObject(initData);
            enc.close();
        } catch (FileNotFoundException e) {
        }
    }

    private void loadIniFile() {
        InitData initData;
        try {
            XMLDecoder dec = new XMLDecoder(
                    new BufferedInputStream(
                    new FileInputStream(System.getProperty("user.home") + "\\oripa.ini")));
            initData = (InitData) dec.readObject();
            dec.close();

            for (int i = 0; i < Config.MRUFILE_NUM; i++) {
                if (initData.MRUFiles[i] != null && !initData.MRUFiles[i].equals("")) {
                    MRUFiles.add(0, initData.MRUFiles[i]);
                }
            }

            lastPath = initData.getLastUsedFile();

        } catch (Exception e) {
        }

    }

    @Override
    public void componentResized(ComponentEvent arg0) {
    }

    @Override
    public void componentMoved(ComponentEvent arg0) {
    }

    @Override
    public void componentShown(ComponentEvent arg0) {
    }

    @Override
    public void componentHidden(ComponentEvent arg0) {
    }

    @Override
    public void windowOpened(WindowEvent arg0) {
    }

    @Override
    public void windowClosing(WindowEvent arg0) {
        saveIniFile();
    }

    @Override
    public void windowClosed(WindowEvent arg0) {
    }

    @Override
    public void windowIconified(WindowEvent arg0) {
    }

    @Override
    public void windowDeiconified(WindowEvent arg0) {
    }

    @Override
    public void windowActivated(WindowEvent arg0) {
    }

    @Override
    public void windowDeactivated(WindowEvent arg0) {
    }
}
