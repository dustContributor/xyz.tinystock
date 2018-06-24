/*
 * This file is generated by jOOQ.
 */
package xyz.tinystock.database;


import javax.annotation.processing.Generated;

import org.jooq.Index;
import org.jooq.OrderField;
import org.jooq.impl.Internal;

import xyz.tinystock.database.tables.Component;
import xyz.tinystock.database.tables.Customer;
import xyz.tinystock.database.tables.Order;
import xyz.tinystock.database.tables.OrderDetail;
import xyz.tinystock.database.tables.Stock;
import xyz.tinystock.database.tables.User;


/**
 * A class modelling indexes of tables of the <code>stockdb</code> schema.
 */
@Generated(
    value = {
        "http://www.jooq.org",
        "jOOQ version:3.11.0"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Indexes {

    // -------------------------------------------------------------------------
    // INDEX definitions
    // -------------------------------------------------------------------------

    public static final Index COMPONENT_COMPONENT_UN = Indexes0.COMPONENT_COMPONENT_UN;
    public static final Index COMPONENT_PRIMARY = Indexes0.COMPONENT_PRIMARY;
    public static final Index CUSTOMER_PRIMARY = Indexes0.CUSTOMER_PRIMARY;
    public static final Index ORDER_ORDER_CUSTOMER_FK = Indexes0.ORDER_ORDER_CUSTOMER_FK;
    public static final Index ORDER_PRIMARY = Indexes0.ORDER_PRIMARY;
    public static final Index ORDER_DETAIL_ORDER_DETAIL_COMPONENT_FK = Indexes0.ORDER_DETAIL_ORDER_DETAIL_COMPONENT_FK;
    public static final Index ORDER_DETAIL_ORDER_DETAIL_ORDER_FK = Indexes0.ORDER_DETAIL_ORDER_DETAIL_ORDER_FK;
    public static final Index STOCK_PRIMARY = Indexes0.STOCK_PRIMARY;
    public static final Index USER_PRIMARY = Indexes0.USER_PRIMARY;
    public static final Index USER_USER_UN = Indexes0.USER_USER_UN;

    // -------------------------------------------------------------------------
    // [#1459] distribute members to avoid static initialisers > 64kb
    // -------------------------------------------------------------------------

    private static class Indexes0 {
        public static Index COMPONENT_COMPONENT_UN = Internal.createIndex("COMPONENT_UN", Component.COMPONENT, new OrderField[] { Component.COMPONENT.CODE }, true);
        public static Index COMPONENT_PRIMARY = Internal.createIndex("PRIMARY", Component.COMPONENT, new OrderField[] { Component.COMPONENT.ID }, true);
        public static Index CUSTOMER_PRIMARY = Internal.createIndex("PRIMARY", Customer.CUSTOMER, new OrderField[] { Customer.CUSTOMER.ID }, true);
        public static Index ORDER_ORDER_CUSTOMER_FK = Internal.createIndex("ORDER_CUSTOMER_FK", Order.ORDER, new OrderField[] { Order.ORDER.ID_CUSTOMER }, false);
        public static Index ORDER_PRIMARY = Internal.createIndex("PRIMARY", Order.ORDER, new OrderField[] { Order.ORDER.ID }, true);
        public static Index ORDER_DETAIL_ORDER_DETAIL_COMPONENT_FK = Internal.createIndex("ORDER_DETAIL_COMPONENT_FK", OrderDetail.ORDER_DETAIL, new OrderField[] { OrderDetail.ORDER_DETAIL.ID_COMPONENT }, false);
        public static Index ORDER_DETAIL_ORDER_DETAIL_ORDER_FK = Internal.createIndex("ORDER_DETAIL_ORDER_FK", OrderDetail.ORDER_DETAIL, new OrderField[] { OrderDetail.ORDER_DETAIL.ID_ORDER }, false);
        public static Index STOCK_PRIMARY = Internal.createIndex("PRIMARY", Stock.STOCK, new OrderField[] { Stock.STOCK.ID_COMPONENT }, true);
        public static Index USER_PRIMARY = Internal.createIndex("PRIMARY", User.USER, new OrderField[] { User.USER.ID }, true);
        public static Index USER_USER_UN = Internal.createIndex("USER_UN", User.USER, new OrderField[] { User.USER.NAME }, true);
    }
}
