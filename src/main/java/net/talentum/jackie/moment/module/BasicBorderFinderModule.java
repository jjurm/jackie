package net.talentum.jackie.moment.module;

import java.awt.Point;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import net.talentum.jackie.moment.MomentData;

public class BasicBorderFinderModule implements BorderFinderModule {

	int movedst;
	int tresholdWidthMax;
	int tresholdWidthMin;

	public BasicBorderFinderModule(int movedst, int tresholdWidthMax, int tresholdWidthMin) {
		this.movedst = movedst;
		this.tresholdWidthMax = tresholdWidthMax;
		this.tresholdWidthMin = tresholdWidthMin;
	}

	@Override
	public Pair<Point, Point> findBorders(MomentData d, Point expected, double direction) {
		// get perpendicular line
		double perpAngle = d.perpendicularAngle(direction);

		// search for borders
		Point l = d.findBorder(expected, perpAngle, movedst, -1);
		Point r = d.findBorder(expected, perpAngle, movedst, 1);

		double maxDivergence = d.mTrailBordersMonitor.getTrailWidth() / 2;
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
