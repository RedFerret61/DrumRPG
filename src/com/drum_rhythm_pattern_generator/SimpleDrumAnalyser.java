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

//import jm.constants.RhythmValues.*;
import jm.JMC;
import jm.music.data.*; //import jm.music.tools.PhraseAnalysis;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Random; //import java.io.IOException;
import java.util.List;

/**
 * This class does simple threshold analysis on a drum score.
 * 
 * @author Paul A.W. Davies
 */

public final class SimpleDrumAnalyser implements JMC {
	static int highNote = 0;
	static int lowNote = 127;
	static double longestRhythm = 0.0;
	static double shortestRhythm = 1000.0;

	static int shortLength = 1000000;
	static int longLength = 0;
	static int ascending = 0;
	static int descending = 0;
	static int prev = 60;

	static int scoreCount = 0;
	static int noteCount = 0;
	static int gridLength = 0;
	static double barSize = 0.0;

	private static int numerator, denominator = 0;
	private static double gridStepDuration = 0.0;
	private static double numBars = 0.0; // number of bars of analysed drum
											// beats

	private static int beatThreshold, beatThreshold2 = 0;
	private static String thresholdVariant;

	private static int percFreq[] = new int[(int) jm.music.data.Note.MAX_MIDI_PITCH];
	private static int gridPercFreq[][];
	private static int gridPercMinDynamic[][];
	private static int gridPercMaxDynamic[][];

	private static Random r = new Random();

	/**
	 * Adds a Drum Score to the SimpleDrumAnalyser
	 * 
	 * @param s
	 *            drum score to add
	 * @param sdag
	 *            to view the data
	 */
	public void addScore(Score s, SimpleDrumAnalyserPresenter sdag) {

		// assumes one time signature per score

		// validate the score has some notes in it

		numerator = s.getNumerator();
		denominator = s.getDenominator();
		// System.out.println("SimpleDrumAnalysis time signature " + numerator +
		// "/"
		// + denominator);
		if (numerator < 1 || denominator < 1) {
			System.err.println("jMusic time signature error " + numerator + " "
					+ denominator);
			System.exit(1);
		}

		gridLength = numerator * denominator; // e.g. for 4/4 the gridLength is
		// 16

		barSize = numerator * jm.constants.RhythmValues.WHOLE_NOTE
				/ denominator;

		gridStepDuration = barSize / gridLength; // may not be right for 2/2?

		if (scoreCount == 0) {

			// Initialise values for first score
			gridPercFreq = new int[(int) jm.music.data.Note.MAX_MIDI_PITCH][gridLength];
			gridPercMinDynamic = new int[(int) jm.music.data.Note.MAX_MIDI_PITCH][gridLength];
			gridPercMaxDynamic = new int[(int) jm.music.data.Note.MAX_MIDI_PITCH][gridLength];
			// initialize arrays
			for (int row = 0; row < gridPercMinDynamic.length; row++) {
				for (int col = 0; col < gridPercMinDynamic[row].length; col++) {
					gridPercMinDynamic[row][col] = Note.MAX_DYNAMIC;
					gridPercMaxDynamic[row][col] = Note.MIN_DYNAMIC;
				}
			}

		}

		// keep track of the number of scores analysed
		scoreCount++;
		// reset melody length
		int tempLength = 0;
		double beatCounter = 0.0; // count current beat
		double beatLength = 0.0;

		// iterate through each note
		// find the highest and lowest notes
		Enumeration enum1 = s.getPartList().elements();
		while (enum1.hasMoreElements()) {
			Part nextPt = (Part) enum1.nextElement();
			Enumeration enum2 = nextPt.getPhraseList().elements();
			while (enum2.hasMoreElements()) {
				Phrase nextPhr = (Phrase) enum2.nextElement();
				// find longest phrase in score
				if (nextPhr.getBeatLength() > beatLength)
					beatLength = nextPhr.getBeatLength();

				// sum rhythm values
				// double myRhythmValueCount = 0.0;
				// myRhythmValueCount =
				// PhraseAnalysis.rhythmValueCount(nextPhr);
				// System.out
				// .println("myRhythmValueCount = " + myRhythmValueCount);
				// waitForEnter();

				Enumeration enum3 = nextPhr.getNoteList().elements();
				while (enum3.hasMoreElements()) {
					Note nextNote = (Note) enum3.nextElement();
					// debug why more notes in phase than analysed
					// it is because typically "notes" come in pairs, the second
					// one having a negative pitch and a zero dynamic
					// nextNote.toString = jMusic NOTE: [Pitch = 46][RhythmValue
					// = 0.1][Dynamic = 115][Pan = 0.5][Duration = 0.1]
					// nextNote.toString = jMusic NOTE: [Pitch =
					// -2147483648][RhythmValue = 0.8999999999999773][Dynamic =
					// 0][Pan = 9.0][Duration = 0.8999999999999773]

					// System.out.println("nextNote.toString = " + nextNote);

					int pitch = nextNote.getPitch();
					// check range
					pitchRange(pitch);

					// calculate grid index
					double barPosition = getBarPosition(beatCounter);

					// use Round to ensure 11.9 becomes 12 instead of 11
					int gridIndex = (int) Math
							.round((barPosition / barSize * gridLength));
					// bounds check and wrap gridIndex
					if (gridIndex < 0 || gridIndex >= gridLength)
						gridIndex = 0;

					// System.out.println("BarPosition = " + barPosition
					// + " gridIndex = " + gridIndex);

					// update frequency stats
					addPitchFreq(pitch, gridIndex);

					int dynamic = nextNote.getDynamic();
					addDynamicFreq(pitch, gridIndex, dynamic);

					// keep track of the number of notes analysed
					noteCount++;

					double rv = nextNote.getRhythmValue();
					beatCounter += rv;

					// check rhythmic values
					rhythmRange(rv);
					// check melody length
					tempLength++;
					// check direction
					upOrDown(pitch);
				}
			}
		}

		// update number of bars analysed
		numBars = numBars + (beatLength / numerator);
		// // System.out.println("Number of bars analysed is " + numBars);
		// update length extremes
		musicLength(tempLength);

		// print(percFreq);

		// waitForEnter();

		// printGrid(gridPercFreq);

		// convert data ready for plotting

		// update scatter plot

		// List points = new ArrayList();
		List points;

		points = getDensityPitchPoints();

		sdag.densityPlot.setPoints(points);
	}

	/**
	 * Returns number of scores analysed.
	 * 
	 * @return the number of scores analysed.
	 */
	public int getScoreCount() {
		return scoreCount;
	}

	private void pitchRange(int pitch) {
		// check the range of the score
		// General MIDI Percussion Key Map is 35 Bass Drum 2 ... 81 Open
		// Triangle
		if (pitch > 0 && pitch < 128) // sanity check pitch range
		{
			if (pitch < lowNote)
				lowNote = pitch;
			if (pitch > highNote)
				highNote = pitch;
		}
	}

	// update the frequency of pitch occurrences
	private void addPitchFreq(int pitch, int gridIndex) {
		// check the range of the score
		// General MIDI Percussion Key Map is 35 Bass Drum 2 ... 81 Open
		// Triangle
		if (pitch >= 0 && pitch < 128) // sanity check pitch range
		{
			percFreq[pitch] = percFreq[pitch] + 1;
			gridPercFreq[pitch][gridIndex] = gridPercFreq[pitch][gridIndex] + 1;
		}
	}

	// update the dynamic range of pitch occurrences
	private void addDynamicFreq(int pitch, int gridIndex, int dynamic) {
		// check pitch and dynamic are valid
		if ((pitch >= 0 && pitch < 128) && (dynamic >= Note.MIN_DYNAMIC)
				&& (dynamic <= Note.MAX_DYNAMIC)) // sanity check pitch range
		{
			if (dynamic < gridPercMinDynamic[pitch][gridIndex])
				gridPercMinDynamic[pitch][gridIndex] = dynamic;
			if (dynamic > gridPercMaxDynamic[pitch][gridIndex])
				gridPercMaxDynamic[pitch][gridIndex] = dynamic;
		}
	}

	/**
	 * clear data
	 */
	public void restart() {
		scoreCount = 0;
		clearData();

	}

	private void rhythmRange(double rv) {
		// check the range of the MIDI file
		if (rv < shortestRhythm)
			shortestRhythm = rv;
		if (rv > longestRhythm)
			longestRhythm = rv;
	}

	private void musicLength(int temp) {
		if (temp < shortLength)
			shortLength = temp;
		if (temp > longLength)
			longLength = temp;
	}

	private void upOrDown(int pitch) {
		if (pitch < prev)
			descending++;
		if (pitch > prev)
			ascending++;
		prev = pitch;
	}

	/*
	 * private static void waitForEnter() { //
	 * System.out.println("waitForEnter bypassed ..."); //
	 * System.out.println("Press Return to continue ..."); try { int ch; while
	 * ((ch = System.in.read()) != -1 && ch != '\r') { ; } } catch (IOException
	 * ex) { } }
	 */

	/**
	 * Retrieve the note's position in the bar as an double.
	 * 
	 * @return double note's bar position
	 */
	private double getBarPosition(double beatCounter) {

		// jMusic is based on a beat pulse where one beat is a value of 1.0 and
		// all other rhythms are relative to that.
		// The beat has several constants that equal it: CROTCHET, C,
		// QUARTER_NOTE, and QN.

		// rhythm values (rv) or "beats" per bar where
		// bar size (in rhythm values (rv)) = ( numerator / denominator ) *
		// WHOLE_NOTE
		// where WHOLE_NOTE = 4.0
		// vast majority of popular music is in 4/4. ( bar size = 4.0 )
		// Some exceptions are "Money" by Pink Floyd, in 7/4 (bar size = 7.0 )
		// and the original Mission Impossible Theme, in 5/4 (bar size = 5.0)
		// 2/4 means there are two beats per bar and each is a quarter note.
		// (bar size = 2.0)
		// 12 / 8 means twelve eighth notes per bar (bar size = 6.0)
		// 2/2 means 2 beats per bar and each is a half note (bar size = 2.0)

		return beatCounter % barSize;
	}

	/**
	 * Print the array in standard output
	 * 
	 * @param a
	 *            2d array of note frequencies per bar step
	 */
	/*
	 * private static void print(int a[]) { int i = 0; int totalNotes = 0; for
	 * (i = 0; i < a.length; i++) { if (a[i] > 0) { //
	 * System.out.println("Note " + i + " Freq " + a[i]+ " " +
	 * Drum.getDrumMapString(i) ); totalNotes = totalNotes + a[i]; } } //
	 * System.out.println("totalNotes " + totalNotes); }
	 */

	/**
	 * Print the array in standard output
	 * 
	 * @param grid
	 *            2d array of note frequencies per bar step
	 */
	/*
	 * private static void printGrid(int grid[][]) {
	 * 
	 * for (int i = 0; i < grid.length; i++) {
	 * 
	 * for (int j = 0; j < grid[i].length; j++) { if (grid[i][j] != 0)
	 * System.out.println("Note " + i + " Step " + j + " Freq " + grid[i][j] +
	 * " " + Drum.getDrumMapString(i)); }
	 * 
	 * } }
	 */

	/**
	 * Returns a generated drum pattern (that may have tempo set and then
	 * played)
	 * 
	 * @param sdag
	 *            display to get parameters from
	 * @return a new score object
	 */
	public Score generateDrumPattern(SimpleDrumAnalyserPresenter sdag) {

		gatherData(sdag);

		// pack the phrase into a part, no instrument , on channel 10
		Part part = new Part("Drums", NO_INSTRUMENT, 9);

		// for each non-zero probability drum, generate a phrase.
		for (int i = 0; i < percFreq.length; i++) {
			if (percFreq[i] > 0) {
				// generate a phrase
				part.addPhrase(generateDrumPhrase(i));
			}
		}

		// pack the part into a score
		Score score = new Score("Drum");

		score.setNumerator(numerator);
		score.setDenominator(denominator);
		score.addPart(part);

		return score;
	}

	/**
	 * generate a drum pattern using an existing score object (that may be
	 * currently playing)
	 * 
	 * @param sdag
	 *            display to get parameters from
	 * @param score
	 *            containing generated drum pattern
	 */
	public void generateDrumPattern(SimpleDrumAnalyserPresenter sdag,
			Score score) {

		gatherData(sdag);

		// pack the phrase into a part, no instrument , on channel 10
		Part part = new Part("Drums", NO_INSTRUMENT, 9);

		// for each non-zero probability drum, generate a phrase.
		for (int i = 0; i < percFreq.length; i++) {
			if (percFreq[i] > 0) {
				// generate a phrase
				part.addPhrase(generateDrumPhrase(i));
			}
		}

		// remove any parts from existing score
		score.removeAllParts();
		score.setNumerator(numerator);
		score.setDenominator(denominator);
		// pack the part into a score
		score.addPart(part);

	}

	// Generate a Drum Phrase for the provided pitch
	private static Phrase generateDrumPhrase(int pitch) {

		int[] pitchArray = new int[gridLength];
		double[] rhythmArray = new double[gridLength];
		int[] dynamic = new int[gridLength];
		boolean chosen = false;

		Phrase myPhrase = new Phrase(0.0); // start this phrase on the first
											// beat

		// A rest (silence) is considered a special case of pitch in jMusic. The
		// constant REST in place of a pitch value will indicate that a 'note'
		// with no sound (i.e., a rest) should be created.
		for (int j = 0; j < gridPercFreq[pitch].length; j++) {
			// initially set default "rest" note at this step
			pitchArray[j] = REST;
			// set the duration of one grid step
			rhythmArray[j] = gridStepDuration;
			// initially set the dynamic to off (0 = off, 1 = quiet, 127=loud)
			dynamic[j] = Note.MIN_DYNAMIC;
			chosen = false;

			// determine whether to randomly add a drum note at this step
			if (gridPercFreq[pitch][j] != 0) {

				// ("Generate Note " + pitch + " Step " + j + " Freq "
				// + gridPercFreq[pitch][j] + " " + DrumMapString(pitch));

				// Each grid step probability is number found/number of bars in
				// the song
				// e.g 93 found / 93 bars = 100% probability.

				if (thresholdVariant == SimpleDrumAnalyserPresenter.aboveString) {
					if (gridPercFreq[pitch][j] > (int) (numBars * beatThreshold / 100.0))
						chosen = true;
				} else if (thresholdVariant == SimpleDrumAnalyserPresenter.belowString) {
					if (gridPercFreq[pitch][j] <= Math.round(numBars
							* beatThreshold / 100.0))
						chosen = true;
				} else if (thresholdVariant == SimpleDrumAnalyserPresenter.insideString) {
					if ((gridPercFreq[pitch][j] > (int) (numBars
							* beatThreshold / 100.0))
							&& (gridPercFreq[pitch][j] <= (int) (numBars
									* (beatThreshold2 + 1) / 100.0)))
						chosen = true;
				} else if (thresholdVariant == SimpleDrumAnalyserPresenter.outsideString) {
					if ((gridPercFreq[pitch][j] < (int) (numBars
							* beatThreshold / 100.0))
							|| (gridPercFreq[pitch][j] > (int) (numBars
									* beatThreshold2 / 100.0)))
						chosen = true;
				}

				if (chosen) {
					pitchArray[j] = pitch;
					dynamic[j] = pickNumberInRange(
							gridPercMinDynamic[pitch][j],
							gridPercMaxDynamic[pitch][j]);
				}
				//
			}
		}

		myPhrase.addNoteList(pitchArray, rhythmArray, dynamic);
		return myPhrase;
	}

	/**
	 * Generate a single random integer between aLowerLimit and aUpperLimit,
	 * inclusive.
	 * 
	 * assumes private static Random r = new Random();
	 * 
	 * @exception IllegalArgumentException
	 *                if aLowerLimit is greater than aUpperLimit.
	 */
	private static int pickNumberInRange(int aLowerLimit, int aUpperLimit) {
		if (aLowerLimit > aUpperLimit) {
			StringBuilder message = new StringBuilder();
			message.append("Lower limit (");
			message.append(aLowerLimit);
			message.append(") must not be greater than Upper limit (");
			message.append(aUpperLimit);
			message.append(")");
			throw new IllegalArgumentException(message.toString());
		}

		if (aLowerLimit == aUpperLimit) {
			return aLowerLimit;
		}

		// get the range, casting to long to avoid overflow problems
		long range = (long) aUpperLimit - (long) aLowerLimit + 1;
		// compute a fraction of the range, 0 <= frac < range
		long fraction = (long) (range * r.nextDouble());
		return (int) (fraction + aLowerLimit);
	}

	private void clearData() {
		// clear arrays
		gridPercFreq = null;

		gridPercMinDynamic = null;

		gridPercMaxDynamic = null;
		// clear primitives
		highNote = 0;
		lowNote = 127;
		longestRhythm = 0.0;
		shortestRhythm = 1000.0;
		shortLength = 1000000;
		longLength = 0;
		ascending = 0;
		descending = 0;
		prev = 60;
		scoreCount = 0;
		noteCount = 0;
		gridLength = 0;
		barSize = 0.0;
		numerator = 0;
		denominator = 0;
		gridStepDuration = 0.0;
		numBars = 0.0;
		beatThreshold = 0;
		beatThreshold2 = 0;

	}

	private void gatherData(SimpleDrumAnalyserPresenter sdag) {

		beatThreshold = sdag.beatThresholdSlider.getValue();
		beatThreshold2 = sdag.beatThresholdSlider2.getValue();

		thresholdVariant = sdag.thresholdChoiceGroup.getSelection()
				.getActionCommand();

	}

	@SuppressWarnings("unchecked")
	private List getDensityPitchPoints() {
		List points = new ArrayList();

		// for each non-zero probability drum, generate a point.
		for (int pitch = 0; pitch < percFreq.length; pitch++) {
			if (percFreq[pitch] > 0) {

				for (int gridStep = 0; gridStep < gridPercFreq[pitch].length; gridStep++) {

					// for each non-zero gridStep generate a point
					if (gridPercFreq[pitch][gridStep] != 0) {
						points
								.add(new Point2D.Float(
										(int) (gridPercFreq[pitch][gridStep]
												/ numBars * 100.0), pitch));
					}
				}
			}
		}
		return points;
	}

}
