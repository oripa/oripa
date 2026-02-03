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
package oripa.persistence.doc;

import oripa.persistence.dao.FileType;

/**
 * @author OUCHI Koji
 *
 */
public class DocFileTypes {
    public static FileType<Doc> opx() {
        return new FileType<>(CreasePatternFileTypeKey.OPX);
    }

    public static FileType<Doc> fold() {
        return new FileType<>(CreasePatternFileTypeKey.FOLD);
    }

    public static FileType<Doc> pictutre() {
        return new FileType<>(CreasePatternFileTypeKey.PICT);
    }

    public static FileType<Doc> dxf() {
        return new FileType<>(CreasePatternFileTypeKey.DXF);
    }

    public static FileType<Doc> cp() {
        return new FileType<>(CreasePatternFileTypeKey.CP);
    }

    public static FileType<Doc> svg() {
        return new FileType<>(CreasePatternFileTypeKey.SVG);
    }

    public static FileType<Doc> pdf() {
        return new FileType<>(CreasePatternFileTypeKey.PDF);
    }

}
