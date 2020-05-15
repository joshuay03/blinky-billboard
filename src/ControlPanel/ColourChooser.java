package ControlPanel;

import javax.swing.*;
import javax.swing.colorchooser.ColorSelectionModel;
import javax.swing.event.ChangeListener;
import java.awt.*;

public class ColourChooser extends JColorChooser {
    public ColourChooser() {
        super(new ColorSelectionModel() {
            @Override
            public Color getSelectedColor() {
                return null;
            }

            @Override
            public void setSelectedColor(Color color) {

            }

            @Override
            public void addChangeListener(ChangeListener listener) {

            }

            @Override
            public void removeChangeListener(ChangeListener listener) {

            }
        });

        super.setPreviewPanel(new JPanel());
    }
}
