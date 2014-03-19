package org.omelogic.utils;


/**
 * @author Chris Zaleski
 * @version 1.0
 *
 * Handles  transformations of files to LocusSets and vice-versa
 */

import java.io.*;
import org.omelogic.locus.*;

public class HL_fileTools
{
	//----------------------------------------------------------
	
	public static LocusSet gffToLocusSet(InputStream iStream) throws HL_fileToolsException
	{
		InputStreamReader isr = new InputStreamReader(iStream);
		return readerToLS(isr);
	}
	
	//----------------------------------------------------------
	
	public static LocusSet gffToLocusSet(String localFileName) throws HL_fileToolsException
	{
		FileReader fr = null;

		try
		{
			fr = new FileReader(localFileName);
		}
		catch (FileNotFoundException fnfe)
		{
			throw new HL_fileToolsException(fnfe);
		}
		
		return readerToLS(fr);
	}
	
	//----------------------------------------------------------
	
	private static LocusSet readerToLS(Reader reader) throws HL_fileToolsException
	{
		BufferedReader br = new BufferedReader(reader);
		LocusSet newLocusSet = new LocusSet("");

		String[] splitLine;
		String[] splitAttributes;
		String currLine;
		String seq = "";
		int start = 0;
		int end = 0;
		String strand;
		int cStrand;
		String label = "";
		int lineCount = 0;

		// iterate through all lines in the file
		try
		{
			currLine = br.readLine();

			while(currLine != null)
			{
				lineCount++;
				currLine = currLine.trim();

				// skip # and blank lines
				if(currLine.startsWith("#") || currLine.equals(""))
				{
					currLine = br.readLine();
					continue;
				}

				splitLine = currLine.split("\t");

				try
				{
					if(splitLine.length < 5)
					{
						throw new HL_fileToolsException("Invalid line argument: \n" + currLine);
					}
					seq = splitLine[0];
					start = Integer.parseInt(splitLine[3]);
					end = Integer.parseInt(splitLine[4]);
					strand = splitLine[6];
					if(strand.equals("+"))
						cStrand = Locus.STRAND.POSITIVE;
					else if(strand.equals("-"))
						cStrand = Locus.STRAND.NEGATIVE;
					else
						cStrand = Locus.STRAND.UNDEFINED;
					if(splitLine.length >= 9)
					{
						splitAttributes = splitLine[8].split(";");
						label = splitAttributes[0];
					}
				}
				catch(NumberFormatException nfe)
				{
					throw new HL_fileToolsException("Invalid line argument: \n" + currLine);
				}

				//validate positional data
				if(end <= start)
				{
					throw new HL_fileToolsException("Invalid positional argument: \n" + currLine);
				}

				// eventually the "type" should be looked up - based on file position 3 - and
				// added here (instead of UNDEFINED
				newLocusSet.addLocus(new Locus(label, seq, start, end, cStrand, Locus.TYPE.UNDEFINED, ""));
				currLine = br.readLine();
			}

			br.close();
		}
		catch (IOException ioe)
		{
			throw new HL_fileToolsException(ioe);
		}
		
		return newLocusSet;
	}

}
