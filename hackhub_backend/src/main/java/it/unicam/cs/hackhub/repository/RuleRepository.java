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

import it.unicam.cs.hackhub.model.Rule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Repository interface for managing persistence operations related to {@link Rule} entities.
 *
 * <p>
 * Extends {@link JpaRepository} to provide standard CRUD operations such as creation,
 * retrieval, update, and deletion of rules.
 * </p>
 *
 * <p>
 * Also defines query methods for retrieving rules based on their unique attributes,
 * such as name or identifier, supporting hackathon rule management.
 * </p>
 */
public interface RuleRepository extends JpaRepository<Rule, Long> {

    /**
     * Retrieves a rule by its name.
     *
     * @param name the rule name
     * @return an {@link Optional} containing the matching rule,
     *         or empty if not found
     */
    Optional<Rule> findByName(String name);

    /**
     * Retrieves a rule identified by its unique idRule attribute.
     *
     * @param idRule the unique identifier of the rule
     * @return an {@link Optional} containing the matching rule,
     *         or empty if no rule with the specified identifier exists
     */
    Optional<Rule> findByIdRule(Long idRule);
}
