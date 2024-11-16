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
import oripa.persistence.entity.exporter.OrigamiModelExporterDXF;
import oripa.persistence.entity.exporter.OrigamiModelExporterOBJ;
import oripa.persistence.entity.exporter.OrigamiModelExporterSVG;
import oripa.persistence.filetool.Exporter;
import oripa.persistence.filetool.FileTypePropertyWithAccessor;
import oripa.persistence.filetool.Loader;

/**
 * @author OUCHI Koji
 *
 */
public enum OrigamiModelFileTypeKey implements FileTypePropertyWithAccessor<OrigamiModel> {
	OBJ_MODEL("obj", 1, null, new OrigamiModelExporterOBJ(), "obj"),
	DXF_MODEL("dxf", 2, null, new OrigamiModelExporterDXF(), "dxf"),
	SVG_MODEL("svg", 3, null, new OrigamiModelExporterSVG(), "svg");

	private final String keyText;
	private final Integer order;
	private final String[] extensions;
	private final Loader<OrigamiModel> loader;
	private final Exporter<OrigamiModel> exporter;

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
	OrigamiModelFileTypeKey(final String key, final Integer order,
			final Loader<OrigamiModel> loader,
			final Exporter<OrigamiModel> exporter,
			final String... extensions) {
		this.keyText = key;
		this.order = order;
		this.loader = loader;
		this.exporter = exporter;
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
	 * @see oripa.persistent.filetool.FileTypeProperty#getOrder()
	 */
	@Override
	public Integer getOrder() {
		return order;
	}

	@Override
	public Loader<OrigamiModel> getLoader() {
		return loader;
	}

	@Override
	public Exporter<OrigamiModel> getExporter() {
		return exporter;
	}
}
