/*
 *      LocusValueSieve.java
 *      
 *      Copyright 2008 Ajish D. George <ajish@hocuslocus.com>
 *      
 */
package org.omelogic.locus.filter;

import org.omelogic.locus.*;

import java.util.*;
import java.util.regex.*;
import java.io.Serializable;


public class LocusNumericSieve extends LocusSieve
{
	//defines the values that will have to be retrieved from outside the annotation map
	public static final String[] DEFINED_VALUES = { "START","END","SCORE" };
	
	private interface NumericSieve extends java.io.Serializable
	{	
		public double getAnnotation( Locus loc ) throws Exception;
	}

	private String annotation;
	private double lower;
	private double upper;
	private boolean inclusion;
	private NumericSieve filter;
	
	public LocusNumericSieve( String anno, double lhs, double rhs, boolean incl ) throws Exception
	{
		if (lhs > rhs){
			throw new Exception("Invalid LocusNumericSieve: lhs > rhs!");
		}
		
		annotation = anno;
		lower = lhs;
		upper = rhs;
		inclusion = incl;
		
		if ( anno.equals("START") ){
			filter = new NumericSieve(){
				public double getAnnotation( Locus loc)  throws Exception
				{
					return (double)loc.getStart();
				}
			};
			return;
		}
		if ( anno.equals("END") ){
			filter = new NumericSieve(){
				public double getAnnotation( Locus loc) throws Exception
				{
					return (double)loc.getEnd();
				}
			};
			return;
		}
		if ( anno.equals("SCORE") ){
			filter = new NumericSieve(){
				public double getAnnotation( Locus loc) throws Exception
				{
					return (double)loc.getScore();
				}
			};
			return;
		}
		
		//default Sieve
		filter = new NumericSieve(){
			public double getAnnotation( Locus loc ) throws Exception{
				double val;
				try{
					val = ((Number)loc.getAnnotation(annotation)).doubleValue();
				}catch(Exception f){
					throw new Exception("LocusNumericSieve.NumericSieve[default]: Cannot cast annotation in: "+annotation+" to class Number!");
				}
				return val;
			}
		};
	
	}
	
	public boolean accept(Locus locus) throws Exception
	{
		double value;
		try{
			value = filter.getAnnotation(locus);
		}catch(Exception e){
			throw new Exception("LocusNumericSieve.accept(locus): Cannot get double value at locus! " + e.toString());
		}
		if (inclusion){
			return (value >= lower && value <= upper);
		}else{
			return (value <= lower || value >= upper);
		}
	}
	
	public String getShortDescription()
	{
		String eq = inclusion ? " = " :" != ";

		String desc = annotation + eq + "[" + lower + "," + upper + "]";
		
		return desc;
	}


}
