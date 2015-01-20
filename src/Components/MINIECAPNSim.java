package Components;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.Vector;
import BdConnection.*;

public class MINIECAPNSim extends JFrame {
    static String ubic = "resources/images/"; //ubicacion de las imagenes utilizadas para mostrar en la ventana
    static final DrawArea disp = new DrawArea();
    String namepnj;  //Nombre del archivo

    public MINIECAPNSim() {
        super("ECAPNSim: Event-Condition-Action & Petri Net Simulator");

        namepnj = new String();
        Container cp = getContentPane();
        addWindowListener(new WindowAdapter() {
                              public void windowClosing(WindowEvent e) {
                                  System.exit(0);
                              }}
        );

//*** File buttons, bFile menu de los botones de opciones
        JToolBar bFile = new JToolBar();
        final JButton nuevo = new JButton(new ImageIcon(ubic + "New16.gif"));
        nuevo.setToolTipText("New File");
        final JButton abrir = new JButton(new ImageIcon(ubic + "Open16.gif"));
        abrir.setToolTipText("Open File");
        final JButton guardar = new JButton(new ImageIcon(ubic + "Save16.gif"));
        guardar.setToolTipText("Save File");
        final JButton guardarC = new JButton(new ImageIcon(ubic + "SaveAs16.gif"));
        guardarC.setToolTipText("Save As");
        final JButton zoomIn = new JButton(new ImageIcon(ubic + "ZoomIn16.gif"));
        zoomIn.setToolTipText("Zoom In");
        final JButton zoomOut = new JButton(new ImageIcon(ubic + "ZoomOut16.gif"));
        zoomOut.setToolTipText("Zoom Out");
        final JButton compilar = new JButton("Compile");
        compilar.setToolTipText("Compile");
        final JButton crear = new JButton("Create Rules");
        crear.setToolTipText("Create Rules");
        final JButton color = new JButton("Change Color");
        color.setToolTipText("Change Color");
        final JButton checar = new JButton("Create Rules");
        checar.setToolTipText("Create Rules");


        checar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.out.println("Button Revisar");
                if(disp.verificar_red())
                    disp.generar_reglas();
                else
                    JOptionPane.showMessageDialog(null,"la Red de Petri no es correcta","RED DE PETRI",JOptionPane.INFORMATION_MESSAGE);
            }
        });

        nuevo.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                System.out.println("Button Nuevo");
                int re = preguntar();
                if(re != JOptionPane.CANCEL_OPTION && re != JOptionPane.DEFAULT_OPTION) {
                    disp.limpiar(true);
                    namepnj = new String();
                }
            }
        });

        abrir.addActionListener(new ActionListener() {
                                    public void actionPerformed(ActionEvent e) {
                                        System.out.println("Button Abrir");
                                        abrirECA();
                                        disp.IncidenceMatrix1( );
                                        try{
                                            disp.guardarMatrizIncidencia( "salida.mtx" );
                                        } catch( Exception ee ){
                                            System.err.println( "No se pudo guardar la matriz" );
                                        }

                                        System.out.println( "ESPERO EN IMPORTAR" );
                                        //suprimir.setEnabled(true);
                                        //cursor.setEnabled(true);
                                        abrirEPS();
                                    }}
        );

        guardar.addActionListener(new ActionListener() {
                                      public void actionPerformed(ActionEvent e) {
                                          System.out.println("Button Guardar");
                                          if(namepnj.equals("")) {
                                              guardarEPS();
                                          } else {
                                              try {
                                                  disp.guardar(namepnj);
                                              } catch (Exception ioe) {
                                                  System.out.println("No se guardó el archivo...");
                                                  JOptionPane.showMessageDialog(null,"No se guardó el archivo","Error",JOptionPane.ERROR_MESSAGE);
                                                  ioe.printStackTrace();
                                              }
                                          }
                                      }}
        );

        guardarC.addActionListener(new ActionListener() {
                                       public void actionPerformed(ActionEvent e) {
                                           System.out.println("Button Guardar Como");
                                           guardarEPS();
                                       }}
        );

        zoomIn.addActionListener(new ActionListener() {
                                     public void actionPerformed(ActionEvent e) {
                                         System.out.println("Button Zoom In");
                                         double por = disp.getPorc();
                                         int porN = (int) (por*100) + 10;
                                         if( porN < 300 ) disp.modZoomin(porN);
                                         disp.pres(-2);
                                         System.out.println("Porcentaje: "+porN);
                                     }}
        );

        zoomOut.addActionListener(new ActionListener() {
                                      public void actionPerformed(ActionEvent e) {
                                          System.out.println("Button Zoom Out");
                                          double por = disp.getPorc();
                                          int porN = (int) (por*100) - 10;
                                          if( porN > 0 ) disp.modZoomout(porN);
                                          disp.pres(-2);
                                          System.out.println("Porcentaje: "+porN);
                                      }}
        );

        compilar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.out.println("Button Compilar");
                Automata aut = new Automata();
                int result;
          /*if(disp.verificar_PN())
             JOptionPane.showMessageDialog(null,"es correcta","RED DE PETRI",JOptionPane.INFORMATION_MESSAGE);
          else
              JOptionPane.showMessageDialog(null,"NO es correcta","RED DE PETRI",JOptionPane.INFORMATION_MESSAGE);*/

                disp.Compuesto();
                String ruta = getCurrentPath();
                JFileChooser c = new JFileChooser(ruta);
                c.addChoosableFileFilter(new ArchFilterEca());
                result = c.showOpenDialog(MINIECAPNSim.this);

                if(result == JFileChooser.CANCEL_OPTION)
                    System.out.println("CANCELAR");

                else if(result == JFileChooser.ERROR_OPTION)
                    System.out.println("ERROR");

                else if(result == JFileChooser.APPROVE_OPTION){
                    String fileECA = c.getCurrentDirectory().toString()+"/"+ c.getSelectedFile().getName();
                    String ct = aut.readFile(fileECA);
                    if(!ct.equals("")) {
                        int _ = aut.compile(ct);
                        String fileOBJ = (fileECA.substring(0, fileECA.indexOf("."))).concat(".obj");
                        if(_==1) {
                            System.out.println("Se va a guardar el archivo... " + fileOBJ);
                            aut.guardar(fileOBJ);
                            JOptionPane.showMessageDialog(null,"Se creó correctamente el diagrama");
                   /* abrirECA(fileOBJ);
                    disp.IncidenceMatrix1( );
                    try{
                    disp.guardarMatrizIncidencia( "salida.mtx" );
                    } catch( Exception ee ){
                      System.err.println( "No se pudo guardar la matriz" );
                    }*/
                        }
                        else {
                            System.out.println("Error. Existen errores en la compilacion. No se guarda el archivo");
                            JOptionPane.showMessageDialog(null,"Existen errores en la compilacion. No se guardó el archivo","Error",JOptionPane.ERROR_MESSAGE);
                        }
                    }
                }
            }
        });

        crear.addActionListener(new ActionListener() {
                                    public void actionPerformed(ActionEvent e) {
                                        System.out.println("Button Crear reglas");
                                    }}
        );

        color.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                Color nuevoColor = JColorChooser.showDialog(null,"Color de líneas", disp.nuevoColor);

                if (nuevoColor != null) {
                    disp.nuevoColor=nuevoColor;
                    repaint();
                }
            }
        });
        bFile.add(nuevo);
        bFile.add(abrir);
        bFile.add(guardar);
        bFile.add(guardarC);
        bFile.add(zoomIn);
        bFile.add(zoomOut);
        bFile.add(compilar);
        bFile.add(checar);

        //bFile.add(crear);
        bFile.add(color);

        //Agregar Menu de Arriba
        cp.add(bFile, BorderLayout.NORTH);

//** Edition buttons, eltos menu de los botones de la red
        JToolBar eltos = new JToolBar(1);
        final JButton place = new JButton(new ImageIcon(ubic + "place1.gif"));
        place.setToolTipText("Insert Place");
        final JButton transition = new JButton(new ImageIcon(ubic + "tran.gif"));
        transition.setToolTipText("Insert Transition");
        final JButton arc = new JButton(new ImageIcon(ubic + "arc.gif"));
        arc.setToolTipText("Insert Arc");
        final JButton token = new JButton(new ImageIcon(ubic + "token.gif"));
        token.setToolTipText("Insert Token");
        final JButton cursor = new JButton(new ImageIcon(ubic + "editIcon.gif"));
        cursor.setToolTipText("Selection");
   /* final JButton mover = new JButton(new ImageIcon(ubic + "editIcon.gif"));
    mover.setToolTipText("Mover");*/
//    cursor.setEnabled(false);
        final JButton suprimir = new JButton(new ImageIcon(ubic + "remove.gif"));
        suprimir.setToolTipText("Delete");

        //Al dar click en el boton de Place
        place.addActionListener(new ActionListener() {
                                    public void actionPerformed(ActionEvent e) {
                                        System.out.println("Button Place");
                                        disp.pres(0);
                                        cursor.setEnabled(true);
                                    }}
        );

        //Al dar click en el boton de Transicion
        transition.addActionListener(new ActionListener() {
                                         public void actionPerformed(ActionEvent e) {
                                             System.out.println("Button Transition");
                                             disp.pres(1);
                                             cursor.setEnabled(true);

                                         }}
        );
        //Al dar click en el boton de Arc
        arc.addActionListener(new ActionListener() {
                                  public void actionPerformed(ActionEvent e) {
                                      System.out.println("Button Arc");
                                      disp.pres(2);
                                      cursor.setEnabled(true);
                                  }}
        );

        token.addActionListener(new ActionListener() {
                                    public void actionPerformed(ActionEvent e) {
                                        System.out.println("Button Token");
                                        disp.pres(3);
                                        cursor.setEnabled(true);
                                    }}
        );

        cursor.addActionListener(new ActionListener() {
                                     public void actionPerformed(ActionEvent e) {
                                         System.out.println("Button Cursor");
                                         disp.pres(-1);
                                         cursor.setEnabled(true);
                                     }}
        );
        //Para mover algo va a hacer en -3

        suprimir.addActionListener(new ActionListener() {
                                       public void actionPerformed(ActionEvent e) {
                                           System.out.println("Button Suprimir");
                                           disp.borrar();
                                       }}
        );

        eltos.add(place);
        eltos.add(transition);
        eltos.add(arc);
        eltos.add(token);
        eltos.add(cursor);
//    eltos.add(mover);
        eltos.add(suprimir);


// ejec menu de las funciones de ejecución
        JToolBar ejec = new JToolBar(1);
        final JButton play = new JButton(new ImageIcon(ubic + "Play16.gif"));
        play.setToolTipText("Play");
        final JButton pause = new JButton(new ImageIcon(ubic + "Pause16.gif"));
        pause.setToolTipText("Pause");
        pause.setEnabled(false);
        final JButton stop = new JButton(new ImageIcon(ubic + "Stop16.gif"));
        stop.setToolTipText("Stop");
        stop.setEnabled(false);
        final JButton reset = new JButton(new ImageIcon(ubic + "clear.gif"));
        reset.setToolTipText("Reset");

        play.addActionListener(new ActionListener() {
                                   public void actionPerformed(ActionEvent e) {
                                       System.out.println("Button Play");

                                       if(disp.play()){
                                           place.setEnabled(false);
                                           transition.setEnabled(false);
                                           arc.setEnabled(false);
                                           token.setEnabled(false);
                                           play.setEnabled(false);
                                           pause.setEnabled(true);
                                           stop.setEnabled(true);
                                           reset.setEnabled(true);
                                           //     mover.setEnabled(false);
                                           suprimir.setEnabled(false);
                                           cursor.setEnabled(false);
                                       }else{
                                           place.setEnabled(true);
                                           transition.setEnabled(true);
                                           arc.setEnabled(true);
                                           token.setEnabled(true);
                                           play.setEnabled(true);
                                           pause.setEnabled(false);
                                           stop.setEnabled(false);
                                           reset.setEnabled(false);
                                           //       mover.setEnabled(true);
                                           suprimir.setEnabled(true);
                                           cursor.setEnabled(true);
                                       }
                                   }}
        );

        pause.addActionListener(new ActionListener() {
                                    public void actionPerformed(ActionEvent e) {
                                        System.out.println("Button Pause");
                                    }}
        );

        stop.addActionListener(new ActionListener() {
                                   public void actionPerformed(ActionEvent e) {
                                       System.out.println("Button Stop");
                                       place.setEnabled(true);
                                       transition.setEnabled(true);
                                       arc.setEnabled(true);
                                       token.setEnabled(true);
                                       cursor.setEnabled(true);
                                       play.setEnabled(true);
                                       pause.setEnabled(false);
                                       stop.setEnabled(false);
                                       reset.setEnabled(true);
                                       //      mover.setEnabled(true);
                                       suprimir.setEnabled(true);
                                       disp.stop();

                                   }}
        );

        reset.addActionListener(new ActionListener() {
                                    public void actionPerformed(ActionEvent e) {
                                        System.out.println("Button Reset");
                                    }}
        );

        ejec.add(play);
        ejec.add(pause);
        ejec.add(stop);
        ejec.add(reset);

        JPanel oper = new JPanel();
        oper.setLayout(new FlowLayout());
        oper.setLayout(new BoxLayout(oper, BoxLayout.Y_AXIS));
        oper.add(eltos);
        oper.add(ejec);
        //Agregar tabla del lado izquierdo
        cp.add(oper, BorderLayout.WEST);



//Parte en la que agrega el panel
        disp.setBackground(Color.WHITE);
        JScrollPane scroller = new JScrollPane(disp);
        scroller.setPreferredSize(new Dimension(300,300));
        cp.add(scroller, BorderLayout.CENTER);


//** Menus bar
        JMenuBar mb = new JMenuBar();
        JMenu[] menu = { new JMenu("File"), new JMenu("Edit"), new JMenu("Insert"),  new JMenu("Execution"), new JMenu("Tools"), new JMenu("Help") };
        menu[0].setMnemonic(KeyEvent.VK_F);
        menu[1].setMnemonic(KeyEvent.VK_E);
        menu[2].setMnemonic(KeyEvent.VK_I);
        menu[3].setMnemonic(KeyEvent.VK_X);
        menu[4].setMnemonic(KeyEvent.VK_T);
        menu[5].setMnemonic(KeyEvent.VK_H);

        JMenuItem[] items = { new JMenuItem("New", KeyEvent.VK_N),
                new JMenuItem("Open"),
                new JMenuItem("Save"),
                new JMenuItem("Save as"),

                new JMenuItem("Data Base Connection"),

                new JMenuItem("Import AR", KeyEvent.VK_I),
                new JMenuItem("Quit"),
                new JMenuItem("Select"),
                new JMenuItem("Move"),
                new JMenuItem("Delete"),
                new JMenuItem("Zoom"),
                new JMenuItem("Place"),
                new JMenuItem("Transition"),
                new JMenuItem("Arc"),
                new JMenuItem("Tokens"),
                new JMenuItem("Play"),
                new JMenuItem("Pause"),
                new JMenuItem("Stop"),
                new JMenuItem("Reset"),
                new JMenuItem("Generar Matriz de Incidencia"),
                new JMenuItem("Guardar Matriz de Incidencia"),
                new JMenuItem("Change Color"),
                new JMenuItem("ECAPNSim help"),
                new JMenuItem("About ECAPNSim")
        };

        ActionListener f1 = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.out.println("Menu Nuevo");
                int re = preguntar();
                if(re != JOptionPane.CANCEL_OPTION && re != JOptionPane.DEFAULT_OPTION) {
                    disp.limpiar(true);
                    namepnj = new String();
                }
            }
        };

        ActionListener f2 = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.out.println("Menu Abrir");
                abrirECA();
                disp.IncidenceMatrix1( );
                try{
                    disp.guardarMatrizIncidencia( "salida.mtx" );
                } catch( Exception ee ){
                    System.err.println( "No se pudo guardar la matriz" );
                }

                System.out.println( "ESPERO EN IMPORTAR" );
                suprimir.setEnabled(true);
                cursor.setEnabled(true);
                abrirEPS();

            }
        };

        ActionListener f3 = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.out.println("Menu Guardar");
                if(namepnj.equals("")) {
                    guardarEPS();
                } else {
                    try {
                        disp.guardar(namepnj);
                    } catch (Exception ioe) {
                        System.out.println("No se guardó el archivo...");
                        JOptionPane.showMessageDialog(null,"No se guardó el archivo","Error",JOptionPane.ERROR_MESSAGE);
                        ioe.printStackTrace();
                    }
                }

            }
        };

        ActionListener f4 = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.out.println("Menu Guardar como");
                guardarEPS();
            }
        };


        ActionListener f5 = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.out.println("Menu Database");


                java.awt.EventQueue.invokeLater(new Runnable() {
                    public void run() {

                        final JFrame DBC=new DbConnection();
                        DBC.setVisible(true);

                        DBC.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                        DBC.addWindowListener(new WindowListener() {

                            public void windowOpened(WindowEvent e) {}

                            public void windowClosing(WindowEvent e) {}

                            public void windowClosed(WindowEvent e) {

                                disp.DBs=((DbConnection)DBC).getDBs();
                                disp.TBs=((DbConnection)DBC).getTBs();
                                disp.DataBaseSelected=((DbConnection)DBC).getBD();
                                disp.DatabaseMagnament=((DbConnection)DBC).getSGBD();
                                System.out.println(disp.DatabaseMagnament);

                            }

                            public void windowIconified(WindowEvent e) {}

                            public void windowDeiconified(WindowEvent e) {}

                            public void windowActivated(WindowEvent e) {}

                            public void windowDeactivated(WindowEvent e) {}
                        });
                    }
                });

            }
        };



        ActionListener f6 = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.out.println("Menu Importar AR");


            }
        };


        ActionListener f7 = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.out.println("Menu Salir");
                System.exit(0);
            }
        };


        ActionListener e1 = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.out.println("Menu Seleccionar");
            }
        };

        ActionListener e2 = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.out.println("Menu Eliminar");
            }
        };

        ActionListener e3 = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.out.println("Menu Limpiar");
            }
        };

        ActionListener e4 = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.out.println("Menu Zoom");
            }
        };


        ActionListener i1 = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.out.println("Menu Place");
                disp.pres(0);
                cursor.setEnabled(true);
            }
        };

        ActionListener i2 = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.out.println("Menu Transition");
                disp.pres(1);
                cursor.setEnabled(true);
            }
        };

        ActionListener i3 = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.out.println("Menu Arc");
                disp.pres(2);
                cursor.setEnabled(true);
            }
        };

        ActionListener i4 = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.out.println("Menu Tokens");
            }
        };


        ActionListener x1 = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.out.println("Menu Play");
            }
        };

        ActionListener x2 = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.out.println("Menu Pause");
            }
        };

        ActionListener x3 = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.out.println("Menu Stop");
            }
        };

        ActionListener x4 = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.out.println("Menu Reset");
            }
        };

        ActionListener t1 = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.out.println("Menu Generar Matriz de Incidencia");
                disp.IncidenceMatrix();
                if(disp.verificar_red())
                    System.out.println("red correcta");
                else
                    System.out.println("Error en la red");
            }
        };

        ActionListener t2 = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.out.println("Menu Guardar Matriz de Incidencia");
            }
        };

        ActionListener t3 = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.out.println("Cambiar Color");
                Color nuevoColor = JColorChooser.showDialog(null,"Color de líneas", disp.nuevoColor);
                if (nuevoColor != null) {
                    disp.nuevoColor=nuevoColor;
                    repaint();
                }
            }
        };

        ActionListener h1 = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.out.println("Menu Acerca de");
            }
        };


        ActionListener h2 = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.out.println("Ayuda");
            }
        };

/*Los Actionlistener declarados arriba llevan un orden de:
 *
 * f File , e Edit , i Insert, x Execicion, t Tools, h Help
 *
 * y un numero
 */
        //Botones de File
        items[0].addActionListener(f1);
        menu[0].add(items[0]);
        items[1].addActionListener(f2);
        menu[0].add(items[1]);
        items[2].addActionListener(f3);
        menu[0].add(items[2]);
        items[3].addActionListener(f4);
        menu[0].add(items[3]);
        items[4].addActionListener(f5);
        menu[0].add(items[4]);
        menu[0].addSeparator();
        items[5].addActionListener(f6);
        menu[0].add(items[5]);

        items[6].addActionListener(f7);
        menu[0].add(items[6]);

        //Botones de Edit
        items[7].addActionListener(e1);
        menu[1].add(items[7]);
        items[8].addActionListener(e2);
        menu[1].add(items[8]);
        items[9].addActionListener(e3);
        menu[1].add(items[9]);
        menu[1].addSeparator();
        items[10].addActionListener(e4);
        menu[1].add(items[10]);

        //Botones de Insert
        items[11].addActionListener(i1);
        menu[2].add(items[11]);
        items[12].addActionListener(i2);
        menu[2].add(items[12]);
        items[13].addActionListener(i3);
        menu[2].add(items[13]);
        items[14].addActionListener(i4);
        menu[2].add(items[14]);


        //Botones de Execucion
        items[15].addActionListener(x1);
        menu[3].add(items[15]);
        items[16].addActionListener(x2);
        menu[3].add(items[16]);
        items[17].addActionListener(x3);
        menu[3].add(items[17]);
        items[18].addActionListener(x4);
        menu[3].add(items[18]);


        //Botones de Tools
        items[19].addActionListener(t1);
        menu[4].add(items[19]);
        items[20].addActionListener(t2);
        menu[4].add(items[20]);
        menu[4].addSeparator();
        items[21].addActionListener(t3);
        menu[4].add(items[21]);

        //Boton de ayuda
        items[22].addActionListener(h1);
        menu[5].add(items[22]);
        items[23].addActionListener(h2);
        menu[5].add(items[23]);


        for (int i=0; i<menu.length; i++)
            mb.add(menu[i]);

        setJMenuBar(mb);

        mb.setVisible(true);
        mb.validate();

    }
    public int preguntar(){
        int resp = JOptionPane.NO_OPTION;
        Object[] options = { "Si", "No", "Cancelar" };
        if(!DrawArea.vp.isEmpty()||!DrawArea.vt.isEmpty())
        {
            resp = JOptionPane.showOptionDialog(null, "Desea guardar los cambios?", "Aviso",
                    JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE, null, options, options[0]);
            if(resp == JOptionPane.YES_OPTION) {
                if(namepnj.equals("")) {
                    guardarEPS();
                } else {
                    try {
                        disp.guardar(namepnj);
                    } catch (Exception ioe) {
                        System.out.println("No se guard� el archivo...");
                        ioe.printStackTrace();
                    }
                }
            }
        }
        System.out.println("resp: " + resp + "; JOptionPane.CANCEL_OPTION: " + JOptionPane.CANCEL_OPTION);
        return resp;
    }

    public void abrirEPS(){
        String ruta = getCurrentPath();
        JFileChooser c = new JFileChooser(ruta);
        c.addChoosableFileFilter(new ArchFilterPnj());
        String fileEPS = null;
        int rVal = c.showOpenDialog(MINIECAPNSim.this);
        try{
            String file =  c.getSelectedFile().getName();
            String dir = c.getCurrentDirectory().toString();
            fileEPS = dir+"/"+file;
        }catch(NullPointerException e){}
        if(rVal == JFileChooser.APPROVE_OPTION) {
            System.out.println("Archivo: " + c.getSelectedFile().getName());
            System.out.println("Directorio: " + c.getCurrentDirectory().toString());
            if(c.getSelectedFile().exists()) {
                try {
                    disp.abrir(fileEPS);
                } catch (Exception ioe) {
                    System.out.println("No se abrió el archivo...");
                }
                namepnj = c.getSelectedFile().getName();
                this.setTitle("ECAPNSim: " + namepnj);
            } else {System.out.println("El archivo no existe..."); }
        }
        if(rVal == JFileChooser.CANCEL_OPTION) {
            System.out.println("You pressed cancel");
        }

    }
    public void guardarEPS() {
        String ruta = getCurrentPath();
        JFileChooser c = new JFileChooser(ruta);
        c.addChoosableFileFilter(new ArchFilterPnj());
        int rVal = c.showSaveDialog(MINIECAPNSim.this);
        String filePNJ = null;
        try{
            String file =  c.getSelectedFile().getName();
            String dir = c.getCurrentDirectory().toString();
            filePNJ = dir+"/"+file;
        }catch(NullPointerException e){}
        if(rVal == JFileChooser.APPROVE_OPTION) {
            System.out.println("Archivo: " + c.getSelectedFile().getName());
            System.out.println("Directorio: " + c.getCurrentDirectory().toString());
            try {
                disp.guardar(filePNJ);
            } catch (Exception ioe) {
                System.out.println("No se guardó el archivo...");
                JOptionPane.showMessageDialog(null,"No se guardó el archivo","Error",JOptionPane.ERROR_MESSAGE);
                ioe.printStackTrace();
            }
            namepnj = c.getSelectedFile().getName();
            this.setTitle("ECAPNSim: " + namepnj);
        }
        if(rVal == JFileChooser.CANCEL_OPTION) {
            System.out.println("You pressed cancel");
        }
    }

    public void abrirECA() {
        int resp = 0;
        if(preguntar() != JOptionPane.CANCEL_OPTION) {
            String ruta = getCurrentPath();
            System.out.println("La ruta es:"+ruta);
            JFileChooser c = new JFileChooser(ruta);
            c.addChoosableFileFilter(new ArchFilterObj());
            int rVal = c.showOpenDialog(MINIECAPNSim.this);
            String fileECA = c.getCurrentDirectory().toString()+"/"+ c.getSelectedFile().getName();
            if(rVal == JFileChooser.APPROVE_OPTION) {
                System.out.println("Archivo: " + c.getSelectedFile().getName());
                System.out.println("Directorio: " + c.getCurrentDirectory().toString());
                if(c.getSelectedFile().exists()) {
                    try {
                        resp = disp.importar(fileECA);
                    } catch (Exception ioe) {
                        System.out.println("No se abrió el archivo...");
//              ioe.printStackTrace();
                    }
                    switch(resp) {
                        case 0 : System.out.println("Respuesta 0");
                            namepnj = new String();
                            break;
                        case 1 : System.out.println("Respuesta 1");
                            break;
                        case 2 : System.out.println("Respuesta 2");
                            break;
                        case 3 : System.out.println("Respuesta 3");
                            break;
                    }
                } else {System.out.println("El archivo no existe..."); }
            }
            if(rVal == JFileChooser.CANCEL_OPTION) {
                System.out.println("You pressed cancel");
            }
        }
    }

    public String getCurrentPath() {
        File f1 = new File("pr.txt");
        String ap = f1.getAbsolutePath();
        int lio = ap.lastIndexOf(f1.separatorChar);
        String oap = ap.substring(0,lio);
        return oap;
    }



    public static void main(String[] args){
        MINIECAPNSim eps = new MINIECAPNSim();
        eps.setVisible(true);
        eps.setSize(new Dimension(1024,571));
    }

}
