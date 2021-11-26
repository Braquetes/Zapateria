
package zapateria;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import static zapateria.principal.conexion;


public class Zapateria {
    static JTextField user;
    static JPasswordField contraseña;

    public static void main(String[] args) {
        principal p = new principal();
        p.IniciarSesion.pack();
        p.IniciarSesion.setLocationRelativeTo(null);
        p.IniciarSesion.setResizable(false);
        p.IniciarSesion.setVisible(true);
        p.setEnabled(false);
    }
    }