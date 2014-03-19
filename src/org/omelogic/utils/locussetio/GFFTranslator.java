package org.omelogic.utils.locussetio;

/**
 *The GFFTranslator class provides methods to be used in the translation of
 *{@code String}s extracted from GFF files to {@code Locus} objects as indicated
 *by the GFF file specifications.
 *
 *@author Wade Gobel
 *@version 07/24/2007
 *@see Locus
 */

import java.io.*;
import java.util.Scanner;
import org.omelogic.locus.*;

public class GFFTranslator implements LocusTranslator
{
    public static final String HOCUSLOCUS_ID_STRING = "hocuslocus_id";
    public static final String DEFAULT_LOCUS_ID = "GFFLocus";
    public static final int MIN_ARGS = 8;   // the minimum number of arguments
                                            // per line in a GFF file
    public static final int MAX_ARGS = 10;  // the maximum number of arguments
                   // per line in a GFF file (including attributes and comments)
    
    public GFFTranslator()
    {
        // do nothing - no variables to instantiate
    }
    
    /**
     *Checks if the passed file name represents a file format supported by
     *this GFFTranslator.
     *
     *@param fileName   the name of the file to be checked.
     *
     *@return           true if the passed file name represents a file format
     *                  supported by this GFFTranslator, false otherwise.
     */
    public boolean isSupportedFile(String fileName)
    {
        return fileName.toLowerCase().endsWith(".gff");
    }
    
    /**
     *Reads GFF file format-specific data from the passed {@code String} and
     *returns a {@code Locus} object with the relevant extracted data.
     *
     *@param line   a {@code String} representing the data for a {@code Locus}
     *              object.
     *
     *@return       a {@code Locus} object containing the data stored in the
     *              passed {@code String} per the GFF file specification.
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
            String message = ("Insufficient arguments provided from GFF line: "
                + "Min - " + MIN_ARGS + ", Provided - " + data.length + ":\n");
            for(String s: data)
                message += (s + " ");
            throw new IllegalDataFormatException(message);
        }
        else if(data.length > MAX_ARGS)
        {
            String message = ("Excess arguments provided from GFF line: " +
                "Max - " + MAX_ARGS + ", Provided - " + data.length + ":\n");
            for(String s: data)
                message += (s + " ");
            throw new IllegalDataFormatException(message);
        }
        
        //Eventually 'type' can check & match Locus.TYPE values...
        //String type = data[2];
        
        int start;
        try
        {
            start = Integer.parseInt(data[3]);
        }
        catch(NumberFormatException n)
        {
            throw new IllegalDataFormatException("Illegal starting index: " +
                                                 data[3]);
        }
        
        int end;
        try
        {
            end = Integer.parseInt(data[4]);
        }
        catch(NumberFormatException n)
        {
            throw new IllegalDataFormatException("Illegal ending index: " +
                                                 data[4]);
        }
        
        double score;
        if(data[5].equals("."))
            score = 0;
        else
            try
            {
                score = Double.parseDouble(data[5]);
            }
            catch(NumberFormatException n)
            {
                throw new IllegalDataFormatException("Illegal score: "+data[5]);
            }
        
        int strand;
        
        if(data[6].equals("+"))
            strand = Locus.STRAND.POSITIVE;
        else if(data[6].equals("-"))
            strand = Locus.STRAND.NEGATIVE;
        else if(data[6].equals("."))
            strand = Locus.STRAND.UNDEFINED;
        else
            throw new IllegalDataFormatException("Illegal strand value: " +
                data[6] + "\nLegal values: '+', '-', and '.'");
        
        String id = DEFAULT_LOCUS_ID;
        Locus result = new Locus(id,data[0],start,end,strand,data[2],data[1]);
        result.setScore(score);
        if(data.length > MIN_ARGS)
        {
            try
            {   // parse other annotations
                Scanner scanner = new Scanner(data[8]);
                scanner.useDelimiter(";");
                
                while ( scanner.hasNext() )
                {
                	String curAnno = scanner.next().trim();
                	
					if (curAnno.equals(""))
					{
						continue;
					}
					
					int firstBreak = curAnno.indexOf( " " );
					String key = curAnno.substring( 0, firstBreak ).trim();
					String val = curAnno.substring( firstBreak+1 ).trim();
					if (key.equals(HOCUSLOCUS_ID_STRING))
					{
						//this should override any other id
						//remove any quotes
						val = val.replaceAll("\"", "");							
						result.setID(val);
					}else{
						//in all other cases just add a key value pair
						result.addAnnotation( key, val );
					}
				}
            }
            catch(Exception e)
            {
                throw new IllegalDataFormatException("Illegal formatting of " +
                    "attributes token: " + data[8] + "\n" +
                    "Must consist of strings separated by semicolons (;).\n" +
                    "Exactly one semicolon in between each tag/value set.");
            }
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
     *based on the GFF translation scheme.
     *
     *@param locus  the {@code Locus} to be encoded.
     *
     *@return       a {@code String} representation of the passed {@code Locus}
     *              based on the GFF translation scheme.
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
        result.append(locus.getSource());
        result.append("\t");
        result.append(locus.getType());
        result.append("\t");
        result.append(locus.getStart());
        result.append("\t");
        result.append(locus.getEnd());
        result.append("\t");
        result.append(locus.getScore());
        result.append("\t");
        result.append(locus.getStrandShortString());
        result.append("\t");
        result.append(".");     // frame
        result.append("\t");
        result.append(HOCUSLOCUS_ID_STRING + " " + locus.getID());
        
        return result.toString();
    }
}
