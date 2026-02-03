import java.util.ArrayList;
import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

// ---------------------------------------------------------------
// Interface - any class that needs to save/load data must use this
// ---------------------------------------------------------------
interface Saveable {
    void saveToFile();
    void loadFromFile();
}

// ---------------------------------------------------------------
// Represents one item on the menu (e.g. Pasta, $12.50)
// ---------------------------------------------------------------
class MenuItem {
    private String name;
    private String category;
    private double price;

    MenuItem(String name, String category, double price) {
        this.name = name;
        this.category = category;
        this.price = price;
    }

    String getName()     { return name; }
    String getCategory() { return category; }
    double getPrice()    { return price; }

    public String toString() {
        return String.format("%-22s %-12s $%.2f", name, category, price);
    }
}

// ---------------------------------------------------------------
// Holds the full list of menu items, saves/loads to menu_data.txt
// ---------------------------------------------------------------
class Menu implements Saveable {
    private ArrayList<MenuItem> items = new ArrayList<>();
    private static final String FILE = "menu_data.txt";

    Menu() {
        loadFromFile();
    }

    void addItem(MenuItem item) {
        items.add(item);
        saveToFile();
    }

    void removeItem(int index) {
        if (index >= 0 && index < items.size()) {
            items.remove(index);
            saveToFile();
            System.out.println("Item removed.");
        } else {
            System.out.println("Invalid number.");
        }
    }

    void display() {
        if (items.isEmpty()) {
            System.out.println("  (Menu is empty)");
            return;
        }
        System.out.println("  #   Name                   Category     Price");
        System.out.println("  ---------------------------------------------------");
        for (int i = 0; i < items.size(); i++) {
            System.out.println("  " + (i + 1) + ".  " + items.get(i));
        }
    }

    MenuItem getItem(int index) {
        if (index >= 0 && index < items.size()) return items.get(index);
        return null;
    }

    int size() { return items.size(); }

    // saves each item as:  Name|Category|Price
    public void saveToFile() {
        try {
            PrintWriter pw = new PrintWriter(FILE);
            for (MenuItem item : items) {
                pw.println(item.getName() + "|" + item.getCategory() + "|" + item.getPrice());
            }
            pw.close();
        } catch (FileNotFoundException e) {
            System.out.println("Could not save menu.");
        }
    }

    // reads menu_data.txt line by line
    public void loadFromFile() {
        items.clear();
        try {
            Scanner sc = new Scanner(new File(FILE));
            while (sc.hasNextLine()) {
                String line = sc.nextLine().trim();
                if (line.isEmpty()) continue;
                String[] p = line.split("\\|");
                if (p.length == 3) {
                    items.add(new MenuItem(p[0], p[1], Double.parseDouble(p[2])));
                }
            }
            sc.close();
        } catch (FileNotFoundException e) {
            // file doesn't exist yet, that is fine
        }
    }
}

// ---------------------------------------------------------------
// One line inside an order  (e.g. 2x Pasta)
// ---------------------------------------------------------------
class OrderItem {
    private MenuItem menuItem;
    private int quantity;

    OrderItem(MenuItem menuItem, int quantity) {
        this.menuItem = menuItem;
        this.quantity = quantity;
    }

    MenuItem getMenuItem() { return menuItem; }
    int      getQuantity() { return quantity; }

    double getSubtotal() {
        return menuItem.getPrice() * quantity;
    }

    public String toString() {
        return String.format("  %dx %-22s @ $%.2f = $%.2f",
                quantity, menuItem.getName(), menuItem.getPrice(), getSubtotal());
    }
}

// ---------------------------------------------------------------
// One complete customer order with a unique ID
// ---------------------------------------------------------------
class Order {
    private static int nextId = 1;   // auto-increment counter

    private int id;
    private ArrayList<OrderItem> items = new ArrayList<>();
    private boolean paid = false;

    // used when creating a new order
    Order() {
        this.id = nextId++;
    }

    // used when loading an old order from file
    Order(int id) {
        this.id = id;
        if (id >= nextId) nextId = id + 1;   // keep counter ahead
    }

    int getID()                        { return id; }
    ArrayList<OrderItem> getItems()    { return items; }
    boolean isPaid()                   { return paid; }

    void addItem(OrderItem oi)  { items.add(oi); }
    void markPaid()             { paid = true; }

    double getTotal() {
        double t = 0;
        for (OrderItem oi : items) t += oi.getSubtotal();
        return t;
    }

    void display() {
        System.out.println("  --- Order #" + id + " ---");
        if (items.isEmpty()) {
            System.out.println("    (empty)");
        } else {
            for (OrderItem oi : items) System.out.println("  " + oi);
        }
        System.out.printf("    Total : $%.2f   [%s]%n", getTotal(), paid ? "PAID" : "UNPAID");
    }
}

// ---------------------------------------------------------------
// Manages all orders; saves/loads to sales_data.txt
// ---------------------------------------------------------------
class Sales implements Saveable {
    private ArrayList<Order> orders = new ArrayList<>();
    private Order current = null;          // the order being built right now
    private Menu menu;
    private static final String FILE = "sales_data.txt";

    Sales(Menu menu) {
        this.menu = menu;
        loadFromFile();
    }

    // start a new order
    void newOrder() {
        if (current != null && !current.isPaid()) {
            System.out.println("Finish or pay the current order first (Order #" + current.getID() + ").");
            return;
        }
        current = new Order();
        orders.add(current);
        System.out.println("New Order #" + current.getID() + " started.");
    }

    // add a menu item to the current order
    void addToOrder(int menuIndex, int qty) {
        if (current == null) {
            System.out.println("No active order. Start one first.");
            return;
        }
        MenuItem mi = menu.getItem(menuIndex);
        if (mi == null) {
            System.out.println("Invalid item number.");
            return;
        }
        current.addItem(new OrderItem(mi, qty));
        System.out.println("Added " + qty + "x " + mi.getName());
        saveToFile();
    }

    void viewCurrent() {
        if (current == null) System.out.println("No active order.");
        else current.display();
    }

    // mark current order as paid
    void pay() {
        if (current == null || current.isPaid()) {
            System.out.println("Nothing to pay.");
            return;
        }
        if (current.getItems().isEmpty()) {
            System.out.println("Order is empty, add items first.");
            return;
        }
        current.markPaid();
        System.out.printf("Payment of $%.2f received for Order #%d.%n", current.getTotal(), current.getID());
        current = null;
        saveToFile();
    }

    void viewAll() {
        if (orders.isEmpty()) {
            System.out.println("  No orders yet.");
            return;
        }
        for (Order o : orders) o.display();
    }

    void summary() {
        double revenue = 0;
        int paid = 0, unpaid = 0;
        for (Order o : orders) {
            if (o.isPaid()) { revenue += o.getTotal(); paid++; }
            else unpaid++;
        }
        System.out.println("  Total orders  : " + orders.size());
        System.out.println("  Paid          : " + paid);
        System.out.println("  Unpaid        : " + unpaid);
        System.out.printf( "  Revenue       : $%.2f%n", revenue);
    }

    // file format:
    //   ORDER|id|paid
    //   ITEM|name|category|price|qty
    //   END_ORDER
    public void saveToFile() {
        try {
            PrintWriter pw = new PrintWriter(FILE);
            for (Order o : orders) {
                pw.println("ORDER|" + o.getID() + "|" + o.isPaid());
                for (OrderItem oi : o.getItems()) {
                    pw.println("ITEM|" + oi.getMenuItem().getName() + "|"
                            + oi.getMenuItem().getCategory() + "|"
                            + oi.getMenuItem().getPrice() + "|"
                            + oi.getQuantity());
                }
                pw.println("END_ORDER");
            }
            pw.close();
        } catch (FileNotFoundException e) {
            System.out.println("Could not save sales data.");
        }
    }

    public void loadFromFile() {
        orders.clear();
        try {
            Scanner sc = new Scanner(new File(FILE));
            Order temp = null;
            while (sc.hasNextLine()) {
                String line = sc.nextLine().trim();
                if (line.isEmpty()) continue;

                if (line.startsWith("ORDER|")) {
                    String[] p = line.split("\\|");
                    temp = new Order(Integer.parseInt(p[1]));
                    if (Boolean.parseBoolean(p[2])) temp.markPaid();

                } else if (line.startsWith("ITEM|") && temp != null) {
                    String[] p = line.split("\\|");
                    MenuItem mi = new MenuItem(p[1], p[2], Double.parseDouble(p[3]));
                    temp.addItem(new OrderItem(mi, Integer.parseInt(p[4])));

                } else if (line.equals("END_ORDER") && temp != null) {
                    orders.add(temp);
                    temp = null;
                }
            }
            sc.close();
            // if the last order is unpaid, set it as current
            if (!orders.isEmpty()) {
                Order last = orders.get(orders.size() - 1);
                if (!last.isPaid()) current = last;
            }
        } catch (FileNotFoundException e) {
            // no file yet
        }
    }
}

// ---------------------------------------------------------------
// MAIN CLASS  –  all user input is handled here
// ---------------------------------------------------------------
public class RestaurantMain {

    static Scanner sc = new Scanner(System.in);
    static Menu  menu;
    static Sales sales;

    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("   Restaurant Management System");
        System.out.println("========================================\n");

        menu  = new Menu();
        sales = new Sales(menu);

        boolean run = true;
        while (run) {
            System.out.println("\n--- Main Menu ---");
            System.out.println(" 1. Menu Management");
            System.out.println(" 2. Sales / Orders");
            System.out.println(" 3. Exit");
            int ch = readInt("Choice: ");

            if      (ch == 1) menuSection();
            else if (ch == 2) salesSection();
            else if (ch == 3) { System.out.println("Goodbye."); run = false; }
            else System.out.println("Invalid choice.");
        }
        sc.close();
    }

    // ---------- Menu Management sub-menu ----------
    static void menuSection() {
        boolean open = true;
        while (open) {
            System.out.println("\n--- Menu Management ---");
            System.out.println(" 1. View Menu");
            System.out.println(" 2. Add Item");
            System.out.println(" 3. Remove Item");
            System.out.println(" 4. Back");
            int ch = readInt("Choice: ");

            if (ch == 1) {
                menu.display();

            } else if (ch == 2) {
                String name     = readString("  Item name : ");
                String category = readString("  Category  : ");
                double price    = readDouble("  Price ($) : ");
                menu.addItem(new MenuItem(name, category, price));
                System.out.println("Item added.");

            } else if (ch == 3) {
                menu.display();
                if (menu.size() == 0) continue;
                int n = readInt("  Remove item # : ");
                menu.removeItem(n - 1);

            } else if (ch == 4) {
                open = false;
            }
        }
    }

    // ---------- Sales / Orders sub-menu ----------
    static void salesSection() {
        boolean open = true;
        while (open) {
            System.out.println("\n--- Sales / Orders ---");
            System.out.println(" 1. New Order");
            System.out.println(" 2. Add Item to Order");
            System.out.println(" 3. View Current Order");
            System.out.println(" 4. Pay");
            System.out.println(" 5. View All Orders");
            System.out.println(" 6. Sales Summary");
            System.out.println(" 7. Back");
            int ch = readInt("Choice: ");

            if (ch == 1) {
                sales.newOrder();

            } else if (ch == 2) {
                menu.display();
                if (menu.size() == 0) { System.out.println("Menu is empty."); continue; }
                int item = readInt("  Pick item # : ");
                int qty  = readInt("  Quantity    : ");
                sales.addToOrder(item - 1, qty);

            } else if (ch == 3) {
                sales.viewCurrent();

            } else if (ch == 4) {
                sales.pay();

            } else if (ch == 5) {
                sales.viewAll();

            } else if (ch == 6) {
                sales.summary();

            } else if (ch == 7) {
                open = false;
            }
        }
    }

    // ---- safe input helpers ----
    static int readInt(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                return Integer.parseInt(sc.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Enter a valid number.");
            }
        }
    }

    static double readDouble(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                return Double.parseDouble(sc.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Enter a valid number.");
            }
        }
    }

    static String readString(String prompt) {
        while (true) {
            System.out.print(prompt);
            String s = sc.nextLine().trim();
            if (!s.isEmpty()) return s;
            System.out.println("Cannot be empty.");
        }
    }
}