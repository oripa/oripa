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
import com.google.inject.Provides;

import jakarta.inject.Singleton;
import oripa.appstate.StateManager;
import oripa.appstate.StatePopper;
import oripa.appstate.StatePopperFactory;
import oripa.domain.paint.linetype.TypeForChangeGettable;
import oripa.gui.bind.state.EditModeStateManager;
import oripa.gui.presenter.creasepattern.EditMode;
import oripa.gui.presenter.creasepattern.ScreenUpdater;
import oripa.gui.presenter.creasepattern.TypeForChangeContext;
import oripa.gui.view.ViewScreenUpdater;

/**
 * @author OUCHI Koji
 *
 */
public class BindingModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(TypeForChangeGettable.class).to(TypeForChangeContext.class);
		bind(TypeForChangeContext.class);
	}

	@Provides
	@Singleton
	StateManager<EditMode> getStateManager() {
		return new EditModeStateManager();
	}

	@Provides
	StatePopperFactory<EditMode> getStatePopperFactory(final StateManager<EditMode> stateManager) {
		return new StatePopperFactory<EditMode>(stateManager);
	}

	@Provides
	StatePopper<EditMode> createStatePopper(final StatePopperFactory<EditMode> factory) {
		return factory.createForState();
	}

	@Provides
	ScreenUpdater getScreenUpdater(final ViewScreenUpdater viewScreenUpdater) {
		return viewScreenUpdater::updateScreen;
	}
}
