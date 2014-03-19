package org.omelogic.lia_web;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;
import org.omelogic.locus.*;
import org.omelogic.lia.*;

public class LIA_Execute extends HttpServlet
{	
	//------------------------------------------------------------------------------
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException
	{
		doPost(request, response);
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
			state.setErrorMessage("Execute error: LIA instance object does not exist");
			forwardToMainPage(dispatcher, request, response);
			return;
		}
		
		String comparisonType = request.getParameter("compType");
		String overlapGap = request.getParameter("overlapGap");
		String comparisonValue = request.getParameter("compValue");
		String comparisonStrand = request.getParameter("compStrand");
		String bridging = request.getParameter("bridging");
		String genome = request.getParameter("genomeRelease");
		
		if(comparisonType == null || overlapGap == null || comparisonValue == null || comparisonStrand == null || genome == null)
		{
			String getParameterMsg = "Execute error retrieving parameters:" +
				" comparisonType:" + comparisonType + 
				" overlapGap:" + overlapGap + 
				" comparisonValue:" + comparisonValue + 
				" comparisonStrand:" + comparisonStrand + 
				" genome:" + genome;
			state.setErrorMessage(getParameterMsg);
			forwardToMainPage(dispatcher, request, response);
			return;
		}
		
		int cType;
		float cValue;
		if(comparisonType.equals("fixed"))
		{
			cType = Locus.COMPARISON_TYPE.FIXED;
			cValue = Float.parseFloat(comparisonValue);
		}
		else // percent
		{
			cType = Locus.COMPARISON_TYPE.PERCENT;
			cValue = (float)(Double.parseDouble(comparisonValue) * .01);
		}
		if(cType == Locus.COMPARISON_TYPE.FIXED && overlapGap.equals("overlap"))
			cValue = cValue * (-1);
		int cStrand = Integer.parseInt(comparisonStrand);
		boolean br = false;
		if(bridging != null)
			br = true;
		
		state.setComparisonType(cType);
		state.setComparisonValue(cValue);
		state.setComparisonStrand(cStrand);
		state.setBridging(br);
		state.setGenome(genome);
		
		lia.setBridging(br);
		try
		{
			lia.intersect(cType, cValue, cStrand);
		}
		catch(LIAException liae)
		{
			state.setErrorMessage("Execute error: LIA execution failed - " + liae.toString());
			forwardToMainPage(dispatcher, request, response);
			return;
		}
		
		state.setLiaComplete(true);
		
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
