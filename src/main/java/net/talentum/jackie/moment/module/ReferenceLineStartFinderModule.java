package net.talentum.jackie.moment.module;

import java.awt.Point;
import java.util.List;

import net.talentum.jackie.moment.MomentData;
import net.talentum.jackie.moment.RobotInstruction;
import net.talentum.jackie.moment.RobotInstructionRegister;
import net.talentum.jackie.moment.Situation;

/**
 * Uses computed data from previous {@code Moment}s.
 * 
 * @author JJurM
 */
public class ReferenceLineStartFinderModule implements LineStartFinderModule {

	private RobotInstructionRegister register;
	private LineStartFinderModule backupFinder;

	/**
	 * @param register
	 * @param backupFinder
	 *            finder to use when the register has history of zero length.
	 */
	public ReferenceLineStartFinderModule(RobotInstructionRegister register, LineStartFinderModule backupFinder) {
		this.register = register;
		this.backupFinder = backupFinder;
	}

	@Override
	public Situation findLineStart(MomentData d) {
		RobotInstruction prev = register.getLastInstruction();

		if (prev != null) {
			List<Point> line = prev.momentData.line;
			List<Double> directionList = prev.momentData.directionList;

			for (int i = 0; i < line.size() && i < directionList.size(); i += 5) {
				double direction = directionList.get(i) + Math.PI / 2;
				Point l = d.findBorder(line.get(i), direction, 1, -1);
				Point r = d.findBorder(line.get(i), direction, 1, 1);

				if (d.dst(l, r) < 100) {
					Point p = d.avg(l, r);
					d.position = p;
					d.line.add(p);
					d.bordersL.add(l);
					d.bordersR.add(r);
					
					return new Situation(p, direction);
				}
			}
			
		}
		
		return backupFinder.findLineStart(d);
	}

}
