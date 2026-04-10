package paq;

import paq.Analizadores.Lexico.AnalizadorLexico;
import paq.Structures.Semantic.Semantic;
import paq.Structures.Semantic.Symbol;
import paq.Structures.Syntax.Nodo;
import paq.Structures.Syntax.Parser;
import paq.Structures.Token;
import paq.Structures.TT;

import javax.swing.*;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.nio.file.Files;
import java.util.*;
import java.util.List;

public class Principal extends JFrame {

    public static LinkedList<Token> errorsTokenList = new LinkedList<>();
    public static List<Token> validTokenList = new LinkedList<>();

    public static List<Symbol> symbolTable = new LinkedList<>();
    public static LinkedList<Nodo> treeNodes = new LinkedList<>();



    private JTextArea editorArea;
    private JTextArea numArea;
    private JTextArea consoleArea;

    private JTable tablaTokens;
    private DefaultTableModel modeloTabla;

    private JTable tablaSimbolos;
    private DefaultTableModel modeloSimbolos;

    private DefaultTableModel modeloSintax;


    public Principal() {
        setTitle("AnalizadorLexico Léxico");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1400, 700);
        setLocationRelativeTo(null);

        init();
        configurarMenu();
    }

    private void init() {
        JPanel panelPrincipal = new JPanel(new BorderLayout());
        JPanel panelSintax = new JPanel(new BorderLayout());
        panelSintax.setBorder(BorderFactory.createTitledBorder("Árbol Sintáctico"));
        panelSintax.setPreferredSize(new Dimension(400, 700));

        String[] columnasSintax = {"Numero", "Nodo", "Num Padre"};
        modeloSintax = new DefaultTableModel(columnasSintax, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable tablaNueva = new JTable(modeloSintax);
        tablaNueva.setFont(new Font("Arial", Font.PLAIN, 12));
        tablaNueva.setRowHeight(25);

        tablaNueva.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        tablaNueva.getTableHeader().setBackground(new Color(70, 130, 180));
        tablaNueva.getTableHeader().setForeground(Color.WHITE);

        tablaNueva.getColumnModel().getColumn(0).setPreferredWidth(100);
        tablaNueva.getColumnModel().getColumn(1).setPreferredWidth(200);
        tablaNueva.getColumnModel().getColumn(2).setPreferredWidth(100);

        JScrollPane scrollTablaNueva = new JScrollPane(tablaNueva);
        scrollTablaNueva.setPreferredSize(new Dimension(250, 600));
        panelSintax.add(scrollTablaNueva, BorderLayout.CENTER);

        JPanel panelCentral = new JPanel(new BorderLayout());

        JPanel panelEditor = new JPanel(new BorderLayout());
        panelEditor.setBorder(BorderFactory.createTitledBorder("Editor de Código"));

        numArea = new JTextArea("1");
        numArea.setBackground(new Color(240, 240, 240));
        numArea.setForeground(Color.GRAY);
        numArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        numArea.setEditable(false);
        numArea.setMargin(new Insets(3, 5, 3, 5));

        editorArea = new JTextArea();
        editorArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        editorArea.setTabSize(4);

        JScrollPane scrollEditor = new JScrollPane(editorArea);
        scrollEditor.setRowHeaderView(numArea);
        scrollEditor.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        panelEditor.add(scrollEditor, BorderLayout.CENTER);

        JPanel panelConsola = new JPanel(new BorderLayout());
        panelConsola.setBorder(BorderFactory.createTitledBorder("Consola"));

        consoleArea = new JTextArea();
        consoleArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        consoleArea.setBackground(new Color(30, 30, 30));
        consoleArea.setForeground(Color.WHITE);
        consoleArea.setEditable(false);

        JScrollPane scrollConsola = new JScrollPane(consoleArea);
        scrollConsola.setPreferredSize(new Dimension(900, 200));

        JPanel panelBotonesConsola = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnAnalizar = new JButton("Analizar");
        btnAnalizar.addActionListener(e -> ejecutarCodigo());

        JButton btnLimpiarConsola = new JButton("Limpiar Consola");
        btnLimpiarConsola.addActionListener(e -> consoleArea.setText(""));

        JButton btnLimpiarEditor = new JButton("Limpiar Editor");
        btnLimpiarEditor.addActionListener(e -> {
            editorArea.setText("");
            limpiarTablas();
        });

        panelBotonesConsola.add(btnAnalizar);
        panelBotonesConsola.add(btnLimpiarConsola);
        panelBotonesConsola.add(btnLimpiarEditor);

        panelConsola.add(scrollConsola, BorderLayout.CENTER);
        panelConsola.add(panelBotonesConsola, BorderLayout.SOUTH);

        JSplitPane splitVertical = new JSplitPane(
                JSplitPane.VERTICAL_SPLIT,
                panelEditor,
                panelConsola
        );
        splitVertical.setResizeWeight(0.7);
        splitVertical.setDividerSize(5);

        panelCentral.add(splitVertical, BorderLayout.CENTER);

        JPanel panelDerecho = new JPanel(new BorderLayout());
        panelDerecho.setPreferredSize(new Dimension(500, 700));

        JPanel panelTokens = new JPanel(new BorderLayout());
        panelTokens.setBorder(BorderFactory.createTitledBorder("Tabla de Tokens"));
        String[] columnasTokens = {"Lexema", "Tipo Token"};
        modeloTabla = new DefaultTableModel(columnasTokens, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tablaTokens = new JTable(modeloTabla);
        tablaTokens.setFont(new Font("Arial", Font.PLAIN, 12));
        tablaTokens.setRowHeight(25);

        tablaTokens.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        tablaTokens.getTableHeader().setBackground(new Color(70, 130, 180));
        tablaTokens.getTableHeader().setForeground(Color.WHITE);

        tablaTokens.getColumnModel().getColumn(0).setPreferredWidth(100);
        tablaTokens.getColumnModel().getColumn(1).setPreferredWidth(150);

        JScrollPane scrollTablaTokens = new JScrollPane(tablaTokens);
        scrollTablaTokens.setPreferredSize(new Dimension(250, 600));
        panelTokens.add(scrollTablaTokens, BorderLayout.CENTER);

        JPanel panelSimbolos = new JPanel(new BorderLayout());
        panelSimbolos.setBorder(BorderFactory.createTitledBorder("Tabla de Símbolos"));

        String[] columnasSimbolos = {"ID", "Valor", "Tipo"};
        modeloSimbolos = new DefaultTableModel(columnasSimbolos, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tablaSimbolos = new JTable(modeloSimbolos);
        tablaSimbolos.setFont(new Font("Arial", Font.PLAIN, 12));
        tablaSimbolos.setRowHeight(25);

        tablaSimbolos.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        tablaSimbolos.getTableHeader().setBackground(new Color(70, 130, 180));
        tablaSimbolos.getTableHeader().setForeground(Color.WHITE);

        tablaSimbolos.getColumnModel().getColumn(0).setPreferredWidth(100);
        tablaSimbolos.getColumnModel().getColumn(1).setPreferredWidth(100);
        tablaSimbolos.getColumnModel().getColumn(2).setPreferredWidth(100);

        JScrollPane scrollTablaSimbolos = new JScrollPane(tablaSimbolos);
        scrollTablaSimbolos.setPreferredSize(new Dimension(250, 600));
        panelSimbolos.add(scrollTablaSimbolos, BorderLayout.CENTER);

        JSplitPane splitTablas = new JSplitPane(
                JSplitPane.HORIZONTAL_SPLIT,
                panelTokens,
                panelSimbolos
        );
        splitTablas.setResizeWeight(0.5);
        splitTablas.setDividerSize(5);
        panelDerecho.add(splitTablas, BorderLayout.CENTER);

        JSplitPane splitHorizontal1 = new JSplitPane(
                JSplitPane.HORIZONTAL_SPLIT,
                panelSintax,
                panelCentral
        );
        splitHorizontal1.setResizeWeight(0.25);
        splitHorizontal1.setDividerSize(5);
        JSplitPane splitHorizontal = new JSplitPane(
                JSplitPane.HORIZONTAL_SPLIT,
                splitHorizontal1,
                panelDerecho
        );
        splitHorizontal.setResizeWeight(0.6);
        splitHorizontal.setDividerSize(5);

        panelPrincipal.add(splitHorizontal, BorderLayout.CENTER);
        editorArea.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                actualizarNumeracion();
            }
            @Override
            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                actualizarNumeracion();
            }
            @Override
            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                actualizarNumeracion();
            }
        });

        add(panelPrincipal);
    }

    private void actualizarNumeracion() {
        String texto = editorArea.getText();
        int lineas = texto.split("\n").length;
        if (texto.endsWith("\n") || texto.isEmpty()) {
            lineas++;
        }

        StringBuilder numeros = new StringBuilder();
        for (int i = 1; i <= lineas; i++) {
            numeros.append(i).append("\n");
        }
        numArea.setText(numeros.toString());
    }

    private void ejecutarCodigo() {
        String codigo = editorArea.getText();
        consoleArea.setText("");
        limpiarTablas();
        AnalizadorLexico.execute(codigo);
        new Parser(validTokenList).parse();
        Semantic.semanticAnalyze(treeNodes);

        if(!errorsTokenList.isEmpty()){
            for(Token tkn : errorsTokenList) {
                String errorMsg =
                        "Error "+ (tkn.isSyntax()?"sintáctico":"léxico")+
                                " en la linea "+tkn.getLine()+":"+tkn.getStart()+" -> "+tkn.getLexeme()+" \n";
                        if(tkn.isSyntax()){
                            errorMsg+="\t"+tkn.getError()+"\n";
                        }
                consoleArea.append(errorMsg);
            }
        }


        actualizarTablas();
    }
    private void actualizarTablas() {
        modeloTabla.setRowCount(0);
        modeloSimbolos.setRowCount(0);
        modeloSintax.setRowCount(0);
        Set<Token> set = new LinkedHashSet<>(validTokenList);

        for(Token token : set){
            String lexema = token.getLexeme();
            TT tipoToken = token.getTipo();
            modeloTabla.addRow(new Object[]{lexema, tipoToken});
        }
        for(Symbol symbol : symbolTable){
            String lexema = symbol.getToken().getLexeme();
            String valor = symbol.getValue()!=null ?  symbol.getValue(): "";
            String type = symbol.getType()!=null ? symbol.getType().toString(): "";
            modeloSimbolos.addRow(new Object[]{lexema, valor, type});
        }
        for(Nodo nodo : treeNodes){
            TT type = nodo.getToken().getTipo();
            boolean showValue = type == TT.TK_DECV || type == TT.TK_INTV || type == TT.TK_ID;
            modeloSintax.addRow(new Object[]{
                    nodo.getNumero(),
                    showValue? type+"("+nodo.getToken().getLexeme()+")": type,
                    nodo.getPadre()});
        }

        JViewport viewportTokens = (JViewport) tablaTokens.getParent();
        if (viewportTokens != null) {
            Component parent = viewportTokens.getParent();
            if (parent instanceof JScrollPane) {
                JScrollPane scrollPane = (JScrollPane) parent;
                scrollPane.setBorder(BorderFactory.createTitledBorder(
                        "Tokens (" + validTokenList.size() + " encontrados)"));
            }
        }

        JViewport viewportSimbolos = (JViewport) tablaSimbolos.getParent();
        if (viewportSimbolos != null) {
            Component parent = viewportSimbolos.getParent();
            if (parent instanceof JScrollPane) {
                JScrollPane scrollPane = (JScrollPane) parent;
                int countSimbolos = modeloSimbolos.getRowCount();
                scrollPane.setBorder(BorderFactory.createTitledBorder(
                        "Símbolos (" + countSimbolos + " encontrados)"));
            }
        }
    }

    private void limpiarTablas() {
        modeloTabla.setRowCount(0);
        modeloSimbolos.setRowCount(0);
        modeloSintax.setRowCount(0);
        validTokenList.clear();
        errorsTokenList.clear();
        treeNodes.clear();
        symbolTable.clear();
    }

    private void configurarMenu() {
        JMenuBar menuBar = new JMenuBar();

        JMenu menuArchivo = new JMenu("Archivo");

        JMenuItem itemNuevo = new JMenuItem("Nuevo");
        itemNuevo.addActionListener(e -> {
            editorArea.setText("");
            limpiarTablas();
            consoleArea.setText("");
        });

        JMenuItem itemAbrir = new JMenuItem("Abrir");
        itemAbrir.addActionListener(e -> abrirArchivo());

        JMenuItem itemGuardar = new JMenuItem("Guardar");
        itemGuardar.addActionListener(e -> guardarArchivo());

        JMenuItem itemSalir = new JMenuItem("Salir");
        itemSalir.addActionListener(e -> System.exit(0));

        menuArchivo.add(itemNuevo);
        menuArchivo.add(itemAbrir);
        menuArchivo.add(itemGuardar);
        menuArchivo.addSeparator();
        menuArchivo.add(itemSalir);

        menuBar.add(menuArchivo);
        setJMenuBar(menuBar);
    }

    private void abrirArchivo() {
        JFileChooser fileChooser = new JFileChooser();
        int resultado = fileChooser.showOpenDialog(this);

        if (resultado == JFileChooser.APPROVE_OPTION) {
            try {
                File archivo = fileChooser.getSelectedFile();
                String contenido = new String(Files.readAllBytes(archivo.toPath()));
                editorArea.setText(contenido);
                consoleArea.append("Archivo abierto: " + archivo.getName() + "\n");
                limpiarTablas();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error al abrir el archivo", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void guardarArchivo() {
        JFileChooser fileChooser = new JFileChooser();
        int resultado = fileChooser.showSaveDialog(this);

        if (resultado == JFileChooser.APPROVE_OPTION) {
            try {
                File archivo = fileChooser.getSelectedFile();
                Files.write(archivo.toPath(), editorArea.getText().getBytes());
                consoleArea.append("Archivo guardado: " + archivo.getName() + "\n");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error al guardar el archivo", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                UIManager.put("Table.rowHeight", 25);
                UIManager.put("Table.selectionBackground", new Color(173, 216, 230));

            } catch (Exception e) {
                e.printStackTrace();
            }

            Principal editor = new Principal();
            editor.setVisible(true);
        });
    }
}