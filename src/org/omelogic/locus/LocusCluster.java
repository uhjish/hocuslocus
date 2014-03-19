/**
 * 
 */
package org.omelogic.locus;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;

/**
 * A class which arranges loci from an intersection into cluster groups based on parent set.
 * @author Frank Doyle
 *
 */
public class LocusCluster implements Serializable {
	private HashMap <String, LocusSet> clusterGroupMap;
	/**
	 * SerialVersionUID
	 */
	private static final long serialVersionUID = 6330700290028261026L;
	private int clusterId = 1;

	/**
	 * Constructor
	 */
	public LocusCluster() {
		clusterGroupMap = new HashMap <String, LocusSet>();
	}
	/**
	 * 
	 * @param inputSetName
	 * @param clusterLocus
	 * @throws LocusException
	 */
	public void addClusterMember(String inputSetName, Locus clusterLocus)throws LocusException{
		LocusSet clusterGroup = null;
		Object obj = clusterGroupMap.get(inputSetName);
		
		if(null==obj){
			clusterGroup = new LocusSet("cluster "+clusterId++);
			clusterGroupMap.put(inputSetName, clusterGroup);
		}else{
			try{
				clusterGroup = (LocusSet)obj;
			}catch(ClassCastException cce){
				throw new LocusException("Could not cast to LocusSet for cluster group retrieval.");
			}
		}
		//add the specified locus to this cluster group
		clusterGroup.addLocus(clusterLocus);
		
	}
	/**
	 * Method allows retrieval of a LocusCluster in format appropriate for 
	 * a line of file output
	 * @return
	 */
	public String getOutputLine(){
		StringBuffer result = new StringBuffer();
		Object[] keys = clusterGroupMap.keySet().toArray();
		//ensure the output of each line is in the same order by using natural ordering of keys
		Arrays.sort(keys);
		for(int i =0;i<keys.length;i++){
			LocusSet currentCluster = (LocusSet)clusterGroupMap.get(keys[i]);
			Iterator locusIterator = currentCluster.getLoci();
			while(locusIterator.hasNext()){
				Locus currentLocus = (Locus)locusIterator.next();
				result.append(currentLocus.getID());
				//if there are more, than place a comma
				if(locusIterator.hasNext()){
					result.append(",");
				}
			}
			//there are more cluster groups, insert a tab delimiter
			if(i<keys.length){
				result.append("\t");
			}
		}
		return result.toString();
		
	}
	@Override
	public String toString() {
		StringBuffer result = new StringBuffer();
		result.append("LocusCluster:\n\t");
		result.append(this.getOutputLine());
		return result.toString();
	}
	

}
