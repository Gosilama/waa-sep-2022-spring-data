package edu.miu.service.impl;

import edu.miu.model.LoginRequest;
import edu.miu.model.LoginResponse;
import edu.miu.model.RefreshTokenRequest;
import edu.miu.security.JwtHelper;
import edu.miu.service.UaaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UaaServiceImpl implements UaaService {
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;

    private final JwtHelper jwtHelper;

    @Override
    public LoginResponse login(LoginRequest loginRequest) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getEmail(),
                            loginRequest.getPassword())
            );
        } catch (BadCredentialsException e) {
            log.info("Bad Credentials");
        }

        final String accessToken = jwtHelper.generateToken(loginRequest.getEmail());
        final String refreshToken = jwtHelper.generateRefreshToken(loginRequest.getEmail());
        return new LoginResponse(accessToken, refreshToken);
    }

    @Override
    public LoginResponse refreshToken(RefreshTokenRequest refreshTokenRequest) {
        boolean isRefreshTokenValid = jwtHelper.validateToken(refreshTokenRequest.getRefreshToken());
        if (isRefreshTokenValid) {
            final String accessToken = jwtHelper.generateToken(jwtHelper.getSubject(refreshTokenRequest.getRefreshToken()));
            return new LoginResponse(accessToken, refreshTokenRequest.getRefreshToken());
        }
        return new LoginResponse();
    }
}
