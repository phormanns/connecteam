<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
    <script>
      document.addEventListener('DOMContentLoaded', () => {
        const $navbarBurgers = Array.prototype.slice.call(document.querySelectorAll('.navbar-burger'), 0);
        if ($navbarBurgers.length > 0) {
          $navbarBurgers.forEach( el => {
            el.addEventListener('click', () => {
              const target = el.dataset.target;
              const $target = document.getElementById(target);
              el.classList.toggle('is-active');
              $target.classList.toggle('is-active');
            });
          });
        }
      });
    </script>
