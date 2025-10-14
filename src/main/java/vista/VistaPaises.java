/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package vista;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.JFrame;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;


/**
 *
 * @author Mouli
 */
public class VistaPaises extends javax.swing.JFrame {
    
    /**
     * Creates new form VistaPaises
     */
    int xMouse, yMouse;
    public VistaPaises() {
        initComponents();
        setTitle("Lista de Paises");
        this.setLocationRelativeTo(null);
        this.setResizable(false);
        this.setSize(1020, 601);
        cargarMusicaDeFondo();
        personalizarTablaEstiloFrutiger(); 
        establecerCursorPersonalizado();
        this.getRootPane().setDefaultButton(btnagregar);
        
    }
        private void establecerCursorPersonalizado() {
    try {
        // 1. Obten el Toolkit, que es la caja de herramientas de AWT
        java.awt.Toolkit toolkit = java.awt.Toolkit.getDefaultToolkit();

        // 2. Carga tu imagen desde los recursos
        java.net.URL urlDeLaImagen = getClass().getResource("/icons/Mouse.png"); // <-- ¡Asegúrate de que el nombre del archivo sea correcto!
        java.awt.Image imagenCursor = new javax.swing.ImageIcon(urlDeLaImagen).getImage();

        // 3. Define el "HotSpot" (el punto exacto del cursor que hace clic)
        // Para la mayoría de los cursores, el punto (0, 0) que es la esquina superior izquierda, funciona bien.
        java.awt.Point hotSpot = new java.awt.Point(0, 0);

        // 4. Crea el objeto Cursor personalizado
        java.awt.Cursor cursorPersonalizado = toolkit.createCustomCursor(
            imagenCursor, 
            hotSpot, 
            "CursorAero" // Un nombre descriptivo para tu cursor
        );

        // 5. Aplica el cursor a TODA la ventana (JFrame)
        this.setCursor(cursorPersonalizado);

    } catch (Exception e) {
        System.out.println("No se pudo cargar el cursor personalizado: " + e.getMessage());
        // Si falla, se mantendrá el cursor por defecto del sistema.
    }
}
    private void personalizarTablaEstiloFrutiger() {
        
        // --- 1. Personalizar el Encabezado (Header) ---
        JTableHeader header = jTable1.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setOpaque(false);
        // Un azul brillante y fresco, muy característico del estilo
        header.setBackground(new Color(0, 176, 240)); 
        header.setForeground(Color.WHITE);

        // --- 2. Personalizar las Celdas con Colores Frutiger Aero ---
        jTable1.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(javax.swing.JTable table, Object value,
                                                             boolean isSelected, boolean hasFocus, int row, int column) {
                
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                // Fila par: un blanco casi puro para dar luminosidad
                if (row % 2 == 0) {
                    c.setBackground(new Color(245, 255, 255)); // Un blanco ligeramente azulado
                    c.setForeground(Color.DARK_GRAY);
                } else {
                    
                    c.setBackground(new Color(220, 245, 255)); 
                    c.setForeground(Color.DARK_GRAY);
                }

                
                if (isSelected) {
                    c.setBackground(new Color(50, 150, 255));
                    c.setForeground(Color.WHITE);
                }
                
                return c;
            }
        });

    // --- 3. Otros Ajustes Visuales ---
    jTable1.setRowHeight(28);
    jTable1.setGridColor(new Color(210, 235, 255));
    jTable1.setShowGrid(true);

    // --- 4. HACER TRANSPARENTE EL SCROLLPANE Y LA TABLA --- ¡ESTA ES LA CLAVE!
    
    // Hacemos que el JScrollPane no pinte su fondo
    jScrollPane1.setOpaque(false);
    jScrollPane1.getViewport().setOpaque(false);
    
    // Hacemos que la JTable no pinte su fondo
    jTable1.setOpaque(true);
    //jTable1.setBackground(new Color(0,0,0,0)); // Alternativa para hacerla completamente transparente
}
    private Clip clipMusica;
    private boolean musicaSonando = false;
    private void cargarMusicaDeFondo() {
    try {
        // Busca el archivo en la carpeta de recursos
        AudioInputStream audioStream = AudioSystem.getAudioInputStream(
            getClass().getResource("/sounds/fondo.wav") // <-- ¡CAMBIA ESTO por el nombre de tu archivo!
        );
        clipMusica = AudioSystem.getClip();
        clipMusica.open(audioStream);
    } catch (Exception ex) {
        System.out.println("Error al cargar la música de fondo: " + ex.getMessage());
    }
}
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel9 = new javax.swing.JLabel();
        minBtn = new javax.swing.JButton();
        extBtn = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        txtcodigo = new javax.swing.JTextField();
        txtcontinente = new javax.swing.JTextField();
        txtnombre = new javax.swing.JTextField();
        txtpoblacion = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        btnagregar = new javax.swing.JButton();
        btnconsultar = new javax.swing.JButton();
        btnmodificar = new javax.swing.JButton();
        jButton1 = new javax.swing.JButton();
        jLabel6 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setUndecorated(true);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel9.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel9.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/Paises.png"))); // NOI18N
        getContentPane().add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 40, 380, -1));

        minBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/boton minizar.png"))); // NOI18N
        minBtn.setAutoscrolls(true);
        minBtn.setBorder(null);
        minBtn.setContentAreaFilled(false);
        minBtn.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        minBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                minBtnActionPerformed(evt);
            }
        });
        getContentPane().add(minBtn, new org.netbeans.lib.awtextra.AbsoluteConstraints(940, 0, 40, 40));

        extBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/boton saliirrr (2).png"))); // NOI18N
        extBtn.setAutoscrolls(true);
        extBtn.setBorder(null);
        extBtn.setContentAreaFilled(false);
        extBtn.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        extBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                extBtnActionPerformed(evt);
            }
        });
        extBtn.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                extBtnKeyPressed(evt);
            }
        });
        getContentPane().add(extBtn, new org.netbeans.lib.awtextra.AbsoluteConstraints(980, 0, -1, -1));

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jPanel1.setForeground(new java.awt.Color(255, 255, 255));
        jPanel1.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                jPanel1MouseDragged(evt);
            }
        });
        jPanel1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jPanel1MousePressed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1020, 40));

        jLabel2.setText("Codigo");
        getContentPane().add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 200, -1, -1));

        jLabel3.setText("Nombre");
        getContentPane().add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 250, -1, -1));

        jLabel4.setText("Continente");
        getContentPane().add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 310, -1, -1));

        jLabel5.setText("Población");
        getContentPane().add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 370, -1, -1));

        txtcodigo.setForeground(new java.awt.Color(153, 153, 153));
        txtcodigo.setText("Ingresa el continente");
        txtcodigo.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtcodigoFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtcodigoFocusLost(evt);
            }
        });
        txtcodigo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtcodigoActionPerformed(evt);
            }
        });
        getContentPane().add(txtcodigo, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 300, 160, 40));

        txtcontinente.setForeground(new java.awt.Color(153, 153, 153));
        txtcontinente.setText("Ingresa el nombre");
        txtcontinente.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtcontinenteFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtcontinenteFocusLost(evt);
            }
        });
        txtcontinente.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtcontinenteActionPerformed(evt);
            }
        });
        getContentPane().add(txtcontinente, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 240, 160, 40));

        txtnombre.setForeground(new java.awt.Color(153, 153, 153));
        txtnombre.setText("Ingresa el codigo");
        txtnombre.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtnombreFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtnombreFocusLost(evt);
            }
        });
        txtnombre.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtnombreActionPerformed(evt);
            }
        });
        getContentPane().add(txtnombre, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 190, 160, 40));

        txtpoblacion.setForeground(new java.awt.Color(153, 153, 153));
        txtpoblacion.setText("Ingresa la población");
        txtpoblacion.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtpoblacionFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtpoblacionFocusLost(evt);
            }
        });
        txtpoblacion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtpoblacionActionPerformed(evt);
            }
        });
        getContentPane().add(txtpoblacion, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 360, 160, 40));

        jTable1.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Codigo", "Nombre", "Continente", "Poblacion"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTable1.setGridColor(new java.awt.Color(204, 255, 255));
        jTable1.setInheritsPopupMenu(true);
        jScrollPane1.setViewportView(jTable1);

        getContentPane().add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(430, 80, 560, 399));

        btnagregar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/Agregar.png"))); // NOI18N
        btnagregar.setBorder(null);
        btnagregar.setBorderPainted(false);
        btnagregar.setContentAreaFilled(false);
        btnagregar.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnagregar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnagregarActionPerformed(evt);
            }
        });
        getContentPane().add(btnagregar, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 430, 130, 60));

        btnconsultar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/Consultar.png"))); // NOI18N
        btnconsultar.setBorder(null);
        btnconsultar.setBorderPainted(false);
        btnconsultar.setContentAreaFilled(false);
        btnconsultar.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnconsultar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnconsultarActionPerformed(evt);
            }
        });
        getContentPane().add(btnconsultar, new org.netbeans.lib.awtextra.AbsoluteConstraints(500, 510, 140, 50));

        btnmodificar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/Modificar.png"))); // NOI18N
        btnmodificar.setBorder(null);
        btnmodificar.setBorderPainted(false);
        btnmodificar.setContentAreaFilled(false);
        btnmodificar.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        getContentPane().add(btnmodificar, new org.netbeans.lib.awtextra.AbsoluteConstraints(740, 510, 130, 50));

        jButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/sonido.png"))); // NOI18N
        jButton1.setBorder(null);
        jButton1.setBorderPainted(false);
        jButton1.setContentAreaFilled(false);
        jButton1.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        getContentPane().add(jButton1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 530, 70, 70));

        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/maybe_fondo.png"))); // NOI18N
        jLabel6.setText("jLabel6");
        getContentPane().add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 40, 380, 560));

        jLabel8.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/Papulandia.png"))); // NOI18N
        getContentPane().add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(920, 490, 100, 110));

        jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel7.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/FOndooo.png"))); // NOI18N
        jLabel7.setText("jLabel7");
        getContentPane().add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(290, 40, 930, 620));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnagregarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnagregarActionPerformed
        DefaultTableModel modelo = (DefaultTableModel) jTable1.getModel();

        
        Object[] fila = new Object[4];
        fila[0] = txtcodigo.getText();
        fila[1] = txtnombre.getText();
        fila[2] = txtcontinente.getText();
        fila[3] = txtpoblacion.getText();
        
        
        
        modelo.addRow(fila);
        
        // Campo Código (que en tu diseño actual es para el continente)
    txtcodigo.setText("");
    txtcodigo.setForeground(new Color(153,153,153));
    
    // Campo Nombre (que en tu diseño actual es para el código)
    txtnombre.setText("");
    txtnombre.setForeground(new Color(153,153,153));

    // Campo Continente (que en tu diseño actual es para el nombre)
    txtcontinente.setText("");
    txtcontinente.setForeground(new Color(153,153,153));

    // Campo Población
    txtpoblacion.setText("");
    txtpoblacion.setForeground(new Color(153,153,153));
    // --- FIN DEL BLOQUE ---

   
    }//GEN-LAST:event_btnagregarActionPerformed

    private void txtcodigoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtcodigoActionPerformed
        // TODO add your handling code here:
    
    }//GEN-LAST:event_txtcodigoActionPerformed

    private void txtcodigoFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtcodigoFocusGained
    // TODO add your handling code here:
        if(txtcodigo.getText().equals("Ingresa el continente"))
        {
            txtcodigo.setText("");
            txtcodigo.setForeground(new Color(0,0,0));
        }
    }//GEN-LAST:event_txtcodigoFocusGained

    private void txtcodigoFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtcodigoFocusLost
        if(txtcodigo.getText().equals("")){
            txtcodigo.setText("Ingresa el continente");
            txtcodigo.setForeground(new Color(153,153,153));
        }
    }//GEN-LAST:event_txtcodigoFocusLost

    private void txtnombreFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtnombreFocusGained

        if(txtnombre.getText().equals("Ingresa el codigo"))
        {
            txtnombre.setText("");
            txtnombre.setForeground(new Color(0,0,0));
        }
    }//GEN-LAST:event_txtnombreFocusGained

    private void txtnombreFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtnombreFocusLost
        // TODO add your handling code here:
        if(txtnombre.getText().equals("")){
            txtnombre.setText("Ingresa el codigo");
            txtnombre.setForeground(new Color(153,153,153));
        }
    }//GEN-LAST:event_txtnombreFocusLost

    private void txtcontinenteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtcontinenteActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtcontinenteActionPerformed

    private void txtcontinenteFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtcontinenteFocusGained
        // TODO add your handling code here:
        if(txtcontinente.getText().equals("Ingresa el nombre"))
        {
            txtcontinente.setText("");
            txtcontinente.setForeground(new Color(0,0,0));
        }
    }//GEN-LAST:event_txtcontinenteFocusGained

    private void txtcontinenteFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtcontinenteFocusLost
        if(txtcontinente.getText().equals("")){
            txtcontinente.setText("Ingresa el nombre");
            txtcontinente.setForeground(new Color(153,153,153));
        }
        
    }//GEN-LAST:event_txtcontinenteFocusLost

    private void txtpoblacionFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtpoblacionFocusGained
        // TODO add your handling code here:
        if(txtpoblacion.getText().equals("Ingresa la población"))
        {
            txtpoblacion.setText("");
            txtpoblacion.setForeground(new Color(0,0,0));
        }
    }//GEN-LAST:event_txtpoblacionFocusGained

    private void txtpoblacionFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtpoblacionFocusLost
        if(txtpoblacion.getText().equals("")){
            txtpoblacion.setText("Ingresa la población");
            txtpoblacion.setForeground(new Color(153,153,153));
        }
    }//GEN-LAST:event_txtpoblacionFocusLost

    private void minBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_minBtnActionPerformed
        this.setExtendedState(JFrame.ICONIFIED);            // TODO add your handling code here:
    }//GEN-LAST:event_minBtnActionPerformed

    private void extBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_extBtnActionPerformed
        System.exit(0);// TODO add your handling code here:
    }//GEN-LAST:event_extBtnActionPerformed

    private void extBtnKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_extBtnKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_extBtnKeyPressed

    private void jPanel1MouseDragged(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanel1MouseDragged
        int x = evt.getXOnScreen();
        int y = evt.getYOnScreen();
        this.setLocation(x - xMouse,y - yMouse);        // TODO add your handling code here:
    }//GEN-LAST:event_jPanel1MouseDragged

    private void jPanel1MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanel1MousePressed
        xMouse = evt.getX();
        yMouse = evt.getY();
    }//GEN-LAST:event_jPanel1MousePressed

    private void txtpoblacionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtpoblacionActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtpoblacionActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        if (clipMusica != null) {
            // Si la música está sonando...
            if (musicaSonando) {
                // ... la detenemos.
                clipMusica.stop();
                // Opcional: Cambia el texto del botón para que el usuario sepa
                // btnMusica.setText("Activar Música");
            } else {
                // Si no está sonando...
                // ... la iniciamos para que se repita continuamente.
                clipMusica.loop(Clip.LOOP_CONTINUOUSLY);
                // Opcional: Cambia el texto del botón
                // btnMusica.setText("Desactivar Música");
            }
            // Invertimos el estado
            musicaSonando = !musicaSonando;
        }
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton1ActionPerformed

    private void txtnombreActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtnombreActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtnombreActionPerformed

    private void btnconsultarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnconsultarActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnconsultarActionPerformed

    /**
     * @param args the command line arguments
     */

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnagregar;
    private javax.swing.JButton btnconsultar;
    private javax.swing.JButton btnmodificar;
    private javax.swing.JButton extBtn;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JButton minBtn;
    private javax.swing.JTextField txtcodigo;
    private javax.swing.JTextField txtcontinente;
    private javax.swing.JTextField txtnombre;
    private javax.swing.JTextField txtpoblacion;
    // End of variables declaration//GEN-END:variables

private void setPlaceholder(javax.swing.JTextField textField, String placeholder) {
        textField.setText(placeholder);
        textField.setForeground(new java.awt.Color(153, 153, 153)); // Un gris un poco más oscuro

        textField.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent evt) {
                if (textField.getText().equals(placeholder)) {
                    textField.setText("");
                    textField.setForeground(java.awt.Color.BLACK);
                }
            }

            @Override
            public void focusLost(java.awt.event.FocusEvent evt) {
                if (textField.getText().isEmpty()) {
                    textField.setText(placeholder);
                    textField.setForeground(new java.awt.Color(153, 153, 153));
                }
            }
        });
    }


}
