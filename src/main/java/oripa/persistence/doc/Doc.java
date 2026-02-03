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

import oripa.domain.creasepattern.CreasePattern;
import oripa.domain.projectprop.Property;

/**
 * @author OUCHI Koji
 *
 */
public class Doc {
    /**
     * Crease Pattern
     */
    private final CreasePattern creasePattern;

    /**
     * Project property
     */
    private final Property property;

    public static Doc forSaving(final CreasePattern creasePattern, final Property property) {
        return new Doc(creasePattern, property);
    }

    public static Doc forLoading(final CreasePattern creasePattern) {
        return new Doc(creasePattern, new Property());
    }

    public static Doc forLoading(final CreasePattern creasePattern, final Property property) {
        return new Doc(creasePattern, property);
    }

    private Doc(final CreasePattern creasePattern, final Property property) {
        this.creasePattern = creasePattern;
        this.property = property;
    }

    public CreasePattern getCreasePattern() {
        return creasePattern;
    }

    public Property getProperty() {
        return property;
    }

}
