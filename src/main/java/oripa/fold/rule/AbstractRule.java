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

/**
 * @author Koji
 *
 */
public abstract class AbstractRule<Variable> implements Rule<Variable> {

	private class Denied implements Rule<Variable> {
		Rule<Variable> rule;
		
		/**
		 * Constructor
		 */
		public Denied(Rule<Variable> rule) {
			this.rule = rule;
		}

		/* (non Javadoc)
		 * @see oripa.fold.rule.Rule#asDenied()
		 */
		@Override
		public Rule<Variable> asDenied() {
			return rule;
		}
	
		/* (non Javadoc)
		 * @see oripa.fold.rule.Rule#holds(java.lang.Object)
		 */
		@Override
		public boolean holds(Variable var) {
			return rule.violates(var);
		}

		/* (non Javadoc)
		 * @see oripa.fold.rule.Rule#violates(java.lang.Object)
		 */
		@Override
		public boolean violates(Variable var) {
			return rule.holds(var);
		}
	}

	private Denied deniedRule = new Denied(this);

	@Override
	public boolean violates(Variable var) {
		return holds(var) == false;
	}


	/* (non Javadoc)
	 * @see oripa.fold.rule.Rule#asDenied()
	 */
	@Override
	public Rule<Variable> asDenied() {
		return deniedRule;
	}
}
