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

import javax.swing.SwingUtilities;

import com.google.inject.Guice;

import oripa.cli.CommandLineInterfaceMain;
import oripa.gui.presenter.main.MainFramePresenter;
import oripa.gui.view.main.MainFrameView;
import oripa.inject.BindingModule;
import oripa.inject.CreasePatternPresenterModule;
import oripa.inject.FileAccessServiceModule;
import oripa.inject.FileHistoryModule;
import oripa.inject.MainViewOripaModule;
import oripa.inject.MainViewSwingModule;
import oripa.inject.PaintDomainModule;
import oripa.inject.PluginModule;

public class ORIPA {
	public static void main(final String[] args) {

		if (args.length > 0) {
			new CommandLineInterfaceMain().run(args);
			return;
		}

		SwingUtilities.invokeLater(() -> {
			// Construction of the main frame

			var injector = Guice.createInjector(
					new PluginModule(),
					new MainViewSwingModule(),
					new MainViewOripaModule(),
					new BindingModule(),
					new CreasePatternPresenterModule(),
					new FileAccessServiceModule(),
					new PaintDomainModule(),
					new FileHistoryModule());

			var mainFrame = injector.getInstance(MainFrameView.class);
			mainFrame.initializeFrameBounds();

			var presenter = injector.getInstance(
					MainFramePresenter.class);
			presenter.setViewVisible(true);

		});
	}

}
