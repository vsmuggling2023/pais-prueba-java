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
public class VistaIdiomas extends javax.swing.JFrame {

    /**
     * Creates new form VistaPaises
     */
    int xMouse, yMouse;
    private int papulandiaClickCount = 0;

    public VistaIdiomas() {
        // Constructor por defecto restaurado: delega la llamada sin filtro
        this(null);
    }

    public VistaIdiomas(String countryCode) {
        initComponents();
        setTitle("Lista de Idiomas"); // Título corregido
        this.setLocationRelativeTo(null);
        this.setResizable(false);
        this.setSize(1220, 600);
        cargarMusicaDeFondo();
        setIconImage(new javax.swing.ImageIcon(getClass().getResource("/icons/Papulandia2.png")).getImage());
        personalizarTablaEstiloFrutiger();
        establecerCursorPersonalizado();
        this.getRootPane().setDefaultButton(btnagregar);
        setPlaceholder(txtcodigo, "Ingresa el código de país");
        setPlaceholder(txtnombre, "Ingresa el idioma");
        setPlaceholder(txtcontinente, "Ingresa T o F (Oficial)");
        setPlaceholder(txtpoblacion, "Ingresa el porcentaje");

        jTable1.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent event) {
                int filaSeleccionada = jTable1.getSelectedRow();
                if (filaSeleccionada >= 0) {
                    DefaultTableModel modelo = (DefaultTableModel) jTable1.getModel();

                    // *** AÑADIR ESTA VERIFICACIÓN: Solo intentar rellenar los campos si la tabla tiene 4 o más columnas (el modo normal) ***
                    if (modelo.getColumnCount() >= 4) {

                        // Cargar datos de 4 columnas (Modo Normal: Código, Idioma, Oficial, Porcentaje)
                        txtcodigo.setText(modelo.getValueAt(filaSeleccionada, 0).toString());
                        txtnombre.setText(modelo.getValueAt(filaSeleccionada, 1).toString());
                        txtcontinente.setText(modelo.getValueAt(filaSeleccionada, 2).toString());
                        txtpoblacion.setText(modelo.getValueAt(filaSeleccionada, 3).toString());

                        txtcodigo.setForeground(Color.black);
                        txtnombre.setForeground(Color.black);
                        txtcontinente.setForeground(Color.black);
                        txtpoblacion.setForeground(Color.black);

                    }
                    // Si la tabla tiene menos de 4 columnas (es el reporte de 2 columnas), no se hace nada, 
                    // ya que los campos de filtro no son aplicables a ese reporte.
                }
            }
        });

        // Lógica de filtro para el nuevo constructor
        if (countryCode != null) {
            // 1. Rellena el campo de filtro Código País (txtcodigo) con el código recibido.
            txtcodigo.setText(countryCode);
            // 2. Quita el color de placeholder (gris) para que el filtro sea activo.
            txtcodigo.setForeground(Color.black);
        }

        // La búsqueda se ejecuta al final, usando el filtro que acabamos de establecer
        buscarIdiomas();
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
    private void buscarIdiomas() {
        DefaultTableModel modelo = new DefaultTableModel(
                new Object[]{"CodigoPais", "Idioma", "EsOficial", "Porcentaje"}, 0
        );

        String codigo = txtcodigo.getText();
        String nombre = txtnombre.getText();
        String oficial = txtcontinente.getText();
        String poblacion = txtpoblacion.getText();

        // --- USAR DAO ---
        Dao.IdiomasDao dao = new Dao.IdiomasDao();
        java.util.List<modelo.Idioma> lista = dao.listarIdiomas(codigo, nombre, oficial, poblacion);

        for (modelo.Idioma i : lista) {
            modelo.addRow(new Object[]{
                i.getCountryCode(),
                i.getLanguage(),
                i.getIsOfficial(),
                i.getPercentage()
            });
        }

        if (lista.isEmpty()) {
            System.out.println("No se encontraron resultados.");
        } else {
            System.out.println(lista.size() + " idiomas cargados.");
        }

        jTable1.setModel(modelo);
        personalizarTablaEstiloFrutiger();
    }

    private void mostrarIdiomasMasHablados() {
        DefaultTableModel modelo = new DefaultTableModel(
                new Object[]{"Idioma", "Hablantes Estimados"}, 0
        );

        // --- USAR DAO ---
        Dao.IdiomasDao dao = new Dao.IdiomasDao();
        java.util.List<modelo.Idioma> lista = dao.listarIdiomasMasHablados();

        for (modelo.Idioma i : lista) {
            modelo.addRow(new Object[]{
                i.getLanguage(),
                i.getTotalSpeakers()
            });
        }

        if (lista.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No se encontraron datos.", "Error", JOptionPane.ERROR_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "Reporte cargado con éxito.", "Reporte", JOptionPane.INFORMATION_MESSAGE);
        }

        jTable1.setModel(modelo);
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

        btnIdiomasMasHablado = new javax.swing.JCheckBox();
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

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setUndecorated(true);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        btnIdiomasMasHablado.setText("Idioma mas Hablado");
        btnIdiomasMasHablado.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnIdiomasMasHabladoActionPerformed(evt);
            }
        });
        getContentPane().add(btnIdiomasMasHablado, new org.netbeans.lib.awtextra.AbsoluteConstraints(1060, 80, 140, -1));

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
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 466, Short.MAX_VALUE)
                .addComponent(btnPaises)
                .addGap(86, 86, 86)
                .addComponent(btnCuidades, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(59, 59, 59)
                .addComponent(btnIdiomas, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(118, 118, 118)
                .addComponent(minBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(extBtn))
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
                            .addComponent(btnIdiomas, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btnCuidades, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btnPaises, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1220, 50));

        jLabel2.setText("Código País");
        getContentPane().add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 200, -1, -1));

        jLabel3.setText("Idioma");
        getContentPane().add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 250, -1, -1));

        jLabel4.setText("Oficial (T/F)");
        getContentPane().add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 310, -1, -1));

        jLabel5.setText("Porcentaje");
        getContentPane().add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 370, -1, -1));

        txtcodigo.setForeground(new java.awt.Color(153, 153, 153));
        txtcodigo.setText("Ingresa el código de país");
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
        txtcontinente.setText("Ingresa el idioma");
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
        txtnombre.setText("Ingresa T o F (Oficial)");
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
        txtpoblacion.setText("Ingresa el porcentaje");
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

        getContentPane().add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(490, 80, 560, 399));

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
        getContentPane().add(btnconsultar, new org.netbeans.lib.awtextra.AbsoluteConstraints(550, 490, 140, 50));

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
        getContentPane().add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(1120, 500, 100, 110));

        jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel7.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/FOndooo.png"))); // NOI18N
        jLabel7.setText("jLabel7");
        getContentPane().add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(290, 40, 930, 620));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnagregarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnagregarActionPerformed
        String codigo = txtcodigo.getText();
        String idioma = txtnombre.getText();
        String oficial = txtcontinente.getText();
        String porcentaje = txtpoblacion.getText();

        // (Tus validaciones de campos vacíos y longitud se mantienen igual aquí...)
        // ...
        // if (codigo.isEmpty() ... ) return;
        try {
            double porc = Double.parseDouble(porcentaje);

            // --- USAR DAO ---
            modelo.Idioma nuevoIdioma = new modelo.Idioma(codigo, idioma, oficial, porc);
            Dao.IdiomasDao dao = new Dao.IdiomasDao();

            if (dao.agregarIdioma(nuevoIdioma)) {
                JOptionPane.showMessageDialog(this, "¡Idioma agregado!", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                buscarIdiomas();
                // Limpiar campos...
                txtcodigo.setText("Ingresa el código de país");
                txtcodigo.setForeground(new Color(153, 153, 153));
                // ... (resto de limpieza)
            } else {
                JOptionPane.showMessageDialog(this, "No se pudo agregar (posible duplicado).", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Porcentaje inválido.", "Error", JOptionPane.ERROR_MESSAGE);
        }

    }//GEN-LAST:event_btnagregarActionPerformed

    private void txtcodigoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtcodigoActionPerformed
        // TODO add your handling code here:

    }//GEN-LAST:event_txtcodigoActionPerformed

    private void txtcodigoFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtcodigoFocusGained

    }//GEN-LAST:event_txtcodigoFocusGained

    private void txtcodigoFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtcodigoFocusLost

    }//GEN-LAST:event_txtcodigoFocusLost

    private void txtnombreFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtnombreFocusGained

    }//GEN-LAST:event_txtnombreFocusGained

    private void txtnombreFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtnombreFocusLost
        // TODO add your handling code here:

    }//GEN-LAST:event_txtnombreFocusLost

    private void txtcontinenteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtcontinenteActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtcontinenteActionPerformed

    private void txtcontinenteFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtcontinenteFocusGained

    }//GEN-LAST:event_txtcontinenteFocusGained

    private void txtcontinenteFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtcontinenteFocusLost


    }//GEN-LAST:event_txtcontinenteFocusLost

    private void txtpoblacionFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtpoblacionFocusGained

    }//GEN-LAST:event_txtpoblacionFocusGained

    private void txtpoblacionFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtpoblacionFocusLost

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
        buscarIdiomas();
    }//GEN-LAST:event_btnconsultarActionPerformed

    private void btnmodificarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnmodificarActionPerformed

        int fila = jTable1.getSelectedRow();
        if (fila < 0) {
            JOptionPane.showMessageDialog(this, "Selecciona un idioma.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        DefaultTableModel modelo = (DefaultTableModel) jTable1.getModel();
        String oldCode = modelo.getValueAt(fila, 0).toString();
        String oldLang = modelo.getValueAt(fila, 1).toString();

        String newCode = txtcodigo.getText();
        String newLang = txtnombre.getText();
        String newOfficial = txtcontinente.getText();
        String newPerc = txtpoblacion.getText();

        // (Validaciones visuales se mantienen...)
        try {
            double porc = Double.parseDouble(newPerc);

            // --- USAR DAO ---
            modelo.Idioma idiomaModificado = new modelo.Idioma(newCode, newLang, newOfficial, porc);
            Dao.IdiomasDao dao = new Dao.IdiomasDao();

            if (dao.modificarIdioma(idiomaModificado, oldCode, oldLang)) {
                JOptionPane.showMessageDialog(this, "¡Idioma modificado!", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                buscarIdiomas();
                // Limpiar campos...
            } else {
                JOptionPane.showMessageDialog(this, "Error al modificar.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Porcentaje inválido.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btnmodificarActionPerformed

    private void txtcodigoKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtcodigoKeyTyped
        String texto = txtcodigo.getText();
        if (texto.length() >= 3) { // El código de país solo tiene 3 caracteres
            evt.consume();
        }
    }//GEN-LAST:event_txtcodigoKeyTyped

    private void txtpoblacionKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtpoblacionKeyTyped
        char n = evt.getKeyChar();
        // Permite dígitos y un punto decimal para el porcentaje
        if ((n < '0' || n > '9') && n != '.') {
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
        // TODO add your handling code here:
    }//GEN-LAST:event_btnIdiomasActionPerformed

    private void btnPaisesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPaisesActionPerformed
        VistaPaises vistaPaises = new VistaPaises();

        // 2. Hacerla visible
        vistaPaises.setVisible(true);

        // 3. (Importante) Cerrar esta ventana actual (VistaPaises)
        this.dispose();    // TODO add your handling code here:
    }//GEN-LAST:event_btnPaisesActionPerformed

    private void btnCuidadesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCuidadesActionPerformed
        VistaCuidades vistaCiudades = new VistaCuidades();

        // 2. Hacerla visible
        vistaCiudades.setVisible(true);

        // 3. (Importante) Cerrar esta ventana actual (VistaPaises)
        this.dispose();         // TODO add your handling code here:
    }//GEN-LAST:event_btnCuidadesActionPerformed

    private void btnIdiomasMasHabladoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnIdiomasMasHabladoActionPerformed
        mostrarIdiomasMasHablados();
    }//GEN-LAST:event_btnIdiomasMasHabladoActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCerrarSesion;
    private javax.swing.JButton btnCuidades;
    private javax.swing.JButton btnIdiomas;
    private javax.swing.JCheckBox btnIdiomasMasHablado;
    private javax.swing.JButton btnPaises;
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
