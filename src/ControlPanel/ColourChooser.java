package ControlPanel;

import javax.swing.*;
import javax.swing.colorchooser.AbstractColorChooserPanel;
import javax.swing.colorchooser.DefaultColorSelectionModel;

public class ColourChooser extends JColorChooser {
    public ColourChooser() {
        super(new DefaultColorSelectionModel());

        AbstractColorChooserPanel[] panels = super.getChooserPanels();
        for (AbstractColorChooserPanel accp : panels) {
            if (accp.getDisplayName().equals("Swatches")) {
                super.setChooserPanels(new AbstractColorChooserPanel[] {accp});
            }
        }

        super.setPreviewPanel(new JPanel());
    }
}