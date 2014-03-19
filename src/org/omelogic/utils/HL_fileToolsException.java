package org.omelogic.utils;

/**
A class representing an Exception which can be thrown from the LS_fileTools class

@author	Chris Zaleski
*/

public class HL_fileToolsException extends Exception
{
	/**
	Constructor
	@param	gripe	String representation of the Exception
	*/
	public HL_fileToolsException(String gripe)
	{
		super(gripe);
	}
	
	/**
	Constructor
	@param	cause	Nested exception
	*/
	public HL_fileToolsException(Throwable cause)
	{
		super(cause);
	}
}
