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

import java.util.TreeMap;

import oripa.domain.fold.halfedge.OrigamiModel;
import oripa.persistence.dao.FileAccessSupportFactory;
import oripa.persistence.dao.FileAccessSupportSelector;
import oripa.persistence.filetool.FileAccessSupport;
import oripa.persistence.filetool.FileTypeProperty;
import oripa.resource.StringID;
import oripa.util.file.FileFactory;

/**
 * @author OUCHI Koji
 *
 */
public class OrigamiModelFileAccessSupportSelectorFactory {

	/**
	 * Constructor
	 */
	public FileAccessSupportSelector<OrigamiModel> create(final FileFactory fileFactory) {
		var supports = new TreeMap<FileTypeProperty<OrigamiModel>, FileAccessSupport<OrigamiModel>>();
		var supportFactory = new FileAccessSupportFactory<OrigamiModel>();

		OrigamiModelFileTypeKey key = OrigamiModelFileTypeKey.DXF_MODEL;
		supports.put(key, supportFactory.createFileAccessSupport(key, StringID.ModelUI.FILE_ID));

		key = OrigamiModelFileTypeKey.OBJ_MODEL;
		supports.put(key, supportFactory.createFileAccessSupport(key, StringID.ModelUI.FILE_ID));

		key = OrigamiModelFileTypeKey.SVG_MODEL;
		supports.put(key, supportFactory.createFileAccessSupport(key, StringID.ModelUI.FILE_ID));

		return new FileAccessSupportSelector<OrigamiModel>(supports, fileFactory);
	}

}
