<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<div class="field" id="submit-div">
	<div class="control">
	   <button class="button btn is-link" type="submit">Speichern</button>
	</div>
	<article class="message is-danger mt-5">
	  <div class="message-header">
	    <p>Fehlerhafte Eingabe</p>
	  </div>
	  <div class="message-body">
	  <c:if test="${ not empty errormessage }">${errormessage}</c:if>
	  <c:if test="${ empty errormessage }">Bitte prüfen Sie Ihre Angaben.</c:if>
	  </div>
	</article>
</div>
