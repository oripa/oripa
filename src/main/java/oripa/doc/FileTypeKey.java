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
package oripa.doc;

import oripa.file.FileTypeProperty;

public enum FileTypeKey implements FileTypeProperty {
	OPX("opx",
			new String[] { ".opx", ".xml" }),
	PICT("pict",
			new String[] { ".png", ".jpg" }),
	DXF("dxf",
			new String[] { ".dxf" }),
	CP("cp",
			new String[] { ".cp" }),
	SVG("svg",
			new String[] { ".svg" }),
	PDF("pdf",
			new String[] { ".pdf" }),

	OBJ_MODEL("obj",
			new String[] { ".obj" }),
	DXF_MODEL("dxf",
			new String[] { ".dxf" }),

	SVG_FOLDED_MODEL("svg_folded_model",
			new String[] { ".svg" }),
	ORMAT_FOLDED_MODEL("ormat",
			new String[] { ".ormat" });

	private final String keyText;
	private final String[] extensions;

	private FileTypeKey(String key, String[] extensions) {
		this.keyText = key;
		this.extensions = extensions;
	}

	@Override
	public String getKeyText() {
		return keyText;
	}

	/*
	 * (non Javadoc)
	 * 
	 * @see oripa.doc.FileTypeProperty#getExtensions()
	 */
	@Override
	public String[] getExtensions() {
		return extensions;
	}
}