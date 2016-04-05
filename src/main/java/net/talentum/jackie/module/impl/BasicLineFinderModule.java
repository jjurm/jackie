package net.talentum.jackie.module.impl;

import java.awt.Point;

import org.apache.commons.lang3.tuple.Pair;

import net.talentum.jackie.module.AngularTurnHandlerModule;
import net.talentum.jackie.module.BorderFinderModule;
import net.talentum.jackie.module.LineFinderModule;
import net.talentum.jackie.robot.MomentData;
import net.talentum.jackie.robot.Situation;

public class BasicLineFinderModule implements LineFinderModule {

	double findingAngle;
	BorderFinderModule mBorderFinder;
	AngularTurnHandlerModule mAngularTurnHandler;

	public BasicLineFinderModule(double findingAngle, BorderFinderModule mBorderFinder,
			AngularTurnHandlerModule mAngularTurnHandler) {
		this.findingAngle = findingAngle;
		this.mBorderFinder = mBorderFinder;
		this.mAngularTurnHandler = mAngularTurnHandler;
	}

	@Override
	public boolean findNext(MomentData d) {
		boolean ret = false;

		double direction = d.mDirectionManager.getDirection();

		// distance from last known point
		int r = (d.notFoundCount + 1) * d.param.movedst;

		Pair<Point, Point> borders = null;

		if (d.notFoundCount == 0) {
			Point expected = d.move(d.position, direction, r);

			// detect angular turn
			Situation s = mAngularTurnHandler.detectAndProceed(d, expected, direction);

			if (s != null) {
				// handle angular turn
				expected = s.getPoint();
				direction = s.getDirection();
				d.mDirectionManager.overwriteAll(s.getPoint(), s.getDirection(), 1, d);
			}
			borders = mBorderFinder.findBorders(d, expected, direction);
		} else {
			// get average width of last n points
			double avgwidth = d.mTrailBordersMonitor.getTrailWidth();
			
			// get number of points to check, and divide given angle
			int expectedCount = (int) Math.ceil(findingAngle * r / avgwidth * 2 / 3);
			double expectedAngle = (expectedCount == 0) ? 0 : (findingAngle / expectedCount);

			for (int i = 0; i < 2 * expectedCount + 1; i++) {
				double angle = (i % 2 == 1 ? 1 : -1) * expectedAngle * ((i + 1) / 2);
				Point expected = d.move(d.position, direction + angle, r);

				borders = mBorderFinder.findBorders(d, expected, direction + angle);

				if (borders == null) {
					d.notFound.add(expected);
				} else {
					break;
				}
			}
		}

		// check if borders were found
		if (borders == null) {
			d.notFoundCount++;
			return true;
		}

		// find next point
		Point next = d.avg(borders.getLeft(), borders.getRight());

		if (d.inBounds(next)) {
			// store points
			d.notFoundCount = 0;
			d.position = next;
			d.bordersL.add(borders.getLeft());
			d.bordersR.add(borders.getRight());
			d.mTrailBordersMonitor.registerBorders(borders.getLeft(), borders.getRight());
			d.line.add(next);
			d.directionList.add(direction);
			d.mDirectionManager.addLinePoint(next);

			return !ret;
		}
		return false;
	}

}
