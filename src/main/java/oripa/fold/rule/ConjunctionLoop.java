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
package oripa.fold.rule;

import java.util.Collection;
import java.util.HashSet;

/**
 * @author Koji
 *
 */
public class ConjunctionLoop<Input> implements Rule<Collection<Input>> {

	private Rule<Input> term;
	private HashSet<Input> violations = new HashSet<>();
	private final boolean shouldIterateAll;

	public ConjunctionLoop(Rule<Input> term, boolean shouldIterateAll){
		this.term = term;
		this.shouldIterateAll = shouldIterateAll;
	}

	/**
	 * 
	 * Constructor which sets shouldIterateAll true.
	 * @param term
	 */
	public ConjunctionLoop(Rule<Input> term){
		this.term = term;
		this.shouldIterateAll = true;
	}

	public boolean holds(Collection<Input> inputs) {

		violations.clear();
		
		boolean result = true;

		// a little messed but faster
		if (! shouldIterateAll) {
			for (Input input : inputs) {
				if (!term.holds(input)) {
					violations.add(input);
					return false;
				}
			}
		}
		else {
			for (Input input : inputs) {
				if (!term.holds(input)) {
					violations.add(input);
					result = false;
				}
			}
		}

		return result;
	}

	/**
	 * @return violations
	 */
	public HashSet<Input> getViolations() {
		return violations;
	}



}
