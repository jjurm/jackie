package net.talentum.jackie.module.impl;

import java.awt.Point;

import net.talentum.jackie.module.LineStartFinderModule;
import net.talentum.jackie.robot.MomentData;
import net.talentum.jackie.robot.Situation;
import net.talentum.jackie.system.Config;

public class BottomLineStartFinderModule implements LineStartFinderModule {

	@Override
	public Situation findLineStart(MomentData d) {

		Point p = d.findLinearlyNearestPoint(new Point(d.image.getWidth() / 2, d.image.getHeight() - 1), 0, true,
				Config.movedst, d.image.getWidth() / 2);
		if (p == null)
			return null;

		Point l = d.findBorder(p, 0, 1, -1);
		Point r = d.findBorder(p, 0, 1, 1);
		p = d.avg(l, r);
		double direction = -Math.PI / 2;

		d.position = p;
		d.line.add(p);
		d.bordersL.add(l);
		d.bordersR.add(r);

		return new Situation(p, direction);

	}

}
