/*

 <This Java Class is part of DrumRPG version 0.1, March 2009.>

 Copyright (C) 2008,2009 Paul A. W. Davies

 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU Lesser General Public
 License as published by the Free Software Foundation; either
 version 2.1 of the License, or (at your option) any later version.

 This library is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 Lesser General Public License for more details.

 You should have received a copy of the GNU Lesser General Public
 License along with this library; if not, write to the Free Software
 Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA

 Note: The LGPL licence can be found online at http://www.gnu.org

 */
package com.drum_rhythm_pattern_generator;

import jm.JMC; //import jm.music.data.*;
//import jm.util.*;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

/**
 * A Swing Component for displaying SimpleDrumAnalyser data.
 * 
 * @author Paul A.W. Davies
 */

@SuppressWarnings("serial")
public class SimpleDrumAnalyserPresenter extends JPanel implements JMC,
		ChangeListener, ActionListener {

	public ButtonGroup thresholdChoiceGroup;

	public JSlider beatThresholdSlider;
	// private JLabel beatThresholdValue;

	public JSlider beatThresholdSlider2;
	// private JLabel beatThresholdValue2;

	static String aboveString = "Above";
	static String belowString = "Below";
	static String insideString = "Inside";
	static String outsideString = "Outside";
	private static DrumRPG theDrumRPG;

	public DensityPlot densityPlot = new DensityPlot();

	public SimpleDrumAnalyserPresenter(DrumRPG myDrumRPG) {

		super();
		theDrumRPG = myDrumRPG;

		// this.part = p;
		Box partBox = new Box(BoxLayout.Y_AXIS);

		this.add(partBox);
		// Overall settings

		JPanel overallPanel = new JPanel();

		Box makeBox = new Box(BoxLayout.X_AXIS);
		Box transportBox = new Box(BoxLayout.Y_AXIS);
		transportBox.add(makeBox);
		Box beatThresholdPanel = new Box(BoxLayout.Y_AXIS);

		beatThresholdPanel.add(new JPanel()); // padding

		beatThresholdPanel.add(densityPlot);

		beatThresholdPanel.add(new JPanel()); // padding

		beatThresholdSlider = new JSlider(SwingConstants.HORIZONTAL, 0, 100, 25);
		beatThresholdSlider.setBorder(BorderFactory
				.createTitledBorder("Lower Threshold = 25 %"));

		beatThresholdSlider.addChangeListener(this);
		beatThresholdSlider.setMajorTickSpacing(10);
		beatThresholdSlider.setPaintTicks(true);
		beatThresholdSlider.setPaintLabels(true);

		beatThresholdPanel.add(new JPanel()); // padding
		beatThresholdPanel.add(beatThresholdSlider);

		beatThresholdPanel.add(new JPanel()); // padding

		beatThresholdSlider2 = new JSlider(SwingConstants.HORIZONTAL, 0, 100,
				100);
		beatThresholdSlider2.setBorder(BorderFactory
				.createTitledBorder("Upper Threshold = 100 %"));
		beatThresholdSlider2.addChangeListener(this);
		beatThresholdSlider2.setMajorTickSpacing(10);
		beatThresholdSlider2.setPaintTicks(true);
		beatThresholdSlider2.setPaintLabels(true);

		beatThresholdSlider2.setEnabled(true);

		beatThresholdPanel.add(beatThresholdSlider2);

		beatThresholdPanel.add(new JPanel()); // padding

		transportBox.add(beatThresholdPanel);

		overallPanel.add(transportBox);

		partBox.add(overallPanel);

		// Create the radio buttons.
		JRadioButton aboveButton = new JRadioButton(aboveString);
		aboveButton.setMnemonic(KeyEvent.VK_A);
		aboveButton.setActionCommand(aboveString);
		aboveButton.setToolTipText("<html>"
				+ "Individual drum beats in loaded files are chosen " + "<br>"
				+ "if their usage is greater than some threshold value."
				+ "<br>"
				+ "Finds the most used pattern from the loaded file(s),"
				+ "<br>" + "that is the main drum pattern." + "</html>");

		// aboveButton.setSelected(true);

		JRadioButton belowButton = new JRadioButton(belowString);
		belowButton.setMnemonic(KeyEvent.VK_B);
		belowButton.setActionCommand(belowString);
		belowButton.setToolTipText("<html>"
				+ "A variant which is the opposite of threshold above."
				+ "<br>"
				+ "Finds the least used pattern from the loaded file(s),"
				+ "<br>" + "that is the main fill pattern." + "</html>");

		JRadioButton insideButton = new JRadioButton(insideString);
		insideButton.setMnemonic(KeyEvent.VK_I);
		insideButton.setActionCommand(insideString);
		insideButton.setToolTipText("<html>"
				+ "A beat is chosen if its value is between two thresholds"
				+ "<br>"
				+ "Finds a middle-usage pattern from the loaded file(s),"
				+ "<br>" + "such as a variation on the main groove," + "<br>"
				+ "or fill pattern (for low values)." + "</html>");
		insideButton.setSelected(true);

		JRadioButton outsideButton = new JRadioButton(outsideString);
		outsideButton.setMnemonic(KeyEvent.VK_O);
		outsideButton.setActionCommand(outsideString);
		outsideButton.setToolTipText("<html>"
				+ "A variant which is the opposite of threshold inside."
				+ "<br>" + "Not very useful?" + "<br>"
				+ "Included for completeness." + "</html>");

		// Group the radio buttons.
		thresholdChoiceGroup = new ButtonGroup();
		thresholdChoiceGroup.add(aboveButton);
		thresholdChoiceGroup.add(belowButton);
		thresholdChoiceGroup.add(insideButton);
		thresholdChoiceGroup.add(outsideButton);

		// Register a listener for the radio buttons.

		aboveButton.addActionListener(this);
		belowButton.addActionListener(this);
		insideButton.addActionListener(this);
		outsideButton.addActionListener(this);

		// Put the radio buttons in a row in a panel.
		JPanel thresholdChoicePanel = new JPanel();

		thresholdChoicePanel.add(new JPanel()); // padding

		JLabel thresholdChoiceLabel = new JLabel("Choose:");
		thresholdChoicePanel.add(thresholdChoiceLabel);

		thresholdChoicePanel.add(aboveButton);
		thresholdChoicePanel.add(belowButton);
		thresholdChoicePanel.add(insideButton);
		thresholdChoicePanel.add(outsideButton);

		beatThresholdPanel.add(thresholdChoicePanel);

		// set border and add
		beatThresholdPanel
				.setBorder(BorderFactory
						.createTitledBorder("Drum Usage (0-100%) per step - Hover over a point for more."));
		partBox.add(beatThresholdPanel);

	}

	public void stateChanged(ChangeEvent e) {
		if (e.getSource() == beatThresholdSlider) {
			// ensure slider2 position always >= to slider1
			if (beatThresholdSlider2.getValue() < beatThresholdSlider
					.getValue()) {
				if (beatThresholdSlider.getValueIsAdjusting())
					return;
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						beatThresholdSlider2.setValue(beatThresholdSlider
								.getValue());

					}

				});

			}
			// beatThresholdValue.setText("" + beatThresholdSlider.getValue() +
			// " %  (Lower Threshold) ");
			beatThresholdSlider.setBorder(BorderFactory
					.createTitledBorder("Lower Threshold = "
							+ +beatThresholdSlider.getValue() + " % "));
			// generate drum pattern
			if (!beatThresholdSlider.getValueIsAdjusting())
				theDrumRPG.generate();
		}
		if (e.getSource() == beatThresholdSlider2) {
			// ensure slider2 position always >= to slider1
			if (beatThresholdSlider2.getValue() < beatThresholdSlider
					.getValue()) {
				if (beatThresholdSlider2.getValueIsAdjusting())
					return;
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						beatThresholdSlider2.setValue(beatThresholdSlider
								.getValue());

					}
				});
			}
			// beatThresholdValue2.setText("" + beatThresholdSlider2.getValue()
			// + " %  (Upper Threshold) ");
			beatThresholdSlider2.setBorder(BorderFactory
					.createTitledBorder("Upper Threshold = "
							+ +beatThresholdSlider2.getValue() + " % "));
			// generate drum pattern
			if (!beatThresholdSlider2.getValueIsAdjusting())
				theDrumRPG.generate();
		}
	}

	/** Listens to the radio buttons. */
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand() == aboveString
				|| e.getActionCommand() == belowString) {
			beatThresholdSlider2.setEnabled(false);
			beatThresholdSlider2.setBorder(BorderFactory
					.createTitledBorder(" "));

			// if an action is performed generate drum pattern
			theDrumRPG.generate();
		}
		if (e.getActionCommand() == insideString
				|| e.getActionCommand() == outsideString) {
			beatThresholdSlider2.setEnabled(true);
			beatThresholdSlider2.setBorder(BorderFactory
					.createTitledBorder("Upper Threshold = "
							+ +beatThresholdSlider2.getValue() + " % "));

			// if an action is performed generate drum pattern
			theDrumRPG.generate();
		}

	}

}
