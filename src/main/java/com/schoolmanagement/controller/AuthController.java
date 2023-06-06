package com.schoolmanagement.controller;

import com.schoolmanagement.entity.enums.RoleType;
import com.schoolmanagement.payload.Response.AuthResponse;
import com.schoolmanagement.payload.request.LoginRequest;
import com.schoolmanagement.security.jwt.JwtUtils;
import com.schoolmanagement.security.service.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.Arrays.stream;

@RestController
@RequestMapping("auth")
@RequiredArgsConstructor
public class AuthController {

    public final JwtUtils jwtUtils;

    public final AuthenticationManager authenticationManager;//

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> authenticateUser(@RequestBody @Valid LoginRequest loginRequest){

        //Gelen request in icin den paralo ve username bilgisi aliniyor,
        String username = loginRequest.getUsername();
        String password = loginRequest.getPassword();

        //Simdi authenticationManager uzerinden kullaniciyi valaide ediyoruz ve authentication nesnesi donecek.
        // authenticationManagerdan kullaniciyi dogrulayacagim.
       Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username,password));
        //Valide edilen kullanici contex e atiliyor.
        SecurityContextHolder.getContext().setAuthentication(authentication);

        //JWT Token olusturuluyor.
        String token1 = "Bearer "+ jwtUtils.generateJwtToken(authentication);

        //Burdan return e kadar ki kisim role bilgisini almak icin yapildi.Burasi opsiyoneldir.Rolu gondermeyecek olursak bunagerek yoktu.
        //GrantedAuthority turundeki rol yapaisni String turune cevirmek icin bunlar yazdik.
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();//anlik olarak login islemini gerceklestiren user i userdetail olarak gonderiyor.

        Set<String> roles = userDetails
                .getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());

        Optional<String> role = roles.stream().findFirst();//null geebilir diye Optional yaptik.

        //AuthResponse
        AuthResponse.AuthResponseBuilder authResponse = AuthResponse.builder();
        authResponse.username(userDetails.getUsername());
        authResponse.token(token1);
        authResponse.name(userDetails.getName());

        //Rol mevcutsa ve teacher ise advisor durumu setleniyor.
        if(role.isPresent()){//rol mevcutsa
            authResponse.role(role.get());
            if(role.get().equalsIgnoreCase(RoleType.TEACHER.name())){
                authResponse.isAdvisor(userDetails.getIsAdvisor().toString());
            }
        }

        //AuthResponse nesnesi ResponseEntity ile gonderiliyor

        return ResponseEntity.ok(authResponse.build());
    }

}
