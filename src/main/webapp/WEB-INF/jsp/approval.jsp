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
                <h1 class="title">Moderation</h1>
                <p class="subtitle">Freigabe einer Nachricht</p>
				<form hx-post="/mod/${post.random}" hx-target="#submit-div" hx-swap="outerHTML">
				
					 <div class="field">
					  <label class="label">Betreff</label>
					  <div class="control">
					    <input class="input" type="text" value="${post.subject}" readonly>
					  </div>
					</div>
					
					<div class="field">
					  <label class="label">Absender</label>
					  <div class="control">
                        <input class="input" type="email" value="${post.originalFrom}" readonly>
                      </div>
					</div>
					
					<div class="field">
					  <label class="label">Nachricht</label>
					  <div class="control">
					    <textarea class="textarea" placeholder="Textarea" readonly>${post.textContent}</textarea>
					  </div>
					</div>
					
					<div class="field">
					  <div class="control">
					    <label class="checkbox">
					      <input type="checkbox" name="sendermaysend"> Absender-Adresse in Zukunft freigeben
					    </label>
					  </div>
					</div>
					
					<div class="field">
					  <div class="control">
					    <label class="radio">
					      <input type="radio" name="approval" value="approve-message"> Diese Nachricht freigeben
					    </label>
					    <label class="radio">
					      <input type="radio" name="approval" value="reject-message"> Diese Nachricht zurückweisen
						</label>
					  </div>
					</div>
					
					<div class="field is-grouped" id="submit-div">
					  <div class="control">
					    <button class="button btn is-link" type="submit">Speichern</button>
					  </div>
					</div>
				</form>
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
