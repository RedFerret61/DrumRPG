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

import java.awt.Color;


/**
 * A class for returning MIDI Drum Map data.
 *
 * @author Paul A.W. Davies
 */
public class Drum {

	/**
	 * Returns the String to be used to represent a drum note
	 * @param pitch MIDI note 35-81
	 * @return String The equivalent drum 
	 */
	public static String getDrumMapString(int pitch) {
		switch (pitch) {
		case 35:
			return "ACOUSTIC_BASS_DRUM";
		case 36:
			return "BASS_DRUM_1";
		case 37:
			return "SIDE_STICK";
		case 38:
			return "ACOUSTIC_SNARE";
		case 39:
			return "HAND_CLAP";
		case 40:
			return "ELECTRIC_SNARE";
		case 41:
			return "LOW_FLOOR_TOM";
		case 42:
			return "CLOSED_HI_HAT";
		case 43:
			return "HIGH_FLOOR_TOM";
		case 44:
			return "PEDAL_HI_HAT";
		case 45 :
			return "LOW_TOM";
		case 46:
			return "OPEN_HI_HAT";
		case 47:
			return "LOW_MID_TOM";
		case 48:
			return "HI_MID_TOM";
		case 49:
			return "CRASH_CYMBAL_1";
		case 50:
			return "HIGH_TOM";
		case 51:
			return "RIDE_CYMBAL_1";
		case 52:
			return "CHINESE_CYMBAL";
		case 53:
			return "RIDE_BELL";
		case 54:
			return "TAMBOURINE";
		case 55:
			return "SPLASH_CYMBAL";
		case 56:
			return "COWBELL";
		case 57:
			return "CRASH_CYMBAL_2";
		case 58:
			return "VIBRASLAP";
		case 59:
			return "RIDE_CYMBAL_2";
		case 60:
			return "HI_BONGO";
		case 61:
			return "LOW_BONGO";
		case 62:
			return "MUTE_HI_CONGA";
		case 63:
			return "OPEN_HI_CONGA";
		case 64:
			return "LOW_CONGA";
		case 65:
			return "HIGH_TIMBALE";
		case 66:
			return "LOW_TIMBALE";
		case 67:
			return "HIGH_AGOGO";
		case 68:
			return "LOW_AGOGO";
		case 69:
			return "CABASA";
		case 70:
			return "MARACAS";
		case 71:
			return "SHORT_WHISTLE";
		case 72:
			return "LONG_WHISTLE";
		case 73:
			return "SHORT_GUIRO";
		case 74:
			return "LONG_GUIRO";
		case 75:
			return "CLAVES";
		case 76:
			return "HI_WOOD_BLOCK";
		case 77:
			return "LOW_WOOD_BLOCK";
		case 78:
			return "MUTE_CUICA";
		case 79:
			return "OPEN_CUICA";
		case 80:
			return "MUTE_TRIANGLE";
		case 81:
			return "OPEN_TRIANGLE";

		default:
			return "NOT_A_GENERAL_MIDI_DRUM";

		}

	}

	/**
	 * Returns the color to be used to represent a drum
	 * @param pitch note 35-81
	 * @return Color equivalent color
	 */
	public static java.awt.Color getDrumColor(int pitch) {
		switch (pitch) {
		case 35:
			// "ACOUSTIC_BASS_DRUM";
			return Color.black;
		case 36:
			// "BASS_DRUM_1";
			return Color.black;
		case 37:
			// "SIDE_STICK";
			return Color.red;
		case 38:
			// "ACOUSTIC_SNARE";
			return Color.red;
		case 39:
			// "HAND_CLAP";
			return Color.red;
		case 40:
			// "ELECTRIC_SNARE";
			return Color.red;
		case 41:
			// "LOW_FLOOR_TOM";
			return Color.orange;
		case 42:
			// "CLOSED_HI_HAT";
			return Color.lightGray;
		case 43:
			// "HIGH_FLOOR_TOM";
			return Color.orange;
		case 44:
			// "PEDAL_HI_HAT";
			return Color.lightGray;
		case 45 :
			// "LOW_TOM";
			return Color.orange;
		case 46:
			// "OPEN_HI_HAT";
			return Color.darkGray;
		case 47:
			// "LOW_MID_TOM";
			return Color.orange;
		case 48:
			// "HI_MID_TOM";
			return Color.orange;
		case 49:
			// "CRASH_CYMBAL_1";
			return Color.yellow;
		case 50:
			// "HIGH_TOM";
			return Color.orange;
		case 51:
			// "RIDE_CYMBAL_1";
			return Color.yellow;
		case 52:
			// "CHINESE_CYMBAL";
			return Color.yellow;
		case 53:
			// "RIDE_BELL";
			return Color.yellow;
		case 54:
			// "TAMBOURINE";
			return Color.yellow;
		case 55:
			// "SPLASH_CYMBAL";
			return Color.yellow;
		case 56:
			// "COWBELL";
			return Color.yellow;
		case 57:
			// "CRASH_CYMBAL_2";
			return Color.yellow;
		case 58:
			// "VIBRASLAP";
			return Color.yellow;
		case 59:
			// "RIDE_CYMBAL_2";
			return Color.yellow;
		case 60:
			// "HI_BONGO";
			return Color.blue;
		case 61:
			// "LOW_BONGO";
			return Color.blue;
		case 62:
			// "MUTE_HI_CONGA";
			return Color.blue;
		case 63:
			// "OPEN_HI_CONGA";
			return Color.blue;
		case 64:
			// "LOW_CONGA";
			return Color.blue;
		case 65:
			// "HIGH_TIMBALE";
			return Color.blue;
		case 66:
			// "LOW_TIMBALE";
			return Color.blue;
		case 67:
			// "HIGH_AGOGO";
			return Color.magenta;
		case 68:
			// "LOW_AGOGO";
			return Color.magenta;
		case 69:
			// "CABASA";
			return Color.pink;
		case 70:
			// "MARACAS";
			return Color.pink;
		case 71:
			// "SHORT_WHISTLE";
			return Color.magenta;
		case 72:
			// "LONG_WHISTLE";
			return Color.magenta;
		case 73:
			// "SHORT_GUIRO";
			return Color.green;
		case 74:
			// "LONG_GUIRO";
			return Color.green;
		case 75:
			// "CLAVES";
			return Color.green;
		case 76:
			// "HI_WOOD_BLOCK";
			return Color.green;
		case 77:
			// "LOW_WOOD_BLOCK";
			return Color.green;
		case 78:
			// "MUTE_CUICA";
			return Color.blue;
		case 79:
			// "OPEN_CUICA";
			return Color.blue;
		case 80:
			// "MUTE_TRIANGLE";
			return Color.yellow;
		case 81:
			// "OPEN_TRIANGLE";
			return Color.yellow;
		default:
			// "NOT_A_GENERAL_MIDI_DRUM";
			return Color.cyan;
		}

	}
}
