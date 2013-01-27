/*
 * This file is part of Spoutcraft Launcher (http://wiki.getspout.org/).
 * 
 * Spoutcraft Launcher is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Spoutcraft Launcher is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.spoutcraft.launcher.async;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

import org.spoutcraft.launcher.Util;

/**
 * Downloads stuff asynchronously. In fact, it's a modified version of
 * StackOverflow sample ;)
 */
public class Download implements Runnable {

	private static final long TIMEOUT = 30000;
	private final URL url;
	private long size = -1;
	private long downloaded = 0;
	private final String outPath;
	private DownloadListener listener;
	private boolean success = false;
	private File outFile = null;
	private final String name;

	public Download(String url, String outPath) throws MalformedURLException {
		this.url = new URL(url);
		this.outPath = outPath;
		this.name = url.substring(url.lastIndexOf('/') + 1, url.length());
	}

	public float getProgress() {
		return ((float) downloaded / size) * 100;
	}

	@Override
	public void run() {
		try {
			URLConnection conn = url.openConnection();
			conn.setRequestProperty(
					"User-Agent",
					"Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/534.30 (KHTML, like Gecko) Chrome/12.0.742.100 Safari/534.30");
			InputStream in = getConnectionInputStream(conn);

			size = conn.getContentLength();
			outFile = new File(outPath);
			outFile.delete();

			final ReadableByteChannel rbc = Channels.newChannel(in);
			final FileOutputStream fos = new FileOutputStream(outFile);

			stateChanged();

			// Create a thread to monitor progress
			final Thread instance = Thread.currentThread();
			Thread progress = new Thread() {

				long last = System.currentTimeMillis();

				@Override
				public void run() {
					while (!this.isInterrupted()) {

						long diff = outFile.length() - downloaded;
						downloaded = outFile.length();

						if (diff == 0) { // nothing downloaded
							if ((System.currentTimeMillis() - last) > TIMEOUT) { // waited
								// too
								// long
								if (listener != null) { // alert ui
									listener.stateChanged("Download Failed",
											getProgress());
								}
								try {
									rbc.close();
									instance.interrupt();
								} catch (IOException e) {
									e.printStackTrace();
								}
								return;
							}
						} else {
							last = System.currentTimeMillis();
						}

						stateChanged();
						try {
							sleep(100);
						} catch (InterruptedException ignore) {
							break;
						}
					}
				}
			};
			progress.start();

			fos.getChannel().transferFrom(rbc, 0,
					size > 0 ? size : Integer.MAX_VALUE);
			in.close();
			rbc.close();
			fos.close();
			progress.interrupt();
			success = size > 0 ? (size == outFile.length()) : true;
		} catch (IOException ioe) {
			ioe.printStackTrace();
			Util.log(String.format("Cannot open '%s' for download.", url));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected InputStream getConnectionInputStream(
			final URLConnection urlconnection) throws Exception {
		final InputStream[] is = new InputStream[1];

		for (int j = 0; (j < 3) && (is[0] == null); j++) {
			Thread stream = new Thread() {

				@Override
				public void run() {
					try {
						is[0] = urlconnection.getInputStream();
					} catch (IOException ignore) {
					}
				}
			};
			stream.start();
			int iterationCount = 0;
			while ((is[0] == null) && (iterationCount++ < 5)) {
				try {
					stream.join(1000L);
				} catch (InterruptedException ignore) {
				}
			}
			if (is[0] != null) {
				continue;
			}
			try {
				stream.interrupt();
				stream.join();
			} catch (InterruptedException ignore) {
			}
		}

		if (is[0] == null) {
			throw new Exception("Unable to download file");
		}
		return new BufferedInputStream(is[0]);
	}

	private void stateChanged() {
		if (listener != null) {
			listener.stateChanged(name, getProgress());
		}
	}

	public void setListener(DownloadListener listener) {
		this.listener = listener;
	}

	public boolean isSuccess() {
		return success;
	}

	public File getOutFile() {
		return outFile;
	}
}
