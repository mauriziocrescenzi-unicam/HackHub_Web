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
package it.unicam.cs.hackhub.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.EqualsAndHashCode;

import java.util.HashSet;
import java.util.Set;

/**
 * JPA entity representing a participation or behavioral rule
 * that can be associated with one or more {@link Hackathon} instances.
 *
 * <p>
 * A rule is uniquely identified by its name and contains
 * a descriptive text explaining the constraint or requirement.
 * </p>
 */
@Entity
@Table(
    name = "rules",
    uniqueConstraints = {
            @UniqueConstraint(columnNames = {"name", "description"})
    }
)
@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Rule {

    /**
     * Primary key of the rule.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_rule")
    @EqualsAndHashCode.Include
    private Long idRule;

    /**
     * Name of the rule.
     */
    @Column(nullable = false, length = 100)
    private String name;

    /**
     * Description of the rule.
     */
    @Column(nullable = false, length = 2000)
    private String description;

    /**
     * Hackathons that use this rule.
     */
    @ManyToMany(mappedBy = "rules")
    private Set<Hackathon> hackathons = new HashSet<>();

    /**
     * Protected no-argument constructor required by JPA.
     *
     * <p>
     * Intended for use by the persistence provider only.
     * </p>
     */
    protected Rule() {
    }

    /**
     * Sets the rule name.
     *
     * @param name non-null and non-blank rule name
     * @throws IllegalArgumentException if {@code name} is null or blank
     */
    public void setName(String name) {
        if (name == null || name.isBlank())
            throw new IllegalArgumentException("Name cannot be null or empty");
        this.name = name;
    }

    /**
     * Sets the rule description.
     *
     * @param description non-null and non-blank description
     * @throws IllegalArgumentException if {@code description} is null or blank
     */
    public void setDescription(String description) {
        if (description == null || description.isBlank())
            throw new IllegalArgumentException("Description cannot be null or empty");
        this.description = description;
    }
}
