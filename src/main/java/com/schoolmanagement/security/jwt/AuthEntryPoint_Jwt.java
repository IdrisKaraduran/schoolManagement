package com.schoolmanagement.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
public class AuthEntryPoint_Jwt implements AuthenticationEntryPoint {



    //Bu sinif yetkilendirme hatasi durumunda islem yapilmasini sagliyor.
    private static final Logger logger = LoggerFactory.getLogger(AuthEntryPoint_Jwt.class);

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        //logger kullanilarak yetkilendirme hatasi kaydediliyor.
        logger.error("Unauthorized error : {}",authException.getMessage());

        //response icerigi Json olacak ve HTTP Status Cod da 401 , UnAuthorized setliyorum
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        final Map<String,Object> body = new HashMap<>();
        body.put("Status",HttpServletResponse.SC_UNAUTHORIZED);
        body.put("error","Unauthorized");
        body.put("message",authException.getMessage());
        body.put("path",request.getServletPath());

        final ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(response.getOutputStream(),body);
        //401 hatasinin istedigim sekilde kullaniciy gonderilmesini sagladim.



    }






}
