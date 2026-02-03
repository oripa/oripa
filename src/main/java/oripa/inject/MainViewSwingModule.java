/**
 * ORIPA - Origami Pattern Editor
 * Copyright (C) 2013-     ORIPA OSS Project  https://github.com/oripa/oripa
 * Copyright (C) 2005-2009 Jun Mitani         http://mitani.cs.tsukuba.ac.jp/

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
package oripa.inject;

import com.google.inject.AbstractModule;

import oripa.gui.view.estimation.EstimationResultFrameFactory;
import oripa.gui.view.file.FileChooserFactory;
import oripa.gui.view.foldability.FoldabilityCheckFrameFactory;
import oripa.gui.view.main.MainFrameDialogFactory;
import oripa.gui.view.main.MainFrameView;
import oripa.gui.view.main.SubFrameFactory;
import oripa.gui.view.model.ModelViewFrameFactory;
import oripa.swing.view.estimation.EstimationResultSwingFrameFactory;
import oripa.swing.view.file.FileChooserSwingFactory;
import oripa.swing.view.foldability.FoldabilityCheckSwingFrameFactory;
import oripa.swing.view.main.MainFrame;
import oripa.swing.view.main.MainFrameSwingDialogFactory;
import oripa.swing.view.main.SubSwingFrameFactory;
import oripa.swing.view.model.ModelViewSwingFrameFactory;

/**
 * @author OUCHI Koji
 *
 */
public class MainViewSwingModule extends AbstractModule {

    @Override
    protected void configure() {

        bind(MainFrameView.class).to(MainFrame.class);
        bind(MainFrameDialogFactory.class).to(MainFrameSwingDialogFactory.class);

        bind(FoldabilityCheckFrameFactory.class).to(FoldabilityCheckSwingFrameFactory.class);
        bind(ModelViewFrameFactory.class).to(ModelViewSwingFrameFactory.class);
        bind(EstimationResultFrameFactory.class).to(EstimationResultSwingFrameFactory.class);
        bind(SubFrameFactory.class).to(SubSwingFrameFactory.class);

        bind(FileChooserFactory.class).to(FileChooserSwingFactory.class);
    }

}
