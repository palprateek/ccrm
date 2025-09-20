package edu.ccrm.io;

import edu.ccrm.config.AppConfig;
import edu.ccrm.util.RecursiveFileUtils;
import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.stream.Stream;

/**
 * Service for handling data backups using NIO.2.
 */
public class BackupService {
    private final Path dataDir;
    private final Path backupRootDir;
    private final DateTimeFormatter timestampFormatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");

    public BackupService() {
        AppConfig config = AppConfig.getInstance();
        this.dataDir = Paths.get(config.getDataDirectory());
        this.backupRootDir = Paths.get(config.getBackupDirectory());
    }

    public String backupData() {
        String timestamp = LocalDateTime.now().format(timestampFormatter);
        Path backupPath = backupRootDir.resolve("backup_" + timestamp);

        try {
            // Create backup root directory if it doesn't exist
            if (Files.notExists(backupRootDir)) {
                Files.createDirectories(backupRootDir);
            }
            
            // Create timestamped backup directory
            Files.createDirectory(backupPath);

            // Check if data directory exists
            if (Files.notExists(dataDir)) {
                System.out.println("No data directory found to backup.");
                Files.delete(backupPath); // Clean up empty backup directory
                return null;
            }

            // Copy all files using NIO.2 with proper error handling
            try (Stream<Path> pathStream = Files.walk(dataDir)) {
                long copiedFiles = pathStream
                    .filter(Files::isRegularFile) // Only copy regular files
                    .mapToLong(source -> {
                        try {
                            Path relativePath = dataDir.relativize(source);
                            Path destination = backupPath.resolve(relativePath);
                            
                            // Create parent directories if they don't exist
                            Path parent = destination.getParent();
                            if (parent != null && Files.notExists(parent)) {
                                Files.createDirectories(parent);
                            }
                            
                            Files.copy(source, destination, StandardCopyOption.REPLACE_EXISTING, 
                                     StandardCopyOption.COPY_ATTRIBUTES);
                            return 1;
                        } catch (IOException e) {
                            System.err.println("Failed to copy file: " + source + " - " + e.getMessage());
                            return 0;
                        }
                    })
                    .sum();
                    
                // Calculate backup size
                long backupSize = RecursiveFileUtils.calculateDirectorySize(backupPath.toString());
                String sizeFormatted = RecursiveFileUtils.formatFileSize(backupSize);
                
                System.out.println("Backup created successfully:");
                System.out.println("  Location: " + backupPath.toAbsolutePath());
                System.out.println("  Files copied: " + copiedFiles);
                System.out.println("  Total size: " + sizeFormatted);
                
                return backupPath.toString();
            }

        } catch (IOException e) {
            System.err.println("Error creating backup: " + e.getMessage());
            // Clean up partial backup on error
            try {
                if (Files.exists(backupPath)) {
                    RecursiveFileUtils.deleteDirectoryRecursively(backupPath);
                }
            } catch (IOException cleanupError) {
                System.err.println("Failed to clean up partial backup: " + cleanupError.getMessage());
            }
            return null;
        }
    }
    
    public boolean restoreBackup(String backupTimestamp) {
        Path backupPath = backupRootDir.resolve("backup_" + backupTimestamp);
        
        if (Files.notExists(backupPath)) {
            System.err.println("Backup not found: " + backupPath);
            return false;
        }
        
        try {
            // Create data directory if it doesn't exist
            if (Files.notExists(dataDir)) {
                Files.createDirectories(dataDir);
            }
            
            // Clear existing data directory
            if (Files.exists(dataDir)) {
                try (Stream<Path> pathStream = Files.walk(dataDir)) {
                    pathStream.filter(Files::isRegularFile)
                             .forEach(file -> {
                                 try {
                                     Files.delete(file);
                                 } catch (IOException e) {
                                     System.err.println("Failed to delete file during restore: " + file);
                                 }
                             });
                }
            }
            
            // Restore files from backup
            try (Stream<Path> pathStream = Files.walk(backupPath)) {
                long restoredFiles = pathStream
                    .filter(Files::isRegularFile)
                    .mapToLong(source -> {
                        try {
                            Path relativePath = backupPath.relativize(source);
                            Path destination = dataDir.resolve(relativePath);
                            
                            // Create parent directories if needed
                            Path parent = destination.getParent();
                            if (parent != null && Files.notExists(parent)) {
                                Files.createDirectories(parent);
                            }
                            
                            Files.copy(source, destination, StandardCopyOption.REPLACE_EXISTING,
                                     StandardCopyOption.COPY_ATTRIBUTES);
                            return 1;
                        } catch (IOException e) {
                            System.err.println("Failed to restore file: " + source + " - " + e.getMessage());
                            return 0;
                        }
                    })
                    .sum();
                    
                System.out.println("Backup restored successfully:");
                System.out.println("  From: " + backupPath.toAbsolutePath());
                System.out.println("  Files restored: " + restoredFiles);
                return true;
            }
            
        } catch (IOException e) {
            System.err.println("Error restoring backup: " + e.getMessage());
            return false;
        }
    }
    
    public void listBackups() {
        if (Files.notExists(backupRootDir)) {
            System.out.println("No backup directory found.");
            return;
        }
        
        try (Stream<Path> backups = Files.list(backupRootDir)) {
            System.out.println("Available backups:");
            backups.filter(Files::isDirectory)
                   .filter(path -> path.getFileName().toString().startsWith("backup_"))
                   .sorted((p1, p2) -> p2.getFileName().toString().compareTo(p1.getFileName().toString())) // Newest first
                   .forEach(backup -> {
                       try {
                           String name = backup.getFileName().toString();
                           String timestamp = name.substring("backup_".length());
                           long size = RecursiveFileUtils.calculateDirectorySize(backup.toString());
                           String sizeFormatted = RecursiveFileUtils.formatFileSize(size);
                           long fileCount = Files.walk(backup).filter(Files::isRegularFile).count();
                           
                           System.out.printf("  %s - %s (%d files)%n", timestamp, sizeFormatted, fileCount);
                       } catch (IOException e) {
                           System.err.println("Error reading backup info: " + backup.getFileName());
                       }
                   });
        } catch (IOException e) {
            System.err.println("Error listing backups: " + e.getMessage());
        }
    }
    
    public boolean deleteBackup(String backupTimestamp) {
        Path backupPath = backupRootDir.resolve("backup_" + backupTimestamp);
        
        if (Files.notExists(backupPath)) {
            System.err.println("Backup not found: " + backupTimestamp);
            return false;
        }
        
        try {
            RecursiveFileUtils.deleteDirectoryRecursively(backupPath);
            System.out.println("Backup deleted: " + backupTimestamp);
            return true;
        } catch (IOException e) {
            System.err.println("Error deleting backup: " + e.getMessage());
            return false;
        }
    }
}