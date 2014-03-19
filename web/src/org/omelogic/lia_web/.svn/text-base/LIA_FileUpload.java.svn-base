package org.omelogic.lia_web;

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import org.apache.commons.fileupload.*;
import org.apache.commons.fileupload.servlet.*;
import org.apache.commons.fileupload.util.Streams;
import org.omelogic.locus.*;
import org.omelogic.utils.locussetio.*;
import org.omelogic.lia.*;

public class LIA_FileUpload extends HttpServlet
{
	//------------------------------------------------------------------------------
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException
	{
		response.setContentType("text/html");
		HttpSession session = request.getSession();
		RequestDispatcher dispatcher = request.getRequestDispatcher("index.jsp");
		
		LIA_WebState state = (LIA_WebState)session.getAttribute(LIA_SessionVars.SESSION_STATE);
		if(state == null) // Should have been created by the main jsp page
		{
			forwardToMainPage(dispatcher, request, response);
			return;
		}
		
		LIA lia = (LIA)session.getAttribute(LIA_SessionVars.LIA_INSTANCE);
		if(lia == null)
		{
			state.setErrorMessage("FileUpload error: LIA instance object does not exist");
			forwardToMainPage(dispatcher, request, response);
			return;
		}
		
		String setIndex = request.getParameter("fileIndex");
		String type = request.getParameter("setType");
		int index = Integer.parseInt(setIndex);
		String name;
		
		if(type.equals("pos"))
		{
			name = state.getPosSets().get(index).getName();
			state.removePosSet(index);
		}
		else // type.equals("neg")
		{
			name = state.getNegSets().get(index).getName();
			state.removeNegSet(index);
		}
		
		try
		{
			lia.removeLocusSet(name);
		}
		catch(LIAException liae)
		{
			state.setErrorMessage("FileRemoval error: lia.removeLocusSet failed: " + liae.toString());
			forwardToMainPage(dispatcher, request, response);
			return;
		}
		
		forwardToMainPage(dispatcher, request, response);
	}
	
	//------------------------------------------------------------------------------
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException
	{
		response.setContentType("text/html");
		HttpSession session = request.getSession();
		RequestDispatcher dispatcher = request.getRequestDispatcher("index.jsp");
		
		LIA_WebState state = (LIA_WebState)session.getAttribute(LIA_SessionVars.SESSION_STATE);
		if(state == null) // Should have been created by the main jsp page
		{
			forwardToMainPage(dispatcher, request, response);
			return;
		}
		
		LIA lia = (LIA)session.getAttribute(LIA_SessionVars.LIA_INSTANCE);
		if(lia == null)
		{
			state.setErrorMessage("FileUpload error: LIA instance object does not exist");
			forwardToMainPage(dispatcher, request, response);
			return;
		}
		
		// Validate multi-part request
		if(!ServletFileUpload.isMultipartContent(request))
		{
			state.setErrorMessage("FileUpload error: Incorrect request - Not multi-part content");
			forwardToMainPage(dispatcher, request, response);
			return;
		}
		
		// get the file item iterator
		ServletFileUpload requestData = new ServletFileUpload();
		FileItemIterator FIiter;
		try
		{
			FIiter = requestData.getItemIterator(request);
		}
		catch(Exception e)
		{
			state.setErrorMessage("FileUpload error: ServletFileUpload.getIterIterator() failed");
			forwardToMainPage(dispatcher, request, response);
			return;
		}
		
		FileItemStream FIS = null;
		String fieldName = "";
		String locusFileName = "";
		boolean isNegative = false;
		InputStream Istream = null;
		LocusSet ls = null;
		
		try
		{
			// iterate through the items in the stream
			while (FIiter.hasNext())
			{
				FIS = FIiter.next();
				fieldName = FIS.getFieldName();
				Istream = FIS.openStream();

				// if it's a regular form field, parse the value
				if(FIS.isFormField())
				{
					if(fieldName.equals("negativeFile"))
					{
						// if the field name exists at all, that meas true. Else false
						isNegative = true;
					}
					// else - currently uninterested in any other fields
				}
				else // else it's a file
				{
					locusFileName = FIS.getName();
					ls = LocusSetIO.readLocusSet(LocusSetIO.getTranslatorCode(locusFileName), Istream, locusFileName);
				}
			}
		}
		catch(Exception e)
		{
			state.setErrorMessage("FileUpload error while parsing stream: " + e.toString());
			forwardToMainPage(dispatcher, request, response);
			return;
		}
		
		try
		{
			if(!isNegative)
			{
				lia.addLocusSet(ls, true);
				state.addPosSet(ls);
			}
			else
			{
				lia.addLocusSet(ls, false);
				state.addNegSet(ls);
			}
			state.setLiaComplete(false);
		}
		catch(LIAException liae)
		{
			state.setErrorMessage("FileUpload error: LIA.addLocusSet failed - " + liae.toString());
			forwardToMainPage(dispatcher, request, response);
			return;
		}
		
		forwardToMainPage(dispatcher, request, response);
	}
	
	//------------------------------------------------------------------------------
	// Used to catch the IOException & wrap it as a ServletException
	// Helps make the code in doPost() method cleaner
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
}
