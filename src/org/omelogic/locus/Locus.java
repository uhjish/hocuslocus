package org.omelogic.locus;

import java.util.*;
import java.io.Serializable;

/**
A class representing the base data structure for Hocus Locus coordinate data.
A Locus can hold a pre-defined set of parameters such as {@code chromosome, start, end}, etc.,
as well as any user defined paramters via the {@code addAnnotation} and {@code getAnnotation} functions.
A Locus can also hold any number of 'child' Locus objects which will automatically track their 'parent'.

@author	Chris Zaleski
*/

public class Locus implements Comparable<Locus>, java.io.Serializable
{
	/**
	A type safe class to declare enumerated constants for Locus types.
	Note: Locus types are not validated - it can be set to anything. These are just some useful
	predefined global values.
	*/
	public final class TYPE
	{
		private TYPE(){}
		/**
		Undefined type
		*/
		public static final String UNDEFINED = "Undefined";
		/**
		Used when multiple loci require a 'wrapper' parent
		*/
		public static final String UNION = "Union";
		/**
		A Locus that represents the intersection of other Loci
		*/
		public static final String INTERSECTION = "Intersection";
		/**
		A genomic region with no special defining annotation 'name'...
		it's just the locus. (eg: repeat region, methylated region, etc)
		*/
		public static final String LOCUS = "Locus";
		/**
		A gene plus its immediate upstream promoter region
		*/
		public static final String OPERON = "Operon";
		/**
		An immediate upstream promoter region
		*/
		public static final String PROMOTER = "Promoter";		
		/**
		A gene, from transcription start to transcription end
		*/
		public static final String GENE = "Gene";
		/**
		The coding region of a gene
		*/
		public static final String CDS = "CDS";
		/**
		5' UTR region of a gene
		*/
		public static final String FIVE_UTR = "5UTR";
		/**
		3' UTR region of a gene
		*/
		public static final String THREE_UTR = "3UTR";
		/**
		An exon of a gene
		*/
		public static final String EXON = "Exon";
		/**
		An intron of a gene
		*/
		public static final String INTRON = "Intron";
		/**
		An mRNA, from transcription start to transcription end
		*/
		public static final String MRNA = "mRNA";
		/**
		An mRNA alignment block
		*/
		public static final String BLOCK = "Block";

		//+++++++++++++++++++++++++
		// New generic Locus types
		/**
		Complete DNA region from transcriptional start site to
		transcriptional end site. Replaces 'gene'.
		*/
		public static final String TRANSCRIPTIONAL_REGION = "Transcriptional Region";

		/**
		A unit of transcription which exists in the mature transcript.
		Replaces 'exon'.
		*/
		public static final String TRANSCRIPTIONAL_UNIT = "Transcriptional Unit";

		/**
		An area of transcription *or* a non-transcribed intervening region
		which does not exist in the mature transcript.
		Replaces 'intron'.
		*/
		public static final String INTERVENING_UNIT = "Intervening Unit";
	}

	/**
	A type safe class to declare enumerated constants for Locus strandedness.
	*/
	public final class STRAND
	{
		private STRAND(){}
		/**
		Strand type - undefined
		*/
		public static final int UNDEFINED = 0;
		/**
		Strand type - positive strand
		*/
		public static final int POSITIVE = 1;
		/**
		Strand type - negative strand
		*/
		public static final int NEGATIVE = 2;
	}

	/**
	A type safe class to declare enumerated constants for Locus comparison results.
	*/
	public final class COMPARE
	{
		private COMPARE(){}
		/**
		This Locus overlaps the compared Locus
		*/
		public static final int OVERLAP = 0;
		/**
		This Locus is before the compared Locus
		*/
		public static final int BEFORE = -1;
		/**
		This Locus is after the compared Locus
		*/
		public static final int AFTER = 1;
	}

	/**
	A type safe class to declare enumerated constants for Locus comparison types.
	*/
	public final class COMPARISON_TYPE
	{
		private COMPARISON_TYPE(){}
		/**
		Locus objects should be compared using a fixed number of nucleotides
		*/
		public static final int FIXED = 0;
		/**
		Locus objects should be compared using a percentage of nucleotide overlap.
		Percentage is based on the smaller of the two Loci
		*/
		public static final int PERCENT = 1;
	}
	/**
	String descriptions of each option
	*/
	public static final String[] COMPARISON_TYPE_DESCRIPTIONS = {"FIXED","PERCENT"};

	/**
	A type safe class to declare enumerated constants for Locus strand comparison types.
	*/
	public final class COMPARISON_STRAND
	{
		private COMPARISON_STRAND(){}
		/**
		Locus objects should be compared without attention to strand.
		*/
		public static final int NEUTRAL = 0;
		/**
		Locus objects should be compared using matching strand value.
		If either Locus has an undefined strand, the result is false.
		*/
		public static final int MATCH_STRICT = 1;
		/**
		Locus objects should be compared using matching strand value.
		If either Locus has an undefined strand, the result is true.
		*/
		public static final int MATCH_PERMISSIVE = 2;
		/**
		Locus objects should be compared using complementary strand value.
		If either Locus has an undefined strand, the result is false.
		*/
		public static final int COMPLEMENT_STRICT = 3;
		/**
		Locus objects should be compared using complementary strand value.
		If either Locus has an undefined strand, the result is true.
		*/
		public static final int COMPLEMENT_PERMISSIVE = 4;
	}
	/**
	String descriptions of each option
	*/		
	public static final String[] COMPARISON_STRAND_DESCRIPTIONS = {"NEUTRAL","MATCH_STRICT","MATCH_PERMISSIVE","COMPLEMENT_STRICT","COMPLEMENT_PERMISSIVE"};

	private String id;
	private String chromosome;
	private int start;
	private int end;
	private String type;
	private String source;
	private double score;
	private int strand;
	private String sequence;
	private String setName;

	private Locus parent;
	private ArrayList<Locus> kids;
	private Map<String, Object> annotation;

	//--------------------------------------------------------------------
	/**
	Constructor with minimum parameters
	@param	id	the name/id/accession/etc, of this Locus.
	@param	chrom	the chromosome to which this Locus belongs.
	@param	start	the start position of this Locus.
	@param	end	the end position of this Locus.
	*/
	public Locus(String id, String chrom, int start, int end)
	{
		this.id = id;
		chromosome = chrom;
		this.start = start;
		this.end = end;

		type = TYPE.UNDEFINED;
		source = null;
		score = 0;
		strand = STRAND.UNDEFINED;
		sequence = null;
		setName = null;

		parent = null;
		kids = null;
		annotation = null;
	}

	//--------------------------------------------------------------------
	/**
	Constructor with extended parameters
	@param	id	the name/id/accession/etc, of this Locus.
	@param	chrom	the chromosome to which this Locus belongs.
	@param	start	the start position of this Locus.
	@param	end	the end position of this Locus.
	@param	strand	the strand of this Locus.
	@param	type	the type of this locus ex: gene, 5utr.
	@param	source	the source of this locus ex: knownGene.
	*/
	public Locus(String id, String chrom, int start, int end, int strand, String type, String source){
		this.id = id;
		chromosome = chrom;
		this.start = start;
		this.end = end;
		this.strand = strand;
		this.type = type;
		this.source = source;

		score = 0;
		sequence = null;
		setName = null;

		parent = null;
		kids = null;
		annotation = null;
	}

	//--------------------------------------------------------------------
	/**
	Constructor to clone an existing Locus.
	Note: This is a shallow copy only. Specifically, the children Loci (ArrayList)
	and annotation (HashMap) are copied according to their respective clone() methods.
	@param	oldLocus	the Locus to be cloned
	*/
	public Locus(Locus oldLocus){
		this.id = oldLocus.getID();
		this.chromosome = oldLocus.getChromosome();
		this.start = oldLocus.getStart();
		this.end = oldLocus.getEnd();
		this.strand = oldLocus.getStrand();
		this.type = oldLocus.getType();
		this.source = oldLocus.getSource();
		this.score = oldLocus.getScore();
		
		this.setName = oldLocus.getSetName();

		parent = oldLocus.getParent();
		kids = oldLocus.getChildrenClone();
		annotation = oldLocus.getAnnotationClone();
	}

	//--------------------------------------------------------------------
	/**
	Returns the id of this Locus.
	@return	the id.
	*/
	public String getID()
	{
		return id;
	}

	//--------------------------------------------------------------------
	/**
	Sets the ID of this Locus.
	@param	id	the ID value.
	*/
	public void setID(String id)
	{
		this.id = id;
	}

	//--------------------------------------------------------------------
	/**
	Returns the chromosome of this Locus.
	@return	the chromosome.
	*/
	public String getChromosome()
	{
		return chromosome;
	}

	//--------------------------------------------------------------------
	/**
	Sets the chromosome of this Locus.
	@param	chrom	the chromosome value.
	*/
	public void setChromosome(String chrom)
	{
		chromosome = chrom;
	}

	//--------------------------------------------------------------------
	/**
	Returns the start position of this Locus.
	@return	the start position.
	*/
	public int getStart()
	{
		return start;
	}

	//--------------------------------------------------------------------
	/**
	Sets the start position of this Locus.
	@param	start	the start position value.
	*/
	public void setStart(int start)
	{
		this.start = start;
	}

	//--------------------------------------------------------------------
	/**
	Returns the end position of this Locus.
	@return	the end position.
	*/
	public int getEnd()
	{
		return end;
	}

	//--------------------------------------------------------------------
	/**
	Sets the end position of this Locus.
	@param	end	the end position value.
	*/
	public void setEnd(int end)
	{
		this.end = end;
	}

	//--------------------------------------------------------------------
	/**
	Returns the length of this Locus.
	@return	the length of the Locus
	*/
	public int getLength()
	{
		return (int)(this.end-this.start+1);
	}

	//--------------------------------------------------------------------
	/**
	Returns the type of this Locus.
	@return	the type as a String, or {@code null} if source is undefined.
	*/
	public String getType()
	{
		return type;
	}

	//--------------------------------------------------------------------
	/**
	Sets the type of this Locus.
	@param	type	the type value.
	*/
	public void setType(String type)
	{
		this.type = type;
	}

	//--------------------------------------------------------------------
	/**
	Returns the source of this Locus.
	@return	the source as a String, or {@code null} if source is undefined.
	*/
	public String getSource()
	{
		return source;
	}

	//--------------------------------------------------------------------
	/**
	Sets the source of this Locus.
	@param	source	the source value.
	*/
	public void setSource(String source)
	{
		this.source = source;
	}

	//--------------------------------------------------------------------
	/**
	Returns the score of this Locus.
	@return	the score. A default value of zero is returned if the score was never set.
	*/
	public double getScore()
	{
		return score;
	}

	//--------------------------------------------------------------------
	/**
	Sets the score of this Locus.
	@param	score	the score value.
	*/
	public void setScore(double score)
	{
		this.score = score;
	}

	//--------------------------------------------------------------------
	/**
	Returns the constant value representing the strand of this Locus.
	Values are enumerated in the {@code Locus.STRAND} class.
	@return	the strand value.
	*/
	public int getStrand()
	{
		return strand;
	}

	//--------------------------------------------------------------------
	/**
	Returns the constant value representing the strand of this Locus.
	Values are enumerated in the {@code Locus.STRAND} class.
	@return	the strand value.
	*/
	public String getStrandShortString()
	{
		String result = "";

		switch( this.strand )
		{
			case Locus.STRAND.UNDEFINED:
				result = ".";
				break;
			case Locus.STRAND.POSITIVE:
				result = "+";
				break;
			case Locus.STRAND.NEGATIVE:
				result = "-";
				break;
		}

		return result;

	}

	//--------------------------------------------------------------------
	/**
	Sets the constant value representing the strand of this Locus.
	Valid values are enumerated in the {@code Locus.STRAND} class.
	@param	strand	the strand value.
	*/
	public void setStrand(int strand)
	{
		this.strand = strand;
	}

	//--------------------------------------------------------------------
	/**
	Returns the sequence of this Locus.
	@return	the sequence as a String, or {@code null} if sequence is undefined.
	*/
	public String getSequence()
	{
		return sequence;
	}

	//--------------------------------------------------------------------
	/**
	Sets the sequence of this Locus.
	@param	sequence	the sequence value.
	*/
	public void setSequence(String sequence)
	{
		this.sequence = sequence;
	}
	
	//--------------------------------------------------------------------
	/**
	Returns the set name (most likely the LocusSet name) of this Locus.
	@return	the name.
	*/
	public String getSetName()
	{
		return setName;
	}

	//--------------------------------------------------------------------
	/**
	Sets the set name (most likely the LocusSet name) of this Locus.
	@param	setName	the name value.
	*/
	public void setSetName(String setName)
	{
		this.setName = setName;
	}

	//--------------------------------------------------------------------
	/**
	Returns the parent Locus of this Locus, if it exists.
	@return	the parent Locus, or {@code null} if it does not exist.
	*/
	public Locus getParent()
	{
		return parent;
	}

	//--------------------------------------------------------------------
	// Defined as protected so that the parent is only set when calling "addChild"
	protected void setParent(Locus parent)
	{
		this.parent = parent;
	}

	//--------------------------------------------------------------------
	/**
	Returns an {@code Iterator} over the child Loci of this Locus.
	Note: The child loci may contain more children. This method is <i>not</i> recursive.
	@return	the iterator. No elements may exist if there are no children.
	@see java.util.Iterator
	*/
	public Iterator<Locus> getChildren()
	{
		if(kids == null)
			kids = new ArrayList<Locus>();

		return kids.iterator();
	}

	//--------------------------------------------------------------------
	/**
	Returns the child Locus at a specific index in the ArrayList.
	@param	index	the index of the Locus being requested.
	@return	the child Locus at {@code index}
	*/
	public Locus getChildByIndex(int index) throws IndexOutOfBoundsException
	{
		if(kids == null)
			kids = new ArrayList<Locus>();

		return kids.get(index);
	}

	//--------------------------------------------------------------------
	/**
	Returns the number of first generation children contained by this Locus.
	Note: The child loci may contain more children. This method is <i>not</i> recursive.
	@return	the number of children loci.
	*/
	public int childCount()
	{
		if(kids == null)
			return 0;
		else
			return kids.size();
	}

	//--------------------------------------------------------------------
	/**
	Returns an {@code ArrayList} referencing the children Locus objects.
	The ArrayList returned is defined by the {@code ArrayList.clone()} method.
	This method is likely to be used primarily for the "clone" contructor
	@return	the ArrayList.
	@see java.util.ArrayList
	*/
	public ArrayList<Locus> getChildrenClone()
	{
		if(kids == null)
			kids = new ArrayList<Locus>();
		//an ArrayList clone is a shallow copy
		//return (ArrayList)kids.clone();
		//in order to satisfy generics type requirements, use new ArrayList<T>(Collection)
		return new ArrayList<Locus>(kids);
	}

	//--------------------------------------------------------------------
	/**
	Adds a child Locus to this Locus
	@param	child	the Locus to be added as a child to this Locus.
	*/
	public void addChild(Locus child) throws LocusException
	{
		if(kids == null)
			kids = new ArrayList<Locus>();

		if(!this.chromosome.equals(child.getChromosome()) || this.start > child.getStart() || this.end < child.getEnd())
		{
			throw new LocusException("Error: child Locus is not within this Locus' scope");
		}

		child.setParent(this);
		kids.add(child);
	}
	/**
	Adds a child Locus to this Locus without making it the parent
	@param	child	the Locus to be added as a child to this Locus.
	*/
	public void adoptChild(Locus child) throws LocusException
	{
		if(kids == null)
			kids = new ArrayList<Locus>();

		if(!this.chromosome.equals(child.getChromosome()) || this.start > child.getStart() || this.end < child.getEnd())
		{
			throw new LocusException("Error: child Locus is not within this Locus' scope");
		}

		kids.add(child);
	}
	//--------------------------------------------------------------------

	/**
	Spawns a child from this locus of given type
	@param	spawnStart of subLocus spawn
	@param	spawnEnd of subLocus spawn
	@param	spawnType  of subLocus spawn
	*/
	public void spawnChild(int spawnStart, int spawnEnd, String spawnType) throws LocusException
	{
			Locus spawn = new Locus(spawnType.concat(id), chromosome, spawnStart, spawnEnd, strand, type, source) ;
			this.addChild(spawn);
	}
	//--------------------------------------------------------------------
	/**
	Sets the entire annotation object{@code Map} for this locus.
	@param	the Map to use as this locus' annotation.
	*/
	public void setAnnotation(Map<String, Object> annotation)
	{
		this.annotation = annotation;

	}

	//--------------------------------------------------------------------
	/**
	Adds a user-defined annotation to this Locus as a key-value pair
	@param	key	a String representing the key
	@param	value	an Object representing the value
	*/
	public void addAnnotation(String key, Object value)
	{
		if(annotation == null)
			annotation = new HashMap<String, Object>();

		annotation.put(key, value);
	}

	//--------------------------------------------------------------------
	/**
	Returns an {@code Object} representing the value of the corresponding key.
	@return	the Object, or null if the key does not exist.
	*/
	public Object getAnnotation(String key)
	{
		if(annotation == null)
			annotation = new HashMap<String, Object>();

		return annotation.get(key);
	}

	//--------------------------------------------------------------------
	/**
	Returns boolean representing the existance of this annotation.
	@return	true if the Locus contains this annotation. False otherwise
	*/
	public boolean containsAnnotation(String key)
	{
		if(annotation == null)
			annotation = new HashMap<String, Object>();

		return annotation.containsKey(key);
	}
	
	//--------------------------------------------------------------------
	/**
	Returns an {@code Iterator<String>} over the keys in the {@code HashMap} of annotations
	@return	the Iterator<String> of keys.
	*/
	public Iterator<String> getAnnotationKeys()
	{
		if(annotation == null)
			annotation = new HashMap<String, Object>();

		return annotation.keySet().iterator();
	}


	//--------------------------------------------------------------------
	/**
	Returns a {@code HashMap} referencing the annotation key-values.
	The HashMap returned is defined by the {@code HashMap.clone()} method.
	This method is likely to be used primarily for the "clone" contructor
	@return	the HashMap.
	@see java.util.HashMap
	*/
	public HashMap<String, Object> getAnnotationClone()
	{
		if(annotation == null)
			annotation = new HashMap<String, Object>();
		//HashMap.clone() only returns a shallow copy, so to
		//satisy type requirments of genrics syntx, we now use new HashMap<T, U>(Map)
		//return (HashMap)annotation.clone();
		return new HashMap<String, Object>(annotation);
	}


	//--------------------------------------------------------------------
	/**
	Function used for sorting Locus objects. Satisfies the Comparable requirement (generics compatible).
	Note: Only the chromosome and start position are considered - end is ignored.
	Note: This is a shallow comparison only. Child loci are not traversed
	Chromosome values are compared lexicographically as per String.compareTo(String)
	@param testLocus	the object to compare against, which in this case must be a Locus
	@return	Returns a positive, negative, or zero value as described in java.lang.Comparable
	@throws ClassCastException if the Object parameter is not found, or if no valid compare case is found
	@see java.lang.Comparable
	@see java.lang.String
	*/
	public int compareTo(Locus testLocus) throws ClassCastException
	{

		if(this.chromosome.compareTo(testLocus.getChromosome()) == 0 && this.start == testLocus.getStart())
			return 0;
		else if(this.chromosome.compareTo(testLocus.getChromosome()) == 0 && this.start > testLocus.getStart())
			return 1;
		else if(this.chromosome.compareTo(testLocus.getChromosome()) == 0 && this.start < testLocus.getStart())
			return -1;
		else if(this.chromosome.compareTo(testLocus.getChromosome()) > 0)
			return 1;
		else if(this.chromosome.compareTo(testLocus.getChromosome()) < 0)
			return -1;
		else
		{
			throw new ClassCastException("Error: Locus.compareTo(Object) - No valid comparison case found");
		}
	}
	
	//--------------------------------------------------------------------
	/**
	Function used for sorting Locus objects. Satisfies the Comparable requirement (generics compatible).
	Note: Only the chromosome and start position are considered - end is ignored.
	Note: This is a shallow comparison only. Child loci are not traversed
	Chromosome values are compared lexicographically as per String.compareTo(String)
	@param testLocus	the object to compare against, which in this case must be a Locus
	@return	Returns a positive, negative, or zero value as described in java.lang.Comparable
	@throws ClassCastException if the Object parameter is not found, or if no valid compare case is found
	@see java.lang.Comparable
	@see java.lang.String
	*/
	public final int overlapCompareTo(Locus testLocus) throws ClassCastException
	{

		if (this.chromosome.compareTo(testLocus.getChromosome()) == 0){
			if (this.end < testLocus.getStart()){
				return -1;
			}else if (this.start > testLocus.getEnd()){
				return 1;
			}else{
				return 0;
			} 
		}else if(this.chromosome.compareTo(testLocus.getChromosome()) < 0){
			return -1;
		}else if(this.chromosome.compareTo(testLocus.getChromosome()) > 0){
			return 1;
		}
	

		if(this.chromosome.compareTo(testLocus.getChromosome()) == 0 && this.start == testLocus.getStart())
			return 0;
		else if(this.chromosome.compareTo(testLocus.getChromosome()) == 0 && this.start > testLocus.getStart())
			return 1;
		else if(this.chromosome.compareTo(testLocus.getChromosome()) == 0 && this.start < testLocus.getStart())
			return -1;
		else if(this.chromosome.compareTo(testLocus.getChromosome()) > 0)
			return 1;
		else if(this.chromosome.compareTo(testLocus.getChromosome()) < 0)
			return -1;
		else
		{
			throw new ClassCastException("Error: Locus.compareTo(Object) - No valid comparison case found");
		}
	}

	//--------------------------------------------------------------------
	/**
	Overloaded compareTo used for individual locus comparisons.
	Uses comparison criteria - {@code comparisonType} and {@code comparisonValue} to determine if this locus
	'overlaps' (shares a common region) with another locus.
	Note: This is a shallow comparison only. Child loci are not traversed
	@param	testLocus the Locus to compare against
	@param	comparisonType	the type of comparison to be performed, as defined in Locus.COMPARISON_TYPE
	@param	comparisonValue	The value to be used for this comparison. If the comparisonType is
	COMPARISON_TYPE.FIXED - the value is converted to an integer, a negative value means 'overlap' and a
	positive value means 'gap'. If the comparisonType is COMPARISON_TYPE.PERCENT, only the 'overlap'
	concept is used, the value (x) must be 0 < x <= 1, and the smaller Locus is used to define
	the discrete value.
	@return	Returns a positive, negative, or zero value as described in Locus.COMPARE. (Similar to
	the return values defined in java.lang.Comparable, however zero means 'overlap' instead of equals).
	@throws LocusException
	 */
	public int compareTo(Locus testLocus, int comparisonType, float comparisonValue) throws LocusException
	{
		int cValue;

		// if they're not in the same sequence (eg: chromosome), return the before or after
		if(this.chromosome.compareTo(testLocus.getChromosome()) > 0)
			return COMPARE.AFTER;
		if(this.chromosome.compareTo(testLocus.getChromosome()) < 0)
			return COMPARE.BEFORE;

		// if one locus is completely contained inside the other, return overlap regardless of the other parameters
		if((this.start >= testLocus.getStart() && this.end <= testLocus.getEnd())
			||
		   (testLocus.getStart() >= this.start && testLocus.getEnd() <= this.end))
			return COMPARE.OVERLAP;

		try
		{
			// adjust the comparison value
			if(comparisonType == COMPARISON_TYPE.FIXED)
			{
				cValue = (int)comparisonValue;
			}
			else if(comparisonType == COMPARISON_TYPE.PERCENT)
			{
				if(comparisonValue <=0 || comparisonValue > 1)
					throw new LocusException("Error in overloaded Locus.compareTo(Locus, int, float). Comparison type is defined as percent, but the comparison value is invalid");

				// calculate percentage
				int smallerLen;
				if((this.end - this.start) < (testLocus.getEnd() - testLocus.getStart()))
					smallerLen = this.end - this.start + 1;
				else
					smallerLen = testLocus.getEnd() - testLocus.getStart() + 1;

				cValue = ((int)Math.ceil(smallerLen * comparisonValue))*-1;
			}
			else
				throw new LocusException("Error in overloaded Locus.compareTo(Locus, int, float): invalid comparisonType value");
		}
		catch(Exception e)
		{
			throw new LocusException("Exception in overloaded Locus.compareTo(Locus, int, float): \n" + e);
		}

		// Do the comparison
		if((this.start - cValue) > (testLocus.getEnd() + 1))
			return COMPARE.AFTER;
		else if((this.end + cValue) < (testLocus.getStart() - 1))
			return COMPARE.BEFORE;
		else if((this.start - cValue) <= (testLocus.getEnd() + 1) && (this.end + cValue) >= (testLocus.getStart() - 1))
			return COMPARE.OVERLAP;
		else
		{
			throw new LocusException("Error in overloaded Locus.compareTo(Locus, int, float). No valid comparison case.");
		}
	}

	//--------------------------------------------------------------------
	/**
	Determines if this Locus ovelaps with another Locus. This function is similar to the overloaded {@code compareTo}
	function, but with 2 differences: it includes a <i>strand</i> type parameter to be included in the comparison,
	and it returns a simple boolean (overlap or not) rather than "before", "after", etc.
	Note: This is a shallow comparison only. Child loci are not traversed
	@param	testLocus the Locus to compare against
	@param	comparisonType	the type of comparison to be performed, as defined in Locus.COMPARISON_TYPE
	@param	comparisonValue	The value to be used for this comparison. If the comparisonType is
	@param	comparisonStrand	the type of strand comparison to be performed, as defined in Locus.COMPARISON_STRAND
	COMPARISON_TYPE.FIXED - the value is converted to an integer, a negative value means 'overlap' and a
	positive value means 'gap'. If the comparisonType is COMPARISON_TYPE.PERCENT, only the 'overlap'
	concept is used, the value (x) must be 0 < x <= 1, and the smaller Locus is used to define
	the discrete value.
	@return	Returns true if the loci overlap, false otherwise
	@throws LocusException
	 */
	public boolean overlaps(Locus testLocus, int comparisonType, float comparisonValue, int comparisonStrand) throws LocusException
	{
		int cValue;

		// if they're not in the same sequence (eg: chromosome), return false
		if(this.chromosome.compareTo(testLocus.getChromosome()) != 0)
			return false;

		// if the strand comparison fails, return false. COMPARISON_STRAND.NEUTRAL ignores this test.
		if(comparisonStrand != COMPARISON_STRAND.NEUTRAL)
		{
			switch(comparisonStrand)
			{
				case COMPARISON_STRAND.MATCH_STRICT:
					if(this.strand == STRAND.UNDEFINED || testLocus.getStrand() == STRAND.UNDEFINED || this.strand != testLocus.getStrand())
						return false;
					break;
				case COMPARISON_STRAND.MATCH_PERMISSIVE:
					if(this.strand != STRAND.UNDEFINED && testLocus.getStrand() != STRAND.UNDEFINED && this.strand != testLocus.getStrand())
						return false;
					break;
				case COMPARISON_STRAND.COMPLEMENT_STRICT:
					if(this.strand == STRAND.UNDEFINED || testLocus.getStrand() == STRAND.UNDEFINED || this.strand == testLocus.getStrand())
						return false;
					break;
				case COMPARISON_STRAND.COMPLEMENT_PERMISSIVE:
					if(this.strand != STRAND.UNDEFINED && testLocus.getStrand() != STRAND.UNDEFINED && this.strand == testLocus.getStrand())
						return false;
					break;
			}
		}

		// if one locus is completely contained inside the other, return true regardless of the other parameters
		if((this.start >= testLocus.getStart() && this.end <= testLocus.getEnd())
			||
		   (testLocus.getStart() >= this.start && testLocus.getEnd() <= this.end))
			return true;

		try
		{
			// adjust the comparison value
			if(comparisonType == COMPARISON_TYPE.FIXED)
			{
				cValue = (int)comparisonValue;
			}
			else if(comparisonType == COMPARISON_TYPE.PERCENT)
			{
				if(comparisonValue <=0 || comparisonValue > 1)
					throw new LocusException("Error in overloaded Locus.compareTo(Locus, int, float). Comparison type is defined as percent, but the comparison value is invalid");

				// calculate percentage
				int smallerLen;
				if((this.end - this.start) < (testLocus.getEnd() - testLocus.getStart()))
					smallerLen = this.end - this.start + 1;
				else
					smallerLen = testLocus.getEnd() - testLocus.getStart() + 1;

				cValue = ((int)Math.ceil(smallerLen * comparisonValue))*-1;
			}
			else
				throw new LocusException("Error in overloaded Locus.compareTo(Locus, int, float): invalid comparisonType value");
		}
		catch(Exception e)
		{
			throw new LocusException("Exception in overloaded Locus.compareTo(Locus, int, float): \n" + e);
		}

		// Do the comparison
		if((this.start - cValue) > (testLocus.getEnd() + 1))
			return false;
		else if((this.end + cValue) < (testLocus.getStart() - 1))
			return false;
		else if((this.start - cValue) <= (testLocus.getEnd() + 1) && (this.end + cValue) >= (testLocus.getStart() - 1))
			return true;
		else
		{
			throw new LocusException("Error in overlap(Locus, int, float, int). No valid comparison case.");
		}
	}

	//--------------------------------------------------------------------
	/**
	Combines the coordinates of this locus with another, reassigning the start & end positions to the largest region occupied by both loci
	Note: Besides the possible preservation of start & end positions, all of the parameter Locus' other attributes are lost.
	@param mergeLocus	the Locus to combine with
	@throws LocusException if the chromosomes do not match
	*/
	public void mergeCoords(Locus mergeLocus) throws LocusException
	{
		if(!this.chromosome.equals(mergeLocus.getChromosome()))
		{
			throw new LocusException("mergeCoords(Locus) Error: chromosomes do not match");
		}

		if(mergeLocus.getStart() < this.start)
			this.start = mergeLocus.getStart();

		if(mergeLocus.getEnd() > this.end)
			this.end = mergeLocus.getEnd();
	}

	//--------------------------------------------------------------------
	/**
	Returns a String representation of this Locus ID
	@return	the id
	*/
	public String toString()
	{
		return id;
	}

	//--------------------------------------------------------------------
	/**
	Returns a String representation of this Locus
	@return	a concatenation of the id, sequence, start, and end positions of the locus.
	*/
	public String toStringDetail()
	{
		if(!id.equals(""))
			return id + "@" + chromosome + ":" + start + "-" + end;
		else
			return chromosome + ":" + start + "-" + end;
	}

	//--------------------------------------------------------------------
	/**
	Sorts the child Locus objects according to the {@code Locus.compareTo(Object)} function.
	Note: The child loci may contain more children. This method is <i>not</i> recursive.
	*/
	public void sortChildLoci()
	{
		Collections.sort(kids);
	}
}
