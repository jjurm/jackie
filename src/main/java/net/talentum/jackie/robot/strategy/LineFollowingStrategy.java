package net.talentum.jackie.robot.strategy;

import net.talentum.jackie.module.BooleanImageFilterModule;
import net.talentum.jackie.module.DirectionManagerModule;
import net.talentum.jackie.module.ImageModifierModule;
import net.talentum.jackie.module.LineFinderModule;
import net.talentum.jackie.module.LineStartFinderModule;
import net.talentum.jackie.module.ModuleSupplier;
import net.talentum.jackie.module.TrailBordersMonitorModule;
import net.talentum.jackie.robot.Parameters;
import net.talentum.jackie.robot.RobotInstruction;
import net.talentum.jackie.robot.Situation;

/**
 * Intelligent strategy for recognizing line. The output contains every
 * recognized line point, in form of a polyline. This strategy is capable of
 * handling also spaced lines and angular turns.
 * 
 * @author JJurM
 */
public class LineFollowingStrategy extends RobotStrategy {

	ImageModifierModule mImageModifier;
	BooleanImageFilterModule mBooleanImageFilter;
	LineStartFinderModule mLineStartFinder;
	ModuleSupplier<TrailBordersMonitorModule> msTrailBordersMonitor;
	ModuleSupplier<DirectionManagerModule> msDirectionManager;
	LineFinderModule mLineFinder;

	public LineFollowingStrategy(Parameters param, ImageModifierModule mImageModifier,
			BooleanImageFilterModule mBooleanImageFilter, LineStartFinderModule mLineStartFinder,
			ModuleSupplier<TrailBordersMonitorModule> msTrailBordersMonitor,
			ModuleSupplier<DirectionManagerModule> msDirectionManager, LineFinderModule mLineFinder) {
		super(param);
		this.mImageModifier = mImageModifier;
		this.mBooleanImageFilter = mBooleanImageFilter;
		this.mLineStartFinder = mLineStartFinder;
		this.msDirectionManager = msDirectionManager;
		this.msTrailBordersMonitor = msTrailBordersMonitor;
		this.mLineFinder = mLineFinder;
	}

	@Override
	public RobotInstruction evaluate() {
		// process image
		if (mImageModifier != null)
			d.image = mImageModifier.modify(d.image);

		// create boolean array
		d.bw = mBooleanImageFilter.filter(d.image);

		// setup TrailWidthDeterminerModule
		d.mTrailBordersMonitor = msTrailBordersMonitor.create(d);

		// find line start
		Situation lineStart = mLineStartFinder.findLineStart(d);
		if (lineStart == null)
			return new RobotInstruction(d.m, d);

		// setup DirectionManagerModule
		d.mDirectionManager = msDirectionManager.create(d);
		d.mDirectionManager.overwriteAll(lineStart.getPoint(), lineStart.getDirection(), 1, d);

		// recognize line
		boolean res = true;
		for (int i = 0; res && i < 2000; i++) {
			res = mLineFinder.findNext(d);
		}

		// result
		RobotInstruction instruction = new RobotInstruction(d.m, d);
		return instruction;
	}

}
