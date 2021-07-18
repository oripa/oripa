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
package oripa.application.model;

import java.awt.Component;
import java.io.IOException;

import oripa.domain.fold.halfedge.OrigamiModel;
import oripa.persistence.dao.DataAccessObject;
import oripa.persistence.filetool.FileAccessSupportFilter;
import oripa.persistence.filetool.FileChooserCanceledException;

/**
 * A service object between the {@link DataAccessObject} and the
 * {@link OrigamiModel}.
 *
 * @author OUCHI Koji
 *
 */
public class OrigamiModelFileAccess {
	private final DataAccessObject<OrigamiModel> dao;

	/**
	 * Constructor
	 */
	public OrigamiModelFileAccess(final DataAccessObject<OrigamiModel> dao) {
		this.dao = dao;
	}

	public void saveFile(final OrigamiModel origamiModel, final Component owner,
			final FileAccessSupportFilter<OrigamiModel>... filters)
			throws IllegalArgumentException, IOException, FileChooserCanceledException {

		dao.saveUsingGUI(origamiModel, null, owner, filters);
	}

}
