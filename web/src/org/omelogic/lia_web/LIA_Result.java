package org.omelogic.lia_web;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;
import java.util.*;
import java.util.zip.*;
import org.omelogic.locus.*;
import org.omelogic.lia.*;
import org.omelogic.utils.locussetio.*;

public class LIA_Result extends HttpServlet
{	
	String fs = System.getProperty("file.separator");
	
	//------------------------------------------------------------------------------
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException
	{
		HttpSession session = request.getSession();
		RequestDispatcher dispatcher = request.getRequestDispatcher("index.jsp");
		
		LIA_WebState state = (LIA_WebState)session.getAttribute(LIA_SessionVars.SESSION_STATE);
		if(state == null) // Should have been created by the main jsp page
		{
			forwardToMainPage(dispatcher, request, response);
			return;
		}
		
		String localDir = "";
		
		LIA lia = (LIA)session.getAttribute(LIA_SessionVars.LIA_INSTANCE);
		if(lia == null)
		{
			state.setErrorMessage("Result error: LIA instance object does not exist");
			forwardToMainPage(dispatcher, request, response);
			return;
		}
		
		String sessionID = session.getId();
		if(sessionID.lastIndexOf('.') != -1)
			sessionID = sessionID.substring(0, sessionID.lastIndexOf('.')); // strip off the appened ".workerX"
		
		String resultOutputType = request.getParameter("rotype");
		String setType = request.getParameter("stype");
		String inputSetIndex = request.getParameter("isindex");
		String ucscDB = request.getParameter("db");
		
		String fileName;
		String zipFileName;
		String origPath;
		String zipPath;
		
		int setIndex = 0;
		try
		{
			setIndex = Integer.parseInt(inputSetIndex);
		}
		catch(NumberFormatException nfe)
		{
			state.setErrorMessage("Result error: Non parsable result set index: " + inputSetIndex);
			forwardToMainPage(dispatcher, request, response);
			return;
		}

		//###################################################
		if(resultOutputType.equals("html") || resultOutputType.equals("excel"))
		{
			if(resultOutputType.equals("html"))
				response.setContentType("text/html");
			else // excel
				response.setContentType("application/vnd.ms-excel");
				
			PrintWriter out = null;
			try
			{
				out = response.getWriter();
			}
			catch(IOException ioe)
			{
				state.setErrorMessage("Result error: IOException on response.getWriter()");
				forwardToMainPage(dispatcher, request, response);
				return;
			}

			try
			{
				//*************************
				if(setType.equals("union"))
				{
					LocusSet ls = lia.getIntersectResult();
					sendSetAsTable(ls, out);
				}
				//*************************
				else if(setType.equals("intersection"))
				{
					LocusSet ls = lia.getIntersectResult();
					LocusSet iSet = getIntersect(ls);
					sendSetAsTable(iSet, out);
				}
				//*************************
				else if(setType.equals("inputSet"))
				{
					String name = state.getPosSets().get(setIndex).getName();
					LocusSet ls = lia.getLocusSetResult(name);
					sendSetAsTable(ls, out);
				}
				//*************************
				else if(setType.equals("matrix"))
				{
					LocusNexus ln = lia.getLocusNexus();
					sendMatrixAsTable(ln, out);
				}
			}
			catch(LIAException liae)
			{
				state.setErrorMessage("Result error: LIA Exception: " + liae.toString());
				forwardToMainPage(dispatcher, request, response);
				return;
			}
		}
		//###################################################
		else if(resultOutputType.equals("download"))
		{
			response.setContentType("application/zip");
			try
			{
				localDir = getSessionFileDir(session);
			}
			catch(IOException ioe)
			{
				state.setErrorMessage("Result error: getSessionFileDir failed " + ioe.toString());
				forwardToMainPage(dispatcher, request, response);
				return;
			}
			
			//*************************
			if(setType.equals("union"))
			{
				LocusSet ls;
				try
				{
					ls = lia.getIntersectResult();
				}
				catch(LIAException liae)
				{
					state.setErrorMessage("Result error: LIAException generating result: " + liae.toString());
					forwardToMainPage(dispatcher, request, response);
					return;
				}
				
				fileName = "LIA_Union.bed";
				zipFileName = "LIA_Union.zip";
				origPath = localDir + fs + fileName;
				zipPath = localDir + fs + zipFileName;
				response.setHeader("Content-Disposition", "attachement; filename=\"" + zipFileName + "\"" );
				String trackLine = "track name='LIA Union' description='LIA Union Result' visibility=2";
				
				try
				{
					createLocalFile(ls, origPath, trackLine);
					zipFile(origPath, zipPath, fileName);
					sendFile(response, zipPath);
				}
				catch(IOException ioe)
				{
					state.setErrorMessage("Result error: IOException generating result: " + ioe.toString());
					forwardToMainPage(dispatcher, request, response);
					return;
				}
			}
			//*************************
			else if(setType.equals("intersection"))
			{
				LocusSet ls;
				try
				{
					ls = lia.getIntersectResult();
				}
				catch(LIAException liae)
				{
					state.setErrorMessage("Result error: LIAException generating result: " + liae.toString());
					forwardToMainPage(dispatcher, request, response);
					return;
				}
				LocusSet iSet = getIntersect(ls);
				
				fileName = "LIA_Intersection.bed";
				zipFileName = "LIA_Intersection.zip";
				origPath = localDir + fs + fileName;
				zipPath = localDir + fs + zipFileName;
				response.setHeader("Content-Disposition", "attachement; filename=\"" + zipFileName + "\"" );
				String trackLine = "track name='LIA Intersection' description='LIA Intersection Result' visibility=2";
				
				try
				{
					createLocalFile(iSet, origPath, trackLine);
					zipFile(origPath, zipPath, fileName);
					sendFile(response, zipPath);
				}
				catch(IOException ioe)
				{
					state.setErrorMessage("Result error: IOException generating result: " + ioe.toString());
					forwardToMainPage(dispatcher, request, response);
					return;
				}
			}
			//*************************
			else if(setType.equals("inputSet"))
			{
				String name = state.getPosSets().get(setIndex).getName();
				LocusSet ls;
				try
				{
					ls = lia.getLocusSetResult(name);
				}
				catch(LIAException liae)
				{
					state.setErrorMessage("Result error: LIAException generating result: " + liae.toString());
					forwardToMainPage(dispatcher, request, response);
					return;
				}
				
				String shortName = name;
				if(name.lastIndexOf('.') != -1)
					shortName = name.substring(0, name.lastIndexOf('.'));

				fileName = "LIA_" + shortName + ".bed";
				zipFileName = "LIA_" + shortName + ".zip";
				origPath = localDir + fs + fileName;
				zipPath = localDir + fs + zipFileName;
				response.setHeader("Content-Disposition", "attachement; filename=\"" + zipFileName + "\"" );
				String trackLine = "track name='" + shortName + "' description='LIA Result for " + name + "' visibility=2";
				
				try
				{
					createLocalFile(ls, origPath, trackLine);
					zipFile(origPath, zipPath, fileName);
					sendFile(response, zipPath);
				}
				catch(IOException ioe)
				{
					state.setErrorMessage("Result error: IOException generating result: " + ioe.toString());
					forwardToMainPage(dispatcher, request, response);
					return;
				}
			}
			//*************************
			else if(setType.equals("matrix"))
			{
				LocusNexus ln;
				try
				{
					ln = lia.getLocusNexus();
				}
				catch(LIAException liae)
				{
					state.setErrorMessage("Result error: LIAException generating result: " + liae.toString());
					forwardToMainPage(dispatcher, request, response);
					return;
				}
				
				fileName = "LIA_Correlation_Matrix.bed";
				zipFileName = "LIA_Correlation_Matrix.zip";
				origPath = localDir + fs + fileName;
				zipPath = localDir + fs + zipFileName;
				response.setHeader("Content-Disposition", "attachement; filename=\"" + zipFileName + "\"" );
				
				try
				{
					createMatrixFile(ln, origPath);
					zipFile(origPath, zipPath, fileName);
					sendFile(response, zipPath);
				}
				catch(IOException ioe)
				{
					state.setErrorMessage("Result error: IOException generating result: " + ioe.toString());
					forwardToMainPage(dispatcher, request, response);
					return;
				}
			}
			
		}
		//###################################################
		else if(resultOutputType.equals("ucsc"))
		{
			try
			{
				localDir = getSessionFileDir(session);
			}
			catch(IOException ioe)
			{
				state.setErrorMessage("Result error: getSessionFileDir failed " + ioe.toString());
				forwardToMainPage(dispatcher, request, response);
				return;
			}

			//*************************
			if(setType.equals("union"))
			{
				LocusSet ls;
				try
				{
					ls = lia.getIntersectResult();
				}
				catch(LIAException liae)
				{
					state.setErrorMessage("Result error: LIAException generating result: " + liae.toString());
					forwardToMainPage(dispatcher, request, response);
					return;
				}
				
				fileName = "LIA_Union.bed";
				origPath = localDir + fs + fileName;
				String trackLine = "track name='LIA Union' description='LIA Union Result' visibility=2";
				String firstLocus = ls.getLocusByIndex(0).getChromosome() + ":" + ls.getLocusByIndex(0).getStart() + "-" + ls.getLocusByIndex(0).getEnd();
				
				String bedFileURL = "http://ribonomics.albany.edu/webapp/scratch/lia/" + sessionID + "/" + fileName;
				String ucscURL = "http://genome.ucsc.edu/cgi-bin/hgTracks?db=" + ucscDB + "&position=" + firstLocus + "&hgt.customText=" + bedFileURL;
				
				try
				{
					createLocalFile(ls, origPath, trackLine);
					response.sendRedirect(ucscURL);
				}
				catch(IOException ioe)
				{
					state.setErrorMessage("Result error: IOException generating result: " + ioe.toString());
					forwardToMainPage(dispatcher, request, response);
					return;
				}
			}
			//*************************
			else if(setType.equals("intersection"))
			{
				LocusSet ls;
				try
				{
					ls = lia.getIntersectResult();
				}
				catch(LIAException liae)
				{
					state.setErrorMessage("Result error: LIAException generating result: " + liae.toString());
					forwardToMainPage(dispatcher, request, response);
					return;
				}
				LocusSet iSet = getIntersect(ls);

				fileName = "LIA_Intersection.bed";
				origPath = localDir + fs + fileName;
				String trackLine = "track name='LIA Union' description='LIA Intersection Result' visibility=2";
				String firstLocus = ls.getLocusByIndex(0).getChromosome() + ":" + ls.getLocusByIndex(0).getStart() + "-" + ls.getLocusByIndex(0).getEnd();
				
				String bedFileURL = "http://ribonomics.albany.edu/webapp/scratch/lia/" + sessionID + "/" + fileName;
				String ucscURL = "http://genome.ucsc.edu/cgi-bin/hgTracks?db=" + ucscDB + "&position=" + firstLocus + "&hgt.customText=" + bedFileURL;
				
				try
				{
					createLocalFile(ls, origPath, trackLine);
					response.sendRedirect(ucscURL);
				}
				catch(IOException ioe)
				{
					state.setErrorMessage("Result error: IOException generating result: " + ioe.toString());
					forwardToMainPage(dispatcher, request, response);
					return;
				}
			}
			//*************************
			else if(setType.equals("inputSet"))
			{
				String name = state.getPosSets().get(setIndex).getName();
				LocusSet ls;
				try
				{
					ls = lia.getLocusSetResult(name);
				}
				catch(LIAException liae)
				{
					state.setErrorMessage("Result error: LIAException generating result: " + liae.toString());
					forwardToMainPage(dispatcher, request, response);
					return;
				}
				
				String shortName = name;
				if(name.lastIndexOf('.') != -1)
					shortName = name.substring(0, name.lastIndexOf('.'));

				fileName = "LIA_" + shortName + ".bed";
				origPath = localDir + fs + fileName;
				String trackLine = "track name='" + shortName + "' description='LIA Result for " + name + "' visibility=2";
				String firstLocus = ls.getLocusByIndex(0).getChromosome() + ":" + ls.getLocusByIndex(0).getStart() + "-" + ls.getLocusByIndex(0).getEnd();
				String bedFileURL = "http://ribonomics.albany.edu/webapp/scratch/lia/" + sessionID + "/" + fileName;
				String ucscURL = "http://genome.ucsc.edu/cgi-bin/hgTracks?db=" + ucscDB + "&position=" + firstLocus + "&hgt.customText=" + bedFileURL;
				
				try
				{
					createLocalFile(ls, origPath, trackLine);
					response.sendRedirect(ucscURL);
				}
				catch(IOException ioe)
				{
					state.setErrorMessage("Result error: IOException generating result: " + ioe.toString());
					forwardToMainPage(dispatcher, request, response);
					return;
				}
			}
			//*************************
			else if(setType.equals("matrix"))
			{
				state.setErrorMessage("Result error: Cannot send matrix to UCSC");
				forwardToMainPage(dispatcher, request, response);
				return;
			}
		}
		//###################################################
		else
		{
			state.setErrorMessage("Result error: unknown result type: " + resultOutputType);
			forwardToMainPage(dispatcher, request, response);
			return;
		}
	}

	//------------------------------------------------------------------------------
	//------------------------------------------------------------------------------
	// Used to catch the IOException & wrap it as a ServletException
	// Helps make the code in doGet() method cleaner
	private void forwardToMainPage(RequestDispatcher dispatcher, HttpServletRequest request, HttpServletResponse response) throws ServletException
	{
		try
		{
			dispatcher.forward(request, response);
		}
		catch(IOException ioe)
		{
			throw new ServletException(ioe);
		}
	}
	//------------------------------------------------------------------------------
	private String getSessionFileDir(HttpSession session) throws IOException
	{
		ServletContext sc = session.getServletContext();
		String sessionDirBase = sc.getInitParameter("SessionDirBase");
		String sessionID = session.getId();
		if(sessionID.lastIndexOf('.') != -1)
			sessionID = sessionID.substring(0, sessionID.lastIndexOf('.')); // strip off the appened ".workerX"
		
		String localFileDir = sessionDirBase + fs + sessionID;

		File outDir = new File(localFileDir);
		if(!outDir.exists())
			outDir.mkdirs();
		
		return localFileDir;
	}
	
	//------------------------------------------------------------------------------
	private LocusSet getIntersect(LocusSet ls)
	{
		LocusSet newSet = new LocusSet("Intersections");
		Locus currLocus, childLocus;
		Iterator<Locus> iter = ls.getLoci();
		Iterator<Locus> childIter;

		while(iter.hasNext())
		{
			currLocus = iter.next();
			childIter = currLocus.getChildren();
			while(childIter.hasNext())
			{
				childLocus = childIter.next();
				if(childLocus.getType() != null && childLocus.getType().equals(Locus.TYPE.INTERSECTION))
					newSet.addLocus(childLocus);
			}
		}
		return newSet;
	}
	
	//------------------------------------------------------------------------------
	private void sendSetAsTable(LocusSet ls, PrintWriter out)
	{
		Iterator<Locus> iter = ls.getLoci();
		Locus currLocus;
		out.println("<table border=1 cellpadding=2 style='border-collapse:collapse'>");
		while(iter.hasNext())
		{
			currLocus = iter.next();
			out.print("<tr>");
			out.print("<td>" + currLocus.getChromosome() + "</td>");
			out.print("<td>" + currLocus.getStart() + "</td>");
			out.print("<td>" + currLocus.getEnd() + "</td>");
			out.print("<td>" + currLocus.getID() + "</td>");
			out.print("<td>" + currLocus.getScore() + "</td>");
			out.print("<td>" + currLocus.getStrandShortString() + "</td>");
			out.print("</tr>");
		}
		out.println("</table>");
		return;
	}
	
	//------------------------------------------------------------------------------
	private void sendMatrixAsTable(LocusNexus ln, PrintWriter out)
	{
		ArrayList<String> names = ln.getSetNames();
		Locus[][] matrix = ln.getMatrix();
		int totalCols = ln.numColumns();
		int totalRows = ln.numRows();
		
		out.println("<table border=1 cellpadding=2 style='border-collapse:collapse'>");
		out.print("<tr>");
		for(int c=0; c<totalCols; c++)
		{
			out.print("<td>" + names.get(c) + "</td>");
		}
		out.println("</tr>");
		
		for(int r=0; r<totalRows; r++)
		{
			out.print("<tr>");
			for(int c=0; c<totalCols; c++)
			{
				out.print("<td>" + matrix[r][c].toString() + "</td>");
			}
			out.println("</tr>");
		}
		out.println("</table>");
		return;
	}
	
	//------------------------------------------------------------------------------
	private void createLocalFile(LocusSet ls, String filePath, String trackLine) throws IOException
	{
		File lFile = new File(filePath);
		if(lFile.exists())
			return;
		
		ArrayList<String> header = new ArrayList<String>();
		header.add(trackLine);
		try
		{
			LocusSetIO.writeLocusSet(ls, filePath, header);
		}
		catch(IllegalArgumentException iae)
		{
			throw new IOException(iae.toString());
		}
		
		/*
		PrintWriter pw = new PrintWriter(new FileWriter(filePath));
		Iterator<Locus> iter = ls.getLoci();
		Locus currLocus;
		pw.println(trackLine);
		while(iter.hasNext())
		{
			currLocus = iter.next();
			pw.print(currLocus.getChromosome() + "\t");
			pw.print(currLocus.getStart() + "\t");
			pw.print(currLocus.getEnd() + "\t");
			pw.print(currLocus.getID() + "\t");
			pw.print(currLocus.getScore() + "\t");
			pw.print(currLocus.getStrandShortString() + "\n");
		}
		pw.flush();
		pw.close();
		*/
	}
	
	//------------------------------------------------------------------------------
	private void createMatrixFile(LocusNexus ln, String filePath) throws IOException
	{
		File csvFile = new File(filePath);
		if(csvFile.exists())
			return;
		
		PrintWriter pw = new PrintWriter(new FileWriter(filePath));
		
		ArrayList<String> names = ln.getSetNames();
		Locus[][] matrix = ln.getMatrix();
		int totalCols = ln.numColumns();
		int totalRows = ln.numRows();
		for(int c=0; c<totalCols; c++)
		{
			pw.print(names.get(c));
			if(c<totalCols-1)
				pw.print("\t");
			else
				pw.print("\n");
		}
		
		for(int r=0; r<totalRows; r++)
		{
			for(int c=0; c<totalCols; c++)
			{
				pw.print(matrix[r][c].toString());
				if(c<totalCols-1)
					pw.print("\t");
				else
					pw.print("\n");
			}
		}

		pw.flush();
		pw.close();
	}
	
	//------------------------------------------------------------------------------
	private void zipFile(String inFileLoc, String outFileLoc, String origFileName) throws IOException
	{
		byte[] bbuf = new byte[1024];
		
		ZipOutputStream out = new ZipOutputStream(new FileOutputStream(outFileLoc));
		FileInputStream in = new FileInputStream(inFileLoc);

		out.putNextEntry(new ZipEntry(origFileName));

		int len;
		while ((len = in.read(bbuf)) != -1)
		{
			out.write(bbuf, 0, len);
		}
		
		out.closeEntry();
		out.close();
		in.close();
	}
	
	//------------------------------------------------------------------------------
	private void sendFile(HttpServletResponse response, String fileLoc) throws IOException
	{
		File file = new File(fileLoc);
		ServletOutputStream op = response.getOutputStream();

		response.setContentLength((int)file.length());

		byte[] bbuf = new byte[1024];
		DataInputStream in = new DataInputStream(new FileInputStream(file));

		int length = 0;
		while ((length = in.read(bbuf)) != -1)
		{
			op.write(bbuf,0,length);
		}

		in.close();
		op.flush();
	}
}
