package org.omelogic.locus.filter;

import java.io.*;
import java.util.*;
import org.omelogic.locus.*;

public class testFilter
{
	LocusSet lSet = new LocusSet("tester");
	ArrayList<Locus> filterSet = new ArrayList<Locus>();

	public static void main(String[] args)
	{
		new testFilter(args);
	}

	//--------------------------------------------------------------------

	testFilter(String[] args)
	{
		readFile(args[0]);

		LocusFilter lf = new LocusFilter();
		lf.addFilterCriteria(new FC_Locus_Type("utr"));
		lf.addFilterCriteria(new FC_Locus_Strand(Locus.STRAND.POSITIVE));
		filterSet = lf.filterSet(lSet, true);

		writeFile();
	}

	//--------------------------------------------------------------------

	private void readFile(String f)
	{
		BufferedReader fileIn = null;

		try
		{
			fileIn = new BufferedReader(new FileReader(f));
		}
		catch (FileNotFoundException fnfe)
		{
			System.out.println("Input file " + f + " cannot be found. Terminating...");
			System.exit(0);
		}

		try
		{
			String currLine = fileIn.readLine();
			String[] splitLine;

			String seq = "";
			int start = 0;
			int end = 0;
			int cdsStart = 0;
			int cdsEnd = 0;
			String strand;
			int s;
			String label = "";

			Locus gene;

			while(currLine != null)
			{
				currLine = currLine.trim();

				// skip # and blank lines
				if(currLine.startsWith("#") || currLine.equals(""))
				{
					currLine = fileIn.readLine();
					continue;
				}

				splitLine = currLine.split("\t");

				seq = splitLine[0];
				start = Integer.parseInt(splitLine[1]);
				end = Integer.parseInt(splitLine[2]);
				label = splitLine[3];
				strand = splitLine[5];
				if(strand.equals("+"))
					s = Locus.STRAND.POSITIVE;
				else
					s = Locus.STRAND.NEGATIVE;
				cdsStart = Integer.parseInt(splitLine[6]);
				cdsEnd = Integer.parseInt(splitLine[7]);

				gene = new Locus(label, seq, start, end, s, "gene", "knownGene");
				gene.addChild(new Locus(label, seq, start, cdsStart, s, "utr", "knownGene"));
				gene.addChild(new Locus(label, seq, cdsEnd, end, s, "utr", "knownGene"));
				gene.addChild(new Locus(label, seq, cdsStart, cdsEnd, s, "cds", "knownGene"));

				lSet.addLocus(gene);

				currLine = fileIn.readLine();
			}

			System.out.println("Input File \"" + f + "\" has been loaded");

			fileIn.close();
		}
		catch (Exception ioe)
		{
			System.out.println(ioe.toString());
			System.exit(1);
		}
	}

	//--------------------------------------------------------------------

	private void writeFile()
	{
		PrintWriter fileOut;
		String fs = System.getProperty("file.separator");
		String outFileName = System.getProperty("user.dir") + fs + "filtered.bed";

		System.out.println(outFileName);

		// Prepare output file
		try
		{
			fileOut = new PrintWriter(new BufferedWriter(new FileWriter(outFileName, false)));

			fileOut.println("browser position chr1");

			fileOut.println("track name=Original color=0,0,200 description=\"Original Loci\"");

			Iterator<Locus> iter = lSet.getLoci();
			Locus currLocus;

			while(iter.hasNext())
			{
				currLocus = iter.next();
				fileOut.println(currLocus.getChromosome() + "\t" + currLocus.getStart() + "\t" + currLocus.getEnd() + "\t" + currLocus.getID());
			}

			fileOut.println("track name=Filtered_Loci color=200,0,0 description=\"Filtered Loci\"");

			iter = filterSet.iterator();

			while(iter.hasNext())
			{
				currLocus = iter.next();
				fileOut.println(currLocus.getChromosome() + "\t" + currLocus.getStart() + "\t" + currLocus.getEnd() + "\t" + currLocus.getID());
			}

			fileOut.flush();
			fileOut.close();
		}
		catch (Exception e)
		{
			System.out.println(e.toString());
			System.exit(0);
		}
	}
}