# 🍽️ Restaurant Management System v2.0

> A comprehensive command-line application for managing restaurant menus and sales operations with advanced features like order discounts, payment tracking, and detailed analytics.

![Version](https://img.shields.io/badge/version-2.0-blue.svg)
![Language](https://img.shields.io/badge/language-Java-orange.svg)
![License](https://img.shields.io/badge/license-MIT-green.svg)

---

## ✨ Key Features

### 📋 Menu Management
- ✅ **Add/Remove Items** - Easily manage your restaurant's menu items
- 🔍 **Smart Search** - Search items by name or category
- 📊 **Popularity Metrics** - Track how many times each item has been ordered
- 📈 **Detailed View** - View menu with popularity statistics

### 🛒 Order Management
- ✨ **Create Orders** - Start new customer orders with unique IDs
- ➕ **Add/Remove Items** - Dynamically manage items in orders
- 💰 **Discount Support** - Apply percentage-based discounts to individual items
- ❌ **Order Cancellation** - Cancel unpaid orders easily
- 💳 **Payment Processing** - Track and process customer payments
- ⏰ **Timestamps** - Automatic recording of order creation and payment times

### 📊 Sales Analytics
- 💵 **Revenue Tracking** - Monitor total revenue from paid orders
- 📈 **Sales Summary** - View comprehensive order statistics (paid, unpaid, cancelled)
- 🏆 **Category Analysis** - Revenue breakdown by menu category
- 📦 **Item Statistics** - Track total items sold and popular items
- 💸 **Discount Tracking** - Monitor total discounts given to customers

### 💾 Data Persistence
- 🔄 **Auto-Save** - All changes are automatically saved to files
- 📁 **Persistent Storage** - Menu and sales data survive between sessions
- 📝 **Structured Format** - Data stored in easy-to-understand pipe-separated format

---

## 🚀 Getting Started

### Prerequisites
- **Java 8 or higher** (JDK/JRE installed)
- **Windows, macOS, or Linux** operating system
- **Command-line terminal** or PowerShell

### Installation

1. **Clone or Download the Project**
   ```bash
   cd C:\Users\YourUsername\OneDrive\Documents\Java\RMS_Mini_Project
   ```

2. **Compile the Java File**
   ```bash
   javac RestaurantMain.java
   ```

3. **Run the Application**
   ```bash
   java RestaurantMain
   ```

---

## 📖 Usage Guide

### Main Menu Options

When you start the application, you'll see the main menu:

```
========================================
   Restaurant Management System
========================================

--- Main Menu ---
 1. Menu Management
 2. Sales / Orders
 3. Exit
```

---

### 1️⃣ Menu Management

Manage your restaurant's menu items:

```
--- Menu Management ---
 1. View Menu                    (Standard view)
 2. View Menu (with popularity)  (See how popular each item is)
 3. Add Item                     (Add new menu items)
 4. Remove Item                  (Remove items from menu)
 5. Search by Category           (Find items by category)
 6. Search by Name               (Search for specific items)
 7. Back                         (Return to main menu)
```

**Examples:**
- Add a new pasta dish: Name: "Spaghetti", Category: "Main Course", Price: 12.50
- Search for all appetizers: Search category "Appetizer"
- View menu with popularity to see customer preferences

---

### 2️⃣ Sales / Orders

Manage customer orders and process sales:

```
--- Sales / Orders ---
 1. New Order                    (Start a new customer order)
 2. Add Item to Order            (Add menu items to current order)
 3. Remove Item from Order       (Remove items if needed)
 4. Apply Discount to Item       (Give discount on specific items)
 5. View Current Order           (See the order being built)
 6. Pay Order                    (Process payment)
 7. Cancel Current Order         (Cancel unpaid orders)
 8. View All Orders              (See complete order history)
 9. View Unpaid Orders           (Track outstanding payments)
10. Sales Summary                (View order statistics)
11. Revenue by Category          (See which categories earn most)
12. Back                         (Return to main menu)
```

**Workflow Example:**
```
1. New Order
   → New Order #1 started
   
2. Add Item to Order
   → Select item #2 (Pasta)
   → Quantity: 2
   → Added 2x Pasta
   
3. Add Item to Order
   → Select item #3 (Salad)
   → Quantity: 1
   → Added 1x Salad
   
4. Apply Discount to Item
   → Item # to discount: 1
   → Discount %: 10
   → Applied 10% discount to item
   
5. View Current Order
   → Shows 2x Pasta @ $12.50 = $22.50 (10% off)
   → Shows 1x Salad @ $8.00 = $8.00
   → Total: $28.50 [UNPAID]
   
6. Pay Order
   → Payment of $28.50 received for Order #1
```

---

## 📁 File Structure

```
RMS_Mini_Project/
│
├── RestaurantMain.java          # Main application file
├── README.md                     # This file
│
├── menu_data.txt                 # Menu items (auto-created)
│   └── Format: Name|Category|Price|TimesOrdered
│
└── sales_data.txt                # Order history (auto-created)
    └── Format: ORDER|id|paid|cancelled
               ITEM|name|category|price|qty|discount
               END_ORDER
```

---

## 💾 Data Persistence

### Menu Data (`menu_data.txt`)
```
Spaghetti|Main Course|12.50|15
Caesar Salad|Appetizer|8.00|22
Tiramisu|Dessert|6.50|8
```
- **Format:** `Name|Category|Price|TimesOrdered`
- **Auto-saved** when items are added or removed

### Sales Data (`sales_data.txt`)
```
ORDER|1|true|false
ITEM|Spaghetti|Main Course|12.50|2|10
ITEM|Caesar Salad|Appetizer|8.00|1|0
END_ORDER
ORDER|2|false|false
ITEM|Tiramisu|Dessert|6.50|2|0
END_ORDER
```
- **Auto-saved** after each order modification or payment
- **Persists** order history across sessions
- Allows recovery of unpaid orders

---

## 🏗️ Program Architecture

### Class Hierarchy

```
MenuItem
  └─ Represents individual menu items
  └─ Tracks: name, category, price, popularity

Menu
  └─ Manages collection of MenuItems
  └─ Features: add, remove, search, display
  └─ Persists to: menu_data.txt

OrderItem
  └─ Represents line items in orders
  └─ Tracks: menu item, quantity, discount

Order
  └─ Represents complete customer order
  └─ Features: add items, calculate totals, cancel
  └─ Tracks: ID, creation time, payment status

Sales
  └─ Manages collection of Orders
  └─ Features: create orders, apply discounts, process payments
  └─ Analytics: summary, revenue by category
  └─ Persists to: sales_data.txt

RestaurantMain
  └─ User interface and input handling
  └─ Routes to Menu Management or Sales sections
  └─ Safe input validation for all user entries
```

---

## 🎮 Interactive Features

### ✅ Data Validation
- **Integer Input:** Validates menu choices and quantities
- **Decimal Input:** Validates prices and discount percentages
- **String Input:** Ensures non-empty item names and categories
- **Error Handling:** Graceful error messages for invalid inputs

### 🔢 Auto-Incrementing Order IDs
- Each order gets a unique sequential ID
- Automatically incremented with each new order
- IDs preserved when loading from file

### 💰 Discount System
- Percentage-based discounts (0-100%)
- Applied to individual order items
- Calculated before final total
- Shows original and discounted amounts

### 📊 Smart Analytics
- Popularity ranking by order count
- Revenue calculations with discounts
- Category-based revenue breakdown
- Distinguishes paid vs unpaid vs cancelled orders

---

## 🔐 Features & Security

### ✨ Data Integrity
- Automatic file persistence prevents data loss
- Order timestamps for audit trail
- Payment status tracking
- Cancellation history

### 🛡️ Input Safety
- All user inputs validated before processing
- Type checking for numeric inputs
- Empty string prevention
- Range validation for discounts

### 📝 State Management
- Current order state tracked
- Prevents payment on already-paid orders
- Prevents cancellation of paid orders
- Prevents new orders while one is unpaid

---

## 📊 Example Workflow

### Complete Restaurant Day Scenario

```
MORNING: Set up menu
├─ Add Item: "Espresso" | "Beverage" | 3.50
├─ Add Item: "Croissant" | "Breakfast" | 2.50
└─ View Menu: Display 2 items

MIDDAY: Handle customer orders
├─ New Order #1
│  ├─ Add: 1x Espresso
│  ├─ Add: 2x Croissant
│  ├─ Apply 10% discount to item 2
│  └─ Pay: $7.75
├─ New Order #2
│  ├─ Add: 2x Espresso
│  └─ Pay: $7.00
└─ View Unpaid Orders: (none)

EVENING: Review daily performance
├─ Sales Summary
│  ├─ Total orders: 2
│  ├─ Completed: 2
│  ├─ Revenue: $14.75
│  └─ Discounts given: $0.75
├─ Revenue by Category
│  ├─ Beverage: $7.00
│  └─ Breakfast: $7.75
└─ Menu with Popularity
   ├─ Espresso: ordered 3 times
   └─ Croissant: ordered 2 times
```

---

## 🐛 Troubleshooting

### Issue: "Could not save menu."
- **Solution:** Ensure write permissions to the project directory
- **Check:** Project folder is not read-only

### Issue: "No active order"
- **Solution:** Start a new order first using "New Order"
- **Note:** Only one order can be active at a time

### Issue: "Cannot cancel a paid order"
- **Solution:** Paid orders are final and cannot be cancelled
- **Note:** Create a new order if customer needs different items

### Issue: Order history not loading
- **Solution:** Check if `sales_data.txt` exists in project directory
- **Note:** First run will create new file automatically

---

## 📈 Advanced Features

### Discount Application Tips
- ✅ Can apply different discounts to different items in same order
- ✅ Useful for promotional offers or loyalty discounts
- ✅ Shows both original and discounted amounts on receipt
- ✅ Tracked in sales summary for reporting

### Popularity Tracking
- 📊 View detailed menu to see item popularity
- 🎯 Identify best-selling items
- 💡 Decide which items to promote or remove

### Revenue Analytics
- 💹 Track revenue by category to identify profit centers
- 📉 Monitor discount impact on margins
- 📊 Analyze paid vs unpaid orders

---

## 🔄 Workflow Tips

### For Best Results:
1. **Start with Menu Setup** - Add all menu items before accepting orders
2. **Review Daily** - Check sales summary and revenue metrics daily
3. **Track Unpaid Orders** - Monitor unpaid orders to ensure payment collection
4. **Analyze Performance** - Use category revenue to optimize menu

### Save & Data:
- ✅ All changes auto-save immediately
- ✅ Data preserved between sessions
- ✅ Historical data never lost
- ✅ Safe to close application anytime

---

## 📋 System Requirements

| Component | Requirement |
|-----------|-------------|
| **Java Version** | 8 or higher |
| **RAM** | 64 MB minimum |
| **Disk Space** | 5 MB for application + data |
| **OS** | Windows, macOS, Linux |
| **Terminal** | Command Prompt, PowerShell, or Bash |

---

## 👨‍💻 Author & Version

| Details | Information |
|---------|-------------|
| **Project** | Restaurant Management System |
| **Version** | 2.0 |
| **Author** | Abhinay Kumar Sahu |
| **Last Updated** | February 3, 2026 |
| **Status** | ✅ Fully Functional |

---

## 📝 License

This project is open-source and available under the MIT License.

---

## 🎯 Future Enhancements

Potential features for future versions:
- 🌐 Web-based interface
- 📱 Mobile application
- 👥 Multi-user accounts
- 🔐 Password protection
- 📊 Graphical reports and charts
- 🗓️ Date-based sales analysis
- 🧾 Receipt printing
- 📧 Email order notifications
- 💳 Multiple payment methods
- ⭐ Customer ratings system

---

## 💬 Support & Feedback

For issues, feature requests, or improvements:
1. Review the **Troubleshooting** section above
2. Check the inline code documentation
3. Ensure Java is properly installed
4. Verify file permissions in project directory

---

## 🎉 Enjoy Your Restaurant Management Experience!

```
╔════════════════════════════════════╗
║  Restaurant Management System v2.0 ║
║        Your Complete Solution      ║
║    for Menu & Sales Management     ║
╚════════════════════════════════════╝
```

---

**Happy Managing!** 🍽️ 💼 📊

