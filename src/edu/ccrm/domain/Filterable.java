package edu.ccrm.domain;

/**
 * Interface for filterable entities in the CCRM system.
 * This interface intentionally has a conflicting default method with Searchable
 * to demonstrate the diamond problem and how Java resolves it.
 * 
 * @param <T> The type of filter criteria this entity supports
 */
public interface Filterable<T> {
    
    /**
     * Checks if this entity matches the given filter criteria.
     * 
     * @param filter The filter criteria to apply
     * @return true if this entity matches the filter, false otherwise
     */
    boolean matchesFilter(T filter);
    
    /**
     * Default method that conflicts with Searchable.getSearchableType().
     * This creates a diamond problem that implementing classes must resolve.
     * 
     * @return A string representing the type of filterable entity
     */
    default String getSearchableType() {
        return "Filterable " + this.getClass().getSimpleName();
    }
    
    /**
     * Default method for checking if a filter is active/enabled.
     * 
     * @param filter The filter to check
     * @return true if the filter is considered active, false otherwise
     */
    default boolean isFilterActive(T filter) {
        return filter != null;
    }
    
    /**
     * Default method for getting filter description.
     * Useful for logging or debugging filter operations.
     * 
     * @param filter The filter to describe
     * @return A string description of the filter
     */
    default String getFilterDescription(T filter) {
        if (filter == null) {
            return "No filter applied";
        }
        return "Filter: " + filter.toString();
    }
    
    /**
     * Static method to validate filter criteria.
     * 
     * @param filter The filter to validate
     * @return true if the filter is valid, false otherwise
     */
    static boolean isValidFilter(Object filter) {
        return filter != null;
    }
    
    /**
     * Static method for combining multiple filters with AND logic.
     * This demonstrates how static methods can provide utility functionality.
     * 
     * @param <F> The filter type
     * @param entity The entity to test
     * @param filters The filters to apply (all must match)
     * @return true if the entity matches all filters, false otherwise
     */
    @SafeVarargs
    static <F> boolean matchesAllFilters(Filterable<F> entity, F... filters) {
        if (filters == null || filters.length == 0) {
            return true; // No filters means everything matches
        }
        
        for (F filter : filters) {
            if (isValidFilter(filter) && !entity.matchesFilter(filter)) {
                return false;
            }
        }
        return true;
    }
    
    /**
     * Static method for combining multiple filters with OR logic.
     * 
     * @param <F> The filter type
     * @param entity The entity to test
     * @param filters The filters to apply (any can match)
     * @return true if the entity matches at least one filter, false otherwise
     */
    @SafeVarargs
    static <F> boolean matchesAnyFilter(Filterable<F> entity, F... filters) {
        if (filters == null || filters.length == 0) {
            return true; // No filters means everything matches
        }
        
        for (F filter : filters) {
            if (isValidFilter(filter) && entity.matchesFilter(filter)) {
                return true;
            }
        }
        return false;
    }
}