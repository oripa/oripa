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

import oripa.persistence.dao.FileType;

/**
 * @author OUCHI Koji
 *
 */
public class FoldedModelFileTypes {
    public static FileType<FoldedModelEntity> svg() {
        return new FileType<>(FoldedModelFileTypeKey.SVG_FOLDED_MODEL);
    }

    public static FileType<FoldedModelEntity> flippedSvg() {
        return new FileType<>(FoldedModelFileTypeKey.SVG_FOLDED_MODEL_FLIP);
    }

    public static FileType<FoldedModelEntity> picture() {
        return new FileType<>(FoldedModelFileTypeKey.PICTURE);
    }

    public static FileType<FoldedModelEntity> ormat() {
        return new FileType<>(FoldedModelFileTypeKey.ORMAT_FOLDED_MODEL);
    }

    public static FileType<FoldedModelEntity> singleFrameFold() {
        return new FileType<>(FoldedModelFileTypeKey.FOLD_SINGLE_OVERLAPS);
    }

    public static FileType<FoldedModelEntity> multiFrameFold() {
        return new FileType<>(FoldedModelFileTypeKey.FOLD_ALL_OVERLAPS);
    }

}
