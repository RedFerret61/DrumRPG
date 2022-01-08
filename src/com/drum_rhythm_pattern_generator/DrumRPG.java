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

import jm.JMC;
import jm.music.data.*;
import jm.util.*;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;


/* icons from
 * http://www.kde-look.org/content/show.php/realistiK+Reloaded?content=52362
 * License: GPL
 */
/**
 * A class which reads in MIDI files and extracts aspects of them to generate
 * new drum rhythm patterns.
 * 
 * @author Paul A.W. Davies
 */

public final class DrumRPG implements JMC, ActionListener, ChangeListener {

	private JFrame f;
	private JTabbedPane tabPane;
	private JButton loadButton, playButton, stopButton, saveButton;
	private JButton restartButton;
	private JLabel timeSigLabel;
	private JList list;
	private DefaultListModel listModel;

	private JSlider tempoSlider;
	private JLabel tempoValue;
	private JLabel tempoLabel;

	private static double scoreTempo = Score.DEFAULT_TEMPO * 2; // 60.0 * 2 =
	// 120 BPM
	private static Score theScore;
	private static Score newScore;
	private static Score drumScore = new Score();
	private static SimpleDrumAnalyser sda = new SimpleDrumAnalyser();
	private static SimpleDrumAnalyserPresenter sdag;
	private static int initialScoreDenominator, initialScoreNumerator = 4;
	// read a MIDI file
	FileDialog fd;
	String fi = "";

	/**
	 * Main method where all good Java programs start
	 */
	public static void main(String[] args) {

		new DrumRPG();
	}

	/**
	 * Constructs a new Drum Rhythm Pattern Generator
	 */
	public DrumRPG() {

		// create frame
		f = new JFrame("DrumRPG (Drum Rhythm Pattern Generator) 0.01");
		f.setIconImage(getFDImage()); // get the icon from a file
		f.setResizable(false);

		// ensure exit without leaving java process running
		f.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				// avoid problem on windows (SoundMAX) of MIDI becoming "locked"
				// silent on next run
				// Play.waitCycle(drumScore);
				Play.stopMidi();
				System.exit(0);
			}
		});

		Box mainBox = new Box(BoxLayout.Y_AXIS);

		// set upper area GUI
		Box topBox = new Box(BoxLayout.Y_AXIS);
		mainBox.add(topBox);
		Box buttonBox = new Box(BoxLayout.X_AXIS);

		JPanel loadPanel = new JPanel();
		// loadButton = new JButton("Load");
		loadButton = new JButton("Load", new ImageIcon(DrumRPG.class
				.getResource("images/load.png")));
		loadButton.setMargin(new Insets(0, 0, 0, 0));
		loadButton.setBorderPainted(false);
		loadButton.setToolTipText("<html>"
				+ "Load a (type 1-multiple track) MIDI file" + "<br>"
				+ "including a track on channel 10 with drums." + "<br>"
				+ "After loading, click Load again to merge another file"
				+ "<br>"
				+ "with previously loaded file(s) for more novel results."
				+ "<br>"
				+ "Use MIDI files recorded with a tempo, not “freely”."
				+ "</html>");

		loadButton.addActionListener(this);
		loadPanel.add(loadButton);
		buttonBox.add(loadPanel);

		JPanel playPanel = new JPanel();
		// playButton = new JButton("Play");
		playButton = new JButton("Play", new ImageIcon(DrumRPG.class
				.getResource("images/play.png")));
		playButton.setMargin(new Insets(0, 0, 0, 0));
		playButton.setBorderPainted(false);
		playButton
				.setToolTipText("<html>"
						+ "Play a drum pattern generated using the current settings." + "<br>"
						+ "Wait a bar and repeat." 
						+ "</html>");

		playButton.addActionListener(this);
		playPanel.add(playButton);
		buttonBox.add(playPanel);

		JPanel stopPanel = new JPanel();

		// stopButton = new JButton("Stop");
		stopButton = new JButton("Stop", new ImageIcon(DrumRPG.class
				.getResource("images/stop.png")));
		stopButton.setMargin(new Insets(0, 0, 0, 0));
		stopButton.setBorderPainted(false);
		stopButton.setToolTipText("Stop playing the drum pattern.");

		stopButton.addActionListener(this);
		stopPanel.add(stopButton);

		stopButton.setEnabled(false);
		buttonBox.add(stopPanel);

		JPanel savePanel = new JPanel();
		// saveButton = new JButton("Save");
		saveButton = new JButton("Save", new ImageIcon(DrumRPG.class
				.getResource("images/save.png")));
		saveButton.setMargin(new Insets(0, 0, 0, 0));
		saveButton.setBorderPainted(false);
		saveButton.setToolTipText("<html>"
				+ "Save a MIDI file containing the drum pattern" + "<br>"
				+ "generated using the current settings." + "</html>");

		saveButton.addActionListener(this);
		savePanel.add(saveButton);
		buttonBox.add(savePanel);

		// restart
		JPanel restartPanel = new JPanel();
		// restartButton = new JButton("restart");
		restartButton = new JButton("restart", new ImageIcon(DrumRPG.class
				.getResource("images/restart.png")));
		restartButton.setMargin(new Insets(0, 0, 0, 0));
		restartButton.setBorderPainted(false);
		restartButton.setToolTipText("<html>" + "Restart program." + "<br>"
				+ "Clear all loaded MIDI data." + "</html>");

		restartButton.addActionListener(this);
		restartPanel.add(restartButton);
		buttonBox.add(restartPanel);

		// add all buttons to topBox
		topBox.add(buttonBox);

		// infoBox
		Box infoBox = new Box(BoxLayout.Y_AXIS);

		
		// timeSigLabel
		Box timeSigPanel = new Box(BoxLayout.Y_AXIS);
		timeSigPanel.add(new JPanel()); // padding
		timeSigLabel = new JLabel("Time Signature: ");
		timeSigPanel.add(timeSigLabel);
		infoBox.add(timeSigPanel);
		
		// Tempo slider

		Box tempoPanel = new Box(BoxLayout.X_AXIS);
		tempoPanel.add(new JPanel()); // padding
		tempoLabel = new JLabel("Tempo:");
		tempoPanel.add(tempoLabel);

		tempoSlider = new JSlider(SwingConstants.HORIZONTAL, 40, 250, 120);
		tempoSlider.addChangeListener(this);
		tempoSlider.setMajorTickSpacing(20);
		tempoSlider.setPaintTicks(true);
		//tempoSlider.setPreferredSize(new Dimension(120, 25));
		tempoPanel.add(tempoSlider);
		tempoValue = new JLabel("120 bpm");
		//tempoValue.setPreferredSize(new Dimension(70, 25));
		tempoPanel.add(tempoValue);
		infoBox.add(tempoPanel);


		// loaded list
		listModel = new DefaultListModel();

		// Create the list and put it in a scroll pane.
		list = new JList(listModel);
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		list.setSelectedIndex(0);
		list.setVisibleRowCount(3);
		JScrollPane listScrollPane = new JScrollPane(list);
		listScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		JPanel listPanel = new JPanel();
	    Border listPanelBorder =
	        BorderFactory.createTitledBorder("Loaded");
	      listPanel.setBorder(listPanelBorder);

		listPanel.add(listScrollPane, BorderLayout.WEST);
		infoBox.add(listPanel);

	

		topBox.add(infoBox);

		// tab

		tabPane = new JTabbedPane();

		mainBox.add(tabPane);

		f.getContentPane().add(mainBox);

		// do Simple tab
		sdag = new SimpleDrumAnalyserPresenter(this);
		tabPane.addTab("Beat Thresholding", sdag);
		tabPane.setSelectedIndex(tabPane.getTabCount() - 1);

		f.pack();
		f.setVisible(true);
		load();

	}

	/**
	 * @return Returns an Image or null.
	 */
	protected static Image getFDImage() {
		java.net.URL imgURL = DrumRPG.class.getResource("images/DrumRPG.jpg");
		if (imgURL != null) {
			return new ImageIcon(imgURL).getImage();
		} else {
			return null;
		}
	}



	/**
	 * generates pattern based on current settings 
	 */
	public void generate() {

		sda.generateDrumPattern(sdag, drumScore);

	}
	

	/**
	 * loads a midi file, analyses and plays it
	 */
	private void load() {

		boolean drumsFound = false;

		// Score fillScore;

		int scoreNumerator = 4;
		int scoreDenominator = 4;

		// chose a file
		fd = new FileDialog(new Frame(), "choose a midi file...", fd.LOAD);
		fd.show();

		theScore = new Score("A Temporary score");
		Read.midi(theScore, fd.getDirectory() + fd.getFile());
		fi = fd.getFile();
		fd.dispose();
		fd = null;

		// determine tempo and sanity check
		// System.out.println("theScore.getTempo = " + theScore.getTempo());
		scoreTempo = theScore.getTempo(); // Returns the Score's tempo
		if (scoreTempo < 1.0 || scoreTempo > 300.0)
			scoreTempo = 120.0;
		// update tempo slider with value from score
		tempoSlider.setValue((int) scoreTempo);

		newScore = new Score("Drum Score");
		scoreNumerator = theScore.getNumerator();
		// System.out.println("theScore.numerator = " + scoreNumerator);
		newScore.setNumerator(scoreNumerator);
		// Returns the denominator of the Phrase's time signature
		scoreDenominator = theScore.getDenominator();
		// System.out.println("theScore.denominator = " + scoreDenominator);
		newScore.setDenominator(scoreDenominator);

		if (sda.getScoreCount() == 0) {
			// remember first Time Signature used as subsequent ones must be the
			// same
			initialScoreNumerator = scoreNumerator;
			initialScoreDenominator = scoreDenominator;
			// display time sig
			timeSigLabel.setText("Time Signature: " + initialScoreNumerator
					+ "/" + initialScoreDenominator);
		}

		if (sda.getScoreCount() > 0
				&& ((initialScoreNumerator != scoreNumerator) || (initialScoreDenominator != scoreDenominator))) {
			// invalid as not first time and Time Sig not identical
			System.out
					.println("Subsequent Time Signatures must match that of first midi file.");
			// custom title, error icon
			JOptionPane.showMessageDialog(null, "Midi File Time Signature "
					+ scoreNumerator + "/" + scoreDenominator
					+ " not same as first Time Signature "
					+ initialScoreNumerator + "/" + initialScoreDenominator,
					"DPG Load error", JOptionPane.ERROR_MESSAGE);

		} else // valid Time Signature
		{
			for (int s = 0; s < theScore.getSize(); s++) {

				// get the drum part from it
				Part drums = theScore.getPart(s);

				if (drums.getChannel() == 9) { // add it to the new score
					drumsFound = true;
					newScore.addPart(drums.copy());
					// get the all phrases in this part as a array

					//Phrase[] phrases = drums.getPhraseArray();

					// System.out.println("phrases.length = " + phrases.length);
					// for (int j = 0; j < phrases.length; j++) {
						// Return the Duration of the phrase in beats.
						// double beatLength = phrases[j].getBeatLength();
						// System.out.println("beatLength = " + beatLength);
						// Returns the numerator of the Phrase's time signature
						// int numerator = phrases[j].getNumerator();
						// // System.out.println("numerator = " + numerator);
						// Returns the denominator of the Phrase's time
						// signature
						// int denominator = phrases[j].getDenominator();
						// // System.out.println("denominator = " + denominator);
						// Returns the all notes in the phrase as a array of
						// notes
						// Note[] notes = phrases[j].getNoteArray();
					//}

					// looping more succinctly
					/*
					for (int i = 0; i < drums.size(); i++) {
						for (int j = 0; j < drums.getPhrase(i).size(); j++) {
							// // System.out.println("Note " +
							// drums.getPhrase(i).getNote(j).toString());
						}
					}
					*/

				}
			}

			if (!drumsFound) {
				// invalid as no drums found in midi file
				// System.out.println("No drums found in midi file: " + fi);
				// custom title, error icon
				JOptionPane
						.showMessageDialog(
								null,
								"No drums found in midi file: "
										+ fi
										+ "\n"
										+ "\n"
										+ "Load a (type 1) MIDI file including a track on channel 10 with drums."
										+ "\n"
										+ "Type 0 MIDI files can be converted using other software e.g."
										+ "\n"
										+ "\n"
										+ "midifilemapper: Browse to a map including:"
										+ "\n"
										+ "<NoteMap Name=\"Pass Channel 10 Notes\" InChannel=\"10\" InNote=\"*\" OutNote=\"*\" />"
										+ "\n"
										+ "Select MIDI File Type: Convert to type 1"
										+ "\n"
										+ "\n"
										+ "DrumTrack: File > Import MIDI ... File > Export to MIDI"
										+ "\n"
										+ "\n"
										+ "Cubase LE: File > Import MIDI File ..., "
										+ "\n"
										+ "    on MIDI part, right click > Midi > Dissolve Part to Separate Channels,"
										+ "\n"
										+ "    File > Export > MIDI file type 1. ",
								"DPG Load file error",
								JOptionPane.ERROR_MESSAGE);
			} else // drumsFound
			{
				// perform analysis on drums found
				sda.addScore(newScore, sdag);

				// generate a drum pattern from the analysis
				drumScore = sda.generateDrumPattern(sdag);
				// add filename to list
				listModel.addElement(fi);

			}
		}
	} // end of load

	// "restart" program
	private void restart() {
		// clear loaded data
		sda.restart();
		// remove all items from loaded file list
		listModel.removeAllElements();
		// Prompt user to load a file
		load();

	}

	/*
	 * Handles the button presses.
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent e) {

		if (e.getSource() == loadButton) {
			load();
		}

		if (e.getSource() == playButton) {

			// Generate before play
			drumScore = sda.generateDrumPattern(sdag);
			// set the tempo according to the slider
			drumScore.setTempo(tempoSlider.getValue());

			// Play.midi(drumScore, false); // play once
			// Play.waitCycle(drumScore); // Thread.sleeps for a period of 1 #
			// stops interaction
			Play.midiCycle(drumScore); // Continually repeat-plays a Score
			// object (loops but with a blank bar
			// between repeats).
			loadButton.setEnabled(false);
			playButton.setEnabled(false);
			stopButton.setEnabled(true);
			restartButton.setEnabled(false);
			tempoSlider.setEnabled(false);
			tempoValue.setEnabled(false);
			tempoLabel.setEnabled(false);
		}
		if (e.getSource() == stopButton) {
			// Play.stopMidi();
			Play.stopCycle(); // to stop the playback loop.
			loadButton.setEnabled(true);
			playButton.setEnabled(true);
			stopButton.setEnabled(false);
			restartButton.setEnabled(true);
			tempoSlider.setEnabled(true);
			tempoValue.setEnabled(true);
			tempoLabel.setEnabled(true);
		}
		if (e.getSource() == saveButton) {
			Write.midi(drumScore);
		}

		if (e.getSource() == restartButton) {
			restart();
		}

	}

	/*
	 * Handles the slider moves.
	 * @see javax.swing.event.ChangeListener#stateChanged(javax.swing.event.ChangeEvent)
	 */
	public void stateChanged(ChangeEvent e) {
		if (e.getSource() == tempoSlider)
		{
			if ((int)tempoSlider.getValue()<100)
			tempoValue.setText("" + "0" + tempoSlider.getValue() + " bpm");
			else 
				tempoValue.setText("" + tempoSlider.getValue() + " bpm");
		}
	}

}
