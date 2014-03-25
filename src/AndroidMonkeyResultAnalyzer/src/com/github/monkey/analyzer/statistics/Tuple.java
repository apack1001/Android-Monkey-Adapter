package com.github.monkey.analyzer.statistics;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import com.github.monkey.analyzer.model.Abnormality;

/**
 * This class is a tuple, it will classify some similar abnormalities to one
 * {@link Tuple} class
 * 
 * @author Alex Chen (apack1001@gmail.com)
 * TODO name and responsibility need to upgrade
 * 
 */
public class Tuple {

	/**
	 * first field
	 */
	public CrashAbnormalityAdapter crashAbnormalities = null;

	/**
	 * second field which stores all the similar abnormalities of the same class
	 */
	public ArrayList<WeakReference<Abnormality>> abnormalities = new ArrayList<WeakReference<Abnormality>>();
}
