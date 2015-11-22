//MyRender.java

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.Component;
import java.awt.Font;
import java.awt.Color;

public class MyTableRenderer extends DefaultTableCellRenderer{
	private static final long serialVersionUID = 1L;
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column){
        //Align the text in the cell to the Center.
    	super.getTableCellRendererComponent(table,  value,  isSelected,  hasFocus,  row,  column);
        super.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);

        //Some conditions to set Colors in Table.
        if(column==0 || row==0){
            super.setBackground(new Color(0x0ff0f0));
            super.setFont(new Font("Serif", Font.BOLD, 15));
        }
        else{
            super.setBackground(new Color(0xffffff));
            super.setFont(new Font("SansSerif", Font.ITALIC, 15));
        }

        super.setForeground(Color.black);

        return this;
    }
}
