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
public class VistaCuidades extends javax.swing.JFrame {

    /**
     * Creates new form VistaPaises
     */
    int xMouse, yMouse;
    private int papulandiaClickCount = 0;

    public VistaCuidades() {
        // CONSTRUCTOR POR DEFECTO RESTAURADO. Delega la inicialización al constructor con String, pasando null.
        this(null);
    }

    public VistaCuidades(String countryCode) {
        initComponents();
        // Título de la ventana corregido
        setTitle("Lista de Ciudades");
        this.setLocationRelativeTo(null);
        this.setResizable(false);
        this.setSize(1220, 600);
        cargarMusicaDeFondo();
        setIconImage(new javax.swing.ImageIcon(getClass().getResource("/icons/Papulandia2.png")).getImage());
        personalizarTablaEstiloFrutiger();
        establecerCursorPersonalizado();
        this.getRootPane().setDefaultButton(btnagregar);

        // Listener de la tabla para rellenar campos
        jTable1.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent event) {
                // Evita errores si no hay fila seleccionada
                if (jTable1.getSelectedRow() != -1 && !event.getValueIsAdjusting()) {
                    int filaSeleccionada = jTable1.convertRowIndexToModel(jTable1.getSelectedRow());
                    if (filaSeleccionada >= 0) {
                        DefaultTableModel modelo = (DefaultTableModel) jTable1.getModel();

                        // Rellena los campos con los datos de la tabla (5 columnas: ID, Nombre, Distrito, Cód. País, Población)
                        txtcodigo.setText(modelo.getValueAt(filaSeleccionada, 0).toString());     // ID (Index 0)
                        txtnombre.setText(modelo.getValueAt(filaSeleccionada, 1).toString());     // Nombre (Index 1)
                        // Índice 2 es Distrito
                        txtcodigo1.setText(modelo.getValueAt(filaSeleccionada, 2).toString());    // Distrito (Index 2)
                        // Índice 3 es Cód. País
                        txtcontinente.setText(modelo.getValueAt(filaSeleccionada, 3).toString()); // Cód. País (Index 3)
                        // Índice 4 es Población
                        txtpoblacion.setText(modelo.getValueAt(filaSeleccionada, 4).toString());  // Población (Index 4)

                        // Pone el texto en negro (quitando el placeholder gris)
                        txtcodigo.setForeground(Color.black);
                        txtnombre.setForeground(Color.black);
                        txtcodigo1.setForeground(Color.black);
                        txtcontinente.setForeground(Color.black);
                        txtpoblacion.setForeground(Color.black);
                    }
                }
            }
        });

        // Lógica de filtro para el nuevo constructor
        if (countryCode != null) {
            // 1. Rellena el campo de filtro Cód. País con el código recibido.
            txtcontinente.setText(countryCode);
            // 2. Quita el color de placeholder (gris) para que se vea como texto de filtro.
            txtcontinente.setForeground(Color.black);
        }

        // Llama al método de búsqueda, que usará el valor de txtcontinente si no es el placeholder
        buscarCiudades();
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

    private void buscarCiudades() {
        // Define las columnas para el modelo de la tabla (AHORA 5 COLUMNAS)
        DefaultTableModel modelo = new DefaultTableModel(
                new Object[]{"ID", "Nombre", "Distrito", "Cód. País", "Población"}, 0
        );

        // 1. Prepara la consulta SQL base (AGREGANDO DISTRICT)
        String sqlBase = "SELECT ID, Name, District, CountryCode, Population FROM city";

        // Listas para construir la consulta dinámica de forma segura
        ArrayList<String> conditions = new ArrayList<>();
        ArrayList<Object> params = new ArrayList<>();

        // 2. Recoge los textos de los campos
        String id = txtcodigo.getText();
        String nombre = txtnombre.getText();
        String distrito = txtcodigo1.getText(); // <<< NUEVA LECTURA DEL CAMPO DISTRITO
        // txtcontinente se usa para CountryCode
        String codigoPais = txtcontinente.getText();
        String poblacion = txtpoblacion.getText();

        try {
            // 3. Añade condiciones SÓLO si el campo está lleno y no es placeholder

            // Si el campo 'id' no está vacío... (Búsqueda por ID exacto)
            if (!id.isEmpty() && !id.equals("Ingresa el ID")) {
                conditions.add("ID = ?");
                params.add(Integer.parseInt(id)); // Parámetro numérico
            }

            // Si el campo 'nombre' no está vacío...
            if (!nombre.isEmpty() && !nombre.equals("Ingresa el nombre")) {
                conditions.add("Name LIKE ?");
                params.add("%" + nombre + "%");
            }

            // Si el campo 'distrito' no está vacío... <<< LÓGICA DE BÚSQUEDA POR DISTRITO
            if (!distrito.isEmpty() && !distrito.equals("Ingresa el Distrito")) {
                conditions.add("District LIKE ?");
                params.add("%" + distrito + "%");
            }

            // Si el campo 'codigoPais' (txtcontinente) no está vacío...
            if (!codigoPais.isEmpty() && !codigoPais.equals("Ingresa el código de pais")) {
                conditions.add("CountryCode LIKE ?");
                params.add(codigoPais + "%");
            }

            // Si el campo 'poblacion' no está vacío...
            if (!poblacion.isEmpty() && !poblacion.equals("Ingresa la población")) {
                conditions.add("Population >= ?");
                params.add(Integer.parseInt(poblacion));
            }

            // 4. Construye la consulta final
            if (!conditions.isEmpty()) {
                sqlBase += " WHERE " + String.join(" AND ", conditions);
            }

            sqlBase += " LIMIT 100";

            // 5. Ejecuta la consulta
            Connection miConexion = Conexion.getConnection();

            try (PreparedStatement pstmt = miConexion.prepareStatement(sqlBase)) {

                for (int i = 0; i < params.size(); i++) {
                    pstmt.setObject(i + 1, params.get(i));
                }

                try (ResultSet rs = pstmt.executeQuery()) {
                    System.out.println("Ejecutando consulta: " + pstmt.toString());

                    // 6. Recorre los resultados (AHORA OBTENIENDO 5 CAMPOS)
                    while (rs.next()) {
                        modelo.addRow(new Object[]{
                            rs.getInt("ID"),
                            rs.getString("Name"),
                            rs.getString("District"), // AÑADE EL DISTRITO
                            rs.getString("CountryCode"),
                            rs.getInt("Population")
                        });
                    }

                    if (modelo.getRowCount() == 0) {
                        System.out.println("No se encontraron resultados para la búsqueda.");
                    } else {
                        System.out.println(modelo.getRowCount() + " ciudades cargadas.");
                    }
                }
            }

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "El ID y la Población deben ser números válidos.", "Error de Formato", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al consultar la base de datos: " + e.getMessage(), "Error SQL", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error inesperado: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }

        // 7. Asigna el modelo a la tabla
        jTable1.setModel(modelo);
        personalizarTablaEstiloFrutiger();
    }

    private void mostrarCiudadesMasPobladas() {
        // Define las columnas para el reporte (5 columnas)
        DefaultTableModel modelo = new DefaultTableModel(
                new Object[]{"ID", "Ciudad", "País", "Continente", "Población"}, 0
        );

        Connection miConexion = null;
        try {
            miConexion = Conexion.getConnection();

            // SQL: Une city (T1) con country (T2) para obtener el País y Continente. Ordena por población DESC.
            String sql = "SELECT "
                    + "T1.ID, T1.Name AS CityName, T1.Population, "
                    + "T2.Name AS CountryName, T2.Continent "
                    + "FROM city T1 "
                    + "JOIN country T2 ON T1.CountryCode = T2.Code "
                    + "ORDER BY T1.Population DESC "
                    + "LIMIT 50"; // Limitar a las 50 ciudades más pobladas

            try (java.sql.Statement stmt = miConexion.createStatement(); java.sql.ResultSet rs = stmt.executeQuery(sql)) {

                System.out.println("Ejecutando consulta de Ciudades más pobladas: " + sql);

                while (rs.next()) {
                    // Formatear la población para mejor lectura
                    String formattedPopulation = String.format("%,d", rs.getInt("Population"));

                    modelo.addRow(new Object[]{
                        rs.getInt("ID"),
                        rs.getString("CityName"),
                        rs.getString("CountryName"),
                        rs.getString("Continent"),
                        formattedPopulation
                    });
                }

                if (modelo.getRowCount() == 0) {
                    JOptionPane.showMessageDialog(this, "No se encontraron datos de ciudades.", "Error de Datos", JOptionPane.ERROR_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Reporte de " + modelo.getRowCount() + " ciudades cargado con éxito, ordenado por población (Top 50).",
                            "Reporte Generado",
                            JOptionPane.INFORMATION_MESSAGE);
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al generar reporte de ciudades más pobladas: " + e.getMessage(), "Error SQL", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error inesperado: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }

        // Asignar el modelo a la tabla
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

                    System.out.println("--- Paises de Sudamérica en la BD 'world' ---");

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

        btnCuidadesMasPobladas = new javax.swing.JButton();
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
        jLabel9 = new javax.swing.JLabel();
        txtcodigo = new javax.swing.JTextField();
        txtcontinente = new javax.swing.JTextField();
        txtcodigo1 = new javax.swing.JTextField();
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

        btnCuidadesMasPobladas.setText("Mas pobladas");
        btnCuidadesMasPobladas.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCuidadesMasPobladasActionPerformed(evt);
            }
        });
        getContentPane().add(btnCuidadesMasPobladas, new org.netbeans.lib.awtextra.AbsoluteConstraints(1090, 110, 110, 30));

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
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 481, Short.MAX_VALUE)
                .addComponent(btnPaises)
                .addGap(63, 63, 63)
                .addComponent(btnCuidades, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(60, 60, 60)
                .addComponent(btnIdiomas, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(125, 125, 125)
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
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(btnPaises, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btnCuidades, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btnIdiomas, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1220, 50));

        jLabel2.setText("Distrito");
        getContentPane().add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 250, -1, -1));

        jLabel3.setText("Nombre");
        getContentPane().add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 190, -1, -1));

        jLabel4.setText("Codigo Pais");
        getContentPane().add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 310, -1, -1));

        jLabel5.setText("Población");
        getContentPane().add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 370, -1, -1));

        jLabel9.setText("Id");
        getContentPane().add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 130, -1, -1));

        txtcodigo.setForeground(new java.awt.Color(153, 153, 153));
        txtcodigo.setText("Ingresa el ID");
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
        getContentPane().add(txtcodigo, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 120, 160, 40));

        txtcontinente.setForeground(new java.awt.Color(153, 153, 153));
        txtcontinente.setText("Ingresa el código de pais");
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

        txtcodigo1.setForeground(new java.awt.Color(153, 153, 153));
        txtcodigo1.setText("Ingresa el Distrito");
        txtcodigo1.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtcodigo1FocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtcodigo1FocusLost(evt);
            }
        });
        txtcodigo1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtcodigo1ActionPerformed(evt);
            }
        });
        txtcodigo1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtcodigo1KeyTyped(evt);
            }
        });
        getContentPane().add(txtcodigo1, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 240, 160, 40));

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
        getContentPane().add(txtnombre, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 180, 160, 40));

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
                "Id", "Nombre", "Distrito", "Codigo Pais", "Poblacion"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Integer.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
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

        getContentPane().add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(470, 80, 610, 399));

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
        getContentPane().add(btnconsultar, new org.netbeans.lib.awtextra.AbsoluteConstraints(500, 490, 140, 50));

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
        getContentPane().add(btnmodificar, new org.netbeans.lib.awtextra.AbsoluteConstraints(790, 490, 130, 50));

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
        getContentPane().add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(290, 40, 930, 620));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnagregarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnagregarActionPerformed

        // 1. Obtener los datos de los campos de texto
        // OMITIMOS el txtcodigo (ID) porque es auto-incremental
        String nombre = txtnombre.getText();
        String codigoPais = txtcontinente.getText(); // Usamos txtcontinente para CountryCode
        String poblacion = txtpoblacion.getText();

        // 2. Validar que los campos no estén vacíos
        if (nombre.isEmpty() || nombre.equals("Ingresa el nombre")
                || codigoPais.isEmpty() || codigoPais.equals("Ingresa el código de pais")
                || poblacion.isEmpty() || poblacion.equals("Ingresa la población")) {

            JOptionPane.showMessageDialog(this, "Por favor, rellena Nombre, Código Pais y Población.", "Campos vacíos", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // --- INICIA LA LÓGICA DE BASE DE DATOS ---
        Connection miConexion = null;
        try {
            miConexion = Conexion.getConnection();

            // 4. Preparar la consulta SQL (CORREGIDA para 'city')
            // (El ID es auto-incremental, no se incluye en el INSERT)
            String sql = "INSERT INTO city (Name, CountryCode, Population) VALUES (?, ?, ?)";

            try (java.sql.PreparedStatement pstmt = miConexion.prepareStatement(sql)) {

                // 6. Asignar los valores a los '?'
                pstmt.setString(1, nombre);
                pstmt.setString(2, codigoPais);
                pstmt.setInt(3, Integer.parseInt(poblacion));

                int filasAfectadas = pstmt.executeUpdate();

                if (filasAfectadas > 0) {
                    JOptionPane.showMessageDialog(this, "¡Ciudad agregada exitosamente a la base de datos!", "Éxito", JOptionPane.INFORMATION_MESSAGE);

                    // 9. Actualizar la tabla visual
                    buscarCiudades();

                    // 10. Limpiar los campos de texto
                    txtcodigo.setText("Ingresa el ID");
                    txtcodigo.setForeground(new Color(153, 153, 153));
                    txtnombre.setText("Ingresa el nombre");
                    txtnombre.setForeground(new Color(153, 153, 153));

                    txtcontinente.setText("Ingresa el código de pais");
                    txtcontinente.setForeground(new Color(153, 153, 153));
                    txtpoblacion.setText("Ingresa la población");
                    txtpoblacion.setForeground(new Color(153, 153, 153));

                } else {
                    JOptionPane.showMessageDialog(this, "No se pudo agregar la ciudad.", "Error", JOptionPane.WARNING_MESSAGE);
                }
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al guardar en la base de datos: " + e.getMessage(), "Error SQL", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "La población debe ser un número entero válido.", "Error de Formato", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error inesperado: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }

    }//GEN-LAST:event_btnagregarActionPerformed

    private void txtcodigoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtcodigoActionPerformed
        // TODO add your handling code here:

    }//GEN-LAST:event_txtcodigoActionPerformed

    private void txtcodigoFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtcodigoFocusGained

        // Placeholder corregido
        if (txtcodigo.getText().equals("Ingresa el ID")) {
            txtcodigo.setText("");
            txtcodigo.setForeground(new Color(0, 0, 0));
        }

    }//GEN-LAST:event_txtcodigoFocusGained

    private void txtcodigoFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtcodigoFocusLost
        if (txtcodigo.getText().equals("")) {
            txtcodigo.setText("Ingresa el ID");
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
        if (txtcontinente.getText().equals("Ingresa el código de pais")) {
            txtcontinente.setText("");
            txtcontinente.setForeground(new Color(0, 0, 0));
        }
    }//GEN-LAST:event_txtcontinenteFocusGained

    private void txtcontinenteFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtcontinenteFocusLost
        if (txtcontinente.getText().equals("")) {
            txtcontinente.setText("Ingresa el código de pais");
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
        buscarCiudades();
    }//GEN-LAST:event_btnconsultarActionPerformed

    private void btnmodificarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnmodificarActionPerformed
        int filaSeleccionada = jTable1.getSelectedRow();

        if (filaSeleccionada < 0) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar una ciudad de la tabla para modificar.", "Fila no seleccionada", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Convertir el índice de la vista al índice del modelo (por si la tabla está ordenada)
        int filaModelo = jTable1.convertRowIndexToModel(filaSeleccionada);

        // 3. Obtener el ID ORIGINAL (PK) y el Distrito Original de la tabla
        DefaultTableModel modelo = (DefaultTableModel) jTable1.getModel();
        String idOriginal = modelo.getValueAt(filaModelo, 0).toString();
        String distritoOriginal = modelo.getValueAt(filaModelo, 2).toString(); // Columna 2 es Distrito

        // 4. Obtener los NUEVOS valores de los campos de texto
        String idNuevo = txtcodigo.getText(); // ID
        String nombreNuevo = txtnombre.getText();
        String codigoPaisNuevo = txtcontinente.getText(); // CountryCode
        String poblacionNueva = txtpoblacion.getText(); // Population

        // 5. Validar que los campos no estén vacíos
        if (idNuevo.isEmpty() || idNuevo.equals("Ingresa el ID")
                || nombreNuevo.isEmpty() || nombreNuevo.equals("Ingresa el nombre")
                || codigoPaisNuevo.isEmpty() || codigoPaisNuevo.equals("Ingresa el código de pais")
                || poblacionNueva.isEmpty() || poblacionNueva.equals("Ingresa la población")) {

            JOptionPane.showMessageDialog(this, "Por favor, rellena todos los campos.", "Campos vacíos", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // --- INICIA LA LÓGICA DE BASE DE DATOS ---
        Connection miConexion = null;
        try {
            miConexion = Conexion.getConnection();

            // 7. Preparar la consulta SQL UPDATE (INCLUYENDO DISTRICT)
            // La tabla city tiene columnas: ID, Name, CountryCode, District, Population.
            String sql = "UPDATE city SET ID = ?, Name = ?, CountryCode = ?, District = ?, Population = ? WHERE ID = ?";

            try (java.sql.PreparedStatement pstmt = miConexion.prepareStatement(sql)) {

                // 8. Asignar los NUEVOS valores (columnas SET)
                pstmt.setInt(1, Integer.parseInt(idNuevo));
                pstmt.setString(2, nombreNuevo);
                pstmt.setString(3, codigoPaisNuevo.trim());
                pstmt.setString(4, distritoOriginal); // Usamos el valor original del Distrito que se muestra en la tabla.
                pstmt.setInt(5, Integer.parseInt(poblacionNueva));

                // 9. Asignar el ID ORIGINAL (columna WHERE)
                pstmt.setInt(6, Integer.parseInt(idOriginal));

                int filasAfectadas = pstmt.executeUpdate();

                if (filasAfectadas > 0) {
                    JOptionPane.showMessageDialog(this, "¡Ciudad modificada exitosamente en la BD!", "Éxito", JOptionPane.INFORMATION_MESSAGE);

                    // 12. Refrescar la tabla
                    buscarCiudades();

                    // 13. Limpiar los campos
                    txtcodigo.setText("Ingresa el ID");
                    txtcodigo.setForeground(new Color(153, 153, 153));
                    txtnombre.setText("Ingresa el nombre");
                    txtnombre.setForeground(new Color(153, 153, 153));
                    txtcontinente.setText("Ingresa el código de pais");
                    txtcontinente.setForeground(new Color(153, 153, 153));
                    txtpoblacion.setText("Ingresa la población");
                    txtpoblacion.setForeground(new Color(153, 153, 153));

                } else {
                    JOptionPane.showMessageDialog(this, "No se encontró la ciudad para modificar.", "Error", JOptionPane.WARNING_MESSAGE);
                }
            }

        } catch (SQLException e) {
            String mensajeError = e.getMessage();
            if (mensajeError.contains("Duplicate entry")) {
                JOptionPane.showMessageDialog(this, "Error: El nuevo ID '" + idNuevo + "' ya existe en la BD.", "Error de Duplicado", JOptionPane.ERROR_MESSAGE);
            } else if (mensajeError.contains("foreign key constraint fails")) {
                JOptionPane.showMessageDialog(this, "Error: El código de país '" + codigoPaisNuevo + "' no existe en la tabla de países (country).", "Error de Clave Foránea", JOptionPane.ERROR_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Error al modificar en la base de datos: " + mensajeError, "Error SQL", JOptionPane.ERROR_MESSAGE);
            }
            e.printStackTrace();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "El ID y la Población deben ser números enteros válidos.", "Error de Formato", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error inesperado: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }//GEN-LAST:event_btnmodificarActionPerformed

    private void txtcodigoKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtcodigoKeyTyped
        char n = evt.getKeyChar();
        if (n < '0' || n > '9') {
            evt.consume(); // Ignora la tecla si no es un número
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
        VistaPaises vistaPaises = new VistaPaises();

        // 2. Hacerla visible
        vistaPaises.setVisible(true);

        // 3. (Importante) Cerrar esta ventana actual (VistaPaises)
        this.dispose();         // TODO add your handling code here:
    }//GEN-LAST:event_btnPaisesActionPerformed

    private void txtcodigo1FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtcodigo1FocusGained
        // TODO add your handling code here:
        if (txtcodigo1.getText().equals("Ingresa el Distrito")) {
            txtcodigo1.setText("");
            txtcodigo1.setForeground(new Color(0, 0, 0));
        }
    }//GEN-LAST:event_txtcodigo1FocusGained

    private void txtcodigo1FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtcodigo1FocusLost
        // TODO add your handling code here:
        if (txtcodigo1.getText().equals("")) {
            txtcodigo1.setText("Ingresa el Distrito");
            txtcodigo1.setForeground(new Color(153, 153, 153));
        }
    }//GEN-LAST:event_txtcodigo1FocusLost

    private void txtcodigo1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtcodigo1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtcodigo1ActionPerformed

    private void txtcodigo1KeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtcodigo1KeyTyped
        // TODO add your handling code here:
    }//GEN-LAST:event_txtcodigo1KeyTyped

    private void btnCuidadesMasPobladasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCuidadesMasPobladasActionPerformed
        mostrarCiudadesMasPobladas();        // TODO add your handling code here:
    }//GEN-LAST:event_btnCuidadesMasPobladasActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCerrarSesion;
    private javax.swing.JButton btnCuidades;
    private javax.swing.JButton btnCuidadesMasPobladas;
    private javax.swing.JButton btnIdiomas;
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
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JButton minBtn;
    private javax.swing.JTextField txtcodigo;
    private javax.swing.JTextField txtcodigo1;
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
