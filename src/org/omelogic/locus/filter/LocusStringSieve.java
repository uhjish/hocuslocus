/*
 *      LocusStringSieve.java
 *      
 *      Copyright 2008 Ajish D. George <ajish@hocuslocus.com>
 *      
 */
package org.omelogic.locus.filter;


import org.omelogic.locus.*;
import java.util.*;
import java.util.regex.*;
import java.io.Serializable;


public class LocusStringSieve extends LocusSieve
{
	//defines the strings that will have to be retrieved from outside the annotation map
	public static final String[] DEFINED_STRINGS = { "ID","TYPE","STRAND","CHROMOSOME","SOURCE" };
	
	private interface StringSieve extends java.io.Serializable
	{	
		public String getAnnotation( Locus loc );
	}

	private String annotation;
	private Pattern expression;
	private boolean inclusion;
	private StringSieve filter;
	
	public LocusStringSieve( String anno, Pattern expr, boolean incl )
	{
		annotation = anno;
		expression = expr;
		inclusion = incl;
		if ( anno.equals("ID") ){
			filter = new StringSieve(){
				public String getAnnotation( Locus loc)
				{
					return loc.getID();
				}
			};
			return;
		}
		if ( anno.equals("TYPE") ){
			filter = new StringSieve(){
				public String getAnnotation( Locus loc)
				{
					return loc.getType();
				}				
			};
			return;
		}
		if ( anno.equals("STRAND") ){
			filter = new StringSieve(){
				public String getAnnotation( Locus loc)
				{
					return loc.getStrandShortString();
				}
			};
			return;
		}
		if ( anno.equals("CHROMOSOME") ){
			filter = new StringSieve(){
				public String getAnnotation( Locus loc)
				{
					return loc.getChromosome();
				}
			};
			return;
		}
		if ( anno.equals("SOURCE") ){
			filter = new StringSieve(){
				public String getAnnotation( Locus loc)
				{
					return loc.getSource();
				}
			};
			return;
		}
		
		//default Sieve
		filter = new StringSieve(){
			public String getAnnotation( Locus loc ){
				
				return (String)loc.getAnnotation(annotation);
			}
		};
	
	}
	
	public boolean accept(Locus locus) throws Exception
	{
		String term = filter.getAnnotation(locus);
		boolean matched =  expression.matcher(term).matches();
		if (inclusion){
			return matched;
		}else{
			return (!matched);
		}
	}
	
	public String getShortDescription()
	{
		String eq = inclusion ? " = " :" != ";

		String desc = annotation + eq + "'" + expression.pattern() + "'";
		
		return desc;
	}

		

}
