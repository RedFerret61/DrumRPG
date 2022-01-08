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

import javax.swing.*;

import java.awt.event.MouseEvent;
import java.awt.geom.*;
import java.awt.Dimension;
import java.awt.Graphics;
import java.util.*;
import java.awt.Color;

/**
 * A Swing class to produce a graph plot.
 * 
 * @author Paul A.W. Davies
 */
@SuppressWarnings("serial")
public class DensityPlot extends JPanel {

	int panelWidth = 375; // the new width of this component in pixels
	int panelHeight = 150; // the new height of this component in pixels

	private List data = new ArrayList();

	private float scaleFactorX = 0;
	private float scaleFactorY = 0;
	private float plotOriginX = 0;
	private float plotOriginY = 0;
	final float rangeXValues = 100; // Default range of x values
	float minimumY = 127;
	float maximumY = 0;
	float borderX = 15;
	float borderY = 10;
	float rangeY = maximumY - minimumY; // Range of y values

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.JComponent#getToolTipText(java.awt.event.MouseEvent)
	 */
	public String getToolTipText(MouseEvent event) {
		int xPos = event.getX(); // The x coord pointed to by the user.
		int yPos = event.getY(); // The y coord pointed to by the user.

		double xPlot = ((xPos - plotOriginX) / scaleFactorX); // The equivalent
		// original data
		// point
		double yPlot = ((yPos - plotOriginY) / -scaleFactorY);
		boolean valueFoundInData = false;

		for (Iterator i = data.iterator(); i.hasNext() && !valueFoundInData;) {
			Point2D.Float dataPoint = (Point2D.Float) i.next();
			// The tooltip coords are worked out by doing the opposite operation
			// from this line:
			// .cf g.fillOval((int)(pt.x * scaleX + originX), (int)(pt.y* scaleY
			// + originY), 2, 2);
			float tollerance = 4; // tooltip selection tollerance
			if ((dataPoint.x - xPlot) >= (-tollerance / scaleFactorX)
					&& (dataPoint.x - xPlot) <= (tollerance / scaleFactorX)
					&& (dataPoint.y - yPlot) >= (-tollerance / scaleFactorY)
					&& (dataPoint.y - yPlot) <= (tollerance / scaleFactorY)) {
				xPlot = dataPoint.x; // We want the actual coordinate, not the
				yPlot = dataPoint.y; // coord worked out from where we pointed
				// to
				valueFoundInData = true; // in case it is inaccurate.
			}
		}

		// Return tooltip text (blank if not pointing to a point)
		return valueFoundInData ? "Density: " + (int) xPlot + "% , GM Drum: "
				+ (int) yPlot + " " + Drum.getDrumMapString((int) yPlot) : "";

	}

	/**
	 * Constructs a new DensityPlot
	 */
	public DensityPlot() {

		setBorder(BorderFactory.createLineBorder(Color.black));

		setSize(panelWidth, panelHeight);

		this.setToolTipText(""); // Required for showing tooltips.

		int width = getWidth();
		int height = getHeight();
		setBounds(20, 20, width, height);
		setVisible(true);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.JComponent#getPreferredSize()
	 */
	public Dimension getPreferredSize() {
		return new Dimension(panelWidth, panelHeight);
	}

	protected void paintComponent(Graphics g) {
		int x, y;

		super.paintComponent(g);

		int width = getWidth();
		int height = getHeight();
		scaleFactorX = (width - (borderX * 2)) / rangeXValues; // Number of
																// pixels per
																// unit x
		scaleFactorY = (height - (borderY * 2)) / (rangeY); // Number of pixels
															// per unit
		// y
		// plotOriginX = 0; // Actual panel position of x-coord of origin
		plotOriginX = 0 + borderX;
		plotOriginY = height + (minimumY * scaleFactorY) - borderY; // Assuming
		// no
		// negative
		// y values,
		// to invert
		// the y
		// axis as
		// draw
		// world
		// origin is
		// top left,
		// plus the
		// scaled
		// minimum Y
		// value and
		// border
		// offset

		for (Iterator i = data.iterator(); i.hasNext();) {
			Point2D.Float pt = (Point2D.Float) i.next();

			g.setColor(Drum.getDrumColor((int) pt.y));
			x = (int) (pt.x * scaleFactorX + plotOriginX);
			y = (int) (-pt.y * scaleFactorY + plotOriginY);
			g.fillOval(x, y, 4, 4);
		}
		// label graph

		g.setColor(Color.lightGray);

		g.drawString("Drum", (int) (plotOriginX + (borderX/2)),
				(int) ((height / 2) - borderY));
		g.drawString("Usage %", (width / 2), (int) (height - borderY));
	}

	/**
	 * Updates the points on the scatter plot.
	 * 
	 * @param newPoints
	 *            x y plot points
	 */
	public void setPoints(List newPoints) {
		this.data = newPoints;
		// find range of data
		for (Iterator i = data.iterator(); i.hasNext();) {
			Point2D.Float pt = (Point2D.Float) i.next();
			maximumY = Math.max(maximumY, pt.y);
			minimumY = Math.min(minimumY, pt.y);
		}
		rangeY = maximumY - minimumY;
		// System.out.println("minY = " + minimumY + " maxY = " + maximumY);
		repaint();
	}

}