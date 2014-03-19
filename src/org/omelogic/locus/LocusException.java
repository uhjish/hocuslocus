
package org.omelogic.locus;

/**
A class representing an Exception which can be thrown from the org.omelogic.locus package

@author	Chris Zaleski
*/

public class LocusException extends Exception
{
	public static final long serialVersionUID = 1L;
	/**
	Constructor
	@param	gripe	String representation of the Exception
	*/
	public LocusException(String gripe)
	{
		super(gripe);
	}
}
