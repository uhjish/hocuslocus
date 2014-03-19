package org.omelogic.locus.filter;

import java.util.*;
import org.omelogic.locus.*;

/**
A tool to filter Locus' children and LocusSets for a given set of criteria

@author	Chris Zaleski
*/

public class LocusFilter
{
	// holds the FilterCriteria objects
	private ArrayList<FilterCriteria> filterCriteria;
	// global counter used in the filterCount function's recursive call
	private int globalCounter;

	//--------------------------------------------------------------------

	/**
	Default constructor
	*/
	public LocusFilter()
	{
		filterCriteria = new ArrayList<FilterCriteria>();
		globalCounter = 0;
	}


	//--------------------------------------------------------------------
	/**
	Adds a FilterCriteria to be used by the filter
	@param	criteria	the criteria object to be added
	*/
	public void addFilterCriteria(FilterCriteria criteria)
	{
		filterCriteria.add(criteria);
	}

	//--------------------------------------------------------------------
	/**
	Filters a Locus object's children based on the current criteria
	@param	locus	the Locus to filter
	@param	recurseChildren	set to true if the Locus' children should be recursively filtered. Set to false if
	only the first-generation children should be filtered.
	@return	a new ArrayList containing references to the child loci which passed the filtering
	*/
	public ArrayList<Locus> filter(Locus locus, boolean recurseChildren)
	{
		ArrayList<Locus> filteredList = new ArrayList<Locus>();

		if(locus == null)
			return filteredList;

		if(recurseChildren)
		{
			filterChildren(locus, filteredList);
		}
		else
		{
			Locus currLocus;
			Iterator<Locus> iter = locus.getChildren();
			while(iter.hasNext())
			{
				currLocus = iter.next();
				if(passesAllCriteria(currLocus))
					filteredList.add(currLocus);
			}
		}

		return filteredList;
	}

	//--------------------------------------------------------------------
	// Function for recursively checking all a Locus' children
	// Maintains an ArrayList for the successful Loci
	private void filterChildren(Locus locus, ArrayList<Locus> filteredList)
	{
		Iterator<Locus> iter;
		Locus nextLocus;

		if(locus.childCount() > 0)
		{
			iter = locus.getChildren();
			while(iter.hasNext())
			{
				nextLocus = iter.next();
				filterChildren(nextLocus, filteredList);
			}
		}
		else
		{
			if(passesAllCriteria(locus))
				filteredList.add(locus);
		}
	}

	//--------------------------------------------------------------------
	/**
	Filters a Locus object's children based on the current criteria
	@param	locus	the Locus to filter
	@param	recurseChildren	set to true if the Locus' children should be recursively filtered. Set to false if
	only the first-generation children should be filtered.
	@return	the number of children which passed the filter criteria
	*/
	public int filterCount(Locus locus, boolean recurseChildren)
	{
		int currCount = globalCounter = 0;

		if(locus == null)
			return currCount;

		if(recurseChildren)
		{
			filterChildrenCount(locus);
		}
		else
		{
			Iterator<Locus> iter = locus.getChildren();
			Locus currLocus;
			while(iter.hasNext())
			{
				currLocus = iter.next();
				if(passesAllCriteria(currLocus))
					currCount++;
			}
		}

		currCount = globalCounter;
		globalCounter = 0;
		return currCount;
	}

	//--------------------------------------------------------------------
	// Function for recursively checking all a Locus' children
	// Updates the globalCount variable to track successful Loci
	private void filterChildrenCount(Locus locus)
	{
		Iterator<Locus> iter;
		Locus nextLocus;

		if(locus.childCount() > 0)
		{
			iter = locus.getChildren();
			while(iter.hasNext())
			{
				nextLocus = iter.next();
				filterChildrenCount(nextLocus);
			}
		}
		else
		{
			if(passesAllCriteria(locus))
				globalCounter++;;
		}
	}

	//--------------------------------------------------------------------
	/**
	Filters the Locus objects in a LocusSet based on the current criteria
	@param	locusSet	the LocusSet to filter
	@param	recurseChildren	set to true if the children in the LocusSet's direct Loci should be recursively filtered.
	Set to false if only the LocusSet's direct Loci should be filtered.
	@return	a new ArrayList containing references to the loci which passed the filtering
	*/
	public ArrayList<Locus> filterSet(LocusSet locusSet, boolean recurseChildren)
	{
		ArrayList<Locus> filteredList = new ArrayList<Locus>();

		if(locusSet == null)
			return filteredList;

		Locus currLocus;
		Iterator<Locus> iter = locusSet.getLoci();

		if(recurseChildren)
		{
			while(iter.hasNext())
			{
				currLocus = iter.next();
				filterChildren(currLocus, filteredList);
			}
		}
		else
		{
			while(iter.hasNext())
			{
				currLocus = iter.next();
				if(!passesAllCriteria(currLocus))
					filteredList.add(currLocus);
			}
		}

		return filteredList;
	}

	//--------------------------------------------------------------------
	// Checks a Locus to see if it passes all criteria
	private boolean passesAllCriteria(Locus l)
	{
		FilterCriteria criteria;

		for(int i = 0; i < filterCriteria.size(); i ++)
		{
		    criteria = filterCriteria.get(i);
		    if(!criteria.passes(l))
			    return false;
		}
		return true;
	}
}
