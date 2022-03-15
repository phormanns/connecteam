<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="de">
<jsp:include page="parts/header.jsp"/>
<body>
    <jsp:include page="parts/navbar.jsp"/>
    <div class="columns">
      <div class="column is-four-fifth-desktop">
        <section class="section">
            <div class="container">
                <h1 class="title">Titel</h1>
                <p class="subtitle">Hallo schöne Welt!</p>
            </div>
        </section>
      </div>
      <div class="column is-one-fifth-desktop">
        <jsp:include page="parts/sidebar.jsp"/>
      </div>
    </div>
    <jsp:include  page="parts/footer.jsp"/>
</body>
</html>
