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
package oripa.inject;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;

import jakarta.inject.Singleton;
import oripa.domain.cutmodel.CutModelOutlinesHolder;
import oripa.domain.cutmodel.DefaultCutModelOutlinesHolder;
import oripa.domain.paint.PaintContext;
import oripa.domain.paint.PaintContextFactory;
import oripa.domain.paint.byvalue.ByValueContext;
import oripa.domain.paint.byvalue.ByValueContextImpl;
import oripa.domain.paint.copypaste.SelectionOriginHolder;
import oripa.domain.paint.copypaste.SelectionOriginHolderImpl;

/**
 * @author OUCHI Koji
 *
 */
public class PaintDomainModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(SelectionOriginHolder.class).to(SelectionOriginHolderImpl.class);
        bind(ByValueContext.class).to(ByValueContextImpl.class);

        bind(CutModelOutlinesHolder.class).to(DefaultCutModelOutlinesHolder.class);
    }

    @Provides
    @Singleton
    PaintContext getPaintContext() {
        return new PaintContextFactory().createContext();
    }
}
