package org.omelogic.utils.locussetio;

/**
 *Objects implementing the LocusTranslator interface provide methods for
 *translating {@code String}s to {@code Locus} objects and vice versa, based on
 *a certain file format translation scheme.
 *
 *@author Wade Gobel
 *@version 7/23/2007
 *@see Locus
 */

import org.omelogic.locus.*;

public interface LocusTranslator
{
    /**
     *Checks if the file format is supported by this LocusTranslator.
     *
     *@param fileName   the name of the file to be checked.
     *
     *@return           true if the passed file name represents a file format
     *                  supported by this LocusTranslator, false otherwise.
     */
    public boolean isSupportedFile(String fileName);
    
    /**
     *Reads file format-specific data from the passed {@code String} and returns
     *a {@code Locus} object with the relevant extracted data.
     *
     *@param line   a {@code String} representing the data for a {@code Locus}
     *              object.
     *
     *@return       a {@code Locus} object containing the data stored in the
     *              passed {@code String} per the particular file specification.
     *              Returns null if the line contains no Locus data.
     *
     *@exception    {@code IllegalDataFormatException} if this LocusTranslator
     *              does not support a decoding scheme for the passed
     *              {@code String} or if the data provided are illegal, missing,
     *              or out of order.
     */
    public Locus decode(String line) throws IllegalDataFormatException;
    
    /**
     *Returns a {@code String} representation of the passed {@code Locus} object
     *based on the particular file format translation scheme.
     *
     *@param locus  the {@code Locus} to be encoded.
     *
     *@return       a {@code String} representation of the passed {@code Locus}
     *              based on the particular file format translation scheme.
     *
     *@exception    {@code NullPointerException} if the passed {@code Locus} is
     *              null.
     */
    public String encode(Locus locus) throws NullPointerException;
    
    /**
     *Performs postprocessing operations (example: condense GTF to one {@code Locus} per gene)
     *
     *@param lset  the {@code LocusSet} to be postprocessed.
     *
     *@return       a {@code LocusSet} with the postprocessing done
     *
     */
    public LocusSet postProcess(LocusSet lset) throws LocusException;
    
}
