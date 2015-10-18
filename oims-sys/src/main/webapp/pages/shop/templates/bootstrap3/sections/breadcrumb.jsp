<%
response.setCharacterEncoding("UTF-8");
response.setHeader("Cache-Control","no-cache");
response.setHeader("Pragma","no-cache");
response.setDateHeader ("Expires", -1);
%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="/WEB-INF/oims-tags.tld" prefix="sm" %> 


	<div class="row">

		<ul class="breadcrumb">
		
		
		  <c:forEach items="${requestScope.BREADCRUMB.breadCrumbs}" var="breadcrumb" varStatus="count">
			  <li class="active">
			    <a href="${breadcrumb.url}<sm:breadcrumbParam/>">${breadcrumb.label}</a>
			  </li>
		  </c:forEach>
		
		</ul>

	</div>
