/*
 * This file is generated by jOOQ.
 */
package models.enums


import models.Public

import org.jooq.Catalog
import org.jooq.EnumType
import org.jooq.Schema


/**
 * This class is generated by jOOQ.
 */
@Suppress("UNCHECKED_CAST")
enum class State(@get:JvmName("literal") public val literal: String) : EnumType {
    opening("opening"),
    closed("closed"),
    processing("processing");
    override fun getCatalog(): Catalog? = schema.catalog
    override fun getSchema(): Schema = Public.PUBLIC
    override fun getName(): String = "state"
    override fun getLiteral(): String = literal
}
