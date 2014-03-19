package org.omelogic.locus.filter;

import org.omelogic.locus.Locus;

/**
Iterface for defining a filter criteria

@author	Chris Zaleski
*/

public interface FilterCriteria
{
	/**
	Validates a Locus object for the implemented criteria
	@param	locus the locus to be validated
	@return	returns true if the Locus passes the criteria, false otherwise
	*/
	public boolean passes(Locus locus);
}