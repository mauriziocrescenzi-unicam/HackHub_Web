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
package it.unicam.cs.hackhub.repository;

import it.unicam.cs.hackhub.model.Account;
import it.unicam.cs.hackhub.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA repository for {@link Account} entities.
 *
 * <p>
 * Provides standard persistence operations through
 * {@link JpaRepository}, including CRUD functionality,
 * pagination and sorting support.
 * </p>
 *
 * <p>
 * Additional query methods are defined for retrieving
 * accounts by unique attributes such as email and nickname.
 * </p>
 */
public interface AccountRepository extends JpaRepository<Account, Long> {

    /**
     * Retrieves an {@link Account} by its unique email address.
     *
     * @param email non-null email address
     * @return an {@link Optional} containing the matching account
     *         if present, otherwise {@link Optional#empty()}
     */
    Optional<Account> findByEmail(String email);

    /**
     * Checks whether an {@link Account} exists with the specified email.
     *
     * @param email non-null email address
     * @return {@code true} if an account with the given email exists,
     *         {@code false} otherwise
     */
    boolean existsByEmail(String email);

    /**
     * Checks whether an {@link Account} exists with the specified nickname.
     *
     * @param nickname non-null nickname
     * @return {@code true} if an account with the given nickname exists,
     *         {@code false} otherwise
     */
    boolean existsByNickname(String nickname);

    /**
     * Retrieves a list of accounts based on the specified role.
     *
     * @param role the role to filter accounts by
     * @return a list of accounts matching the specified role, or an empty list if no accounts are found
     */
    List<Account> findByRole(Role role);
}
