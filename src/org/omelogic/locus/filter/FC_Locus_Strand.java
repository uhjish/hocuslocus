package org.omelogic.locus.filter;

import org.omelogic.locus.Locus;

/**
A FilterCriteria implementation for Locus.strand

@author	Chris Zaleski
*/

public class FC_Locus_Strand implements FilterCriteria
{
	int strandCriteria;

	public FC_Locus_Strand(int strand)
	{
		strandCriteria = strand;
	}

	public boolean passes(Locus locus)
	{
		if(locus.getStrand() == strandCriteria)
			return true;
		else
			return false;
	}
}
