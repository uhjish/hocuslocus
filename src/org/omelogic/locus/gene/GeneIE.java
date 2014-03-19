/*
 *      GeneIE.java
 *      
 *      Copyright 2008 Ajish D. George <ajish@hocuslocus.com>
 *      
 * 		Gene Information Extensions
 * 		Provides methods to get and annotate genes [Locus type gene or mRNA]
 */

package org.omelogic.locus.gene;

import org.omelogic.locus.*;
import java.util.*;

public class GeneIE {
	
	public static LocusSet addPromoters( LocusSet genes, int upstream, int downstream ) throws Exception
	{
		LocusSet operonSet = new LocusSet( genes.getName() );
		
		Iterator<Locus> curGene = genes.getLoci();

		Locus loc, prom, operon;
		int start, end, promstart, promend;
		while( curGene.hasNext() )
		{
			loc = (Locus)(curGene.next());			
			start = loc.getStart();
			end = loc.getEnd();
			
			if ( loc.getStrand() == Locus.STRAND.NEGATIVE)
			{
				promstart = end - downstream;
				promend = end + upstream;
				if (promstart < start) promstart = start;
				if (promstart < 0) promstart =0;

				prom = new Locus( loc.getID(), loc.getChromosome(), promstart, promend, loc.getStrand(), Locus.TYPE.PROMOTER, loc.getSource() );
				operon = new Locus( loc.getID(), loc.getChromosome(), start, promend, loc.getStrand(), Locus.TYPE.OPERON, loc.getSource() );

			}else{
				
				promstart = start - upstream;
				promend = start + downstream;
				if (promend > end) promend = end;
				if (promstart < 0) promstart = start;

				prom = new Locus( loc.getID(), loc.getChromosome(), promstart, promend, loc.getStrand(), Locus.TYPE.PROMOTER, loc.getSource() );
				operon = new Locus( loc.getID(), loc.getChromosome(), promstart, end, loc.getStrand(), Locus.TYPE.OPERON, loc.getSource() );

			}

			//ensure this
			try{
				operon.addChild( prom );
			}catch (Exception e){
				throw new Exception("Couldn't add promoter" + e.toString() + start + "->" + end + "pro:" + promstart + "->" + promend + "opn:"+operon.getStart() +"->" + operon.getEnd());
			}
			try{
				operon.addChild( loc );
			}catch (Exception e){
				throw new Exception("Couldn't add locus" + e.toString() + start + "->" + end + "pro:" + promstart + "->" + promend + "opn:"+operon.getStart() +"->" + operon.getEnd());
			}
			operonSet.addLocus( operon );
			
		}
		
		return operonSet;
	}

}
