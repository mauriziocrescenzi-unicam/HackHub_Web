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

import it.unicam.cs.hackhub.model.Staff;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository interface for managing persistence operations related to {@link Staff} entities.
 *
 * <p>
 * Extends {@link JpaRepository} to provide standard CRUD operations such as creation,
 * retrieval, update, and deletion of staff assignments.
 * </p>
 *
 * <p>
 * The {@link Staff} entity represents the association between hackathons and their assigned
 * staff members, including organizers, mentors, and judges.
 * </p>
 */
public interface StaffRepository extends JpaRepository<Staff, Integer> {
}
