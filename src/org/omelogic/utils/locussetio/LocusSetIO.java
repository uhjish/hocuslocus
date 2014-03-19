package org.omelogic.utils.locussetio;

/**
 *The LocusSetIO class provides static methods to be used for reading from and
 *writing to certain file formats that store sets of {@code Locus} data. Objects
 *that deal with interpretation of specific file formats must implement the
 *{@code LocusTranslator} interface.
 *
 *@author Wade Gobel
 *@version 07/23/2007
 *@see Locus
 *@see LocusSet
 *@see LocusTranslator
 */

import java.io.*;
import java.util.*;
import org.omelogic.locus.*;

public class LocusSetIO
{
    // the codes to be passed to the read and write methods to indicate the
    // appropriate translation scheme.
    public static final int GFF = 0;    // the index of each LocusTranslator
    public static final int GTF = 1;
    public static final int BED = 2;    // in the TRANSLATORS array
    public static final int FASTA = 3;
    
    public static final String[] FORMAT_DESCRIPTIONS = {
    		"GFF [Gene Feature Format]",
    		"GTF [Gene Transfer Format]",
    		"BED [Browser Extensible Data]",
    		"FASTA [Fast-All Sequences]"
		};
    
    // returned by getLocusTranslator(String fileName) to indicate that no
    // translation scheme exists for a provided file type.
    private static final int NO_TRANSLATOR = -1;
    
    private static final LocusTranslator[] TRANSLATORS = new LocusTranslator[]    {
    	new GFFTranslator(), 
    	new GTFTranslator(),
    	new BEDTranslator(),
    	new FASTATranslator()
    	};
    
    /**
     *Constructs a LocusSetIO object.
     */
    public LocusSetIO()
    {
        // do nothing - no variables to instantiate
    }
    
    /**
     *Returns the index of the {@code LocusTranslator} in the TRANSLATORS array
     *that can correctly interpret the data contained in the file with the
     *provided name. Returns NO_TRANSLATOR if no recognized LocusTranslator
     *exists.
     *
     *@param fileName   the name of the file containing {@code Locus} data.
     *
     *@return           the integer index of the {@code LocusTranslator} in the
     *                  TRANSLATORS array that can appropriately translate the
     *                  data contained in the file into {@code Locus} objects.
     *                  Returns NO_TRANSLATOR if no recognized LocusTranslator
     *                  exists.
     */
    public static int getTranslatorCode(String fileName)
    {
        for(int i = 0; i < TRANSLATORS.length; i++)
            if(getLocusTranslator(i).isSupportedFile(fileName))
                return i;
        
        return NO_TRANSLATOR;
    }
    
    /**
     *Returns the {@code LocusTranslator} with the passed code.
     *
     *@param translator     the integer code of the desired
     *                      {@code LocusTranslator}.
     *
     *@return               the {@code LocusTranslator} with the specified code.
     *
     *@exception            {@code IllegalArgumentException} if the passed code
     *                      does not have an associated {@code LocusTranslator}.
     */
    private static LocusTranslator getLocusTranslator(int translator)
                                                 throws IllegalArgumentException
    {
        if(translator < 0 || translator >= TRANSLATORS.length)
            throw new IllegalArgumentException("Translator code does not " +
                "have an associated translation scheme: " + translator);
        
        return TRANSLATORS[translator];
    }
    
    /**
     *Reads from the indicated file and returns a {@code LocusSet} of
     *{@code Locus} objects as interpreted per the specifications of the
     *appropriate translation scheme. (The LocusSet's name will be set to the
     *file name.)
     *
     *@param fileName   the name of the file to be read.
     *
     *@return           a {@code LocusSet} containing {@code Locus} objects with
     *                  data read from the file as interpreted per the
     *                  specifications of the appropriate translation scheme.
     *
     *@exception        {@code UnsupportedFileTypeException} if no recognized
     *                  translation scheme exists for the passed file type.
     *@exception        {@code IOException} if a problem occurs while reading
     *                  from the file.
     *@exception        {@code IllegalDataFormatException} if the data in the
     *                  file are illegal, missing, or out of order.
     */
    public static LocusSet readLocusSet(String fileName)
    throws UnsupportedFileTypeException, IOException, IllegalDataFormatException, LocusException
    {
        int translator = getTranslatorCode(fileName);
        if(translator == NO_TRANSLATOR)
            throw new UnsupportedFileTypeException(
                "No recognized translation scheme for file: " + fileName);
        else
            return readLocusSet(translator, fileName);
    }
    
    /**
     *Reads from the indicated file and returns a {@code LocusSet} of
     *{@code Locus} objects as interpreted per the specifications of the passed
     *translator code. (The LocusSet's name will be set to the file name.)
     *
     *@param translator the integer code indicating the translation scheme to be
     *                  used while interpreting the file data.
     *@param fileName   the name of the file to be read.
     *
     *@return           a {@code LocusSet} containing {@code Locus} objects with
     *                  data read from the file as interpreted by the indicated
     *                  translation scheme.
     *
     *@exception        {IllegalArgumentException} if the passed translator code
     *                  does not have an associated translation scheme.
     *@exception        {@code IOException} if a problem occurs while reading
     *                  from the file.
     *@exception        {@code IllegalDataFormatException} if the data in the
     *                  file are illegal, missing, or ot of order.
     */
    public static LocusSet readLocusSet(int translator, String fileName)
      throws IllegalArgumentException, IllegalDataFormatException, IOException, LocusException
    {
        try
        {
            LocusTranslator t = getLocusTranslator(translator);
            FileInputStream stream = new FileInputStream(fileName);
            return readLocusSet(t, stream, fileName);
        }
        catch(IOException io)
        {
            throw new IOException("Problem reading Locus file " + fileName +
                                  ":\n" + io.toString());
        }
    }
    
    /**
     *Reads from the passed {@code InputStream} and returns a {@code LocusSet}
     *containing {@code Locus} objects as interpreted per the specifications of
     *the passed translation scheme. The LocusSet's name will be set to the
     *passed {@code String}. Closes the InputStream when reading is completed.
     *
     *@param translator the integer code of the translation scheme to be used
     *                  while interpreting the {@code InputStream} data.
     *@param stream     the {@code InputStream} to be read.
     *@param setName    the desired name of the {@code LocusSet}.
     *
     *@return           a {@code LocusSet} containing the {@code Locus} objects
     *                  with data read from the passed {@code InputStream},
     *                  interpreted per the specifications of the passed
     *                  translation code.
     *
     *@exception        {@code IllegalArgumentException} if the passed
     *                  translation code does not have an associated translation
     *                  scheme.
     *@exception        {@code IllegalDataFormatException} if the data provided
     *                  by the {@code InputStream} are illegal, missing, or out
     *                  of order.
     *@exception        {@code IOException} if a problem occurs while reading
     *                  from the {@code InputStream}.
     *
     *@see InputStream
     */
    public static LocusSet readLocusSet(int translator, InputStream stream,
                  String setName) throws IllegalArgumentException,
                                         IllegalDataFormatException, IOException, LocusException
    {
        return readLocusSet(getLocusTranslator(translator), stream, setName);
    }
    
    /**
     *Reads from the passed {@code InputStream} and returns a {@code LocusSet}
     *containing {@code Locus} objects as interpreted by the passed
     *{@code LocusTranslator}. The LocusSet's name will be set to the passed
     *{@code String}. Closes the InputStream when reading is completed.
     *
     *@param t          the {@code LocusTranslator} to be used while
     *                  interpreting the {@code InputStream} data.
     *@param stream     the {@code InputStream} to be read.
     *@param setName    the desired name of the {@code LocusSet}.
     *
     *@return           a {@code LocusSet} containing the {@code Locus} objects
     *                  with data read from the passed {@code InputStream},
     *                  interpreted by the passed {@code LocusTranslator}.
     *
     *@exception        {@code IllegalDataFormatException} if the data provided
     *                  by the {@code InputStream} are illegal, missing, or out
     *                  of order.
     *@exception        {@code IOException} if a problem occurs while reading
     *                  from the {@code InputStream}.
     *
     *@see InputStream
     */
    public static LocusSet readLocusSet(LocusTranslator t, InputStream stream,
                  String setName) throws IllegalDataFormatException, IOException, LocusException
    {
    	if(t instanceof FASTATranslator)
    		throw new IllegalDataFormatException("FASTATranslator does not support reading. " +
    			"Only writing is supported");
    	
        try
        {
            Scanner scanner = new Scanner(stream);
            LocusSet resultSet = new LocusSet(setName);
            
            for(int lineNum = 1; scanner.hasNextLine(); lineNum++)
            {
                try
                {
                    String currLine = scanner.nextLine().trim();
                    Locus currLocus = t.decode(currLine);
                    if(currLocus != null)
                        resultSet.addLocus(currLocus);
                }
                catch(IllegalDataFormatException i)
                {
                    throw new IllegalDataFormatException("Problem reading " +
                        resultSet.getName() + " at line " + lineNum + ":\n" +
                        i.getMessage());
                }
            }
           //	resultSet = t.postProcess(resultSet);
            stream.close();
            return resultSet;
        }
        catch(IOException io)
        {
            throw new IOException("Problem reading Locus information:\n" +
                                  io.toString());
        }
    }
    
    /**
     *Writes the {@code Locus} objects contained in the passed {@code LocusSet}
     *to the file name as specified by the appropriate translation scheme.
     *
     *@param set        the {@code LocusSet} containing the {@code Locus} data
     *                  to be written to the file.
     *@param fileName   the name of the file to which the {@code Locus} objects
     *                  contained in the passed {@code LocusSet} are to be
     *                  written.
     *
     *@exception        {@code UnsupportedFileTypeException} if no recognized
     *                  translation scheme exists for the passed file type.
     *@exception        {@code IOException} if a problem occurs while writing to
     *                  the file.
     */
    public static void writeLocusSet(LocusSet set, String fileName)
                                throws UnsupportedFileTypeException, IOException
    {
        int translator = getTranslatorCode(fileName);
        if(translator == NO_TRANSLATOR)
            throw new UnsupportedFileTypeException(
                "No recognized translation scheme for file: " + fileName);
        else
            writeLocusSet(translator, set, fileName, null);
    }
    
    /**
     *Writes the {@code Locus} objects contained in the passed {@code LocusSet}
     *to the file name as specified by the appropriate translation scheme.
     *
     *@param set        the {@code LocusSet} containing the {@code Locus} data
     *                  to be written to the file.
     *@param fileName   the name of the file to which the {@code Locus} objects
     *                  contained in the passed {@code LocusSet} are to be
     *                  written.
     *@param headerLines        optional header lines to be added to the beginning
     *							of the file. Lines are added in the same sequence 
     *							they appear in the List.
     *
     *@exception        {@code UnsupportedFileTypeException} if no recognized
     *                  translation scheme exists for the passed file type.
     *@exception        {@code IOException} if a problem occurs while writing to
     *                  the file.
     */
    public static void writeLocusSet(LocusSet set, String fileName, List<String> headerLines)
                                throws UnsupportedFileTypeException, IOException
    {
        int translator = getTranslatorCode(fileName);
        if(translator == NO_TRANSLATOR)
            throw new UnsupportedFileTypeException(
                "No recognized translation scheme for file: " + fileName);
        else
            writeLocusSet(translator, set, fileName, headerLines);
    }
    
    /**
     *Writes the {@code Locus} objects contained in the passed {@code LocusSet}
     *to the file name per the specifications of the passed translation scheme.
     *
     *@param translator the integer code of the translation scheme to be used
     *                  while writing the {@code Locus} data to the file.
     *@param set        the {@code LocusSet} containing the {@code Locus} data
     *                  to be written to the file.
     *@param fileName   the name of the file to which the {@code Locus} objects
     *                  contained in the passed {@code LocusSet} are to be
     *                  written.
     *@param headerLines        optional header lines to be added to the beginning
     *							of the file. Lines are added in the same sequence 
     *							they appear in the List.
     *
     *@exception        {@code IllegalArgumentException} if the passed
     *                  translator code does not have an associated translation
     *                  scheme.
     *@exception        {@code IOException} if a problem occurs while writing to
     *                  the file.
     */
    public static void writeLocusSet(int translator, LocusSet set,
        String fileName, List<String> headerLines) throws IllegalArgumentException, IOException
    {
        writeLocusSet(getLocusTranslator(translator), set, fileName, headerLines);
    }
    
    /**
     *Writes the {@code Locus} objects contained in the passed {@code LocusSet}
     *to the file name as specified by the {@code LocusTranslator}'s translation
     *scheme.
     *
     *@param t          the {@code LocusTranslator} to be used for encoding the
     *                  {@code Locus} data.
     *@param set        the {@code LocusSet} containing the {@code Locus} data
     *                  to be written to the file.
     *@param fileName   the name of the file to which the {@code Locus} objects
     *                  contained in the passed {@code LocusSet} are to be
     *                  written.
     *@param headerLines        optional header lines to be added to the beginning
     *							of the file. Lines are added in the same sequence 
     *							they appear in the List.
     *
     *@exception        {@code IOException} if a problem occurs while writing to
     *                  the file.
     */
    public static void writeLocusSet(LocusTranslator t, LocusSet set,
                                      String fileName, List<String> headerLines) throws IOException
    {
        try
        {
            FileWriter writer = new FileWriter(fileName);
            if(headerLines != null)
            {
            	Iterator<String> lineIter = headerLines.iterator();
            	while(lineIter.hasNext())
            		writer.append(lineIter.next() + "\n");
            }
            Iterator<Locus> itr = set.getLoci();
            while(itr.hasNext())
            {
                Locus currLocus = itr.next();
                String locusString = t.encode(currLocus);
                writer.append(locusString);
                if(itr.hasNext())
                    writer.append("\n");
            }
            writer.close();
        }
        catch(IOException io)
        {
            throw new IOException("Problem writing to file " + fileName +
                " from set " + set.getName() + ": " + io.toString());
        }
    }
    
    /**
     *Writes the {@code Locus} objects contained in the passed {@code ClusterSet}
     *to the file name using static clusterSet output format
     *
     *@param set        the {@code ClusterSet} containing the {@code LocusCluster} data
     *                  to be written to the file.
     *@param fileName   the name of the file to which the {@code Locus} objects
     *                  contained in the passed {@code LocusSet} are to be
     *                  written.
     *@param headerLines        optional header lines to be added to the beginning
     *							of the file. Lines are added in the same sequence 
     *							they appear in the List.
     *
     *@exception        {@code IOException} if a problem occurs while writing to
     *                  the file.
     */
    public static void writeClusterSet(ClusterSet set, String fileName,
    										List<String> headerLines) throws IOException
    {
        try
        {
            FileWriter writer = new FileWriter(fileName);
            if(headerLines != null)
            {
            	Iterator<String> lineIter = headerLines.iterator();
            	while(lineIter.hasNext())
            		writer.append(lineIter.next() + "\n");
            }
            Iterator<LocusCluster> itr = set.getClusters();
            while(itr.hasNext())
            {
            	LocusCluster currCluster = itr.next();
                writer.append(currCluster.getOutputLine());
                if(itr.hasNext())
                    writer.append("\n");
            }
            writer.close();
        }
        catch(IOException io)
        {
            throw new IOException("Problem writing to file " + fileName +
                " from set " + set.getName() + ": " + io.toString());
        }
    }    
    
}
