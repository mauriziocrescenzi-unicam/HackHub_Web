/*
 * Copyright © 2025 Victoria Coacci, Daniel Duda, Fattori Filippo
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the “Software”), to deal in the
 * Software without restriction, including without limitation the rights to use, copy,
 * modify, merge, publish, distribute, sublicense, and/or sell copies of the Software,
 * and to permit persons to whom the Software is furnished to do so, subject to the
 * following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
 * CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package it.unicam.cs.hackhub.service;

import it.unicam.cs.hackhub.model.Account;
import it.unicam.cs.hackhub.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Custom {@link UserDetailsService} implementation used by Spring Security to load user data for authentication.
 *
 * <p>
 * Retrieves an {@link Account} from the persistence layer using the provided email and maps it to a
 * Spring Security {@link UserDetails} instance, including the corresponding granted authority
 * in the form {@code ROLE_<ROLE>}.
 * </p>
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private AccountRepository repository;

    /**
     * Loads the user details associated with the specified email.
     *
     * <p>
     * This method is invoked by Spring Security during authentication to obtain user credentials
     * and authorities. The account role is mapped to a {@link SimpleGrantedAuthority} using the
     * {@code ROLE_} prefix required by Spring Security conventions.
     * </p>
     *
     * @param email the email used as the username for authentication
     * @return a {@link UserDetails} instance containing email, encoded password, and granted authorities
     * @throws UsernameNotFoundException if no account with the specified email exists
     */
    @Override
    public UserDetails loadUserByUsername(String email)
            throws UsernameNotFoundException {

        Account account = repository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Not found"));

        return new User(
                account.getEmail(),
                account.getPassword(),
                List.of(new SimpleGrantedAuthority("ROLE_" + account.getRole().name()))
        );
    }
}
