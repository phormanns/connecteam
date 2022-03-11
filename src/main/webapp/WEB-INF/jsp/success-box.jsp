<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<article class="message is-success mt-5">
  <div class="message-header">
    <p>Gespeichert</p>
  </div>
  <div class="message-body">
  <c:if test="${ not empty successmessage }">${successmessage}</c:if>
  <c:if test="${ empty successmessage }">Ihre Daten wurden gespeichert.</c:if>
  </div>
</article>