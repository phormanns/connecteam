<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="de">
<jsp:include page="WEB-INF/jsp/header.jsp"/>
<body>
    <jsp:include page="WEB-INF/jsp/navbar.jsp"/>
    <div class="columns">
      <div class="column is-one-quarter">
        <jsp:include page="WEB-INF/jsp/sidebar.jsp"/>
      </div>
      <div class="column">
        <section class="section">
            <div class="container">
                <h1 class="title">Titel</h1>
                <p class="subtitle">Hallo schöne Welt!</p>
            </div>
        </section>
      </div>
    </div>
    <jsp:include  page="WEB-INF/jsp/footer.jsp"/>
</body>
</html>
