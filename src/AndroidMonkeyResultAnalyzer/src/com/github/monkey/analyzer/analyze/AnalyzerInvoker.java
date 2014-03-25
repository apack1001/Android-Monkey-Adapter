package com.github.monkey.analyzer.analyze;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.github.monkey.analyzer.model.Abnormality;
import com.github.monkey.analyzer.model.InvalidAbnormality;

/**
 * 
 * @author Alex Chen (apack1001@gmail.com)
 *
 */
class AnalyzerInvoker {
	/**
	 * The context of all analyzers, it stores all the Commands
	 */
	protected List<IAnalyzer> analyzers = Collections
			.synchronizedList(new ArrayList<IAnalyzer>());
	/**
	 * The target monkey result file to be analyzed
	 */
	protected List<String> paths = Collections
			.synchronizedList(new ArrayList<String>());

	/**
	 * All the known results
	 */
	protected ArrayList<Abnormality> knownAbnormalities = new ArrayList<Abnormality>();

	/**
	 * All the unknown results
	 */
	protected ArrayList<Abnormality> unknownAbnormalties = new ArrayList<Abnormality>();

	/**
	 * Traverse the command iteration an
	 */
	public final void analyze() {
		if (analyzers.size() == 0 || paths.size() == 0)
			return;

		for (String path : paths) {
			// Matching! Add it to the known abnormalities
			boolean matching = false;
			for (IAnalyzer analyzer : analyzers) {
				analyzer.setPath(path);
				Abnormality abnormality = analyzer.toAbnormality();
				if (abnormality.isValid()) {
					knownAbnormalities.add(abnormality);
					matching = true;
					break;
				}
			}
			
			// Not matching! Add it to the unknown abnormalities
			if (false == matching) {
				Abnormality invalidAbnormality = null;
				try {
					invalidAbnormality = InvalidAbnormality.class.newInstance();
					if (analyzers.size() > 0)
						invalidAbnormality.copyFrom(analyzers.get(0)
								.toAbnormality().getExtras());
					
					if (invalidAbnormality != null)
						unknownAbnormalties.add(invalidAbnormality);
					
				} catch (InstantiationException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}

				
			}
			matching = false;
		}
	}

	final public void registerAnalyzer(IAnalyzer analyzer) {
		analyzers.add(analyzer);
	}

	/**
	 * add one monkey result path to be analyzed
	 * 
	 * @param path
	 *            monkey result directory parent path
	 */
	final public void addPath(String path) {
		if (path == null || path == "")
			throw new IllegalArgumentException("Invalid file path");

		paths.add(path);
	}

	/**
	 * Get all the known kind of abnormalities which are unprocessed.<br/>
	 * So duplicated result may exist<br/>
	 * 
	 * @return all the <b>known</b> kind of abnormalities
	 */
	final public ArrayList<Abnormality> getKnownAbnormalities() {
		return knownAbnormalities;
	}

	/**
	 * Get all the unknown kind of abnormalities.<br/>
	 * 
	 * @return all the <b>unknown</b> kind of abnormalities
	 */
	final public ArrayList<Abnormality> getUnknownAbnormalities() {
		return unknownAbnormalties;
	}
}
