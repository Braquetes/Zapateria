

package zapateria;

import com.mxrck.autocompleter.TextAutoCompleter;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import javax.swing.JFrame;
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
import static zapateria.principal.conexion;

public class ModuloContable extends javax.swing.JFrame {
  float total = 0;
             String vendedor = "";
             String fecha = null;
             String hora;
       JFrame d;
    
    public ModuloContable( JFrame x) {
        d = x;
        initComponents();
        Date fachaActual = new Date();
        this.jDateChooser1.setMaxSelectableDate(fachaActual);
         this.jDateChooser1.setDate(fachaActual);
        AutoCompletar(this.opcion.getSelectedIndex());
         selecionTabla();
         todas();
         setTitle("Contabiliada ");
         setLocationRelativeTo(null);
         setResizable(false);
         d.setEnabled(false);
    }
        public void selecionTabla(){
        // agregar metodeo de escucha a la tabla 
       jtb_facturas.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

           @Override
           public void valueChanged(ListSelectionEvent e) {
               if (jtb_facturas.getSelectedRow()!= -1) {
                   int fila  = jtb_facturas.getSelectedRow();
                   // colocar los datos de la fila selecionada en las etiquetas correspondientes
                   id_etiqueta.setText(jtb_facturas.getValueAt(fila, 0).toString());
                   cantidad_etiqueta.setText(jtb_facturas.getValueAt(fila, 1).toString());
                   precio_etiqueta.setText(jtb_facturas.getValueAt(fila, 2).toString());
                   vendedor_etiqueta.setText(jtb_facturas.getValueAt(fila, 3).toString());
                   fecha_etiqueta.setText(jtb_facturas.getValueAt(fila, 4).toString());
                   hora_etiqueta.setText(jtb_facturas.getValueAt(fila, 5).toString());
               }
           }
       });
}
        
     public void todas(){
          try{
//        GetConnection();
        Statement st = conexion.createStatement();
        ResultSet rs;
           
        rs = st.executeQuery("SELECT `idfac`, `cantidadpro`, `totalventa`, "
            + "`vendedor`, `fecha`, `hora` FROM `registroventasfac` WHERE 1");
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
        DefaultTableModel dtm =(DefaultTableModel)this.jtb_facturas.getModel();
        // eliminar los datos que exinten en el modelo de la tabla antes de agregar los datos 
        if (dtm.getRowCount() != 0){
            int d = dtm.getRowCount();
        for (int y = 0; y < d; y++){
            dtm.removeRow(0);
           }
        }
        this.jtb_facturas.setModel(dtm);
        // agregar los datos de la consulta a la tabla 
        for (int i = 0; i <data.size(); i++) {
             dtm.addRow(data.get(i));
            }   
     }catch(SQLException ex){
        JOptionPane.showMessageDialog(null,ex.getMessage(), "¡ERROR!", JOptionPane.ERROR_MESSAGE);
     }
     }
        
         public boolean GuardarImprimir(){
           boolean est= false;
          int filas = this.jtb_productos_facturas.getRowCount();
         Object[][] valores = new Object[100][6];
         for ( int f = 0 ; f < filas; f++){
             for (int c = 0 ; c < 6; c++){

               valores[f][c] = this.jtb_productos_facturas.getValueAt(f, c).toString();
             }
         }
       
         int id = Integer.parseInt(valores[0][0].toString());
           try{
           Statement st = conexion.createStatement();
           st.execute("DELETE FROM `imprecion` WHERE 1");
           ResultSet rs = st.executeQuery("SELECT `totalventa`, `vendedor`, `fh` FROM "
           + "`registroventasfac` WHERE `idfac` = "+ id);

          while (rs.next()){
              total = Float.parseFloat(rs.getString(1));
              vendedor= rs.getString(2);
              fecha = rs.getString(3);
            
          }
//           
         }catch(SQLException ex){
            JOptionPane.showMessageDialog(null, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
       }   
         for ( int h = 0 ; h < filas ; h++){
            String sql = "INSERT INTO `imprecion`(`idfac`, `idpro`, `nombre`, `cantidad`, `subtotal`, `fecha`, `Total`,"
                    + " `vendedor`, `PreUnid`) VALUES ("+id+","
                    +Integer.parseInt(valores[h][1].toString())
                    +",'"+valores[h][2].toString()+"',"+Integer.parseInt(valores[h][3].toString())
                    +","+Float.parseFloat(valores[h][5].toString())+",'"+fecha+"',"
                    + "'"+total+"', '"+vendedor+"',"+Float.parseFloat(valores[h][4].toString())+")";

             try{
           Statement st = conexion.createStatement();
   
           st.execute(sql);
           est = true;
         }catch(SQLException ex){
            JOptionPane.showMessageDialog(null, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            est= false;
             try{
           Statement st = conexion.createStatement();
           st.execute("DELETE FROM `imprecion` WHERE 1");
           }catch(SQLException e){}  
       }        
         }
       return est;
    }
         public void Imprimir(){
        JasperReport reporte;
     JasperPrint reporte_view;     
     try{
         //direccion del archivo JASPER
          URL  in = this.getClass().getResource("factura.jasper");
          reporte = (JasperReport) JRLoader.loadObject( in );           
          reporte_view= JasperFillManager.fillReport( reporte, new HashMap(), conexion );
          JasperViewer.viewReport(reporte_view, false);
          //terminamos la conexion a la base de datos
          
   }catch (JRException E){
     E.printStackTrace();
          }
    }
 public void AutoCompletar (int op){
        // crear la bariable acutocompletar
         TextAutoCompleter ac = new TextAutoCompleter(jtf_buscar1);
        //  abrab dos casos posibles: 1- por id del zapato 2- por nombre del zapato
         switch(op){
             // Caso por "idfac"
             case 0:
                  try{
                            Statement st = conexion.createStatement();
                            ResultSet rs = st.executeQuery("select idfac from registroventasfac");
                            ac.removeAll();
                            
                            while(rs.next()){
                                ac.addItem(rs.getString("idfac"));
                            }
                        }catch(SQLException ex){
                            JOptionPane.showMessageDialog(null, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                        }
                 break;
             // Caso por "vendedor"    
             case 1:
                 String vendedor = "vacio";
                 
                 try{
                            Statement st = conexion.createStatement();
                            ResultSet rs = st.executeQuery("select vendedor from registroventasfac");
                            ac.removeAll();
                          
                            while(rs.next()){ 
                                
                                if (vendedor.equals(rs.getString("vendedor"))){
                                   
                                }else{
                                    ac.addItem(rs.getString("vendedor")); 
                                    vendedor = rs.getString("vendedor");
                                }
                               
                            }
                        }catch(SQLException ex){
                            JOptionPane.showMessageDialog(null, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                        }
                 break;
         }
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel2 = new javax.swing.JPanel();
        opcion = new javax.swing.JComboBox();
        jtf_buscar1 = new javax.swing.JTextField();
        btn_buscar1 = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        id_etiqueta = new javax.swing.JLabel();
        cantidad_etiqueta = new javax.swing.JLabel();
        precio_etiqueta = new javax.swing.JLabel();
        vendedor_etiqueta = new javax.swing.JLabel();
        fecha_etiqueta = new javax.swing.JLabel();
        hora_etiqueta = new javax.swing.JLabel();
        btn_imprimir = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jtb_facturas = new javax.swing.JTable();
        btn_cargar = new javax.swing.JButton();
        btn_buscar2 = new javax.swing.JButton();
        jScrollPane3 = new javax.swing.JScrollPane();
        jtb_productos_facturas = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosed(java.awt.event.WindowEvent evt) {
                Salir(evt);
            }
        });

        opcion.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "IDFAC", "Vendedor" }));
        opcion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                opcionActionPerformed(evt);
            }
        });

        btn_buscar1.setText(" buscar");
        btn_buscar1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_buscar1ActionPerformed(evt);
            }
        });

        jLabel2.setText("ID");

        jLabel3.setText("CANTIDAD");

        jLabel4.setText("PRECIO");

        jLabel5.setText("VENDEDOR");

        jLabel6.setText("FECHA");

        jLabel7.setText("HORA");

        id_etiqueta.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        cantidad_etiqueta.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        precio_etiqueta.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        vendedor_etiqueta.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        fecha_etiqueta.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        hora_etiqueta.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        btn_imprimir.setText("IMPRIMIR");
        btn_imprimir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_imprimirActionPerformed(evt);
            }
        });

        jtb_facturas.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID", "CANTIDAD", "PRECIO", "VENDEDOR", "FECHA", "HORA"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.Integer.class, java.lang.Float.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, true, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(jtb_facturas);

        btn_cargar.setText("CARGAR");
        btn_cargar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_cargarActionPerformed(evt);
            }
        });

        btn_buscar2.setText(" buscar");
        btn_buscar2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_buscar2ActionPerformed(evt);
            }
        });

        jtb_productos_facturas.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID", "ID Producto ", "Nombre", "Cantidad", "Precio", "SubTotal"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.Integer.class, java.lang.String.class, java.lang.Integer.class, java.lang.Float.class, java.lang.Float.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        jScrollPane3.setViewportView(jtb_productos_facturas);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btn_cargar, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btn_imprimir, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(23, 23, 23))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(opcion, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jtf_buscar1, javax.swing.GroupLayout.PREFERRED_SIZE, 116, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(btn_buscar1, javax.swing.GroupLayout.PREFERRED_SIZE, 81, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(163, 163, 163)
                                .addComponent(btn_buscar2, javax.swing.GroupLayout.PREFERRED_SIZE, 81, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 4, Short.MAX_VALUE))
                            .addComponent(jScrollPane1)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel2)
                                    .addComponent(id_etiqueta, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(18, 18, 18)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel3)
                                    .addComponent(cantidad_etiqueta, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel2Layout.createSequentialGroup()
                                        .addGap(39, 39, 39)
                                        .addComponent(jLabel4)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(precio_etiqueta, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(18, 18, 18)))
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel5)
                                    .addComponent(vendedor_etiqueta, javax.swing.GroupLayout.PREFERRED_SIZE, 97, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(18, 18, 18)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(fecha_etiqueta, javax.swing.GroupLayout.PREFERRED_SIZE, 96, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel6))
                                .addGap(33, 33, 33)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(hora_etiqueta, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addComponent(jScrollPane3, javax.swing.GroupLayout.Alignment.TRAILING))
                        .addContainerGap())))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(opcion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jtf_buscar1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btn_buscar1))
                    .addComponent(btn_buscar2))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jLabel3)
                    .addComponent(jLabel4)
                    .addComponent(jLabel5)
                    .addComponent(jLabel6)
                    .addComponent(jLabel7))
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(vendedor_etiqueta, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(id_etiqueta, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(cantidad_etiqueta, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(fecha_etiqueta, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(hora_etiqueta, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(precio_etiqueta, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(18, 18, 18)
                .addComponent(btn_cargar)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btn_imprimir)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Bucar Facturas Anteriores", jPanel2);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jTabbedPane1)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jTabbedPane1)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btn_buscar2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_buscar2ActionPerformed
        Date cd = jDateChooser1.getDate();
        SimpleDateFormat fechas = new SimpleDateFormat("yyyy-MM-dd");
        String fechah = fechas.format(cd);
        
         try{
//        GetConnection();
        Statement st = conexion.createStatement();
        ResultSet rs;
           
        rs = st.executeQuery("SELECT `idfac`, `cantidadpro`, `totalventa`, "
            + "`vendedor`, `fecha`, `hora` FROM `registroventasfac` WHERE `fecha` = '"+fechah+"'");
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
        DefaultTableModel dtm =(DefaultTableModel)this.jtb_facturas.getModel();
        // eliminar los datos que exinten en el modelo de la tabla antes de agregar los datos 
        if (dtm.getRowCount() != 0){
            int d = dtm.getRowCount();
        for (int y = 0; y < d; y++){
            dtm.removeRow(0);
           }
        }
        this.jtb_facturas.setModel(dtm);
        // agregar los datos de la consulta a la tabla 
        for (int i = 0; i <data.size(); i++) {
             dtm.addRow(data.get(i));
            }   
     }catch(SQLException ex){
        JOptionPane.showMessageDialog(null,ex.getMessage(), "¡ERROR!", JOptionPane.ERROR_MESSAGE);
     }
       
    }//GEN-LAST:event_btn_buscar2ActionPerformed

    private void btn_buscar1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_buscar1ActionPerformed
         // generar el texto que emos escrito en el jtextfil para ser buscado
        String textoBuscar = (String) this.jtf_buscar1.getText();
        // generar el texto del item selecionado en combo box de tipo de busqueda
        String t = (String) this.opcion.getSelectedItem();
        
        // convertir el texto anterior en minusculas solamente
        String tipo = t.toLowerCase();
        // realizar la busqueda de los datos segun el tipo y el dato ingersado
        try{
//        GetConnection();
        Statement st = conexion.createStatement();
        ResultSet rs;
           
        rs = st.executeQuery("SELECT `idfac`, `cantidadpro`, `totalventa`, "
            + "`vendedor`, `fecha`, `hora` FROM `registroventasfac` WHERE `"+tipo+"` = '"+textoBuscar+"'");
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
        DefaultTableModel dtm =(DefaultTableModel)this.jtb_facturas.getModel();
        // eliminar los datos que exinten en el modelo de la tabla antes de agregar los datos 
        if (dtm.getRowCount() != 0){
            int d = dtm.getRowCount();
        for (int y = 0; y < d; y++){
            dtm.removeRow(0);
           }
        }
        this.jtb_facturas.setModel(dtm);
        // agregar los datos de la consulta a la tabla 
        for (int i = 0; i <data.size(); i++) {
             dtm.addRow(data.get(i));
            }   
     }catch(SQLException ex){
        JOptionPane.showMessageDialog(null,ex.getMessage(), "¡ERROR!", JOptionPane.ERROR_MESSAGE);
     }
    }//GEN-LAST:event_btn_buscar1ActionPerformed

    private void btn_cargarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_cargarActionPerformed
        
         String textoBuscar = (String) this.id_etiqueta.getText();
        
        try{
//        GetConnection();
        Statement st = conexion.createStatement();
        ResultSet rs;
       
           
        rs = st.executeQuery("SELECT `idfac`, `idpro`, `nombre`, `cantidad`, `precio`, `subtotal` FROM `registroprocfacturas`"
                + " WHERE `idfac` = '"+textoBuscar+"'");
       
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
        DefaultTableModel dtm =(DefaultTableModel)this.jtb_productos_facturas.getModel();
        // eliminar los datos que exinten en el modelo de la tabla antes de agregar los datos 
        if (dtm.getRowCount() != 0){
            int d = dtm.getRowCount();
        for (int y = 0; y < d; y++){
            dtm.removeRow(0);
           }
        }
        this.jtb_productos_facturas.setModel(dtm);
        // agregar los datos de la consulta a la tabla 
        for (int i = 0; i <data.size(); i++) {
             dtm.addRow(data.get(i));
            }   
     }catch(SQLException ex){
        JOptionPane.showMessageDialog(null,ex.getMessage(), "¡ERROR!", JOptionPane.ERROR_MESSAGE);
     }
    }//GEN-LAST:event_btn_cargarActionPerformed

    private void btn_imprimirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_imprimirActionPerformed
     
     boolean op = GuardarImprimir();
     if (op==true){
         Imprimir();
             } 
     
    }//GEN-LAST:event_btn_imprimirActionPerformed

    private void opcionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_opcionActionPerformed
        
        AutoCompletar(this.opcion.getSelectedIndex());
    }//GEN-LAST:event_opcionActionPerformed

    private void Salir(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_Salir
        d.setEnabled(true);
        d.setVisible(true);
        
    }//GEN-LAST:event_Salir

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
            java.util.logging.Logger.getLogger(ModuloContable.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ModuloContable.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ModuloContable.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ModuloContable.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
//        java.awt.EventQueue.invokeLater(new Runnable() {
////            public void run() {
////                new ModuloContable().setVisible(true);
////            }
//        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btn_buscar1;
    private javax.swing.JButton btn_buscar2;
    private javax.swing.JButton btn_cargar;
    private javax.swing.JButton btn_imprimir;
    private javax.swing.JLabel cantidad_etiqueta;
    private javax.swing.JLabel fecha_etiqueta;
    private javax.swing.JLabel hora_etiqueta;
    private javax.swing.JLabel id_etiqueta;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTable jtb_facturas;
    private javax.swing.JTable jtb_productos_facturas;
    private javax.swing.JTextField jtf_buscar1;
    private javax.swing.JComboBox opcion;
    private javax.swing.JLabel precio_etiqueta;
    private javax.swing.JLabel vendedor_etiqueta;
    // End of variables declaration//GEN-END:variables
}
