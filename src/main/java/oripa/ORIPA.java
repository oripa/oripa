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

import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import oripa.application.main.DataFileAccess;
import oripa.application.main.IniFileAccess;
import oripa.doc.Doc;
import oripa.domain.paint.PaintContextFactory;
import oripa.domain.paint.PaintDomainContext;
import oripa.domain.paint.copypaste.SelectionOriginHolderImpl;
import oripa.file.FileHistory;
import oripa.file.InitDataFileReader;
import oripa.file.InitDataFileWriter;
import oripa.gui.bind.state.EditModeStateManager;
import oripa.gui.presenter.creasepattern.CreasePatternPresentationContext;
import oripa.gui.presenter.creasepattern.CreasePatternViewContextFactory;
import oripa.gui.presenter.creasepattern.MouseActionHolder;
import oripa.gui.presenter.creasepattern.TypeForChangeContext;
import oripa.gui.presenter.main.MainFramePresenter;
import oripa.gui.presenter.main.SwitcherBetweenPasteAndChangeOrigin;
import oripa.gui.view.main.ArrayCopyDialogFactory;
import oripa.gui.view.main.CircleCopyDialogFactory;
import oripa.gui.view.main.MainFrame;
import oripa.gui.view.main.MainFrameSwingDialogFactory;
import oripa.gui.view.main.PropertyDialogFactory;
import oripa.gui.viewsetting.ViewUpdateSupport;
import oripa.gui.viewsetting.main.KeyProcessingImpl;
import oripa.gui.viewsetting.main.MainScreenUpdater;
import oripa.persistence.doc.DocDAO;
import oripa.persistence.doc.DocFilterSelector;
import oripa.resource.Constants;

public class ORIPA {
	public static void main(final String[] args) {
		SwingUtilities.invokeLater(() -> {
			int uiPanelWidth = 0;// 150;

			int mainFrameWidth = 1000;
			int mainFrameHeight = 800;

			int appTotalWidth = mainFrameWidth + uiPanelWidth;
			int appTotalHeight = mainFrameHeight;

			// Construction of the main frame
			var screenUpdater = new MainScreenUpdater();
			var mouseActionHolder = new MouseActionHolder();
			var keyProcessing = new KeyProcessingImpl(
					new SwitcherBetweenPasteAndChangeOrigin(mouseActionHolder),
					screenUpdater);
			var viewUpdateSupport = new ViewUpdateSupport(screenUpdater, keyProcessing);
			var mainFrame = new MainFrame(viewUpdateSupport);

			Toolkit toolkit = mainFrame.getToolkit();
			Dimension dim = toolkit.getScreenSize();
			int originX = (int) (dim.getWidth() / 2 - appTotalWidth / 2);
			int originY = (int) (dim.getHeight() / 2 - appTotalHeight / 2);

			mainFrame.setBounds(originX + uiPanelWidth, originY, mainFrameWidth, mainFrameHeight);
			mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

			var paintContext = new PaintContextFactory().createContext();
			var viewContext = new CreasePatternViewContextFactory().create(paintContext);

			var dialogFactory = new MainFrameSwingDialogFactory(
					new ArrayCopyDialogFactory(),
					new CircleCopyDialogFactory(),
					new PropertyDialogFactory());

			var presenter = new MainFramePresenter(
					mainFrame,
					viewUpdateSupport,
					dialogFactory,
					new Doc(),
					new PaintDomainContext(paintContext, new SelectionOriginHolderImpl()),
					new CreasePatternPresentationContext(viewContext, mouseActionHolder,
							new TypeForChangeContext()),
					new EditModeStateManager(),
					new FileHistory(Constants.MRUFILE_NUM),
					new IniFileAccess(
							new InitDataFileReader(), new InitDataFileWriter()),
					new DataFileAccess(new DocDAO(new DocFilterSelector())));
			presenter.setViewVisible(true);

//			if (Config.FOR_STUDY) {
//				int modelFrameWidth = 400;
//				int modelFrameHeight = 400;
//				modelFrame3D = new ModelViewFrame3D();
//				modelFrame3D.setBounds(0, 0,
//						modelFrameWidth * 2, modelFrameHeight * 2);
//				modelFrame3D.setVisible(true);
//			}
		});
	}

}
