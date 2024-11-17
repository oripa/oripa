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

import java.util.HashMap;
import java.util.Map;

import oripa.persistence.dao.FileSelectionSupport;
import oripa.persistence.dao.FileSelectionSupportFactory;
import oripa.persistence.dao.FileSelectionSupportSelector;
import oripa.persistence.dao.FileType;
import oripa.persistence.filetool.FileAccessSupport;
import oripa.persistence.filetool.FileAccessSupportFactory;
import oripa.persistence.filetool.FileTypeProperty;
import oripa.resource.StringID;
import oripa.util.file.FileFactory;

/**
 * @author OUCHI Koji
 *
 */
public class FoldedModelFileSelectionSupportSelectorFactory {
	private final FileSelectionSupportFactory selectionSupportFactory = new FileSelectionSupportFactory();
	private final FileAccessSupportFactory accessSupportFactory = new FileAccessSupportFactory();

	public FileSelectionSupportSelector<FoldedModelEntity> create(final boolean modelFlip,
			final FileFactory fileFactory) {
		var supports = new HashMap<FileType<FoldedModelEntity>, FileSelectionSupport<FoldedModelEntity>>();

		var key = FoldedModelFileTypeKey.ORMAT_FOLDED_MODEL;
		put(
				supports,
				key,
				accessSupportFactory.createFileAccessSupport(key, StringID.ModelUI.FILE_ID));

		key = FoldedModelFileTypeKey.FOLD_SINGLE_OVERLAPS;
		put(
				supports,
				key,
				accessSupportFactory.createFileAccessSupport(key, StringID.ModelUI.FILE_ID, " (single)"));

		key = FoldedModelFileTypeKey.FOLD_ALL_OVERLAPS;
		put(
				supports,
				key,
				accessSupportFactory.createFileAccessSupport(key, StringID.ModelUI.FILE_ID, " (all)"));

		if (modelFlip) {
			key = FoldedModelFileTypeKey.SVG_FOLDED_MODEL_FLIP;
			put(
					supports,
					key,
					accessSupportFactory.createFileAccessSupport(key, StringID.ModelUI.FILE_ID));
		} else {
			key = FoldedModelFileTypeKey.SVG_FOLDED_MODEL;
			put(
					supports,
					key,
					accessSupportFactory.createFileAccessSupport(key, StringID.ModelUI.FILE_ID));
		}

		key = FoldedModelFileTypeKey.PICTURE;
		put(
				supports,
				key,
				accessSupportFactory.createFileAccessSupport(key, StringID.ModelUI.FILE_ID));

		return new FileSelectionSupportSelector<FoldedModelEntity>(
				supports,
				selectionSupportFactory,
				accessSupportFactory,
				fileFactory);
	}

	private void put(final Map<FileType<FoldedModelEntity>, FileSelectionSupport<FoldedModelEntity>> supports,
			final FileTypeProperty<FoldedModelEntity> key, final FileAccessSupport<FoldedModelEntity> accessSupport) {
		supports.put(
				new FileType<>(key),
				selectionSupportFactory.create(accessSupport));
	}
}
