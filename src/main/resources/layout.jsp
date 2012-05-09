<%@ page contentType="text/html; charset=UTF-8"%>
<%@ page pageEncoding="UTF-8"%>

<%@ include file="/WEB-INF/jsp/common/taglibs.jsp" %>

<fmt:setBundle basename="StripesResources"/>

<s:layout-definition>

    <!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
    <html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
        <title>${title}</title>
        <s:layout-component name="head">
        </s:layout-component>
    </head>
    <body>

    <%-- Display messages --%>
    <s:messages/>
    <s:errors/>


    <%-- The Header --%>
    <div id="header">
        <h1><fmt:message key="application.header.title"/></h1>
    </div>

    <%-- The main content --%>
    <div id="main">
        <s:layout-component name="body">
        </s:layout-component>
    </div>

    <%-- The footer --%>
    <div id="footer">This is your footer !</div>

    </body>
    </html>

</s:layout-definition>