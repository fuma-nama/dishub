/*
 * This file is generated by jOOQ.
 */
package models.tables.records


import models.tables.Guild

import org.jooq.Field
import org.jooq.Record1
import org.jooq.Record3
import org.jooq.Row3
import org.jooq.impl.UpdatableRecordImpl


/**
 * This class is generated by jOOQ.
 */
@Suppress("UNCHECKED_CAST")
open class GuildRecord() : UpdatableRecordImpl<GuildRecord>(Guild.GUILD), Record3<Long?, Long?, Long?> {

    open var id: Long?
        set(value): Unit = set(0, value)
        get(): Long? = get(0) as Long?

    open var container: Long?
        set(value): Unit = set(1, value)
        get(): Long? = get(1) as Long?

    open var managerRole: Long?
        set(value): Unit = set(2, value)
        get(): Long? = get(2) as Long?

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    override fun key(): Record1<Long?> = super.key() as Record1<Long?>

    // -------------------------------------------------------------------------
    // Record3 type implementation
    // -------------------------------------------------------------------------

    override fun fieldsRow(): Row3<Long?, Long?, Long?> = super.fieldsRow() as Row3<Long?, Long?, Long?>
    override fun valuesRow(): Row3<Long?, Long?, Long?> = super.valuesRow() as Row3<Long?, Long?, Long?>
    override fun field1(): Field<Long?> = Guild.GUILD.ID
    override fun field2(): Field<Long?> = Guild.GUILD.CONTAINER
    override fun field3(): Field<Long?> = Guild.GUILD.MANAGER_ROLE
    override fun component1(): Long? = id
    override fun component2(): Long? = container
    override fun component3(): Long? = managerRole
    override fun value1(): Long? = id
    override fun value2(): Long? = container
    override fun value3(): Long? = managerRole

    override fun value1(value: Long?): GuildRecord {
        this.id = value
        return this
    }

    override fun value2(value: Long?): GuildRecord {
        this.container = value
        return this
    }

    override fun value3(value: Long?): GuildRecord {
        this.managerRole = value
        return this
    }

    override fun values(value1: Long?, value2: Long?, value3: Long?): GuildRecord {
        this.value1(value1)
        this.value2(value2)
        this.value3(value3)
        return this
    }

    /**
     * Create a detached, initialised GuildRecord
     */
    constructor(id: Long? = null, container: Long? = null, managerRole: Long? = null): this() {
        this.id = id
        this.container = container
        this.managerRole = managerRole
    }
}
