package com.sliit.healthins.pattern.command;

/**
 * Command Pattern: Defines the interface for customer support operations
 * This allows for encapsulating requests as objects and supporting undo operations
 */
public interface CustomerSupportCommand {
    
    /**
     * Executes the command
     * @return Result of the command execution
     */
    Object execute();
    
    /**
     * Undoes the command (if supported)
     * @return Result of the undo operation
     */
    default Object undo() {
        throw new UnsupportedOperationException("Undo not supported for this command");
    }
    
    /**
     * Gets the command description
     * @return Description of what this command does
     */
    String getDescription();
    
    /**
     * Checks if the command supports undo operation
     * @return true if undo is supported, false otherwise
     */
    default boolean supportsUndo() {
        return false;
    }
}
