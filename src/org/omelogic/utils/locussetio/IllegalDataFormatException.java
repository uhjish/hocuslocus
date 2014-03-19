package org.omelogic.utils.locussetio;

/**
 *Signals illegal or incorrectly formatted data.
 *
 *@author Wade Gobel
 *@version 07/20/2007
 */

public class IllegalDataFormatException extends IllegalArgumentException
{
    /**
     *Constructs an IllegalDataFormatException with null as its error detail
     *message.
     */
    public IllegalDataFormatException()
    {
        super();
    }
    
    /**
     *Constructs an IllegalDataFormatException with the specified detail
     *message.
     *
     *@param message    the detail message.
     */
    public IllegalDataFormatException(String message)
    {
        super(message);
    }
}
