package org.omelogic.locus;

import java.util.*;
import java.io.Serializable;

/**
A class representing a group or set of Locus objects. LocusSet maintains a wrapper around an Arraylist
as well as additional attributes and functionality which define the contained loci.

@author	Chris Zaleski
*/

public class LocusSet implements java.io.Serializable
{
	private ArrayList<Locus> loci;
	private String setName;
	private boolean hasBeenSquished;

	//--------------------------------------------------------------------
	/**
	Constructor
	@param	name	a String representing a name/identifier for this LocusSet
	*/
	public LocusSet(String name)
	{
		setName = name;
		loci = new ArrayList<Locus>();
		hasBeenSquished = false;
	}
	
	//--------------------------------------------------------------------
	/**
	Constructor
	@param	name	a String representing a name/identifier for this LocusSet
	@param	set	a Collection of Locus objects to initialize the set
	*/
	public LocusSet(String name, Collection<Locus> set)
	{
		setName = name;
		loci = new ArrayList<Locus>(set);
		hasBeenSquished = false;
	}

	//--------------------------------------------------------------------
	/**
	Returns an {@code Iterator} over the Loci of this LocusSet.
	@return	the iterator. No elements may exist if there are no contained loci.
	@see java.util.Iterator
	*/
	public Iterator<Locus> getLoci()
	{
		return loci.iterator();
	}
	//--------------------------------------------------------------------
	/**
	Retrieves the {@code List} of Loci contained in this LocusSet.
	@return	the loci List. May be empty.
	@see java.util.Iterator
	*/
	public List<Locus> getLociList()
	{
		return loci;
	}
	//--------------------------------------------------------------------
	/**
	Returns the Locus at a specific index in the ArrayList.
	@param	index	the index of the Locus being requested.
	@return	the Locus at {@code index}
	*/
	public Locus getLocusByIndex(int index) throws IndexOutOfBoundsException
	{
		return loci.get(index);
	}

	//--------------------------------------------------------------------
	/**
	Returns a copy of the composing ArrayList.
	@return	a clone of ArrayList loci
	*/
	public Locus[] asLocusArray()
	{
		Locus[] results=null;
		loci.trimToSize();
		return loci.toArray(results);

	}

	//--------------------------------------------------------------------
	/**
	Adds a Locus to this LocusSet
	@param	newLocus	the Locus to be added.
	*/
	public int addLocus(Locus newLocus)
	{
		//if ( ! loci.contains(newLocus) ){
			loci.add(newLocus);
			return 1;
		//}else{
		//	return 0;
		//}
	}

	//--------------------------------------------------------------------
	/**
	Returns true if this LocusSet contains the query Locus
	@param	query	the Locus to be checked for
	*/	
	public boolean containsLocus(Locus query)
	{
		return loci.contains(query);
	}
		
	
	//--------------------------------------------------------------------
	/**
	Removes a Locus from this LocusSet as specified in
	ArrayList.remove(Object) and Object.equals(Object).
	@param	locus	the Locus to be removed.
	@return	Returns true if the list contained the specified element
	@see java.util.ArrayList
	@see java.lang.Object
	*/
	public boolean removeLocus(Locus locus)
	{
		return loci.remove(locus);
	}

	/**
	Gets the index of a Locus to this LocusSet
	@param	tgtLocus	the Locus to be added.
	*/
	public int indexOf(Locus tgtLocus)
	{
		return loci.indexOf(tgtLocus);
	}
	
	
	public boolean intersectsSetRegions(Locus tgtLocus)
	{
		Iterator<Locus> iter = this.loci.iterator();
		
		Locus currLocus;
		
		while (iter.hasNext()){
			currLocus = iter.next();
			if(currLocus.overlapCompareTo(tgtLocus) < 0){
				continue;
			}else{
				if (currLocus.overlapCompareTo(tgtLocus) == 0){
					return true;
				}else{
					return false;
				}
			}
		}
		
		return false;
		
	}
	
		


	//--------------------------------------------------------------------
	/**
	Appends a LocusSet to this LocusSet
	@param	newLoci	the LocusSet to be added.
	*/

	public void appendSet(LocusSet newLoci){

		Iterator<Locus> locIter = newLoci.getLoci();
		Locus newLocus;

		while( locIter.hasNext() ) {

			newLocus = locIter.next();
			loci.add(newLocus);

		}
	}

	//--------------------------------------------------------------------
	/**
	Returns the name of this LocusSet.
	@return	the name.
	*/
	public String getName()
	{
		return setName;
	}

	//--------------------------------------------------------------------
	/**
	Set the name of this LocusSet.
	@param	newName	the new name for the set
	*/
	public void setName(String newName)
	{
		setName = newName;
	}


	//--------------------------------------------------------------------
	/**
	Returns the size of this LocusSet.
	@return	the size.
	*/
	public int getSize()
	{
		return loci.size();
	}

	//--------------------------------------------------------------------
	/**
	Returns the total length of the loci in this LocusSet
	@return	the sum of lengths of the loci *Squish first for nucleotide identity.
	*/
	public int getTotalLength()
	{
		Iterator<Locus> myLoci = loci.iterator();
		int totalLength = 0;
		while ( myLoci.hasNext() ){
			totalLength += myLoci.next().getLength();
		}
		return totalLength;
	}

	//--------------------------------------------------------------------
	/**
	Sorts the Locus objects in the ArrayList according to Locus.compareTo(Object);
	*/
	public void sortLoci()
	{
		Collections.<Locus>sort(loci);
	}

	//--------------------------------------------------------------------
	/**
	Iterates through the Locus objects in this set, and where any Loci overlap (share a region)
	they are added to a 'parent' Locus. This linearizes or "flattens" the Loci in this list.
	Any process calling this function should check for 'parent' Loci that have been created. This
	is accomplished by checking for {@code Locus.getType().equals(Locus.TYPE.REGION)}
	Squishing is based on an overlap of at least 1 nucleotide.
	@param	wrapAll	Set to true if all Loci should be wrapped in a parent Locus, even if they do not
	overlap with any other Loci. Set to false if only overlapping Loci should be wrapped, and non-overlapping
	Loci should be left alone.
	@throws LocusException
	*/
	public void squish(boolean wrapAll) throws LocusException
	{
		if(loci.size() == 0)
			return;

		if(hasBeenSquished)
			throw new LocusException("Error: LocusSet has already been squished");
		else
			hasBeenSquished = true;

		sortLoci();

		Locus currLocus = null;
		Locus parentLocus = null;
		ArrayList<Locus> newLocusList = new ArrayList<Locus>();
		Iterator<Locus> childIter;

		for(int lCount = 0; lCount < loci.size(); lCount++)
		{
			currLocus = loci.get(lCount);

			// at the first locus, add it to a new parent
			if(lCount == 0)
			{
				parentLocus = new Locus("LocusSet_Squished_Region", currLocus.getChromosome(), currLocus.getStart(), currLocus.getEnd());
				parentLocus.setType(Locus.TYPE.UNION);
				parentLocus.adoptChild(currLocus);
				continue;
			}

			// if the current parent overlaps with the current Locus
			if(parentLocus.compareTo(currLocus, Locus.COMPARISON_TYPE.FIXED, -1) == Locus.COMPARE.OVERLAP)
			{
				parentLocus.mergeCoords(currLocus);
				parentLocus.adoptChild(currLocus);
			}
			else
			{
				if(wrapAll)
					newLocusList.add(parentLocus);
				else
				{
					// if the parent has multiple loci, add it to the new list
					if(parentLocus.childCount() > 1)
						newLocusList.add(parentLocus);
					else // if only one Locus in the parent, just add the Locus - not the parent
					{
						childIter = parentLocus.getChildren();
						newLocusList.add(childIter.next()); // this is safe since there must always be 1 child at this point
					}
				}

				parentLocus = new Locus("LocusSet_Squished_Region", currLocus.getChromosome(), currLocus.getStart(), currLocus.getEnd());
				parentLocus.setType(Locus.TYPE.UNION);
				parentLocus.adoptChild(currLocus);
			}
		}

		// handle the last parent/locus with same logic as above
		if(wrapAll)
			newLocusList.add(parentLocus);
		else
		{
			if(parentLocus.childCount() > 1)
				newLocusList.add(parentLocus);
			else
			{
				childIter = parentLocus.getChildren();
				newLocusList.add(childIter.next()); // this is safe since there must always be 1 child at this point
			}
		}

		loci = newLocusList;
	}
	
	/**
	Iterates through the Locus objects in this set, and where any Loci overlap (share a region)
	they are added to a 'parent' Locus. This linearizes or "flattens" the Loci in this list.
	Any process calling this function should check for 'parent' Loci that have been created. This
	is accomplished by checking for {@code Locus.getType().equals(Locus.TYPE.REGION)}
	Squishing is based on an overlap of at least 1 nucleotide.
	@param the LocusSet to squish
	@param	wrapAll	Set to true if all Loci should be wrapped in a parent Locus, even if they do not
	overlap with any other Loci. Set to false if only overlapping Loci should be wrapped, and non-overlapping
	Loci should be left alone.
	@return a squished LocusSet containing either:
	1) only loci of type LocusSet_Squished_Region with children from the original LocusSet, or
	2) loci of type LocusSet_Squished_Region where multiple orginal loci overlap and simply orginal
	   where there are no overlaps.
	@throws LocusException
	*/
	public static LocusSet squish(LocusSet squishMe, boolean wrapAll) throws LocusException
	{
		LocusSet squishedSet = new LocusSet(squishMe.getName());
	
		if(squishMe.getSize() == 0)
			return squishedSet;


		List<Locus> sortedLoci = 	squishMe.getLociList();
		Collections.<Locus>sort(sortedLoci);
		Locus currLocus = null;
		Locus parentLocus = null;
		Iterator<Locus> childIter;

		for(int lCount = 0; lCount < sortedLoci.size(); lCount++)
		{
			currLocus = sortedLoci.get(lCount);

			// at the first locus, add it to a new parent
			if(lCount == 0)
			{
				parentLocus = new Locus("LocusSet_Squished_Region", currLocus.getChromosome(), currLocus.getStart(), currLocus.getEnd());
				parentLocus.setType(Locus.TYPE.UNION);
				parentLocus.adoptChild(currLocus);
				continue;
			}

			// if the current parent overlaps with the current Locus
			if(parentLocus.compareTo(currLocus, Locus.COMPARISON_TYPE.FIXED, -1) == Locus.COMPARE.OVERLAP)
			{
				parentLocus.mergeCoords(currLocus);
				parentLocus.adoptChild(currLocus);
			}
			else
			{
				if(wrapAll)
					squishedSet.addLocus(parentLocus);
				else
				{
					// if the parent has multiple loci, add it to the new list
					if(parentLocus.childCount() > 1)
						squishedSet.addLocus(parentLocus);
					else // if only one Locus in the parent, just add the Locus - not the parent
					{
						childIter = parentLocus.getChildren();
						squishedSet.addLocus(childIter.next()); // this is safe since there must always be 1 child at this point
					}
				}

				parentLocus = new Locus("LocusSet_Squished_Region", currLocus.getChromosome(), currLocus.getStart(), currLocus.getEnd());
				parentLocus.setType(Locus.TYPE.UNION);
				parentLocus.adoptChild(currLocus);
			}
		}

		// handle the last parent/locus with same logic as above
		if (wrapAll)
			squishedSet.addLocus(parentLocus);
		else
		{
			if(parentLocus.childCount() > 1)
				squishedSet.addLocus(parentLocus);
			else
			{
				childIter = parentLocus.getChildren();
				squishedSet.addLocus(childIter.next()); // this is safe since there must always be 1 child at this point
			}
		}

		return squishedSet;
	}
	

	/**
	Iterates through the Locus objects in this set, and where any Locus has children
	the children are recursively iterated through for their children and so on.
	All parent and child loci are returned in one "flat" LocusSet.
	The parent-child relationships within the loci are not affected by this.
	It simply allows referring to loci in all levels of the hierarchy homogeneously.
	@param	the LocusSet to be flattened
	@return the flattened LocusSet where children and parents are peers
	@throws LocusException
	*/	
	public static LocusSet flattenSet(LocusSet flattenMe){

		LocusSet flatSet = new LocusSet(flattenMe.getName());

		for ( int i = 0; i < flattenMe.getSize(); i++){
			liftChildren(flatSet, flattenMe.getLocusByIndex(i));
		}

		flatSet.sortLoci();

		return( flatSet );

	}
	
	/**
	 Supporting function used by flattenSet to recursively
	 trawl the children of a locus and return the locus
	 plus all its descendants in one LocusSet
	 */
	private static void liftChildren(LocusSet flatSet, Locus parentSeq )	{

		//add this sequence to the stack
		flatSet.addLocus( parentSeq );

		//run liftChildren on all of it's children
		for(int i = 0; i < parentSeq.childCount(); i++){
			liftChildren(flatSet, parentSeq.getChildByIndex(i));
		}

		return;

	}

	
	/**
	 *  get table view of locusSet
	 * HashMap -- field/annot Name -> String[] of values for entire LocusSet
	 */
	public HashMap<String, String[]> getTableView()
	{

		HashMap<String, String[]> result = new HashMap<String, String[]>();

		final int setLength = this.getSize();

		//init string arrays for fields
		String[] ids = new String[setLength];
		String[] chrs = new String[setLength];
		String[] strands = new String[setLength];
		String[] starts = new String[setLength];
		String[] ends = new String[setLength];
		String[] types = new String[setLength];
		String[] sources = new String[setLength];
		String[] scores = new String[setLength];
		
		//add field columns to result table
		result.put("id", ids);
		result.put("chromosome", chrs);
		result.put("strand", strands);
		result.put("start", starts);
		result.put("end", ends);
		result.put("type", types);
		result.put("source", sources);
		result.put("score", scores);
			
		Locus loc;
		HashMap<String, Object> annos;
		Object[] annoKeys;
		for (int i = 0; i < setLength; i++)
		{
			//add fields to table
			loc = loci.get(i);
			ids[i] = loc.getID();
			chrs[i] = loc.getChromosome();
			strands[i] = loc.getStrandShortString();
			starts[i] = Integer.toString(loc.getStart());
			ends[i] = Integer.toString(loc.getEnd());
			types[i] = loc.getType();
			sources[i] = loc.getSource();
			scores[i] = Double.toString(loc.getScore());

			// get other annotations for locus
			annos = loc.getAnnotationClone();
			annoKeys = 	(annos.keySet().toArray());
			
			for(int j = 0; j < annoKeys.length; j++)
			{
				//parse each annotation
				String key = (String)(annoKeys[j]);

				String annot;
				try{ 
					//get the string representing the annotation value
					annot = annos.get(annoKeys[j]).toString();
				}catch(Exception e){
					continue;
				}
				
				String[] annCol = result.get(key);
				
				//verify annot column exists in result table
				if (annCol == null){
					annCol = new String[setLength];
					result.put(key, annCol);
				}
				
				annCol[i] = annot;
			} //next annot
		
			
		} // next locus
		
		return result;
		
	}
	
}
