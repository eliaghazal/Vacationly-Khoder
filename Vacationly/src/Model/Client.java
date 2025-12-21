package Model;

public class Client extends User_Base {
    private double balance;
    private String creditCard;

    public Client(String id, String username, String password, String fullName, double initialBalance, String creditCard) {
        super(id, username, password, fullName, "CLIENT");
        this.balance = initialBalance;
        this.creditCard = creditCard;
    }

    public double getBalance() { return balance; }
    public void setBalance(double balance) { this.balance = balance; }
    
    public void deductBalance(double amount) throws Exception {
        if (this.balance < amount) {
            throw new Exception("Insufficient Funds");
        }
        this.balance -= amount;
    }
}
