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
package oripa.application.estimation;

import oripa.application.FileAccessService;
import oripa.persistence.dao.FileDAO;
import oripa.persistence.entity.FoldedModelEntity;
import oripa.persistence.entity.FoldedModelFileAccessSupportSelectorFactory;
import oripa.util.file.FileFactory;

/**
 * @author OUCHI Koji
 *
 */
public class FoldedModelFileAccessServiceFactory {
	private final FoldedModelFileAccessSupportSelectorFactory selectorFactory;
	private final FileFactory fileFactory;

	public FoldedModelFileAccessServiceFactory(
			final FoldedModelFileAccessSupportSelectorFactory selectorFactory,
			final FileFactory fileFactory) {
		this.selectorFactory = selectorFactory;
		this.fileFactory = fileFactory;
	}

	public FileAccessService<FoldedModelEntity> create(final boolean isFaceOrderFlipped) {
		return new FileAccessService<FoldedModelEntity>(
				new FileDAO<>(
						selectorFactory.create(isFaceOrderFlipped, fileFactory),
						fileFactory));
	}
}
