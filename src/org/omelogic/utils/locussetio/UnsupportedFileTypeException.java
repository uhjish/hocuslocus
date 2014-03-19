package org.omelogic.utils.locussetio;

/**
 *Signals that I/O for a particular file type is not supported.
 *
 *@author Wade Gobel
 *@version 07/20/2007
 */

public class UnsupportedFileTypeException extends java.io.IOException
{
    /**
     *Constructs an IllegalFileFormatException with null as its error detail
     *message.
     */
    public UnsupportedFileTypeException()
    {
        super();
    }
    
    /**
     *Constructs an IllegalFileFormatException with the specified detail
     *message.
     *
     *@param message    the detail message.
     */
    public UnsupportedFileTypeException(String message)
    {
        super(message);
    }
}
