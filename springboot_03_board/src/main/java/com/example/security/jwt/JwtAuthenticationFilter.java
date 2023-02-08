package com.example.security.jwt;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.example.login.dto.User;
import com.example.security.service.PrincipalDetails;
import com.fasterxml.jackson.databind.ObjectMapper;

//POST    http://localhost:8090/login

// 인증처리
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
   private AuthenticationManager authManager;

   public JwtAuthenticationFilter(AuthenticationManager authManager) {
      this.authManager = authManager;
   }

   // http://localhost:8090/login 요청을 하면 실행되는 함수
   @Override
   public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
         throws AuthenticationException {
      System.out.println("JwtAuthenticationFilter => login 요청 처리를 시작");

      try {
//         BufferedReader br = request.getReader();
//         String input = null;
//         while((input=br.readLine())!=null) {
//            System.out.println(input);
//         }

         ObjectMapper om = new ObjectMapper();
         User user = om.readValue(request.getInputStream(), User.class); // 로그인을하면 username, password 값을 User.class객체에 담아 user에 보내줌
         System.out.printf("username:%s password:%s\n", user.getUsername(), user.getPassword());

         UsernamePasswordAuthenticationToken authenticationToken =
        		 new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword());

         Authentication authentication = authManager.authenticate(authenticationToken); // 인증이 처리되는 단계

         PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
         System.out.printf("로그인 완료 됨(인증)  %s %s ", principalDetails.getUsername(), principalDetails.getPassword());

         return authentication; // 객체값을 리턴 (정상적으로 처리가됐으면)

      } catch (IOException e) {
         e.printStackTrace();
      }

      return null;
   }

   // 호출되어 정상적으로 처리
   @Override
   protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
         Authentication authResult) throws IOException, ServletException {

      System.out.println("successfulAuthentication 실행됨: 인증이 완료되었다는 의미이기도 함");
      PrincipalDetails principalDetails = (PrincipalDetails) authResult.getPrincipal();

      // RSA방식은 아니고 Hash방식
      String jwtToken = JWT.create()
    		.withSubject("mycos")
            .withExpiresAt(new Date(System.currentTimeMillis() + (60 * 1000 * 3 * 1L))) // 3분 (만료시간)
            .withClaim("username", principalDetails.getUser().getUsername())
            .withClaim("authRole", principalDetails.getUser().getAuthRole()) // 권한
            .sign(Algorithm.HMAC512("mySecurityCos")); // 토큰생성 (알고리즘사용)
      System.out.println("jwtToken:" + jwtToken);
      response.addHeader("Authorization", "Bearer " + jwtToken);
      

      
      final Map<String, Object> body = new HashMap<String, Object>();
      body.put("username", principalDetails.getUser().getUsername()); // username은 body에 담아 보내줌, body는 Map에 담는다

      ObjectMapper mapper = new ObjectMapper();
      mapper.writeValue(response.getOutputStream(), body);
   }

}