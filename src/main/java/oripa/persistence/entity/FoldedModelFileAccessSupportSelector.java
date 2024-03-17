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

import oripa.persistence.dao.AbstractFileAccessSupportSelector;
import oripa.persistence.filetool.FileAccessSupport;
import oripa.persistence.filetool.FileTypeProperty;
import oripa.resource.StringID;

/**
 * @author OUCHI Koji
 *
 */
public class FoldedModelFileAccessSupportSelector extends AbstractFileAccessSupportSelector<FoldedModelEntity> {
	private final SortedMap<FileTypeProperty<FoldedModelEntity>, FileAccessSupport<FoldedModelEntity>> supports = new TreeMap<>();

	/**
	 * Constructor
	 */
	public FoldedModelFileAccessSupportSelector(final boolean modelFlip) {
		FoldedModelFileTypeKey key = FoldedModelFileTypeKey.ORMAT_FOLDED_MODEL;
		putFileAccessSupport(key, createDescription(key, StringID.ModelUI.FILE_ID));

		key = FoldedModelFileTypeKey.FOLD_SINGLE_OVERLAPS;
		putFileAccessSupport(key, createDescription(key, StringID.ModelUI.FILE_ID) + " (single)");

		key = FoldedModelFileTypeKey.FOLD_ALL_OVERLAPS;
		putFileAccessSupport(key, createDescription(key, StringID.ModelUI.FILE_ID) + " (all)");

		if (modelFlip) {
			key = FoldedModelFileTypeKey.SVG_FOLDED_MODEL_FLIP;
			putFileAccessSupport(key, createDescription(key, StringID.ModelUI.FILE_ID));
		} else {
			key = FoldedModelFileTypeKey.SVG_FOLDED_MODEL;
			putFileAccessSupport(key, createDescription(key, StringID.ModelUI.FILE_ID));
		}

		key = FoldedModelFileTypeKey.PICTURE;
		putFileAccessSupport(key, createDescription(key, StringID.ModelUI.FILE_ID));
	}

	@Override
	protected SortedMap<FileTypeProperty<FoldedModelEntity>, FileAccessSupport<FoldedModelEntity>> getFileAccessSupports() {
		return supports;
	}
}
