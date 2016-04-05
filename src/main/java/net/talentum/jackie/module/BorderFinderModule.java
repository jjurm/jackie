package net.talentum.jackie.module;

import java.awt.Point;

import org.apache.commons.lang3.tuple.ImmutablePair;

import net.talentum.jackie.robot.MomentData;

/**
 * Module interface that search for borders, given the expected trail point and
 * computed trail direction.
 * 
 * @author JJurM
 */
public interface BorderFinderModule {

	public ImmutablePair<Point, Point> findBorders(MomentData d, Point expected, double direction);

}
