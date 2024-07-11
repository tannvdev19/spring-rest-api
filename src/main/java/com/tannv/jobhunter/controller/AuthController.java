package com.tannv.jobhunter.controller;

import com.tannv.jobhunter.domain.User;
import com.tannv.jobhunter.domain.request.ReqLoginDTO;
import com.tannv.jobhunter.domain.response.ResLoginDTO;
import com.tannv.jobhunter.service.UserService;
import com.tannv.jobhunter.util.SecurityUtil;
import com.tannv.jobhunter.util.anotation.ApiMessage;
import com.tannv.jobhunter.util.error.IdInvalidException;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final SecurityUtil securityUtil;
    private final UserService userService;

    public AuthController(AuthenticationManagerBuilder authenticationManagerBuilder, SecurityUtil securityUtil, UserService userService) {
        this.authenticationManagerBuilder = authenticationManagerBuilder;
        this.securityUtil = securityUtil;
        this.userService = userService;
    }

    @PostMapping("/login")
    @ApiMessage("Login")
    public ResponseEntity<ResLoginDTO> login(@Valid @RequestBody ReqLoginDTO reqLoginDto) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(reqLoginDto.getUsername(), reqLoginDto.getPassword());
        Authentication authentication = this.authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        SecurityContextHolder.getContext().setAuthentication(authentication);

        ResLoginDTO res = new ResLoginDTO();
        User currentUserDb = this.userService.handleGetUserByUsername(reqLoginDto.getUsername());
        if(currentUserDb != null) {
            ResLoginDTO.UserLogin userLogin = new ResLoginDTO.UserLogin(currentUserDb.getId(), currentUserDb.getEmail(), currentUserDb.getName());
            res.setUser(userLogin);
        }
        String accessToken = this.securityUtil.createAccessToken(reqLoginDto.getUsername(), res.getUser());
        res.setAccessToken(accessToken);
        String refreshToken = this.securityUtil.createRefreshToken(reqLoginDto.getUsername(), res);
        this.userService.updateUserToken(refreshToken, reqLoginDto.getUsername());
        ResponseCookie responseCookie = ResponseCookie.from("refresh_token", refreshToken)
                .httpOnly(true)
                .path("/")
                .maxAge(this.securityUtil.getRefreshTokenExpiration())
                .secure(true)
                .build();
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, responseCookie.toString())
                .body(res);
    }

    @GetMapping("/account")
    @ApiMessage("Fetch account")
    public ResponseEntity<ResLoginDTO.UserGetAccount> getAccount() {
        String email = SecurityUtil.getCurrentUserLogin().orElse(null);

        User userDb = this.userService.handleGetUserByUsername(email);
        ResLoginDTO.UserGetAccount userGetAccount = new ResLoginDTO.UserGetAccount();
        ResLoginDTO.UserLogin userLogin = new ResLoginDTO.UserLogin();
        if(userDb != null) {
            userLogin.setId(userDb.getId());
            userLogin.setName(userDb.getName());
            userLogin.setEmail(userDb.getEmail());
            userGetAccount.setUser(userLogin);
        }
        return ResponseEntity.ok().body(userGetAccount);
    }

    @GetMapping("/refresh")
    @ApiMessage("Get user by refresh token")
    public ResponseEntity<ResLoginDTO> getRefreshToken(
            @CookieValue(name = "refresh_token") String refresh_token
    ) throws IdInvalidException {
        Jwt decodedToken = this.securityUtil.checkValidRefreshToken(refresh_token);
        String email = decodedToken.getSubject();

        User currentUser = this.userService.getUserByRefreshTokenAndEmail(refresh_token, email);
        if(currentUser == null) {
            throw new IdInvalidException("Refresh token invalid");
        }

        ResLoginDTO res = new ResLoginDTO();
        User currentUserDb = this.userService.handleGetUserByUsername(email);
        if(currentUserDb != null) {
            ResLoginDTO.UserLogin userLogin = new ResLoginDTO.UserLogin(currentUserDb.getId(), currentUserDb.getEmail(), currentUserDb.getName());
            res.setUser(userLogin);
        }
        String newAccessToken = this.securityUtil.createAccessToken(email, res.getUser());
        res.setAccessToken(newAccessToken);
        String newRefreshToken = this.securityUtil.createRefreshToken(email, res);
        this.userService.updateUserToken(newRefreshToken, email);
        ResponseCookie responseCookie = ResponseCookie.from("refresh_token", newRefreshToken)
                .httpOnly(true)
                .path("/")
                .maxAge(this.securityUtil.getRefreshTokenExpiration())
                .secure(true)
                .build();
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, responseCookie.toString())
                .body(res);
    }

    @PostMapping("/logout")
    @ApiMessage("Logout user")
    public ResponseEntity<Void> logout(
            @CookieValue(name = "refresh_token") String refresh_token
    ) throws IdInvalidException {
        String email = SecurityUtil.getCurrentUserLogin().orElse(null);
        if(email == null) {
            throw new IdInvalidException("Access token invalid");
        }
        this.userService.updateUserToken(null, email);

        ResponseCookie deleteSpringCookie = ResponseCookie
                .from("refresh_token", "")
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(0)
                .build();
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, deleteSpringCookie.toString())
                .body(null);
    }
}
