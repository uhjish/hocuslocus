<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<HTML>
<HEAD>
<TITLE>LIA: Locus Intersection Analysis</TITLE>
<META name="description"
      content="This page contains a submission form for a bioinformatic tool (LIA) that correlates multiple sets of genomic loci" />
<META name="keywords"
      content="bioinformatics, biology, mrna, rna, dna, structure, tenenbaum, scott, zaleski, chris, nucleic acids, sequence, albany" />
<META name="expires" content="never" />
<META name="distribution" content="global" />
<META name="rating" content="general" />
<META name="robots" content="index, follow" />
<META http-equiv=Content-Language content=en-us />
<META http-equiv=Content-Type content="text/html; charset=windows-1252" />
<SCRIPT language=JavaScript type=text/JavaScript src="http://www.albany.edu/main/templates_javascript/roll_over_functions.js"></SCRIPT>
<META content="MSHTML 6.00.2800.1400" name=GENERATOR />

<style type="text/css">
<!--
table.files {
	border-left: solid;
	border-width:2;
	border-color:gray;
	background-color:white;
}

tr.results td {
	border-top: dotted;
	border-width:1;
	border-color:gray;
	font-weight:bold;
}
-->
</style>
<link href="http://www.albany.edu/cancergenomics/stylesheets/main_content01a.css" rel="stylesheet" type="text/css">

<%-- JSP Imports --%>
<%@ page import="org.omelogic.lia_web.*" %>
<%@ page import="org.omelogic.lia.*" %>
<%@ page import="org.omelogic.locus.*" %>
<%@ page import="java.util.ArrayList" %>

<%-- Init session objects --%>
<%
	LIA_WebState state = (LIA_WebState)session.getAttribute(LIA_SessionVars.SESSION_STATE);
	LIA lia  = (LIA)session.getAttribute(LIA_SessionVars.LIA_INSTANCE);
	if(state == null)
	{
		state = new LIA_WebState();
		session.setAttribute(LIA_SessionVars.SESSION_STATE, state);
	}
	
	if(lia == null) 
	{
		lia = new LIA();
		session.setAttribute(LIA_SessionVars.LIA_INSTANCE, lia);
	}
	
%>

<SCRIPT language=JavaScript>

function getObj(id)
{
    var obj = false;
    if(document.getElementById)
        obj = document.getElementById(id);
    else if(document.all)
        obj = document.all[id];
    else if(document.layers)
        obj = document.layers[id];

    return obj;
}

function validateUpload()
{
	var currFile = getObj("fileUpload").value;
	if(currFile == "")
	{
		alert("Please define a file to upload");
		return false;
	}
	else
		return true;
}

function validateParams()
{
	var fileCount = <%= state.getPosSets().size() + state.getNegSets().size() %>;
	if(fileCount < 2)
	{
		alert("You must upload at least 2 files");
		return false;
	}
	
	var compType = getObj("compType").value;
	var compValue = getObj("compValue").value;
	
	if(compValue == "")
	{
		alert("Please enter a valid comparison value");
		return false;
	}
	
	if(isNaN(compValue))
	{
		alert("Comparison value is invalid: " + compValue);
		return false;
	}
	
	if(compValue < 0 || parseInt(compValue) != parseFloat(compValue))
	{
		alert("Please use only positive integers for Comparison value");
		return false;
	}
	
	if(compType == "percent" && (compValue < 1 || compValue > 100))
	{
		alert("Please use a value between 1 and 100 for Percent comparisons");
		return false;
	}
	
	var msgPanel = getObj('messagePanel');
	msgPanel.innerHTML = "Please wait, this may take a few minutes...";
	return true;
}

function checkMessages()
{
	var msgPanel = getObj('messagePanel');
	var errmsgPanel = getObj('errorMessagePanel');
	msgPanel.innerHTML = "";
	errmsgPanel.innerHTML = "";
	
	<%
	String msg = state.getMessage();
	if(!msg.equals(""))
	{
		msg = msg.replaceAll("\r|\n|\r\n", "<br>");
		out.println("msgPanel.innerHTML = '<b><u>Server Message:</u></b><br>" + msg + "';");
		state.setMessage("");
	}
	String errmsg = state.getErrorMessage();
	if(!errmsg.equals(""))
	{
		errmsg = errmsg.replaceAll("\r|\n|\r\n", "<br>");
		out.println("errmsgPanel.innerHTML = '<b><u>Server Error Message:</u></b><br>" + errmsg + "';");
		state.setErrorMessage("");
	}
	%>
}

function setValueLabel()
{
	var label = getObj("valueLabel");
	var compType = getObj("compType");
	
	if(compType.value == "fixed")
		label.innerHTML = "NTs";
	else //percent
		label.innerHTML = "%";
}

function checkType()
{
	var cType = getObj("compType");
	var og = getObj("overlapGap");
	if(cType.selectedIndex == 1)
	{
		og.selectedIndex = 0;
		og.options[1].disabled = true;
	}
	else
	{
		og.options[1].disabled = false;
	}
}

function showResults()
{
	var results = getObj("resultPanel");
	if(navigator.appName == "Microsoft Internet Explorer")
	{
		if(<%= state.isLiaComplete()%>)
			results.style.display = "inline";
		else
			results.style.display = "none";
	}
	else if(navigator.appName == "Netscape")
	{
		if(<%= state.isLiaComplete()%>)
			results.style.visibility = "visible";
		else
			results.style.visibility = "collapse";
	}
	// else do nothing
}

</SCRIPT>

</HEAD>
<!-- onload handler  calls above MM_preload function and downloads images used in roll overs. Those from bannerlevel3 directory are for global navigation-->
<BODY onload="MM_preloadImages('http://www.albany.edu/main/bannerlevel3/ua_search_litea.gif','http://www.albany.edu/main/bannerlevel3/ua_siteindex_litea.gif','http://www.albany.edu/main/bannerlevel3/ua_home_litea.gif');checkMessages();showResults();">

<A name=top></A>
<!-- Begin table for top banner and first line of navigation -->
<TABLE cellSpacing=0 cellPadding=0 width=755 align=center border=0>
  <TBODY>
  <TR>

      <TD>
      
		<jsp:include page="Common/top_banner_nav.html" />
      
      </TD></TR>

  <TR>
    <TD><!-- Page Body Table to hold left navigation, Page body -->
        <TABLE cellSpacing=0 cellPadding=0 width=755 border=0>

          <TBODY>
            <TR>
              <TD width=164 align=left vAlign=top background=http://www.albany.edu/templates/banner_nav/leftnavbg_repeat.gif class="td_bordbotlitetan">

			<jsp:include page="lia_left_menu.html" />

              </TD>

              <TD width=591 valign=top class="td_litetan">
                <!--Table for page title graphic and main content table and tertiary column table-->

                <TABLE cellSpacing=0 cellPadding=0 border=0>
                  <TBODY>

                    <TR>
                      <TD width=20>&nbsp;</TD>
                      <TD vAlign=top align=left width=541>
		      		<p><span><br><font color="#880000" size="5"><b>LIA <font color="#000000" size="4">  - Locus Intersection Analysis</b></p>

                        <hr>
                        <font color="#000000" size="2">
                         LIA allows for any number of sets of genomic loci to be compared for common regions (overlap).
                         Any length of nucleotides can be defined for either minimum allowed overlap, or maximum gap between loci.
                         Locus bridging is also allowed if required.
                         </font>
                        <hr>

				<!-- Actual entry form code starts here!!!! -->

				<form name="uploadForm" id="uploadForm" method="POST" action="LIA_FileUpload" enctype="multipart/form-data" onsubmit="return validateUpload();">
				<font color="#000000" size="2">
				
					<table cellpadding=2 width=100%>
					<tr><td colspan=2>Upload Files: <font size=1><i>(".bed", ".gff" or ".gtf" format)</i></font></td></tr>
					<tr><td colspan=2><input type=file name="fileUpload" id="fileUpload">&nbsp;&nbsp;&nbsp;
							<input type="checkbox" name="negativeFile" id="negativeFile">Negative Set&nbsp;&nbsp;&nbsp;
							<input type="submit" value="Upload" name="formSubmitButton"></td>
					</tr>
					
					<tr><td height=10px width=50%></td><td height=10px width=50%></td></tr>
					<tr><td><u><b>Positive Files</b></u></td><td><u><b>Negative Files</b></u></td></tr>
					<tr><td valign=top>
						<table class="files">
							<%
							ArrayList<LocusSet> posFiles = state.getPosSets();
							for(int p=0; p<posFiles.size(); p++)
								out.println("<tr><td valign=top>" + posFiles.get(p).getName() + "</td><td>&nbsp;&nbsp;<a href='http://ribonomics.albany.edu/LIA_Web/LIA_FileUpload?fileIndex=" + p + "&setType=pos'><img border=0 src='images/action_delete.gif' alt='Remove file' title='Remove file' height='14' width='14' /></a></td></tr>");
							%>
						</table>
						</td>
						<td valign=top>
						<table class="files">
							<%
							ArrayList<LocusSet> negFiles = state.getNegSets();
							for(int n=0; n<negFiles.size(); n++)
								out.println("<tr><td valign=top>" + negFiles.get(n).getName() + "</td><td>&nbsp;&nbsp;<a href='http://ribonomics.albany.edu/LIA_Web/LIA_FileUpload?fileIndex=" + n + "&setType=neg'><img border=0 src='images/action_delete.gif' alt='Remove file' title='Remove file' height='14' width='14' /></a></td></tr>");
							%>
						</table>
						</td>
					</tr>
					</table>
				<br>
				</form>

				<hr>

				<form name="paramForm" id="paramForm" method="POST" action="LIA_Execute" onsubmit="return validateParams();">
				<table cellpadding=3>

				<tr><td valign=top>
				Comparison Type:
				</td><td>
				<select name="compType" id="compType" onchange="checkType();setValueLabel();">
				<option value="fixed" <% if(state.getComparisonType() == Locus.COMPARISON_TYPE.FIXED){out.println("selected");} %>>fixed</option>
				<option value="percent" <% if(state.getComparisonType() == Locus.COMPARISON_TYPE.PERCENT){out.println("selected");} %>>percentage</option>
				</select>
				&nbsp;&nbsp;
				<select name="overlapGap" id="overlapGap">
				<option value="overlap" <% if(state.getComparisonValue() < 0){out.println("selected");} %>>minimum overlap</option>
				<option value="gap" <% if(state.getComparisonValue() >= 0){out.println("selected");} %>>maximum gap</option>
				</select>
				</td></tr>
				
				<tr><td valign=top>
				Comparison Value:
				</td><td>
				<%
					String cVal;
					String vLabel;
					if(state.getComparisonType() == Locus.COMPARISON_TYPE.FIXED)
					{
						cVal = Integer.toString((int)Math.abs(state.getComparisonValue()));
						vLabel = "NTs";
					}
					else
					{
						cVal = Integer.toString((int)Math.abs(state.getComparisonValue() * 100));
						//cVal = Float.toString(Math.abs(state.getComparisonValue()));
						vLabel = "%";
					}
				%>
				<input type="text" name="compValue" id="compValue" size="4" value="<%= cVal %>">&nbsp;<span name="valueLabel" id="valueLabel"><%= vLabel %></span>
				</td></tr>
				
				<tr><td valign=top>
				Comparison Strand:
				</td><td>
				<table>
					<tr><td>
					<input type="radio" name="compStrand" value="<%= Locus.COMPARISON_STRAND.NEUTRAL %>" <% if(state.getComparisonStrand() == Locus.COMPARISON_STRAND.NEUTRAL){out.println("checked");} %>>Neutral<br>
					</td><td>
					<input type="radio" name="compStrand" value="<%= Locus.COMPARISON_STRAND.MATCH_STRICT %>" <% if(state.getComparisonStrand() == Locus.COMPARISON_STRAND.MATCH_STRICT){out.println("checked");} %>>Strict Match<br>
					</td><td>
					<input type="radio" name="compStrand" value="<%= Locus.COMPARISON_STRAND.COMPLEMENT_STRICT %>" <% if(state.getComparisonStrand() == Locus.COMPARISON_STRAND.COMPLEMENT_STRICT){out.println("checked");} %>>Strict Complement<br>
					</td></tr>
					<tr><td></td><td>
					<input type="radio" name="compStrand" value="<%= Locus.COMPARISON_STRAND.MATCH_PERMISSIVE %>" <% if(state.getComparisonStrand() == Locus.COMPARISON_STRAND.MATCH_PERMISSIVE){out.println("checked");} %>>Permissive Match<br>
					</td><td>
					<input type="radio" name="compStrand" value="<%= Locus.COMPARISON_STRAND.COMPLEMENT_PERMISSIVE %>" <% if(state.getComparisonStrand() == Locus.COMPARISON_STRAND.COMPLEMENT_PERMISSIVE){out.println("checked");} %>>Permissive Complement<br>
					</td></tr>
				</table>
				</td></tr>

				<tr><td>
				Allow Bridging:
				</td><td>
				<input type="checkbox" name="bridging" id="bridging" <% if(state.getBridging()){out.println("checked");} %>>
				</td></tr>

				<tr><td valign=top>
				Genome Release:
				</td><td>
				<select name="genomeRelease" id="genomeRelease">
				<option value="hg18" <% if(state.getGenome().equals("hg18")){out.println("selected");} %>>Human - NCBI Build 36</option>
				<option value="hg17" <% if(state.getGenome().equals("hg17")){out.println("selected");} %>>Human - NCBI Build 35</option>
				<option value="hg16" <% if(state.getGenome().equals("hg16")){out.println("selected");} %>>Human - NCBI Build 34</option>
				<option value="panTro1" <% if(state.getGenome().equals("panTro1")){out.println("selected");} %>>Chimp - CGSC Build 1 Version 1</option>
				<option value="rheMac2" <% if(state.getGenome().equals("rheMac2")){out.println("selected");} %>>Rhesus - Baylor HGSC v1.0 Mmul_051212</option>
				<option value="rheMac1" <% if(state.getGenome().equals("rheMac1")){out.println("selected");} %>>Rhesus - Baylor HGSC Mmul_0.1</option>
				<option value="canFam2" <% if(state.getGenome().equals("canFam2")){out.println("selected");} %>>Dog - Broad Institute v2.0</option>
				<option value="canFam1" <% if(state.getGenome().equals("canFam1")){out.println("selected");} %>>Dog - Broad Institute v1.0</option>
				<option value="mm7" <% if(state.getGenome().equals("mm7")){out.println("selected");} %>>Mouse - NCBI Build 35</option>
				<option value="mm6" <% if(state.getGenome().equals("mm6")){out.println("selected");} %>>Mouse - NCBI Build 34</option>
				<option value="mm5" <% if(state.getGenome().equals("mm5")){out.println("selected");} %>>Mouse - NCBI Build 33</option>
				<option value="rn3" <% if(state.getGenome().equals("rn3")){out.println("selected");} %>>Rat - Baylor HGSC v3.1</option>
				<option value="rn2" <% if(state.getGenome().equals("rn2")){out.println("selected");} %>>Rat - Baylor HGSC v2.1</option>
				<option value="dm2" <% if(state.getGenome().equals("dm2")){out.println("selected");} %>>D. melanogaster - BDGP Release 4</option>
				<option value="dm1" <% if(state.getGenome().equals("dm1")){out.println("selected");} %>>D. melanogaster - BDGP Release 3.1</option>
				<option value="ce2" <% if(state.getGenome().equals("ce2")){out.println("selected");} %>>C. elegans - WormBase v. WS120</option>
				<option value="ce1" <% if(state.getGenome().equals("ce1")){out.println("selected");} %>>C. elegans - WormBase v. WS100</option>
				<option value="sacCer1" <% if(state.getGenome().equals("sacCer1")){out.println("selected");} %>>Yeast - SGD 1 Oct 2003 sequence</option>
				</select>
				<br>
				<font size=1><i>(for viewing in the UCSC Genome Browser)</i></font>
				</td></tr>
				
				<tr><td></td>

				<td align=right>
				<input type="submit" value="Submit" name="paramSubmitButton" style="font-weight:bold;">
				</td></tr>
				</table>

				</font>
				</form>

				<br>
				<span name="messagePanel" id="messagePanel" style="font-size:small;"></span>
				<br>
				<span name="errorMessagePanel" id="errorMessagePanel" style="background-color:white;font-size:small;"></span>
				<div name="resultPanel" id="resultPanel" style="background-color:white;border-style:double;visibility:collapse">
				Results:
				<table width=100% style="border-top:2px solid black;">
				<tr><td align=right>
					<font size=1>
					Comparison Type: <% if(state.getComparisonType() == Locus.COMPARISON_TYPE.FIXED){out.println("Fixed ");}else{out.println("Percent ");} %>
									<% if(state.getComparisonValue() < 0){out.println("Overlap");}else{out.println("Gap");} %>
									<br>
					Comparison Value: <%= cVal %>&nbsp;<%= vLabel %>
									<br>
									<%
										String compStrand = "";
										switch(state.getComparisonStrand())
										{
											case Locus.COMPARISON_STRAND.NEUTRAL:
												compStrand = "Neutral";
												break;
											case Locus.COMPARISON_STRAND.MATCH_STRICT:
												compStrand = "Strict Match";
												break;
											case Locus.COMPARISON_STRAND.MATCH_PERMISSIVE:
												compStrand = "Permissive Match";
												break;
											case Locus.COMPARISON_STRAND.COMPLEMENT_STRICT:
												compStrand = "Strict Complement";
												break;
											case Locus.COMPARISON_STRAND.COMPLEMENT_PERMISSIVE:
												compStrand = "Permissive Complement";
												break;
										}
									%>
					Comparison Strand: <%= compStrand %>
									<br>
					Bridging: <%= state.getBridging() %>
					</font>
				</td></tr>
				<tr><td>
					<table width=100% cellpadding=4 cellspacing=0>
					<%
					ArrayList<LocusSet> posSets = state.getPosSets();
					LocusSet currSet, currResultSet;
					for(int setIndex=0; setIndex<posSets.size(); setIndex++)
					{
						out.println("<tr class='results'>");
						currSet = posSets.get(setIndex);
						currResultSet = lia.getLocusSetResult(currSet.getName());
						out.println("<td><u>" + currSet.getName() + "</u>:<br>" + 
								currResultSet.getSize() + "/" + currSet.getSize() + "&nbsp;&nbsp;(" + 
								(Math.round((((double)currResultSet.getSize() / (double)currSet.getSize()) * (double)10000)) / (double)100) + "%)</td>");
			
						if(currResultSet.getSize() > 65536)
						{
							out.println("<td>Too large to view</td>");
							out.println("<td>Too large to view</td>");
						}
						else
						{
							out.println("<td><a target='_blank' href='http://ribonomics.albany.edu/LIA_Web/LIA_Result?rotype=html&stype=inputSet&isindex=" + setIndex + "&db=" + state.getGenome() + "'>View as Table</a></td>");
							out.println("<td><a target='_blank' href='http://ribonomics.albany.edu/LIA_Web/LIA_Result?rotype=excel&stype=inputSet&isindex=" + setIndex + "&db=" + state.getGenome() + "'>View in Excel</a></td>");
						}
						
						out.println("<td><a target='_blank' href='http://ribonomics.albany.edu/LIA_Web/LIA_Result?rotype=download&stype=inputSet&isindex=" + setIndex + "&db=" + state.getGenome() + "'>Download Zipped</a></td>");
						out.println("<td><a target='_blank' href='http://ribonomics.albany.edu/LIA_Web/LIA_Result?rotype=ucsc&stype=inputSet&isindex=" + setIndex + "&db=" + state.getGenome() + "'>Send to UCSC</a></td>");
						out.println("</tr>");
					}
					%>
					<tr class='results' style='font-weight:bold;'><td><u>Unions</u></td>
						<td><a target='_blank' href='http://ribonomics.albany.edu/LIA_Web/LIA_Result?rotype=html&stype=union&isindex=0&db=<%= state.getGenome() %>'>View as Table</a></td>
						<td><a target='_blank' href='http://ribonomics.albany.edu/LIA_Web/LIA_Result?rotype=excel&stype=union&isindex=0&db=<%= state.getGenome() %>'>View in Excel</a></td>
						<td><a target='_blank' href='http://ribonomics.albany.edu/LIA_Web/LIA_Result?rotype=download&stype=union&isindex=0&db=<%= state.getGenome() %>'>Download Zipped</a></td>
						<td><a target='_blank' href='http://ribonomics.albany.edu/LIA_Web/LIA_Result?rotype=ucsc&stype=union&isindex=0&db=<%= state.getGenome() %>'>Send to UCSC</a></td>
					</tr>
					<tr class='results'><td><u>Intersections</u></td>
						<td><a target='_blank' href='http://ribonomics.albany.edu/LIA_Web/LIA_Result?rotype=html&stype=intersection&isindex=0&db=<%= state.getGenome() %>'>View as Table</a></td>
						<td><a target='_blank' href='http://ribonomics.albany.edu/LIA_Web/LIA_Result?rotype=excel&stype=intersection&isindex=0&db=<%= state.getGenome() %>'>View in Excel</a></td>
						<td><a target='_blank' href='http://ribonomics.albany.edu/LIA_Web/LIA_Result?rotype=download&stype=intersection&isindex=0&db=<%= state.getGenome() %>'>Download Zipped</a></td>
						<td><a target='_blank' href='http://ribonomics.albany.edu/LIA_Web/LIA_Result?rotype=ucsc&stype=intersection&isindex=0&db=<%= state.getGenome() %>'>Send to UCSC</a></td>
					</tr>
					<tr class='results'><td><u>Correlation Matrix</u></td>
						<td><a target='_blank' href='http://ribonomics.albany.edu/LIA_Web/LIA_Result?rotype=html&stype=matrix&isindex=0&db=<%= state.getGenome() %>'>View as Table</a></td>
						<td><a target='_blank' href='http://ribonomics.albany.edu/LIA_Web/LIA_Result?rotype=excel&stype=matrix&isindex=0&db=<%= state.getGenome() %>'>View in Excel</a></td>
						<td><a target='_blank' href='http://ribonomics.albany.edu/LIA_Web/LIA_Result?rotype=download&stype=matrix&isindex=0&db=<%= state.getGenome() %>'>Download Zipped</a></td>
					<td></td></tr>
					</table>
				</td></tr>

				</table>
				</div>


				<!-- End of entry form code  -->

						</TD>
					      <TD width=20></TD>
					    </TR>
					    <TR>
					    
					      <TD colSpan=3><span style="font-size:8px;color:grey;"><%= session.getId().substring(0, session.getId().indexOf('.')) %></span></TD>
					    </TR>
					    
					  </TBODY>
					</TABLE>
				      </TD>

				    </TR>
				    
				    
				    
				    </TBODY>

				</TABLE>
			<!-- End Page Body Table to hold left navigation, Page body -->
			</TD></TR></TBODY></TABLE>
<P></P>

<!-- End Total page table, 2 rows 1 column to hold top banner and page body -->
<!-- Table for Bottom Global Navigation -->

<jsp:include page="Common/bottom_nav.html" />

<!-- End of Table for Bottom Global Navigation -->
</BODY></HTML>
