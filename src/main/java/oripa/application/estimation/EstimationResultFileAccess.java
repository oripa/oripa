/**
 * ORIPA - Origami Pattern Editor Copyright (C) 2013- ORIPA OSS Project
 * https://github.com/oripa/oripa Copyright (C) 2005-2009 Jun Mitani
 * http://mitani.cs.tsukuba.ac.jp/
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package oripa.application.estimation;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Optional;

import oripa.application.FileAccessService;
import oripa.domain.fold.FoldedModel;
import oripa.persistence.dao.AbstractFileDAO;
import oripa.persistence.dao.DataAccessObject;
import oripa.persistence.entity.FoldedModelEntity;
import oripa.persistence.filetool.FileTypeProperty;
import oripa.persistence.filetool.FileVersionError;
import oripa.persistence.filetool.WrongDataFormatException;

/**
 * A service object between the {@link DataAccessObject} and the
 * {@link FoldedModel}.
 *
 * @author OUCHI Koji
 *
 */
public class EstimationResultFileAccess extends FileAccessService<FoldedModelEntity> {
	private final AbstractFileDAO<FoldedModelEntity> dao;

	public EstimationResultFileAccess(final AbstractFileDAO<FoldedModelEntity> dao) {
		this.dao = dao;
	}

	@Override
	protected AbstractFileDAO<FoldedModelEntity> getFileDAO() {
		return dao;
	}

	@Override
	public final void saveFile(final FoldedModelEntity document,
			final String path, final FileTypeProperty<FoldedModelEntity> type)
			throws IOException, IllegalArgumentException {

		if (type == null) {
			dao.save(document, path);
		} else {
			dao.save(document, path, type);
		}
	}

	@Override
	public Optional<FoldedModelEntity> loadFile(final String filePath)
			throws FileVersionError, IllegalArgumentException,
			WrongDataFormatException, IOException, FileNotFoundException {
		throw new RuntimeException("Not implemented yet.");
	}
}
