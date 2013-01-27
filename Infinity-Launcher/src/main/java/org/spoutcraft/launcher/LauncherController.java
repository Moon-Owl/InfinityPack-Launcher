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

import java.applet.Applet;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;

import org.spoutcraft.launcher.exception.CorruptedMinecraftJarException;
import org.spoutcraft.launcher.exception.MinecraftVerifyException;
import org.spoutcraft.launcher.exception.UnknownMinecraftException;

public class LauncherController {

	public static Class<?> mcClass = null, appletClass = null;
	public static Field mcField = null;

	public static Applet getMinecraftApplet()
			throws CorruptedMinecraftJarException, MinecraftVerifyException {

		File mcBinFolder = GameUpdater.binDir;

		File spoutcraftJar = new File(mcBinFolder, "modpack.jar");
		File minecraftJar = new File(mcBinFolder, "minecraft.jar");
		File jinputJar = new File(mcBinFolder, "jinput.jar");
		File lwglJar = new File(mcBinFolder, "lwjgl.jar");
		File lwjgl_utilJar = new File(mcBinFolder, "lwjgl_util.jar");
		File customJar = new File(mcBinFolder, "custom.jar");
		int librarycount = 6;

		if (!customJar.exists()) {
			try {
				FileOutputStream stream = new FileOutputStream(customJar);
				JarOutputStream out = new JarOutputStream(stream);
				JarEntry entry = new JarEntry("wee/");
				out.putNextEntry(entry);
				out.close();
				stream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		File[] files = new File[librarycount];
		URL urls[] = new URL[librarycount];

		try {
			// spoutcraftJar must be loaded first into classpath with FML 3.x+
			urls[0] = spoutcraftJar.toURI().toURL();
			files[0] = spoutcraftJar;
			urls[1] = minecraftJar.toURI().toURL();
			files[1] = minecraftJar;
			urls[2] = jinputJar.toURI().toURL();
			files[2] = jinputJar;
			urls[3] = lwglJar.toURI().toURL();
			files[3] = lwglJar;
			urls[4] = lwjgl_utilJar.toURI().toURL();
			files[4] = lwjgl_utilJar;
			urls[5] = customJar.toURI().toURL();
			files[5] = customJar;

			ClassLoader classLoader = new MinecraftClassLoader(urls,
					ClassLoader.getSystemClassLoader(), spoutcraftJar,
					customJar, files);

			setMinecraftDirectory(classLoader, GameUpdater.modpackDir);

			String nativesPath = new File(mcBinFolder, "natives")
					.getAbsolutePath();
			System.setProperty("org.lwjgl.librarypath", nativesPath);
			System.setProperty("net.java.games.input.librarypath", nativesPath);

			appletClass = classLoader
					.loadClass("net.minecraft.client.MinecraftApplet");
			mcClass = classLoader.loadClass("net.minecraft.client.Minecraft");
			mcField = appletClass.getDeclaredFields()[1];

			return (Applet) appletClass.newInstance();
		} catch (MalformedURLException ex) {
			ex.printStackTrace();
			return null;
		} catch (ClassNotFoundException ex) {
			throw new CorruptedMinecraftJarException(ex);
		} catch (IllegalAccessException ex) {
			throw new CorruptedMinecraftJarException(ex);
		} catch (InstantiationException ex) {
			throw new CorruptedMinecraftJarException(ex);
		} catch (VerifyError ex) {
			throw new MinecraftVerifyException(ex);
		} catch (Throwable t) {
			throw new UnknownMinecraftException(t);
		}
	}

	/*
	 * This method works based on the assumption that there is only one field in
	 * Minecraft.class that is a private static File, this may change in the
	 * future and so should be tested with new minecraft versions.
	 */
	private static void setMinecraftDirectory(ClassLoader loader, File directory)
			throws MinecraftVerifyException {
		try {
			Class<?> clazz = loader.loadClass("net.minecraft.client.Minecraft");
			Field[] fields = clazz.getDeclaredFields();

			int fieldCount = 0;
			Field mineDirField = null;
			for (Field field : fields) {
				if (field.getType() == File.class) {
					int mods = field.getModifiers();
					if (Modifier.isStatic(mods) && Modifier.isPrivate(mods)) {
						mineDirField = field;
						fieldCount++;
					}
				}
			}
			if (fieldCount != 1) {
				throw new MinecraftVerifyException(
						"Cannot find directory field in minecraft");
			}

			mineDirField.setAccessible(true);
			mineDirField.set(null, directory);

		} catch (Exception e) {
			throw new MinecraftVerifyException(e,
					"Cannot set directory in Minecraft class");
		}

	}
}
