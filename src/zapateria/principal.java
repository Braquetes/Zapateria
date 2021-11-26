

package zapateria;
import com.mxrck.autocompleter.TextAutoCompleter;
import com.sun.glass.events.KeyEvent;
import java.awt.Color;
import java.awt.Desktop;
import java.io.IOException;
import static java.lang.Thread.sleep;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.view.JasperViewer;
import sun.util.resources.cldr.CalendarData;
        


public class principal extends javax.swing.JFrame {
private ImageIcon img;
static Boolean estado;
static Connection conexion = null;
static float total = 0;
String operacion;
String ProgramaUser;
String ProgramaContraseña;
        
    public principal() {
        initComponents();
        reloj r = new reloj(reloj, reloj2);
        r.start();
        
        // Pocicionar la pantalla en el centro de la pantalla
        setLocationRelativeTo(null);
        // impedir cambio de tamaño de la ventana
        setResizable(false);
        // Cambiar el titulo de la ventana
        setTitle("Zapateria");
        // generar una imagen 
        img = new ImageIcon(this.getClass().getResource("/Iconos/trainers_256.png"));
//        // Colocor imagen para el programa 
        setIconImage(img.getImage());
        img = new ImageIcon(this.getClass().getResource("/Iconos/info.png"));
        this.Acercade.setIconImage(img.getImage());
        img = new ImageIcon(this.getClass().getResource("/Iconos/process.png"));
        this.frame_configuracion.setIconImage(img.getImage());
        img = new ImageIcon(this.getClass().getResource("/Iconos/download.png"));
        this.frame_inventario.setIconImage(img.getImage());
        img = new ImageIcon(this.getClass().getResource("/Iconos/icon-33.png"));
        this.frame_ventas.setIconImage(img.getImage());        
// ventana visible
        setVisible(true);
        
        selecionTabla();
        InavilitarPanelOpcionesInventario();
       
    }

     public void estadoConexion (Boolean est,String user, String cont, String db){
        if(est == true){
            this.estadoConeccion.setText("Conectado");
            this.estadoConeccion.setBackground(new Color(134, 255, 63));
            this.Cestado.setText("Conectado");
            this.Cestado.setForeground(new Color(0, 179, 0));
            this.Cuser.setText(user);
            this.Cpasword.setText(cont);
            this.dbConectada.setText(db);
        } 
        if(est == false){
            this.estadoConeccion.setText("No conectado");
            this.estadoConeccion.setBackground(new Color(255, 8, 23));
            this.Cestado.setText("No Conectado");
            this.Cestado.setForeground(new Color(0, 8, 23));
            this.Cuser.setText(user);
            this.Cpasword.setText(cont);
            this.dbConectada.setText(db);
        }
   } 
    public void selecionTabla(){
        // agregar metodeo de escucha a la tabla 
       jtb_inventario_tablazapatos.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

           @Override
           public void valueChanged(ListSelectionEvent e) {
               if (jtb_inventario_tablazapatos.getSelectedRow()!= -1) {
                   int fila  = jtb_inventario_tablazapatos.getSelectedRow();
                   // colocar los datos de la fila selecionada en las etiquetas correspondientes
                   lbl_inventario_etiqueta_8.setText(jtb_inventario_tablazapatos.getValueAt(fila, 0).toString());
                   lbl_inventario_etiqueta_9.setText(jtb_inventario_tablazapatos.getValueAt(fila, 1).toString());
                   lbl_inventario_etiqueta_10.setText(jtb_inventario_tablazapatos.getValueAt(fila, 2).toString());
                   lbl_inventario_etiqueta_11.setText(jtb_inventario_tablazapatos.getValueAt(fila, 3).toString());
                   lbl_inventario_etiqueta_12.setText(jtb_inventario_tablazapatos.getValueAt(fila, 4).toString());
                   lbl_inventario_etiqueta_13.setText(jtb_inventario_tablazapatos.getValueAt(fila, 5).toString());
                   lbl_inventario_etiqueta_14.setText(jtb_inventario_tablazapatos.getValueAt(fila, 6).toString());
               }
           }
       });
}
    public boolean GetConnection(String usuarioDB, String contraseña ) {
             
             String db = "zapateria";
         try {
             Class.forName("com.mysql.cj.jdbc.Driver");
             String servidor = "jdbc:mysql://127.0.0.1/"+db;
             
             conexion = DriverManager.getConnection(servidor, usuarioDB, contraseña);
             estado = true;
             estadoConexion (estado,usuarioDB, contraseña, db);
             System.out.println("Conexión establecida con " + db); 
         } catch (ClassNotFoundException ex) {
             JOptionPane.showMessageDialog(null, ex.getMessage(), "Error 1 en la Conexión con la BD " + ex.getMessage(), JOptionPane.ERROR_MESSAGE);
             conexion = null;
             estado = false;
             estadoConexion (estado,usuarioDB, contraseña, db);
         } catch (SQLException ex) {
             JOptionPane.showMessageDialog(null, ex.getMessage(), "Error 2 en la Conexión con la BD " + ex.getMessage(), JOptionPane.ERROR_MESSAGE);
             conexion = null;
             estado = false;
         estadoConexion (estado,usuarioDB, contraseña, db);
         } finally {
            
         }
         return estado;
     }
    public void consultas (){ // metodo para la busqueda de la informacion del inventario

    try{
//        GetConnection();
        Statement st = conexion.createStatement();
        ResultSet rs;
           
        rs = st.executeQuery("SELECT * FROM `inventario`");
        ResultSetMetaData rsm = rs.getMetaData();
        // crear una lista donde almacenare los datos
        ArrayList<Object[]> data=new ArrayList<>();
        // calcular el numero de filas y gebnerar la informacion para cada una
        while (rs.next()) {            
             Object[] rows=new Object[rsm.getColumnCount()];
             for (int i = 0; i < rows.length; i++) {
                // generar los datos de cada fila 
                 rows[i]=rs.getObject(i+1);
                 }
             // Guardar los datos consultados en una lista 
             data.add(rows);
         }
        // general el modelo de la tabla
        DefaultTableModel dtm =(DefaultTableModel)this.jtb_inventario_tablazapatos.getModel();
        // eliminar los datos que exinten en el modelo de la tabla antes de agregar los datos 
        if (dtm.getRowCount() != 0){
            int d = dtm.getRowCount();
        for (int y = 0; y < d; y++){
            dtm.removeRow(0);
           }
        }
        this.jtb_inventario_tablazapatos.setModel(dtm);
        // agregar los datos de la consulta a la tabla 
        for (int i = 0; i <data.size(); i++) {
             dtm.addRow(data.get(i));
            }   
     }catch(SQLException ex){
        JOptionPane.showMessageDialog(null,ex.getMessage(), "¡ERROR!", JOptionPane.ERROR_MESSAGE);
     } 
   }
    public void AutoCompletar (int op){
        // crear la bariable acutocompletar
         TextAutoCompleter ac = new TextAutoCompleter(jtf_ventas_buscar);
        //  abrab dos casos posibles: 1- por id del zapato 2- por nombre del zapato
         switch(op){
             // Caso por "ID"
             case 0:
                  try{
                            Statement st = conexion.createStatement();
                            ResultSet rs = st.executeQuery("select id from inventario");
                            ac.removeAll();
                            while(rs.next()){
                                ac.addItem(rs.getString("id"));
                            }
                        }catch(SQLException ex){
                            JOptionPane.showMessageDialog(null, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                        }
                 break;
             // Caso por "Nombre"    
             case 1:
                 try{
                            Statement st = conexion.createStatement();
                            ResultSet rs = st.executeQuery("select nombre from inventario");
                            ac.removeAll();
                            while(rs.next()){
                                ac.addItem(rs.getString("nombre"));
                            }
                        }catch(SQLException ex){
                            JOptionPane.showMessageDialog(null, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                        }
                 break;
         }
    }
    public void NumeroFactura(){
        try{
                            Statement st = conexion.createStatement();
                            ResultSet rs = st.executeQuery("SELECT MAX(`idfac`) FROM `registroventasfac`");
                            while(rs.next()){
                                int i = Integer.parseInt(rs.getString("MAX(`idfac`)"));
                                int idfact = i + 1;
                                
                            this.idfac.setText(""+idfact);
                            
                            }
                        }catch(SQLException ex){
                            JOptionPane.showMessageDialog(null, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                        }
        
    }
    public boolean guardadRegistroFactura(){
        boolean confirmacacion = true;
       int id = Integer.parseInt(this.idfac.getText());
        Date cd = this.Calendario.getDate();
        SimpleDateFormat fechas = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat horas = new SimpleDateFormat("HH:mm:ss");
        SimpleDateFormat fhs = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
       String fecha = fechas.format(cd);
       String hora = horas.format(cd);
       String fh = fhs.format(cd);
       String vendedor = this.vendedor.getText();
       int productos = this.jtbl_ventas.getRowCount();
       try{
         Statement st = conexion.createStatement();
         st.execute("INSERT INTO `registroventasfac`(`idfac`, `cantidadpro`, `totalventa`, `vendedor`, `fecha`, `hora`,`fh` ) VALUES ('"+id+"', '"+productos+"', '"+total+"', '"+vendedor+"','"+fecha+"','"+hora+"','"+fh+"');");
         
        
       }catch(SQLException ex){
           JOptionPane.showMessageDialog(null,ex.getMessage(), "¡ERROR!  1 registro ventas ", JOptionPane.ERROR_MESSAGE);
           confirmacacion = false;
       } 
       return confirmacacion;
    }
    public boolean guardaRegistrosProductosFac(){
        boolean confirmacacion = true;
        int id = Integer.parseInt(this.idfac.getText());
        Date cd = this.Calendario.getDate();
       SimpleDateFormat fechas = new SimpleDateFormat("yyyy-MM-dd");
       SimpleDateFormat horas = new SimpleDateFormat("HH:mm:ss");
       String fecha = fechas.format(cd);
       String hora = horas.format(cd);
       String sql ;
         int filas = this.jtbl_ventas.getRowCount();
         Object[][] valores = new Object[100][6];
         for ( int f = 0 ; f < filas; f++){
             for (int c = 0 ; c < 6; c++){

               valores[f][c] = this.jtbl_ventas.getValueAt(f, c).toString();
             }
         }
       
         for ( int h = 0 ; h < filas ; h++){
                try{
           Statement st = conexion.createStatement();
           sql = "INSERT INTO `registroprocfacturas`(`idfac`, `idpro`, `nombre`, `cantidad`, `precio`,"
                   +" `subtotal`, `fecha`, `hora`) VALUES "
                   +"("+id+","
                   +Integer.parseInt(valores[h][0].toString())
                   +",'"+valores[h][1].toString()+"',"
                   +""+Integer.parseInt(valores[h][3].toString())
                   +","+ Float.parseFloat(valores[h][4].toString())
                   +","+Float.parseFloat(valores[h][5].toString())+","
                   +"'"+fecha+"',"
                   +"'"+hora+"')";
            
           st.execute(sql);
         }catch(SQLException ex){
            JOptionPane.showMessageDialog(null, ex.getMessage(), "Error 2", JOptionPane.ERROR_MESSAGE);
            confirmacacion = false;
       } 
            
         }
      
         return confirmacacion;
    }
    public void InavilitarPanelOpcionesInventario(){
        // inavilitar los campos de texto
        this.jtf_inventario_etiqueta_8.setEnabled(false);
        this.jtf_inventario_etiqueta_9.setEnabled(false);
        this.jtf_inventario_etiqueta_10.setEnabled(false);
        this.jtf_inventario_etiqueta_11.setEnabled(false);
        this.jtf_inventario_etiqueta_12.setEnabled(false);
        this.jtf_inventario_etiqueta_13.setEnabled(false);
        this.jtf_inventario_etiqueta_14.setEnabled(false);
        // desactivar el boton de opciones
        this.bnt_opciones_inventario.setEnabled(false);
    }
    public void LimpiarPanelOpcionesInventario(){
        // inavilitar los campos de texto
        this.jtf_inventario_etiqueta_8.setText("");
        this.jtf_inventario_etiqueta_9.setText("");
        this.jtf_inventario_etiqueta_10.setText("");
        this.jtf_inventario_etiqueta_11.setText("");
        this.jtf_inventario_etiqueta_12.setText("");
        this.jtf_inventario_etiqueta_13.setText("");
        this.jtf_inventario_etiqueta_14.setText("");
        // desactivar el boton de opciones
        this.bnt_opciones_inventario.setText("Aplicar Operacion");
    }
    public void OpcionesInventario (String op){
        // trasferir la info de las etiquetas a los campos de texto
        this.jtf_inventario_etiqueta_8.setText(this.lbl_inventario_etiqueta_8.getText());
        this.jtf_inventario_etiqueta_9.setText(this.lbl_inventario_etiqueta_9.getText());
        this.jtf_inventario_etiqueta_10.setText(this.lbl_inventario_etiqueta_10.getText());
        this.jtf_inventario_etiqueta_11.setText(this.lbl_inventario_etiqueta_11.getText());
        this.jtf_inventario_etiqueta_12.setText(this.lbl_inventario_etiqueta_12.getText());
        this.jtf_inventario_etiqueta_13.setText(this.lbl_inventario_etiqueta_13.getText());
        this.jtf_inventario_etiqueta_14.setText(this.lbl_inventario_etiqueta_14.getText());
        // avilitar los campos de texto
        this.jtf_inventario_etiqueta_8.setEnabled(true);
        this.jtf_inventario_etiqueta_9.setEnabled(true);
        this.jtf_inventario_etiqueta_10.setEnabled(true);
        this.jtf_inventario_etiqueta_11.setEnabled(true);
        this.jtf_inventario_etiqueta_12.setEnabled(true);
        this.jtf_inventario_etiqueta_13.setEnabled(true);
        this.jtf_inventario_etiqueta_14.setEnabled(true);
        // cambiar la palabra del boton de opciones
        this.bnt_opciones_inventario.setText(op);
        // activar el boton de opciones
        this.bnt_opciones_inventario.setEnabled(true);
        operacion = op;
        switch(operacion){
            case "Editar":
                // avilitar ediccion  los campos de texto
                    this.jtf_inventario_etiqueta_8.setEditable(false);
                    this.jtf_inventario_etiqueta_9.setEditable(true);
                    this.jtf_inventario_etiqueta_10.setEditable(true);
                    this.jtf_inventario_etiqueta_11.setEditable(true);
                    this.jtf_inventario_etiqueta_12.setEditable(true);
                    this.jtf_inventario_etiqueta_13.setEditable(true);
                    this.jtf_inventario_etiqueta_14.setEditable(true);
                break;
            case "Eliminar":
                  // inavilitar ediccion  los campos de texto
                    this.jtf_inventario_etiqueta_8.setEditable(false);
                    this.jtf_inventario_etiqueta_9.setEditable(false);
                    this.jtf_inventario_etiqueta_10.setEditable(false);
                    this.jtf_inventario_etiqueta_11.setEditable(false);
                    this.jtf_inventario_etiqueta_12.setEditable(false);
                    this.jtf_inventario_etiqueta_13.setEditable(false);
                    this.jtf_inventario_etiqueta_14.setEditable(false);
                break;
        }
    }
    public void OperacionEditarInventario(int id){
        String sql = "UPDATE `inventario` SET `nombre`='"+this.jtf_inventario_etiqueta_9.getText()+"',"
                + "`tipo`='"+this.jtf_inventario_etiqueta_10.getText()+"',"
                + "`cantidad`="+Integer.parseInt(this.jtf_inventario_etiqueta_11.getText())+","
                + "`color`='"+this.jtf_inventario_etiqueta_12.getText()+"',"
                + "`talla`="+Integer.parseInt(this.jtf_inventario_etiqueta_13.getText())+","
                + "`precio`="+Float.parseFloat(this.jtf_inventario_etiqueta_14.getText())+" "
                + "WHERE `id` = "+id+";";
        System.out.println(sql);
        int dec = JOptionPane.showConfirmDialog(null, "Desea Editar el producto seleccionado",
                         "Confirmar Edicion", JOptionPane.OK_CANCEL_OPTION);
        switch(dec){
                            case 0:
                                 try{
                                     Statement st = conexion.createStatement();
                                    st.execute(sql);
                                     JOptionPane.showMessageDialog(null, "El producto a sido modificado");
                                     LimpiarPanelOpcionesInventario();
                                    InavilitarPanelOpcionesInventario();
                                    }catch(SQLException ex){
                                        JOptionPane.showMessageDialog(null, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                                       }   
                                break;
                            case 2:
                                JOptionPane.showMessageDialog(null, "Modificación  Cancelada");
                                 LimpiarPanelOpcionesInventario();
                                 InavilitarPanelOpcionesInventario();
                                break;
                        }
    }
    public void OperacionAgregarInventario(int id){
        String sql = "INSERT INTO `inventario`(`id`, `nombre`, `tipo`, `cantidad`, `color`, `talla`, `precio`)"
                + " VALUES ("
                + ""+id+","
                + "'"+this.jtf_inventario_etiqueta_9.getText()+"',"
                + "'"+this.jtf_inventario_etiqueta_10.getText()+"',"
                + ""+Integer.parseInt(this.jtf_inventario_etiqueta_11.getText())+","
                + "'"+this.jtf_inventario_etiqueta_12.getText()+"',"
                + ""+Integer.parseInt(this.jtf_inventario_etiqueta_13.getText())+","
                + ""+Float.parseFloat(this.jtf_inventario_etiqueta_14.getText())+")";
        
    
                                 try{
                                     Statement st = conexion.createStatement();
                                    st.execute(sql);
                                     JOptionPane.showMessageDialog(null, "El producto a sido agredado a inventario");
                                     LimpiarPanelOpcionesInventario();
                                    InavilitarPanelOpcionesInventario();
                                    }catch(SQLException ex){
                                        JOptionPane.showMessageDialog(null, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                                       }   
                       
    }
    public void DescuentoProductosInventario(int cant){
       
         int filas = this.jtbl_ventas.getRowCount();
         Object[][] valores = new Object[100][6];
         for ( int f = 0 ; f < filas; f++){
             for (int c = 0 ; c < 6; c++){
               valores[f][c] = this.jtbl_ventas.getValueAt(f, c).toString();
             }
         }
         for ( int h = 0 ; h < filas ; h++){
             int cantidadn = cant - Integer.parseInt(valores[h][3].toString());
                    String sql = "UPDATE `inventario` SET `cantidad`="+cantidadn+" WHERE  `id` = "+Integer.parseInt(valores[h][0].toString());
             try{
           Statement st = conexion.createStatement();
           st.execute(sql);
         }catch(SQLException ex){
            JOptionPane.showMessageDialog(null, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
       }        
         }
    }
    public boolean GuardarImprimir(){
        boolean confirmacacion = true;
        String vendedor = this.vendedor.getText();
        int id = Integer.parseInt(this.idfac.getText());
        Date cd = new Date();
        SimpleDateFormat fechas = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
       String fecha = fechas.format(cd);
       int filas = this.jtbl_ventas.getRowCount();
         Object[][] valores = new Object[100][6];
         try{
           Statement st = conexion.createStatement();
           st.execute("DELETE FROM `imprecion` WHERE 1");
          
         }catch(SQLException ex){
            
       }        
         for ( int f = 0 ; f < filas; f++){
             for (int c = 0 ; c < 6; c++){
               valores[f][c] = this.jtbl_ventas.getValueAt(f, c).toString();
             }
         }
         for ( int h = 0 ; h < filas ; h++){
            String sql = "INSERT INTO `imprecion`(`idfac`, `idpro`, `nombre`, `cantidad`, `subtotal`, `fecha`, `Total`,"
                    + " `vendedor`, `PreUnid`) VALUES ("+id+","
                    +Integer.parseInt(valores[h][0].toString())
                    +",'"+valores[h][1].toString()+"',"+Integer.parseInt(valores[h][3].toString())
                    +","+Float.parseFloat(valores[h][5].toString())+",'"+fecha+"',"
                    + "'"+total+"', '"+vendedor+"',"+Float.parseFloat(valores[h][4].toString())+")";
           
             try{
           Statement st = conexion.createStatement();
//           st.execute("DELETE FROM `imprecion` WHERE 1");
           st.execute(sql);
         }catch(SQLException ex){
            JOptionPane.showMessageDialog(null, ex.getMessage(), "Error guardando", JOptionPane.ERROR_MESSAGE);
            confirmacacion = false;
       }        
         }
         return confirmacacion;
    }
    
    
    
    public void Imprimir(){
        JasperReport reporte;
     JasperPrint reporte_view;     
     try{
         //direccion del archivo JASPER
          URL  in = this.getClass().getResource("factura.jasper");
          reporte = (JasperReport) JRLoader.loadObject( in ); 
          
          reporte_view= JasperFillManager.fillReport( reporte, new HashMap(), conexion );
        
//         JasperViewer.viewReport(reporte_view); 
         JasperViewer.viewReport(reporte_view, false);
          //terminamos la conexion a la base de datos
          
   }catch (JRException E){
     E.printStackTrace();
          }
    }
    
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        frame_ventas = new javax.swing.JFrame();
        jPanel13 = new javax.swing.JPanel();
        jtf_ventas_buscar = new javax.swing.JTextField();
        btn_ventas_buscar = new javax.swing.JButton();
        btn_ventas_agregar = new javax.swing.JButton();
        jtf_ventas_cantidad = new javax.swing.JTextField();
        lbl_etiqueta_cantidadavender = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jtbl_ventas = new javax.swing.JTable();
        btn_ventas_generar = new javax.swing.JButton();
        jcb_ventas_tipodebusqueda = new javax.swing.JComboBox();
        jLabel2 = new javax.swing.JLabel();
        jtf_vetas_total = new javax.swing.JTextField();
        idfac = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        fecha = new javax.swing.JLabel();
        vendedor = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        btn_nuevaventa_ventas = new javax.swing.JButton();
        btn_cancelar_ventas = new javax.swing.JButton();
        jLabel28 = new javax.swing.JLabel();
        jPanel19 = new javax.swing.JPanel();
        lbl_etiqueta_cantidad = new javax.swing.JLabel();
        lbl_etiqueta_color = new javax.swing.JLabel();
        lbl_etiqueta_talla = new javax.swing.JLabel();
        lbl_etiqueta_precio = new javax.swing.JLabel();
        lbl_etiqueta_id = new javax.swing.JLabel();
        lbl_etiqueta_nombre = new javax.swing.JLabel();
        lbl_etiqueta_tipo = new javax.swing.JLabel();
        lbl_etiqueta_1 = new javax.swing.JLabel();
        lbl_etiqueta_2 = new javax.swing.JLabel();
        lbl_etiqueta_3 = new javax.swing.JLabel();
        lbl_etiqueta_4 = new javax.swing.JLabel();
        lbl_etiqueta_5 = new javax.swing.JLabel();
        lbl_etiqueta_6 = new javax.swing.JLabel();
        lbl_etiqueta_7 = new javax.swing.JLabel();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu3 = new javax.swing.JMenu();
        frame_inventario = new javax.swing.JFrame();
        jTabbedPane5 = new javax.swing.JTabbedPane();
        jPanel9 = new javax.swing.JPanel();
        btn_editar_inventario = new javax.swing.JButton();
        btn_eliminar_inventario = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        btn_agregar_inventario = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        jtb_inventario_tablazapatos = new javax.swing.JTable();
        btn_actulizar_inventario = new javax.swing.JButton();
        jPanel12 = new javax.swing.JPanel();
        lbl_inventario_etiqueta_id2 = new javax.swing.JLabel();
        lbl_inventario_etiqueta_nombre1 = new javax.swing.JLabel();
        lbl_inventario_etiqueta_tipo1 = new javax.swing.JLabel();
        lbl_inventario_etiqueta_cantidad1 = new javax.swing.JLabel();
        lbl_inventario_etiqueta_talla1 = new javax.swing.JLabel();
        lbl_inventario_etiqueta_color1 = new javax.swing.JLabel();
        lbl_inventario_etiqueta_precio1 = new javax.swing.JLabel();
        lbl_inventario_etiqueta_8 = new javax.swing.JLabel();
        lbl_inventario_etiqueta_9 = new javax.swing.JLabel();
        lbl_inventario_etiqueta_10 = new javax.swing.JLabel();
        lbl_inventario_etiqueta_11 = new javax.swing.JLabel();
        lbl_inventario_etiqueta_13 = new javax.swing.JLabel();
        lbl_inventario_etiqueta_12 = new javax.swing.JLabel();
        lbl_inventario_etiqueta_14 = new javax.swing.JLabel();
        jPanel11 = new javax.swing.JPanel();
        jtf_inventario_etiqueta_8 = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jtf_inventario_etiqueta_10 = new javax.swing.JTextField();
        jtf_inventario_etiqueta_11 = new javax.swing.JTextField();
        jtf_inventario_etiqueta_13 = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jtf_inventario_etiqueta_12 = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        jtf_inventario_etiqueta_14 = new javax.swing.JTextField();
        bnt_opciones_inventario = new javax.swing.JButton();
        jLabel12 = new javax.swing.JLabel();
        jtf_inventario_etiqueta_9 = new javax.swing.JTextField();
        menubar_ventas1 = new javax.swing.JMenuBar();
        jMenu5 = new javax.swing.JMenu();
        frame_configuracion = new javax.swing.JFrame();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel2 = new javax.swing.JPanel();
        jTabbedPane2 = new javax.swing.JTabbedPane();
        jPanel4 = new javax.swing.JPanel();
        jPanel14 = new javax.swing.JPanel();
        jPanel15 = new javax.swing.JPanel();
        jButton4 = new javax.swing.JButton();
        jtf_usuario_configuracion = new javax.swing.JTextField();
        jLabel22 = new javax.swing.JLabel();
        jpf_contraseña_configuracion = new javax.swing.JPasswordField();
        jLabel23 = new javax.swing.JLabel();
        jPanel16 = new javax.swing.JPanel();
        jLabel24 = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        basededatos = new javax.swing.JTabbedPane();
        cambiaDb = new javax.swing.JPanel();
        jLabel16 = new javax.swing.JLabel();
        Cestado = new javax.swing.JLabel();
        jPanel6 = new javax.swing.JPanel();
        Cuser = new javax.swing.JTextField();
        Cpasword = new javax.swing.JPasswordField();
        jcb_basededats_configuracion = new javax.swing.JComboBox();
        jLabel17 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        Creconectar = new javax.swing.JButton();
        jLabel20 = new javax.swing.JLabel();
        dbConectada = new javax.swing.JLabel();
        IniciarSesion = new javax.swing.JFrame();
        jPanel17 = new javax.swing.JPanel();
        jButton5 = new javax.swing.JButton();
        jtf_usuario_configuracion1 = new javax.swing.JTextField();
        jLabel25 = new javax.swing.JLabel();
        jpf_contraseña_configuracion1 = new javax.swing.JPasswordField();
        jLabel26 = new javax.swing.JLabel();
        jPanel18 = new javax.swing.JPanel();
        jLabel27 = new javax.swing.JLabel();
        Acercade = new javax.swing.JFrame();
        jPanel20 = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        jLabel29 = new javax.swing.JLabel();
        youtube = new javax.swing.JLabel();
        instagran = new javax.swing.JLabel();
        google = new javax.swing.JLabel();
        facebook = new javax.swing.JLabel();
        twitter = new javax.swing.JLabel();
        jTabbedPane4 = new javax.swing.JTabbedPane();
        jPanel7 = new javax.swing.JPanel();
        btn_ventas = new javax.swing.JButton();
        btn_inventario = new javax.swing.JButton();
        lbl_ventas = new javax.swing.JLabel();
        lbl_inventario = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        reloj2 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        jButton2 = new javax.swing.JButton();
        jLabel30 = new javax.swing.JLabel();
        jPanel8 = new javax.swing.JPanel();
        Calendario = new com.toedter.calendar.JCalendar();
        jPanel10 = new javax.swing.JPanel();
        reloj = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        estadoConeccion = new javax.swing.JButton();
        menubar_principal = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenuItem1 = new javax.swing.JMenuItem();
        jMenu2 = new javax.swing.JMenu();
        Configuracion = new javax.swing.JMenuItem();
        jMenuItem3 = new javax.swing.JMenuItem();
        jSeparator2 = new javax.swing.JPopupMenu.Separator();
        jSeparator1 = new javax.swing.JPopupMenu.Separator();
        Acercade_barra = new javax.swing.JMenuItem();

        frame_ventas.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        frame_ventas.setTitle("Ventas");
        frame_ventas.addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosed(java.awt.event.WindowEvent evt) {
                SalirVentas(evt);
            }
            public void windowOpened(java.awt.event.WindowEvent evt) {
                AbrirVentas(evt);
            }
        });
        frame_ventas.getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel13.setBackground(new java.awt.Color(203, 240, 220));
        jPanel13.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Facturar venta", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 14))); // NOI18N
        jPanel13.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jtf_ventas_buscar.setToolTipText("");
        jPanel13.add(jtf_ventas_buscar, new org.netbeans.lib.awtextra.AbsoluteConstraints(260, 20, 174, -1));

        btn_ventas_buscar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Iconos/search.png"))); // NOI18N
        btn_ventas_buscar.setText("Buscar");
        btn_ventas_buscar.setToolTipText("Buscar el producto");
        btn_ventas_buscar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_ventas_buscarActionPerformed(evt);
            }
        });
        jPanel13.add(btn_ventas_buscar, new org.netbeans.lib.awtextra.AbsoluteConstraints(450, 20, -1, -1));

        btn_ventas_agregar.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        btn_ventas_agregar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Iconos/add.png"))); // NOI18N
        btn_ventas_agregar.setText("Agregar ");
        btn_ventas_agregar.setToolTipText("Agregar selecion a la factura");
        btn_ventas_agregar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_ventas_agregarActionPerformed(evt);
            }
        });
        jPanel13.add(btn_ventas_agregar, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 150, 137, -1));

        jtf_ventas_cantidad.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jtf_ventas_cantidad.setText("1");
        jtf_ventas_cantidad.setToolTipText("Cantidad de producto a agregar a la factura ");
        jtf_ventas_cantidad.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jtf_ventas_cantidadKeyTyped(evt);
            }
        });
        jPanel13.add(jtf_ventas_cantidad, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 150, 48, -1));

        lbl_etiqueta_cantidadavender.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lbl_etiqueta_cantidadavender.setText("Cantidad a Vender:");
        jPanel13.add(lbl_etiqueta_cantidadavender, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 150, -1, -1));

        jtbl_ventas.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID", "Nombre ", "Tipo", "Cantidad", "Precio", "SubTotal"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.Integer.class, java.lang.Float.class, java.lang.Float.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        jtbl_ventas.setToolTipText("Factura");
        jScrollPane1.setViewportView(jtbl_ventas);
        if (jtbl_ventas.getColumnModel().getColumnCount() > 0) {
            jtbl_ventas.getColumnModel().getColumn(5).setResizable(false);
        }

        jPanel13.add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 230, 540, 205));

        btn_ventas_generar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Iconos/accept.png"))); // NOI18N
        btn_ventas_generar.setText("Generar Venta");
        btn_ventas_generar.setToolTipText("Generar venta y visualizar factura");
        btn_ventas_generar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_ventas_generarActionPerformed(evt);
            }
        });
        jPanel13.add(btn_ventas_generar, new org.netbeans.lib.awtextra.AbsoluteConstraints(350, 490, 150, -1));

        jcb_ventas_tipodebusqueda.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "ID", "Nombre" }));
        jcb_ventas_tipodebusqueda.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jcb_ventas_tipodebusquedaActionPerformed(evt);
            }
        });
        jPanel13.add(jcb_ventas_tipodebusqueda, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 20, 123, -1));

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel2.setText("Total De la Venta:");
        jPanel13.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(320, 450, -1, -1));

        jtf_vetas_total.setEditable(false);
        jtf_vetas_total.setToolTipText("total a pagar ");
        jPanel13.add(jtf_vetas_total, new org.netbeans.lib.awtextra.AbsoluteConstraints(440, 450, 116, -1));

        idfac.setEditable(false);
        idfac.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        idfac.setText("1");
        idfac.setToolTipText("ID con l que se guardara la factura");
        jPanel13.add(idfac, new org.netbeans.lib.awtextra.AbsoluteConstraints(360, 200, 69, 20));

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel3.setText("ID Factura");
        jPanel13.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(290, 200, -1, -1));

        jLabel4.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel4.setText("Fecha:");
        jPanel13.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 200, -1, -1));

        fecha.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        fecha.setText("23-06-2014");
        fecha.setToolTipText("Fecha de venta");
        jPanel13.add(fecha, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 200, -1, -1));

        vendedor.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        vendedor.setText("Cilogmz");
        vendedor.setToolTipText("Vendedor que realiza la venta");
        jPanel13.add(vendedor, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 200, -1, -1));

        jLabel7.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel7.setText("Vendedor:");
        jPanel13.add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 200, -1, -1));

        jButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Iconos/delete.png"))); // NOI18N
        jButton1.setText("Eliminar producto");
        jButton1.setToolTipText("eliminar producto selecionado de la factura");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        jPanel13.add(jButton1, new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 490, 150, -1));

        btn_nuevaventa_ventas.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Iconos/download.png"))); // NOI18N
        btn_nuevaventa_ventas.setText("Nueva venta");
        btn_nuevaventa_ventas.setToolTipText("Limpiar panel para reiniciar venta");
        btn_nuevaventa_ventas.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_nuevaventa_ventasActionPerformed(evt);
            }
        });
        jPanel13.add(btn_nuevaventa_ventas, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 490, 150, -1));

        btn_cancelar_ventas.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Iconos/delete_page.png"))); // NOI18N
        btn_cancelar_ventas.setToolTipText("Cancelar la venta");
        btn_cancelar_ventas.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_cancelar_ventasActionPerformed(evt);
            }
        });
        jPanel13.add(btn_cancelar_ventas, new org.netbeans.lib.awtextra.AbsoluteConstraints(510, 490, 50, -1));

        jLabel28.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel28.setText("Productos:");
        jPanel13.add(jLabel28, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 20, 100, -1));

        jPanel19.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Producto selecionado", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 18))); // NOI18N

        lbl_etiqueta_cantidad.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lbl_etiqueta_cantidad.setText("Cantidad");

        lbl_etiqueta_color.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lbl_etiqueta_color.setText("Color");

        lbl_etiqueta_talla.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lbl_etiqueta_talla.setText("Talla");

        lbl_etiqueta_precio.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lbl_etiqueta_precio.setText("Precio");

        lbl_etiqueta_id.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lbl_etiqueta_id.setText("ID");

        lbl_etiqueta_nombre.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lbl_etiqueta_nombre.setText("Nombre");

        lbl_etiqueta_tipo.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lbl_etiqueta_tipo.setText("Tipo");

        lbl_etiqueta_1.setBackground(new java.awt.Color(225, 20, 19));
        lbl_etiqueta_1.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lbl_etiqueta_1.setText(" ");

        lbl_etiqueta_2.setBackground(new java.awt.Color(225, 20, 19));
        lbl_etiqueta_2.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lbl_etiqueta_2.setText(" ");

        lbl_etiqueta_3.setBackground(new java.awt.Color(225, 20, 19));
        lbl_etiqueta_3.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lbl_etiqueta_3.setText(" ");

        lbl_etiqueta_4.setBackground(new java.awt.Color(225, 20, 19));
        lbl_etiqueta_4.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lbl_etiqueta_4.setText(" ");

        lbl_etiqueta_5.setBackground(new java.awt.Color(225, 20, 19));
        lbl_etiqueta_5.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lbl_etiqueta_5.setText(" ");

        lbl_etiqueta_6.setBackground(new java.awt.Color(225, 20, 19));
        lbl_etiqueta_6.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lbl_etiqueta_6.setText(" ");

        lbl_etiqueta_7.setBackground(new java.awt.Color(225, 20, 19));
        lbl_etiqueta_7.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lbl_etiqueta_7.setText(" ");

        javax.swing.GroupLayout jPanel19Layout = new javax.swing.GroupLayout(jPanel19);
        jPanel19.setLayout(jPanel19Layout);
        jPanel19Layout.setHorizontalGroup(
            jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel19Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel19Layout.createSequentialGroup()
                        .addComponent(lbl_etiqueta_id)
                        .addGap(37, 37, 37)
                        .addComponent(lbl_etiqueta_nombre)
                        .addGap(76, 76, 76)
                        .addComponent(lbl_etiqueta_tipo, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(76, 76, 76)
                        .addComponent(lbl_etiqueta_cantidad)
                        .addGap(20, 20, 20)
                        .addComponent(lbl_etiqueta_color)
                        .addGap(51, 51, 51)
                        .addComponent(lbl_etiqueta_talla, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(20, 20, 20)
                        .addComponent(lbl_etiqueta_precio, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel19Layout.createSequentialGroup()
                        .addComponent(lbl_etiqueta_1, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(lbl_etiqueta_2, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(lbl_etiqueta_3, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(10, 10, 10)
                        .addComponent(lbl_etiqueta_4, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(lbl_etiqueta_5, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(41, 41, 41)
                        .addComponent(lbl_etiqueta_6, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(20, 20, 20)
                        .addComponent(lbl_etiqueta_7, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel19Layout.setVerticalGroup(
            jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel19Layout.createSequentialGroup()
                .addGroup(jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lbl_etiqueta_id)
                    .addComponent(lbl_etiqueta_nombre)
                    .addComponent(lbl_etiqueta_tipo)
                    .addComponent(lbl_etiqueta_cantidad)
                    .addComponent(lbl_etiqueta_color)
                    .addComponent(lbl_etiqueta_talla)
                    .addComponent(lbl_etiqueta_precio))
                .addGap(6, 6, 6)
                .addGroup(jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lbl_etiqueta_1)
                    .addComponent(lbl_etiqueta_2)
                    .addComponent(lbl_etiqueta_3)
                    .addComponent(lbl_etiqueta_4)
                    .addComponent(lbl_etiqueta_5)
                    .addComponent(lbl_etiqueta_6)
                    .addComponent(lbl_etiqueta_7))
                .addGap(0, 22, Short.MAX_VALUE))
        );

        jPanel13.add(jPanel19, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 60, 560, 80));

        frame_ventas.getContentPane().add(jPanel13, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 580, 560));

        jMenu3.setText("Archivo");
        jMenuBar1.add(jMenu3);

        frame_ventas.setJMenuBar(jMenuBar1);

        frame_inventario.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        frame_inventario.setTitle("Inventario");
        frame_inventario.addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                SalirInventario(evt);
            }
            public void windowOpened(java.awt.event.WindowEvent evt) {
                AbrirInventario(evt);
            }
        });

        jPanel9.setBackground(new java.awt.Color(203, 240, 220));

        btn_editar_inventario.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Iconos/tag_blue.png"))); // NOI18N
        btn_editar_inventario.setText("Editar");
        btn_editar_inventario.setToolTipText("Editar  el producto  selecionado");
        btn_editar_inventario.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_editar_inventarioActionPerformed(evt);
            }
        });

        btn_eliminar_inventario.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Iconos/warning.png"))); // NOI18N
        btn_eliminar_inventario.setText("Eliminar");
        btn_eliminar_inventario.setToolTipText("Eliminar el producto que selecionado");
        btn_eliminar_inventario.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_eliminar_inventarioActionPerformed(evt);
            }
        });

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel1.setText("Agregar un nuevo producto a inventario");

        btn_agregar_inventario.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Iconos/adry.png"))); // NOI18N
        btn_agregar_inventario.setText("Agregar");
        btn_agregar_inventario.setToolTipText("Agregar un nuevo producto al inventario");
        btn_agregar_inventario.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_agregar_inventarioActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(60, 60, 60)
                .addComponent(jLabel1)
                .addGap(34, 34, 34)
                .addComponent(btn_agregar_inventario, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(41, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(btn_agregar_inventario))
                .addContainerGap(31, Short.MAX_VALUE))
        );

        jtb_inventario_tablazapatos.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jtb_inventario_tablazapatos.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID", "NOMBRE", "TIPO", "CANTIDAD", "COLOR", "TALLA", "PRECIO"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.Integer.class, java.lang.String.class, java.lang.Integer.class, java.lang.Float.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jtb_inventario_tablazapatos.setToolTipText("Productos existente en nuestro inventario");
        jtb_inventario_tablazapatos.setName("Inventario de zapatos"); // NOI18N
        jScrollPane2.setViewportView(jtb_inventario_tablazapatos);
        if (jtb_inventario_tablazapatos.getColumnModel().getColumnCount() > 0) {
            jtb_inventario_tablazapatos.getColumnModel().getColumn(0).setResizable(false);
            jtb_inventario_tablazapatos.getColumnModel().getColumn(1).setResizable(false);
            jtb_inventario_tablazapatos.getColumnModel().getColumn(2).setResizable(false);
            jtb_inventario_tablazapatos.getColumnModel().getColumn(3).setResizable(false);
            jtb_inventario_tablazapatos.getColumnModel().getColumn(4).setResizable(false);
            jtb_inventario_tablazapatos.getColumnModel().getColumn(5).setResizable(false);
            jtb_inventario_tablazapatos.getColumnModel().getColumn(6).setResizable(false);
        }

        btn_actulizar_inventario.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Iconos/refresh.png"))); // NOI18N
        btn_actulizar_inventario.setToolTipText("Actualizar el estado del inventario ");
        btn_actulizar_inventario.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_actulizar_inventarioActionPerformed(evt);
            }
        });

        jPanel12.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Producto selecionado", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 14))); // NOI18N

        lbl_inventario_etiqueta_id2.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        lbl_inventario_etiqueta_id2.setText("ID");

        lbl_inventario_etiqueta_nombre1.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        lbl_inventario_etiqueta_nombre1.setText("Nombre");

        lbl_inventario_etiqueta_tipo1.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        lbl_inventario_etiqueta_tipo1.setText("Tipo");

        lbl_inventario_etiqueta_cantidad1.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        lbl_inventario_etiqueta_cantidad1.setText("Cantidad");

        lbl_inventario_etiqueta_talla1.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        lbl_inventario_etiqueta_talla1.setText("Talla");

        lbl_inventario_etiqueta_color1.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        lbl_inventario_etiqueta_color1.setText("Color");

        lbl_inventario_etiqueta_precio1.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        lbl_inventario_etiqueta_precio1.setText("Precio");

        lbl_inventario_etiqueta_8.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        lbl_inventario_etiqueta_9.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        lbl_inventario_etiqueta_10.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        lbl_inventario_etiqueta_11.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        lbl_inventario_etiqueta_13.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        lbl_inventario_etiqueta_12.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        lbl_inventario_etiqueta_14.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        javax.swing.GroupLayout jPanel12Layout = new javax.swing.GroupLayout(jPanel12);
        jPanel12.setLayout(jPanel12Layout);
        jPanel12Layout.setHorizontalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel12Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel12Layout.createSequentialGroup()
                        .addComponent(lbl_inventario_etiqueta_8, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lbl_inventario_etiqueta_9, javax.swing.GroupLayout.PREFERRED_SIZE, 111, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel12Layout.createSequentialGroup()
                        .addComponent(lbl_inventario_etiqueta_id2, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lbl_inventario_etiqueta_nombre1, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(12, 12, 12)
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lbl_inventario_etiqueta_tipo1, javax.swing.GroupLayout.PREFERRED_SIZE, 81, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbl_inventario_etiqueta_10, javax.swing.GroupLayout.PREFERRED_SIZE, 123, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel12Layout.createSequentialGroup()
                        .addComponent(lbl_inventario_etiqueta_cantidad1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(lbl_inventario_etiqueta_talla1, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel12Layout.createSequentialGroup()
                        .addComponent(lbl_inventario_etiqueta_11, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(41, 41, 41)
                        .addComponent(lbl_inventario_etiqueta_13, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(34, 34, 34)
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel12Layout.createSequentialGroup()
                        .addComponent(lbl_inventario_etiqueta_12, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(lbl_inventario_etiqueta_14, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 10, Short.MAX_VALUE))
                    .addGroup(jPanel12Layout.createSequentialGroup()
                        .addComponent(lbl_inventario_etiqueta_color1, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(lbl_inventario_etiqueta_precio1, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );
        jPanel12Layout.setVerticalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel12Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lbl_inventario_etiqueta_id2)
                    .addComponent(lbl_inventario_etiqueta_nombre1)
                    .addComponent(lbl_inventario_etiqueta_cantidad1)
                    .addComponent(lbl_inventario_etiqueta_color1)
                    .addComponent(lbl_inventario_etiqueta_talla1)
                    .addComponent(lbl_inventario_etiqueta_precio1)
                    .addComponent(lbl_inventario_etiqueta_tipo1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(lbl_inventario_etiqueta_9, javax.swing.GroupLayout.DEFAULT_SIZE, 19, Short.MAX_VALUE)
                    .addComponent(lbl_inventario_etiqueta_8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lbl_inventario_etiqueta_10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lbl_inventario_etiqueta_11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lbl_inventario_etiqueta_13, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lbl_inventario_etiqueta_12, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lbl_inventario_etiqueta_14, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2)
                    .addComponent(jPanel12, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel9Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addComponent(btn_editar_inventario, javax.swing.GroupLayout.PREFERRED_SIZE, 183, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btn_eliminar_inventario, javax.swing.GroupLayout.PREFERRED_SIZE, 183, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btn_actulizar_inventario))
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(115, 115, 115))
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 132, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(btn_editar_inventario)
                        .addComponent(btn_eliminar_inventario))
                    .addComponent(btn_actulizar_inventario, javax.swing.GroupLayout.Alignment.TRAILING))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jTabbedPane5.addTab("Inventario", jPanel9);

        jPanel11.setBackground(new java.awt.Color(203, 240, 220));
        jPanel11.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(new java.awt.Color(0, 204, 255), null), "Opciones", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 18))); // NOI18N

        jtf_inventario_etiqueta_8.setEditable(false);
        jtf_inventario_etiqueta_8.setToolTipText("Codigo unico de cada producto");
        jtf_inventario_etiqueta_8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jtf_inventario_etiqueta_8ActionPerformed(evt);
            }
        });
        jtf_inventario_etiqueta_8.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jtf_inventario_etiqueta_8KeyTyped(evt);
            }
        });

        jLabel5.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel5.setText("ID:");

        jLabel6.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel6.setText("Tipo:");

        jtf_inventario_etiqueta_11.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jtf_inventario_etiqueta_11KeyTyped(evt);
            }
        });

        jtf_inventario_etiqueta_13.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jtf_inventario_etiqueta_13KeyTyped(evt);
            }
        });

        jLabel8.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel8.setText("Cantidad:");

        jLabel9.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel9.setText("Talla");

        jLabel10.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel10.setText("Color");

        jLabel11.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel11.setText("Precio");

        jtf_inventario_etiqueta_14.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jtf_inventario_etiqueta_14KeyTyped(evt);
            }
        });

        bnt_opciones_inventario.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        bnt_opciones_inventario.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Iconos/next.png"))); // NOI18N
        bnt_opciones_inventario.setText("Aplicar");
        bnt_opciones_inventario.setToolTipText("Aplicar la operacion selecionada ");
        bnt_opciones_inventario.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bnt_opciones_inventarioActionPerformed(evt);
            }
        });

        jLabel12.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel12.setText("Nombre");

        javax.swing.GroupLayout jPanel11Layout = new javax.swing.GroupLayout(jPanel11);
        jPanel11.setLayout(jPanel11Layout);
        jPanel11Layout.setHorizontalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel11Layout.createSequentialGroup()
                        .addGap(45, 45, 45)
                        .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jtf_inventario_etiqueta_10, javax.swing.GroupLayout.PREFERRED_SIZE, 136, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jtf_inventario_etiqueta_11, javax.swing.GroupLayout.PREFERRED_SIZE, 136, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jtf_inventario_etiqueta_13, javax.swing.GroupLayout.PREFERRED_SIZE, 136, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanel11Layout.createSequentialGroup()
                        .addGap(45, 45, 45)
                        .addComponent(jtf_inventario_etiqueta_9))
                    .addGroup(jPanel11Layout.createSequentialGroup()
                        .addGap(45, 45, 45)
                        .addComponent(jtf_inventario_etiqueta_12))
                    .addGroup(jPanel11Layout.createSequentialGroup()
                        .addGap(45, 45, 45)
                        .addComponent(jtf_inventario_etiqueta_14))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel11Layout.createSequentialGroup()
                        .addGap(21, 21, 21)
                        .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(bnt_opciones_inventario, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(jPanel11Layout.createSequentialGroup()
                                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel6)
                                    .addComponent(jLabel12)
                                    .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 61, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel9)
                                    .addComponent(jLabel10)
                                    .addComponent(jLabel11)
                                    .addGroup(jPanel11Layout.createSequentialGroup()
                                        .addComponent(jLabel5)
                                        .addGap(27, 27, 27)
                                        .addComponent(jtf_inventario_etiqueta_8, javax.swing.GroupLayout.PREFERRED_SIZE, 96, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addGap(0, 0, Short.MAX_VALUE)))))
                .addGap(24, 24, 24))
        );
        jPanel11Layout.setVerticalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(jtf_inventario_etiqueta_8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel12)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jtf_inventario_etiqueta_9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(3, 3, 3)
                .addComponent(jLabel6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jtf_inventario_etiqueta_10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel8)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jtf_inventario_etiqueta_11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel9)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jtf_inventario_etiqueta_13, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel10)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jtf_inventario_etiqueta_12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel11)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jtf_inventario_etiqueta_14, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(bnt_opciones_inventario)
                .addContainerGap(24, Short.MAX_VALUE))
        );

        menubar_ventas1.setBackground(new java.awt.Color(203, 240, 220));

        jMenu5.setText("Archivo");
        menubar_ventas1.add(jMenu5);

        frame_inventario.setJMenuBar(menubar_ventas1);

        javax.swing.GroupLayout frame_inventarioLayout = new javax.swing.GroupLayout(frame_inventario.getContentPane());
        frame_inventario.getContentPane().setLayout(frame_inventarioLayout);
        frame_inventarioLayout.setHorizontalGroup(
            frame_inventarioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(frame_inventarioLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jTabbedPane5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, Short.MAX_VALUE)
                .addComponent(jPanel11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        frame_inventarioLayout.setVerticalGroup(
            frame_inventarioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(frame_inventarioLayout.createSequentialGroup()
                .addGroup(frame_inventarioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(frame_inventarioLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jPanel11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(jTabbedPane5))
                .addContainerGap())
        );

        frame_configuracion.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        frame_configuracion.setTitle("Configuracion");
        frame_configuracion.addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                SalirConfiguracion(evt);
            }
            public void windowOpened(java.awt.event.WindowEvent evt) {
                AbrirConfiguracion(evt);
            }
        });

        jTabbedPane2.setBackground(new java.awt.Color(203, 240, 220));

        jPanel4.setBackground(new java.awt.Color(203, 240, 220));

        jPanel14.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jPanel15.setBackground(new java.awt.Color(203, 240, 220));
        jPanel15.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jButton4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Iconos/accept.png"))); // NOI18N
        jButton4.setText("Iniciar sesión ");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        jLabel22.setText("Contraseña:");

        jLabel23.setText("Usuario:");

        jPanel16.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel24.setText("Cambiar de usuario");

        javax.swing.GroupLayout jPanel16Layout = new javax.swing.GroupLayout(jPanel16);
        jPanel16.setLayout(jPanel16Layout);
        jPanel16Layout.setHorizontalGroup(
            jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel16Layout.createSequentialGroup()
                .addGap(76, 76, 76)
                .addComponent(jLabel24)
                .addContainerGap(108, Short.MAX_VALUE))
        );
        jPanel16Layout.setVerticalGroup(
            jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel16Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel24)
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel15Layout = new javax.swing.GroupLayout(jPanel15);
        jPanel15.setLayout(jPanel15Layout);
        jPanel15Layout.setHorizontalGroup(
            jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel15Layout.createSequentialGroup()
                .addGap(92, 92, 92)
                .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel23, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel15Layout.createSequentialGroup()
                        .addComponent(jLabel22)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel15Layout.createSequentialGroup()
                .addGap(0, 42, Short.MAX_VALUE)
                .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel15Layout.createSequentialGroup()
                        .addComponent(jPanel16, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(43, 43, 43))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel15Layout.createSequentialGroup()
                        .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jpf_contraseña_configuracion, javax.swing.GroupLayout.PREFERRED_SIZE, 155, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jtf_usuario_configuracion, javax.swing.GroupLayout.PREFERRED_SIZE, 155, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel15Layout.createSequentialGroup()
                                .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, 126, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(9, 9, 9)))
                        .addGap(95, 95, 95))))
        );
        jPanel15Layout.setVerticalGroup(
            jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel15Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jPanel16, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel23)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jtf_usuario_configuracion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel22)
                .addGap(5, 5, 5)
                .addComponent(jpf_contraseña_configuracion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jButton4)
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel14Layout = new javax.swing.GroupLayout(jPanel14);
        jPanel14.setLayout(jPanel14Layout);
        jPanel14Layout.setHorizontalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel14Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel15, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel14Layout.setVerticalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel14Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel15, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel14, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel14, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPane2.addTab("Usuario", jPanel4);

        jPanel5.setBackground(new java.awt.Color(203, 240, 220));

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 413, Short.MAX_VALUE)
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 256, Short.MAX_VALUE)
        );

        jTabbedPane2.addTab("Opciones", jPanel5);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane2)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane2)
        );

        jTabbedPane1.addTab("Configuracion basica", jPanel2);

        cambiaDb.setBackground(new java.awt.Color(203, 240, 220));
        cambiaDb.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel16.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel16.setText("Estado de la coneccion:");

        Cestado.setText("esperando...");
        Cestado.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jPanel6.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        Cuser.setText("root");

        jcb_basededats_configuracion.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Zapateria", "Bodega1", "Bodega2" }));

        jLabel17.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel17.setText("Base de datos");

        jLabel18.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel18.setText("Usuario:");

        jLabel19.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel19.setText("Contraseña:");

        Creconectar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Iconos/refresh.png"))); // NOI18N
        Creconectar.setText("Reconectar");
        Creconectar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CreconectarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGap(38, 38, 38)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel17)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addGap(22, 22, 22)
                        .addComponent(jLabel18))
                    .addComponent(jLabel19, javax.swing.GroupLayout.Alignment.TRAILING))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 71, Short.MAX_VALUE)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jcb_basededats_configuracion, javax.swing.GroupLayout.PREFERRED_SIZE, 147, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(Cuser)
                        .addComponent(Cpasword, javax.swing.GroupLayout.PREFERRED_SIZE, 147, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(36, 36, 36))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(Creconectar)
                .addGap(119, 119, 119))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jcb_basededats_configuracion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel17))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(Cuser, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel18))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(Cpasword, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel19))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(Creconectar)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLabel20.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel20.setText("Base de datos:");

        dbConectada.setText("Esperando");

        javax.swing.GroupLayout cambiaDbLayout = new javax.swing.GroupLayout(cambiaDb);
        cambiaDb.setLayout(cambiaDbLayout);
        cambiaDbLayout.setHorizontalGroup(
            cambiaDbLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(cambiaDbLayout.createSequentialGroup()
                .addGroup(cambiaDbLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(cambiaDbLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(cambiaDbLayout.createSequentialGroup()
                        .addGap(53, 53, 53)
                        .addGroup(cambiaDbLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel20, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel16, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(30, 30, 30)
                        .addGroup(cambiaDbLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(Cestado, javax.swing.GroupLayout.DEFAULT_SIZE, 109, Short.MAX_VALUE)
                            .addComponent(dbConectada, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap(17, Short.MAX_VALUE))
        );
        cambiaDbLayout.setVerticalGroup(
            cambiaDbLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(cambiaDbLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(cambiaDbLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel16)
                    .addComponent(Cestado))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(cambiaDbLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel20)
                    .addComponent(dbConectada))
                .addGap(18, 18, 18)
                .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        basededatos.addTab("Base de datos", cambiaDb);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(basededatos)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(basededatos)
        );

        jTabbedPane1.addTab("Configuracion avanzada", jPanel3);

        javax.swing.GroupLayout frame_configuracionLayout = new javax.swing.GroupLayout(frame_configuracion.getContentPane());
        frame_configuracion.getContentPane().setLayout(frame_configuracionLayout);
        frame_configuracionLayout.setHorizontalGroup(
            frame_configuracionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(frame_configuracionLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jTabbedPane1)
                .addContainerGap())
        );
        frame_configuracionLayout.setVerticalGroup(
            frame_configuracionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(frame_configuracionLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jTabbedPane1)
                .addContainerGap())
        );

        jPanel17.setBackground(new java.awt.Color(203, 240, 220));
        jPanel17.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jButton5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Iconos/lock.png"))); // NOI18N
        jButton5.setText("Iniciar sesión ");
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

        jtf_usuario_configuracion1.setText("cilogmz");
        jtf_usuario_configuracion1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jtf_usuario_configuracion1ActionPerformed(evt);
            }
        });

        jLabel25.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel25.setText("Contraseña:");

        jpf_contraseña_configuracion1.setText("3206699872");

        jLabel26.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel26.setText("Usuario:");

        jPanel18.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel27.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel27.setText("Iniciar Sesion Zapt V1.0");

        javax.swing.GroupLayout jPanel18Layout = new javax.swing.GroupLayout(jPanel18);
        jPanel18.setLayout(jPanel18Layout);
        jPanel18Layout.setHorizontalGroup(
            jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel18Layout.createSequentialGroup()
                .addContainerGap(97, Short.MAX_VALUE)
                .addComponent(jLabel27)
                .addGap(87, 87, 87))
        );
        jPanel18Layout.setVerticalGroup(
            jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel18Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel27)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel17Layout = new javax.swing.GroupLayout(jPanel17);
        jPanel17.setLayout(jPanel17Layout);
        jPanel17Layout.setHorizontalGroup(
            jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel17Layout.createSequentialGroup()
                .addGap(92, 92, 92)
                .addGroup(jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel26, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel17Layout.createSequentialGroup()
                        .addComponent(jLabel25)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(jPanel17Layout.createSequentialGroup()
                .addGap(23, 23, 23)
                .addComponent(jPanel18, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(30, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel17Layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addGroup(jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jpf_contraseña_configuracion1, javax.swing.GroupLayout.DEFAULT_SIZE, 155, Short.MAX_VALUE)
                    .addComponent(jtf_usuario_configuracion1, javax.swing.GroupLayout.DEFAULT_SIZE, 155, Short.MAX_VALUE)
                    .addComponent(jButton5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(95, 95, 95))
        );
        jPanel17Layout.setVerticalGroup(
            jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel17Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel18, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel26)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jtf_usuario_configuracion1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel25)
                .addGap(5, 5, 5)
                .addComponent(jpf_contraseña_configuracion1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jButton5)
                .addContainerGap())
        );

        javax.swing.GroupLayout IniciarSesionLayout = new javax.swing.GroupLayout(IniciarSesion.getContentPane());
        IniciarSesion.getContentPane().setLayout(IniciarSesionLayout);
        IniciarSesionLayout.setHorizontalGroup(
            IniciarSesionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(IniciarSesionLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel17, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        IniciarSesionLayout.setVerticalGroup(
            IniciarSesionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(IniciarSesionLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel17, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        Acercade.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        Acercade.setTitle("Acerca de ZaDosV1.0 - Cilogmz");
        Acercade.setBackground(new java.awt.Color(68, 204, 226));
        Acercade.addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosed(java.awt.event.WindowEvent evt) {
                AcercadecerrarA(evt);
            }
        });

        jPanel20.setBackground(new java.awt.Color(203, 240, 220));
        jPanel20.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jTextArea1.setEditable(false);
        jTextArea1.setColumns(20);
        jTextArea1.setFont(new java.awt.Font("Arial Narrow", 2, 18)); // NOI18N
        jTextArea1.setRows(5);
        jTextArea1.setText("> Programa para administrar Zapateria \n\n> Realizado por:\n\t\tJuan Camilo Gómez M.\n\t\tEstudiante de Sistemas \n\t\t@Cilogmz");
        jScrollPane4.setViewportView(jTextArea1);

        jLabel29.setFont(new java.awt.Font("Arial Narrow", 3, 24)); // NOI18N
        jLabel29.setText("ZaDos  V 1.0.0 ");

        youtube.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Iconos/youtube_64.png"))); // NOI18N
        youtube.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        youtube.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                youtubeMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                youtubeMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                youtubeMouseExited(evt);
            }
        });

        instagran.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Iconos/instagram_64.png"))); // NOI18N
        instagran.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        instagran.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                instagranMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                instagranMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                instagranMouseExited(evt);
            }
        });

        google.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Iconos/google_64.png"))); // NOI18N
        google.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        google.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                googleMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                googleMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                googleMouseExited(evt);
            }
        });

        facebook.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Iconos/facebook_64.png"))); // NOI18N
        facebook.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        facebook.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                facebookMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                facebookMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                facebookMouseExited(evt);
            }
        });

        twitter.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Iconos/1397001360_social_twitter_box_blue.png"))); // NOI18N
        twitter.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        twitter.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                twitterMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                twitterMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                twitterMouseExited(evt);
            }
        });

        javax.swing.GroupLayout jPanel20Layout = new javax.swing.GroupLayout(jPanel20);
        jPanel20.setLayout(jPanel20Layout);
        jPanel20Layout.setHorizontalGroup(
            jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel20Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane4)
                    .addGroup(jPanel20Layout.createSequentialGroup()
                        .addComponent(twitter)
                        .addGap(18, 18, 18)
                        .addComponent(facebook)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 15, Short.MAX_VALUE)
                        .addComponent(instagran)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(google)
                        .addGap(18, 18, 18)
                        .addComponent(youtube)))
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel20Layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(jLabel29)
                .addGap(145, 145, 145))
        );
        jPanel20Layout.setVerticalGroup(
            jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel20Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel29)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 137, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(youtube)
                    .addComponent(google)
                    .addComponent(instagran)
                    .addComponent(facebook)
                    .addComponent(twitter))
                .addContainerGap())
        );

        javax.swing.GroupLayout AcercadeLayout = new javax.swing.GroupLayout(Acercade.getContentPane());
        Acercade.getContentPane().setLayout(AcercadeLayout);
        AcercadeLayout.setHorizontalGroup(
            AcercadeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(AcercadeLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel20, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        AcercadeLayout.setVerticalGroup(
            AcercadeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(AcercadeLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel20, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jTabbedPane4.setBackground(new java.awt.Color(203, 240, 220));
        jTabbedPane4.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jPanel7.setBackground(new java.awt.Color(203, 240, 220));

        btn_ventas.setText("Ventas");
        btn_ventas.setToolTipText("Generar una ventas(facturas)");
        btn_ventas.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_ventasActionPerformed(evt);
            }
        });

        btn_inventario.setText("Inventario");
        btn_inventario.setToolTipText("Ingresar al inventario");
        btn_inventario.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_inventarioActionPerformed(evt);
            }
        });

        lbl_ventas.setText("Imgresar al panel de ventas");

        lbl_inventario.setText("Revisar y administrar el inventario");

        jLabel15.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel15.setText("Hora");

        reloj2.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        reloj2.setText("...............");
        reloj2.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel21.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Iconos/24881.png"))); // NOI18N
        jLabel21.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jButton2.setText("Contabilidad");
        jButton2.setToolTipText("Panel contable");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jLabel30.setText("Ingresar al area contable");

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel7Layout.createSequentialGroup()
                .addContainerGap(52, Short.MAX_VALUE)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel7Layout.createSequentialGroup()
                        .addComponent(jLabel15)
                        .addGap(18, 18, 18)
                        .addComponent(reloj2)
                        .addGap(59, 59, 59))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel7Layout.createSequentialGroup()
                        .addComponent(jLabel21)
                        .addGap(43, 43, 43))))
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(lbl_inventario, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lbl_ventas)
                    .addComponent(jLabel30, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jButton2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btn_ventas, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btn_inventario, javax.swing.GroupLayout.DEFAULT_SIZE, 121, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel15)
                    .addComponent(reloj2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lbl_ventas)
                    .addComponent(btn_ventas, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lbl_inventario)
                    .addComponent(btn_inventario))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton2)
                    .addComponent(jLabel30))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 19, Short.MAX_VALUE)
                .addComponent(jLabel21, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jTabbedPane4.addTab("Inicio", jPanel7);

        jPanel8.setBackground(new java.awt.Color(203, 240, 220));

        Calendario.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        jPanel10.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel10.setToolTipText("Hora actual");

        reloj.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        reloj.setText(" ");

        jLabel13.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel13.setText("Hora:");

        javax.swing.GroupLayout jPanel10Layout = new javax.swing.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel10Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(reloj, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel10Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(reloj)
                    .addComponent(jLabel13))
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(Calendario, javax.swing.GroupLayout.DEFAULT_SIZE, 335, Short.MAX_VALUE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel8Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jPanel10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(85, 85, 85))
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel8Layout.createSequentialGroup()
                .addContainerGap(36, Short.MAX_VALUE)
                .addComponent(jPanel10, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(Calendario, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jTabbedPane4.addTab("Calendario", jPanel8);

        jLabel14.setText("Estado de la conexion a la base de datos");

        estadoConeccion.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        estadoConeccion.setToolTipText("Estado de la conexion a la base de datos");
        estadoConeccion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                estadoConeccionActionPerformed(evt);
            }
        });

        menubar_principal.setBackground(new java.awt.Color(203, 240, 220));

        jMenu1.setText("Archivo");

        jMenuItem1.setText("Salir");
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem1);

        menubar_principal.add(jMenu1);

        jMenu2.setText("Opciones");

        Configuracion.setText("Configuracion");
        Configuracion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ConfiguracionActionPerformed(evt);
            }
        });
        jMenu2.add(Configuracion);

        jMenuItem3.setText("Inventario");
        jMenuItem3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem3ActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItem3);
        jMenu2.add(jSeparator2);
        jMenu2.add(jSeparator1);

        Acercade_barra.setText("Acerca de...");
        Acercade_barra.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Acercade_barraActionPerformed(evt);
            }
        });
        jMenu2.add(Acercade_barra);

        menubar_principal.add(jMenu2);

        setJMenuBar(menubar_principal);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jTabbedPane4)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel14)
                        .addGap(18, 18, 18)
                        .addComponent(estadoConeccion, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel14)
                    .addComponent(estadoConeccion, javax.swing.GroupLayout.DEFAULT_SIZE, 19, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTabbedPane4)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btn_ventasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_ventasActionPerformed
        // llamar el frame de ventas
        this.frame_ventas.pack(); // que el tamaño del frame sea el de los componentes
        this.frame_ventas.setVisible(true);// hacer la ventana visible
        this.frame_ventas.setResizable(false);
        this.frame_ventas.setLocationRelativeTo(null);
//         this.frame_ventas.setSize(560, 460);
        // generar la lista de auntocompletar para el jtextfil de la busqueda 
        // por "id"
        AutoCompletar(0);
        // numero de la factura
        NumeroFactura();
        Date cd = this.Calendario.getDate();
        SimpleDateFormat formateador = new SimpleDateFormat("dd/MM/yyyy");
        String fecha = formateador.format(cd);
        this.fecha.setText(fecha);
        this.btn_ventas_agregar.setEnabled(false);
        this.vendedor.setText(ProgramaUser);
    }//GEN-LAST:event_btn_ventasActionPerformed

    private void btn_inventarioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_inventarioActionPerformed
        // llamar el frame del inventario
        this.frame_inventario.pack(); // que el tamaño del frame sea el de los componentes
        this.frame_inventario.setVisible(true);// hacer la ventana visible
        this.frame_inventario.setLocationRelativeTo(null);// paracece en el medio
        this.frame_inventario.setResizable(false);
        // llenar la tabla del inventario de los zapatos
        consultas();
    }//GEN-LAST:event_btn_inventarioActionPerformed

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
       // ordenar salir al programa 
        System.exit(0);
    }//GEN-LAST:event_jMenuItem1ActionPerformed

    private void jcb_ventas_tipodebusquedaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jcb_ventas_tipodebusquedaActionPerformed
        // definir de que forma se llenara el autocompletar
        int op = this.jcb_ventas_tipodebusqueda.getSelectedIndex();
        AutoCompletar(op);
    }//GEN-LAST:event_jcb_ventas_tipodebusquedaActionPerformed

    private void btn_ventas_buscarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_ventas_buscarActionPerformed
        
        // generar el texto que emos escrito en el jtextfil para ser buscado
        String textoBuscar = (String) this.jtf_ventas_buscar.getText();
        // generar el texto del item selecionado en combo box de tipo de busqueda
        String t = (String) this.jcb_ventas_tipodebusqueda.getSelectedItem();
        // convertir el texto anterior en minusculas solamente
        String tipo = t.toLowerCase();
        // realizar la busqueda de los datos segun el tipo y el dato ingersado
         try{
           Statement st = conexion.createStatement();
           ResultSet rs;
           rs = st.executeQuery("SELECT * FROM `inventario` WHERE `"+tipo+"` like '"+textoBuscar+"';");
           while(rs.next()){
               // Colocr los datos en las etiquitas 
               this.lbl_etiqueta_1.setText(rs.getString("id"));
               this.lbl_etiqueta_2.setText(rs.getString("nombre"));
               this.lbl_etiqueta_3.setText(rs.getString("tipo"));
               this.lbl_etiqueta_4.setText(rs.getString("cantidad"));
               this.lbl_etiqueta_5.setText(rs.getString("color"));
               this.lbl_etiqueta_6.setText(rs.getString("talla"));
               this.lbl_etiqueta_7.setText(rs.getString("precio"));
               this.btn_ventas_agregar.setEnabled(true);
           }
           
         }catch(SQLException ex){
            JOptionPane.showMessageDialog(null, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
       }        
    }//GEN-LAST:event_btn_ventas_buscarActionPerformed

    private void btn_ventas_agregarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_ventas_agregarActionPerformed
try{
          // extraer la informacion de los labels para llenar la taba de factura    
        String idVenta = this.lbl_etiqueta_1.getText(); // id
        String  nombreVenta = this.lbl_etiqueta_2.getText(); // nombre
        String  tipoVenta = this.lbl_etiqueta_3.getText(); // tipo
        int cantidad = Integer.parseInt(this.jtf_ventas_cantidad.getText()); // cantida de zapatos que agregaremos a la factura
        float precio = Float.parseFloat(this.lbl_etiqueta_7.getText()); // percio unitario
        float subtotal = precio * cantidad;
        int cant = Integer.parseInt(this.lbl_etiqueta_4.getText());
        // agregar la informacion a la tabla de la factura 
        if (cant >= cantidad){
             DefaultTableModel dtm = (DefaultTableModel) this.jtbl_ventas.getModel();
       Object[] obj = new Object[6];
       obj[0]= idVenta;
       obj[1]= nombreVenta;
       obj[2]= tipoVenta;
       obj[3]= cantidad;
       obj[4]= precio;
       obj[5]=subtotal;
       
       dtm.addRow(obj);
       this.jtbl_ventas.setModel(dtm);
       total = total + subtotal;
       this.jtf_vetas_total.setText(""+total);
       this.btn_ventas_agregar.setEnabled(false); 
       if ((cant - cantidad) <= 5){
           JOptionPane.showMessageDialog(null, "El producto '"+nombreVenta+"' esta escaso, Queda: "+ (cant - cantidad)+ 
                   " unidades.");
       }
        }else{
            JOptionPane.showMessageDialog(null, "La cantidad en mayor a las existencias"); 
        }
      
       }catch(Exception ex){
           JOptionPane.showMessageDialog(null, ex.getMessage());
       }       
       
    }//GEN-LAST:event_btn_ventas_agregarActionPerformed

    private void btn_ventas_generarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_ventas_generarActionPerformed
       int id = Integer.parseInt(this.idfac.getText());
            int filas = this.jtbl_ventas.getRowCount();
        if ( filas > 0 ){
           boolean a = guardadRegistroFactura();
        boolean b = guardaRegistrosProductosFac(); 
        boolean c = GuardarImprimir();
        if (a == true & b == true & c== true){
        int cant = Integer.parseInt(this.lbl_etiqueta_4.getText());
        DescuentoProductosInventario(cant); 
        Imprimir();
        JOptionPane.showMessageDialog(null, "Venta Generada Exitosamente");
        DefaultTableModel dtm =(DefaultTableModel)this.jtbl_ventas.getModel();
        // eliminar los datos que exinten en el modelo de la tabla antes de agregar los datos 
        if (dtm.getRowCount() != 0){
            int d = dtm.getRowCount();
        for (int y = 0; y < d; y++){
            dtm.removeRow(0);
           }
        }
           this.lbl_etiqueta_1.setText("");
               this.lbl_etiqueta_2.setText("");
               this.lbl_etiqueta_3.setText("");
               this.lbl_etiqueta_4.setText("");
               this.lbl_etiqueta_5.setText("");
               this.lbl_etiqueta_6.setText("");
               this.lbl_etiqueta_7.setText("");
               this.jtf_ventas_buscar.setText("");
               this.jtf_ventas_cantidad.setText("");
               this.jtf_vetas_total.setText("");
               this.frame_ventas.setVisible(true);
        btn_nuevaventa_ventasActionPerformed(null);
        }else{
             try{
           Statement st = conexion.createStatement();
                 try {
                     st.execute("WHERE DELETE FROM `imprecion` WHERE `idfac` = "+id);
                 } catch (SQLException e) {
                 }
                 try {
                    st.execute("WHERE DELETE FROM `registroprocfacturas` WHERE `idfac` = "+id); 
                 } catch (SQLException e) {
                 }
                 try {
                      st.execute("WHERE DELETE FROM `registroventasfac` WHERE `idfac` = "+id);
                 } catch (SQLException e) {
                 }
           JOptionPane.showMessageDialog(null, "La Venta no pudo ser Realizada", "Error", JOptionPane.ERROR_MESSAGE);
         }catch(SQLException ex){
            JOptionPane.showMessageDialog(null, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
       }        
        }
        }else{
            JOptionPane.showMessageDialog(null, "Por favor agrege un producto a al factura", "Error en venta", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btn_ventas_generarActionPerformed

    private void btn_eliminar_inventarioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_eliminar_inventarioActionPerformed
        OpcionesInventario("Eliminar");
    }//GEN-LAST:event_btn_eliminar_inventarioActionPerformed

    private void btn_editar_inventarioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_editar_inventarioActionPerformed
        OpcionesInventario("Editar");
    }//GEN-LAST:event_btn_editar_inventarioActionPerformed

    private void jtf_inventario_etiqueta_8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jtf_inventario_etiqueta_8ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jtf_inventario_etiqueta_8ActionPerformed

    private void bnt_opciones_inventarioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bnt_opciones_inventarioActionPerformed
    // generar id
        int id = Integer.parseInt(this.jtf_inventario_etiqueta_8.getText());
    // Escoger la  accion que se realizar dependiendo de la varioble operacion
        switch(operacion){
            case "Editar":
                OperacionEditarInventario(id);
                break;
            case "Eliminar":
                 String sql = "DELETE FROM `inventario` WHERE `id` = "+id+";";
                 int dec = JOptionPane.showConfirmDialog(null, "Desea Eliminar el producto seleccionado",
                         "Confirmar Eliminacion", JOptionPane.OK_CANCEL_OPTION);
                 switch(dec){
                            case 0:
                                 try{
                                     Statement st = conexion.createStatement();
                                    st.execute(sql);
                                     JOptionPane.showMessageDialog(null, "El producto a sido eliminado");
                                     LimpiarPanelOpcionesInventario();
                                    InavilitarPanelOpcionesInventario();
                                    }catch(SQLException ex){
                                        JOptionPane.showMessageDialog(null, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                                       }   
                                break;
                            case 2:
                                JOptionPane.showMessageDialog(null, "Eliminacion Cancelada");
                                 LimpiarPanelOpcionesInventario();
                                 InavilitarPanelOpcionesInventario();
                                break;
                        }
                       
                break;
            case "Agregar":
                OperacionAgregarInventario(id);
                break;
        }
        
        btn_actulizar_inventarioActionPerformed(null);
        
    }//GEN-LAST:event_bnt_opciones_inventarioActionPerformed

    private void btn_agregar_inventarioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_agregar_inventarioActionPerformed
        btn_editar_inventarioActionPerformed(null);
        // Defino la operacion a relaizar
        operacion = "Agregar";
        // limpiar los campos 
        LimpiarPanelOpcionesInventario();
        
        // cambiar el texto del boton de operaciones
        this.bnt_opciones_inventario.setText(operacion);
        // id editable para agregar un nuevo producto
        this.jtf_inventario_etiqueta_8.setEditable(true);
        // avilitar los campos de texto
        this.jtf_inventario_etiqueta_8.setEnabled(true);
        this.jtf_inventario_etiqueta_9.setEnabled(true);
        this.jtf_inventario_etiqueta_10.setEnabled(true);
        this.jtf_inventario_etiqueta_11.setEnabled(true);
        this.jtf_inventario_etiqueta_12.setEnabled(true);
        this.jtf_inventario_etiqueta_13.setEnabled(true);
        this.jtf_inventario_etiqueta_14.setEnabled(true);
        // activar el boton de operaciones
        this.bnt_opciones_inventario.setEnabled(true);
        try{
                            Statement st = conexion.createStatement();
                            ResultSet rs = st.executeQuery("SELECT MAX(`id`) FROM `inventario`");
                            while(rs.next()){
                                int i = Integer.parseInt(rs.getString("MAX(`id`)"));
                                int id = i + 1;
                            this.jtf_inventario_etiqueta_8.setText("" +id);
                            }
                            
                            
                        }catch(SQLException ex){
                            JOptionPane.showMessageDialog(null, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                        }
    }//GEN-LAST:event_btn_agregar_inventarioActionPerformed

    private void btn_actulizar_inventarioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_actulizar_inventarioActionPerformed
        consultas();
    }//GEN-LAST:event_btn_actulizar_inventarioActionPerformed

    private void estadoConeccionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_estadoConeccionActionPerformed
                  
        this.frame_configuracion.pack();
        this.frame_configuracion.setResizable(false);
        this.frame_configuracion.setLocationRelativeTo(null);
        this.frame_configuracion.setVisible(true);
    }//GEN-LAST:event_estadoConeccionActionPerformed

    private void CreconectarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CreconectarActionPerformed
        try{
            String db = (String) this.jcb_basededats_configuracion.getSelectedItem();
            String servidor = "jdbc:mysql://127.0.0.1/"+db;
            String us = (String) this.Cuser.getText();
            String pas = (String) this.Cpasword.getText();
            conexion = DriverManager.getConnection(servidor, us, pas);

            estadoConexion(true, us, pas , db);
            this.Cestado.setText("Conectado");
        }catch(SQLException ex){
            JOptionPane.showMessageDialog(null, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            this.Cestado.setText("Desconectado");
        }
         this.Cpasword.setText("");
    }//GEN-LAST:event_CreconectarActionPerformed

    private void ConfiguracionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ConfiguracionActionPerformed
        this.frame_configuracion.pack();
        this.frame_configuracion.setResizable(false);
        this.frame_configuracion.setLocationRelativeTo(null);
        this.frame_configuracion.setVisible(true);
    }//GEN-LAST:event_ConfiguracionActionPerformed

    private void btn_nuevaventa_ventasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_nuevaventa_ventasActionPerformed
         SalirVentas(null);
         btn_ventasActionPerformed(null);
    }//GEN-LAST:event_btn_nuevaventa_ventasActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        DefaultTableModel model = (DefaultTableModel) jtbl_ventas.getModel();
        model.removeRow(jtbl_ventas.getSelectedRow());
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        ProgramaUser = this.jtf_usuario_configuracion.getText();
        ProgramaContraseña = this.jpf_contraseña_configuracion.getText();
       boolean op =  GetConnection(ProgramaUser, ProgramaContraseña);
       if(op=true){
           int oper;
           JOptionPane.showMessageDialog(null, "Conectado");
            oper = JOptionPane.showConfirmDialog(null, "¿Salir de configuracion?", "Salir", JOptionPane.YES_NO_OPTION);
            switch(oper){
                case 0:
                    this.frame_configuracion.dispose();
                    break;
                case 2: 
                    JOptionPane.showConfirmDialog(null, "2");
                    break;
            }
       }
    }//GEN-LAST:event_jButton4ActionPerformed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        ProgramaUser = this.jtf_usuario_configuracion1.getText();
        ProgramaContraseña = this.jpf_contraseña_configuracion1.getText();
        boolean op = GetConnection(ProgramaUser, ProgramaContraseña);
        
        if (op == true ){
            this.setEnabled(true);
             this.IniciarSesion.hide();
             this.setVisible(true);
        }else{
            System.exit(0);
        }
    }//GEN-LAST:event_jButton5ActionPerformed

    private void SalirVentas(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_SalirVentas
         DefaultTableModel dtm =(DefaultTableModel)this.jtbl_ventas.getModel();
        // eliminar los datos que exinten en el modelo de la tabla antes de agregar los datos 
        if (dtm.getRowCount() != 0){
            int d = dtm.getRowCount();
        for (int y = 0; y < d; y++){
            dtm.removeRow(0);
           }
        }
           this.lbl_etiqueta_1.setText("");
               this.lbl_etiqueta_2.setText("");
               this.lbl_etiqueta_3.setText("");
               this.lbl_etiqueta_4.setText("");
               this.lbl_etiqueta_5.setText("");
               this.lbl_etiqueta_6.setText("");
               this.lbl_etiqueta_7.setText("");
               this.setEnabled(true);
        this.setVisible(true);
    }//GEN-LAST:event_SalirVentas

    private void btn_cancelar_ventasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_cancelar_ventasActionPerformed
      this.frame_ventas.dispose();
    }//GEN-LAST:event_btn_cancelar_ventasActionPerformed

    private void SalirInventario(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_SalirInventario
                   lbl_inventario_etiqueta_8.setText("");
                   lbl_inventario_etiqueta_9.setText("");
                   lbl_inventario_etiqueta_10.setText("");
                   lbl_inventario_etiqueta_11.setText("");
                   lbl_inventario_etiqueta_12.setText("");
                   lbl_inventario_etiqueta_13.setText("");
                   lbl_inventario_etiqueta_14.setText("");
                   this.jtf_inventario_etiqueta_8.setText("");
        this.jtf_inventario_etiqueta_9.setText("");
        this.jtf_inventario_etiqueta_10.setText("");
        this.jtf_inventario_etiqueta_11.setText("");
        this.jtf_inventario_etiqueta_12.setText("");
        this.jtf_inventario_etiqueta_13.setText("");
        this.jtf_inventario_etiqueta_14.setText("");
                   
                   DefaultTableModel dtm =(DefaultTableModel)this.jtb_inventario_tablazapatos.getModel();
        // eliminar los datos que exinten en el modelo de la tabla antes de agregar los datos 
        if (dtm.getRowCount() != 0){
            int d = dtm.getRowCount();
        for (int y = 0; y < d; y++){
            dtm.removeRow(0);
           }
        }
        this.setEnabled(true);
        this.setVisible(true);
    }//GEN-LAST:event_SalirInventario

    private void jtf_inventario_etiqueta_14KeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtf_inventario_etiqueta_14KeyTyped
          char car = evt.getKeyChar();
        if(((car < '0') || (car > '9')) && (car != KeyEvent.VK_BACKSPACE)&& (car !='.')){
               evt.consume();
                }
        
        if (car =='.' && this.jtf_inventario_etiqueta_14.getText().contains(".")){
            evt.consume();
        }
    }//GEN-LAST:event_jtf_inventario_etiqueta_14KeyTyped

    private void jtf_inventario_etiqueta_11KeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtf_inventario_etiqueta_11KeyTyped
        char car = evt.getKeyChar();
        if((car<'0' || car>'9')) evt.consume();
    }//GEN-LAST:event_jtf_inventario_etiqueta_11KeyTyped

    private void jtf_inventario_etiqueta_13KeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtf_inventario_etiqueta_13KeyTyped
        char car = evt.getKeyChar();
        if((car<'0' || car>'9')) evt.consume();
    }//GEN-LAST:event_jtf_inventario_etiqueta_13KeyTyped

    private void jtf_inventario_etiqueta_8KeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtf_inventario_etiqueta_8KeyTyped
         char car = evt.getKeyChar();
        if((car<'0' || car>'9')) evt.consume();
    }//GEN-LAST:event_jtf_inventario_etiqueta_8KeyTyped

    private void jtf_ventas_cantidadKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtf_ventas_cantidadKeyTyped
         char car = evt.getKeyChar();
            if((car<'0' || car>'9'))evt.consume();
    }//GEN-LAST:event_jtf_ventas_cantidadKeyTyped

    private void AbrirConfiguracion(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_AbrirConfiguracion
       this.setEnabled(false);
    }//GEN-LAST:event_AbrirConfiguracion

    private void AbrirInventario(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_AbrirInventario
        this.setEnabled(false);
    }//GEN-LAST:event_AbrirInventario

    private void AbrirVentas(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_AbrirVentas
        this.setEnabled(false);
    }//GEN-LAST:event_AbrirVentas

    private void SalirConfiguracion(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_SalirConfiguracion
        this.setEnabled(true);
        this.setVisible(true);
    }//GEN-LAST:event_SalirConfiguracion

    private void jMenuItem3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem3ActionPerformed
         // llamar el frame del inventario
        this.frame_inventario.pack(); // que el tamaño del frame sea el de los componentes
        this.frame_inventario.setVisible(true);// hacer la ventana visible
        this.frame_inventario.setLocationRelativeTo(null);// paracece en el medio
        this.frame_inventario.setResizable(false);
        // llenar la tabla del inventario de los zapatos
        consultas();
    }//GEN-LAST:event_jMenuItem3ActionPerformed

    private void Acercade_barraActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Acercade_barraActionPerformed
      this.Acercade.pack(); // que el tamaño del frame sea el de los componentes
        this.Acercade.setVisible(true);// hacer la ventana visible
        this.Acercade.setLocationRelativeTo(null);// paracece en el medio
        this.Acercade.setResizable(false);
    }//GEN-LAST:event_Acercade_barraActionPerformed

    private void youtubeMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_youtubeMouseClicked

        String url = "https://www.youtube.com/user/TCilogmz";
        try {
            Desktop.getDesktop().browse(new URI(url));
        } catch (URISyntaxException ex) {
            Logger.getLogger(principal.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(principal.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_youtubeMouseClicked

    private void youtubeMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_youtubeMouseEntered
       colorEtiquetasEntrar(this.youtube);
    }//GEN-LAST:event_youtubeMouseEntered

    private void instagranMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_instagranMouseClicked
        String url = "http://instagram.com/cilogmz";
        try {
            Desktop.getDesktop().browse(new URI(url));
        } catch (URISyntaxException ex) {
            Logger.getLogger(principal.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(principal.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_instagranMouseClicked

    private void googleMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_googleMouseClicked
        String url = "https://plus.google.com/u/0/111638749681174853477";
        try {
            Desktop.getDesktop().browse(new URI(url));
        } catch (URISyntaxException ex) {
            Logger.getLogger(principal.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(principal.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_googleMouseClicked

    private void facebookMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_facebookMouseClicked
        String url = "https://www.facebook.com/Cilogmz2";
        try {
            Desktop.getDesktop().browse(new URI(url));
        } catch (URISyntaxException ex) {
            Logger.getLogger(principal.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(principal.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_facebookMouseClicked

    private void twitterMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_twitterMouseClicked
        String url = "https://twitter.com/Cilogmz";
        try {
            Desktop.getDesktop().browse(new URI(url));
        } catch (URISyntaxException ex) {
            Logger.getLogger(principal.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(principal.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_twitterMouseClicked

    private void AcercadecerrarA(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_AcercadecerrarA
        this.enable(true);
        this.setVisible(true);
    }//GEN-LAST:event_AcercadecerrarA

    private void youtubeMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_youtubeMouseExited
        colorEtiquetasSalir(this.youtube);
    }//GEN-LAST:event_youtubeMouseExited

    private void googleMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_googleMouseEntered
        colorEtiquetasEntrar(this.google);
    }//GEN-LAST:event_googleMouseEntered

    private void instagranMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_instagranMouseEntered
        colorEtiquetasEntrar(this.instagran);
    }//GEN-LAST:event_instagranMouseEntered

    private void facebookMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_facebookMouseEntered
        colorEtiquetasEntrar(this.facebook);
    }//GEN-LAST:event_facebookMouseEntered

    private void twitterMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_twitterMouseEntered
       colorEtiquetasEntrar(this.twitter);
    }//GEN-LAST:event_twitterMouseEntered

    private void googleMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_googleMouseExited
         colorEtiquetasSalir(this.google);
    }//GEN-LAST:event_googleMouseExited

    private void instagranMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_instagranMouseExited
         colorEtiquetasSalir(this.instagran);
    }//GEN-LAST:event_instagranMouseExited

    private void facebookMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_facebookMouseExited
         colorEtiquetasSalir(this.facebook);
    }//GEN-LAST:event_facebookMouseExited

    private void twitterMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_twitterMouseExited
         colorEtiquetasSalir(this.twitter);
    }//GEN-LAST:event_twitterMouseExited

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        ModuloContable modulo = new ModuloContable(this);
        modulo.setVisible(true);
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jtf_usuario_configuracion1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jtf_usuario_configuracion1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jtf_usuario_configuracion1ActionPerformed

    public void colorEtiquetasEntrar(JLabel lbl){
        Color c = new Color(134, 255, 63);
        lbl.setBackground(c);
    }
       public void colorEtiquetasSalir(JLabel lbl){
        Color c = new Color(240, 240, 240);
        lbl.setBackground(c);
    }
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(principal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(principal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(principal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(principal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
//        java.awt.EventQueue.invokeLater(new Runnable() {
//            public void run() {
//                new principal().setVisible(true);
//            }
//        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JFrame Acercade;
    private javax.swing.JMenuItem Acercade_barra;
    private com.toedter.calendar.JCalendar Calendario;
    private javax.swing.JLabel Cestado;
    private javax.swing.JMenuItem Configuracion;
    private javax.swing.JPasswordField Cpasword;
    private javax.swing.JButton Creconectar;
    private javax.swing.JTextField Cuser;
    public javax.swing.JFrame IniciarSesion;
    private javax.swing.JTabbedPane basededatos;
    private javax.swing.JButton bnt_opciones_inventario;
    private javax.swing.JButton btn_actulizar_inventario;
    private javax.swing.JButton btn_agregar_inventario;
    private javax.swing.JButton btn_cancelar_ventas;
    private javax.swing.JButton btn_editar_inventario;
    private javax.swing.JButton btn_eliminar_inventario;
    private javax.swing.JButton btn_inventario;
    private javax.swing.JButton btn_nuevaventa_ventas;
    private javax.swing.JButton btn_ventas;
    private javax.swing.JButton btn_ventas_agregar;
    private javax.swing.JButton btn_ventas_buscar;
    private javax.swing.JButton btn_ventas_generar;
    private javax.swing.JPanel cambiaDb;
    private javax.swing.JLabel dbConectada;
    private javax.swing.JButton estadoConeccion;
    private javax.swing.JLabel facebook;
    private javax.swing.JLabel fecha;
    private javax.swing.JFrame frame_configuracion;
    private javax.swing.JFrame frame_inventario;
    private javax.swing.JFrame frame_ventas;
    private javax.swing.JLabel google;
    private javax.swing.JTextField idfac;
    private javax.swing.JLabel instagran;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton4;
    public javax.swing.JButton jButton5;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenu jMenu3;
    private javax.swing.JMenu jMenu5;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel13;
    private javax.swing.JPanel jPanel14;
    private javax.swing.JPanel jPanel15;
    private javax.swing.JPanel jPanel16;
    private javax.swing.JPanel jPanel17;
    private javax.swing.JPanel jPanel18;
    private javax.swing.JPanel jPanel19;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel20;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JPopupMenu.Separator jSeparator1;
    private javax.swing.JPopupMenu.Separator jSeparator2;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTabbedPane jTabbedPane2;
    private javax.swing.JTabbedPane jTabbedPane4;
    private javax.swing.JTabbedPane jTabbedPane5;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JComboBox jcb_basededats_configuracion;
    private javax.swing.JComboBox jcb_ventas_tipodebusqueda;
    private javax.swing.JPasswordField jpf_contraseña_configuracion;
    public javax.swing.JPasswordField jpf_contraseña_configuracion1;
    private javax.swing.JTable jtb_inventario_tablazapatos;
    private javax.swing.JTable jtbl_ventas;
    private javax.swing.JTextField jtf_inventario_etiqueta_10;
    private javax.swing.JTextField jtf_inventario_etiqueta_11;
    private javax.swing.JTextField jtf_inventario_etiqueta_12;
    private javax.swing.JTextField jtf_inventario_etiqueta_13;
    private javax.swing.JTextField jtf_inventario_etiqueta_14;
    private javax.swing.JTextField jtf_inventario_etiqueta_8;
    private javax.swing.JTextField jtf_inventario_etiqueta_9;
    private javax.swing.JTextField jtf_usuario_configuracion;
    public javax.swing.JTextField jtf_usuario_configuracion1;
    private javax.swing.JTextField jtf_ventas_buscar;
    private javax.swing.JTextField jtf_ventas_cantidad;
    private javax.swing.JTextField jtf_vetas_total;
    private javax.swing.JLabel lbl_etiqueta_1;
    private javax.swing.JLabel lbl_etiqueta_2;
    private javax.swing.JLabel lbl_etiqueta_3;
    private javax.swing.JLabel lbl_etiqueta_4;
    private javax.swing.JLabel lbl_etiqueta_5;
    private javax.swing.JLabel lbl_etiqueta_6;
    private javax.swing.JLabel lbl_etiqueta_7;
    private javax.swing.JLabel lbl_etiqueta_cantidad;
    private javax.swing.JLabel lbl_etiqueta_cantidadavender;
    private javax.swing.JLabel lbl_etiqueta_color;
    private javax.swing.JLabel lbl_etiqueta_id;
    private javax.swing.JLabel lbl_etiqueta_nombre;
    private javax.swing.JLabel lbl_etiqueta_precio;
    private javax.swing.JLabel lbl_etiqueta_talla;
    private javax.swing.JLabel lbl_etiqueta_tipo;
    private javax.swing.JLabel lbl_inventario;
    private javax.swing.JLabel lbl_inventario_etiqueta_10;
    private javax.swing.JLabel lbl_inventario_etiqueta_11;
    private javax.swing.JLabel lbl_inventario_etiqueta_12;
    private javax.swing.JLabel lbl_inventario_etiqueta_13;
    private javax.swing.JLabel lbl_inventario_etiqueta_14;
    private javax.swing.JLabel lbl_inventario_etiqueta_8;
    private javax.swing.JLabel lbl_inventario_etiqueta_9;
    private javax.swing.JLabel lbl_inventario_etiqueta_cantidad1;
    private javax.swing.JLabel lbl_inventario_etiqueta_color1;
    private javax.swing.JLabel lbl_inventario_etiqueta_id2;
    private javax.swing.JLabel lbl_inventario_etiqueta_nombre1;
    private javax.swing.JLabel lbl_inventario_etiqueta_precio1;
    private javax.swing.JLabel lbl_inventario_etiqueta_talla1;
    private javax.swing.JLabel lbl_inventario_etiqueta_tipo1;
    private javax.swing.JLabel lbl_ventas;
    private javax.swing.JMenuBar menubar_principal;
    private javax.swing.JMenuBar menubar_ventas1;
    private javax.swing.JLabel reloj;
    private javax.swing.JLabel reloj2;
    private javax.swing.JLabel twitter;
    private javax.swing.JLabel vendedor;
    private javax.swing.JLabel youtube;
    // End of variables declaration//GEN-END:variables
}
