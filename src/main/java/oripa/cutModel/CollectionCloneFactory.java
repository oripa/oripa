package oripa.cutModel;

import java.util.Collection;

public class CollectionCloneFactory<Element> {

	/**
	 * creates clone of given collection as specified class
	 * @param list
	 * @param returnedType
	 * @return
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */
	public Collection<Element> createCloneOf(
			Collection<Element> list, Class<? extends Collection> returnedType)
					throws InstantiationException, IllegalAccessException {

		Collection<Element> cloned = returnedType.newInstance();
		
		cloned.addAll(list);
		
		return cloned;
	}
}
