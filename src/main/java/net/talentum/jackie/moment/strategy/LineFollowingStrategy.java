package net.talentum.jackie.moment.strategy;

import net.talentum.jackie.moment.Parameters;
import net.talentum.jackie.moment.RobotInstruction;
import net.talentum.jackie.moment.Situation;
import net.talentum.jackie.moment.module.BooleanImageFilterModule;
import net.talentum.jackie.moment.module.DirectionManagerModule;
import net.talentum.jackie.moment.module.ImageModifierModule;
import net.talentum.jackie.moment.module.LineFinderModule;
import net.talentum.jackie.moment.module.LineStartFinderModule;
import net.talentum.jackie.moment.module.ModuleSupplier;
import net.talentum.jackie.moment.module.TrailBordersMonitorModule;

public class LineFollowingStrategy extends RobotStrategy {

	ImageModifierModule mImageModifier;
	BooleanImageFilterModule mBooleanImageFilter;
	LineStartFinderModule mLineStartFinder;
	ModuleSupplier<TrailBordersMonitorModule> msTrailBordersMonitor;
	ModuleSupplier<DirectionManagerModule> msDirectionManager;
	LineFinderModule mLineFinder;

	public LineFollowingStrategy(String name, Parameters param, ImageModifierModule mImageModifier,
			BooleanImageFilterModule mBooleanImageFilter, LineStartFinderModule mLineStartFinder,
			ModuleSupplier<TrailBordersMonitorModule> msTrailBordersMonitor,
			ModuleSupplier<DirectionManagerModule> msDirectionManager, LineFinderModule mLineFinder) {
		super(name, param);
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
