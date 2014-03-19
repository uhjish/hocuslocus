/*
 *      LocusSieve.java
 *      
 *      Copyright 2008 Ajish D. George <ajish@hocuslocus.com>
 *      
 */
package org.omelogic.locus.filter;

import org.omelogic.locus.*;

import java.io.Serializable;
import java.util.*;

public abstract class LocusSieve implements java.io.Serializable
{

	abstract public boolean accept(Locus locus) throws Exception;
	abstract public String getShortDescription();
	
	public String toString()
	{
		return this.getShortDescription();
	}
	
	public static LocusSet siftSet( LocusSet siftMe, LocusSieve sieve ) throws Exception{
		
		LocusSieve[] sieveArray = { sieve };
		return siftSet( siftMe, sieveArray );
	
	}
	
	public static LocusSet siftSet( LocusSet siftMe, Collection<LocusSieve> sieveSet ) throws Exception{
		
		LocusSieve[] sieveArray = new LocusSieve[sieveSet.size()];
		Iterator<LocusSieve> sieveIterator = sieveSet.iterator();
		int sieveIndex=0;
		//LocusSieve sieve;
		while (sieveIterator.hasNext()){
			sieveArray[sieveIndex++] = (LocusSieve)sieveIterator.next();
		}
		
		return siftSet( siftMe, sieveArray );
	
	}		

	public static LocusSet siftSet( LocusSet siftMe, LocusSieve[] sieveArray ) throws Exception{
	
		LocusSet sifted = new LocusSet(siftMe.getName());		
		Iterator<Locus> locusIterator = siftMe.getLoci();
		Locus locus;
		boolean passed;
		//LocusSieve filter;
		int sieveIndex;
		while ( locusIterator.hasNext() ){
			locus = (Locus)locusIterator.next();
			passed = true;
			for (sieveIndex =0; sieveIndex < sieveArray.length && passed; sieveIndex++){
				passed = (passed && sieveArray[sieveIndex].accept(locus));
			}
			if (passed){
				sifted.addLocus( locus );
			}
		}
		
		return sifted;

	}

}
