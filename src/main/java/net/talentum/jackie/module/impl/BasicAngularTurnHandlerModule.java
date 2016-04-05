package net.talentum.jackie.module.impl;

import java.awt.Point;

import net.talentum.jackie.module.AngularTurnHandlerModule;
import net.talentum.jackie.robot.MomentData;
import net.talentum.jackie.robot.Situation;

public class BasicAngularTurnHandlerModule implements AngularTurnHandlerModule {

	@Override
	public Situation detectAndProceed(MomentData d, Point expected, double direction) {
		// get perpendicular line
		double perpAngle = d.perpendicularAngle(direction);

		// get average width of last n points
		double avgwidth = d.mTrailBordersMonitor.getTrailWidth();

		Point pointR1 = d.move(expected, perpAngle, avgwidth * 1.5);
		Point pointR2 = d.move(pointR1, direction, d.param.movedst * 2);
		Point pointL1 = d.move(expected, perpAngle, avgwidth * -1.5);
		Point pointL2 = d.move(pointL1, direction, d.param.movedst * 2);

		if (!(d.inBounds(pointR1) && d.inBounds(pointL1) && d.inBounds(pointR2) && d.inBounds(pointL2)))
			return null;

		int trailR = (d.isTrailPoint(pointR1) ? 1 : 0) + (d.isTrailPoint(pointR2) ? 1 : 0);
		int trailL = (d.isTrailPoint(pointL1) ? 1 : 0) + (d.isTrailPoint(pointL2) ? 1 : 0);

		if (trailR - trailL == 2 || trailL - trailR == 2) {
			int orientation = trailR - 1;
			Point point1 = d.move(expected, perpAngle, avgwidth * orientation);
			Point point2 = d.move(expected, perpAngle, avgwidth * orientation * 1.5);
			d.notFound.add(pointR1);
			d.notFound.add(pointL1);
			d.notFound.add(pointR2);
			d.notFound.add(pointL2);

			if (!d.inBounds(point1) || !d.inBounds(point2))
				return null;

			int mdst = Math.max(1, d.param.movedst / 2);
			Point point1Border = d.findLinearlyNearestPoint(point1, direction, !d.isTrailPoint(point1), mdst,
					(int) avgwidth);
			Point point2Border = d.findLinearlyNearestPoint(point2, direction, !d.isTrailPoint(point2), mdst,
					(int) (avgwidth * 2));

			if (point1Border == null || point2Border == null) {
				return null;
			}

			double newDirection = Math.atan2(point2Border.y - point1Border.y, point2Border.x - point1Border.x);
			d.position = d.move(point2, d.perpendicularAngle(newDirection), ((int) (avgwidth / 2)) * orientation * -1);
			d.highlight.add(d.move(expected, direction, (int) (avgwidth / 2)));

			Situation s = new Situation(d.position, newDirection);
			return s;
		}

		return null;
	}

}
