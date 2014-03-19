/*
 *      Compressor.java
 *
 *      Copyright 2007 Ajish D. George <ajishg@gmail.com>
 *
 */

package org.omelogic.utils;

import java.util.zip.*;
import java.io.*;
import java.lang.Exception;

public class Compressor {
	
	public static byte[] compress( File file ) throws Exception
	{
		byte [] rawBuffer = new byte[(int)file.length()];

		try 
		{
			BufferedInputStream fstream = new BufferedInputStream(new FileInputStream(file));
			fstream.read(rawBuffer,0,rawBuffer.length);
			fstream.close();
		}
		catch (Exception e)
		{
			throw new Exception("CompressorERROR: " + e.toString());
		}
		
		Deflater compressor = new Deflater();
		compressor.setLevel(Deflater.BEST_SPEED);			
		
		compressor.setInput(rawBuffer);
		compressor.finish();
		
	    // Create an expandable byte array to hold the compressed data.
		// You cannot use an array that's the same size as the orginal because
		// there is no guarantee that the compressed data will be smaller than
		// the uncompressed data.
		ByteArrayOutputStream bos = new ByteArrayOutputStream((int)file.length());
		
		// Compress the data
		byte[] buf = new byte[1024];
		while (!compressor.finished()) {
			int count = compressor.deflate(buf);
			bos.write(buf, 0, count);
		}
		try {
			bos.close();
		} catch (IOException e) {
		}
		
		// Get the compressed data
		return bos.toByteArray();
	}
	
	public static byte[] compressObject( Object obj ) throws Exception
	{
		
	    // Create an expandable byte array to hold the compressed data.
		// You cannot use an array that's the same size as the orginal because
		// there is no guarantee that the compressed data will be smaller than
		// the uncompressed data.
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		
		GZIPOutputStream gzipout  = new GZIPOutputStream(bos);
		
		ObjectOutputStream oos = new ObjectOutputStream(gzipout);
		
		oos.writeObject(obj);
		
		gzipout.finish();
		
		bos.close();
		
		return bos.toByteArray();
		
	}
	
	public static Object decompressObject( byte[] bytes) throws Exception
	{
		
	    // Create an expandable byte array to hold the compressed data.
		// You cannot use an array that's the same size as the orginal because
		// there is no guarantee that the compressed data will be smaller than
		// the uncompressed data.
		GZIPInputStream gzipin = new GZIPInputStream( bytesToInputStream(bytes) );
		
		ObjectInputStream ois = new ObjectInputStream( gzipin );
		
		Object o = ois.readObject();
		
		return o;
				
		
	}

	
	public static InputStream decompress( byte[] compressed )
	{
	    // Create the decompressor and give it the data to compress
		Inflater decompressor = new Inflater();
		decompressor.setInput(compressed);
		
		// Create an expandable byte array to hold the decompressed data
		ByteArrayOutputStream bos = new ByteArrayOutputStream(compressed.length);
		
		// Decompress the data
		byte[] buf = new byte[1024];
		while (!decompressor.finished()) {
			try {
				int count = decompressor.inflate(buf);
				bos.write(buf, 0, count);
			} catch (Exception e) {
				//			
			}
		}
		try {
			bos.close();
		} catch (Exception e) {
			//
		}
		
		// Get the decompressed data
		
		return bytesToInputStream(bos.toByteArray());
	}
	

	public static InputStream bytesToInputStream( byte[] bytes )
	{
		return (InputStream)(new ByteArrayInputStream(bytes));

	}

}
