import Controller.App_Controller;
import View.Login_View;
import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            App_Controller controller = new App_Controller();
            new Login_View(controller).setVisible(true);
        });
    }
}