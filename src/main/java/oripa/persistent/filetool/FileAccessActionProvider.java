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
package oripa.persistent.filetool;

import java.awt.Component;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * @author Koji
 *
 * @param <Data>
 */
public interface FileAccessActionProvider<Data> {

	/**
	 * Opens chooser dialog and return saver object for the chosen file.
	 *
	 * @param parent
	 *            parent GUI component
	 *
	 * @throws FileChooserCanceledException
	 *             when user canceled saving.
	 * @throws IllegalStateException
	 *             this object doesn't have a saving action for the chosen file.
	 * @return saver object.
	 */
	public abstract AbstractSavingAction<Data> getActionForSavingFile(
			Component parent)
			throws FileChooserCanceledException, IllegalStateException;

	/**
	 * Opens chooser dialog and returns loader object for the chosen file.
	 *
	 * @param parent
	 *            parent GUI component
	 *
	 * @return loader object.
	 * @throws FileChooserCanceledException
	 *             when user canceled loading.
	 * @throws IOException
	 *             selected file doesn't exist.
	 * @throws IllegalStateException
	 *             this object doesn't have a loading action for the chosen
	 *             file.
	 */
	public abstract AbstractLoadingAction<Data> getActionForLoadingFile(
			Component parent)
			throws FileChooserCanceledException, FileNotFoundException, IllegalStateException;
}