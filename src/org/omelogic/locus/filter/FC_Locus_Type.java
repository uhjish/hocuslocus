package org.omelogic.locus.filter;

import org.omelogic.locus.Locus;

/**
A FilterCriteria implementation for Locus.type

@author	Chris Zaleski
*/

public class FC_Locus_Type implements FilterCriteria
{
	String typeCriteria;

	public FC_Locus_Type(String type)
	{
		typeCriteria = type;
	}

	public boolean passes(Locus locus)
	{
		if(locus.getType().equals(typeCriteria))
			return true;
		else
			return false;
	}
}
