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
import javax.swing.JOptionPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.util.Collections;
import java.util.List;
import java.util.Arrays;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.ArrayList;
import javax.swing.table.TableRowSorter;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.RowFilter;
import javax.swing.RowSorter;
import java.awt.Desktop;
import java.net.URI;
import conn.Conexion;
// Importa las clases necesarias de SQL
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.util.ArrayList;

/**
 *
 * @author Mouli
 */
public class VistaPaises extends javax.swing.JFrame {

    /**
     * Creates new form VistaPaises
     */
    int xMouse, yMouse;
    private int papulandiaClickCount = 0;

    public VistaPaises() {
        initComponents();
        jTable1.setSelectionMode(javax.swing.ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        setTitle("Lista de Paises");
        this.setLocationRelativeTo(null);
        this.setResizable(false);
        this.setSize(1020, 600);
        cargarMusicaDeFondo();
        setIconImage(new javax.swing.ImageIcon(getClass().getResource("/icons/Papulandia2.png")).getImage());
        personalizarTablaEstiloFrutiger();
        establecerCursorPersonalizado();
        this.getRootPane().setDefaultButton(btnagregar);
        jTable1.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent event) {
                int filaSeleccionada = jTable1.getSelectedRow();
                if (filaSeleccionada >= 0) {
                    DefaultTableModel modelo = (DefaultTableModel) jTable1.getModel();
                    txtcodigo.setText(modelo.getValueAt(filaSeleccionada, 0).toString());
                    txtnombre.setText(modelo.getValueAt(filaSeleccionada, 1).toString());
                    txtcontinente.setText(modelo.getValueAt(filaSeleccionada, 2).toString());
                    txtpoblacion.setText(modelo.getValueAt(filaSeleccionada, 3).toString());

                    txtcodigo.setForeground(Color.black);
                    txtnombre.setForeground(Color.black);
                    txtcontinente.setForeground(Color.black);
                    txtpoblacion.setForeground(Color.black);
                }
            }
        });
        buscarPaises();
    }

    private void establecerCursorPersonalizado() {
        try {

            java.awt.Toolkit toolkit = java.awt.Toolkit.getDefaultToolkit();

            java.net.URL urlDeLaImagen = getClass().getResource("/icons/Mouse.png");
            java.awt.Image imagenCursor = new javax.swing.ImageIcon(urlDeLaImagen).getImage();

            java.awt.Point hotSpot = new java.awt.Point(0, 0);

            java.awt.Cursor cursorPersonalizado = toolkit.createCustomCursor(
                    imagenCursor,
                    hotSpot,
                    "CursorAero"
            );

            this.setCursor(cursorPersonalizado);

        } catch (Exception e) {
            System.out.println("No se pudo cargar el cursor personalizado: " + e.getMessage());

        }
        jLabel8.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                papulandiaClickCount++; // Incrementa el contador con cada clic

                // Si el contador llega a 3...
                if (papulandiaClickCount == 3) {
                    try {
                        // ...intenta abrir el enlace en el navegador
                        Desktop.getDesktop().browse(new URI("https://www.youtube.com/shorts/q4oigdRoBG4"));
                    } catch (Exception ex) {
                        // Si algo sale mal, imprime un error en la consola
                        System.out.println("No se pudo abrir el enlace: " + ex.getMessage());
                    }

                    // Reinicia el contador para que se pueda volver a activar
                    papulandiaClickCount = 0;
                }
            }
        });
    }

    /**
     * Ejecuta una consulta SQL dinámica a la base de datos 'country' basándose
     * en los campos de texto y actualiza la jTable.
     */
    private void buscarPaises() {
        // Define las columnas para el modelo de la tabla
        DefaultTableModel modelo = new DefaultTableModel(
                new Object[]{"Codigo", "Nombre", "Continente", "Poblacion"}, 0
        );

        // 1. Prepara la consulta SQL base
        // (Asegúrate que los nombres de columna sean 'Code', 'Name', etc. como en tu BD)
        String sqlBase = "SELECT Code, Name, Continent, Population FROM country";

        // Listas para construir la consulta dinámica de forma segura
        ArrayList<String> conditions = new ArrayList<>();
        ArrayList<Object> params = new ArrayList<>();

        // 2. Recoge los textos de los campos
        String codigo = txtcodigo.getText();
        String nombre = txtnombre.getText();
        String continente = txtcontinente.getText();
        String poblacion = txtpoblacion.getText();

        try {
            // 3. Añade condiciones SÓLO si el campo está lleno

            // Si el campo 'codigo' no está vacío ni es el placeholder
            if (!codigo.isEmpty() && !codigo.equals("Ingresa el codigo")) {
                conditions.add("Code LIKE ?"); // Buscar por código
                params.add(codigo + "%");      // Parámetro para 'Code'
            }

            // Si el campo 'nombre' no está vacío...
            if (!nombre.isEmpty() && !nombre.equals("Ingresa el nombre")) {
                conditions.add("Name LIKE ?"); // Buscar por nombre
                params.add("%" + nombre + "%");  // Parámetro para 'Name' (con comodines)
            }

            // Si el campo 'continente' no está vacío...
            if (!continente.isEmpty() && !continente.equals("Ingresa el continente")) {
                conditions.add("Continent LIKE ?"); // Buscar por continente
                params.add("%" + continente + "%"); // Parámetro para 'Continent'
            }

            // Si el campo 'poblacion' no está vacío...
            if (!poblacion.isEmpty() && !poblacion.equals("Ingresa la población")) {
                conditions.add("Population >= ?"); // Buscar población MAYOR O IGUAL que
                params.add(Integer.parseInt(poblacion)); // Parámetro numérico
            }

            // 4. Construye la consulta final
            if (!conditions.isEmpty()) {
                // Si hay al menos una condición, une todas con " AND "
                sqlBase += " WHERE " + String.join(" AND ", conditions);
            }

            sqlBase += " LIMIT 100"; // Limitar a 100 resultados

            // 5. Ejecuta la consulta
            Connection miConexion = Conexion.getConnection();

            // Usamos PreparedStatement para insertar los parámetros de forma segura
            try (PreparedStatement pstmt = miConexion.prepareStatement(sqlBase)) {

                // Asigna los valores de la lista 'params' a la consulta
                for (int i = 0; i < params.size(); i++) {
                    pstmt.setObject(i + 1, params.get(i));
                }

                // Ejecuta la consulta y obtén los resultados
                try (ResultSet rs = pstmt.executeQuery()) {

                    System.out.println("Ejecutando consulta: " + pstmt.toString());

                    // 6. Recorre los resultados y llena el modelo de la tabla
                    while (rs.next()) {
                        modelo.addRow(new Object[]{
                            rs.getString("Code"),
                            rs.getString("Name"),
                            rs.getString("Continent"),
                            rs.getInt("Population")
                        });
                    }

                    if (modelo.getRowCount() == 0) {
                        System.out.println("No se encontraron resultados para la búsqueda.");
                    } else {
                        System.out.println(modelo.getRowCount() + " países cargados.");
                    }
                }
            }

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "La población debe ser un número válido.", "Error de Formato", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al consultar la base de datos: " + e.getMessage(), "Error SQL", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error inesperado: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }

        // 7. Asigna el modelo (lleno o vacío) a la tabla
        jTable1.setModel(modelo);
        // Vuelve a aplicar el estilo Frutiger a la tabla (importante)
        personalizarTablaEstiloFrutiger();
    }

    public class EjemploConsulta {

        public void consultarPaises() {
            // 1. Obtienes la conexión estática
            Connection miConexion = Conexion.getConnection();

            // Verificas que la conexión no sea nula
            if (miConexion != null) {

                // 2. Escribes tu consulta a la tabla 'country'
                String sql = "SELECT Name, Continent, Population FROM country WHERE Continent = 'South America'";

                try (Statement stmt = miConexion.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {

                    System.out.println("--- Países de Sudamérica en la BD 'world' ---");

                    // 3. Recorres los resultados
                    while (rs.next()) {
                        String nombre = rs.getString("Name");
                        String continente = rs.getString("Continent");
                        int poblacion = rs.getInt("Population");

                        System.out.println(nombre + " (" + continente + ") - Población: " + poblacion);
                    }

                } catch (SQLException e) {
                    System.out.println("❌ Error al ejecutar la consulta SQL");
                    e.printStackTrace();
                }
                // (Opcional) Puedes cerrar la conexión cuando tu app se cierre
                // Conexion.closeConnection();
            }
        }
    }

    private void personalizarTablaEstiloFrutiger() {

        JTableHeader header = jTable1.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setOpaque(false);

        header.setBackground(new Color(0, 176, 240));
        header.setForeground(Color.WHITE);

        jTable1.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(javax.swing.JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {

                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                if (row % 2 == 0) {
                    c.setBackground(new Color(245, 255, 255));
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

        jTable1.setRowHeight(28);
        jTable1.setGridColor(new Color(210, 235, 255));
        jTable1.setShowGrid(true);

        jScrollPane1.setOpaque(false);
        jScrollPane1.getViewport().setOpaque(false);

        jTable1.setOpaque(true);

    }
    private Clip clipMusica;
    private boolean musicaSonando = false;

    private void cargarMusicaDeFondo() {
        try {

            AudioInputStream audioStream = AudioSystem.getAudioInputStream(
                    getClass().getResource("/sounds/fondo.wav")
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

        btnVerDetalles = new javax.swing.JButton();
        jLabel9 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        minBtn = new javax.swing.JButton();
        extBtn = new javax.swing.JButton();
        btnCerrarSesion = new javax.swing.JButton();
        btnPaises = new javax.swing.JButton();
        btnCuidades = new javax.swing.JButton();
        btnIdiomas = new javax.swing.JButton();
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
        jDesktopPane1 = new javax.swing.JDesktopPane();
        jScrollPane2 = new javax.swing.JScrollPane();
        jList1 = new javax.swing.JList<>();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setUndecorated(true);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        btnVerDetalles.setText("Detalles");
        btnVerDetalles.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnVerDetallesActionPerformed(evt);
            }
        });
        getContentPane().add(btnVerDetalles, new org.netbeans.lib.awtextra.AbsoluteConstraints(280, 90, -1, -1));

        jLabel9.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel9.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/Paises.png"))); // NOI18N
        getContentPane().add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 40, 380, -1));

        jPanel1.setBackground(new java.awt.Color(229, 246, 246));
        jPanel1.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jPanel1.setForeground(new java.awt.Color(255, 255, 255));
        jPanel1.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
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

        btnCerrarSesion.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/CerrarSesion.png"))); // NOI18N
        btnCerrarSesion.setContentAreaFilled(false);
        btnCerrarSesion.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnCerrarSesion.setFocusPainted(false);
        btnCerrarSesion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCerrarSesionActionPerformed(evt);
            }
        });

        btnPaises.setText("Paises");
        btnPaises.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPaisesActionPerformed(evt);
            }
        });

        btnCuidades.setText("Cuidades");
        btnCuidades.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCuidadesActionPerformed(evt);
            }
        });

        btnIdiomas.setText("Idiomas");
        btnIdiomas.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnIdiomasActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btnCerrarSesion, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 389, Short.MAX_VALUE)
                .addComponent(btnPaises)
                .addGap(50, 50, 50)
                .addComponent(btnCuidades, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(39, 39, 39)
                .addComponent(btnIdiomas, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(51, 51, 51)
                .addComponent(minBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(extBtn))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(btnPaises)
                        .addComponent(btnCuidades)
                        .addComponent(btnIdiomas))
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(btnCerrarSesion, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                        .addComponent(minBtn, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(extBtn, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1020, 50));

        jLabel2.setText("Codigo");
        getContentPane().add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 200, -1, -1));

        jLabel3.setText("Nombre");
        getContentPane().add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 250, -1, -1));

        jLabel4.setText("Continente");
        getContentPane().add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 310, -1, -1));

        jLabel5.setText("Población");
        getContentPane().add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 370, -1, -1));

        txtcodigo.setForeground(new java.awt.Color(153, 153, 153));
        txtcodigo.setText("Ingresa el codigo");
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
        txtcodigo.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtcodigoKeyTyped(evt);
            }
        });
        getContentPane().add(txtcodigo, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 190, 160, 40));

        txtcontinente.setForeground(new java.awt.Color(153, 153, 153));
        txtcontinente.setText("Ingresa el continente");
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
        getContentPane().add(txtcontinente, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 300, 160, 40));

        txtnombre.setForeground(new java.awt.Color(153, 153, 153));
        txtnombre.setText("Ingresa el nombre");
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
        txtnombre.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtnombreKeyTyped(evt);
            }
        });
        getContentPane().add(txtnombre, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 240, 160, 40));

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
        txtpoblacion.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtpoblacionKeyTyped(evt);
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
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.String.class, java.lang.String.class, java.lang.Integer.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTable1.setGridColor(new java.awt.Color(204, 255, 255));
        jTable1.setInheritsPopupMenu(true);
        jScrollPane1.setViewportView(jTable1);

        getContentPane().add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(460, 90, 560, 399));

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
        btnmodificar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnmodificarActionPerformed(evt);
            }
        });
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
        jLabel8.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        getContentPane().add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(920, 490, 100, 110));

        jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel7.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/FOndooo.png"))); // NOI18N
        jLabel7.setText("jLabel7");
        getContentPane().add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(290, 40, 930, 620));

        javax.swing.GroupLayout jDesktopPane1Layout = new javax.swing.GroupLayout(jDesktopPane1);
        jDesktopPane1.setLayout(jDesktopPane1Layout);
        jDesktopPane1Layout.setHorizontalGroup(
            jDesktopPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );
        jDesktopPane1Layout.setVerticalGroup(
            jDesktopPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 40, Short.MAX_VALUE)
        );

        getContentPane().add(jDesktopPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(250, 0, -1, 40));

        jList1.setModel(new javax.swing.AbstractListModel<String>() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public String getElementAt(int i) { return strings[i]; }
        });
        jScrollPane2.setViewportView(jList1);

        getContentPane().add(jScrollPane2, new org.netbeans.lib.awtextra.AbsoluteConstraints(320, 0, -1, -1));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnagregarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnagregarActionPerformed
        String codigo = txtcodigo.getText();
        String nombre = txtnombre.getText();
        String continente = txtcontinente.getText();
        String poblacion = txtpoblacion.getText();

        // 2. Validar que los campos no estén vacíos (con los placeholders)
        if (codigo.isEmpty() || codigo.equals("Ingresa el codigo")
                || nombre.isEmpty() || nombre.equals("Ingresa el nombre")
                || continente.isEmpty() || continente.equals("Ingresa el continente")
                || poblacion.isEmpty() || poblacion.equals("Ingresa la población")) {

            JOptionPane.showMessageDialog(this, "Por favor, rellena todos los campos.", "Campos vacíos", JOptionPane.WARNING_MESSAGE);
            return; // Salir si algo falta
        }

        // --- INICIA LA LÓGICA DE BASE DE DATOS ---
        Connection miConexion = null;
        try {
            // 3. Obtener la conexión
            miConexion = Conexion.getConnection();

            // 4. Preparar la consulta SQL (¡Usa los nombres de columna de tu BD!)
            // (Asumo que son 'Code', 'Name', 'Continent', 'Population' de la BD 'world')
            String sql = "INSERT INTO country (Code, Name, Continent, Population) VALUES (?, ?, ?, ?)";

            // 5. Usar PreparedStatement para insertar datos de forma segura
            try (java.sql.PreparedStatement pstmt = miConexion.prepareStatement(sql)) {

                // 6. Asignar los valores a los '?'
                pstmt.setString(1, codigo);      // El 'Code' (ej: "CHL")
                pstmt.setString(2, nombre);      // El 'Name'
                pstmt.setString(3, continente);  // El 'Continent'
                pstmt.setInt(4, Integer.parseInt(poblacion)); // La 'Population'

                // 7. Ejecutar la inserción
                int filasAfectadas = pstmt.executeUpdate();

                // 8. Verificar si la inserción fue exitosa
                if (filasAfectadas > 0) {
                    JOptionPane.showMessageDialog(this, "¡País agregado exitosamente a la base de datos!", "Éxito", JOptionPane.INFORMATION_MESSAGE);

                    // 9. Actualizar la tabla visual
                    // (Llama al método de búsqueda que hicimos antes para refrescar)
                    buscarPaises();

                    // 10. Limpiar los campos de texto
                    txtcodigo.setText("Ingresa el codigo");
                    txtcodigo.setForeground(new Color(153, 153, 153));
                    txtnombre.setText("Ingresa el nombre");
                    txtnombre.setForeground(new Color(153, 153, 153));
                    txtcontinente.setText("Ingresa el continente");
                    txtcontinente.setForeground(new Color(153, 153, 153));
                    txtpoblacion.setText("Ingresa la población");
                    txtpoblacion.setForeground(new Color(153, 153, 153));

                } else {
                    JOptionPane.showMessageDialog(this, "No se pudo agregar el país.", "Error", JOptionPane.WARNING_MESSAGE);
                }
            }

        } catch (SQLException e) {
            // Error de SQL (ej: código duplicado, tipo de dato incorrecto)
            JOptionPane.showMessageDialog(this, "Error al guardar en la base de datos: " + e.getMessage(), "Error SQL", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        } catch (NumberFormatException e) {
            // Error si la población no es un número
            JOptionPane.showMessageDialog(this, "La población debe ser un número entero válido.", "Error de Formato", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            // Cualquier otro error (ej: conexión)
            JOptionPane.showMessageDialog(this, "Error inesperado: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }//GEN-LAST:event_btnagregarActionPerformed
    private void mostrarDetallesPaisSeleccionado() {
        int filaSeleccionada = jTable1.getSelectedRow();

        if (filaSeleccionada < 0) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar un país de la tabla para ver sus detalles.", "País no seleccionado", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Obtener el código de país (PK) de la fila seleccionada
        DefaultTableModel modelo = (DefaultTableModel) jTable1.getModel();
        // *** EL AJUSTE CRÍTICO: .trim() para eliminar espacios en blanco y asegurar la coincidencia SQL ***
        String codigoPais = modelo.getValueAt(filaSeleccionada, 0).toString().trim();

        Connection miConexion = null;
        try {
            miConexion = conn.Conexion.getConnection();

            // Consulta SQL para obtener todos los detalles del país
            String sql = "SELECT "
                    + "T1.Name, T1.Continent, T1.Region, T1.SurfaceArea, T1.IndepYear, "
                    + "T1.Population, T1.LifeExpectancy, T1.GNP, T1.GovernmentForm, "
                    + "T1.HeadOfState, T2.Name AS CapitalName "
                    + "FROM country T1 "
                    + "LEFT JOIN city T2 ON T1.Capital = T2.ID " // Unir con City para obtener el nombre de la Capital
                    + "WHERE T1.Code = ?";

            try (PreparedStatement pstmt = miConexion.prepareStatement(sql)) {
                pstmt.setString(1, codigoPais);

                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        // 1. Recolección de datos
                        String nombre = rs.getString("Name");
                        String continente = rs.getString("Continent");
                        String region = rs.getString("Region");
                        double superficie = rs.getDouble("SurfaceArea");

                        // Manejar IndepYear que puede ser NULL en la BD
                        Object indepYearObj = rs.getObject("IndepYear");
                        String indepYear = (indepYearObj != null) ? indepYearObj.toString() : "N/A";

                        int poblacion = rs.getInt("Population");
                        // Manejar LifeExpectancy que puede ser NULL
                        double lifeExpectancyRaw = rs.getDouble("LifeExpectancy");
                        String lifeExpectancy = rs.wasNull() ? "N/A" : String.format("%,.1f", lifeExpectancyRaw) + " años";

                        double gnp = rs.getDouble("GNP");
                        String formaGobierno = rs.getString("GovernmentForm");
                        String jefeEstado = rs.getString("HeadOfState");
                        String capital = rs.getString("CapitalName");

                        // 2. Formato del mensaje en HTML para una mejor presentación (LA MINI VENTANA)
                        String detalles = "<html><body style='width: 300px; font-family: sans-serif;'>"
                                + "<h2>Detalles Completos de " + nombre + " (" + codigoPais + ")</h2>"
                                + "<hr style='border: 1px solid #ccc;'>"
                                + "<p><b>Continente:</b> " + continente + "</p>"
                                + "<p><b>Región:</b> " + region + "</p>"
                                + "<p><b>Superficie:</b> " + String.format("%,.2f", superficie) + " km²</p>"
                                + "<p><b>Año de Independencia:</b> " + indepYear + "</p>"
                                + "<p><b>Población:</b> " + String.format("%,d", poblacion) + "</p>"
                                + "<p><b>Expectativa de Vida:</b> " + lifeExpectancy + "</p>"
                                + "<p><b>Producto Nacional Bruto (GNP):</b> " + String.format("%,.2f", gnp) + "</p>"
                                + "<p><b>Forma de Gobierno:</b> " + formaGobierno + "</p>"
                                + "<p><b>Jefe de Estado:</b> " + jefeEstado + "</p>"
                                + "<p><b>Capital:</b> " + (capital != null ? capital : "N/A") + "</p>"
                                + "</body></html>";

                        // 3. Mostrar el diálogo (la mini ventana)
                        JOptionPane.showMessageDialog(this, detalles, "Información Detallada del País", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(this, "No se encontraron detalles completos para el país seleccionado en la base de datos.", "Error de Datos", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al consultar la base de datos: " + e.getMessage(), "Error SQL", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error inesperado al cargar detalles: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    // Agregar este nuevo método a la clase VistaPaises.java

    private void compararPaises(int[] filasSeleccionadas) {
        DefaultTableModel modelo = (DefaultTableModel) jTable1.getModel();
        List<String> codigos = new ArrayList<>();

        // 1. Recoger los códigos de los países seleccionados
        for (int fila : filasSeleccionadas) {
            // Usar .trim() para seguridad
            codigos.add(modelo.getValueAt(fila, 0).toString().trim());
        }

        Connection miConexion = null;
        try {
            miConexion = conn.Conexion.getConnection();

            // 2. Construir la consulta con 10 campos comparables + Nombre y Código
            String placeholders = String.join(",", Collections.nCopies(codigos.size(), "?"));

            // --- SQL ACTUALIZADO para las 10 métricas del documento ---
            String sql = "SELECT "
                    + "T1.Name, T1.Continent, T1.Region, T1.SurfaceArea, T1.IndepYear, "
                    + "T1.Population, T1.LifeExpectancy, T1.GNP, T1.GovernmentForm, "
                    + "T1.HeadOfState, T2.Name AS CapitalName "
                    + "FROM country T1 "
                    + "LEFT JOIN city T2 ON T1.Capital = T2.ID "
                    + // Para obtener el nombre de la Capital
                    "WHERE T1.Code IN (" + placeholders + ") ORDER BY T1.Name";
            // -----------------------------------------------------------

            try (PreparedStatement pstmt = miConexion.prepareStatement(sql)) {
                // Asignar los códigos a los placeholders
                for (int i = 0; i < codigos.size(); i++) {
                    pstmt.setString(i + 1, codigos.get(i));
                }

                try (ResultSet rs = pstmt.executeQuery()) {

                    StringBuilder tableHtml = new StringBuilder();
                    // Aumentar un poco el ancho para las 10 métricas
                    tableHtml.append("<html><body style='width: 650px; font-family: sans-serif;'>");
                    tableHtml.append("<h2>Comparación de Países Seleccionados</h2>");
                    tableHtml.append("<table border='1' style='width: 100%; border-collapse: collapse; text-align: right;'>");

                    // Mapeo de datos: Clave=Métrica, Valor=Lista de valores por país
                    Map<String, List<String>> resultados = new LinkedHashMap<>();
                    List<String> nombresPaises = new ArrayList<>();

                    // 3. Recoger los resultados y organizar por métrica (10 MÉTTRICAS)
                    while (rs.next()) {
                        nombresPaises.add(rs.getString("Name"));

                        // 1. Población
                        resultados.computeIfAbsent("Población", k -> new ArrayList<>()).add(String.format("%,d", rs.getInt("Population")));

                        // 2. Superficie
                        resultados.computeIfAbsent("Superficie (km²)", k -> new ArrayList<>()).add(String.format("%,.2f", rs.getDouble("SurfaceArea")));

                        // 3. PNB
                        resultados.computeIfAbsent("PNB", k -> new ArrayList<>()).add(String.format("%,.2f", rs.getDouble("GNP")));

                        // 4. Expectativa de Vida (Manejo de NULLs)
                        double lifeExpectancyRaw = rs.getDouble("LifeExpectancy");
                        String lifeExpectancy = rs.wasNull() ? "N/A" : String.format("%,.1f", lifeExpectancyRaw);
                        resultados.computeIfAbsent("Expectativa de Vida (años)", k -> new ArrayList<>()).add(lifeExpectancy);

                        // 5. Continente
                        resultados.computeIfAbsent("Continente", k -> new ArrayList<>()).add(rs.getString("Continent"));

                        // 6. Región
                        resultados.computeIfAbsent("Región", k -> new ArrayList<>()).add(rs.getString("Region"));

                        // 7. Año de Independencia (Manejo de NULLs)
                        Object indepYearObj = rs.getObject("IndepYear");
                        String indepYear = (indepYearObj != null) ? indepYearObj.toString() : "N/A";
                        resultados.computeIfAbsent("Año de Independencia", k -> new ArrayList<>()).add(indepYear);

                        // 8. Forma de Gobierno
                        resultados.computeIfAbsent("Forma de Gobierno", k -> new ArrayList<>()).add(rs.getString("GovernmentForm"));

                        // 9. Jefe de Estado
                        String headOfState = rs.getString("HeadOfState");
                        resultados.computeIfAbsent("Jefe de Estado", k -> new ArrayList<>()).add(headOfState != null && !headOfState.trim().isEmpty() ? headOfState : "N/A");

                        // 10. Capital
                        String capitalName = rs.getString("CapitalName");
                        resultados.computeIfAbsent("Capital", k -> new ArrayList<>()).add(capitalName != null ? capitalName : "N/A");
                    }

                    // 4. Generar encabezados de tabla (Nombres de Países)
                    tableHtml.append("<tr><th style='text-align: left; background-color: #e0f7fa;'>Métrica</th>");
                    for (String nombrePais : nombresPaises) {
                        tableHtml.append("<th style='background-color: #f0f0f0; padding: 5px;'>").append(nombrePais).append("</th>");
                    }
                    tableHtml.append("</tr>");

                    // 5. Generar filas de datos (LISTA COMPLETA DE 10 MÉTRICAS)
                    List<String> metricas = Arrays.asList(
                            "Población",
                            "Superficie (km²)",
                            "PNB",
                            "Expectativa de Vida (años)",
                            "Continente",
                            "Región",
                            "Año de Independencia",
                            "Forma de Gobierno",
                            "Jefe de Estado",
                            "Capital"
                    );

                    for (String metrica : metricas) {
                        tableHtml.append("<tr><td style='text-align: left; background-color: #f9f9f9;'><b>").append(metrica).append("</b></td>");
                        List<String> valores = resultados.getOrDefault(metrica, Collections.emptyList());
                        for (String valor : valores) {
                            tableHtml.append("<td style='background-color: #ffffff; color: #333; padding: 5px;'>").append(valor).append("</td>");
                        }
                        tableHtml.append("</tr>");
                    }

                    tableHtml.append("</table></body></html>");

                    // Mostrar la mini ventana de comparación
                    JOptionPane.showMessageDialog(this, tableHtml.toString(), "Comparación de Países", JOptionPane.INFORMATION_MESSAGE);

                }
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al realizar la comparación en la base de datos: " + e.getMessage(), "Error SQL", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error inesperado al preparar la comparación: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    private void txtcodigoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtcodigoActionPerformed
        // TODO add your handling code here:

    }//GEN-LAST:event_txtcodigoActionPerformed

    private void txtcodigoFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtcodigoFocusGained
        // TODO add your handling code here:
        if (txtcodigo.getText().equals("Ingresa el codigo")) {
            txtcodigo.setText("");
            txtcodigo.setForeground(new Color(0, 0, 0));
        }
    }//GEN-LAST:event_txtcodigoFocusGained

    private void txtcodigoFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtcodigoFocusLost
        if (txtcodigo.getText().equals("")) {
            txtcodigo.setText("Ingresa el codigo");
            txtcodigo.setForeground(new Color(153, 153, 153));
        }
    }//GEN-LAST:event_txtcodigoFocusLost

    private void txtnombreFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtnombreFocusGained

        if (txtnombre.getText().equals("Ingresa el nombre")) {
            txtnombre.setText("");
            txtnombre.setForeground(new Color(0, 0, 0));
        }
    }//GEN-LAST:event_txtnombreFocusGained

    private void txtnombreFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtnombreFocusLost
        // TODO add your handling code here:
        if (txtnombre.getText().equals("")) {
            txtnombre.setText("Ingresa el nombre");
            txtnombre.setForeground(new Color(153, 153, 153));
        }
    }//GEN-LAST:event_txtnombreFocusLost

    private void txtcontinenteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtcontinenteActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtcontinenteActionPerformed

    private void txtcontinenteFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtcontinenteFocusGained
        // TODO add your handling code here:
        if (txtcontinente.getText().equals("Ingresa el continente")) {
            txtcontinente.setText("");
            txtcontinente.setForeground(new Color(0, 0, 0));
        }
    }//GEN-LAST:event_txtcontinenteFocusGained

    private void txtcontinenteFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtcontinenteFocusLost
        if (txtcontinente.getText().equals("")) {
            txtcontinente.setText("Ingresa el continente");
            txtcontinente.setForeground(new Color(153, 153, 153));
        }

    }//GEN-LAST:event_txtcontinenteFocusLost

    private void txtpoblacionFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtpoblacionFocusGained
        // TODO add your handling code here:
        if (txtpoblacion.getText().equals("Ingresa la población")) {
            txtpoblacion.setText("");
            txtpoblacion.setForeground(new Color(0, 0, 0));
        }
    }//GEN-LAST:event_txtpoblacionFocusGained

    private void txtpoblacionFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtpoblacionFocusLost
        if (txtpoblacion.getText().equals("")) {
            txtpoblacion.setText("Ingresa la población");
            txtpoblacion.setForeground(new Color(153, 153, 153));
        }
    }//GEN-LAST:event_txtpoblacionFocusLost

    private void txtpoblacionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtpoblacionActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtpoblacionActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        if (clipMusica != null) {

            if (musicaSonando) {

                clipMusica.stop();

            } else {

                clipMusica.loop(Clip.LOOP_CONTINUOUSLY);

            }

            musicaSonando = !musicaSonando;
        }
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton1ActionPerformed

    private void txtnombreActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtnombreActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtnombreActionPerformed

    private void btnconsultarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnconsultarActionPerformed
        buscarPaises();
    }//GEN-LAST:event_btnconsultarActionPerformed

    private void btnmodificarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnmodificarActionPerformed
        int filaSeleccionada = jTable1.getSelectedRow();

        // 2. Validar que haya una fila seleccionada
        if (filaSeleccionada < 0) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar un país de la tabla para modificar.", "Fila no seleccionada", JOptionPane.WARNING_MESSAGE);
            return; // Salir del método si no hay nada seleccionado
        }

        // 3. Obtener el CÓDIGO ORIGINAL (PK) de la tabla
        //    (Es más seguro que leerlo del textfield, por si el usuario lo cambió)
        DefaultTableModel modelo = (DefaultTableModel) jTable1.getModel();
        String codigoOriginal = modelo.getValueAt(filaSeleccionada, 0).toString();

        // 4. Obtener los NUEVOS valores de los campos de texto
        String codigoNuevo = txtcodigo.getText();
        String nombreNuevo = txtnombre.getText();
        String continenteNuevo = txtcontinente.getText();
        String poblacionNueva = txtpoblacion.getText();

        // 5. Validar que los campos no estén vacíos (con los placeholders)
        if (codigoNuevo.isEmpty() || codigoNuevo.equals("Ingresa el codigo")
                || nombreNuevo.isEmpty() || nombreNuevo.equals("Ingresa el nombre")
                || continenteNuevo.isEmpty() || continenteNuevo.equals("Ingresa el continente")
                || poblacionNueva.isEmpty() || poblacionNueva.equals("Ingresa la población")) {

            JOptionPane.showMessageDialog(this, "Por favor, rellena todos los campos.", "Campos vacíos", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // --- INICIA LA LÓGICA DE BASE DE DATOS ---
        Connection miConexion = null;
        try {
            // 6. Obtener la conexión
            miConexion = Conexion.getConnection();

            // 7. Preparar la consulta SQL UPDATE
            //    (Usamos los nombres de columna de tu BD: Code, Name, Continent, Population)
            //    Esto te permite modificar todos los campos, incluso el código (PK)
            String sql = "UPDATE country SET Code = ?, Name = ?, Continent = ?, Population = ? WHERE Code = ?";

            try (java.sql.PreparedStatement pstmt = miConexion.prepareStatement(sql)) {

                // 8. Asignar los NUEVOS valores (columnas SET)
                pstmt.setString(1, codigoNuevo);
                pstmt.setString(2, nombreNuevo);
                pstmt.setString(3, continenteNuevo);
                pstmt.setInt(4, Integer.parseInt(poblacionNueva));

                // 9. Asignar el CÓDIGO ORIGINAL (columna WHERE)
                //    Así sabe qué fila actualizar
                pstmt.setString(5, codigoOriginal);

                // 10. Ejecutar la modificación
                int filasAfectadas = pstmt.executeUpdate();

                // 11. Verificar el resultado
                if (filasAfectadas > 0) {
                    JOptionPane.showMessageDialog(this, "¡País modificado exitosamente en la BD!", "Éxito", JOptionPane.INFORMATION_MESSAGE);

                    // 12. Refrescar la tabla (llamando al método que ya creamos)
                    buscarPaises();

                    // 13. Limpiar los campos (como en tu código original)
                    txtcodigo.setText("Ingresa el codigo");
                    txtcodigo.setForeground(new Color(153, 153, 153));
                    txtnombre.setText("Ingresa el nombre");
                    txtnombre.setForeground(new Color(153, 153, 153));
                    txtcontinente.setText("Ingresa el continente");
                    txtcontinente.setForeground(new Color(153, 153, 153));
                    txtpoblacion.setText("Ingresa la población");
                    txtpoblacion.setForeground(new Color(153, 153, 153));

                } else {
                    JOptionPane.showMessageDialog(this, "No se encontró el país para modificar (pudo ser borrado por otro usuario).", "Error", JOptionPane.WARNING_MESSAGE);
                }
            }

        } catch (SQLException e) {
            // Error de SQL (ej: código duplicado, tipo de dato incorrecto)
            String mensajeError = e.getMessage();
            if (mensajeError.contains("Duplicate entry")) {
                JOptionPane.showMessageDialog(this, "Error: El nuevo código '" + codigoNuevo + "' ya existe en la BD.", "Error de Duplicado", JOptionPane.ERROR_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Error al modificar en la base de datos: " + mensajeError, "Error SQL", JOptionPane.ERROR_MESSAGE);
            }
            e.printStackTrace();
        } catch (NumberFormatException e) {
            // Error si la población no es un número
            JOptionPane.showMessageDialog(this, "La población debe ser un número entero válido.", "Error de Formato", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            // Cualquier otro error (ej: conexión)
            JOptionPane.showMessageDialog(this, "Error inesperado: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }

    }//GEN-LAST:event_btnmodificarActionPerformed

    private void txtcodigoKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtcodigoKeyTyped
        String texto = txtcodigo.getText();
        if (texto.length() >= 3) {
            evt.consume();
        }
    }//GEN-LAST:event_txtcodigoKeyTyped

    private void txtpoblacionKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtpoblacionKeyTyped
        char n = evt.getKeyChar();
        if (n < '0' || n > '9') {
            evt.consume();
        }

    }//GEN-LAST:event_txtpoblacionKeyTyped

    private void txtnombreKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtnombreKeyTyped


    }//GEN-LAST:event_txtnombreKeyTyped

    private void jPanel1MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanel1MousePressed
        xMouse = evt.getX();
        yMouse = evt.getY();
    }//GEN-LAST:event_jPanel1MousePressed

    private void jPanel1MouseDragged(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanel1MouseDragged
        int x = evt.getXOnScreen();
        int y = evt.getYOnScreen();
        this.setLocation(x - xMouse, y - yMouse);        // TODO add your handling code here:
    }//GEN-LAST:event_jPanel1MouseDragged

    private void btnCerrarSesionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCerrarSesionActionPerformed
        Object[] opciones = {"Aceptar", "Cancelar"};

        // 2. Mostrar un cuadro de diálogo con las opciones personalizadas
        int respuesta = javax.swing.JOptionPane.showOptionDialog(
                this, // El componente padre (esta misma ventana)
                "¿Estás seguro de que deseas cerrar la sesión?", // El mensaje a mostrar
                "Confirmar Cierre de Sesión", // El título de la ventana
                javax.swing.JOptionPane.YES_NO_OPTION, // El tipo de opción
                javax.swing.JOptionPane.QUESTION_MESSAGE, // El tipo de mensaje (icono)
                null, // No usamos un icono personalizado
                opciones, // ¡Aquí pasamos nuestro array con los textos "Sí" y "No"!
                opciones[0] // El botón que aparecerá seleccionado por defecto ("Sí")
        );

        if (respuesta == javax.swing.JOptionPane.YES_OPTION) {

            if (clipMusica != null && clipMusica.isRunning()) {
                clipMusica.stop();
            }

            VistaLogin vistaLogin = new VistaLogin();
            vistaLogin.setVisible(true);

            this.dispose();
        }
    }//GEN-LAST:event_btnCerrarSesionActionPerformed

    private void extBtnKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_extBtnKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_extBtnKeyPressed

    private void extBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_extBtnActionPerformed
        System.exit(0);// TODO add your handling code here:
    }//GEN-LAST:event_extBtnActionPerformed

    private void minBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_minBtnActionPerformed
        this.setExtendedState(JFrame.ICONIFIED);            // TODO add your handling code here:
    }//GEN-LAST:event_minBtnActionPerformed

    private void btnIdiomasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnIdiomasActionPerformed
        VistaIdiomas vistaIdiomas = new VistaIdiomas();

        // 2. Hacerla visible
        vistaIdiomas.setVisible(true);

        // 3. (Importante) Cerrar esta ventana actual (VistaPaises)
        this.dispose();         // TODO add your handling code here:
    }//GEN-LAST:event_btnIdiomasActionPerformed

    private void btnPaisesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPaisesActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnPaisesActionPerformed

    private void btnCuidadesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCuidadesActionPerformed
        VistaCuidades vistaCiudades = new VistaCuidades();

        // 2. Hacerla visible
        vistaCiudades.setVisible(true);

        // 3. (Importante) Cerrar esta ventana actual (VistaPaises)
        this.dispose();         // TODO add your handling code here:
    }//GEN-LAST:event_btnCuidadesActionPerformed

    private void btnVerDetallesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnVerDetallesActionPerformed
        int[] selectedRows = jTable1.getSelectedRows();

        if (selectedRows.length == 1) {
            // Comportamiento original: Mostrar detalles individuales
            mostrarDetallesPaisSeleccionado();
        } else if (selectedRows.length >= 2 && selectedRows.length <= 3) {
            // Nuevo comportamiento: Comparar 2 o 3 países
            compararPaises(selectedRows);
        } else {
            // Advertencia si la selección no es válida
            JOptionPane.showMessageDialog(this,
                    "Debe seleccionar entre 2 y 3 países para realizar una comparación.",
                    "Selección Inválida",
                    JOptionPane.WARNING_MESSAGE);
        }
    }//GEN-LAST:event_btnVerDetallesActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCerrarSesion;
    private javax.swing.JButton btnCuidades;
    private javax.swing.JButton btnIdiomas;
    private javax.swing.JButton btnPaises;
    private javax.swing.JButton btnVerDetalles;
    private javax.swing.JButton btnagregar;
    private javax.swing.JButton btnconsultar;
    private javax.swing.JButton btnmodificar;
    private javax.swing.JButton extBtn;
    private javax.swing.JButton jButton1;
    private javax.swing.JDesktopPane jDesktopPane1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JList<String> jList1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
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
