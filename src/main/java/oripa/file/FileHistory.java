package oripa.file;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.stream.Collectors;

import oripa.resource.Constants;

/**
 * for handling the most recently used files list
 *
 */
public class FileHistory {
	private LinkedList<String> mostRecentlyUsedHistory = new LinkedList<>();
	private final int maxSize;

	/**
	 *
	 * Constructor
	 *
	 * @param maxSize
	 */
	public FileHistory(final int maxSize) {
		this.maxSize = maxSize;
	}

	/**
	 *
	 * @return maxSize of most recently used list
	 */
	public int getMaxSize() {
		return maxSize;
	}

	/**
	 * update used paths list
	 *
	 * @param filePath
	 * @return true if the given path is appended to history
	 */
	public boolean useFile(final String filePath) {
		int index = mostRecentlyUsedHistory.indexOf(filePath);

		if (index < 0) {
			if (mostRecentlyUsedHistory.size() >= maxSize) {
				mostRecentlyUsedHistory.removeLast();
			}

			mostRecentlyUsedHistory.addFirst(filePath);
			return true;
		}

		String item = mostRecentlyUsedHistory.remove(index);
		mostRecentlyUsedHistory.addFirst(item);

		return false;
	}

	/**
	 *
	 * @return most recently used paths
	 */
	public Collection<String> getHistory() {
		return mostRecentlyUsedHistory;
	}

	/**
	 * set all most recently used paths
	 *
	 * @param history
	 */
	public void setHistory(final Collection<String> history) {
		mostRecentlyUsedHistory = new LinkedList<>(history);
	}

	/**
	 *
	 * @return only the last file path. if not possible user home.
	 */
	public String getLastPath() {
		if (mostRecentlyUsedHistory.isEmpty()) {
			return Constants.UER_HOME_DIR_PATH;
		}

		return mostRecentlyUsedHistory.getFirst();
	}

	/**
	 *
	 * @return last directory. if no last file found user home.
	 */
	public String getLastDirectory() {
		if (mostRecentlyUsedHistory.isEmpty()) {
			return Constants.UER_HOME_DIR_PATH;
		}

		File file = new File(mostRecentlyUsedHistory.getFirst());
		try {
			return file.getParentFile().getCanonicalPath();
		} catch (IOException e) {
			return Constants.UER_HOME_DIR_PATH;
		}
	}

	/**
	 * set history list from {@code ini}
	 *
	 * @param ini
	 */
	public void loadFromInitData(final InitData ini) {
		var mruList = Arrays.asList(ini.getMRUFiles())
				.subList(0, Math.min(maxSize, ini.getMRUFiles().length)).stream()
				.filter(fileName -> fileName != null && !fileName.isEmpty())
				.collect(Collectors.toList());

		setHistory(mruList);
	}

}
