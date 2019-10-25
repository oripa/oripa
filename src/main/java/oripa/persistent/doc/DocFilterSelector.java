package oripa.persistent.doc;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import oripa.persistent.doc.exporter.ExporterCP;
import oripa.persistent.doc.exporter.ExporterDXFFactory;
import oripa.persistent.doc.exporter.ExporterOBJFactory;
import oripa.persistent.doc.exporter.ExporterSVGFactory;
import oripa.persistent.doc.exporter.ExporterXML;
import oripa.persistent.doc.exporter.PictureExporter;
import oripa.persistent.doc.loader.LoaderCP;
import oripa.persistent.doc.loader.LoaderDXF;
import oripa.persistent.doc.loader.LoaderPDF;
import oripa.persistent.doc.loader.LoaderXML;
import oripa.persistent.filetool.FileAccessSupportFilter;
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

	private final HashMap<FileTypeKey, FileAccessSupportFilter<Doc>> filters = new HashMap<>();

	private final ResourceHolder resourceHolder = ResourceHolder.getInstance();

	/**
	 *
	 * A constructor that puts default filters into this instance.
	 */
	@SuppressWarnings("unchecked")
	public DocFilterSelector() {

		FileTypeKey key = FileTypeKey.OPX;
		putFilter(key, createDescription(key, StringID.Main.ORIPA_FILE_ID),
				new ExporterXML(), new LoaderXML());

		key = FileTypeKey.PICT;
		putFilter(key, createDescription(key, StringID.Main.PICTURE_FILE_ID),
				new PictureExporter(), null);

		key = FileTypeKey.DXF;
		putFilter(key, createDescription(key, StringID.Main.FILE_ID),
				ExporterDXFFactory.createCreasePatternExporter(),
				new LoaderDXF());

		key = FileTypeKey.OBJ_MODEL;
		putFilter(key, createDescription(key, StringID.Main.FILE_ID),
				ExporterOBJFactory.createFoldedModelExporter(),
				// new ModelExporterOBJ(),
				null);

		key = FileTypeKey.CP;
		putFilter(key, createDescription(key, StringID.Main.FILE_ID),
				new ExporterCP(),
				new LoaderCP());

		key = FileTypeKey.SVG;
		putFilter(key, createDescription(key, StringID.Main.FILE_ID),
				ExporterSVGFactory.createCreasePatternExporter(),
				null);

		key = FileTypeKey.PDF;
		putFilter(key, createDescription(key, StringID.Main.FILE_ID),
				null,
				new LoaderPDF());

	}

	/**
	 *
	 * @param key
	 * @param desctiption
	 * @param exporter
	 * @param loader
	 */
	private void putFilter(final FileTypeKey key, final String desctiption,
			final Exporter<Doc> exporter, final Loader<Doc> loader) {
		FileAccessSupportFilter<Doc> filter;

		filter = new FileAccessSupportFilter<>(key,
				desctiption);

		if (exporter != null) {
			filter.setSavingAction(new SavingDocAction(exporter));
		}

		if (loader != null) {
			filter.setLoadingAction(new LoadingDocAction(loader));
		}
		this.putFilter(key, filter);

	}

	/**
	 *
	 * @param fileTypeKey
	 * @param resourceKey
	 * @return
	 */
	private String createDescription(final FileTypeKey fileTypeKey, final String resourceKey) {
		return FileAccessSupportFilter.createDefaultDescription(fileTypeKey,
				resourceHolder.getString(ResourceKey.LABEL, resourceKey));

	}

	/**
	 *
	 * @param key
	 *            A value that describes the file type you want.
	 * @return A filter for given key.
	 */
	public FileAccessSupportFilter<Doc> getFilter(final FileTypeKey key) {
		return filters.get(key);
	}

	// /**
	// *
	// * @param key
	// * A value that describes the file type you want.
	// * @return A filter for given key.
	// */
	// @Deprecated
	// public FileAccessSupportFilter<Doc> getFilter(String key) {
	// return filters.get(key);
	// }

	/**
	 *
	 * @param key
	 *            A value that describes the file type you want.
	 * @param filter
	 *            A filter to be set.
	 * @return A filter for given key.
	 */

	public FileAccessSupportFilter<Doc> putFilter(final FileTypeKey key,
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
		for (FileTypeKey key : filters.keySet()) {
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

		for (FileTypeKey key : filters.keySet()) {
			FileAccessSupportFilter<Doc> filter = filters.get(key);
			if (filter.getLoadingAction() != null) {
				loadables.add(filter);
			}
		}

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
		File file = new File(path);
		if (file.isDirectory()) {
			return null;
		}

		for (FileAccessSupportFilter<Doc> filter : this.toArray()) {
			if (filter.accept(file)) {
				filter.getLoadingAction().setPath(path);
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

		for (FileTypeKey key : filters.keySet()) {
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
