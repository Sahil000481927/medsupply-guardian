/**
 * File Overview: Date and time utility functions
 * 
 * This file provides utility functions for formatting dates, calculating
 * time differences, and handling timestamp conversions used throughout
 * the application for expiration tracking and audit timestamps.
 * 
 * @author Sahil Patel
 * @version 1.0
 */

package com.sahilpatel.medsupplyguardian.utils

import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * Utility object for date and time operations.
 */
object DateUtils {
    
    /**
     * Default date format for display (e.g., "Nov 21, 2025").
     */
    private const val DATE_FORMAT_DISPLAY = "MMM dd, yyyy"
    
    /**
     * Date and time format for audit timestamps (e.g., "Nov 21, 2025 5:48 AM").
     */
    private const val DATETIME_FORMAT_DISPLAY = "MMM dd, yyyy h:mm a"
    
    /**
     * ISO format for data exchange (e.g., "2025-11-21").
     */
    private const val DATE_FORMAT_ISO = "yyyy-MM-dd"
    
    /**
     * Formats a Unix timestamp to a readable date string.
     * 
     * @param timestamp Unix timestamp in milliseconds
     * @param pattern Date format pattern (defaults to display format)
     * @return Formatted date string
     */
    fun formatDate(timestamp: Long, pattern: String = DATE_FORMAT_DISPLAY): String {
        val sdf = SimpleDateFormat(pattern, Locale.getDefault())
        return sdf.format(Date(timestamp))
    }
    
    /**
     * Formats a Unix timestamp to a readable date and time string.
     * 
     * @param timestamp Unix timestamp in milliseconds
     * @return Formatted date and time string
     */
    fun formatDateTime(timestamp: Long): String {
        return formatDate(timestamp, DATETIME_FORMAT_DISPLAY)
    }
    
    /**
     * Formats a Unix timestamp to ISO date format.
     * 
     * @param timestamp Unix timestamp in milliseconds
     * @return ISO formatted date string
     */
    fun formatDateISO(timestamp: Long): String {
        return formatDate(timestamp, DATE_FORMAT_ISO)
    }
    
    /**
     * Calculates the number of days between two timestamps.
     * 
     * @param startTimestamp Start time in milliseconds
     * @param endTimestamp End time in milliseconds
     * @return Number of days between the timestamps
     */
    fun daysBetween(startTimestamp: Long, endTimestamp: Long): Long {
        val diffMillis = endTimestamp - startTimestamp
        return TimeUnit.MILLISECONDS.toDays(diffMillis)
    }
    
    /**
     * Calculates days until a future timestamp from current time.
     * 
     * @param futureTimestamp Future time in milliseconds
     * @return Number of days until the timestamp (negative if in the past)
     */
    fun daysUntil(futureTimestamp: Long): Long {
        return daysBetween(System.currentTimeMillis(), futureTimestamp)
    }
    
    /**
     * Calculates days since a past timestamp from current time.
     * 
     * @param pastTimestamp Past time in milliseconds
     * @return Number of days since the timestamp (negative if in the future)
     */
    fun daysSince(pastTimestamp: Long): Long {
        return daysBetween(pastTimestamp, System.currentTimeMillis())
    }
    
    /**
     * Checks if a timestamp represents a date in the past.
     * 
     * @param timestamp Unix timestamp in milliseconds
     * @return true if the timestamp is in the past, false otherwise
     */
    fun isPast(timestamp: Long): Boolean {
        return timestamp < System.currentTimeMillis()
    }
    
    /**
     * Checks if a timestamp represents a date in the future.
     * 
     * @param timestamp Unix timestamp in milliseconds
     * @return true if the timestamp is in the future, false otherwise
     */
    fun isFuture(timestamp: Long): Boolean {
        return timestamp > System.currentTimeMillis()
    }
    
    /**
     * Gets the current Unix timestamp in milliseconds.
     * 
     * @return Current time as Unix timestamp
     */
    fun getCurrentTimestamp(): Long {
        return System.currentTimeMillis()
    }
    
    /**
     * Adds days to a timestamp.
     * 
     * @param timestamp Base timestamp in milliseconds
     * @param days Number of days to add (can be negative to subtract)
     * @return New timestamp with days added
     */
    fun addDays(timestamp: Long, days: Int): Long {
        return timestamp + TimeUnit.DAYS.toMillis(days.toLong())
    }
    
    /**
     * Formats days until expiration as a human-readable string.
     * 
     * @param daysUntilExpiry Number of days until expiration
     * @return Formatted string (e.g., "Expires in 15 days", "Expired 3 days ago")
     */
    fun formatExpiryStatus(daysUntilExpiry: Long): String {
        return when {
            daysUntilExpiry < 0 -> "Expired ${-daysUntilExpiry} days ago"
            daysUntilExpiry == 0L -> "Expires today"
            daysUntilExpiry == 1L -> "Expires tomorrow"
            else -> "Expires in $daysUntilExpiry days"
        }
    }
    
    /**
     * Parses an ISO date string to Unix timestamp.
     * 
     * @param dateString Date string in ISO format (yyyy-MM-dd)
     * @return Unix timestamp in milliseconds or null if parsing fails
     */
    fun parseISODate(dateString: String): Long? {
        return try {
            val sdf = SimpleDateFormat(DATE_FORMAT_ISO, Locale.getDefault())
            sdf.parse(dateString)?.time
        } catch (e: Exception) {
            null
        }
    }
}