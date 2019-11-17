package lab3;

import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.text.DecimalFormat;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

public class GornerTableCellRenderer implements TableCellRenderer {

    private JPanel panel = new JPanel();
    private JLabel label = new JLabel();
    private String needle = null;
    private Double rangeMin = null;
    private Double rangeMax = null;

    private DecimalFormat formatter = MainFrame.getFormatter();

    public GornerTableCellRenderer() {
        panel.add(label);
        panel.setLayout(new FlowLayout(FlowLayout.LEFT));
    }

    public Component getTableCellRendererComponent(JTable table,
                                                   Object value, boolean isSelected, boolean hasFocus, int row, int col) {
        String formattedDouble = formatter.format(value);
        label.setText(formattedDouble);
        if (col == 1 && needle != null && needle.equals(formattedDouble)
                || rangeMin != null &&  rangeMax != null && (Double)value >= rangeMin && (Double)value <= rangeMax ) {
            panel.setBackground(Color.RED);
        } else {
            if((row + col)%2 == 1){
                panel.setBackground(Color.BLACK);
                label.setForeground(Color.WHITE);
            }
            else{
                panel.setBackground(Color.WHITE);
                label.setForeground(Color.BLACK);
            }
        }
        return panel;
    }

    public void setNeedle(String needle) {
        this.needle = needle;
    }
    public void setRange(Double min, Double max) {
        rangeMin = min;
        rangeMax = max;
    }

}
