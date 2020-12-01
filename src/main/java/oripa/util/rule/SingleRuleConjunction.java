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
package oripa.util.rule;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Koji
 *
 */
public class SingleRuleConjunction<Variable> extends AbstractRule<Collection<Variable>> {

	private final Rule<Variable> rule;

	/**
	 *
	 * @param rule
	 */
	public SingleRuleConjunction(final Rule<Variable> rule) {
		this.rule = rule;
	}

	@Override
	public boolean holds(final Collection<Variable> inputs) {
		return inputs.stream().allMatch(input -> rule.holds(input));
	}

	public Set<Variable> findViolations(final Collection<Variable> inputs) {
		return inputs.stream()
				.filter(input -> rule.violates(input))
				.collect(Collectors.toSet());
	}
}
