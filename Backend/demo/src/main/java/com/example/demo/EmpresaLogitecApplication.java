package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class EmpresaLogitecApplication {

    public static void main(String[] args) {
        SpringApplication.run(EmpresaLogitecApplication.class, args);
        System.out.println("===========================================");
        System.out.println("  Sistema LogiTrack iniciado correctamente");
        System.out.println("  Puerto: 8080");
        System.out.println("  API: http://localhost:8080/api");
        System.out.println("===========================================");
    }
}
