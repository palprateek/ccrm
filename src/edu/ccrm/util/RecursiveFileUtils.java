package edu.ccrm.util;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.stream.Stream;

/**
 * Comprehensive utility for recursive file operations using NIO.2 and Streams.
 */
public class RecursiveFileUtils {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    /**
     * Recursively calculates the total size of a directory.
     * @param dirPath The path to the directory.
     * @return Total size in bytes.
     */
    public static long calculateDirectorySize(String dirPath) {
        return calculateDirectorySize(Paths.get(dirPath));
    }
    
    /**
     * Recursively calculates the total size of a directory.
     * @param dirPath The path to the directory.
     * @return Total size in bytes.
     */
    public static long calculateDirectorySize(Path dirPath) {
        try (Stream<Path> paths = Files.walk(dirPath)) {
            return paths
                .filter(Files::isRegularFile)
                .mapToLong(path -> {
                    try {
                        return Files.size(path);
                    } catch (IOException e) {
                        System.err.println("Could not get size of file: " + path + " - " + e.getMessage());
                        return 0L;
                    }
                })
                .sum();
        } catch (IOException e) {
            System.err.println("Could not calculate directory size for: " + dirPath + " - " + e.getMessage());
            return 0L;
        }
    }
    
    /**
     * Recursively lists all files in a directory by depth level.
     * @param dirPath The directory path to explore.
     * @param maxDepth Maximum depth to traverse (0 = only current directory).
     */
    public static void listFilesByDepth(String dirPath, int maxDepth) {
        listFilesByDepth(Paths.get(dirPath), maxDepth);
    }
    
    /**
     * Recursively lists all files in a directory by depth level.
     * @param dirPath The directory path to explore.
     * @param maxDepth Maximum depth to traverse (0 = only current directory).
     */
    public static void listFilesByDepth(Path dirPath, int maxDepth) {
        if (Files.notExists(dirPath)) {
            System.err.println("Directory does not exist: " + dirPath);
            return;
        }
        
        try (Stream<Path> paths = Files.walk(dirPath, maxDepth)) {
            System.out.println("Files in " + dirPath.toAbsolutePath() + " (max depth: " + maxDepth + "):");
            
            paths.sorted()
                 .forEach(path -> {
                     try {
                         int depth = path.getNameCount() - dirPath.getNameCount();
                         String indent = "  ".repeat(depth);
                         String type = Files.isDirectory(path) ? "[DIR]" : "[FILE]";
                         String name = path.getFileName().toString();
                         
                         if (Files.isRegularFile(path)) {
                             long size = Files.size(path);
                             String sizeFormatted = formatFileSize(size);
                             LocalDateTime modified = LocalDateTime.ofInstant(
                                 Files.getLastModifiedTime(path).toInstant(), 
                                 ZoneId.systemDefault()
                             );
                             System.out.printf("%s%s %s (%s) - %s%n", 
                                 indent, type, name, sizeFormatted, modified.format(DATE_FORMATTER));
                         } else if (Files.isDirectory(path) && depth > 0) {
                             long fileCount = countFilesInDirectory(path);
                             System.out.printf("%s%s %s (%d items)%n", indent, type, name, fileCount);
                         }
                     } catch (IOException e) {
                         System.err.println("Error reading file info: " + path + " - " + e.getMessage());
                     }
                 });
        } catch (IOException e) {
            System.err.println("Error listing files: " + e.getMessage());
        }
    }
    
    /**
     * Counts the number of files and directories in a directory (non-recursive).
     * @param dirPath The directory path.
     * @return Number of items in the directory.
     */
    public static long countFilesInDirectory(Path dirPath) {
        try (Stream<Path> paths = Files.list(dirPath)) {
            return paths.count();
        } catch (IOException e) {
            return 0;
        }
    }
    
    /**
     * Recursively counts total files in a directory tree.
     * @param dirPath The directory path.
     * @return Total number of files (excluding directories).
     */
    public static long countFilesRecursively(String dirPath) {
        return countFilesRecursively(Paths.get(dirPath));
    }
    
    /**
     * Recursively counts total files in a directory tree.
     * @param dirPath The directory path.
     * @return Total number of files (excluding directories).
     */
    public static long countFilesRecursively(Path dirPath) {
        try (Stream<Path> paths = Files.walk(dirPath)) {
            return paths.filter(Files::isRegularFile).count();
        } catch (IOException e) {
            System.err.println("Error counting files: " + e.getMessage());
            return 0;
        }
    }
    
    /**
     * Formats file size in human-readable format.
     * @param bytes Size in bytes.
     * @return Formatted size string.
     */
    public static String formatFileSize(long bytes) {
        if (bytes < 1024) return bytes + " B";
        
        String[] units = {"KB", "MB", "GB", "TB"};
        int unitIndex = 0;
        double size = bytes;
        
        while (size >= 1024 && unitIndex < units.length - 1) {
            size /= 1024;
            unitIndex++;
        }
        
        return String.format("%.2f %s", size, units[unitIndex]);
    }
    
    /**
     * Recursively deletes a directory and all its contents.
     * @param dirPath The directory to delete.
     * @throws IOException If deletion fails.
     */
    public static void deleteDirectoryRecursively(Path dirPath) throws IOException {
        if (Files.notExists(dirPath)) {
            return;
        }
        
        try (Stream<Path> paths = Files.walk(dirPath)) {
            paths.sorted(Comparator.reverseOrder()) // Delete files before directories
                 .forEach(path -> {
                     try {
                         Files.delete(path);
                     } catch (IOException e) {
                         System.err.println("Failed to delete: " + path + " - " + e.getMessage());
                     }
                 });
        }
    }
    
    /**
     * Finds files matching a pattern recursively.
     * @param dirPath Directory to search in.
     * @param pattern Glob pattern to match (e.g., "*.csv", "backup_*").
     * @return Stream of matching paths.
     */
    public static Stream<Path> findFiles(Path dirPath, String pattern) {
        try {
            PathMatcher matcher = FileSystems.getDefault().getPathMatcher("glob:" + pattern);
            return Files.walk(dirPath)
                        .filter(Files::isRegularFile)
                        .filter(path -> matcher.matches(path.getFileName()));
        } catch (IOException e) {
            System.err.println("Error searching for files: " + e.getMessage());
            return Stream.empty();
        }
    }
    
    /**
     * Prints comprehensive directory statistics.
     * @param dirPath Directory to analyze.
     */
    public static void printDirectoryStats(String dirPath) {
        printDirectoryStats(Paths.get(dirPath));
    }
    
    /**
     * Prints comprehensive directory statistics.
     * @param dirPath Directory to analyze.
     */
    public static void printDirectoryStats(Path dirPath) {
        if (Files.notExists(dirPath)) {
            System.err.println("Directory does not exist: " + dirPath);
            return;
        }
        
        try {
            long totalSize = calculateDirectorySize(dirPath);
            long fileCount = countFilesRecursively(dirPath);
            long dirCount = Files.walk(dirPath).filter(Files::isDirectory).count() - 1; // Exclude root
            
            System.out.println("Directory Statistics for: " + dirPath.toAbsolutePath());
            System.out.println("  Total size: " + formatFileSize(totalSize));
            System.out.println("  Files: " + fileCount);
            System.out.println("  Directories: " + dirCount);
            System.out.println("  Total items: " + (fileCount + dirCount));
            
            // Find largest files
            System.out.println("  Largest files:");
            try (Stream<Path> paths = Files.walk(dirPath)) {
                paths.filter(Files::isRegularFile)
                     .sorted((p1, p2) -> {
                         try {
                             return Long.compare(Files.size(p2), Files.size(p1));
                         } catch (IOException e) {
                             return 0;
                         }
                     })
                     .limit(5)
                     .forEach(path -> {
                         try {
                             long size = Files.size(path);
                             String relativePath = dirPath.relativize(path).toString();
                             System.out.println("    " + relativePath + " - " + formatFileSize(size));
                         } catch (IOException e) {
                             System.err.println("    Error reading file: " + path);
                         }
                     });
            }
            
        } catch (IOException e) {
            System.err.println("Error analyzing directory: " + e.getMessage());
        }
    }
}