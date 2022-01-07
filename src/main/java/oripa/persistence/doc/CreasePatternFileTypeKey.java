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

import oripa.doc.Doc;
import oripa.persistence.doc.exporter.ExporterCP;
import oripa.persistence.doc.exporter.ExporterDXFFactory;
import oripa.persistence.doc.exporter.ExporterFOLD;
import oripa.persistence.doc.exporter.ExporterSVGFactory;
import oripa.persistence.doc.exporter.ExporterXML;
import oripa.persistence.doc.exporter.PictureExporter;
import oripa.persistence.doc.loader.LoaderCP;
import oripa.persistence.doc.loader.LoaderDXF;
import oripa.persistence.doc.loader.LoaderFOLD;
import oripa.persistence.doc.loader.LoaderPDF;
import oripa.persistence.doc.loader.LoaderXML;
import oripa.persistence.filetool.Exporter;
import oripa.persistence.filetool.FileTypeProperty;
import oripa.persistence.filetool.Loader;

/**
 * File type for crease pattern input/output.
 *
 * @author OUCHI Koji
 *
 */
public enum CreasePatternFileTypeKey implements FileTypeProperty<Doc> {
	OPX("opx", 1, new LoaderXML(), new ExporterXML(), ".opx", ".xml"),
	FOLD("fold", 2, new LoaderFOLD(), new ExporterFOLD(), ".fold"),
	PICT("pict", 3, null, new PictureExporter(), ".png", ".jpg"),
	DXF("dxf", 4, new LoaderDXF(), ExporterDXFFactory.createCreasePatternExporter(), ".dxf"),
	CP("cp", 5, new LoaderCP(), new ExporterCP(), ".cp"),
	SVG("svg", 6, null, ExporterSVGFactory.createCreasePatternExporter(), ".svg"),
	PDF("pdf", 7, new LoaderPDF(), null, ".pdf");

	private final String keyText;
	private final Integer order;
	private final String[] extensions;
	private final Loader<Doc> loader;
	private final Exporter<Doc> exporter;

	/**
	 *
	 * Constructor
	 *
	 * @param key
	 *            key string
	 * @param order
	 *            defines the order of members.
	 * @param loader
	 * @param exporter
	 * @param extensions
	 *            which should be managed as that file type.
	 */
	private CreasePatternFileTypeKey(final String key, final Integer order,
			final Loader<Doc> loader,
			final Exporter<Doc> exporter,
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

	@Override
	public String[] getExtensions() {
		return extensions;
	}

	@Override
	public Integer getOrder() {
		return order;
	}

	@Override
	public Loader<Doc> getLoader() {
		return loader;
	}

	@Override
	public Exporter<Doc> getExporter() {
		return exporter;
	}
}