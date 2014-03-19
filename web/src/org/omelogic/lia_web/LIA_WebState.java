package org.omelogic.lia_web;

import java.util.ArrayList;
import org.omelogic.locus.*;

public class LIA_WebState
{
	private int comparisonType;
	private float comparisonValue;
	private int comparisonStrand;
	private boolean bridging;
	private ArrayList<LocusSet> posSets;
	private ArrayList<LocusSet> negSets;
	private String genome;
	
	private boolean liaComplete;
	
	private String message;
	private String errorMessage;
	
	//------------------------------------
	public LIA_WebState()
	{
		comparisonType = Locus.COMPARISON_TYPE.FIXED;
		comparisonValue = -1;
		comparisonStrand = Locus.COMPARISON_STRAND.NEUTRAL;
		bridging = false;
		posSets = new ArrayList<LocusSet>();
		negSets = new ArrayList<LocusSet>();
		genome = "";
		liaComplete = false;
		message = "";
		errorMessage = "";
	}
	
	//------------------------------------
	public int getComparisonType()
	{
		return comparisonType;
	}
	
	//------------------------------------
	public void setComparisonType(int comparisonType)
	{
		this.comparisonType = comparisonType;
	}
	
	//------------------------------------
	public float getComparisonValue()
	{
		return comparisonValue;
	}
	
	//------------------------------------
	public void setComparisonValue(float comparisonValue)
	{
		this.comparisonValue = comparisonValue;
	}
	
	//------------------------------------
	public int getComparisonStrand()
	{
		return comparisonStrand;
	}
	
	//------------------------------------
	public void setComparisonStrand(int comparisonStrand)
	{
		this.comparisonStrand = comparisonStrand;
	}
	
	//------------------------------------
	public boolean getBridging()
	{
		return bridging;
	}
	
	//------------------------------------
	public void setBridging(boolean bridging)
	{
		this.bridging = bridging;
	}
	
	//------------------------------------
	public ArrayList<LocusSet> getPosSets()
	{
		return posSets;
	}
	
	//------------------------------------
	public void addPosSet(LocusSet set)
	{
		posSets.add(set);
	}
	
	//------------------------------------
	public void removePosSet(int setIndex)
	{
		posSets.remove(setIndex);
	}
	
	//------------------------------------
	public ArrayList<LocusSet> getNegSets()
	{
		return negSets;
	}
	
	//------------------------------------
	public void addNegSet(LocusSet set)
	{
		negSets.add(set);
	}
	
	//------------------------------------
	public void removeNegSet(int setIndex)
	{
		negSets.remove(setIndex);
	}
	
	//------------------------------------
	public void clearSets()
	{
		posSets.clear();
		negSets.clear();
	}
	
	//------------------------------------
	public String getGenome()
	{
		return genome;
	}
	
	//------------------------------------
	public void setGenome(String genome)
	{
		this.genome = genome;
	}
	
	//------------------------------------
	public boolean isLiaComplete()
	{
		return liaComplete;
	}
	
	//------------------------------------
	public void setLiaComplete(boolean complete)
	{
		liaComplete = complete;
	}
	
	//------------------------------------
	public String getMessage()
	{
		return message;
	}
	
	//------------------------------------
	public void setMessage(String message)
	{
		this.message = message;
	}
	
	//------------------------------------
	public void addMessage(String message)
	{
		this.message = this.message + message;
	}
	
	//------------------------------------
	public String getErrorMessage()
	{
		return errorMessage;
	}
	
	//------------------------------------
	public void setErrorMessage(String message)
	{
		errorMessage = message;
	}
}
