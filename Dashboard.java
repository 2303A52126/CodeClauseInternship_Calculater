import java.io.*;
import java.time.LocalDateTime;
import javax.swing.*;

public class Dashboard extends JFrame {
    String accountNumber, name;
    double balance;

    JLabel balanceLabel;

    public Dashboard(String acc, String name, double bal) {
        this.accountNumber = acc;
        this.name = name;
        this.balance = bal;

        setTitle("Dashboard");
        setSize(400, 300);
        setLayout(null);

        JLabel welcome = new JLabel("Welcome, " + name);
        welcome.setBounds(20, 20, 200, 30);
        add(welcome);

        balanceLabel = new JLabel("Balance: ₹" + balance);
        balanceLabel.setBounds(20, 60, 200, 30);
        add(balanceLabel);

        JButton depositBtn = new JButton("Deposit");
        depositBtn.setBounds(20, 100, 100, 30);
        depositBtn.addActionListener(e -> handleDeposit());
        add(depositBtn);

        JButton withdrawBtn = new JButton("Withdraw");
        withdrawBtn.setBounds(140, 100, 100, 30);
        withdrawBtn.addActionListener(e -> handleWithdraw());
        add(withdrawBtn);

        JButton transferBtn = new JButton("Transfer");
        transferBtn.setBounds(260, 100, 100, 30);
        transferBtn.addActionListener(e -> handleTransfer());
        add(transferBtn);

        JButton historyBtn = new JButton("View History");
        historyBtn.setBounds(20, 150, 150, 30);
        historyBtn.addActionListener(e -> showTransactionHistory());
        add(historyBtn);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    void updateBalance(double newBalance) {
        balance = newBalance;
        balanceLabel.setText("Balance: ₹" + balance);

        // update in users.txt
        try {
            File inputFile = new File("users.txt");
            File tempFile = new File("users_temp.txt");

            BufferedReader reader = new BufferedReader(new FileReader(inputFile));
            BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));

            String line;
            while ((line = reader.readLine()) != null) {
                String[] user = line.split(",");
                if (user[0].equals(accountNumber)) {
                    writer.write(accountNumber + "," + name + "," + user[2] + "," + balance);
                } else {
                    writer.write(line);
                }
                writer.newLine();
            }
            reader.close();
            writer.close();

            inputFile.delete();
            tempFile.renameTo(inputFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void recordTransaction(String type, double amount) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("transactions.txt", true))) {
            writer.write(accountNumber + "," + type + "," + amount + "," + LocalDateTime.now());
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void handleDeposit() {
        String amtStr = JOptionPane.showInputDialog(this, "Enter amount to deposit:");
        double amt = Double.parseDouble(amtStr);
        balance += amt;
        updateBalance(balance);
        recordTransaction("deposit", amt);
    }

    void handleWithdraw() {
        String amtStr = JOptionPane.showInputDialog(this, "Enter amount to withdraw:");
        double amt = Double.parseDouble(amtStr);
        if (amt > balance) {
            JOptionPane.showMessageDialog(this, "Insufficient funds!");
            return;
        }
        balance -= amt;
        updateBalance(balance);
        recordTransaction("withdraw", amt);
    }

    void handleTransfer() {
        String targetAcc = JOptionPane.showInputDialog(this, "Enter target account number:");
        String amtStr = JOptionPane.showInputDialog(this, "Enter amount to transfer:");
        double amt = Double.parseDouble(amtStr);

        if (amt > balance) {
            JOptionPane.showMessageDialog(this, "Insufficient balance.");
            return;
        }

        boolean found = false;
        try {
            File inputFile = new File("users.txt");
            File tempFile = new File("users_temp.txt");

            BufferedReader reader = new BufferedReader(new FileReader(inputFile));
            BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));

            String line;
            while ((line = reader.readLine()) != null) {
                String[] user = line.split(",");
                if (user[0].equals(accountNumber)) {
                    writer.write(accountNumber + "," + name + "," + user[2] + "," + (balance - amt));
                } else if (user[0].equals(targetAcc)) {
                    double newBal = Double.parseDouble(user[3]) + amt;
                    writer.write(user[0] + "," + user[1] + "," + user[2] + "," + newBal);
                    found = true;
                } else {
                    writer.write(line);
                }
                writer.newLine();
            }

            reader.close();
            writer.close();

            inputFile.delete();
            tempFile.renameTo(inputFile);

            if (found) {
                balance -= amt;
                updateBalance(balance);
                recordTransaction("transfer-out", amt);

                // log for receiver
                try (BufferedWriter writer2 = new BufferedWriter(new FileWriter("transactions.txt", true))) {
                    writer2.write(targetAcc + ",transfer-in," + amt + "," + LocalDateTime.now());
                    writer2.newLine();
                }

                JOptionPane.showMessageDialog(this, "Transfer successful!");
            } else {
                JOptionPane.showMessageDialog(this, "Target account not found.");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void showTransactionHistory() {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader("transactions.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] txn = line.split(",");
                if (txn[0].equals(accountNumber)) {
                    sb.append(txn[1]).append(" ₹").append(txn[2]).append(" on ").append(txn[3]).append("\n");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        JOptionPane.showMessageDialog(this, sb.toString().isEmpty() ? "No transactions found." : sb.toString());
    }
}
