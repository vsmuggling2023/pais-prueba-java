/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.papu.datospaises;

import vista.VistaLogin; // 1. Importa tu ventana de login

/**
 *
 * @author mouli
 */
public class Datospaises {

    public static void main(String[] args) {
        // 2. En lugar de "Hello World", crea y muestra la ventana de login
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new VistaLogin().setVisible(true);
            }
        });
    }
}
