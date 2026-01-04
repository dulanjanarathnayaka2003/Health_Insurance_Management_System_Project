package com.sliit.healthins.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Data
@Getter
@Setter
public class SystemMetricsDTO {
    private String status;
    private String uptime;
    private int activeConnections;
    private int totalUsers;
    private int activeUsers;
    private LocalDateTime timestamp;
    private String serverInfo;
    private String databaseStatus;
    private double memoryUsage;
    private double cpuUsage;

    public SystemMetricsDTO() {
    }

    public SystemMetricsDTO(String status, String uptime, int activeConnections, int totalUsers, 
                           int activeUsers, LocalDateTime timestamp, String serverInfo, 
                           String databaseStatus, double memoryUsage, double cpuUsage) {
        this.status = status;
        this.uptime = uptime;
        this.activeConnections = activeConnections;
        this.totalUsers = totalUsers;
        this.activeUsers = activeUsers;
        this.timestamp = timestamp;
        this.serverInfo = serverInfo;
        this.databaseStatus = databaseStatus;
        this.memoryUsage = memoryUsage;
        this.cpuUsage = cpuUsage;
    }
}
