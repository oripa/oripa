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
package oripa.gui.viewsetting.main;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.util.function.Consumer;

import jakarta.inject.Inject;
import jakarta.inject.Qualifier;
import jakarta.inject.Singleton;
import oripa.gui.view.ViewScreenUpdater;
import oripa.gui.view.main.KeyProcessing;

@Singleton
public class KeyProcessingImpl implements KeyProcessing {

	private final Consumer<Boolean> changeActionIfCopyAndPaste;
	private final ViewScreenUpdater screenUpdater;

	@Qualifier
	@Target({ FIELD, PARAMETER, METHOD })
	@Retention(RUNTIME)
	public @interface KeyOnOffListener {
	}

	@Inject
	public KeyProcessingImpl(@KeyOnOffListener final Consumer<Boolean> keyOnOffListener,
			final ViewScreenUpdater screenUpdater) {
		this.changeActionIfCopyAndPaste = keyOnOffListener;
		this.screenUpdater = screenUpdater;
	}

	@Override
	public void controlKeyPressed() {
		changeActionIfCopyAndPaste.accept(true);
		screenUpdater.updateScreen();
	}

	@Override
	public void escapeKeyPressed() {
		screenUpdater.updateScreen();
	}

	@Override
	public void keyReleased() {
		changeActionIfCopyAndPaste.accept(false);
		screenUpdater.updateScreen();
	}
}