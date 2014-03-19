package org.omelogic.utils.locussetio;

/**
 *The FASTATranslator class provides methods to be used for writing FASTA files
 *from LocusSets.
 *
 *@author Chris Zaleski
 *@version 10/23/2007
 *@see Locus
 */

import java.io.*;
import java.util.Scanner;
import org.omelogic.locus.*;

public class FASTATranslator implements LocusTranslator
{
    private String headerDelimiter = "\t";
    
    public FASTATranslator()
    {
        // do nothing - no variables to instantiate
    }
    
    /**
     *@param headerDelimiter   The delimiter to use for the header line tokens
     */
    public FASTATranslator(String headerDelimiter)
    {
        this.headerDelimiter = headerDelimiter;
    }
    
    /**
     *Checks if the passed file name represents a file format supported by
     *this FASTATranslator.
     *
     *@param fileName   the name of the file to be checked.
     *
     *@return           true if the passed file name represents a file format
     *                  supported by this FASTATranslator, false otherwise.
     */
    public boolean isSupportedFile(String fileName)
    {
        return (fileName.toLowerCase().endsWith(".fasta") || fileName.toLowerCase().endsWith(".fa"));
    }
    
    /**
     *Decoding of a FASTA file into a LocusSet is unsupported
     *
     *@param line   
     *
     *@return       A locus representing the argument String
     *
     *@exception    {@code IllegalDataFormatException} if this function is called
     */
    public Locus decode(String line) throws IllegalDataFormatException
    {
        throw new IllegalDataFormatException("FASTATranslator does not support decoding");
    }

    /**
     *Performs postprocessing operations (example: condense GTF to one {@code Locus} per gene)
     *
     *@param lset  the {@code LocusSet} to be postprocessed.
     *
     *@return       a {@code LocusSet} with the postprocessing done
     *
     */
    public LocusSet postProcess(LocusSet lset) throws LocusException
    {
    	return lset;
	}    


    
    /**
     *Returns a {@code String} representation of the passed {@code Locus} object
     *based on the FASTA translation scheme.
     *
     *@param locus  the {@code Locus} to be encoded.
     *
     *@return       a {@code String} representation of the passed {@code Locus}
     *              based on the FASTA translation scheme.
     *
     *@exception    {@code NullPointerException} if the passed {@code LocusSet}
     *              is null.
     */
    public String encode(Locus locus) throws NullPointerException
    {
        if(locus == null)
            throw new NullPointerException("Encoded Locus cannot be null");
        
        StringBuffer result = new StringBuffer();
        
		result.append(">");
		result.append(locus.getID());
		result.append(headerDelimiter);
		result.append(locus.getChromosome());
		result.append(headerDelimiter);
		result.append(locus.getStrandShortString());
		result.append(headerDelimiter);
		result.append(locus.getStart());
		result.append(headerDelimiter);
		result.append(locus.getEnd());
		result.append("\n");
		
		String seq = locus.getSequence();
		int sequenceEndIndex = seq.length();
		int subStartIndex = 0; // init value
		int subEndIndex = 50; // init value
		
		while(sequenceEndIndex > subEndIndex)
		{
			result.append(seq.substring(subStartIndex, subEndIndex));
			result.append("\n");
			subStartIndex = subStartIndex+50;
			subEndIndex = subStartIndex+50;
		}
		// remainder
		result.append(seq.substring(subStartIndex));
        
        return result.toString();
    }
}
