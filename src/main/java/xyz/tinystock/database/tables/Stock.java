/*
 * This file is generated by jOOQ.
*/
package xyz.tinystock.database.tables;


import java.util.Arrays;
import java.util.List;

import javax.annotation.Generated;

import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Index;
import org.jooq.Name;
import org.jooq.Schema;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.UniqueKey;
import org.jooq.impl.DSL;
import org.jooq.impl.TableImpl;
import org.jooq.types.UInteger;

import xyz.tinystock.database.Indexes;
import xyz.tinystock.database.Keys;
import xyz.tinystock.database.Stockdb;
import xyz.tinystock.database.tables.records.StockRecord;


/**
 * This class is generated by jOOQ.
 */
@Generated(
    value = {
        "http://www.jooq.org",
        "jOOQ version:3.10.2"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Stock extends TableImpl<StockRecord> {

    private static final long serialVersionUID = -1253684290;

    /**
     * The reference instance of <code>stockdb.STOCK</code>
     */
    public static final Stock STOCK = new Stock();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<StockRecord> getRecordType() {
        return StockRecord.class;
    }

    /**
     * The column <code>stockdb.STOCK.ID_COMPONENT</code>.
     */
    public final TableField<StockRecord, UInteger> ID_COMPONENT = createField("ID_COMPONENT", org.jooq.impl.SQLDataType.INTEGERUNSIGNED.nullable(false), this, "");

    /**
     * The column <code>stockdb.STOCK.QUANTITY</code>.
     */
    public final TableField<StockRecord, UInteger> QUANTITY = createField("QUANTITY", org.jooq.impl.SQLDataType.INTEGERUNSIGNED.nullable(false).defaultValue(org.jooq.impl.DSL.inline("0", org.jooq.impl.SQLDataType.INTEGERUNSIGNED)), this, "");

    /**
     * Create a <code>stockdb.STOCK</code> table reference
     */
    public Stock() {
        this(DSL.name("STOCK"), null);
    }

    /**
     * Create an aliased <code>stockdb.STOCK</code> table reference
     */
    public Stock(String alias) {
        this(DSL.name(alias), STOCK);
    }

    /**
     * Create an aliased <code>stockdb.STOCK</code> table reference
     */
    public Stock(Name alias) {
        this(alias, STOCK);
    }

    private Stock(Name alias, Table<StockRecord> aliased) {
        this(alias, aliased, null);
    }

    private Stock(Name alias, Table<StockRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, "");
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
        return Arrays.<Index>asList(Indexes.STOCK_PRIMARY);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UniqueKey<StockRecord> getPrimaryKey() {
        return Keys.KEY_STOCK_PRIMARY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<UniqueKey<StockRecord>> getKeys() {
        return Arrays.<UniqueKey<StockRecord>>asList(Keys.KEY_STOCK_PRIMARY);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ForeignKey<StockRecord, ?>> getReferences() {
        return Arrays.<ForeignKey<StockRecord, ?>>asList(Keys.STOCK_COMPONENT_FK);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Stock as(String alias) {
        return new Stock(DSL.name(alias), this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Stock as(Name alias) {
        return new Stock(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public Stock rename(String name) {
        return new Stock(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public Stock rename(Name name) {
        return new Stock(name, null);
    }
}