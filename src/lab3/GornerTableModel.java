package lab3;

import javax.swing.table.AbstractTableModel;

public class GornerTableModel extends AbstractTableModel {

    private Double[] coefficients;
    private Double from;
    private Double to;
    private Double step;

    public GornerTableModel(Double from, Double to, Double step,
                            Double[] coefficients) {
        this.from = from;
        this.to = to;
        this.step = step;
        this.coefficients = coefficients;
    }

    public Double getFrom() {
        return from;
    }

    public Double getTo() {
        return to;
    }

    public Double getStep() {
        return step;
    }

    public int getColumnCount() {
        // В данной модели два столбца
        return 4;
    }

    public int getRowCount() {
        // Вычислить количество точек между началом и концом отрезка
        // исходя из шага табулирования
        return new Double(Math.ceil((to-from)/step)).intValue()+1;
    }

    public Object getValueAt(int row, int col) {
        // Вычислить значение X как НАЧАЛО_ОТРЕЗКА + ШАГ*НОМЕР_СТРОКИ
        double x = from + step*row;
        if (col==0) {
            // Если запрашивается значение 1-го столбца, то это X
            return x;
        }
        else if(col == 1) {
            // Если запрашивается значение 2-го столбца, то это значение многочлена
            if (coefficients.length == 1) return coefficients[0];
            Double result = coefficients[0]*x + coefficients[1];
            for(int i = 1; i < coefficients.length - 1; i++) {
                result = result * x + coefficients[i+1];
            }

            return result;
        }
        else if(col == 2){
            // Если запрашивается значение 2-го столбца, то это значение многочлена
            if (coefficients.length == 1) return coefficients[0];
            Double result = coefficients[coefficients.length - 1]*x + coefficients[coefficients.length - 2];
            for(int i = coefficients.length - 2; i >= 1; i--) {
                result = result * x + coefficients[i-1];
            }

            return result;
        }
        else{

            if (coefficients.length == 1) return 0;
            Double result1 = coefficients[0]*x + coefficients[1];
            for(int i = 1; i < coefficients.length - 1; i++) {
                result1 = result1 * x + coefficients[i+1];
            }
            Double result2 = coefficients[coefficients.length - 1]*x + coefficients[coefficients.length - 2];
            for(int i = coefficients.length - 2; i >= 1; i--) {
                result2 = result2 * x + coefficients[i-1];
            }
            return result1 - result2;

        }
    }

    public String getColumnName(int col) {
        switch (col) {
            case 0:
                return "Значение X";
            case 1:
                return "Значение многочлена";
            case 2:
                return "Значение многочлена в обр. порядке";
            default:
                return "Разность столбцов 2 и 3";
        }
    }

    public Class<?> getColumnClass(int col) {
        return Double.class;
    }

}
