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
package oripa.gui.presenter.main.logic;

import java.lang.invoke.MethodHandles;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.inject.Inject;
import oripa.application.main.IniFileAccess;
import oripa.file.FileHistory;
import oripa.gui.presenter.creasepattern.CreasePatternViewContext;
import oripa.gui.view.main.MainFrameView;
import oripa.gui.view.main.PainterScreenSetting;

/**
 * @author OUCHI Koji
 *
 */
public class IniFileAccessPresentationLogic {
    private static Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final MainFrameView view;
    private final PainterScreenSetting screenSetting;

    private final CreasePatternViewContext creasePatternViewContext;

    private final IniFileAccess iniFileAccess;

    private final FileHistory fileHistory;

    @Inject
    public IniFileAccessPresentationLogic(
            final MainFrameView view,
            final PainterScreenSetting screenSetting,
            final CreasePatternViewContext creasePatternViewContext,
            final IniFileAccess iniFileAccess,
            final FileHistory fileHistory

    ) {
        this.view = view;
        this.screenSetting = screenSetting;
        this.creasePatternViewContext = creasePatternViewContext;
        this.iniFileAccess = iniFileAccess;
        this.fileHistory = fileHistory;
    }

    public void saveIniFile() {
        try {
            iniFileAccess.save(fileHistory, creasePatternViewContext);
        } catch (IllegalStateException e) {
            logger.error("error when building ini file data", e);
            view.showSaveIniFileFailureErrorMessage(e);
        }
    }

    public void loadIniFile() {
        var ini = iniFileAccess.load();

        fileHistory.loadFromInitData(ini);
        screenSetting.setZeroLineWidth(ini.isZeroLineWidth());

        logger.debug("loaded ini.mvLineVisible: " + ini.isMvLineVisible());
        screenSetting.setMVLineVisible(ini.isMvLineVisible());

        logger.debug("loaded ini.auxLineVisible: " + ini.isAuxLineVisible());
        screenSetting.setAuxLineVisible(ini.isAuxLineVisible());

        logger.debug("loaded ini.vertexVisible: " + ini.isVertexVisible());
        screenSetting.setVertexVisible(ini.isVertexVisible());
    }

}
