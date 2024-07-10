package com.dperez.CarRegistry.config;

import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;


@EnableAsync
public class AsyncConfig {

    @Bean(name = "taskExecutor")
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5); // Número mínimo de hilos concurrentes
        executor.setMaxPoolSize(10); // Número máximo de hilos
        executor.setQueueCapacity(500); // Capacidad de la cola de tareas
        executor.setThreadNamePrefix("CarRegistryThread-"); // Prefijo de los nombres de los hilos
        executor.initialize();
        return executor;
    }
}






