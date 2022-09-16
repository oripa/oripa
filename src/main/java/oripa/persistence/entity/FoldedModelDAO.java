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
package oripa.persistence.entity;

import oripa.domain.fold.halfedge.OrigamiModel;
import oripa.persistence.dao.AbstractFileDAO;
import oripa.persistence.dao.AbstractFileAccessSupportSelector;
import oripa.persistence.entity.exporter.FoldedModelEntity;
import oripa.persistence.filetool.FileTypeProperty;

/**
 * @author OUCHI Koji
 *
 */
public class FoldedModelDAO extends AbstractFileDAO<FoldedModelEntity> {
	private final AbstractFileAccessSupportSelector<FoldedModelEntity> selector;

	/**
	 * Constructor
	 *
	 * @param selector
	 *            If you don't use {@link #load(String)} and
	 *            {@link AbstractFileDAO#save(OrigamiModel, String, FileTypeProperty)},
	 *            {@code selector} can be null.
	 */
	public FoldedModelDAO(final AbstractFileAccessSupportSelector<FoldedModelEntity> selector) {
		this.selector = selector;
	}

	/* (non Javadoc)
	 * @see oripa.persistent.dao.AbstractDAO#getFilterSelector()
	 */
	@Override
	protected AbstractFileAccessSupportSelector<FoldedModelEntity> getFileAccessSupportSelector() {
		return selector;
	}

}
