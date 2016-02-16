package net.talentum.jackie.moment.module;

import java.awt.Point;

import org.apache.commons.lang3.tuple.Pair;

import net.talentum.jackie.moment.MomentData;

public interface BorderFinderModule {

	public Pair<Point, Point> findBorders(MomentData d, Point expected, double direction);

}
