package org.spoutcraft.launcher;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Apache Commons IO
 */
public class FileUtils {

	/**
	 * Deletes a directory recursively.
	 * 
	 * @param directory
	 *            directory to delete
	 * @throws IOException
	 *             in case deletion is unsuccessful
	 */
	public static void deleteDirectory(File directory) throws IOException {
		if (!directory.exists()) {
			return;
		}

		if (!isSymlink(directory)) {
			cleanDirectory(directory);
		}

		if (!directory.delete()) {
			String message = "Unable to delete directory " + directory + ".";
			throw new IOException(message);
		}
	}

	/**
	 * Deletes a file, never throwing an exception. If file is a directory,
	 * delete it and all sub-directories.
	 * <p>
	 * The difference between File.delete() and this method are:
	 * <ul>
	 * <li>A directory to be deleted does not have to be empty.</li>
	 * <li>No exceptions are thrown when a file or directory cannot be deleted.</li>
	 * </ul>
	 * 
	 * @param file
	 *            file or directory to delete, can be <code>null</code>
	 * @return <code>true</code> if the file or directory was deleted, otherwise
	 *         <code>false</code>
	 * 
	 * @since Commons IO 1.4
	 */
	public static boolean deleteQuietly(File file) {
		if (file == null) {
			return false;
		}
		try {
			if (file.isDirectory()) {
				cleanDirectory(file);
			}
		} catch (Exception ignored) {
		}

		try {
			return file.delete();
		} catch (Exception ignored) {
		}
		return false;
	}

	/**
	 * Cleans a directory without deleting it.
	 * 
	 * @param directory
	 *            directory to clean
	 * @throws IOException
	 *             in case cleaning is unsuccessful
	 */
	public static void cleanDirectory(File directory) {
		if (!directory.exists()) {
			Util.log("%s does not exist", directory);
		}

		if (!directory.isDirectory()) {
			Util.log("%s is not a directory", directory);
		}

		File[] files = directory.listFiles();
		if (files == null) { // null if security restricted
			Util.log("Failed to list contents of %s", directory);
			return;
		}

		for (File file : files) {
			try {
				forceDelete(file);
			} catch (IOException ioe) {
				Util.logi("File %s failed to delete", file);
			}
		}
	}

	/**
	 * Deletes a file. If file is a directory, delete it and all
	 * sub-directories.
	 * <p>
	 * The difference between File.delete() and this method are:
	 * <ul>
	 * <li>A directory to be deleted does not have to be empty.</li>
	 * <li>You get exceptions when a file or directory cannot be deleted.
	 * (java.io.File methods returns a boolean)</li>
	 * </ul>
	 * 
	 * @param file
	 *            file or directory to delete, must not be <code>null</code>
	 * @throws NullPointerException
	 *             if the directory is <code>null</code>
	 * @throws FileNotFoundException
	 *             if the file was not found
	 * @throws IOException
	 *             in case deletion is unsuccessful
	 */
	public static void forceDelete(File file) throws IOException {
		if (file.isDirectory()) {
			deleteDirectory(file);
		} else {
			boolean filePresent = file.exists();
			if (!file.delete()) {
				if (!filePresent) {
					throw new FileNotFoundException("File does not exist: "
							+ file);
				}
				String message = "Unable to delete file: " + file;
				throw new IOException(message);
			}
		}
	}

	/**
	 * Determines whether the specified file is a Symbolic Link rather than an
	 * actual file.
	 * <p>
	 * Will not return true if there is a Symbolic Link anywhere in the path,
	 * only if the specific file is.
	 * 
	 * @param file
	 *            the file to check
	 * @return true if the file is a Symbolic Link
	 * @throws IOException
	 *             if an IO error occurs while checking the file
	 * @since Commons IO 2.0
	 */
	public static boolean isSymlink(File file) throws IOException {
		if (file == null) {
			throw new NullPointerException("File must not be null");
		}
		File fileInCanonicalDir;
		if (file.getParent() == null) {
			fileInCanonicalDir = file;
		} else {
			File canonicalDir = file.getParentFile().getCanonicalFile();
			fileInCanonicalDir = new File(canonicalDir, file.getName());
		}

		return (!fileInCanonicalDir.getCanonicalFile().equals(
				fileInCanonicalDir.getAbsoluteFile()));
	}
}
