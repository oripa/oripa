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

import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import oripa.application.main.IniFileAccess;
import oripa.file.FileHistory;
import oripa.file.InitData;
import oripa.gui.presenter.creasepattern.CreasePatternViewContext;
import oripa.gui.view.main.MainFrameView;
import oripa.gui.view.main.PainterScreenSetting;

/**
 * @author OUCHI Koji
 *
 */
@ExtendWith(MockitoExtension.class)
class IniFileAccessPresentationLogicTest {

	@InjectMocks
	IniFileAccessPresentationLogic presentationLogic;

	@Mock
	MainFrameView view;

	@Mock
	PainterScreenSetting screenSetting;

	@Mock
	CreasePatternViewContext creasePatternViewContext;

	@Mock
	IniFileAccess iniFileAccess;

	@Mock
	FileHistory fileHistory;

	@Nested
	class TestLoadIniFile {

		@MethodSource("createIniFileShouldBeLoadedArguments")
		@ParameterizedTest
		void iniFileShouldBeLoaded(
				final boolean isZeroLineWidth,
				final boolean isMvLineVisible,
				final boolean isAuxLineVisible,
				final boolean isVertexVisible) {

			InitData initData = mock();
			when(initData.isZeroLineWidth()).thenReturn(isZeroLineWidth);
			when(initData.isMvLineVisible()).thenReturn(isMvLineVisible);
			when(initData.isAuxLineVisible()).thenReturn(isAuxLineVisible);
			when(initData.isVertexVisible()).thenReturn(isVertexVisible);

			when(iniFileAccess.load()).thenReturn(initData);

			presentationLogic.loadIniFile();

			verify(iniFileAccess).load();

			verify(fileHistory).loadFromInitData(initData);

			verify(screenSetting).setZeroLineWidth(isZeroLineWidth);
			verify(screenSetting).setMVLineVisible(isMvLineVisible);
			verify(screenSetting).setAuxLineVisible(isAuxLineVisible);
			verify(screenSetting).setVertexVisible(isVertexVisible);
		}

		static List<Arguments> createIniFileShouldBeLoadedArguments() {
			var booleanValues = List.of(true, false);

			var args = new ArrayList<Arguments>();

			for (var zeroWidth : booleanValues) {
				for (var mvLine : booleanValues) {
					for (var auxLine : booleanValues) {
						for (var vertex : booleanValues) {
							args.add(Arguments.of(zeroWidth, mvLine, auxLine, vertex));
						}
					}
				}
			}
			return args;
		}
	}
}
