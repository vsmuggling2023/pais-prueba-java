/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package vista;

import Dao.PaisesDao;
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
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
// Importa las clases necesarias de SQL
import java.util.ArrayList;
import modelo.Pais;
import java.util.List;

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
        this.setSize(1220, 600);
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

                    // *** AÑADIR ESTA VERIFICACIÓN: Solo rellenar si es el modelo estándar de 4 columnas ***
                    if (modelo.getColumnCount() >= 4) {

                        // Cargar datos de 4 columnas (Modo Normal: Código, Nombre, Continente, Población)
                        txtcodigo.setText(modelo.getValueAt(filaSeleccionada, 0).toString());
                        txtnombre.setText(modelo.getValueAt(filaSeleccionada, 1).toString());
                        txtcontinente.setText(modelo.getValueAt(filaSeleccionada, 2).toString());
                        txtpoblacion.setText(modelo.getValueAt(filaSeleccionada, 3).toString());

                        txtcodigo.setForeground(Color.black);
                        txtnombre.setForeground(Color.black);
                        txtcontinente.setForeground(Color.black);
                        txtpoblacion.setForeground(Color.black);
                    }
                    // Si la tabla tiene menos de 4 columnas (el reporte de 3 columnas), no se hace nada.
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
        // (Toda la parte inicial igual...)
        DefaultTableModel modelo = new DefaultTableModel(
                new Object[]{"Codigo", "Nombre", "Continente", "Poblacion"}, 0
        );

        String codigo = txtcodigo.getText();
        String nombre = txtnombre.getText();
        String continente = txtcontinente.getText();
        String poblacion = txtpoblacion.getText();

        Dao.PaisesDao dao = new Dao.PaisesDao();
        List<modelo.Pais> lista = dao.listarPaises(codigo, nombre, continente, poblacion);

        // *** AQUÍ ESTÁ LA CORRECCIÓN ***
        for (modelo.Pais p : lista) {
            modelo.addRow(new Object[]{
                p.getCode(), // Antes: p.getCodigo()
                p.getName(), // Antes: p.getNombre()
                p.getContinent(), // Antes: p.getContinente()
                p.getPopulation() // Antes: p.getPoblacion()
            });
        }

        System.out.println("Se procesaron " + lista.size() + " objetos Pais.");
        jTable1.setModel(modelo);
        personalizarTablaEstiloFrutiger();
    }

    public class EjemploConsulta {

        public void consultarPaises() {

            Connection miConexion = Conexion.getConnection();

            if (miConexion != null) {

                String sql = "SELECT Name, Continent, Population FROM country WHERE Continent = 'South America'";

                try (Statement stmt = miConexion.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {

                    System.out.println("--- Países de Sudamérica en la BD 'world' ---");

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

        btnOrdenarIndep = new javax.swing.JRadioButton();
        btnMostarCapitales = new javax.swing.JButton();
        btnVerDetalles = new javax.swing.JButton();
        btnVerIdiomas = new javax.swing.JButton();
        jLabel9 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        minBtn = new javax.swing.JButton();
        extBtn = new javax.swing.JButton();
        btnCerrarSesion = new javax.swing.JButton();
        btnPaises = new javax.swing.JButton();
        btnCuidades = new javax.swing.JButton();
        btnIdiomas = new javax.swing.JButton();
        btnVerCuidades = new javax.swing.JButton();
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

        btnOrdenarIndep.setText("Ordenar por año");
        btnOrdenarIndep.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnOrdenarIndepActionPerformed(evt);
            }
        });
        getContentPane().add(btnOrdenarIndep, new org.netbeans.lib.awtextra.AbsoluteConstraints(1060, 90, 120, -1));

        btnMostarCapitales.setText("Mostrar Capitales");
        btnMostarCapitales.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMostarCapitalesActionPerformed(evt);
            }
        });
        getContentPane().add(btnMostarCapitales, new org.netbeans.lib.awtextra.AbsoluteConstraints(1060, 270, 130, -1));

        btnVerDetalles.setText("Detalles");
        btnVerDetalles.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnVerDetallesActionPerformed(evt);
            }
        });
        getContentPane().add(btnVerDetalles, new org.netbeans.lib.awtextra.AbsoluteConstraints(400, 90, 80, -1));

        btnVerIdiomas.setText("Ver Idiomas");
        btnVerIdiomas.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnVerIdiomasActionPerformed(evt);
            }
        });
        getContentPane().add(btnVerIdiomas, new org.netbeans.lib.awtextra.AbsoluteConstraints(1060, 220, 130, -1));

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
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 482, Short.MAX_VALUE)
                .addComponent(btnPaises)
                .addGap(88, 88, 88)
                .addComponent(btnCuidades)
                .addGap(85, 85, 85)
                .addComponent(btnIdiomas)
                .addGap(122, 122, 122)
                .addComponent(minBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(extBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(btnCerrarSesion, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(minBtn, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(extBtn, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btnPaises, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btnCuidades, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btnIdiomas, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1220, 50));

        btnVerCuidades.setText("Ver Cuidades");
        btnVerCuidades.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnVerCuidadesActionPerformed(evt);
            }
        });
        getContentPane().add(btnVerCuidades, new org.netbeans.lib.awtextra.AbsoluteConstraints(1060, 170, 130, -1));

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

        getContentPane().add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(490, 90, 560, 399));

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
        getContentPane().add(btnconsultar, new org.netbeans.lib.awtextra.AbsoluteConstraints(560, 490, 140, 50));

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
        getContentPane().add(btnmodificar, new org.netbeans.lib.awtextra.AbsoluteConstraints(840, 490, 130, 50));

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
        getContentPane().add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(1120, 490, 100, 110));

        jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel7.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/FOndooo.png"))); // NOI18N
        jLabel7.setText("jLabel7");
        getContentPane().add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(290, 50, 930, 620));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnagregarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnagregarActionPerformed
        String codigo = txtcodigo.getText();
        String nombre = txtnombre.getText();
        String continente = txtcontinente.getText();
        String poblacion = txtpoblacion.getText();

        // Validaciones visuales (se mantienen igual)
        if (codigo.isEmpty() || codigo.equals("Ingresa el codigo")
                || nombre.isEmpty() || nombre.equals("Ingresa el nombre")
                || continente.isEmpty() || continente.equals("Ingresa el continente")
                || poblacion.isEmpty() || poblacion.equals("Ingresa la población")) {
            JOptionPane.showMessageDialog(this, "Por favor, rellena todos los campos.", "Campos vacíos", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // --- USAR DAO ---
        // Crear objeto con los datos
        Pais nuevoPais = new Pais(nombre, continente, poblacion, codigo);
        PaisesDao dao = new PaisesDao();

        boolean exito = dao.agregarPais(nuevoPais);

        if (exito) {
            JOptionPane.showMessageDialog(this, "¡País agregado exitosamente!", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            buscarPaises(); // Recargar tabla

            // Limpiar campos (Tu lógica de limpieza visual se mantiene aquí)
            txtcodigo.setText("Ingresa el codigo");
            txtcodigo.setForeground(new Color(153, 153, 153));
            // ... limpiar el resto ...
        } else {
            JOptionPane.showMessageDialog(this, "No se pudo agregar el país (Verifica que el código no esté duplicado o la población sea número).", "Error", JOptionPane.WARNING_MESSAGE);
        }

    }//GEN-LAST:event_btnagregarActionPerformed
    private void mostrarDetallesPaisSeleccionado() {
        int filaSeleccionada = jTable1.getSelectedRow();
        if (filaSeleccionada < 0) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar un país.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        DefaultTableModel modelo = (DefaultTableModel) jTable1.getModel();
        String codigoPais = modelo.getValueAt(filaSeleccionada, 0).toString().trim();

        // --- USAR DAO ---
        PaisesDao dao = new PaisesDao();
        Pais p = dao.obtenerDetallesPais(codigoPais); // Recupera el objeto completo

        if (p != null) {
            // Construir HTML solo con los getters del objeto Pais
            String detalles = "<html><body style='width: 300px; font-family: sans-serif;'>"
                    + "<h2>Detalles de " + p.getName() + " (" + p.getCode() + ")</h2>"
                    + "<hr>"
                    + "<p><b>Continente:</b> " + p.getContinent() + "</p>"
                    + "<p><b>Región:</b> " + p.getRegion() + "</p>"
                    + "<p><b>Superficie:</b> " + p.getSurfaceArea() + " km²</p>"
                    + "<p><b>Independencia:</b> " + p.getIndepYear() + "</p>"
                    + "<p><b>Población:</b> " + p.getPopulation() + "</p>"
                    + "<p><b>Exp. Vida:</b> " + p.getLifeExpectancy() + " años</p>"
                    + "<p><b>PNB:</b> " + p.getGnp() + "</p>"
                    + "<p><b>Gobierno:</b> " + p.getGovernmentForm() + "</p>"
                    + "<p><b>Jefe Estado:</b> " + (p.getHeadOfState() != null ? p.getHeadOfState() : "N/A") + "</p>"
                    + "<p><b>Capital:</b> " + (p.getCapitalName() != null ? p.getCapitalName() : "N/A") + "</p>"
                    + "</body></html>";

            JOptionPane.showMessageDialog(this, detalles, "Detalles del País", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "No se encontraron detalles.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    // Agregar este nuevo método a la clase VistaPaises.java

    private void compararPaises(int[] filasSeleccionadas) {
        DefaultTableModel modeloTabla = (DefaultTableModel) jTable1.getModel();
        List<String> codigos = new ArrayList<>();
        for (int fila : filasSeleccionadas) {
            codigos.add(modeloTabla.getValueAt(fila, 0).toString().trim());
        }

        // --- USAR DAO ---
        Dao.PaisesDao dao = new Dao.PaisesDao();
        List<modelo.Pais> paises = dao.obtenerPaisesPorCodigos(codigos);

        // Construir HTML
        StringBuilder html = new StringBuilder("<html><body style='width: 700px; font-family: sans-serif;'>");
        html.append("<h2>Comparación de Países</h2>");
        html.append("<table border='1' style='width:100%; border-collapse:collapse; text-align:left;'>");

        // --- ENCABEZADOS (Nombres de los países) ---
        html.append("<tr style='background-color:#e0f7fa;'>");
        html.append("<th style='padding:8px;'>Métrica</th>");
        for (modelo.Pais p : paises) {
            html.append("<th style='padding:8px;'>").append(p.getName()).append("</th>");
        }
        html.append("</tr>");

        // --- LAS 10 MÉTRICAS ---
        agregarFilaComparacion(html, "Población", paises, p -> p.getPopulation());
        agregarFilaComparacion(html, "Superficie (km²)", paises, p -> p.getSurfaceArea());
        agregarFilaComparacion(html, "PNB (GNP)", paises, p -> p.getGnp());
        agregarFilaComparacion(html, "Exp. Vida (años)", paises, p -> p.getLifeExpectancy());
        agregarFilaComparacion(html, "Continente", paises, p -> p.getContinent());
        agregarFilaComparacion(html, "Región", paises, p -> p.getRegion());
        agregarFilaComparacion(html, "Año Independencia", paises, p -> p.getIndepYear());
        agregarFilaComparacion(html, "Forma de Gobierno", paises, p -> p.getGovernmentForm());
        agregarFilaComparacion(html, "Jefe de Estado", paises, p -> p.getHeadOfState());
        agregarFilaComparacion(html, "Capital", paises, p -> p.getCapitalName());

        html.append("</table></body></html>");
        JOptionPane.showMessageDialog(this, html.toString(), "Comparación", JOptionPane.INFORMATION_MESSAGE);
    }

    // Helper pequeño para no repetir código HTML en la vista
    private void agregarFilaComparacion(StringBuilder sb, String titulo, List<modelo.Pais> paises, java.util.function.Function<modelo.Pais, String> getter) {
        sb.append("<tr>");
        // Columna del título de la métrica
        sb.append("<td style='background-color:#f0f0f0; padding:6px; font-weight:bold;'>").append(titulo).append("</td>");

        // Columnas con los valores de cada país
        for (modelo.Pais p : paises) {
            String valor = getter.apply(p);
            sb.append("<td style='padding:6px;'>").append(valor != null ? valor : "N/A").append("</td>");
        }
        sb.append("</tr>");
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

        if (filaSeleccionada < 0) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar un país de la tabla para modificar.", "Fila no seleccionada", JOptionPane.WARNING_MESSAGE);
            return;
        }

        DefaultTableModel modelo = (DefaultTableModel) jTable1.getModel();
        String codigoOriginal = modelo.getValueAt(filaSeleccionada, 0).toString();

        String codigoNuevo = txtcodigo.getText();
        String nombreNuevo = txtnombre.getText();
        String continenteNuevo = txtcontinente.getText();
        String poblacionNueva = txtpoblacion.getText();

        // Validaciones visuales (se mantienen igual)
        if (codigoNuevo.isEmpty() || codigoNuevo.equals("Ingresa el codigo") /* ... resto de validaciones ... */) {
            JOptionPane.showMessageDialog(this, "Por favor, rellena todos los campos.", "Campos vacíos", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // --- USAR DAO ---
        Pais paisModificado = new Pais(nombreNuevo, continenteNuevo, poblacionNueva, codigoNuevo);
        PaisesDao dao = new PaisesDao();

        boolean exito = dao.modificarPais(paisModificado, codigoOriginal);

        if (exito) {
            JOptionPane.showMessageDialog(this, "¡País modificado exitosamente!", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            buscarPaises();
            // Limpiar campos...
        } else {
            JOptionPane.showMessageDialog(this, "No se pudo modificar el país (Verifica datos o duplicados).", "Error", JOptionPane.WARNING_MESSAGE);
        }

    }//GEN-LAST:event_btnmodificarActionPerformed
// Agregue este nuevo método a la clase VistaPaises.java

    private void ordenarPaisesPorIndependencia() {
        PaisesDao dao = new PaisesDao();
        List<Pais> lista = dao.listarPorIndependencia();

        DefaultTableModel modelo = new DefaultTableModel(
                new Object[]{"Codigo", "Nombre", "Continente", "Poblacion"}, 0
        );

        for (Pais p : lista) {
            modelo.addRow(new Object[]{p.getCode(), p.getName(), p.getContinent(), p.getPopulation()});
        }

        jTable1.setModel(modelo);
        personalizarTablaEstiloFrutiger();
        JOptionPane.showMessageDialog(this, "Tabla ordenada por Año de Independencia.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
    }

    private void mostrarCapitalesPorContinente() {
        PaisesDao dao = new PaisesDao();
        List<String[]> lista = dao.listarCapitalesPorContinente();

        DefaultTableModel modelo = new DefaultTableModel(
                new Object[]{"Continente", "País", "Capital"}, 0
        );

        for (String[] fila : lista) {
            modelo.addRow(fila);
        }

        jTable1.setModel(modelo);
        personalizarTablaEstiloFrutiger();
        JOptionPane.showMessageDialog(this, "Reporte de Capitales cargado.", "Reporte Generado", JOptionPane.INFORMATION_MESSAGE);
    }
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

    private void btnVerCuidadesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnVerCuidadesActionPerformed
        int filaSeleccionada = jTable1.getSelectedRow();

        if (filaSeleccionada < 0) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar un país para ver sus ciudades.", "País no seleccionado", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Obtener el código de país (PK) de la fila seleccionada
        DefaultTableModel modelo = (DefaultTableModel) jTable1.getModel();
        String codigoPais = modelo.getValueAt(filaSeleccionada, 0).toString().trim();

        // Crear y mostrar la nueva ventana de ciudades con el filtro
        VistaCuidades vistaCiudades = new VistaCuidades(codigoPais); // <-- LLAMADA CORRECTA
        vistaCiudades.setVisible(true);

        // Cerrar la ventana actual
        this.dispose();

    }//GEN-LAST:event_btnVerCuidadesActionPerformed

    private void btnVerIdiomasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnVerIdiomasActionPerformed

        int filaSeleccionada = jTable1.getSelectedRow();

        if (filaSeleccionada < 0) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar un país para ver sus idiomas.", "País no seleccionado", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Obtener el código de país (PK) de la fila seleccionada
        DefaultTableModel modelo = (DefaultTableModel) jTable1.getModel();
        // Columna 0 es el Código, usar trim() es crucial.
        String codigoPais = modelo.getValueAt(filaSeleccionada, 0).toString().trim();

        // Crear y mostrar la nueva ventana de idiomas con el filtro
        VistaIdiomas vistaIdiomas = new VistaIdiomas(codigoPais); // <-- Usa el nuevo constructor
        vistaIdiomas.setVisible(true);

        // Cerrar la ventana actual
        this.dispose();

    }//GEN-LAST:event_btnVerIdiomasActionPerformed

    private void btnOrdenarIndepActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnOrdenarIndepActionPerformed
        ordenarPaisesPorIndependencia();        // TODO add your handling code here:
    }//GEN-LAST:event_btnOrdenarIndepActionPerformed

    private void btnMostarCapitalesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMostarCapitalesActionPerformed
        mostrarCapitalesPorContinente();
    }//GEN-LAST:event_btnMostarCapitalesActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCerrarSesion;
    private javax.swing.JButton btnCuidades;
    private javax.swing.JButton btnIdiomas;
    private javax.swing.JButton btnMostarCapitales;
    private javax.swing.JRadioButton btnOrdenarIndep;
    private javax.swing.JButton btnPaises;
    private javax.swing.JButton btnVerCuidades;
    private javax.swing.JButton btnVerDetalles;
    private javax.swing.JButton btnVerIdiomas;
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
