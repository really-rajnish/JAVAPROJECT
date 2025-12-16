# 🛒 E-Commerce Order Processing System

A robust, Java-based simulation of a real-world e-commerce order lifecycle. [cite_start]This project focuses on clean object-oriented design, modular architecture, and accurate financial calculations for tax and discounts[cite: 11, 12].

---

## 👥 Team Members
**Presented by:**
* [cite_start]**Mehul Ghloth** (ID: 2500032607) [cite: 4, 5]
* [cite_start]**Rajnish Ranjan** (ID: 2500032608) [cite: 6, 7]
* **M. [cite_start]Anil** (ID: 2500080085) [cite: 8, 9]

---

## 🚀 Objectives
The main goal of this project is to simulate an e-commerce backend with a focus on:
* [cite_start]**Real-world lifecycle simulation:** Managing the flow from product selection to payment[cite: 11].
* [cite_start]**Financial Accuracy:** Ensuring GST and discount policies are applied correctly to reduce billing errors[cite: 12, 24].
* [cite_start]**Data Management:** Reading product and coupon data from external CSV sources[cite: 14].

## ✨ Key Features
* [cite_start]**Shopping Cart Management:** Users can add products and manage quantities dynamically[cite: 13, 20].
* [cite_start]**Automated Pricing Engine:** Handles tax (GST) and discount calculations automatically during checkout[cite: 15, 22].
* [cite_start]**Invoice Generation:** Generates a physical invoice file for every completed order[cite: 16].
* [cite_start]**Inventory Control:** Prevents ordering more items than available in stock via custom exception handling[cite: 74].

---

## 🏗️ System Architecture & Modules
[cite_start]The system is divided into 6 distinct modules to ensure modularity[cite: 41]:

1.  [cite_start]**Product & Pricing Module:** Manages product data (ID, name, price, category) using abstract `Item` and concrete `Product` classes[cite: 42, 43].
2.  [cite_start]**Cart & Order Module:** Models line items using `OrderItem` to store products and quantities[cite: 44, 45].
3.  [cite_start]**Promotions Module:** Handles cost calculations and discounts using `CostComponent` and `BaseOrderCost`[cite: 46, 47].
4.  [cite_start]**Payment Module:** Abstraction of payment methods via the `PaymentProcessor` interface[cite: 48, 49].
5.  [cite_start]**Service & Inventory Module:** Maintains the in-memory inventory `Map` and cart `List`[cite: 50, 51].
6.  [cite_start]**Exception Handling Module:** robust error handling for invalid coupons and stock limits[cite: 52, 53].

---

## 🛠️ Technology Stack & Concepts
This project demonstrates advanced Java concepts:

### Object-Oriented Programming (OOP)
* **Inheritance:** `Product` extends `Item`; [cite_start]Decorators extend `PromotionDecorator`[cite: 61, 62].
* [cite_start]**Polymorphism:** Interfaces like `Taxable`, `DiscountPolicy`, and `PaymentProcessor` enable runtime behavior changes[cite: 62, 63].
* [cite_start]**Encapsulation:** Data hiding using private fields and public getters[cite: 64, 65].
* [cite_start]**Abstraction:** Hiding implementation details using Abstract classes and Interfaces[cite: 66, 67].

### Functional Programming
* [cite_start]**Java Stream API:** Used for sorting products by price (`Comparator.comparingDouble`) and summing subtotals (`mapToDouble().sum()`)[cite: 69, 70, 71].

### Exception Handling
* **Custom Exceptions:**
    * [cite_start]`InvalidCouponException`: Thrown for expired/invalid codes[cite: 73].
    * [cite_start]`OutOfStockException`: Thrown when request exceeds inventory[cite: 74].

---

## 📐 Mathematical Logic
[cite_start]The system ensures financial compliance using the following formulas[cite: 54]:
* [cite_start]**GST Calculation:** `Tax = Subtotal * GST Rate` [cite: 55]
* [cite_start]**Discount Calculation:** `Discount = Subtotal * Discount %` [cite: 56]
* [cite_start]**Net Payable:** `Amount = Subtotal - Discount + Tax` [cite: 57]

---

## 💻 Application Flow (CLI)
[cite_start]The Command Line Interface follows this structured flow[cite: 31]:
1.  **Start Program**
2.  [cite_start]**View Products** [cite: 33]
3.  [cite_start]**Add to Cart** [cite: 34]
4.  [cite_start]**View Cart** [cite: 37]
5.  [cite_start]**Checkout** [cite: 36]
6.  [cite_start]**Payment Processing** [cite: 40]
7.  [cite_start]**Invoice Generation** [cite: 39]

---

## 🔧 How to Run
1.  Clone the repository.
2.  Compile the source code:
    ```bash
    javac Ecommerce/EcommerceSystem.java
    ```
3.  Run the main system:
    ```bash
    java Ecommerce.EcommerceSystem
    ```

---
*University Project - 2025*
