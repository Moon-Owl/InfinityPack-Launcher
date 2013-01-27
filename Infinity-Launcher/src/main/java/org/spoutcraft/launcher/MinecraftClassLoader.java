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
package org.spoutcraft.launcher;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.CodeSigner;
import java.security.CodeSource;
import java.util.HashMap;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class MinecraftClassLoader extends URLClassLoader {
	private final HashMap<String, Class<?>> loadedClasses = new HashMap<String, Class<?>>(
			1000);
	private File spoutcraft = null;
	private File custom = null;
	private final File[] libraries;

	public MinecraftClassLoader(URL[] urls, ClassLoader parent,
			File spoutcraft, File custom, File[] libraries) {
		super(urls, parent);
		this.spoutcraft = spoutcraft;
		this.libraries = libraries;
		this.custom = custom;
		for (File f : libraries) {
			try {
				this.addURL(f.toURI().toURL());
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
		}
		try {
			this.addURL(custom.toURI().toURL());
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}

	// NOTE: VerifyException is due to multiple classes of the same type in
	// jars, need to override all classloader methods to fix...

	@Override
	protected Class<?> findClass(String name) throws ClassNotFoundException {
		Class<?> result = loadedClasses.get(name); // checks in cached classes
		if (result != null) {
			return result;
		}

		result = findClassInjar(name, spoutcraft);
		if (result != null) {
			return result;
		}

		result = findClassInjar(name, custom);
		if (result != null) {
			return result;
		}

		for (File file : libraries) {
			result = findClassInjar(name, file);
			if (result != null) {
				return result;
			}
		}
		return super.findClass(name);
	}

	private Class<?> findClassInjar(String name, File file)
			throws ClassNotFoundException {
		try {
			if (!file.canRead()) {
				Util.log("Not allowed to open '%s'!.", file.getName());
			}
			JarFile jar = new JarFile(file);
			JarEntry entry = jar.getJarEntry(name.replace(".", "/") + ".class");
			if (entry != null) {
				InputStream is = jar.getInputStream(entry);
				ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
				int next = is.read();
				while (-1 != next) {
					byteStream.write(next);
					next = is.read();
				}

				byte classByte[] = byteStream.toByteArray();
				Class<?> result = defineClass(name, classByte, 0,
						classByte.length, new CodeSource(file.toURI().toURL(),
								(CodeSigner[]) null));
				loadedClasses.put(name, result);
				return result;
			}
			jar.close();
		} catch (Exception e) {
			Util.log("Errpr opening '%s'.", file.getName());
			e.printStackTrace();
		}
		return null;
	}
}
