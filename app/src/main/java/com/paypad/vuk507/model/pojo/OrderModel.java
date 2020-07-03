package com.paypad.vuk507.model.pojo;

import com.paypad.vuk507.model.Sale;
import com.paypad.vuk507.model.SaleItem;
import com.paypad.vuk507.model.Transaction;
import com.paypad.vuk507.model.order.Order;
import com.paypad.vuk507.model.order.OrderDiscount;
import com.paypad.vuk507.model.order.OrderItem;
import com.paypad.vuk507.model.order.OrderItemDiscount;
import com.paypad.vuk507.model.order.OrderItemTax;
import com.paypad.vuk507.model.order.OrderTransaction;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import io.realm.RealmList;

public class OrderModel implements Serializable {

    private Order order;
    private List<OrderItem> orderItems;
    private List<OrderDiscount> orderDiscounts;
    private List<OrderItemDiscount> orderItemDiscounts;
    private List<OrderItemTax> orderItemTaxes;
    private List<OrderTransaction> orderTransactions;

    public OrderModel() {
        setOrder(new Order());
        order.setOrderId(UUID.randomUUID().toString());

        setOrderItems(new ArrayList<>());
        setOrderDiscounts(new ArrayList<>());
        setOrderItemDiscounts(new ArrayList<>());
        setOrderItemTaxes(new ArrayList<>());
        setOrderTransactions(new ArrayList<>());
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public List<OrderItem> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(List<OrderItem> orderItems) {
        this.orderItems = orderItems;
    }

    public List<OrderDiscount> getOrderDiscounts() {
        return orderDiscounts;
    }

    public void setOrderDiscounts(List<OrderDiscount> orderDiscounts) {
        this.orderDiscounts = orderDiscounts;
    }

    public List<OrderItemDiscount> getOrderItemDiscounts() {
        return orderItemDiscounts;
    }

    public void setOrderItemDiscounts(List<OrderItemDiscount> orderItemDiscounts) {
        this.orderItemDiscounts = orderItemDiscounts;
    }

    public List<OrderItemTax> getOrderItemTaxes() {
        return orderItemTaxes;
    }

    public void setOrderItemTaxes(List<OrderItemTax> orderItemTaxes) {
        this.orderItemTaxes = orderItemTaxes;
    }

    public List<OrderTransaction> getOrderTransactions() {
        return orderTransactions;
    }

    public void setOrderTransactions(List<OrderTransaction> orderTransactions) {
        this.orderTransactions = orderTransactions;
    }
}
