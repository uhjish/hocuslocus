
package org.omelogic.utils;

public class BadNucleotideSequenceException extends Exception
{
	public static final long serialVersionUID = 1L;
	
	public BadNucleotideSequenceException(String gripe)
	{
		super(gripe);
	}
}
