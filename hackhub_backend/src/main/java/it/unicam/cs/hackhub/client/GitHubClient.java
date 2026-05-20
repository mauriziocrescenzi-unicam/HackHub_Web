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
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

/**
 * Lightweight utility class for accessing selected endpoints of the GitHub REST API.
 * <p>
 * This client currently exposes minimal functionality focused on retrieving
 * repository commit information without relying on external JSON libraries.
 * All operations are performed through static methods.
 */
public class GitHubClient {

    /** Root endpoint of the GitHub REST API (v3). */
    private static final String GITHUB_API = "https://api.github.com";

    /**
     * Private constructor to prevent instantiation.
     * <p>
     * This class is designed as a static utility holder.
     */
    private GitHubClient() {}

    /**
     * Fetches the SHA identifier of the most recent commit on the {@code main}
     * branch of a given GitHub repository.
     * <p>
     * The method parses the provided repository URL, constructs the corresponding
     * GitHub API endpoint, performs an HTTP GET request, and extracts the commit
     * SHA from the returned JSON payload.
     *
     * @param repositoryUrl the full HTTPS URL of the GitHub repository
     *                      (e.g. {@code https://github.com/owner/repository})
     * @return the SHA string representing the latest commit on the {@code main} branch
     * @throws RuntimeException if:
     *                          <ul>
     *                              <li>the repository URL is malformed,</li>
     *                              <li>the HTTP request fails,</li>
     *                              <li>the API returns a non-200 status code,</li>
     *                              <li>or the response body cannot be processed.</li>
     *                          </ul>
     */
    public static String getLastCommit(String repositoryUrl) {
        try {
            RepoResponse repo = GitHubRepoParser.parse(repositoryUrl);
            HttpClient client = HttpClient.newHttpClient();

            for (String branch : List.of("main", "master")) {
                String url = GITHUB_API + "/repos/" +
                        repo.owner() + "/" +
                        repo.repo() + "/commits/" + branch;

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .header("Accept", "application/vnd.github+json")
                        .GET()
                        .build();

                HttpResponse<String> response =
                        client.send(request, HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() == 200) {
                    return extractSha(response.body());
                }
            }
            throw new RuntimeException("Branch principale non trovato (provati: main, master)");
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Unable to retrieve commit", e);
        }
    }


    /**
     * Parses the JSON response returned by the GitHub API and extracts
     * the value associated with the {@code sha} field.
     * <p>
     * This implementation performs a basic string-based search and does not
     * rely on a JSON parsing library. It assumes that the response follows
     * the standard structure of GitHub's commit endpoint.
     *
     * @param json the raw JSON string returned by the GitHub API
     * @return the extracted SHA value
     * @throws RuntimeException if the {@code sha} field is not found
     *                          in the provided JSON string
     */
    private static String extractSha(String json) {
        int index = json.indexOf("\"sha\":\"");
        if (index == -1)
            throw new RuntimeException("Invalid GitHub response");
        int start = index + 7;
        int end = json.indexOf("\"", start);
        return json.substring(start, end);
    }
}
