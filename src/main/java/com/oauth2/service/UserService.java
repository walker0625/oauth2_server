package com.oauth2.service;

import com.oauth2.domain.users.User;
import com.oauth2.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collection;

@RequiredArgsConstructor
@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final RedisTemplate redisTemplate;

    /* DB에 USER 값 있으면 비활성화
    private final PasswordEncoder passwordEncoder;

    @PostConstruct
    public void save(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
    }

    public void init() {
        User tester = userRepository.findByUsername("tester");

        if (tester == null) {
            User user = new User();
            user.setUsername("tester");
            user.setPassword("qwe123!@#");
            this.save(user);
        }
    }*/

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username);
        saveRedis(user);
        return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), getAuthorities());
    }

    private void saveRedis(User user) {
        ValueOperations valueOperations = redisTemplate.opsForValue();
        valueOperations.set("id", user.getId());
    }

    private Collection<? extends GrantedAuthority> getAuthorities() {
        return Arrays.asList(new SimpleGrantedAuthority("ROLE_USER"));
    }

}