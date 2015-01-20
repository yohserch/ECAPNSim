package Components;

import javax.swing.JTextField;
import java.awt.Choice;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

/**
 * Created by serch on 19/01/15.
 */
public class Listener implements ItemListener {
    private Choice logic;
    private Choice attribute;
    private Choice conditions;
    private JTextField values;

    public Listener(Choice logic, Choice attribute, Choice conditions, JTextField TF_value) {
        this.logic = logic;
        this.attribute = attribute;
        this.conditions = conditions;
        this.values = TF_value;
    }
    @Override
    public void itemStateChanged(ItemEvent e) {
        boolean a = !logic.getItem(logic.getSelectedIndex()).equalsIgnoreCase("  ");
        attribute.setEnabled(a);
        conditions.setEnabled(a);
        values.setEnabled(a);
    }
}
