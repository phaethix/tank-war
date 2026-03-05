package org.tinygame.tankwar;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class Main {
    static void main() {
        Frame f = new Frame();

        f.setTitle("Tank War");
        f.setVisible(true);
        f.setResizable(false);
        f.setSize(800, 600);

        f.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
    }
}
