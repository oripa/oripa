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
package oripa.persistent.doc.loader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

import oripa.domain.creasepattern.CreasePatternFactory;
import oripa.persistent.doc.Doc;
import oripa.persistent.doc.Loader;
import oripa.persistent.foldformat.CreasePatternElementConverter;
import oripa.persistent.foldformat.CreasePatternFOLDFormat;

/**
 * @author OUCHI Koji
 *
 */
public class LoaderFOLD implements Loader<Doc> {
	private static final Logger logger = LoggerFactory.getLogger(LoaderFOLD.class);

	/*
	 * (non Javadoc)
	 *
	 * @see oripa.persistent.doc.Loader#load(java.lang.String)
	 */
	@Override
	public Doc load(final String filePath) {

		var gson = new Gson();
		CreasePatternFOLDFormat foldFormat;
		try {
			foldFormat = gson.fromJson(
					Files.readString(Paths.get(filePath)),
					CreasePatternFOLDFormat.class);
		} catch (IOException e) {
			logger.error("failed to open .fold file", e);
			return new Doc();
		}

		var converter = new CreasePatternElementConverter();

		var lines = converter.fromEdges(
				foldFormat.getEdgesVertices(),
				foldFormat.getEdgesAssignment(),
				foldFormat.getVerticesCoords());

		var factory = new CreasePatternFactory();
		var cp = factory.createCreasePattern(lines);
		var doc = new Doc();
		doc.setCreasePattern(cp);

		var property = doc.getProperty();
		property.setEditorName(foldFormat.getFileAuthor());
		property.setTitle(foldFormat.getFrameTitle());

		return doc;
	}

}
