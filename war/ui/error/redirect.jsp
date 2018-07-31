<%@ page contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<html>
<head>
	<meta charset="utf-8">
	<meta http-equiv="X-UA-Compatible" content="IE=edge">
	<meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
	<meta name="description" content="Analyze Manager">
	<meta name="author" content="Optim">
	<title>AI Voice Analytics</title>
</head>
<body>
<%
	String root = request.getContextPath();
	String url = "/ui/error/internal-error.html";

	int status = response.getStatus();
	switch ( status ) {

	case HttpServletResponse.SC_NOT_FOUND :
		url = "/ui/error/not-found.html";
		break;
	}

	response.sendRedirect( root + url );
%>
</body>
</html>