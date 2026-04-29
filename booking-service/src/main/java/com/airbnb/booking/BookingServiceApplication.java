package com.airbnb.booking;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * Booking Microservice - Senior-Level Architecture
 * 
 * Features:
 * - Java 21 Virtual Threads for high concurrency
 * - Shared MongoDB with Node.js application
 * - REST API for booking management
 * - Conflict detection for overlapping bookings
 * 
 * @author Your Name
 * @version 1.0.0
 */
@SpringBootApplication
@EnableAsync
public class BookingServiceApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(BookingServiceApplication.class, args);
        
        System.out.println("""
            
            ╔════════════════════════════════════════════════════════╗
            ║  🚀 Booking Microservice - STARTED                     ║
            ║  📡 Port: 8080                                         ║
            ║  🧵 Virtual Threads: ENABLED                           ║
            ║  🗄️  Database: MongoDB (Shared)                        ║
            ║  🔗 Health: http://localhost:8080/actuator/health      ║
            ╚════════════════════════════════════════════════════════╝
            """);
    }
}