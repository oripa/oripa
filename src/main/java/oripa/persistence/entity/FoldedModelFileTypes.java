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

import oripa.persistence.filetool.FileTypeProperty;

/**
 * @author OUCHI Koji
 *
 */
public class FoldedModelFileTypes {
	public static FileTypeProperty<FoldedModelEntity> svg() {
		return FoldedModelFileTypeKey.SVG_FOLDED_MODEL;
	}

	public static FileTypeProperty<FoldedModelEntity> flippedSvg() {
		return FoldedModelFileTypeKey.SVG_FOLDED_MODEL_FLIP;
	}

	public static FileTypeProperty<FoldedModelEntity> picture() {
		return FoldedModelFileTypeKey.PICTURE;
	}

	public static FileTypeProperty<FoldedModelEntity> ormat() {
		return FoldedModelFileTypeKey.ORMAT_FOLDED_MODEL;
	}

	public static FileTypeProperty<FoldedModelEntity> singleFrameFold() {
		return FoldedModelFileTypeKey.FOLD_SINGLE_OVERLAPS;
	}

	public static FileTypeProperty<FoldedModelEntity> multiFrameFold() {
		return FoldedModelFileTypeKey.FOLD_ALL_OVERLAPS;
	}

}
