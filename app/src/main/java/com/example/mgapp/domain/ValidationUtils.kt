package com.example.mgapp.domain

import com.example.mgapp.R
import com.example.mgapp.data.model.FieldSchema
import com.example.mgapp.data.model.FieldType
import com.example.mgapp.data.model.HotspotSchema

data class ValidationError(
    val fieldId: String,
    val messageRes: Int
)

fun validateField(value: String?, schema: FieldSchema): List<ValidationError> {
    val rule = schema.validation
    val errors = mutableListOf<ValidationError>()

    // Check if field is required and empty
    if (rule.required && (value == null || value.isBlank())) {
        errors += ValidationError(schema.id, R.string.err_required)
        return errors
    }

    // Apply validation depending on field type
    when (schema.type) {
        FieldType.TEXT -> {
            val len = value?.length ?: 0
            // Minimum and maximum length validation
            if (rule.min != null && len < rule.min!!) errors += ValidationError(schema.id, R.string.err_min_length)
            if (rule.max != null && len > rule.max!!) errors += ValidationError(schema.id, R.string.err_max_length)
            // Regex pattern validation
            if (!rule.regex.isNullOrBlank() && !(value?.matches(rule.regex!!.toRegex()) ?: false))
                errors += ValidationError(schema.id, R.string.err_invalid_format)
        }

        FieldType.NUMBER -> {
            if (!value.isNullOrBlank()) {
                val num = value.toDoubleOrNull()
                // Check if input is a valid number
                if (num == null) errors += ValidationError(schema.id, R.string.err_invalid_number)
                else {
                    // Validate number range
                    if (rule.min != null && num < rule.min!!) errors += ValidationError(schema.id, R.string.err_too_low)
                    if (rule.max != null && num > rule.max!!) errors += ValidationError(schema.id, R.string.err_too_high)
                }
            }
        }

        FieldType.DATE -> {

        }

        FieldType.DROPDOWN -> {
            // Check if selected value exists in allowed options
            if (!rule.options.isNullOrEmpty() && value != null && value !in rule.options)
                errors += ValidationError(schema.id, R.string.err_invalid_option)
        }

        FieldType.CHECKBOX -> {
            // (No specific checkbox validation yet)
        }
    }

    return errors
}

// Validate all fields in the form based on the schema
fun validateForm(values: Map<String, String?>, schema: HotspotSchema): Map<String, List<ValidationError>> {
    return schema.fields.associate { f -> f.id to validateField(values[f.id], f) }
}
