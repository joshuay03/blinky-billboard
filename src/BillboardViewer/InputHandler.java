package BillboardViewer;

import javax.swing.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

class InputHandlerGlassPane extends JComponent {
    InputHandlerGlassPane() {
        this.addKeyListener(new MyKeyboardHandler());
        this.addMouseListener(new MyMouseHandler());
    }
}

class MyMouseHandler extends MouseAdapter {

    @Override
    public void mouseClicked(MouseEvent e) {
        if(e.getButton() == MouseEvent.BUTTON1){
            System.exit(1);
        }
    }

}

class MyKeyboardHandler extends KeyAdapter {

    @Override
    public void keyPressed(KeyEvent e) {
        if(e.getKeyCode() == KeyEvent.VK_ESCAPE){
            System.exit(1);
        }
    }
}
