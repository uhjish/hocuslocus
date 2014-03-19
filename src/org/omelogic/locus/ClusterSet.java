/**
 * 
 */
package org.omelogic.locus;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Holds a List of LocusCluster objects representing groups of loci from each set that are 
 * related by intersection with other sets.  Based on an intersection result LocusSet, it 
 * differs just by it's logical grouping of constituent loci for cluster analysis
 *  
 * @author Frank Doyle
 *
 */
public class ClusterSet implements Serializable {
	private ArrayList<LocusCluster> clusters;
	private String setName;
	/**
	 * SerialVersionUID
	 */
	private static final long serialVersionUID = -7018820178999435761L;

	/**
	 * @param	name	a String representing a name/identifier for this ClusterSet
	 */
	public ClusterSet(String name) {
		setName = name;
		clusters = new ArrayList<LocusCluster>();
	}
	/**
	 * Creates a new LocusCluster object, places it in the clusters list and returns a 
	 * reference to calling code.
	 * @return
	 */
	public LocusCluster createNewCluster(){
		LocusCluster newCluster = new LocusCluster();
		clusters.add(newCluster);
		return newCluster;
	}
	//--------------------------------------------------------------------
	/**
	Returns an {@code Iterator} over the LocusClusters of this ClusterSet.
	@return	the iterator. No elements may exist if there are no contained loci.
	@see java.util.Iterator
	*/
	public Iterator<LocusCluster> getClusters()
	{
		return clusters.iterator();
	}
	//--------------------------------------------------------------------
	/**
	Returns the name of this ClusterSet.
	@return	the name.
	*/
	public String getName()
	{
		return setName;
	}

	//--------------------------------------------------------------------
	/**
	Set the name of this ClusterSet.
	@param	newName	the new name for the set
	*/
	public void setName(String newName)
	{
		setName = newName;
	}	

}
