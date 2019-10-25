package oripa.util.collection;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;

public class CollectionCloneFactory<Element> {

	/**
	 * creates clone of given collection as specified class
	 *
	 * @param list
	 * @param returnedType
	 * @return
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 * @throws NoSuchMethodException
	 */
	public Collection<Element> createCloneOf(
			final Collection<Element> list, final Class<? extends Collection> returnedType)
			throws InstantiationException, IllegalAccessException, IllegalArgumentException,
			InvocationTargetException, NoSuchMethodException {

		@SuppressWarnings("unchecked")
		Collection<Element> cloned = returnedType.getConstructor().newInstance();

		cloned.addAll(list);

		return cloned;
	}
}
