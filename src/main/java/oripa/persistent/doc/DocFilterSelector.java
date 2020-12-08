package oripa.persistent.doc;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.SortedMap;
import java.util.TreeMap;

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

//		key = FileTypeKey.OBJ_MODEL;
//		putFilter(key, createDescription(key, StringID.Main.FILE_ID),
//				ExporterOBJFactory.createFoldedModelExporter(),
//				// new ModelExporterOBJ(),
//				null);

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

		int i = 0;
		for (CreasePatternFileTypeKey key : filters.keySet()) {
			array[i] = filters.get(key);
			i++;
		}

		return array;
	}

	/**
	 *
	 * @return filters that can load Doc from a file.
	 */
	public FileAccessSupportFilter<Doc>[] getLoadables() {
		ArrayList<FileAccessSupportFilter<Doc>> loadables = new ArrayList<>();

		for (CreasePatternFileTypeKey key : filters.keySet()) {
			FileAccessSupportFilter<Doc> filter = filters.get(key);
			if (filter.getLoadingAction() != null) {
				loadables.add(filter);
			}
		}

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
	public FileAccessSupportFilter<Doc> getLoadableFilterOf(final String path) {
		if (path == null) {
			return null;
		}

		File file = new File(path);
		if (file.isDirectory()) {
			return null;
		}

		for (FileAccessSupportFilter<Doc> filter : this.toArray()) {
			if (filter.accept(file)) {
				return filter;
			}
		}

		return null;
	}

	/**
	 *
	 * @return filters that can save a Doc object.
	 */
	@SuppressWarnings("unchecked")
	public FileAccessSupportFilter<Doc>[] getSavables() {
		ArrayList<FileAccessSupportFilter<Doc>> savables = new ArrayList<>();

		for (CreasePatternFileTypeKey key : filters.keySet()) {
			FileAccessSupportFilter<Doc> filter = filters.get(key);
			if (filter.getSavingAction() != null) {
				savables.add(filter);
			}
		}

		Collections.sort(savables);

		return (FileAccessSupportFilter<Doc>[]) savables
				.toArray(new FileAccessSupportFilter<?>[savables.size()]);
	}

}
