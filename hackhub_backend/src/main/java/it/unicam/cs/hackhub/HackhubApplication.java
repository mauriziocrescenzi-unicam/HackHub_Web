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
package it.unicam.cs.hackhub;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
/**
 * Main entry point of the HackHub Spring Boot application.
 *
 * <p>
 * This class bootstraps and launches the Spring Boot framework, initializing the application
 * context, configuring components, and starting the embedded web server.
 * </p>
 *
 * <p>
 * The {@link SpringBootApplication} annotation enables autoconfiguration, component scanning,
 * and configuration support for the entire HackHub application.
 * </p>
 */
@SpringBootApplication
public class HackhubApplication {

    /**
     * Starts the HackHub Spring Boot application.
     *
     * <p>
     * This method delegates application startup to {@link SpringApplication}, which initializes
     * the Spring context and launches the embedded server.
     * </p>
     *
     * @param args command-line arguments passed to the application at startup
     */
	public static void main(String[] args) {
		SpringApplication.run(HackhubApplication.class, args);
	}

}
