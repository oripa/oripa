package oripa.persistent.doc;

import java.io.File;
import java.util.Collections;
import java.util.Optional;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import oripa.doc.Doc;
import oripa.persistent.filetool.FileAccessSupportFilter;
import oripa.persistent.filetool.MultiTypeAcceptableFileLoadingFilter;
import oripa.resource.ResourceHolder;
import oripa.resource.ResourceKey;
import oripa.resource.StringID;

/**
 * Manages available filters for file access.
 *
 * @author OUCHI Koji
 *
 */
public class DocFilterSelector {

	private final SortedMap<CreasePatternFileTypeKey, FileAccessSupportFilter<Doc>> filters = new TreeMap<>();

	private final ResourceHolder resourceHolder = ResourceHolder.getInstance();

	/**
	 *
	 * A constructor that puts default filters into this instance.
	 */
	public DocFilterSelector() {

		CreasePatternFileTypeKey key = CreasePatternFileTypeKey.OPX;
		putFilter(key, createDescription(key, StringID.Main.ORIPA_FILE_ID));

		key = CreasePatternFileTypeKey.PICT;
		putFilter(key, createDescription(key, StringID.Main.PICTURE_FILE_ID));

		key = CreasePatternFileTypeKey.FOLD;
		putFilter(key, createDescription(key, StringID.Main.FILE_ID));

		key = CreasePatternFileTypeKey.DXF;
		putFilter(key, createDescription(key, StringID.Main.FILE_ID));

		key = CreasePatternFileTypeKey.CP;
		putFilter(key, createDescription(key, StringID.Main.FILE_ID));

		key = CreasePatternFileTypeKey.SVG;
		putFilter(key, createDescription(key, StringID.Main.FILE_ID));

		key = CreasePatternFileTypeKey.PDF;
		putFilter(key, createDescription(key, StringID.Main.FILE_ID));

	}

	/**
	 *
	 * @param key
	 * @param desctiption
	 * @param exporter
	 * @param loader
	 */
	private void putFilter(final CreasePatternFileTypeKey key, final String desctiption) {
		FileAccessSupportFilter<Doc> filter;

		filter = new FileAccessSupportFilter<>(key, desctiption);

		this.putFilter(key, filter);
	}

	/**
	 *
	 * @param fileTypeKey
	 * @param resourceKey
	 * @return
	 */
	private String createDescription(final CreasePatternFileTypeKey fileTypeKey,
			final String resourceKey) {
		return FileAccessSupportFilter.createDefaultDescription(fileTypeKey,
				resourceHolder.getString(ResourceKey.LABEL, resourceKey));

	}

	/**
	 *
	 * @param key
	 *            A value that describes the file type you want.
	 * @return A filter for given key.
	 */
	public FileAccessSupportFilter<Doc> getFilter(final CreasePatternFileTypeKey key) {
		return filters.get(key);
	}

	/**
	 *
	 * @param key
	 *            A value that describes the file type you want.
	 * @param filter
	 *            A filter to be set.
	 * @return The previous filter for given key.
	 */

	public FileAccessSupportFilter<Doc> putFilter(final CreasePatternFileTypeKey key,
			final FileAccessSupportFilter<Doc> filter) {
		return filters.put(key, filter);
	}

	/**
	 *
	 * @return all filters in this instance.
	 */
	public FileAccessSupportFilter<Doc>[] toArray() {
		@SuppressWarnings("unchecked")
		FileAccessSupportFilter<Doc>[] array = new FileAccessSupportFilter[filters
				.size()];

		return filters.values().toArray(array);
	}

	/**
	 *
	 * @return filters that can load Doc from a file.
	 */
	public FileAccessSupportFilter<Doc>[] getLoadables() {
		var loadables = filters.values().stream()
				.filter(f -> f.getLoadingAction() != null)
				.collect(Collectors.toList());

		var multi = new MultiTypeAcceptableFileLoadingFilter<Doc>(filters.values(), "Any type");
		loadables.add(multi);

		Collections.sort(loadables);

		@SuppressWarnings("unchecked")
		FileAccessSupportFilter<Doc>[] array = new FileAccessSupportFilter[loadables
				.size()];

		return loadables.toArray(array);
	}

	/**
	 * returns null if any filter cannot load the file.
	 *
	 * @param path
	 * @return a filter that can load the file at the path.
	 */
	public Optional<FileAccessSupportFilter<Doc>> getLoadableFilterOf(final String path) {
		if (path == null) {
			return Optional.empty();
		}

		File file = new File(path);
		if (file.isDirectory()) {
			return Optional.empty();
		}

		return Stream.of(toArray())
				.filter(f -> f.accept(file))
				.findFirst();
	}

	/**
	 *
	 * @return filters that can save a Doc object.
	 */
	@SuppressWarnings("unchecked")
	public FileAccessSupportFilter<Doc>[] getSavables() {
		var savables = filters.values().stream()
				.filter(f -> f.getSavingAction() != null)
				.collect(Collectors.toList());

		Collections.sort(savables);

		return (FileAccessSupportFilter<Doc>[]) savables
				.toArray(new FileAccessSupportFilter<?>[savables.size()]);
	}

}
