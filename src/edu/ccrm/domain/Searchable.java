package edu.ccrm.domain;

/**
 * Interface for searchable entities in the CCRM system.
 * Demonstrates default methods, static methods, and generic interfaces.
 * 
 * @param <T> The type of search criteria this entity supports
 */
public interface Searchable<T> {
    
    /**
     * Checks if this entity matches the given search term.
     * 
     * @param searchTerm The term to search for
     * @return true if this entity matches the search term, false otherwise
     */
    boolean matches(T searchTerm);
    
    /**
     * Default method that provides a common implementation for getting the searchable type.
     * This demonstrates default methods in interfaces (Java 8+).
     * Classes implementing this interface can override this method if needed.
     * 
     * @return A string representing the type of searchable entity
     */
    default String getSearchableType() {
        return this.getClass().getSimpleName();
    }
    
    /**
     * Default method for case-insensitive string matching.
     * Provides a utility method for common string search operations.
     * 
     * @param text The text to search in
     * @param searchTerm The term to search for
     * @return true if the text contains the search term (case-insensitive), false otherwise
     */
    default boolean containsIgnoreCase(String text, String searchTerm) {
        if (text == null || searchTerm == null) {
            return false;
        }
        return text.toLowerCase().contains(searchTerm.toLowerCase());
    }
    
    /**
     * Static method to validate search terms.
     * Demonstrates static methods in interfaces (Java 8+).
     * 
     * @param searchTerm The search term to validate
     * @return true if the search term is valid (not null and not empty), false otherwise
     */
    static boolean isValidSearchTerm(String searchTerm) {
        return searchTerm != null && !searchTerm.trim().isEmpty();
    }
    
    /**
     * Static method to normalize search terms for consistent searching.
     * 
     * @param searchTerm The search term to normalize
     * @return The normalized search term (trimmed and lowercase)
     */
    static String normalizeSearchTerm(String searchTerm) {
        if (!isValidSearchTerm(searchTerm)) {
            return "";
        }
        return searchTerm.trim().toLowerCase();
    }
    
    /**
     * Default method for highlighting search matches in text.
     * This could be used in UI components to highlight search results.
     * 
     * @param text The original text
     * @param searchTerm The term that was searched for
     * @param highlightStart The string to insert before the match (e.g., "<mark>")
     * @param highlightEnd The string to insert after the match (e.g., "</mark>")
     * @return The text with search terms highlighted
     */
    default String highlightMatches(String text, String searchTerm, String highlightStart, String highlightEnd) {
        if (text == null || !isValidSearchTerm(searchTerm)) {
            return text;
        }
        
        String normalizedTerm = normalizeSearchTerm(searchTerm);
        String lowerText = text.toLowerCase();
        
        if (!lowerText.contains(normalizedTerm)) {
            return text;
        }
        
        // Simple highlighting - replace all occurrences
        StringBuilder result = new StringBuilder();
        int lastIndex = 0;
        int index = lowerText.indexOf(normalizedTerm);
        
        while (index != -1) {
            // Add text before the match
            result.append(text, lastIndex, index);
            // Add highlighted match
            result.append(highlightStart);
            result.append(text, index, index + normalizedTerm.length());
            result.append(highlightEnd);
            
            lastIndex = index + normalizedTerm.length();
            index = lowerText.indexOf(normalizedTerm, lastIndex);
        }
        
        // Add remaining text
        result.append(text.substring(lastIndex));
        return result.toString();
    }
}