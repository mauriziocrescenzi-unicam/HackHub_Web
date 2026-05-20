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
package it.unicam.cs.hackhub.client;

import it.unicam.cs.hackhub.DTO.RepoResponse;

/**
 * Helper utility for validating and decomposing GitHub repository URLs.
 * <p>
 * This class extracts the repository owner and repository name from a
 * standard GitHub HTTPS URL and encapsulates them in a {@link RepoResponse}.
 * It performs basic structural validation but does not verify repository existence.
 */
public class GitHubRepoParser {
    /**
     * Validates and parses a GitHub repository URL of the form:
     * <pre>
     * https://github.com/{owner}/{repository}
     * </pre>
     * <p>
     * The method removes the fixed GitHub domain prefix and splits the
     * remaining path segments to retrieve the owner and repository name.
     *
     * @param repoUrl the full HTTPS URL pointing to a GitHub repository
     * @return a {@link RepoResponse} containing:
     *         <ul>
     *             <li>the repository owner</li>
     *             <li>the repository name</li>
     *         </ul>
     * @throws IllegalArgumentException if:
     *         <ul>
     *             <li>the provided URL is {@code null}</li>
     *             <li>the URL does not start with {@code https://github.com/}</li>
     *             <li>the URL does not contain both owner and repository segments</li>
     *         </ul>
     */
    public static RepoResponse parse(String repoUrl) {
        if (repoUrl == null || !repoUrl.startsWith("https://github.com/")) {
            throw new IllegalArgumentException("Invalid GitHub URL");
        }

        String[] parts = repoUrl
                .replace("https://github.com/", "")
                .split("/");

        if (parts.length < 2) {
            throw new IllegalArgumentException("Invalid GitHub URL");
        }

        return new RepoResponse(parts[0], parts[1]);
    }
}
