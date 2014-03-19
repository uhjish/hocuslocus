package org.omelogic.utils.locussetio;

/**
 *The GTFTranslator class provides methods to be used in the translation of
 *{@code String}s extracted from GTF files to {@code Locus} objects as indicated
 *by the GTF file specifications.
 *
 *@author Wade Gobel
 *@version 07/24/2007
 *@see Locus
 */

import java.io.*;
import java.util.*;
import org.omelogic.locus.*;

public class GTFTranslator implements LocusTranslator
{
    public static final String HOCUSLOCUS_ID_STRING = "hocuslocus_id";
    public static final String DEFAULT_LOCUS_ID = "GTFLocus";
    public static final String GTF_IDENTIFIER_FIELD = "transcript_id";
    public static final int MIN_ARGS = 9;   // the minimum number of arguments
                                            // per line in a GTF file
    public static final int MAX_ARGS = 10;  // the maximum number of arguments
                   // per line in a GTF file (including attributes and comments)
    
    public GTFTranslator()
    {
        // do nothing - no variables to instantiate
    }
    
    /**
     *Checks if the passed file name represents a file format supported by
     *this GTFTranslator.
     *
     *@param fileName   the name of the file to be checked.
     *
     *@return           true if the passed file name represents a file format
     *                  supported by this GTFTranslator, false otherwise.
     */
    public boolean isSupportedFile(String fileName)
    {
        return fileName.toLowerCase().endsWith(".gtf");
    }
    
    /**
     *Reads GTF file format-specific data from the passed {@code String} and
     *returns a {@code Locus} object with the relevant extracted data.
     *
     *@param line   a {@code String} representing the data for a {@code Locus}
     *              object.
     *
     *@return       a {@code Locus} object containing the data stored in the
     *              passed {@code String} per the GTF file specification.
     *              Returns null if the line is null, empty, or commented.
     *
     *@exception    {@code IllegalDataFormatException} if the data provided are
     *              illegal, missing, or out of order.
     */
    public Locus decode(String line) throws IllegalDataFormatException
    {
    	/*   	
			GTF (Gene Transfer Format) is a refinement to GFF that tightens the specification. The first eight GTF fields are the same as GFF. The group field has been expanded into a list of attributes. Each attribute consists of a type/value pair. Attributes must end in a semi-colon, and be separated from any following attribute by exactly one space.
			The attribute list must begin with the two mandatory attributes:
				* gene_id value - A globally unique identifier for the genomic source of the sequence.
				* transcript_id value - A globally unique identifier for the predicted transcript. 
			Example:
			Here is an example of the ninth field in a GTF data line:
				gene_id "Em:U62317.C22.6.mRNA"; transcript_id "Em:U62317.C22.6.mRNA"; exon_number 1 	
		*/
        if(line == null || line.length() == 0 || line.startsWith("#"))
            return null;
        
        String[] data = line.split("\t");
        
        if(data.length < MIN_ARGS)
        {
            String message = ("Insufficient arguments provided from GTF line: "
                + "Min - " + MIN_ARGS + ", Provided - " + data.length + ":\n");
            for(String s: data)
                message += (s + " ");
            throw new IllegalDataFormatException(message);
        }
        else if(data.length > MAX_ARGS)
        {
            String message = ("Excess arguments provided from GTF line: " +
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
					if (key.equals("hocuslocus_id"))
					{
						//this should override any other id
						//remove any quotes
						val = val.replaceAll("\"", "");							
						result.setID(val);
					}else{
						if (key.equals(GTF_IDENTIFIER_FIELD)){
							//this will first use the transcript_id as the locus_id
							//remember that transcript_id is mandatory for gtf loci
							//guaranteed to be parsed first
							//and guaranteed to be globally unique
							val = val.replaceAll("\"", "");							
							result.setID(val);
							//this will be the id if there is no hocuslocus_id						
						}
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
    	
    	LocusSet returnSet = new LocusSet(lset.getName());
    	
    	HashMap<String, Locus> gtfIDsToLoci = new HashMap<String, Locus>();
    	
    	Iterator<Locus> locIter = lset.getLoci();
    	
    	while ( locIter.hasNext() )
    	{
    		Locus curLoc = locIter.next();
    		String curID = (String)curLoc.getAnnotation(GTF_IDENTIFIER_FIELD);
    		Locus curParent;
    		if (! gtfIDsToLoci.containsKey( curID ) )
    		{
    			curParent = new Locus( curID, curLoc.getChromosome(), curLoc.getStart(), curLoc.getEnd(), curLoc.getStrand(), Locus.TYPE.TRANSCRIPTIONAL_REGION, curLoc.getSource());
    			gtfIDsToLoci.put(curID, curParent);
    			returnSet.addLocus( curParent );
    			
			}else{
				curParent = gtfIDsToLoci.get(curID);
			}
			
			curParent.mergeCoords(curLoc);
			curParent.addChild(curLoc);
		}
			
		return returnSet;
	}

    /**
     *Returns a {@code String} representation of the passed {@code Locus} object
     *based on the GTF translation scheme.
     *
     *@param locus  the {@code Locus} to be encoded.
     *
     *@return       a {@code String} representation of the passed {@code Locus}
     *              based on the GTF translation scheme.
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
