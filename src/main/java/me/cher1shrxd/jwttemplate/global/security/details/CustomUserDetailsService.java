package me.cher1shrxd.jwttemplate.global.security.details;

import lombok.RequiredArgsConstructor;
import me.cher1shrxd.jwttemplate.domain.user.entity.UserEntity;
import me.cher1shrxd.jwttemplate.domain.user.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity userEntity = userRepository.findByEmail(username)
                .orElseThrow(RuntimeException::new);

        return new CustomUserDetails(userEntity);
    }
}
