package org.spoutcraft.launcher.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.ScrollPane;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;
import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JProgressBar;
import javax.swing.JRadioButton;
import javax.swing.JSlider;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.plaf.ColorUIResource;

import org.spoutcraft.launcher.FileUtils;
import org.spoutcraft.launcher.GameUpdater;
import org.spoutcraft.launcher.LibrariesYML;
import org.spoutcraft.launcher.MD5Utils;
import org.spoutcraft.launcher.Main;
import org.spoutcraft.launcher.MinecraftUtils;
import org.spoutcraft.launcher.MinecraftYML;
import org.spoutcraft.launcher.MirrorUtils;
import org.spoutcraft.launcher.PlatformUtils;
import org.spoutcraft.launcher.SettingsUtil;
import org.spoutcraft.launcher.Util;
import org.spoutcraft.launcher.async.DownloadListener;
import org.spoutcraft.launcher.exception.AccountMigratedException;
import org.spoutcraft.launcher.exception.BadLoginException;
import org.spoutcraft.launcher.exception.MCNetworkException;
import org.spoutcraft.launcher.exception.MinecraftUserNotPremiumException;
import org.spoutcraft.launcher.exception.NoMirrorsAvailableException;
import org.spoutcraft.launcher.exception.OutdatedMCLauncherException;
import org.spoutcraft.launcher.gui.widget.ComboBoxRenderer;
import org.spoutcraft.launcher.modpacks.ModLibraryYML;
import org.spoutcraft.launcher.modpacks.ModPackListYML;
import org.spoutcraft.launcher.modpacks.ModPackUpdater;
import org.spoutcraft.launcher.modpacks.ModPackYML;
import javax.swing.JScrollPane;

public class MainPane extends JFrame implements ActionListener,
		DownloadListener, KeyListener, WindowListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JPanel mainpane = new JPanel();
	private static JPasswordField pField_1;
	private final JButton loginButton = new JButton("Login");
	OptionDialog options;
	JButton modsButton = new JButton("Add/Remove Mods");
	JProgressBar progressBar = new JProgressBar();
	public String workingDir = PlatformUtils.getWorkingDirectory()
			.getAbsolutePath();
	static HashMap<String, UserPasswordInformation> usernames = new HashMap<String, UserPasswordInformation>();
	private final JButton loginSkin1;
	private final List<JButton> loginSkin1Image;
	JLabel uField = new JLabel("UserName:");
	JLabel pField = new JLabel("Password:");
	static JComboBox uField_1 = new JComboBox();
	static JCheckBox remembercheck = new JCheckBox("Remember");
	private String value;
	public static final ModPackUpdater gameUpdater = new ModPackUpdater();
	JButton offlinemode = new JButton("Offline Mode");
	public static String[] values = null;
	private static String pass = null;
	private int success = LauncherFrame.ERROR_IN_LAUNCH;
	public boolean mcUpdate = false;
	public boolean spoutUpdate = false;
	public boolean modpackUpdate = false;
	public static UpdateDialog updateDialog;
	JComboBox buidselect = new JComboBox();
	JRadioButton devBuilds = new JRadioButton("Always use development builds");
	JRadioButton recBuilds = new JRadioButton("Always use recommended builds");
	JRadioButton customBuilds = new JRadioButton("Manual build selection");
	private final JButton tryAgain = new JButton("Try Again");
	private final JComboBox modpackList;
	private ActionListener actionPerformed;
	private ActionListener actionPerformed1;
	private JButton clearcachebutton = new JButton("Clear Cache");
	private JPanel build = new JPanel();
	JCheckBox backupCheckbox = new JCheckBox(
			"Include worlds when doing automated backup");
	JCheckBox retryLoginCheckbox = new JCheckBox(
			"Retry after connection timeout");
	JLabel buildInfo = new JLabel();
	JLabel urlLabel = new JLabel();
	JTextField urlText = new JTextField();
	private TumblerFeedParsingWorker         tumblerFeed;
	private JTextPane editorPane_2;
	public MainPane() {
		loadLauncherData();

		MainPane.updateDialog = new UpdateDialog(this);
		gameUpdater.setListener(this);

		this.addWindowListener(this);
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		setBounds((dim.width - 800) / 2, (dim.height - 500) / 2, 800, 500);

		setContentPane(mainpane);
		getContentPane().setBackground(new Color(12, 12, 12));
		getContentPane().setLayout(null);

		uField_1.setBounds(302, 9, 150, 26);
		getContentPane().add(uField_1);
		uField_1.getEditor().addActionListener(this);
		uField_1.setEditable(true);
		remembercheck.setForeground(Color.WHITE);
		remembercheck.setOpaque(false);

		remembercheck.setBounds(686, 13, 92, 18);
		getContentPane().add(remembercheck);

		pField.setBounds(459, 14, 65, 16);
		getContentPane().add(pField);
		pField.setFont(new Font("SansSerif", Font.BOLD, 12));
		pField.setForeground(Color.WHITE);

		uField.setBounds(234, 14, 71, 16);
		getContentPane().add(uField);
		uField.setFont(new Font("SansSerif", Font.BOLD, 12));
		uField.setForeground(Color.WHITE);
		uField.setBackground(Color.WHITE);

		pField_1 = new JPasswordField();
		pField_1.setBounds(523, 8, 156, 28);
		pField_1.addKeyListener(this);
		remembercheck.addKeyListener(this);
		getContentPane().add(pField_1);

		JTabbedPane tabbedPane = new JTabbedPane(SwingConstants.BOTTOM);
		tabbedPane.setBorder(null);
		tabbedPane.setOpaque(true);
		tabbedPane.setBackground(new Color(40, 40, 40));
		tabbedPane.setBounds(0, 51, 784, 411);
		getContentPane().add(tabbedPane);

		readUsedUsernames();

		JPanel mainPanel = new JPanel();
		mainPanel.setOpaque(false);
		mainPanel.setBackground(Color.GRAY);
		tabbedPane.addTab("", new ImageIcon("maps.png"), mainPanel);
		mainPanel.setLayout(null);
		
		JRadioButton selectbuildrad = new JRadioButton("Select Build");
		selectbuildrad.setForeground(Color.WHITE);
		selectbuildrad.setBackground(Color.DARK_GRAY);
		selectbuildrad.setBounds(134, 183, 109, 23);
		mainPanel.add(selectbuildrad);
		selectbuildrad.setVisible(false);
		
		JCheckBox retrylogincheck = new JCheckBox("Retry Logins");
		retrylogincheck.setForeground(Color.WHITE);
		retrylogincheck.setBackground(Color.DARK_GRAY);
		retrylogincheck.setBounds(16, 194, 116, 23);
		mainPanel.add(retrylogincheck);
		retrylogincheck.setVisible(false);
		
		JCheckBox backmapscheck = new JCheckBox("Back up Maps");
		backmapscheck.setForeground(Color.WHITE);
		backmapscheck.setBackground(Color.DARK_GRAY);
		backmapscheck.setBounds(16, 173, 116, 23);
		mainPanel.add(backmapscheck);
		backmapscheck.setVisible(false);

		offlinemode.setBounds(6, 280, 247, 55);
		mainPanel.add(offlinemode);
		offlinemode.setVisible(false);

		JLabel lblmodpackSelect = new JLabel(
				"^ModPack Select   |   Build Select v");
		lblmodpackSelect.setFont(new Font("SansSerif", Font.BOLD, 12));
		lblmodpackSelect.setForeground(Color.WHITE);
		lblmodpackSelect.setBounds(28, 115, 202, 16);
		mainPanel.add(lblmodpackSelect);

		List<String> items = new ArrayList<String>();
		int i = 0;
		for (String item : ModPackListYML.modpackMap.keySet()) {
			if (!Main.isOffline || GameUpdater.canPlayOffline(item)) {
				items.add(item);
				i += 1;
			}
		}
		String[] itemArray = new String[i];
		modpackList = new JComboBox(items.toArray(itemArray));
		modpackList.setBounds(6, 6, 328, 100);
		ComboBoxRenderer renderer = new ComboBoxRenderer();
	    renderer.setPreferredSize(new Dimension(200, 110));
	    
	    modpackList.setRenderer(renderer);
	    modpackList.setMaximumRowCount(4);
	    modpackList.setSelectedItem(SettingsUtil.getModPackSelection());
	    modpackList.addActionListener(this);
		
		mainPanel.add(modpackList);

		
		

		buidselect.setBounds(6, 132, 247, 26);
		mainPanel.add(buidselect);
		buidselect.addActionListener(this);
		buidselect.setEnabled(true);


		updateBuildsList();
		updateBuildsCombo();
		
		

		loginButton.setFont(new Font("Arial", Font.BOLD, 15));
		loginButton.setBounds(6, 280, 247, 55);
		loginButton.setOpaque(false);
		loginButton.addActionListener(this);

		loginButton.setEnabled(true); // disable until login info is read
		mainPanel.add(loginButton);

		modsButton.setFont(new Font("SansSerif", Font.BOLD, 15));
		modsButton.setBounds(6, 224, 247, 55);
		mainPanel.add(modsButton);

		loginSkin1 = new JButton("Login as Player");
		loginSkin1.setFont(new Font("Arial", Font.PLAIN, 11));
		loginSkin1.setBounds(72, 428, 119, 23);
		loginSkin1.setOpaque(false);
		loginSkin1.addActionListener(this);
		loginSkin1.setVisible(false);
		loginSkin1Image = new ArrayList<JButton>();

		JEditorPane newsEPane = new JEditorPane();
		newsEPane.setSelectedTextColor(Color.LIGHT_GRAY);
		newsEPane.setForeground(Color.WHITE);
		newsEPane.setEditable(false);
		newsEPane.setDisabledTextColor(Color.RED);
		newsEPane.setBackground(Color.GRAY);
		newsEPane.setBounds(436, 6, 342, 329);


		JPanel newsPane = new JPanel();
		newsPane.setBackground(Color.BLACK);
		tabbedPane.addTab("", new ImageIcon("news.png"), newsPane, null);
		newsPane.setLayout(null);
		
        JTextPane editorPane_2 = new JTextPane();
		editorPane_2.setEditable(false);
	    editorPane_2.setOpaque(false);
		editorPane_2.setBounds(10, 0, 764, 331);
		editorPane_2.setContentType("text/html");
		mainpane.add(editorPane_2);
		

		JPanel settingsPane = new JPanel();
		settingsPane.setBackground(Color.BLACK);
		tabbedPane
				.addTab("", new ImageIcon("settings.png"), settingsPane, null);
		settingsPane.setLayout(null);

		clearcachebutton.setBounds(41, 111, 200, 41);
		clearcachebutton.addActionListener(this);
		settingsPane.add(clearcachebutton);

		JLabel lblSelectRamAmount = new JLabel("NOT IN USE");
		lblSelectRamAmount.setBounds(83, 24, 120, 16);
		settingsPane.add(lblSelectRamAmount);
		

		JSlider ramSlider = new JSlider();
		ramSlider.setBounds(41, 52, 200, 21);
		settingsPane.add(ramSlider);

		JLabel settingsBack = new JLabel("");
		settingsBack.setOpaque(true);
		settingsBack.setBounds(6, 6, 772, 329);
		settingsPane.add(settingsBack);

		JLabel loginBack = new JLabel("");
		loginBack.setBounds(229, 6, 549, 33);
		getContentPane().add(loginBack);
		loginBack.setOpaque(true);
		loginBack.setBackground(new Color(32, 32, 32));

		progressBar.setVisible(false);
		progressBar.setStringPainted(true);
		progressBar.setOpaque(true);
		progressBar.setBounds(6, 6, 216, 33);
		getContentPane().add(progressBar);
		(new File(PlatformUtils.getWorkingDirectory(), "launcher_cache.jpg"))
				.delete();

		// loginButton.setEnabled(true); // enable once logins are read
		modsButton.setEnabled(true);
		setResizable(false);
		List<Component> order = new ArrayList<Component>(5);
		order.add(uField_1.getEditor().getEditorComponent());
		order.add(pField_1);
		order.add(remembercheck);
		order.add(loginButton);
		order.add(modsButton);
		
		JLabel buildback = new JLabel("");
		buildback.setOpaque(true);
		buildback.setBackground(Color.DARK_GRAY);
		buildback.setBounds(6, 169, 247, 55);
		mainPanel.add(buildback);
		
		JScrollPane scrollPane_1 = new JScrollPane();
		scrollPane_1.setBounds(396, 0, 383, 383);
		mainPanel.add(scrollPane_1);

	}

	public static boolean clearCache() {
		try {
			FileUtils.deleteDirectory(GameUpdater.binDir);
			FileUtils.deleteDirectory(GameUpdater.tempDir);
			FileUtils.deleteDirectory(GameUpdater.cacheDir);
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		} finally {
			ModPackYML.getModPackYML().setProperty("current", null);
			MinecraftYML.setInstalledVersion("");
		}
	}

	private String getSelectedBuildFromCombo() {
		String build = null;
		try {
			String item = ((String) buidselect.getSelectedItem());
			if (item.contains("|")) {
				item = item.split("\\|")[0];
			}
			build = item.trim();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return build;
	}

	public void updateBuildsList() {
		buidselect.removeAllItems();
		String[] buildList = ModPackYML.getModpackBuilds();
		if (buildList != null) {
			for (String item : buildList) {
				buidselect.addItem(item);
			}
		} else {
			buidselect.addItem("No builds found");
		}
		updateBuildsCombo();
	}

	public void updateBuildsCombo() {
		buidselect.setEnabled(customBuilds.isSelected());

		if (customBuilds.isSelected()) {
			if (SettingsUtil.getSelectedBuild() != null) {
				String build = SettingsUtil.getSelectedBuild();
				for (int i = 0; i < buidselect.getItemCount(); i++) {
					String item = (String) buidselect.getItemAt(i);
					if (item.contains(String.valueOf(build))) {
						buidselect.setSelectedIndex(i);
						break;
					}
				}
			}
		} else if (devBuilds.isSelected()) {
			buidselect.setSelectedIndex(0);
		} else if (recBuilds.isSelected()) {
			for (int i = 0; i < buidselect.getItemCount(); i++) {
				String item = (String) buidselect.getItemAt(i);
				if (item.contains("Rec. Build")) {
					buidselect.setSelectedIndex(i);
					break;
				}
			}
		}
	}

	public void loadLauncherData() {
		MirrorUtils.updateMirrorsYMLCache();
		MD5Utils.updateMD5Cache();
		ModPackListYML.updateModPacksYMLCache();

		ModPackListYML.getAllModPackResources();
		ModPackListYML.loadModpackLogos();

		LibrariesYML.updateLibrariesYMLCache();
		ModLibraryYML.updateModLibraryYML();

		if (SettingsUtil.getModPackSelection() != null) {
			updateBranding();
		} else {
			setTitle("Infinity Launcher - No Modpack Selected");
		}
	}

	public void updateBranding() {
		loginButton.setEnabled(false);

		setTitle("Loading Modpack Data...");
		SwingWorker<Object, Object> updateThread = new SwingWorker<Object, Object>() {

			@Override
			protected Object doInBackground() throws Exception {
				ModPackListYML.setCurrentModpack();
				return null;
			}

			@Override
			protected void done() {
				if (options == null) {
					options = new OptionDialog();
					options.modPackList = ModPackListYML.modpackMap;
					options.setVisible(false);
				}

				loginButton.setEnabled(true);

				setIconImage(Toolkit.getDefaultToolkit().getImage(
						ModPackYML.getModPackIcon()));
				setTitle(String.format("Technic Launcher - %s - (%s)",
						Main.build, ModPackListYML.currentModPackLabel));
				options.reloadSettings();
				MinecraftYML.updateMinecraftYMLCache();
				setModLoaderEnabled();
			}
		};
		updateThread.execute();
	}

	public void setModLoaderEnabled() {
		File modLoaderConfig = new File(GameUpdater.modconfigsDir,
				"ModLoader.cfg");
		boolean modLoaderExists = modLoaderConfig.exists();
		modsButton.setEnabled(modLoaderExists);
	}

	@Override
	public void stateChanged(String fileName, float progress) {
		int intProgress = Math.round(progress);

		if (intProgress >= 0) {
			progressBar.setValue(intProgress);
			progressBar.setIndeterminate(false);
		} else {
			progressBar.setIndeterminate(true);
		}

		fileName = fileName.replace(workingDir, "");
		if (fileName.contains("?")) {
			fileName = fileName.substring(0, fileName.indexOf("?"));
		}

		if (fileName.length() > 60) {
			fileName = fileName.substring(0, 60) + "...";
		}
		String progressText = intProgress + "% " + fileName;
		if (intProgress < 0)
			progressText = fileName;
		progressBar.setString(progressText);
	}

	@Override
	public void keyTyped(KeyEvent e) {
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if (loginButton.isEnabled() && e.getKeyCode() == KeyEvent.VK_ENTER) {
			doLogin();
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
	}

	private void readUsedUsernames() {
		int i = 0;
		try {
			File lastLogin = new File(PlatformUtils.getWorkingDirectory(),
					"lastlogin");
			if (!lastLogin.exists()) {
				return;
			}
			Cipher cipher = getCipher(2, "passwordfile");

			DataInputStream dis;
			if (cipher != null) {
				dis = new DataInputStream(new CipherInputStream(
						new FileInputStream(lastLogin), cipher));
			} else {
				dis = new DataInputStream(new FileInputStream(lastLogin));
			}

			try {
				// noinspection InfiniteLoopStatement
				while (true) {
					String user = dis.readUTF();
					boolean isHash = dis.readBoolean();
					if (isHash) {
						byte[] hash = new byte[32];
						dis.read(hash);

						usernames.put(user, new UserPasswordInformation(hash));
					} else {
						String password = dis.readUTF();
						if (!password.isEmpty()) {
							i++;
							String skinName = user;
							if (dis.readBoolean())
								skinName = dis.readUTF();

							if (i == 1) {
								// if (tumblerFeed != null) {
								TumblerFeedParsingWorker.setUser(skinName);
								// }
								if (!Main.isOffline) {
									loginSkin1.setText(user);
									loginSkin1.setVisible(true);
									ImageUtils.drawCharacter(mainpane, this, "http://s3.amazonaws.com/MinecraftSkins/" + skinName + ".png", 103, 170, loginSkin1Image);
								}
							} else if (i == 2) { 
								if (!Main.isOffline) {

								}
							}
						}
						usernames.put(user, new UserPasswordInformation(
								password));
					}
					this.uField_1.addItem(user);
				}
			} catch (EOFException ignored) {
			}
			dis.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
		updatePasswordField();
	}

	private void writeUsernameList() {
		try {
			File lastLogin = new File(PlatformUtils.getWorkingDirectory(),
					"lastlogin");

			Cipher cipher = getCipher(1, "passwordfile");

			DataOutputStream dos;
			if (cipher != null) {
				dos = new DataOutputStream(new CipherOutputStream(
						new FileOutputStream(lastLogin), cipher));
			} else {
				dos = new DataOutputStream(
						new FileOutputStream(lastLogin, true));
			}
			for (String user : usernames.keySet()) {
				dos.writeUTF(user);
				UserPasswordInformation info = usernames.get(user);
				dos.writeBoolean(info.isHash);
				if (info.isHash) {
					dos.write(info.passwordHash);
				} else {
					dos.writeUTF(info.password);
				}
				dos.writeBoolean(info.hasProfileName());
				dos.writeUTF(info.getProfileName());
			}
			dos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		String eventId = event.getActionCommand();
		Object source = event.getSource();
		String id = event.getActionCommand();
		
		if ((source == modpackList)) {
			if (ModPackListYML.currentModPack == null) {
				SettingsUtil.init();
				GameUpdater.copy(SettingsUtil.settingsFile,
						ModPackListYML.ORIGINAL_PROPERTIES);
			} else {
				GameUpdater.copy(SettingsUtil.settingsFile, new File(
						GameUpdater.modpackDir, "launcher.properties"));
			}
			String selectedItem = (String) modpackList.getSelectedItem();
			SettingsUtil.setModPack(selectedItem);
			updateBranding();
		}
		if ((eventId.equals("Login") || eventId.equals(uField_1
				.getSelectedItem())) && loginButton.isEnabled()) {
			doLogin();
		} else if (eventId.equals("Options")) {

			options.setBounds((int) getBounds().getCenterX() - 250,
					(int) getBounds().getCenterY() - 75, 360, 325);
		} else if (eventId.equals(modsButton.getText())) {
			if (ModPackListYML.currentModPack != null) {
				open(new File(GameUpdater.modconfigsDir, "ModLoader.cfg"));
			}
		} else if (eventId.equals("comboBoxChanged")) {
			updatePasswordField();
		}

		if (source == offlinemode) {
			gameUpdater.user = "user";
			gameUpdater.downloadTicket = "0";
			offlinemode.setEnabled(false);
			runGame();

		}
		if (source == clearcachebutton) {
			if (clearCache()) {
				JOptionPane.showMessageDialog(getParent(),
						"Successfully cleared the cache.");
			} else {
				JOptionPane
						.showMessageDialog(
								getParent(),
								"Failed to clear the cache! Ensure Modpack files are open.\nIf all else fails, close the launcher, restart it, and try again.");
			}
		}
	}

	public static void open(File document) {
		if (!document.exists())
			return;
		try {
			Desktop dt = Desktop.getDesktop();
			dt.open(document);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void updatePasswordField() {
		if (this.uField_1.getSelectedItem() != null) {
			UserPasswordInformation info = usernames.get(this.uField_1
					.getSelectedItem().toString());
			if (info != null) {
				if (info.isHash) {
					this.pField_1.setText("");
					this.remembercheck.setSelected(false);
				} else {
					this.pField_1.setText(info.password);
					this.remembercheck.setSelected(true);
				}
			}
		}
	}

	public void doLogin() {
		doLogin(uField_1.getSelectedItem().toString(),
				new String(pField_1.getPassword()), false);
	}

	public void doLogin(final String user, final String pass) {
		doLogin(user, pass, true);
	}

	public void doLogin(final String user, final String pass,
			final boolean cmdLine) {
		if (user == null || user.isEmpty() || pass == null || pass.isEmpty()) {
			return;
		}
		this.loginButton.setEnabled(false);

		this.modsButton.setEnabled(true);
		this.loginSkin1.setEnabled(false);

		options.setVisible(false);
		SwingWorker<Boolean, Boolean> loginThread = new SwingWorker<Boolean, Boolean>() {

			@Override
			protected Boolean doInBackground() {
				progressBar.setVisible(true);
				progressBar.setString("Connecting to www.minecraft.net...");
				String password = pass.toString();
				try {
					values = MinecraftUtils.doLogin(user, pass, progressBar);
					return true;
				} catch (AccountMigratedException e) {
					JOptionPane.showMessageDialog(getParent(),
							"Account migrated, use e-mail as username");
					this.cancel(true);
					progressBar.setVisible(false);
				} catch (BadLoginException e) {
					JOptionPane
							.showMessageDialog(getParent(),
									"Incorrect usernameField/passwordField combination");
					this.cancel(true);
					progressBar.setVisible(false);
				} catch (MinecraftUserNotPremiumException e) {
					JOptionPane.showMessageDialog(getParent(),
							"You purchase a minecraft account to play");
					this.cancel(true);
					progressBar.setVisible(false);
				} catch (MCNetworkException e) {
					UserPasswordInformation info = null;

					for (String username : usernames.keySet()) {
						if (username.equalsIgnoreCase(user)) {
							info = usernames.get(username);
							break;
						}
					}

					boolean authFailed = (info == null);

					if (!authFailed) {
						if (info.isHash) {
							try {
								MessageDigest digest = MessageDigest
										.getInstance("SHA-256");
								byte[] hash = digest.digest(pass.getBytes());
								for (int i = 0; i < hash.length; i++) {
									if (hash[i] != info.passwordHash[i]) {
										authFailed = true;
										break;
									}
								}
							} catch (NoSuchAlgorithmException ex) {
								authFailed = true;
							}
						} else {
							authFailed = !(password.equals(info.password));
						}
					}

					if (authFailed) {
						JOptionPane
								.showMessageDialog(getParent(),
										"Unable to authenticate account with minecraft.net");
					} else {
						int result = JOptionPane.showConfirmDialog(getParent(),
								"Would you like to run in offline mode?",
								"Unable to Connect to Minecraft.net",
								JOptionPane.YES_NO_OPTION);
						if (result == JOptionPane.YES_OPTION) {
							values = new String[] { "0", "0", user, "0" };
							return true;
						}
					}
					this.cancel(true);
					progressBar.setVisible(false);
				} catch (OutdatedMCLauncherException e) {
					JOptionPane.showMessageDialog(getParent(),
							"Incompatible Login Version.");
					progressBar.setVisible(false);
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
					this.cancel(true);
					progressBar.setVisible(false);
				} catch (Exception e) {
					e.printStackTrace();
				}
				enableUI();
				this.cancel(true);
				return false;
			}

			@Override
			protected void done() {
				if (values == null || values.length < 4) {
					return;
				}
				MainPane.pass = pass;
				String profileName = values[2].toString();

				MessageDigest digest = null;

				try {
					digest = MessageDigest.getInstance("SHA-256");
				} catch (NoSuchAlgorithmException e) {
				}

				gameUpdater.user = uField_1.getSelectedItem().toString(); // values[2].trim();
				gameUpdater.downloadTicket = values[1].trim();
				if (!cmdLine) {
					String password = new String(pField_1.getPassword());
					if (remembercheck.isSelected()) {
						usernames.put(gameUpdater.user,
								new UserPasswordInformation(password,
										profileName));
					} else {
						if (digest == null) {
							usernames.put(gameUpdater.user,
									new UserPasswordInformation(""));
						} else {
							usernames.put(
									gameUpdater.user,
									new UserPasswordInformation(digest
											.digest(password.getBytes())));
						}
					}
					writeUsernameList();
				}

				SwingWorker<Boolean, String> updateThread = new SwingWorker<Boolean, String>() {

					@Override
					protected Boolean doInBackground() throws Exception {
						publish("Checking for Minecraft Update...\n");
						try {
							mcUpdate = gameUpdater.checkMCUpdate();
						} catch (Exception e) {
							e.printStackTrace();
						}

						publish("Checking for Spoutcraft update...\n");
						try {
							spoutUpdate = gameUpdater
									.isSpoutcraftUpdateAvailable();
						} catch (Exception e) {
							e.printStackTrace();
						}

						publish(String.format("Checking for %s update...\n",
								ModPackListYML.currentModPackLabel));
						try {
							modpackUpdate = gameUpdater
									.isModpackUpdateAvailable();
						} catch (Exception e) {
							e.printStackTrace();
						}
						return true;
					}

					@Override
					protected void done() {
						if (modpackUpdate) {
							updateDialog
									.setToUpdate(ModPackListYML.currentModPackLabel);
						} else if (spoutUpdate) {
							updateDialog.setToUpdate("Spoutcraft");
						} else if (mcUpdate) {
							updateDialog.setToUpdate("Minecraft");
						}
						if (mcUpdate || spoutUpdate || modpackUpdate) {
							if (!GameUpdater.binDir.exists() || mcUpdate) {
								updateThread();
							} else {
								MainPane.updateDialog.setVisible(true);
							}
						} else {
							runGame();
						}
						this.cancel(true);
					}

					@Override
					protected void process(List<String> chunks) {
						progressBar.setString(chunks.get(0));
					}
				};
				updateThread.execute();
				this.cancel(true);
			}
		};
		loginThread.execute();
	}

	public void updateThread() {
		SwingWorker<Boolean, String> updateThread = new SwingWorker<Boolean, String>() {

			boolean error = false;

			@Override
			protected void done() {
				progressBar.setVisible(false);
				// FileUtils.cleanDirectory(GameUpdater.tempDir);
				if (!error) {
					runGame();
				}
				this.cancel(true);
			}

			@Override
			protected Boolean doInBackground() throws Exception {
				try {
					if (mcUpdate) {
						gameUpdater.updateMC();
					}
					if (spoutUpdate) {
						gameUpdater.updateSpoutcraft();
					}
					if (modpackUpdate) {
						gameUpdater.updateModPackMods();
					}
				} catch (NoMirrorsAvailableException e) {
					JOptionPane
							.showMessageDialog(getParent(),
									"No Mirrors Are Available to download from!\nTry again later.");
				} catch (Exception e) {
					e.printStackTrace();
					JOptionPane
							.showMessageDialog(getParent(), "Update Failed!");
					error = true;
					enableUI();
					this.cancel(true);
					return false;
				}
				return true;
			}

			@Override
			protected void process(List<String> chunks) {
				progressBar.setString(chunks.get(0));
			}
		};
		updateThread.execute();
	}

	public void enableUI() {
		loginButton.setEnabled(true);

		modsButton.setEnabled(true);
		loginSkin1.setEnabled(true);
	}

	private Cipher getCipher(int mode, String password) throws Exception {
		Random random = new Random(43287234L);
		byte[] salt = new byte[8];
		random.nextBytes(salt);
		PBEParameterSpec pbeParamSpec = new PBEParameterSpec(salt, 5);

		SecretKey pbeKey = SecretKeyFactory.getInstance("PBEWithMD5AndDES")
				.generateSecret(new PBEKeySpec(password.toCharArray()));
		Cipher cipher = Cipher.getInstance("PBEWithMD5AndDES");
		cipher.init(mode, pbeKey, pbeParamSpec);
		return cipher;
	}

	public void runGame() {
		if (ModPackListYML.currentModPack.equals("infinitewepons")) {
			File temp = new File(GameUpdater.modsDir,
					"industrialcraft-2-client_1.64.jar");
			if (temp.exists())
				temp.delete();
		}

		LauncherFrame launcher = new LauncherFrame();
		launcher.setLoginForm(this);
		int result = (Main.isOffline) ? launcher
				.runGame(null, null, null, null) : launcher.runGame(
				values[2].trim(), values[3].trim(), values[1].trim(), pass);
		if (result == LauncherFrame.SUCCESSFUL_LAUNCH) {
			MainPane.updateDialog.dispose();
			MainPane.updateDialog = null;
			setVisible(false);
			Main.loginForm = null;

			dispose();
		} else if (result == LauncherFrame.ERROR_IN_LAUNCH) {
			loginButton.setEnabled(true);

			modsButton.setEnabled(true);
			loginSkin1.setEnabled(true);

			progressBar.setVisible(false);
		}

		this.success = result;
		// Do nothing for retrying launch
	}

	@Override
	public void windowOpened(WindowEvent e) {
		tumblerFeed = new TumblerFeedParsingWorker(editorPane_2);
	    tumblerFeed.execute();

	}

	@Override
	public void windowClosing(WindowEvent e) {
	}

	@Override
	public void windowClosed(WindowEvent e) {
		if (success == LauncherFrame.ERROR_IN_LAUNCH) {
			Util.log("Exiting the Technic Launcher");
			System.exit(0);
		}
	}

	@Override
	public void windowIconified(WindowEvent e) {
	}

	@Override
	public void windowDeiconified(WindowEvent e) {
	}

	@Override
	public void windowActivated(WindowEvent e) {
	}

	@Override
	public void windowDeactivated(WindowEvent e) {
	}

	private static final class UserPasswordInformation {

		public boolean isHash;
		public byte[] passwordHash = null;
		public String password = null;
		private String profileName = "";

		public UserPasswordInformation(String pass, String profileName) {
			this(pass);
			this.setProfileName(profileName);
		}

		public UserPasswordInformation(String pass) {
			isHash = false;
			password = pass;
		}

		public UserPasswordInformation(byte[] hash) {
			isHash = true;
			passwordHash = hash;
		}

		public Boolean hasProfileName() {
			if (getProfileName().equals("")) {
				return false;
			}
			return true;
		}

		/**
		 * @return the profileName
		 */
		public String getProfileName() {
			return profileName;
		}

		/**
		 * @param profileName
		 *            the profileName to set
		 */
		public void setProfileName(String profileName) {
			this.profileName = profileName;
		}
	}
}