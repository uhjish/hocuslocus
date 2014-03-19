package org.omelogic.utils.locussetio;

/**
 *The BEDTranslator class provides methods to be used in the translation of
 *{@code String}s extracted from BED files to {@code Locus} objects as indicated
 *by the BED file specifications.
 *
 *@author Wade Gobel
 *@version 07/24/2007
 *@see Locus
 */

import java.io.*;
import java.util.Scanner;
import org.omelogic.locus.*;

public class BEDTranslator implements LocusTranslator
{
    public static final int MIN_ARGS = 3;   // the minimum number of arguments
                                            // per line in a BED file.
    public static final int MAX_ARGS = 12;  // the maximum number of arguments
                                            // per line in a BED file.
    
    public BEDTranslator()
    {
        // do nothing - no variables to instantiate
    }
    
    /**
     *Checks if the passed file name represents a file format supported by
     *this BEDTranslator.
     *
     *@param fileName   the name of the file to be checked.
     *
     *@return          true if the passed file name represents a file format
     *                  supported by this BEDTranslator, false otherwise.
     */
    public boolean isSupportedFile(String fileName)
    {
        return fileName.toLowerCase().endsWith(".bed");
    }
    
    /**
     *Reads BED file format-specific data from the passed {@code String} and
     *returns a {@code Locus} object with the relevant extracted data.
     *
     *@param line   a {@code String} representing the data for a {@code Locus}
     *              object.
     *
     *@return       a {@code Locus} object containing the data stored in the
     *              passed {@code String} per the BED file specification.
     *              Returns null if the line is null, empty, or commented.
     *
     *@exception    {@code IllegalDataFormatException} if the data provided are
     *              illegal, missing, or out of order.
     */
    public Locus decode(String line) throws IllegalDataFormatException
    {
        if(line == null || line.length() == 0 || line.startsWith("#"))
            return null;
        
        String[] data = line.split("\t");
        
        if(data.length < MIN_ARGS)
        {
            String message = ("Insufficient arguments provided from BED line: "
                + " Min - " + MIN_ARGS + ", Provided - " + data.length + ":\n");
            for(String s: data)
                message += (s + " ");
            throw new IllegalDataFormatException(message);
        }
        else if(data.length > MAX_ARGS)
        {
            String message = ("Excess arguments provided from BED line: Max - " +
                MAX_ARGS + ", Provided - " + data.length + ":\n");
            for(String s: data)
                message += (s + " ");
            throw new IllegalDataFormatException(message);
        }
        
        int start;
        try
        {
            start = Integer.parseInt(data[1]);
        }
        catch(NumberFormatException n)
        {
            throw new IllegalDataFormatException("Illegal starting index: " +
                                                 data[1]);
        }
        
        int end;
        try
        {
            end = Integer.parseInt(data[2]);
        }
        catch(NumberFormatException n)
        {
            throw new IllegalDataFormatException("Illegal ending index: " +
                                                 data[2]);
        }
        
        Locus result = new Locus("", data[0], start, end);
        
        if(data.length >= 4)
        	result.setID(data[3]);
        
        if(data.length >= 5)
        {
            try
            {
                double score = Double.parseDouble(data[4]);
                result.setScore(score);
            }
            catch(NumberFormatException n)
            {
                throw new IllegalDataFormatException("Illegal score value: " +
                                                     data[4]);
            }
        }
            
	   if(data.length >= 6)
	   {
			if(data[5].equals("+"))
				result.setStrand(Locus.STRAND.POSITIVE);
			else if(data[5].equals("-"))
				result.setStrand(Locus.STRAND.NEGATIVE);
			else if(data[5].equals("."))
				result.setStrand(Locus.STRAND.UNDEFINED);
			else
				throw new IllegalDataFormatException("Illegal strand value: " +
					data[5] + "\nLegal values: '+', '-', and '.'");
	   }
        
        return result;
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
     *based on the BED file format translation scheme.
     *
     *@param locus  the {@code Locus} to be encoded.
     *
     *@return       a {@code String} representation of the passed {@code Locus}
     *              based on the BED translation scheme.
     *
     *@exception    {@code NullPointerException} if the passed {@code LocusSet}
     *              is null.
     */
    public String encode(Locus locus) throws NullPointerException
    {
        if(locus == null)
            throw new NullPointerException("Encoded Locus cannot be null");
        
        StringBuffer result = new StringBuffer();
        
        result.append(locus.getChromosome());
        result.append("\t");
        result.append(locus.getStart());
        result.append("\t");
        result.append(locus.getEnd() + 1);
        result.append("\t");
        result.append(locus.getID());
        result.append("\t");
        result.append(locus.getScore());
        result.append("\t");
        result.append(locus.getStrandShortString());
        
        /** /
        result.append("\t");
        
        result.append(".");     // thickStart
        result.append("\t");
        result.append(".");     // thickEnd
        result.append("\t");
        result.append(".");     // itemRgb
        result.append("\t");
        result.append(".");     // blockCount
        result.append("\t");
        result.append(".");     // blockSizes
        result.append("\t");
        result.append(".");     // blockStarts
        /**/
        
        return result.toString();
    }
}
