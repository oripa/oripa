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

import java.util.SortedMap;
import java.util.TreeMap;

import oripa.domain.fold.halfedge.OrigamiModel;
import oripa.persistence.dao.AbstractFileAccessSupportSelector;
import oripa.persistence.filetool.FileAccessSupport;
import oripa.persistence.filetool.FileTypeProperty;
import oripa.resource.StringID;

/**
 * @author OUCHI Koji
 *
 */
public class OrigamiModelFileAccessSupportSelector extends AbstractFileAccessSupportSelector<OrigamiModel> {
	private final SortedMap<FileTypeProperty<OrigamiModel>, FileAccessSupport<OrigamiModel>> supports;

	/**
	 * Constructor
	 */
	public OrigamiModelFileAccessSupportSelector() {
		supports = new TreeMap<>();

		OrigamiModelFileTypeKey key = OrigamiModelFileTypeKey.DXF_MODEL;
		putFileAccessSupport(key, createDescription(key, StringID.ModelUI.FILE_ID));

		key = OrigamiModelFileTypeKey.OBJ_MODEL;
		putFileAccessSupport(key, createDescription(key, StringID.ModelUI.FILE_ID));

		key = OrigamiModelFileTypeKey.SVG_MODEL;
		putFileAccessSupport(key, createDescription(key, StringID.ModelUI.FILE_ID));

	}

	/* (non Javadoc)
	 * @see oripa.persistent.dao.AbstractFilterSelector#getFilters()
	 */
	@Override
	protected SortedMap<FileTypeProperty<OrigamiModel>, FileAccessSupport<OrigamiModel>> getFileAccessSupports() {
		return supports;
	}

}
