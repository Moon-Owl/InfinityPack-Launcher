package org.spoutcraft.launcher.gui.widget;

import java.awt.Component;
import java.awt.Dimension;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import org.spoutcraft.launcher.modpacks.ModPackListYML;

public class ComboBoxRenderer extends JLabel implements ListCellRenderer {
	private static final long serialVersionUID = 3596084337758726795L;

	public ComboBoxRenderer() {
		setOpaque(true);
		setHorizontalAlignment(CENTER);
		setVerticalAlignment(CENTER);
	}

	/*
	 * This method finds the image and text corresponding to the selected value
	 * and returns the label, set up to display the text and image.
	 */
	@Override
	public Component getListCellRendererComponent(JList list, Object value,
			int index, boolean isSelected, boolean cellHasFocus) {
		// Get the selected index. (The index param isn't
		// always valid, so just use the value.)
		String selectedItem = (String) value;

		if (isSelected) {
			setBackground(list.getSelectionBackground());
			setForeground(list.getSelectionForeground());
		} else {
			setBackground(list.getBackground());
			setForeground(list.getForeground());
		}

		// Set the icon and text. If icon was null, say so.
		ImageIcon icon = ModPackListYML.getModPackLogo(selectedItem);
		setIcon(icon);
		if (icon != null) {
			this.setPreferredSize(new Dimension(0, icon.getIconHeight() + 4));
		}

		return this;
	}
}