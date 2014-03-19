package org.omelogic.locus;

import java.util.ArrayList;

/**
A class representing a set of Genomic sequences not associated with Loci data.

@author	Frank Doyle
*/
public class SequenceSet {
	private ArrayList<String> sequences;
	private String setName;
	
	public SequenceSet(String name){
		setName = name;
		sequences = new ArrayList<String>();
	}
	
	public ArrayList getSequences(){
		return sequences;
	}
	
	public void addSequence(String seq){
		sequences.add(seq);
	}
	
}
