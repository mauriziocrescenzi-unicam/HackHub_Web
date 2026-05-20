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
package it.unicam.cs.hackhub.DTO;

import it.unicam.cs.hackhub.model.Rule;
/**
 * Data Transfer Object (DTO) representing a hackathon rule.
 * <p>
 * Used to expose rule information through REST responses.
 *
 * @param id          the unique identifier of the rule
 * @param name        the name of the rule
 * @param description the detailed description of the rule
 */
public record RuleResponse(
        Long id,
        String name,
        String description
) {
    /**
     * Converts a {@link Rule} entity into a {@link RuleResponse}.
     *
     * @param rule the {@link Rule} entity to convert
     * @return a corresponding {@link RuleResponse},
     *         or {@code null} if the provided entity is {@code null}
     */
    public static RuleResponse fromEntity(Rule rule) {

        if (rule == null) {
            return null;
        }
        return new RuleResponse(
                rule.getIdRule(),
                rule.getName(),
                rule.getDescription()
        );
    }
}
