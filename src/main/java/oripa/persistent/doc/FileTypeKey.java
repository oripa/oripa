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
package oripa.persistent.doc;

import java.util.Arrays;

import oripa.persistent.filetool.FileTypeProperty;

public enum FileTypeKey implements FileTypeProperty {
	OPX("opx", 1, ".opx", ".xml"),
	FOLD("fold", 2,	".fold"),
	PICT("pict", 3,	".png", ".jpg"),
	DXF("dxf", 4, ".dxf"),
	CP("cp", 5,	".cp"),
	SVG("svg", 6, ".svg"),
	PDF("pdf", 7, ".pdf"),

	OBJ_MODEL("obj", 8,	".obj"),
	DXF_MODEL("dxf", 9,	".dxf"),

	SVG_FOLDED_MODEL("svg_folded_model", 10, ".svg"),
	ORMAT_FOLDED_MODEL("ormat", 11,	".ormat");

	private final String keyText;
	private final Integer order;
	private final String[] extensions;

	/**
	 *
	 * Constructor
	 *
	 * @param key
	 *            key string
	 * @param order
	 *            defines the order of members.
	 * @param extensions
	 *            which should be managed as that file type.
	 */
	private FileTypeKey(final String key, final Integer order, final String... extensions) {
		this.keyText = key;
		this.order = order;
		this.extensions = extensions;
	}

	@Override
	public String getKeyText() {
		return keyText;
	}

	/*
	 * (non Javadoc)
	 *
	 * @see oripa.persistent.doc.FileTypeProperty#getExtensions()
	 */
	@Override
	public String[] getExtensions() {
		return extensions;
	}

	/*
	 * (non Javadoc)
	 *
	 * @see
	 * oripa.persistent.filetool.FileTypeProperty#extensionsMatch(java.lang.
	 * String)
	 */
	@Override
	public boolean extensionsMatch(final String filePath) {
		if (filePath == null) {
			return false;
		}
		return Arrays.asList(extensions).stream()
				.anyMatch(extention -> filePath.endsWith(extention));
	}

	/*
	 * (non Javadoc)
	 *
	 * @see oripa.persistent.filetool.FileTypeProperty#getOrder()
	 */
	@Override
	public Integer getOrder() {
		return order;
	}
}