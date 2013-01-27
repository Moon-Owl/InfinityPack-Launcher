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
package org.spoutcraft.launcher.gui;

import java.applet.Applet;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import net.minecraft.Launcher;

import org.spoutcraft.launcher.LauncherController;
import org.spoutcraft.launcher.MinecraftUtils;
import org.spoutcraft.launcher.PlatformUtils;
import org.spoutcraft.launcher.Util;
import org.spoutcraft.launcher.exception.CorruptedMinecraftJarException;
import org.spoutcraft.launcher.exception.MinecraftVerifyException;
import org.spoutcraft.launcher.modpacks.ModPackListYML;
import org.spoutcraft.launcher.modpacks.ModPackYML;

public class LauncherFrame extends JFrame implements WindowListener {
	private static final long serialVersionUID = 4524937541564722358L;
	private Launcher minecraft;
	private MainPane loginForm = null;

	public static final int RETRYING_LAUNCH = -1;
	public static final int ERROR_IN_LAUNCH = 0;
	public static final int SUCCESSFUL_LAUNCH = 1;

	public LauncherFrame() {
		super(ModPackListYML.currentModPackLabel);
		if (System.getProperty("os.name").contains("OS X")) {
			try {
				Class<?> fullScreenUtilityClass = Class
						.forName("com.apple.eawt.FullScreenUtilities");
				java.lang.reflect.Method setWindowCanFullScreenMethod = fullScreenUtilityClass
						.getDeclaredMethod("setWindowCanFullScreen",
								new Class[] { Window.class, Boolean.TYPE });
				setWindowCanFullScreenMethod.invoke(null, new Object[] { this,
						Boolean.valueOf(true) });
			} catch (Exception e) {
				// This is not a fatal exception, so just log it for brevity.
				e.printStackTrace();
			}
		}
		super.setVisible(true);
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		this.setLocation((dim.width - 870) / 2, (dim.height - 518) / 2);
		this.setSize(new Dimension(871, 519));
		this.setResizable(true);
		this.addWindowListener(this);

		setIconImage(Toolkit.getDefaultToolkit().getImage(
				ModPackYML.getModPackIcon()));
	}

	public void setLoginForm(MainPane form) {
		loginForm = form;
	}

	public MainPane getLoginForm() {
		return loginForm;
	}

	public int runGame(String user, String session, String downloadTicket,
			String mcpass) {
		Applet applet = null;
		try {
			applet = LauncherController.getMinecraftApplet();
		} catch (CorruptedMinecraftJarException corruption) {
			corruption.printStackTrace();
		} catch (MinecraftVerifyException verify) {
			System.err
					.println("Exception thrown while initializing MineCraft.");
			verify.printStackTrace();
			OptionDialog.clearCache();
			JOptionPane
					.showMessageDialog(
							getParent(),
							"The minecraft installation was corrupted. \nThe minecraft installation has been cleaned. \nTry to login again. If that fails, close and \nrestart the appplication.");
			this.setVisible(false);
			this.dispose();
			return ERROR_IN_LAUNCH;
		} catch (Throwable t) {
			System.err
					.println("Exception thrown while initializing MineCraft.");
			t.printStackTrace();
			applet = null;
		}
		if (applet == null) {
			String message = "Failed to start launcher, errors reported in the log.";
			this.setVisible(false);
			JOptionPane.showMessageDialog(getParent(), message);
			this.dispose();
			return ERROR_IN_LAUNCH;
		}

		minecraft = new Launcher(applet);

		String launcherPath = String.format("%s/%s",
				PlatformUtils.LAUNCHER_DIR, ModPackListYML.currentModPack);

		minecraft.addParameter("username", user);
		minecraft.addParameter("sessionid", session);
		minecraft.addParameter("downloadticket", downloadTicket);
		minecraft.addParameter("mppass", mcpass);
		minecraft.addParameter("spoutcraftlauncher", "true");
		minecraft.addParameter("stand-alone", "true");
		minecraft.addParameter("portable",
				String.valueOf(MinecraftUtils.getOptions().isPortable()));
		minecraft.addParameter("directory", launcherPath);
		Util.log("Loading Launcher from '%s'", launcherPath);
		if (MinecraftUtils.getOptions().getServer() != null) {
			minecraft.addParameter("server", MinecraftUtils.getOptions()
					.getServer());
			if (MinecraftUtils.getOptions().getPort() != null) {
				minecraft.addParameter("port", MinecraftUtils.getOptions()
						.getPort());
			}
		}

		applet.setStub(minecraft);

		this.add(minecraft);
		validate();

		try {
			minecraft.init();
			minecraft.setSize(getWidth(), getHeight());
			minecraft.start();
		} catch (Throwable t) {
			System.err
					.println("Exception thrown while initializing MineCraft.");
			t.printStackTrace();
			JOptionPane.showMessageDialog(getParent(),
					"Minecraft failed to start, errors reported in the log.");
			this.setVisible(false);
			this.dispose();
			return ERROR_IN_LAUNCH;
		}

		this.setVisible(true);
		return SUCCESSFUL_LAUNCH;
	}

	@Override
	public void windowActivated(WindowEvent e) {
	}

	@Override
	public void windowClosed(WindowEvent e) {
	}

	@Override
	public void windowClosing(WindowEvent e) {
		if (LauncherFrame.this.minecraft != null) {
			LauncherFrame.this.minecraft.stop();
			LauncherFrame.this.minecraft.destroy();
		}
		try {
			Thread.sleep(10000L);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		System.out.println("Exiting Launcher");
		System.exit(0);
	}

	@Override
	public void windowDeactivated(WindowEvent e) {
	}

	@Override
	public void windowDeiconified(WindowEvent e) {
	}

	@Override
	public void windowIconified(WindowEvent e) {
	}

	@Override
	public void windowOpened(WindowEvent e) {
	}
}
