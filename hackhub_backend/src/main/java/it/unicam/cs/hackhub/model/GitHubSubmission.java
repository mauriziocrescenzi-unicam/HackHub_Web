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

import it.unicam.cs.hackhub.client.GitHubClient;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * JPA entity representing a GitHub-based submission.
 *
 * <p>
 * The immutable reference of this submission type corresponds to the SHA
 * of the latest commit on the {@code main} branch at the time of submission
 * or subsequent update.
 * </p>
 *
 * <p>
 * Each {@code GitHubSubmission} is associated with exactly one {@link Team}
 * and one {@link Hackathon}, inherited from {@link Submission}.
 * </p>
 *
 * <p>
 * This entity uses single-table inheritance and is identified
 * by the discriminator value {@code "GITHUB"}.
 * </p>
 */
@Entity
@DiscriminatorValue("GITHUB")
@Getter
@NoArgsConstructor
public class GitHubSubmission extends Submission {

    /**
     * URL of the GitHub repository submitted by the team.
     */
    @Column(nullable = false, length = 500)
    @Setter
    private String repositoryUrl;

    /**
     * SHA of the latest commit on the main branch at submission time.
     */
    @Column(nullable = false, length = 100)
    private String lastCommitSha;


    /**
     * Creates a new GitHub-based submission.
     *
     * <p>
     * Upon construction, the latest commit SHA is immediately retrieved
     * from the specified repository and stored as the immutable reference.
     * </p>
     *
     * @param repositoryUrl the URL of the GitHub repository
     * @param team          the team submitting the project
     * @param hackathon     the hackathon to which the submission belongs
     * @throws IllegalArgumentException if the repository URL is null/blank
     *                                  or if team or hackathon are null
     */
    public GitHubSubmission(String repositoryUrl,
                            Team team,
                            Hackathon hackathon) {

        if (repositoryUrl == null || repositoryUrl.isBlank()) {
            throw new IllegalArgumentException("Repository URL cannot be null or blank");
        }

        if (team == null || hackathon == null) {
            throw new IllegalArgumentException("Team and Hackathon cannot be null");
        }

        this.repositoryUrl = repositoryUrl;
        this.team = team;
        this.hackathon = hackathon;

        update();
    }



    /**
     * Returns the immutable reference of this submission.
     *
     * <p>
     * For GitHub submissions, this corresponds to the commit SHA
     * of the latest commit on the {@code main} branch.
     * </p>
     *
     * @return the stored commit SHA
     */
    @Override
    public String getImmutableReference() {
        return lastCommitSha;
    }

    /**
     * Updates the submission by:
     * <ul>
     *     <li>Fetching the latest commit SHA from the GitHub repository</li>
     *     <li>Refreshing the submission timestamp</li>
     * </ul>
     *
     * @throws IllegalStateException if the submission is not editable
     */
    public void update() {

        if (!isEditable()) {
            throw new IllegalStateException("Submission is not editable");
        }

        this.lastCommitSha = GitHubClient.getLastCommit(repositoryUrl);
        this.submittedAt = LocalDateTime.now();
    }
}
