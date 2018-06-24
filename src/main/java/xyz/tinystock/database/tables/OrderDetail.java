/*
 * This file is generated by jOOQ.
 */
package xyz.tinystock.database.tables;


import java.util.Arrays;
import java.util.List;

import javax.annotation.processing.Generated;

import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Index;
import org.jooq.Name;
import org.jooq.Schema;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.impl.DSL;
import org.jooq.impl.TableImpl;
import org.jooq.types.UInteger;

import xyz.tinystock.database.Indexes;
import xyz.tinystock.database.Keys;
import xyz.tinystock.database.Stockdb;
import xyz.tinystock.database.tables.records.OrderDetailRecord;


/**
 * This class is generated by jOOQ.
 */
@Generated(
    value = {
        "http://www.jooq.org",
        "jOOQ version:3.11.0"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class OrderDetail extends TableImpl<OrderDetailRecord> {

    private static final long serialVersionUID = 600837456;

    /**
     * The reference instance of <code>stockdb.ORDER_DETAIL</code>
     */
    public static final OrderDetail ORDER_DETAIL = new OrderDetail();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<OrderDetailRecord> getRecordType() {
        return OrderDetailRecord.class;
    }

    /**
     * The column <code>stockdb.ORDER_DETAIL.ID_ORDER</code>.
     */
    public final TableField<OrderDetailRecord, UInteger> ID_ORDER = createField("ID_ORDER", org.jooq.impl.SQLDataType.INTEGERUNSIGNED.nullable(false), this, "");

    /**
     * The column <code>stockdb.ORDER_DETAIL.ID_COMPONENT</code>.
     */
    public final TableField<OrderDetailRecord, UInteger> ID_COMPONENT = createField("ID_COMPONENT", org.jooq.impl.SQLDataType.INTEGERUNSIGNED.nullable(false), this, "");

    /**
     * The column <code>stockdb.ORDER_DETAIL.QUANTITY</code>.
     */
    public final TableField<OrderDetailRecord, UInteger> QUANTITY = createField("QUANTITY", org.jooq.impl.SQLDataType.INTEGERUNSIGNED.nullable(false), this, "");

    /**
     * The column <code>stockdb.ORDER_DETAIL.TOTAL_PRICE</code>. In cents
     */
    public final TableField<OrderDetailRecord, UInteger> TOTAL_PRICE = createField("TOTAL_PRICE", org.jooq.impl.SQLDataType.INTEGERUNSIGNED.nullable(false), this, "In cents");

    /**
     * Create a <code>stockdb.ORDER_DETAIL</code> table reference
     */
    public OrderDetail() {
        this(DSL.name("ORDER_DETAIL"), null);
    }

    /**
     * Create an aliased <code>stockdb.ORDER_DETAIL</code> table reference
     */
    public OrderDetail(String alias) {
        this(DSL.name(alias), ORDER_DETAIL);
    }

    /**
     * Create an aliased <code>stockdb.ORDER_DETAIL</code> table reference
     */
    public OrderDetail(Name alias) {
        this(alias, ORDER_DETAIL);
    }

    private OrderDetail(Name alias, Table<OrderDetailRecord> aliased) {
        this(alias, aliased, null);
    }

    private OrderDetail(Name alias, Table<OrderDetailRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Schema getSchema() {
        return Stockdb.STOCKDB;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Index> getIndexes() {
        return Arrays.<Index>asList(Indexes.ORDER_DETAIL_ORDER_DETAIL_COMPONENT_FK, Indexes.ORDER_DETAIL_ORDER_DETAIL_ORDER_FK);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ForeignKey<OrderDetailRecord, ?>> getReferences() {
        return Arrays.<ForeignKey<OrderDetailRecord, ?>>asList(Keys.ORDER_DETAIL_ORDER_FK, Keys.ORDER_DETAIL_COMPONENT_FK);
    }

    public Order order() {
        return new Order(this, Keys.ORDER_DETAIL_ORDER_FK);
    }

    public Component component() {
        return new Component(this, Keys.ORDER_DETAIL_COMPONENT_FK);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public OrderDetail as(String alias) {
        return new OrderDetail(DSL.name(alias), this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public OrderDetail as(Name alias) {
        return new OrderDetail(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public OrderDetail rename(String name) {
        return new OrderDetail(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public OrderDetail rename(Name name) {
        return new OrderDetail(name, null);
    }
}
