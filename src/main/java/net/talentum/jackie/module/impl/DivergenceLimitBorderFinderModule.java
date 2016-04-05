package net.talentum.jackie.module.impl;

import java.awt.Point;

import org.apache.commons.lang3.tuple.ImmutablePair;

import net.talentum.jackie.module.BorderFinderModule;
import net.talentum.jackie.robot.MomentData;

public class DivergenceLimitBorderFinderModule implements BorderFinderModule {

	int movedst;
	int tresholdWidthMax;
	int tresholdWidthMin;
	double divergenceFactor;

	/**
	 * @param movedst
	 * @param tresholdWidthMax
	 *            max width of detected trail
	 * @param tresholdWidthMin
	 *            min width of detected trail
	 * @param divergenceFactor
	 *            max divergence from expected border (R,L independently)
	 */
	public DivergenceLimitBorderFinderModule(int movedst, int tresholdWidthMax, int tresholdWidthMin, double divergenceFactor) {
		this.movedst = movedst;
		this.tresholdWidthMax = tresholdWidthMax;
		this.tresholdWidthMin = tresholdWidthMin;
		this.divergenceFactor = divergenceFactor;
	}

	@Override
	public ImmutablePair<Point, Point> findBorders(MomentData d, Point expected, double direction) {
		// get perpendicular line
		double perpAngle = d.perpendicularAngle(direction);

		// search for borders
		Point l = d.findBorder(expected, perpAngle, movedst, -1);
		Point r = d.findBorder(expected, perpAngle, movedst, 1);

		double maxDivergence = d.mTrailBordersMonitor.getTrailWidth() * divergenceFactor;
		Point lastL = d.mTrailBordersMonitor.getBorderL();
		Point lastR = d.mTrailBordersMonitor.getBorderR();
		if (lastL != null && lastR != null && (d.dst(l, lastL) > maxDivergence || d.dst(r, lastR) > maxDivergence)) {
			return null;
		}

		double dst = d.dst(l, r);
		if (dst <= tresholdWidthMax && dst >= tresholdWidthMin) {
			return new ImmutablePair<Point, Point>(l, r);
		}

		return null;
	}

}
