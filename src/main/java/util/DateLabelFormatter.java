// src/main/java/com/fasttracklogistics/util/DateLabelFormatter.java

package com.fasttracklogistics.util;

import javax.swing.JFormattedTextField.AbstractFormatter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * A formatter class for JDatePickerImpl to handle date parsing and formatting.
 * This class ensures that the date picked from the JDatePicker is correctly
 * converted to and from a String representation (yyyy-MM-dd).
 */
public class DateLabelFormatter extends AbstractFormatter {

    // Define the date format pattern
    private String datePattern = "yyyy-MM-dd";
    // Create a SimpleDateFormat instance with the defined pattern
    private SimpleDateFormat dateFormatter = new SimpleDateFormat(datePattern);

    /**
     * Parses the given text into a Date object.
     *
     * @param text The text to parse.
     * @return A Date object representing the parsed date.
     * @throws ParseException If the text cannot be parsed into a date.
     */
    @Override
    public Object stringToValue(String text) throws ParseException {
        // Parse the string using the SimpleDateFormat and return a Date object
        return dateFormatter.parseObject(text);
    }

    /**
     * Formats the given object (expected to be a Date or Calendar) into a string.
     *
     * @param value The object to format.
     * @return A string representation of the date.
     * @throws ParseException If the object cannot be formatted.
     */
    @Override
    public String valueToString(Object value) throws ParseException {
        // If the value is a Calendar, get its Date representation
        if (value != null) {
            Calendar cal = (Calendar) value;
            return dateFormatter.format(cal.getTime());
        }
        // Return an empty string if the value is null
        return "";
    }
}
