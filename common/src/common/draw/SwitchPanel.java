package common.draw;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

final class SwitchPanel extends JPanel {

    SwitchPanel(final GraphPanel panel) {
        StatModeEnum[] values = StatModeEnum.values();
        setLayout(new GridLayout(values.length, 1));
        ButtonGroup group = new ButtonGroup();
        for (final StatModeEnum value : values) {
            JRadioButton butt = new JRadioButton(value.toString());
            butt.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    saveMode(value);
                    panel.setMode(value);
                }
            });
            add(butt);
            group.add(butt);
            if (value == panel.getMode()) {
                butt.setSelected(true);
            }
        }
    }

    static StatModeEnum loadMode() {
        return StatModeEnum.valueOf(GraphGuiUtil.getPrefs().get("display.mode", StatModeEnum.ENERGY.name()));
    }

    private static void saveMode(StatModeEnum mode) {
        Preferences prefs = GraphGuiUtil.getPrefs();
        prefs.put("display.mode", mode.name());
        try {
            prefs.flush();
        } catch (BackingStoreException ex) {
            // ignore
        }
    }
}
