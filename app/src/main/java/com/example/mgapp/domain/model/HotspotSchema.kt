package com.example.mgapp.data.model

import androidx.annotation.StringRes

enum class FieldType {
    TEXT, NUMBER, DATE, DROPDOWN, CHECKBOX
}

// Validation rules
data class ValidationRule(
    val required: Boolean = false,
    val min: Double? = null,        // min length
    val max: Double? = null,        // max length
    val regex: String? = null,      // pattern
    val options: List<String>? = null // (dropdown)
)

data class FieldSchema(
    val id: String,
    @StringRes val labelRes: Int,
    val type: FieldType,
    val validation: ValidationRule
)

data class HotspotSchema(
    val hotspotId: String,
    val fields: List<FieldSchema>
)
