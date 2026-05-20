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

/**
 * Enumeration representing the authorization roles
 * available within the system.
 *
 * <p>
 * Roles define the level of permissions granted to an {@link Account}
 * and are used to enforce access control policies.
 * </p>
 *
 * <ul>
 *     <li>{@link #USER} – Standard user with basic permissions.</li>
 *     <li>{@link #STAFF} – Staff member with elevated operational permissions.</li>
 *     <li>{@link #ADMIN} – System administrator with full access privileges.</li>
 * </ul>
 */
public enum Role {
    /**
     * A regular user with limited permissions.
     */
    USER,

    /**
     * A staff member with limited permissions.
     */
    STAFF,

    /**
     * An administrator with full permissions.
     */
    ADMIN
}