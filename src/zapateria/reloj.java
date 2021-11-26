
package zapateria;

import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.JLabel;

public class reloj extends Thread{
    private JLabel lbl;
    private JLabel lbl2;
    
    public reloj(JLabel lbl, JLabel lbl2){
        this.lbl = lbl;
        this.lbl2 = lbl2;
    }
    
    public void run(){
       while(true){
           Date hoy = new Date();
           SimpleDateFormat s = new SimpleDateFormat("hh:mm:ss a");
           lbl.setText(s.format(hoy));
           lbl2.setText(s.format(hoy));
           try{
               sleep(1000);
           }catch(Exception ex){
               
           }
       } 
    }
}
