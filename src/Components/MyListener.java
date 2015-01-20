package Components;

import javax.swing.JTextField;
import java.awt.Choice;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

/**
 * Created by serch on 19/01/15.
 */
public class MyListener implements ItemListener {
    private Choice event;
    private Choice text1;
    private JTextField text2;

    public MyListener(Choice events, Choice attribute) {
        this.event = events;
        this.text1 = attribute;
    }

    public MyListener(Choice events, Choice attribute, JTextField text) {
        this.event = events;
        this.text1 = attribute;
        this.text2 = text;
    }

    @Override
    public void itemStateChanged(ItemEvent e) {
        boolean b = event.getItem(event.getSelectedIndex()).equalsIgnoreCase("update");
        text1.setEnabled(b);
        if (text2 != null)
            text2.setEnabled(b);
    }
}
