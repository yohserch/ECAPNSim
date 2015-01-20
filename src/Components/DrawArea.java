package Components;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.Vector;
import javax.swing.*;
import java.util.ArrayList;

public class DrawArea extends JPanel implements MouseMotionListener {

    Vector<String> DBs;
    Vector<String> TBs;
    String DataBaseSelected;
    int DatabaseMagnament;//0-Mysql  2-postgres
    int cont = 0;
    int tamaño = 30;
    ArcPuro a, b;
    int z;
    int elto = -2;
    int indObjSel, tipoObjSel; // 0:Place, 1:Transition, 2:ArcoPT, 3:ArcoTP
    PlacePuro p = null;
    int x, y, x1 = 0, y1 = 0, x2 = 0, y2 = 0, x_dif = 0, y_dif = 0;
    static Vector vp, vt;
    Punto pto, pto2;
    double porc = 1.0;
    boolean changed = false;
    boolean dentroPlace = false, dentroTrans = false;
    boolean esta_selec = false, es_rectangulo = false;
    PlacePuro ptoP;
    TransitionPura ptoT;
    Vector vaPT, vaTP;
    private Dimension size;
    SelectRec sel_rec;
    int inc = 0;
    int indice_mover = 0; //Va a almacenar el indice de la transicion o de la place de la cual se va a mover como principal
    int rt = 3;
    Vector <ControlAnimacion> vControl, vControlT; //Vector para almacenar los movimientos de play
    Timer timerPT; //Timer para mover los tokens
    Color nuevoColor = Color.yellow;
    boolean se_puede;
    boolean esArco;

    Vector ev, rules;
    IncidenceMatrix matrix;
    Vector inputArcsT, outputArcsT;

    int nArb, nxt = 0, nyt = 0;
    Vector eltosPorNivel;
    Vector arboles, hojas, PlacesConsiderados, TransConsideradas;
    Vector headNodes;

    static Vector tables;

    boolean updated = false;

    //$Constructor
    public DrawArea() {
        super();
        size = new Dimension(0, 0);
        vp = new Vector();
        vt = new Vector();
        vaTP = new Vector();
        vaPT = new Vector();
        sel_rec = new SelectRec();

        vControl = new Vector();
        vControlT = new Vector();
         /*
          * Algoritmo para poder ejecutar el play y q comenzar con los tokens
          */
        timerPT = new Timer(100, new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                int q = 0;

                for (int i = 0; i < vControl.size(); i++) {
                    //Verifica si esta en el pt
                    if (checarTrans(vControl.elementAt(i).getPto(), vControl.elementAt(i).getTransition()))//Este if sirve para verificar si el token llego a trans
                    {
                        //Si es una transicion simple
                        if (((TransitionPura) vt.elementAt(vControl.elementAt(i).getTransition())).getTipo() == 0) {
                            vControl.elementAt(i).incrementa();
                            repaint();
                        }
                        //Si es una transicion compuesta
                        if (((TransitionPura) vt.elementAt(vControl.elementAt(i).getTransition())).getTipo() == 1 || ((TransitionPura) vt.elementAt(vControl.elementAt(i).getTransition())).getTipo() == 2) {

                            boolean se_puede = false;
                            //Verificar que tenga la places tokens si no ya no la aumenta
                            for (int y = 0; y < vaPT.size(); y++) {
                                if (vControl.elementAt(i).getTransition() == ((ArcPuro) vaPT.elementAt(y)).getTransition()) {
                                    vControl.elementAt(i).incrementa();
                                }
                            }
                        }


                    }
                    //Si llego a la transicion
                    else {
                        System.out.println("Termino:  " + vControl.elementAt(i).getTransition());
                        vControl.remove(i);
                        evaluar();
                        repaint();
                    }


                }

                for (int i = 0; i < vControlT.size(); i++) {
                    if (checarPlace(vControlT.elementAt(i).getPto(), vControlT.elementAt(i).getPlace()))//Este if sirve para verificar si el token llego a trans
                    {
                        vControlT.elementAt(i).incrementa();
                        repaint();
                    } else {
                        vControlT.remove(i);
                        evaluar();
                        repaint();
                    }

                }

            }
        });

        addMouseListener(new MouseAdapter() {
            //Cuando se da click en place o en transicion los manda a dibujar y los agrega a sus respectivos vectores
            public void mousePressed(MouseEvent e) {
                //Detectar boton nderecho
                PlacePuro p = null;
                TransitionPura t = null;

                //*** ALGORITMO para ubicar los componentes dentro de una cuadrícula
                //*** Ubicamos los puntos donde de dio click
                x = ajustaTamI(e.getX());
                y = ajustaTamI(e.getY());


                //*** Obtenemos el tamaño del panel
                //*** d.width ancho, d.height alto
                Dimension d = Toolkit.getDefaultToolkit().getScreenSize();

                x = ajustaX(x);
                y = ajustaY(y);

                //*** Con las nuevas coordenadas creamos un nuevo punto
                pto = new Punto(x, y);
                //Con este if se detecta si se pareto el click derecho ubicar que place y que transicion para poder
                //desplegar la ventana de las opciones de cada una
                if (e.isMetaDown()) {
                    System.out.println("Se apreto boton derecho");
                    if (checkPlace(pto)) {
                        System.out.println("Se dio click derechi en una place");
                    }
                    if (checkTrans(pto)) {
                        System.out.println("Se dio click derecho en una transicion");
                    }
                } else {
                    switch (elto) {
                        case -1:  // Cursor
                            es_rectangulo = false;
                            pto.nvoX(x);
                            pto.nvoY(y);
                            System.out.println("Seleccionando??...");
                            if ((esta_selec && (pto.valX() > sel_rec.getX()) && (pto.valX() < sel_rec.getX1()) && ((pto.valY() > sel_rec.getY()) && (pto.valY() < sel_rec.getY1()))))
                                elto = 4;
                            if (!verificar(pto)) {
                                _selec();
                                if (checkPlace(pto)) {
                                    //Verifica si hay algo seleccionado para limpiar
                                    ((PlacePuro) vp.elementAt(ptoP.getIndex())).setSelected(true);
                                    esta_selec = true;
                                }
                                if (checkTrans(pto)) {

                                    ((TransitionPura) vt.elementAt(ptoT.getIndice())).setSeleccion(true);
                                    esta_selec = true;
                                }
                            } else {
                                sel_rec.agregar(pto.valX(), pto.valY());
                                es_rectangulo = true;
                            }

                            break;

                        case 0:  // Place
                            if (verificar(pto)) {//Este if es para verificar y que no se pueda a poner un Place o una transicion en donde ya hay algo
                                p = new PlacePuro(pto, vp.size());
                                vp.addElement(p);
                                scroll(new Rectangle(x, y, ajustaTam(30), ajustaTam(30)));//Srive para la barra de desplazamiento aumentarla
                            } else
                                JOptionPane.showMessageDialog(null, "No se puede", "Error", JOptionPane.ERROR_MESSAGE);
                            break;
                        case 1: //transition
                            if (verificar(pto)) {
                                t = new TransitionPura(pto, vt.size());
                                vt.addElement(t);
                                scroll(new Rectangle(x, y, ajustaTam(30), ajustaTam(30)));//Sirve para aumentar la barra de desplazamiento
                            } else
                                JOptionPane.showMessageDialog(null, "No se puede", "Error", JOptionPane.ERROR_MESSAGE);
                            break;
                        case 3:
                            if (checkPlace(pto)) {
                                System.out.println("Elemento seleccionado3 " + elto);
                                AgregarCopias();
                                //if(!IsTransCopy(ptoP))
                                ptoP.setNumTokens(1);
                            }
                            break;

                        case 4:

                            //Si hay algo ya seleccionado
                               /*
                                * Verifica los puntos si es transicion o place lo que esta almacenado
                                */
                            if (esta_selec && !verificar(pto)) {
                                x1 = pto.valX();
                                y1 = pto.valY();
                                if (checkPlace(pto)) {
                                    indice_mover = ptoP.getIndex();
                                }
                                if (checkTrans(pto)) {
                                    indice_mover = ptoT.getIndice();
                                }
                            }
                            break;

                    }
                }
                repaint();
            }

            //Funciones de cuando se suelta el mouse
            public void mouseReleased(MouseEvent e) {
                //cambiamos el estado de la variable para que deje de dibujar la linea punteada
                esArco = false;

                dentroPlace = checkPlace(pto);
                dentroTrans = checkTrans(pto);
                changed = false;

                //ajustamos al zoom y a la cuadrícula
                int xx = ajustaTam(e.getX());
                int yy = ajustaTam(e.getY());
                xx = ajustaX(xx);
                yy = ajustaY(yy);
                Punto pt = new Punto(xx, yy);

                if (elto == 2 && (dentroPlace || dentroTrans)) {
                    if (dentroPlace && checkTrans(pt)) {
                        //Este if para verificar si ya existe una transicion compuesta
                        System.out.println("Entro de place a transicion");


                        if (existeTransCopia(ptoP.getIndex())) {
                            System.out.println("Existe una transicion");
                            //Parte para dibujar la transicion
                            Vector<Integer> arcos = obtenerArcoPT(ptoP.getIndex());
                            Punto pplace = ptoP.getOrigen();
                            int x = pplace.valX();
                            int y = pplace.valY();
                            Punto trans = new Punto(x, y - 30);
                            ptoP.getOrigen().nvoX(x);
                            ptoP.getOrigen().nvoY(y - 60);
                            Punto Trans = new Punto(xx, yy);
                            Punto place1 = new Punto(x - 30, y);
                            Punto place2 = new Punto(x + 30, y);
                            PlacePuro PlaceC = new PlacePuro(place1, vp.size(), -1, "Copy_of " + ptoP.getNombre(), "");
                            PlaceC.setTipo(14);
                            vp.add(PlaceC);
                            PlacePuro PlaceC2 = new PlacePuro(place2, vp.size(), -1, "Copy_of " + ptoP.getNombre(), "");
                            PlaceC2.setTipo(14);
                            vp.add(PlaceC2);
                            ArcPuroPT arco = (ArcPuroPT) vaPT.elementAt(arcos.get(0));
                            ArcPuroPT arc = new ArcPuroPT(place2, Trans, vaPT.size(), PlaceC.getIndex(), ptoT.getIndice());
                            vaPT.add(arc);
                            Punto T = ((TransitionPura) vt.elementAt(arco.getTransition())).getCentro();
                            ((ArcPuroPT) vaPT.elementAt(arcos.get(0))).mover(place1, T);
                            ((ArcPuroPT) vaPT.elementAt(arcos.get(0))).valBeta();
                            ((ArcPuroPT) vaPT.elementAt(arcos.get(0))).calcDrawLine();
                            ((ArcPuroPT) vaPT.elementAt(arcos.get(0))).calcX();
                            ((ArcPuroPT) vaPT.elementAt(arcos.get(0))).calcY();

                            TransitionPura t = new TransitionPura(trans, vt.size());
                            t.setTipo(2);
                            vt.addElement(t);
                            ArcPuroTP a1 = new ArcPuroTP(trans, place1, vaTP.size(), PlaceC.getIndex(), t.getIndice());
                            vaTP.add(a1);
                            ArcPuroTP a2 = new ArcPuroTP(trans, place2, vaTP.size(), PlaceC2.getIndex(), t.getIndice());
                            vaTP.add(a2);
                            ArcPuroPT a3 = new ArcPuroPT(ptoP.getOrigen(), trans, vaPT.size(), ptoP.getIndex(), t.getIndice());
                            vaPT.add(a3);
                            ptoP = PlaceC2;
                        }
                        if (existeTransCompuesta(ptoP.getIndex())) {
                            System.out.println("Existe una transicion compuesta");
                            int indice_P = 0;
                            boolean agree = false;
                            if (tiene_algo(ptoT.getIndice())) {
                                indice_P = indice_arco(ptoT.getIndice());
                                if (indice_P == -1) {
                                    System.out.println("No lo encontro");
                                }
                                agree = true;
                            }
                            //llamamos el metodo para el caso especial
                            nuevasTrans(ptoT.getIndice(), ptoP.getIndex(), indice_P, agree);

                        } else {
                            a = new ArcPuroPT(ptoP.getOrigen(), ptoT.getCentro(), vaPT.size(), ptoP.getIndex(), ptoT.getIndice());
                            if (existeP(a.getPlace())) {
                                if (existeTransSimple(ptoP.getIndex())) {
                                    //Verificar si tiene algo la transicion
                                    int indice_P = 0;
                                    boolean agree = false;
                                    if (tiene_algo(ptoT.getIndice())) {
                                        indice_P = indice_arco(ptoT.getIndice());
                                        if (indice_P == -1) {
                                            System.out.println("No lo encontro");
                                        }
                                        agree = true;
                                    }
                                    if (tiene_algoT(ptoP.getIndex())) {
                                        int indice_A = indice_arcoT(ptoP.getIndex());
                                        if (indice_A == -1) {
                                            System.out.println("No lo encontro");
                                        } else {
                                            agregarTransCompuesta(ptoP.getIndex(), true, 60, indice_A, agree, indice_P, true);
                                            ((TransitionPura) vt.elementAt(ptoT.getIndice())).setSeleccion(true);
                                            borrar_selec();
                                            repaint();
                                        }
                                    } else {
                                        agregarTransCompuesta(ptoP.getIndex(), false, 0, 0, agree, indice_P, true);
                                        ((TransitionPura) vt.elementAt(ptoT.getIndice())).setSeleccion(true);
                                        borrar_selec();
                                    }

                                }
                            } else {
                                //Verificar tipo de transicion si es compuesta o es simple o ciclo para transicion a muchos places
                                verificar_tipo(ptoT.getIndice(), ptoP.getIndex(), false);
                                ((TransitionPura) vt.elementAt(ptoT.getIndice())).setPlaceConec(1);
                                vaPT.addElement(a);

                            }
                        }
                    }

                    if (dentroTrans && checkPlace(pt)) {
                        a = new ArcPuroTP(ptoT.getCentro(), ptoP.getOrigen(), vaTP.size(), ptoP.getIndex(), ptoT.getIndice());
                        if (existeATP(a))
                            JOptionPane.showMessageDialog(null, "El Arco ya existe!, esta accion sera ignorada.", "Conflicto!", JOptionPane.ERROR_MESSAGE);
                        else {
                            verificar_tipo(ptoT.getIndice(), ptoP.getIndex(), true);
                            vaTP.addElement(a);
                        }
                    }
                }
                //fUNCIONES PARA CUANDO SE VA A SELECCIONAR ALGO
                if (elto == -1 && es_rectangulo) {
                    sel_rec.agregarTamanio(pt.valX(), pt.valY());
                 /*
                  * Aqui va a ir la seleccion apartir de rectangulo
                  */
                    //places
                    Punto p0, p1;
                    //Forma para place
                    for (int i = 0; i < vp.size(); i++) {
                        p0 = ajustaTam(((PlacePuro) vp.elementAt(i)).getOrigen());
                        ((PlacePuro) vp.elementAt(i)).setSelected(false);
                        //Verifica si esta dentro del area seleccionada
                        if (((p0.valX() > sel_rec.getX()) && (p0.valX() < sel_rec.getX1())) && ((p0.valY() > sel_rec.getY()) && (p0.valY() < sel_rec.getY1()))) {
                            ((PlacePuro) vp.elementAt(i)).setSelected(true);
                            esta_selec = true;
                        }
                    }

                    //For para las transiciones
                    for (int i = 0; i < vt.size(); i++) {
                        p0 = ajustaTam(((TransitionPura) vt.elementAt(i)).getOrigen());
                        ((TransitionPura) vt.elementAt(i)).setSeleccion(false);
                        //Verifica si esta dentro del area seleccionada
                        if (((p0.valX() > sel_rec.getX()) && (p0.valX() < sel_rec.getX1())) && ((p0.valY() > sel_rec.getY()) && (p0.valY() < sel_rec.getY1()))) {
                            ((TransitionPura) vt.elementAt(i)).setSeleccion(true);
                            esta_selec = true;
                        }
                    }

                    //Lineas de Place a transition
                    for (int i = 0; i < vaPT.size(); i++) {
                        p0 = ajustaTam(((ArcPuroPT) vaPT.elementAt(i)).getPoint1());
                        p1 = ajustaTam(((ArcPuroPT) vaPT.elementAt(i)).getPoint2());
                        ((ArcPuro) vaPT.elementAt(i)).setSelected(false);
                        //Verifica si esta dentro del area seleccionada
                        if ((((p0.valX() > sel_rec.getX()) && (p0.valX() < sel_rec.getX1())) && ((p0.valY() > sel_rec.getY()) && (p0.valY() < sel_rec.getY1()))) || (((p1.valX() > sel_rec.getX()) && (p1.valX() < sel_rec.getX1())) && ((p1.valY() > sel_rec.getY()) && (p1.valY() < sel_rec.getY1())))) {
                            ((ArcPuro) vaPT.elementAt(i)).setSelected(true);
                            esta_selec = true;
                        }
                    }
                    //Lineas de Transition a Place
                    for (int i = 0; i < vaTP.size(); i++) {
                        p0 = ajustaTam(((ArcPuroTP) vaTP.elementAt(i)).getPoint1());
                        p1 = ajustaTam(((ArcPuroTP) vaTP.elementAt(i)).getPoint2());
                        ((ArcPuro) vaTP.elementAt(i)).setSelected(false);

                        //Verifica si esta dentro del area seleccionada
                        if ((((p0.valX() > sel_rec.getX()) && (p0.valX() < sel_rec.getX1())) && ((p0.valY() > sel_rec.getY()) && (p0.valY() < sel_rec.getY1()))) || (((p1.valX() > sel_rec.getX()) && (p1.valX() < sel_rec.getX1())) && ((p1.valY() > sel_rec.getY()) && (p1.valY() < sel_rec.getY1())))) {
                            ((ArcPuro) vaTP.elementAt(i)).setSelected(true);
                            esta_selec = true;
                        }
                    }
                }

                // Funcion cuando esta seleccionado y se va a mover l
                if (elto == 4 && esta_selec) {
                    x2 = e.getX();
                    y2 = e.getY();

                    x2 = ajustaX(x2);
                    y2 = ajustaY(y2);

                    Punto prueba = new Punto(x1, y1);
                    //Parte para verificar si de click dentro de una place y una transcion si no ubica la primera seleccionada
                    System.out.println("Se va a empezar a mover...");
                    if (checkTrans(prueba) || checkPlace(prueba)) {
                        System.out.println("Se dio click en transicion");
                        //Place para acomodar las que se van a mover
                    } else {
                        System.out.println("Se dio click en otro distinto a place o transicion");
                        boolean trans = false;
                        for (int i = 0; i < vt.size(); i++) {
                            //Verifca los if que se estan modificando y aparte se aumenta 15 y 5 para el rectangulo de transicion quede en el centro
                            if (((TransitionPura) vt.elementAt(i)).getSeleccion()) {
                                Punto poa = ((TransitionPura) vt.elementAt(i)).getCentro();
                                //System.out.println(x1+" , "+y1);
                                x1 = poa.valX();
                                y1 = poa.valY();
                                trans = true;
                                break;
                            }
                        }
                        if (!trans) {
                            for (int i = 0; i < vp.size(); i++) {
                                if (((PlacePuro) vp.elementAt(i)).getSelected()) {
                                    Punto poa = ((PlacePuro) vp.elementAt(i)).getOrigen();
                                    System.out.println(x1 + " , " + y1);
                                    x1 = poa.valX();
                                    y1 = poa.valY();
                                    break;
                                }
                            }
                        }
                        esta_selec = false;
                    }

                    x_dif = x2 - x1;
                    y_dif = y2 - y1;
                    //Place para acomodar las que se van a mover
                    for (int i = 0; i < vp.size(); i++) {
                        if (((PlacePuro) vp.elementAt(i)).getSelected()) {
//                        Vector<Integer> v1=obtenerArcoPT(((PlacePuro) vp.elementAt(i)).getIndice());
//                        for(Integer k:v1)
//                        {
//                            ((ArcPuroPT)vaPT.get(k)).setSeleccion(true);
//                        }
//                        Vector<Integer> v2=obtenerArcoTP1(((PlacePuro) vp.elementAt(i)).getIndice());
//                        for(Integer k:v1)
//                        {
//                            ((ArcPuroPT)vaTP.get(k)).setSeleccion(true);
//                        }
                            Punto poa = ((PlacePuro) vp.elementAt(i)).getOrigen();
                            ((PlacePuro) vp.elementAt(i)).mover(poa.valX() + x_dif, poa.valY() + y_dif);
                            ((PlacePuro) vp.elementAt(i)).setSelected(false);

                            verificar_PT(ptoP.getIndex());
                            verificar_TP(ptoP.getIndex());
                            repaint();

                        }
                    }
                    //Transition
                    for (int i = 0; i < vt.size(); i++) {
                        //Verifca los if que se estan modificando y aparte se aumenta 15 y 5 para el rectangulo de transicion quede en el centro
                        if (((TransitionPura) vt.elementAt(i)).getSeleccion()) {
                            Punto poa = ((TransitionPura) vt.elementAt(i)).getOrigen();
                            ((TransitionPura) vt.elementAt(i)).mover(poa.valX() + x_dif + 15, poa.valY() + y_dif + 5);
                            ((TransitionPura) vt.elementAt(i)).setSeleccion(false);

                            verificar_PT_t(ptoT.getIndice());
                            verificar_TP_t(ptoT.getIndice());
                            repaint();
                        }
                    }


                    int place = 0, trans = 0;
                    for (int i = 0; i < vaTP.size(); i++) {
                        if (((ArcPuro) vaTP.elementAt(i)).getSelected()) {
                            place = ((ArcPuro) vaTP.elementAt(i)).getPlace();
                            trans = ((ArcPuro) vaTP.elementAt(i)).getTransition();
                            Punto t = ((TransitionPura) vt.elementAt(trans)).getCentro();
                            Punto p = ((PlacePuro) vp.elementAt(place)).getOrigen();
                            System.out.println("Se va dibujar de: " + t.valX() + " , " + t.valY() + " y va de " + p.valX() + " , " + p.valY());

                            ((ArcPuro) vaTP.elementAt(i)).mover(t, p);
                            ((ArcPuro) vaTP.elementAt(i)).valBeta();
                            ((ArcPuro) vaTP.elementAt(i)).calcDrawLine();
                            ((ArcPuro) vaTP.elementAt(i)).calcX();
                            ((ArcPuro) vaTP.elementAt(i)).calcY();
                            ((ArcPuro) vaTP.elementAt(i)).setSelected(false);
                        }
                    }
                    for (int i = 0; i < vaPT.size(); i++) {
                        if (((ArcPuro) vaPT.elementAt(i)).getSelected()) {
                            System.out.println(((ArcPuro) vaPT.elementAt(i)).getIndice());
                            place = ((ArcPuro) vaPT.elementAt(i)).getPlace();
                            trans = ((ArcPuro) vaPT.elementAt(i)).getTransition();
                            Punto t = ((TransitionPura) vt.elementAt(trans)).getCentro();
                            Punto p = ((PlacePuro) vp.elementAt(place)).getOrigen();
                            System.out.println("Se va dibujar de: " + t.valX() + " , " + t.valY() + " y va de " + p.valX() + " , " + p.valY());

                            ((ArcPuro) vaPT.elementAt(i)).mover(p, t);
                            ((ArcPuro) vaPT.elementAt(i)).valBeta();
                            ((ArcPuro) vaPT.elementAt(i)).calcDrawLine();
                            ((ArcPuro) vaPT.elementAt(i)).calcX();
                            ((ArcPuro) vaPT.elementAt(i)).calcY();

                            ((ArcPuro) vaPT.elementAt(i)).setSelected(false);
                        }
                    }


                }

                repaint();
            }

            // Verifica si un place ya tiene arco definido
            public boolean existeP(int ap) {
                for (int i = 0; i < vaPT.size(); i++)
                    if (((ArcPuroPT) vaPT.elementAt(i)).getPlace() == ap)
                        return true;
                return false;
            }

            //Verfica si existe el place a la misma transicion
            public boolean existeATP(ArcPuro ap) {
                int pl = ap.getPlace(),
                        tr = ap.getTransition();
                for (int i = 0; i < vaTP.size(); i++)
                    if (((ArcPuro) vaTP.elementAt(i)).getPlace() == pl && ((ArcPuro) vaTP.elementAt(i)).getTransition() == tr)
                        return true;
                return false;
            }

            //Verifica que tipo de place es se le manda la transicion q es
            private void verificar_tipo(int indiceT, int indiceP, boolean tp) {
                int r = 1;
                boolean ciclo = false, compuesta = false;
                System.out.println(indiceT + "    " + indiceP);

//////////////////////////////////////////////////////*
                //Se va a colocar de place a transicion
                int trans = 0;
                if (!tp) {
                    //Verificar de Transicion a Place
                    for (int q = 0; q < vaTP.size(); q++) {
                        if (indiceT == ((ArcPuro) vaTP.elementAt(q)).getTransition() && indiceP == ((ArcPuro) vaTP.elementAt(q)).getPlace()) {
                            ciclo = true;
                        }
                    }
                    //Verificar de place a transicion
                    for (int q = 0; q < vaPT.size(); q++) {
                        if (indiceT == ((ArcPuro) vaPT.elementAt(q)).getTransition()) {
                            r++;
                            if (r >= 2) {
                                if (!ciclo) {
                                    if (((TransitionPura) vt.elementAt(indiceT)).getTipo() == 0) {
                                        compuesta = true;
                                        System.out.println("Es la transicion Compuesta: " + indiceT);
                                    } else {
                                        System.out.println("Ya existe la copia");
                                    }
                                }
                            }
                        }
                    }


                }
                if (tp) {
                    for (int q = 0; q < vaPT.size(); q++) {
                        if (indiceT == ((ArcPuro) vaPT.elementAt(q)).getTransition() && indiceP == ((ArcPuro) vaPT.elementAt(q)).getPlace()) {
                            ciclo = true;
                        }
                    }
                    r = 1;
                    for (int q = 0; q < vaTP.size(); q++) {
                        if (indiceT == ((ArcPuro) vaTP.elementAt(q)).getTransition()) {
                            r++;
                            if (r >= 2) {
                                System.out.println("Numero de place conectadas a la transicion" + indiceT + ": " + r);
                                ((TransitionPura) vt.elementAt(indiceT)).setTipo(2);
                                ((TransitionPura) vt.elementAt(indiceT)).mover(
                                        ((TransitionPura) vt.elementAt(indiceT)).getPunto().valX(),
                                        ((TransitionPura) vt.elementAt(indiceT)).getPunto().valY());
                                ((PlacePuro) vp.elementAt(indiceP)).setTipo(14);
                                dibujar_copias(indiceT);

                                if (!verificar_existePT(indiceP)) {
                                    //Agregar Transicion
                                    Punto ptran = ((PlacePuro) vp.elementAt(indiceP)).getOrigen();
                                    int indice = ((PlacePuro) vp.elementAt(indiceP)).getIndex();
                                    int x = ptran.valX();
                                    int y = ptran.valY();

                                    Punto place = new Punto(x, y + 60);
                                    TransitionPura t = new TransitionPura(place, vt.size());
                                    vt.addElement(t);

                                    //Agregar arco de place a transcion
                                    ArcPuroPT arc = new ArcPuroPT(ptran, place, vaPT.size(), indice, vt.size() - 1);
                                    vaPT.addElement(arc);

                                    //Agregar place
                                    Punto ptrans = new Punto(x, y + 120);
                                    p = new PlacePuro(ptrans, vp.size());
                                    vp.addElement(p);

                                    //Agregar arco de transicion a place
                                    ArcPuroTP mod = new ArcPuroTP(place, ptrans, vaTP.size(), vp.size() - 1, vt.size() - 1);
                                    vaTP.addElement(mod);
                                }
                            }
                        }
                    }
                }
                //Si la bandera de ciclo es true dibuja
                if (ciclo) {
                    //PArte para dibujar los componenetes del ciclo
                    //Parte que se agrega la place

                    ((TransitionPura) vt.elementAt(indiceT)).setTipo(2);

                    Punto ptran = ((TransitionPura) vt.elementAt(indiceT)).getCentro();

                    int x = ptran.valX();
                    int y = ptran.valY();
                    Punto ptrans = new Punto(x, y + 60);

                    while (true) {
                        if (!verificar(ptrans)) {
                            ptrans.nvoX(ptrans.valX() + 60);
                        } else {
                            break;
                        }
                    }

                    p = new PlacePuro(ptrans, vp.size());
                    p.setTipo(14);
                    vp.addElement(p);
                    repaint();

                    //Parte de dibujar el Arco
                    ArcPuroTP mod = new ArcPuroTP(ptran, ptrans, vaTP.size(), vp.size() - 1, ((TransitionPura) vt.elementAt(indiceT)).getIndice());
                    vaTP.addElement(mod);

                    //Parte para dibujar la transicion
                    Punto place = new Punto(x + 60, y + 60);
                    while (true) {
                        if (!verificar(place)) {
                            place.nvoX(place.valX() + 60);
                        } else {
                            break;
                        }
                    }
                    TransitionPura t = new TransitionPura(place, vt.size());
                    vt.addElement(t);
                    repaint();

                    //Parte de dibujar el arco de place a transicion
                    ArcPuroPT arc = new ArcPuroPT(ptrans, place, vaPT.size(), vp.size() - 1, vt.size() - 1);
                    vaPT.addElement(arc);

                    //Parte de dibujar el Arco
                    System.out.println("El indice de la transicion es: " + (vt.size() - 1));
                    Punto place1 = ((PlacePuro) vp.elementAt(indiceP)).getOrigen();
                    int inp = ((PlacePuro) vp.elementAt(indiceP)).getIndex();
                    a = new ArcPuroTP(place, place1, vaTP.size(), inp, vt.size() - 1);
                    //vaTP.addElement(mod);

                }
                //Si la bandera de compuesta es true dibuja
                if (compuesta) {
                    System.out.println("Es una transicion compuesta");

                    //Parte que se agrega la place
                    Punto ptran = ((TransitionPura) vt.elementAt(indiceT)).getCentro();
                    ((TransitionPura) vt.elementAt(indiceT)).setTipo(1);
                    int x = ptran.valX();
                    int y = ptran.valY();

                    Punto ptrans = new Punto(x, y + 60);
                    p = new PlacePuro(ptrans, vp.size());
                    p.setTipo(11);
                    vp.addElement(p);
                    repaint();

                    //Parte de dibujar el Arco

                    //Parte para dibujar la transicion
                    Punto place = new Punto(x, y + 120);
                    TransitionPura t = new TransitionPura(place, vt.size());
                    vt.addElement(t);
                    repaint();
                    if (tieneArcoTP(indiceT)) {
                        Punto pto1 = new Punto(x, t.getOrigen().valY() + 60);
                        ArcPuroTP arco = ObtenerArcoTP(indiceT);
                        arco.mover(t.getCentro(), pto1);
                        arco.valBeta();
                        arco.calcDrawLine();
                        arco.calcX();
                        arco.calcY();
                        PlacePuro place2 = (PlacePuro) vp.elementAt(arco.getPlace());
                        place2.getOrigen().nvoX(pto1.valX());
                        place2.getOrigen().nvoY(pto1.valY());
                        arco.setTransition(t.getIndice());

                    }
                    ArcPuroTP mod = new ArcPuroTP(ptran, ptrans, vaTP.size(), vp.size() - 1, ((TransitionPura) vt.elementAt(indiceT)).getIndice());
                    vaTP.addElement(mod);

                    //Parte de dibujar el arco de place a transicion
                    ArcPuroPT arc = new ArcPuroPT(ptrans, place, vaPT.size(), vp.size() - 1, vt.size() - 1);
                    vaPT.addElement(arc);
                }
            }

            ;

            private Vector<Integer> obtenerArcoPT(int index) {
                Vector<Integer> Res = new Vector();
                for (int i = 0; i < vaPT.size(); i++) {
                    int place = ((ArcPuroPT) vaPT.get(i)).getPlace();
                    if (place == index) {
                        Res.add(i);
                    }
                }
                return Res;
            }

            private Vector<Integer> obtenerArcoTP1(int indice) {
                Vector<Integer> v = new Vector();
                for (int j = 0; j < vaPT.size(); j++) {
                    ArcPuroPT arco = (ArcPuroPT) vaPT.elementAt(j);
                    if (arco.getPlace() == indice)
                        v.add(j);
                }
                return v;
            }

            private boolean tieneArcoTP(int indiceT) {
                for (int i = 0; i < vaTP.size(); i++) {
                    if (((ArcPuroTP) vaTP.get(i)).getTransition() == indiceT) {
                        return true;
                    }
                }
                return false;
            }

            private ArcPuroTP ObtenerArcoTP(int indiceT) {
                for (int i = 0; i < vaTP.size(); i++) {
                    if (((ArcPuroTP) vaTP.get(i)).getTransition() == indiceT) {
                        return (ArcPuroTP) vaTP.get(i);
                    }
                }
                return null;
            }

            private void AgregarCopias() {
                if (IsTransCopy(ptoP)) {
                    Vector<Integer> v = obtenerArcoPT(ptoP.getIndex());
                    for (Integer i : v) {
                        TransitionPura T = (TransitionPura) vt.get(((ArcPuroPT) vaPT.get(i)).getTransition());
                        Vector<PlacePuro> P = getPlaces(T);
                        for (PlacePuro p : P) {
                            p.setNumTokens(1);
                        }
                    }
                }
            }

            private boolean IsTransCopy(PlacePuro ptoP) {

                Vector<Integer> v = obtenerArcoPT(ptoP.getIndex());
                for (Integer i : v) {
                    TransitionPura T = (TransitionPura) vt.get(((ArcPuroPT) vaPT.get(i)).getTransition());
                    if (T.getTipo() == 2) {
                        return true;
                    }
                }
                return false;
            }

            private Vector<PlacePuro> getPlaces(TransitionPura T) {
                Vector<PlacePuro> P = new Vector();
                for (int i = 0; i < vaTP.size(); i++) {
                    int t = ((ArcPuroTP) vaTP.get(i)).getTransition();
                    if (t == T.getIndice()) {
                        P.add((PlacePuro) vp.get(((ArcPuroTP) vaTP.get(i)).getPlace()));
                    }
                }
                return P;

            }
        });

        //Register for mouse and key events on Canvas
        addMouseMotionListener(this);
    } // Constructor end

    public int ajustaTamI(int ni) {
        return ((int) (ni / porc));
    }

    void pres(int n) {
        elto = n;
    }

    //Funcion para aumentar la barra de desplazamiento
    void scroll(Rectangle r) {

        scrollRectToVisible(r);

        int this_width = (r.x + r.width + 2);
        if (this_width > size.width) {
            size.width = this_width;
            changed = true;
        }

        int this_height = (r.y + r.height + 2);
        if (this_height > size.height) {
            size.height = this_height;
            changed = true;
        }

        if (changed) {
            setPreferredSize(size);
            revalidate();
        }

    }

    public void mouseDragged(MouseEvent e) {
        changed = false;
        pto2 = new Punto();

        dentroPlace = checkPlace(pto);
        dentroTrans = checkTrans(pto);

        int xx = ajustaTamI(e.getX());
        int yy = ajustaTamI(e.getY());
        xx = ajustaX(xx);
        yy = ajustaY(yy);

        if (elto == 2 && (dentroPlace || dentroTrans)) {
            esArco = true;
            if (a == null) {
                pto2 = new Punto(xx, yy);
            } else {
                pto2.nvoX(xx);
                pto2.nvoY(yy);
            }
        }
        if (elto == -1) {
            es_rectangulo = true;
            if (sel_rec == null) {
                sel_rec.agregar(xx, yy);
            } else
                sel_rec.agregarTamanio(xx, yy);
        }
        repaint();
    }

    public void mouseMoved(MouseEvent e) {
        //    ECAPNSim.saySomething("Mouse moved", e);
    }

    public void Marcar_Regla(int cont) {
        //cambia color de la transicion a la que se le está asignando una regla
        TransitionPura T = (TransitionPura) vt.elementAt(cont);
        int i = T.getIndice();
        int arcoT = ObtenerArcTP(i);
        int arcoP = ObtenerArcPT(i);
        T.setColor(java.awt.Color.GREEN);
        ((ArcPuro) vaTP.elementAt(arcoT)).setColor(Color.GREEN);
        ((ArcPuro) vaPT.elementAt(arcoP)).setColor(Color.GREEN);
        ((PlacePuro) vp.elementAt(((ArcPuro) vaTP.elementAt(arcoT)).getPlace())).setColor(Color.GREEN);
        ((PlacePuro) vp.elementAt(((ArcPuro) vaPT.elementAt(arcoP)).getPlace())).setColor(Color.GREEN);
        repaint();
    }

    public void Desmarcar_Regla(int Tr) {
        TransitionPura T = (TransitionPura) vt.elementAt(Tr);
        int i = T.getIndice();
        int arcoT = ObtenerArcTP(i);
        int arcoP = ObtenerArcPT(i);
        T.setColor(java.awt.Color.red);
        ((ArcPuro) vaTP.elementAt(arcoT)).setColor(Color.BLACK);
        ((ArcPuro) vaPT.elementAt(arcoP)).setColor(Color.black);
        ((PlacePuro) vp.elementAt(((ArcPuro) vaTP.elementAt(arcoT)).getPlace())).setColor(Color.BLACK);
        ((PlacePuro) vp.elementAt(((ArcPuro) vaPT.elementAt(arcoP)).getPlace())).setColor(Color.BLACK);
        repaint();
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Punto poa;
/////////////////////////////////////////////////////////////////////////////////Forma en que dibuja la cuadricula
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        g.setColor(nuevoColor);
        if (d.width <= d.height) {
            for (int i = 0; i < d.width; i += tamaño) {
                for (int j = 0; j < d.height; j += tamaño) {
                    g.drawLine(0, j, d.height, j);//fila
                    g.drawLine(i, 0, i, d.height);//columna
                }
            }
        }
        //400 y 300
        if (d.width > d.height) {
            for (int i = 0; i < d.width; i += tamaño) {
                for (int j = 0; j < d.height; j += tamaño) {
                    g.drawLine(0, j, d.width, j);//fila
                    g.drawLine(i, 0, i, d.width);//columna
                }
            }
        }
//////////////////////////////////////////////////////////////////////////////////////////Fin de dibujo de la cuadricula
//**Dibujar places *************************************
        for (int i = 0; i < vp.size(); i++) {
            poa = ((PlacePuro) vp.elementAt(i)).getOrigen();
            Punto po = ajustaTam(poa); //new Punto((int) (poa.valX()*porc), (int) (poa.valY()*porc));
            int r = ajustaTam(((PlacePuro) vp.elementAt(i)).getRadio());
            g.setColor(((PlacePuro) vp.elementAt(i)).color);

            Graphics2D g2d = (Graphics2D) g;
            int interCirc = ajustaTam(2);
            g2d.setStroke(new BasicStroke(ajustaTam(1.5f)));

            float dash1[] = {5.0f};
            switch (((PlacePuro) vp.elementAt(i)).getTipo()) {
                case 0:
                case 1:
                case 2:
                case 3: // Eventos primitivos
                    g.drawOval(po.valX() - r, po.valY() - r, 2 * r, 2 * r);
                    break;
                case 6:
                case 7:
                case 8:
                case 9:
                case 10:
                case 11: // Eventos compuestos
                    g.drawOval(po.valX() - r, po.valY() - r, 2 * r, 2 * r);
                    g.drawOval(po.valX() - r + interCirc, po.valY() - r + interCirc, 2 * (r - interCirc), 2 * (r - interCirc));
                    break;
                case 4:
                case 5:
                case 12:
                case 13: // lugar virtual
                    g2d.setStroke(new BasicStroke(ajustaTam(1.5f),
                            BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 1.0f, dash1, 0.0f));
//                       g2d.setStroke(new BasicStroke(ajustaTam(1.0f)));
                    g.drawOval(po.valX() - r, po.valY() - r, 2 * r, 2 * r);
                    g2d.setStroke(new BasicStroke());
                    break;
                case 14: // lugar de tipo COPY
                    g.drawOval(po.valX() - r, po.valY() - r, 2 * r, 2 * r);
                    g2d.setStroke(new BasicStroke(ajustaTam(1.0f),
                            BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, dash1, 0.0f));
                    g.drawOval(po.valX() - r + interCirc, po.valY() - r + interCirc, 2 * (r - interCirc), 2 * (r - interCirc));
                    g2d.setStroke(new BasicStroke());
                    break;

                default:
                    g.drawOval(po.valX() - r, po.valY() - r, 2 * r, 2 * r);
            }
            String nomb = /*"HOLA";*/ ((PlacePuro) vp.elementAt(i)).getNombre();
            int ic = 11;
            int incY = 0;
            int tc = nomb.length();             //
            String nomb2;
            while (tc >= ic) {     //
                nomb2 = nomb.substring(ic - 11, ic);
                g.drawString(nomb2, po.valX() + (int) (20 * porc), po.valY() + incY);
                ic += 11;
                incY += ((int) (15 * porc));
            }
            if (tc > (ic - 11)) {
                nomb2 = nomb.substring(ic - 11, tc);
                g.drawString(nomb2, po.valX() + (int) (20 * porc), po.valY() + incY);
            }

            int tok = ((PlacePuro) vp.elementAt(i)).getNumTokens();
            g.setColor(Color.black);
            switch (tok) {
                case 0: // Sin tokens
                    break;
                case 1: // Un solo token
                    g.fillOval(po.valX() - rt, po.valY() - rt, rt * 2, rt * 2);
                    break;
                case 2: // Dos tokens
                    g.fillOval(po.valX() - ((r - 5) / 2) - rt, po.valY() - rt, rt * 2, rt * 2);
                    g.fillOval(po.valX() + ((r - 5) / 2) - rt, po.valY() - rt, rt * 2, rt * 2);
                    break;
                case 3: // Tres tokens
                    g.fillOval(po.valX() - ((r - 5) / 2) - rt, po.valY() + ((r - 5) / 2) - rt, rt * 2, rt * 2);
                    g.fillOval(po.valX() + ((r - 5) / 2) - rt, po.valY() + ((r - 5) / 2) - rt, rt * 2, rt * 2);
                    g.fillOval(po.valX() - rt, po.valY() - ((r - 5) / 2) - rt, rt * 2, rt * 2);
                    break;
                case 4: // Cuatro tokens
                    g.fillOval(po.valX() - ((r - 5) / 2) - rt, po.valY() + ((r - 5) / 2) - rt, rt * 2, rt * 2);
                    g.fillOval(po.valX() + ((r - 5) / 2) - rt, po.valY() + ((r - 5) / 2) - rt, rt * 2, rt * 2);
                    g.fillOval(po.valX() - ((r - 5) / 2) - rt, po.valY() - ((r - 5) / 2) - rt, rt * 2, rt * 2);
                    g.fillOval(po.valX() + ((r - 5) / 2) - rt, po.valY() - ((r - 5) / 2) - rt, rt * 2, rt * 2);
                    break;
                default: // Un numero mayor de 4
                    Integer ni = new Integer(tok);
                    String si = ni.toString();
                    byte[] a5 = si.getBytes();
                    int tx, ty;
                    ty = po.valY() + ajustaTam(5);
                    switch (a5.length) {
                        case 1: // Un solo digito
                            tx = po.valX() - ajustaTam(3);
                            break;
                        case 2: // Dos digitos
                            tx = po.valX() - ajustaTam(6);
                            break;
                        case 3: // Tres digitos
                            tx = po.valX() - ajustaTam(9);
                            break;
                        default: // Cuatro digitos o mas
                            tx = po.valX() - ajustaTam(12);
                    }
                    g.drawBytes(a5, 0, a5.length, tx, ty);
            }

        }
//***************Dibujar Transition
        for (int i = 0; i < vt.size(); i++) {
            poa = ((TransitionPura) vt.elementAt(i)).getOrigen();
            Punto po = ajustaTam(poa); //new Punto((int) (poa.valX()*porc), (int) (poa.valY()*porc));
            int wi = ajustaTam(((TransitionPura) vt.elementAt(i)).getW());
            int hi = ajustaTam(((TransitionPura) vt.elementAt(i)).getH());

            g.setColor(Color.black);
            g.drawString(((TransitionPura) vt.elementAt(i)).getNombre(), po.valX() + ajustaTam(30), po.valY());

            g.setColor(((TransitionPura) vt.elementAt(i)).color);
            //Es una transicion simple
            if (((TransitionPura) vt.elementAt(i)).getTipo() == 0)
                g.drawRect(po.valX(), po.valY(), wi, hi);
            //Es una transicion compuesta
            if (((TransitionPura) vt.elementAt(i)).getTipo() == 1) {
                g.drawLine(po.valX(), po.valY(), po.valX() + wi, po.valY());
                g.drawLine(po.valX(), po.valY() + hi, po.valX() + wi, po.valY() + hi);
            }
            if (((TransitionPura) vt.elementAt(i)).getTipo() == 2)
                g.fillRect(po.valX(), po.valY(), wi, 1);

        }
//**************Dibujar Flechas

        for (int i = 0; i < vaPT.size(); i++) {
            g.setColor(((ArcPuro) vaPT.elementAt(i)).color);
            ArcPuro ap = (ArcPuro) vaPT.elementAt(i);
            Linea ldd = ap.getLineDraw();
            Punto po = ajustaTam(ldd.getPoint1());//Punto origen
            Punto pd = ajustaTam(ldd.getPoint2());//Punto Destino
            g.drawLine(po.valX(), po.valY(), pd.valX(), pd.valY());
            g.fillPolygon(ajustaTam(ap.getPointsX()), ajustaTam(ap.getPointsY()), 3);
        }
        for (int i = 0; i < vaTP.size(); i++) {
            ArcPuro ap = (ArcPuro) vaTP.elementAt(i);
            g.setColor(((ArcPuro) vaTP.elementAt(i)).color);
            Linea ldd = ap.getLineDraw();
            Punto po = ajustaTam(ldd.getPoint1());
            Punto pd = ajustaTam(ldd.getPoint2());
            g.drawLine(po.valX(), po.valY(), pd.valX(), pd.valY());
            g.fillPolygon(ajustaTam(ap.getPointsX()), ajustaTam(ap.getPointsY()), 3);
        }

        g.setColor(Color.black);
        if (elto == 2 && esArco) {
            Graphics2D g2 = (Graphics2D) g;
            float dash[] = {5.0f};
            g2.setStroke(new BasicStroke(ajustaTam(1.0f), BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER, 10.0f, dash, 0.0f));
            g.drawLine(ajustaTam(pto.valX()), ajustaTam(pto.valY()), pto2.valX(), pto2.valY());
            g2.setStroke(new BasicStroke());
        }
  /*
   * Parte para dibujar cuando esta seleccionado
   */

        if (elto == -1 && es_rectangulo) {
            g.setColor(Color.BLUE);
            Graphics2D g2 = (Graphics2D) g;
            float dash[] = {5.0f};
            g2.setStroke(new BasicStroke(ajustaTam(1.0f), BasicStroke.CAP_SQUARE, BasicStroke.JOIN_BEVEL, 10.0f, dash, 0.0f));
            g.drawRect(sel_rec.getX(), sel_rec.getY(), sel_rec.getWidth(), sel_rec.getHeight());
            g2.setStroke(new BasicStroke());
        }
        if (elto == -1) {
            // Dibuja Objeto Seleccionado
            g.setColor(Color.gray);
            Graphics2D g2 = (Graphics2D) g;
            for (int i = 0; i < vp.size(); i++) {
                if (((PlacePuro) vp.elementAt(i)).getSelected()) {
                    Punto p0 = ajustaTam(((PlacePuro) vp.elementAt(i)).getOrigen());
                    int r = ajustaTam(((PlacePuro) vp.elementAt(i)).getRadio());
                    g.setColor(Color.white);
                    g.fillRect(p0.valX() + r - 2, p0.valY() - 2, 4, 4);
                    g.fillRect(p0.valX() - 2, p0.valY() - r - 2, 4, 4);
                    g.fillRect(p0.valX() - r - 2, p0.valY() - 2, 4, 4);
                    g.fillRect(p0.valX() - 2, p0.valY() + r - 2, 4, 4);
                    g.setColor(Color.black);
                    g.drawRect(p0.valX() + r - 2, p0.valY() - 2, 4, 4);
                    g.drawRect(p0.valX() - 2, p0.valY() - r - 2, 4, 4);
                    g.drawRect(p0.valX() - r - 2, p0.valY() - 2, 4, 4);
                    g.drawRect(p0.valX() - 2, p0.valY() + r - 2, 4, 4);
                }
            }
            for (int i = 0; i < vt.size(); i++) {
                if (((TransitionPura) vt.elementAt(i)).getSeleccion()) {
                    Punto p1 = ajustaTam(((TransitionPura) vt.elementAt(i)).getOrigen());
                    int wi = ajustaTam(((TransitionPura) vt.elementAt(i)).getW());
                    int hi = ajustaTam(((TransitionPura) vt.elementAt(i)).getH());
                    g.setColor(Color.white);
                    g.fillRect(p1.valX() - 2, p1.valY() - 2, 4, 4);
                    g.fillRect(p1.valX() - 2, p1.valY() + hi - 2, 4, 4);
                    g.fillRect(p1.valX() + (int) (wi / 2) - 2, p1.valY() + hi - 2, 4, 4);
                    g.fillRect(p1.valX() + wi - 2, p1.valY() + hi - 2, 4, 4);
                    g.fillRect(p1.valX() + wi - 2, p1.valY() - 2, 4, 4);
                    g.fillRect(p1.valX() + (int) (wi / 2) - 2, p1.valY() - 2, 4, 4);
                    g.setColor(Color.black);
                    g.drawRect(p1.valX() - 2, p1.valY() - 2, 4, 4);
                    g.drawRect(p1.valX() - 2, p1.valY() + hi - 2, 4, 4);
                    g.drawRect(p1.valX() + (int) (wi / 2) - 2, p1.valY() + hi - 2, 4, 4);
                    g.drawRect(p1.valX() + wi - 2, p1.valY() + hi - 2, 4, 4);
                    g.drawRect(p1.valX() + wi - 2, p1.valY() - 2, 4, 4);
                    g.drawRect(p1.valX() + (int) (wi / 2) - 2, p1.valY() - 2, 4, 4);
                }
            }
            for (int i = 0; i < vaPT.size(); i++) {
                if (((ArcPuro) vaPT.elementAt(i)).getSelected()) {
                    ArcPuro ap2 = (ArcPuro) vaPT.elementAt(i);
                    Punto po2 = ajustaTam(ap2.getLineDraw().getPoint1());
                    Punto pd2 = ajustaTam(ap2.getLineDraw().getPoint2());
                    Linea lin = new Linea(po2, pd2);
                    Punto pm = lin.puntoMedio();
                    g.setColor(Color.white);
                    g.fillRect(po2.valX() - 2, po2.valY() - 2, 4, 4);
                    g.fillRect(pm.valX() - 2, pm.valY() - 2, 4, 4);
                    g.fillRect(pd2.valX() - 2, pd2.valY() - 2, 4, 4);
                    g.setColor(Color.black);
                    g.drawRect(po2.valX() - 2, po2.valY() - 2, 4, 4);
                    g.drawRect(pm.valX() - 2, pm.valY() - 2, 4, 4);
                    g.drawRect(pd2.valX() - 2, pd2.valY() - 2, 4, 4);
                }


            }
            for (int i = 0; i < vaTP.size(); i++) {
                if (((ArcPuro) vaTP.elementAt(i)).getSelected()) {
                    ArcPuro ap3 = (ArcPuro) vaTP.elementAt(i);
                    Punto po3 = ajustaTam(ap3.getLineDraw().getPoint1());
                    Punto pd3 = ajustaTam(ap3.getLineDraw().getPoint2());
                    Linea lin2 = new Linea(po3, pd3);
                    Punto pm = lin2.puntoMedio();
                    g.setColor(Color.white);
                    g.fillRect(po3.valX() - 2, po3.valY() - 2, 4, 4);
                    g.fillRect(pm.valX() - 2, pm.valY() - 2, 4, 4);
                    g.fillRect(pd3.valX() - 2, pd3.valY() - 2, 4, 4);
                    g.setColor(Color.black);
                    g.drawRect(po3.valX() - 2, po3.valY() - 2, 4, 4);
                    g.drawRect(pm.valX() - 2, pm.valY() - 2, 4, 4);
                    g.drawRect(pd3.valX() - 2, pd3.valY() - 2, 4, 4);
                }
            }
        }
        //Dibujar si esta corriendo timer PT
        if (timerPT.isRunning()) {
            for (int i = 0; i < vControl.size(); i++)
                g.fillOval(((vControl.elementAt(i)).getPto().valX()) - rt, ((vControl.elementAt(i)).getPto().valY()) - rt, rt * 2, rt * 2);
            for (int i = 0; i < vControlT.size(); i++)
                g.fillOval(((vControlT.elementAt(i)).getPto().valX()) - rt, ((vControlT.elementAt(i)).getPto().valY()) - rt, rt * 2, rt * 2);
        }
    }

    public Punto ajustaTam(Punto ptam) {
        int vx = (int) (ptam.valX() * porc);
        int vy = (int) (ptam.valY() * porc);
        return (new Punto(vx, vy));
    }

    private int ajustaTam(int radio) {
        return ((int) (radio * porc));
    }

    boolean checkPlace(Punto p) {
        boolean r = false;
        for (int i = 0; i < vp.size(); i++) {
            if (((PlacePuro) vp.elementAt(i)).getOrigen().distancia(p) <= ((PlacePuro) vp.elementAt(i)).getRadio()) {
                r = true;
                ptoP = (PlacePuro) vp.elementAt(i);
                break;
            }
        }
        return r;
    }

    boolean checkTrans(Punto p) {
        boolean r = false;
        for (int i = 0; i < vt.size(); i++) {
            if (((TransitionPura) vt.elementAt(i)).pertenece(p)) {
                r = true;
                ptoT = (TransitionPura) vt.elementAt(i);
                break;
            }
        }
        return r;
    }

    boolean checarTrans(Punto p, int trans) {
        boolean r = true;
        z = 0;
        for (int i = 0; i < vaTP.size(); i++) {
            if (((ArcPuro) vaTP.elementAt(i)).getTransition() == trans) {
                z++;
                System.out.println("Hay una conexion");
            }
        }
        System.out.println("Se agregaron: " + z);
        for (int i = 0; i < vt.size(); i++) {
            if (((TransitionPura) vt.elementAt(i)).pertenece(p) && ((TransitionPura) vt.elementAt(i)).getIndice() == trans) {
                ((TransitionPura) vt.elementAt(i)).settoken(1 * z);
                r = false;
                break;
            }
        }
        return r;
    }

    boolean checarPlace(Punto p, int place) {
        boolean r = true;
        for (int i = 0; i < vp.size(); i++) {
            if (((PlacePuro) vp.elementAt(i)).getOrigen().distancia(p) <= ((PlacePuro) vp.elementAt(i)).getRadio() && ((PlacePuro) vp.elementAt(i)).getIndex() == place) {
                ((PlacePuro) vp.elementAt(i)).setNumTokens(1);
                r = false;
                break;
            }
        }
        return r;
    }

    public int[] ajustaTam(int[] points) {
        int[] nPoints = new int[points.length];
        for (int ip = 0; ip < points.length; ip++)
            nPoints[ip] = ajustaTam(points[ip]);
        return nPoints;
    }

    public float ajustaTam(float ni) {
        return ((float) (ni * porc));
    }

    public boolean play() {
        if (!timerPT.isRunning() && evaluar()) {
            timerPT.start();
            return true;
        } else {
            return false;
        }

    }

    //--metodo que verifica la correctitud de la red
    public boolean verificar_red() {
        for (int i = 0; i < vaPT.size(); i++) {
            ArcPuroPT arco = (ArcPuroPT) vaPT.get(i);
            if (!buscaT(arco.getTransition()))
                return false;
        }
        return true;
    }

    public boolean buscaT(int index) {
        for (int i = 0; i < vaTP.size(); i++) {
            ArcPuroTP arco = (ArcPuroTP) vaTP.get(i);
            if (arco.getTransition() == index)
                return true;
        }
        return false;
    }

    public boolean evaluar() {
        verificar_tokens();
        verificar_comp();
        if (vp.size() != 0 && se_puede) {
            activarTrans();
            activarPlace();
            //Activar_PT();
            return true;
        } else {
            JOptionPane.showMessageDialog(null, "Existen un Error ", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    public void activarTrans() {
        for (int i = 0; i < vp.size(); i++) {

            for (int j = 0; j < vaPT.size(); j++) {
                if (i == ((ArcPuro) vaPT.elementAt(j)).getPlace()) {
                    //Aplicacion de tokens con respecto al peso
                    if (((PlacePuro) vp.elementAt((((ArcPuro) vaPT.elementAt(j)).getPlace()))).getNumTokens() >= ((ArcPuro) vaPT.elementAt(j)).getPeso()) {
                        //Verifica si todavia esta corriendo de Place a transicion
                        if (verificar_CorriendoPT(((ArcPuro) vaPT.elementAt(j)).getPlace(), ((ArcPuro) vaPT.elementAt(j)).getTransition())) {
                            ((PlacePuro) vp.elementAt((((ArcPuro) vaPT.elementAt(j)).getPlace()))).restarToken();
                            ArcPuro arp = (ArcPuro) vaPT.elementAt(j);
                            double dX = arp.getLineDraw().getPoint2().valX() - arp.getLineDraw().getPoint1().valX(),
                                    dY = arp.getLineDraw().getPoint2().valY() - arp.getLineDraw().getPoint1().valY();
                            vControl.add(new ControlAnimacion(new Punto(arp.getLineDraw().getPoint1()), dX, dY, ((ArcPuroPT) vaPT.elementAt(j)).getPlace(), ((ArcPuroPT) vaPT.elementAt(j)).getTransition()));//Agrega al vector Control
                            System.out.println("J: " + ((ArcPuroPT) vaPT.elementAt(j)).getTransition());
                            System.out.println("I: " + ((ArcPuroPT) vaPT.elementAt(i)).getTransition());
                        }


                    }
                }
            }
        }
    }

    public void stop() {
        timerPT.stop();
    }

    public void activarPlace() {
        for (int k = 0; k < vt.size(); k++) {
            for (int i = 0; i < vaTP.size(); i++) {
                if (((ArcPuro) vaTP.elementAt(i)).getTransition() == k) {
                    if (((TransitionPura) vt.elementAt(k)).getTokens() > 0) {

                        if (verificar_CorriendoTP(((ArcPuro) vaTP.elementAt(i)).getPlace(), ((ArcPuro) vaTP.elementAt(i)).getTransition())) {
                            ((TransitionPura) vt.elementAt((((ArcPuro) vaTP.elementAt(k)).getTransition()))).restarToken();
                            ArcPuro arp = (ArcPuro) vaTP.elementAt(i);
                            double dX = arp.getLineDraw().getPoint2().valX() - arp.getLineDraw().getPoint1().valX(),
                                    dY = arp.getLineDraw().getPoint2().valY() - arp.getLineDraw().getPoint1().valY();
                            vControlT.add(new ControlAnimacion(new Punto(arp.getLineDraw().getPoint1()), dX, dY, ((ArcPuroTP) vaTP.elementAt(i)).getPlace(), ((ArcPuroTP) vaTP.elementAt(i)).getTransition()));//Agrega al vector Control
                        }

                    }
                }
            }
        }
    }

    //Regresa la proporcion que tiene para el zom
    double getPorc() {
        return porc;
    }

    //Codigo para hacer mas Zoom
    void modZoomin(int perc) {
        elto = -2;
        porc = perc / 100.0;
        rt = ajustaTam(3);
        inc = inc + 5;
        int nxt2 = ajustaTam(nxt);
        int nyt2 = ajustaTam(nyt);
        size.width = 0;
        size.height = 0;
        changed = true;
        tamaño = tamaño + 3;
        scroll(new Rectangle(0, 0, nxt2 + ajustaTam(100), nyt2 + ajustaTam(100)));
        repaint();
    }

    //Metodo para hacer menos zoom
    void modZoomout(int perc) {
        elto = -2;
        porc = perc / 100.0;
        rt = ajustaTam(3);
        inc = inc - 5;
        int nxt2 = ajustaTam(nxt);
        int nyt2 = ajustaTam(nyt);
        size.width = 0;
        size.height = 0;
        changed = true;
        tamaño = tamaño - 3;
        scroll(new Rectangle(0, 0, nxt2 + ajustaTam(100), nyt2 + ajustaTam(100)));
        repaint();
    }

    void guardar(String namepnj) throws FileNotFoundException, IOException {
        FileOutputStream fos = new FileOutputStream(namepnj);
        ObjectOutputStream oos = new ObjectOutputStream(fos);


        oos.writeObject(new Double(porc));
        oos.writeObject(new Integer(nxt));
        oos.writeObject(new Integer(nyt));
        oos.writeObject(size);
        oos.writeObject(vp);
        oos.writeObject(vt);
        oos.writeObject(vaPT);
        oos.writeObject(vaTP);

        oos.flush();
        oos.close();

        System.out.println("Archivo guardado");
    }

    void limpiar(boolean b) {
        elto = -2;
        vp = new Vector();
        vt = new Vector();
        vaPT = new Vector();
        vaTP = new Vector();
        vControl = new Vector();
        size.width = 0;
        size.height = 0;
        changed = true;
        scroll(new Rectangle(0, 0, 0, 0));
        repaint();
        porc = 1.0;
        rt = 3;
        TransitionPura.cont = 0;
        PlacePuro.cont = 0;
    }

    void abrir(String nomArch) throws IOException {
        limpiar(true);
        FileInputStream fis = new FileInputStream(nomArch);
        ObjectInputStream ois = new ObjectInputStream(fis);

        vp = new Vector();
        vt = new Vector();
        vaPT = new Vector();
        vaTP = new Vector(); //vTabla = new Vector();
        Dimension s = new Dimension();
        try {
            porc = ((Double) ois.readObject()).doubleValue();
            nxt = ((Integer) ois.readObject()).intValue();
            nyt = ((Integer) ois.readObject()).intValue();
            s = (Dimension) ois.readObject();
            scroll(new Rectangle(0, 0, s.width, s.height));

            vp = (Vector) ois.readObject();
            vt = (Vector) ois.readObject();
            vaPT = (Vector) ois.readObject();
            vaTP = (Vector) ois.readObject();

        } catch (Exception exc) {
            System.out.println("Se cach� la excepci�n....vaTP");
        }
        ois.close();
        repaint();

    }

    public void verificar_PT(int indice_p) {
        for (int i = 0; i < vaPT.size(); i++) {
            if (((ArcPuro) vaPT.elementAt(i)).getPlace() == indice_p) {
                System.out.println("Si tiene este place un arco");
                ((ArcPuro) vaPT.elementAt(i)).setSelected(true);
                System.out.println("Esta seleccionado el arco: " + ((ArcPuro) vaPT.elementAt(i)).getIndice());
            }
        }
        repaint();
    }

    public void verificar_TP(int indice_p) {
        for (int i = 0; i < vaTP.size(); i++) {
            if (((ArcPuro) vaTP.elementAt(i)).getPlace() == indice_p) {
                System.out.println("Si tiene este trans un arco");
                ((ArcPuro) vaTP.elementAt(i)).setSelected(true);
                System.out.println("Esta seleccionado el arco: " + ((ArcPuro) vaTP.elementAt(i)).getIndice());
            }
        }
        repaint();
    }

    public void verificar_TP_t(int indice_t) {
        for (int i = 0; i < vaTP.size(); i++) {
            if (((ArcPuro) vaTP.elementAt(i)).getTransition() == indice_t) {
                System.out.println("Si tiene este trans un arco");
                ((ArcPuro) vaTP.elementAt(i)).setSelected(true);
            }
        }
        repaint();
    }

    public void verificar_PT_t(int indice_t) {
        for (int i = 0; i < vaPT.size(); i++) {
            if (((ArcPuro) vaPT.elementAt(i)).getTransition() == indice_t) {
                System.out.println("Si tiene este trans un arco");
                ((ArcPuro) vaPT.elementAt(i)).setSelected(true);
            }
        }
        repaint();
    }

    void _selec() {
        for (int i = 0; i < vp.size(); i++)
            if (((PlacePuro) vp.elementAt(i)).getSelected()) {
                ((PlacePuro) vp.elementAt(i)).setSelected(false);
            }


        for (int i = 0; i < vt.size(); i++)
            if (((TransitionPura) vt.elementAt(i)).getSeleccion()) {
                ((TransitionPura) vt.elementAt(i)).setSeleccion(false);
            }

    }

    boolean verifica_selec() {
        for (int i = 0; i < vp.size(); i++) {
            if (((PlacePuro) vp.elementAt(i)).getSelected()) {
                return true;
            }
        }
        for (int i = 0; i < vt.size(); i++) {
            if (((TransitionPura) vt.elementAt(i)).getSeleccion()) {
                return true;
            }
        }
        return false;
    }

    void borrar_selec() {
        //Si esta seleccionada place busca los arcos que estan con el
        System.out.println(vp.size());
        for (int i = 0; i < vp.size(); i++) {
            if (((PlacePuro) vp.elementAt(i)).getSelected()) {
                verificar_PT(((PlacePuro) vp.elementAt(i)).getIndex());
                verificar_TP(((PlacePuro) vp.elementAt(i)).getIndex());
            }
        }
        //Si esta seleccionada trans busca los arcos que estan con el
        for (int i = 0; i < vt.size(); i++) {
            if (((TransitionPura) vt.elementAt(i)).getSeleccion()) {
                verificar_PT_t(((TransitionPura) vt.elementAt(i)).getIndice());
                verificar_TP_t(((TransitionPura) vt.elementAt(i)).getIndice());
            }
        }

        //Eliminar transicion
        for (int i = 0; i < vt.size(); i++)
            if (((TransitionPura) vt.elementAt(i)).getSeleccion()) {
                vt.removeElementAt(i);
                recorrer_t(i);
                repaint();
            }

        //Eliminar Place
        for (int i = 0; i < vp.size(); i++)
            if (((PlacePuro) vp.elementAt(i)).getSelected()) {
                vp.removeElementAt(i);
                recorrer_p(i);
                recorrer_aTP_p(i);
                recorrer_aPT_p(i);
                repaint();
            }

        //Eliminamos Placa a Transicion
        eliminar_PT();

        //Eliminamos Transicion a Place
        eliminar_TP();


    }

    //Metodo llamado de MINIECap para borrar lo que esta seleccionado
    void borrar() {
        //If Para ver si algo esta seleccionado
        if (verifica_selec()) {
            System.out.println("Si hay algo que borrar");
            borrar_selec();
        } else {
            JOptionPane.showMessageDialog(null, "No hay nada que borrar", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void recorrer_p(int i) {
        for (int r = i; r < vp.size(); r++) {
            ((PlacePuro) vp.elementAt(r)).setIndex(r);
        }
    }

    public void recorrer_t(int i) {
        for (int r = i; r < vt.size(); r++) {
            recorrer_aTP_t(r);
            recorrer_aPT_t(r);
            ((TransitionPura) vt.elementAt(r)).setIndice(r);

        }
    }

    public void recorrer_aTP(int i) {
        for (int r = i; r < vaTP.size(); r++) {
            ((ArcPuro) vaTP.elementAt(r)).setIndice(r);
        }
    }

    public void recorrer_aPT(int i) {
        for (int r = i; r < vaPT.size(); r++) {
            ((ArcPuro) vaPT.elementAt(r)).setIndice(r);
        }
    }

    public void recorrer_aTP_p(int i) {
        for (int r = 0; r < vaTP.size(); r++) {
            if (i < ((ArcPuro) vaTP.elementAt(r)).getPlace()) {
                int q = ((ArcPuro) vaTP.elementAt(r)).getPlace();
                ((ArcPuro) vaTP.elementAt(r)).setPlace(q - 1);
            }
        }
    }

    public void recorrer_aPT_p(int i) {
        for (int r = 0; r < vaPT.size(); r++) {
            if (i < ((ArcPuro) vaPT.elementAt(r)).getPlace()) {
                int q = ((ArcPuro) vaPT.elementAt(r)).getPlace();
                ((ArcPuro) vaPT.elementAt(r)).setPlace(q - 1);
            }
        }
    }

    public void recorrer_aTP_t(int i) {
        for (int r = 0; r < vaTP.size(); r++) {
            if (i < ((ArcPuro) vaTP.elementAt(r)).getTransition()) {
                int q = ((ArcPuro) vaTP.elementAt(r)).getTransition();
                ((ArcPuro) vaTP.elementAt(r)).setTransition(q - 1);
            }
        }
    }

    public void recorrer_aPT_t(int i) {
        for (int r = 0; r < vaPT.size(); r++) {
            if (i < ((ArcPuro) vaPT.elementAt(r)).getTransition()) {
                int q = ((ArcPuro) vaPT.elementAt(r)).getTransition();
                ((ArcPuro) vaPT.elementAt(r)).setTransition(q - 1);
            }
        }
    }

    void eliminar_TP() {
        for (int i = 0; i < vaTP.size(); i++)
            if (((ArcPuro) vaTP.elementAt(i)).getSelected()) {
                vaTP.removeElementAt(i);
                recorrer_aTP(i);
                eliminar_TP();
            }
    }

    private void eliminar_PT() {
        for (int i = 0; i < vaPT.size(); i++)
            if (((ArcPuro) vaPT.elementAt(i)).getSelected()) {
                vaPT.removeElementAt(i);
                recorrer_aPT(i);
                eliminar_PT();
            }
    }

    private void verificar_comp() {
        for (int i = 0; i < vp.size(); i++)
            if (((PlacePuro) vp.elementAt(i)).getNumTokens() != 0) {
                if (HasComp((PlacePuro) vp.get(i))) {

                    Vector<Integer> v = obtenerArcoPT(ptoP.getIndex());
                    for (Integer ind : v) {
                        TransitionPura T = (TransitionPura) vt.get(((ArcPuroPT) vaPT.get(ind)).getTransition());
                        Vector<Integer> v2 = obtenerArcoPT1(T.getIndice());
                        for (Integer k : v2) {

                            if (((PlacePuro) vp.get(((ArcPuroPT) vaPT.get(k)).getPlace())).getNumTokens() > 0) {
                                se_puede = true;
                            } else
                                se_puede = false;
                        }
                    }
                }
            }
    }

    private Vector<Integer> obtenerArcoPT(int index) {
        Vector<Integer> Res = new Vector();
        for (int i = 0; i < vaPT.size(); i++) {
            int place = ((ArcPuroPT) vaPT.get(i)).getPlace();
            if (place == index) {
                Res.add(i);
            }
        }
        return Res;
    }

    private Vector<Integer> obtenerArcoPT1(int index) {
        Vector<Integer> Res = new Vector();
        for (int i = 0; i < vaPT.size(); i++) {
            int place = ((ArcPuroPT) vaPT.get(i)).getTransition();
            if (place == index) {
                Res.add(i);
            }
        }
        return Res;
    }

    private boolean HasComp(PlacePuro placePuro) {
        Vector<Integer> v = obtenerArcoPT(placePuro.getIndex());
        for (Integer i : v) {
            TransitionPura T = (TransitionPura) vt.get(((ArcPuroPT) vaPT.get(i)).getTransition());
            if (T.getTipo() == 1) {
                return true;
            }
        }
        return false;
    }


    void verificar_tokens() {
        for (int i = 0; i < vp.size(); i++) {
            if (((PlacePuro) vp.elementAt(i)).getNumTokens() != 0) {
                if (verificar_existeTP(((PlacePuro) vp.elementAt(i)).getIndex()) && verificar_existePT(((PlacePuro) vp.elementAt(i)).getIndex())) {
                    se_puede = true;
                }
            }
        }
    }

    private boolean verificar_existePT(int indice) {
        for (int i = 0; i < vaPT.size(); i++) {
            if (((ArcPuro) vaPT.elementAt(i)).getPlace() == indice) {
                return true;
            }
        }
        return false;
    }

    private boolean verificar_existeTP(int indice) {
        for (int i = 0; i < vaTP.size(); i++) {
            if (((ArcPuro) vaTP.elementAt(i)).getPlace() == indice) {
                return false;
            }
        }
        return true;
    }

    private void Activar_PT() {
        //Verificar los arcos de place a transucion si la place a la que estan conectados
        for (int i = 0; i < vaPT.size(); i++) {
            System.out.println("Place: " + ((ArcPuro) vaPT.elementAt(i)).getPlace());
            System.out.println("Numero de Tokens: " + ((PlacePuro) vp.elementAt((((ArcPuro) vaPT.elementAt(i)).getPlace()))).getNumTokens());
            System.out.println("Numero del peso del Arco: " + ((ArcPuro) vaPT.elementAt(i)).getPeso());
            if (((PlacePuro) vp.elementAt((((ArcPuro) vaPT.elementAt(i)).getPlace()))).getNumTokens() >= ((ArcPuro) vaPT.elementAt(i)).getPeso()) {
                ((PlacePuro) vp.elementAt((((ArcPuro) vaPT.elementAt(i)).getPlace()))).restarToken();
                ArcPuro arp = (ArcPuro) vaPT.elementAt(i);
                double dX = arp.getLineDraw().getPoint2().valX() - arp.getLineDraw().getPoint1().valX(),
                        dY = arp.getLineDraw().getPoint2().valY() - arp.getLineDraw().getPoint1().valY();
                vControl.add(new ControlAnimacion(new Punto(arp.getLineDraw().getPoint1()), dX, dY, ((ArcPuroPT) vaPT.elementAt(i)).getPlace(), ((ArcPuroPT) vaPT.elementAt(i)).getTransition()));//Agrega al vector Control
                System.out.println("Se creo un nuevo Control Animacion");
            }
        }
    }

    void dibujar_copias(int indiceT) {
        for (int i = 0; i < vaTP.size(); i++) {
            System.out.println("El tamaño del vectores: " + vaTP.size());
            if (indiceT == ((ArcPuro) vaTP.elementAt(i)).getTransition()) {
                if (((PlacePuro) vp.elementAt(((ArcPuro) vaTP.elementAt(i)).getPlace())).getTipo() == 0) {
                    ((PlacePuro) vp.elementAt(((ArcPuro) vaTP.elementAt(i)).getPlace())).setTipo(14);
                    System.out.println("Entro de la transicion: " + indiceT);
                    System.out.println("Y con place: " + ((ArcPuro) vaTP.elementAt(i)).getPlace());

                    //Agregar Transicion
                    Punto ptran = ((PlacePuro) vp.elementAt(((ArcPuro) vaTP.elementAt(i)).getPlace())).getOrigen();
                    int indice = ((PlacePuro) vp.elementAt(((ArcPuro) vaTP.elementAt(i)).getPlace())).getIndex();
                    int x = ptran.valX();
                    int y = ptran.valY();
                    Punto place = new Punto(x, y + 60);
                    TransitionPura t = new TransitionPura(place, vt.size());
                    vt.addElement(t);

                    //Agregar arco de place a transcion
                    ArcPuroPT arc = new ArcPuroPT(ptran, place, vaPT.size(), indice, vt.size() - 1);
                    vaPT.addElement(arc);

                    //Agregar place
                    Punto ptrans = new Punto(x, y + 120);
                    p = new PlacePuro(ptrans, vp.size());
                    vp.addElement(p);

                    //Agregar arco de transicion a place
                    ArcPuroTP mod = new ArcPuroTP(place, ptrans, vaTP.size(), vp.size() - 1, vt.size() - 1);
                    vaTP.addElement(mod);
                }
            }
        }
    }

    void IncidenceMatrix() {
        JFrame jft = new JFrame("Incidence Matrix");
        jft.setSize(new Dimension(30 * (vp.size() + 6), 30 * (vt.size() + 6)));
        matrix = new IncidenceMatrix(vt.size(), vp.size(), vaPT, vaTP);
        jft.getContentPane().add(matrix);
        jft.setVisible(true);
    }

    boolean verificar_PN() {
        boolean chk = false;
        IncidenceMatrix();
        int col = matrix.getCol();
        int row = matrix.getRow();
        int[][] m = matrix.getMatrix();

        for (int i = 0; i < row; i++) {
            for (int j = 0; j < col; j++) {
                if (m[i][j] < 0)
                    chk = checkrow(i, j, m);
            }
        }
        return chk;
    }

    boolean checkcol(int pos, int col, int[][] m) {
        boolean ok = true;
        for (int i = 0; i < matrix.getRow(); i++) {
            if (i != pos) {
                if (m[i][col] != 0) {
                    if (m[i][col] < 0)
                        return checkrow(col, i, m);
                    else
                        ok = false;
                }
            }
        }
        return ok;
    }

    boolean checkrow(int pos, int row, int[][] m) {
        boolean ok = true;
        for (int i = 0; i < matrix.getCol(); i++) {
            if (i != pos) {
                if (m[row][i] != 0) {
                    if (m[row][i] > 0)
                        return checkcol(row, i, m);
                    else
                        ok = false;
                }
            }
        }
        return ok;
    }

    public Vector primitivos() {
        int[][] m = matrix.getMatrix();
        Vector<Integer> prim = new Vector();
        for (int i = 0; i < matrix.getCol(); i++) {
            if (IsPrimitive(i, m))
                prim.add(i);
        }
        JOptionPane.showMessageDialog(null, prim.toString(), "lugares de Entrada", JOptionPane.INFORMATION_MESSAGE);
        return prim;
    }

    void Compuesto() {
        Vector ciclos = new Vector();

        for (int i = 0; i < vaPT.size(); i++) {
            ArrayList cplaces = new ArrayList();
            ArcPuro a = (ArcPuro) vaPT.elementAt(i);
            int p1 = ((ArcPuro) vaPT.elementAt(i)).getPlace();
            int t1 = ((ArcPuro) vaPT.elementAt(i)).getTransition();
            //System.out.println("---APT "+((ArcPuro) vaPT.elementAt(i)).getIndic()+" conecta place "+p1+" transicion "+t1);
            cplaces.add(p1);
            for (int j = i; j < vaTP.size(); j++) {
                ArcPuro b = (ArcPuro) vaTP.elementAt(j);
                int p2 = ((ArcPuro) vaTP.elementAt(j)).getPlace();
                int t2 = ((ArcPuro) vaTP.elementAt(j)).getTransition();
                cplaces.add(p2);
                //System.out.println("ATP "+((ArcPuro) vaTP.elementAt(j)).getIndex()+" conecta transicion "+t2+" place "+p2);
                if (p1 == p2) {
                    //System.out.println("Hay un ciclo");
                    ciclos.add(cplaces);
                    break;
                }
            }
        }

        for (int x = 0; x < ciclos.size(); x++) {
            ArrayList elementos = (ArrayList) ciclos.get(x);
            elementos.remove(elementos.size() - 1);
            System.out.println("Ciclo " + x + ":");
            for (int y = 0; y < elementos.size(); y++)
                System.out.print(elementos.get(y) + " ");

            System.out.println();
        }

    }

    boolean IsPrimitive(int col, int[][] m) {
        for (int i = 0; i < matrix.getRow(); i++) {
            if (m[i][col] < 0)
                return check(i, col, m);
        }
        return false;
    }

    boolean check(int row, int col, int[][] m) {
        for (int i = 0; i < matrix.getRow(); i++) {
            if (i != row) {
                if (m[i][col] != 0)
                    return false;
            }
        }
        return true;
    }

    boolean verificar_CorriendoPT(int place, int trans) {
        for (int i = 0; i < vControl.size(); i++) {
            if (vControl.elementAt(i).getPlace() == place && vControl.elementAt(i).getTransition() == trans) {
                return false;
            }
        }
        return true;
    }

    boolean verificar_CorriendoTP(int place, int trans) {
        for (int i = 0; i < vControlT.size(); i++) {
            if (vControlT.elementAt(i).getPlace() == place && vControlT.elementAt(i).getTransition() == trans) {
                return false;
            }
        }
        return true;
    }

    public void IncidenceMatrix1() {
        matrix = new IncidenceMatrix(vt.size(), vp.size(), vaPT, vaTP, headNodes);
        inputArcsT = matrix.getInputArcs();
        outputArcsT = matrix.getOutputArcs();
    }

    public void guardarMatrizIncidencia(String nomArch) throws IOException {
        FileOutputStream fos = new FileOutputStream(nomArch);
        ObjectOutputStream oos = new ObjectOutputStream(fos);

        int col = matrix.getCol();
        int row = matrix.getRow();
        int[][] m = matrix.getMatrix();

        oos.writeObject(new Integer(row));
        oos.writeObject(new Integer(col));


        for (int i = 0; i < row; i++) {
            System.out.print("\n");
            for (int j = 0; j < col; j++) {
                oos.writeObject(new Integer(m[i][j]));
                if (m[i][j] <= -1)
                    System.out.print(" " + m[i][j] + " ");
                else System.out.print("  " + m[i][j] + " ");
            }
        }
        oos.writeObject(vp);
        oos.writeObject(vt);

        oos.flush();
        oos.close();

        System.out.println("Archivo guardado");
//    abrirIncidenceMatrix(nomArch);
    }

    public int importar(String arch) throws IOException {
        System.out.println();

        abrirObj(arch);
        System.out.println(arch);
        System.out.println("importar** tables.size() = " + tables.size());
        Punto pt;
        PlacePuro p1;
        TransitionPura t1, tc;
        int elm = 0;

        // Generar estructura de Red de Petri
        limpiar(false);


// Generar la CCPN para los eventos (primitivos y compuestos)
        for (int i = 0; i < ev.size(); i++) {
//      pt = new Punto(0, 0);
            Vector estmp;
            System.out.println("*** Genera place");
            p1 = new PlacePuro(new Punto(0, 0), vp.size(), i, ((Event) ev.elementAt(i)).getEventName(), ((Event) ev.elementAt(i)).getCommand());
            p1.setTipo(((Event) ev.elementAt(i)).getType());
            vp.addElement(p1);
            System.out.println("Switch");
            switch (((Event) ev.elementAt(i)).getType()) {
                case 4: //AND
                    System.out.println("\nCase 4 AND\n");
                    t1 = new TransitionPura(new Punto(0, 0), vt.size(), 2, ((Event) ev.elementAt(i)).getType() /*, ((ECA) vReglas.elementAt(0)).getCondition(), ((ECA) vReglas.elementAt(0)).getVectorCond(), ((ECA) vReglas.elementAt(0)).getActionSql()*/);
                    t1.setH(5);
                    vt.addElement(t1);
                    estmp = ((Event) ev.elementAt(i)).getEvents();
                    for (int j = 0; j < estmp.size(); j++) {
                        conectEvents(i, placeIndexOf(((Integer) estmp.elementAt(j)).intValue()), t1);
                    }
                    a = new ArcPuroTP(t1.getCentro(), p1.getOrigen(), vaTP.size(), p1.getIndex(), t1.getIndice());
                    vaTP.addElement(a);
                    break;
                case 7: //SEQUENCE
                case 11: //SIMULTANEOUS
                    System.out.println("\nCase 7 Simultaneous\n");
                    t1 = new TransitionPura(new Punto(0, 0), vt.size(), 2, ((Event) ev.elementAt(i)).getType() /*, ((ECA) vReglas.elementAt(0)).getCondition(), ((ECA) vReglas.elementAt(0)).getVectorCond(), ((ECA) vReglas.elementAt(0)).getActionSql()*/);
                    t1.setH(5);
                    vt.addElement(t1);
                    estmp = ((Event) ev.elementAt(i)).getEvents();
                    for (int j = 0; j < estmp.size(); j++) {
                        conectEvents(i, placeIndexOf(((Integer) estmp.elementAt(j)).intValue()), t1/*, p1*/);
                    }  // for
                    a = new ArcPuroTP(t1.getCentro(), p1.getOrigen(), vaTP.size(), p1.getIndex(), t1.getIndice());
                    vaTP.addElement(a);
                    break;
                case 5: //OR
                    System.out.println("\nCase 5 OR\n");
                    estmp = ((Event) ev.elementAt(i)).getEvents();
                    for (int j = 0; j < estmp.size(); j++) {
                        t1 = new TransitionPura(new Punto(0, 0), vt.size(), 1, 5);
/*vane*/
                        t1.setH(1);
                        vt.addElement(t1);

                        t1 = conectEvents(i, placeIndexOf(((Integer) estmp.elementAt(j)).intValue()), t1);

                        a = new ArcPuroTP(t1.getCentro(), p1.getOrigen(), vaTP.size(), p1.getIndex(), t1.getIndice());
                        vaTP.addElement(a);
                    }

                    break;
                case 6: //NOT
                case 8: //CLOSURE
                case 9: //LAST
                case 10: //TIMES
                    System.out.println("\nCase 10 Times\n");
                    t1 = new TransitionPura(new Punto(0, 0), vt.size(), 2, ((Event) ev.elementAt(i)).getType());

                    t1.setH(5);
                    vt.addElement(t1);
                    estmp = ((Event) ev.elementAt(i)).getEvents();
                    conectEvents(i, placeIndexOf(((Integer) estmp.elementAt(0)).intValue()), t1/*, p1*/);
                    a = new ArcPuroTP(t1.getCentro(), p1.getOrigen(), vaTP.size(), p1.getIndex(), t1.getIndice());
                    vaTP.addElement(a);
                    break;
                case 12: //ANY
                    System.out.println("\nCase 12 ANY\n");
                    estmp = ((Event) ev.elementAt(i)).getEvents();

                    for (int j = 0; j < estmp.size(); j++) {
                        System.out.println("Antes de new TransitionPura");
                        t1 = new TransitionPura(new Punto(0, 0), vt.size(), 1/*2*/, 5/*, ((ECA) vReglas.elementAt(0)).getCondition(), ((ECA) vReglas.elementAt(0)).getVectorCond(), ((ECA) vReglas.elementAt(0)).getActionSql()*/);
                        t1.setH(1);
                        vt.addElement(t1);

                        t1 = conectEvents(i, placeIndexOf(((Integer) estmp.elementAt(j)).intValue()), t1);
                        a = new ArcPuroTP(t1.getCentro(), p1.getOrigen(), vaTP.size(), p1.getIndex(), t1.getIndice());
                        vaTP.addElement(a);
                    }
                    break;
            }
        }

//*** Conectamos los eventos con las reglas
        Vector arels;
        if (rules.size() > 0) {
            System.out.println("Conecta los eventos con las reglas");
            for (int i = 0; i < rules.size(); i++) {
                System.out.println("Siguiente regla: ");
                pt = new Punto(0, 0);
                t1 = new TransitionPura(pt, vt.size(), 0, ((Rule) rules.elementAt(i)).getCondition(), ((Rule) rules.elementAt(i)).getActionRule());
                vt.addElement(t1);
                System.out.println("***** Entra a Conect event");
                conectEvents(((Rule) rules.elementAt(i)).getIndexEvent(), placeIndexOf(((Rule) rules.elementAt(i)).getIndexEvent()), t1);
                System.out.println("***** FIN Conect event");
                pt = new Punto(0, 0);
                arels = ((Rule) rules.elementAt(i)).getActionRule().getElements();

//*** Verificamos la existencia de algun autociclo
                if (i > 0) {
                    elm = i - 1;
                    System.out.println("elm " + elm);
                    System.out.println("Lugar: " + ((ArcPuro) vaTP.elementAt(elm)).getTransition());
                    System.out.println("Conectado con Lugar: " + ((ArcPuro) vaTP.elementAt(elm)).getPlace());

                    if (((ArcPuro) vaTP.elementAt(elm)).getTransition() == ((ArcPuro) vaTP.elementAt(elm)).getPlace()) {
                        System.out.println("HAY AUTOCICLO!!!");
//*** Creamos una transición copia
                        System.out.println("Creamos un tans copia, i=" + i);
                        tc = new TransitionPura(pt, vt.size(), 1, ((Rule) rules.elementAt(i - 1)).getCondition(), ((Rule) rules.elementAt(i - 1)).getActionRule());
                        vt.add(tc);
                        System.out.println("Transicion: " + tc.getNombre());
                        tc.setH(1);

//*** El lugar copia se genera en conectEvent

//*** creamos los lugares con las transiciones
                        conectEvents(((Rule) rules.elementAt(i - ((Rule) rules.elementAt(i - 1)).getIndexEvent())).getIndexEvent(), placeIndexOf(((Rule) rules.elementAt(i - ((Rule) rules.elementAt(i - 1)).getIndexEvent())).getIndexEvent()), tc);
                    }
                }
//*** Fin del algoritmo para el autociclo
//*** identificamos si representa a regla.accion
                for (int j = 0; j < arels.size(); j++) {
                    System.out.println("Tratoaccion");

                    tratoAccion(((Action) arels.elementAt(j)).getAction(), t1);
                }
                System.out.println("FIN de conectar la regla\n");
            }
            System.out.println("FIN DE CONECTAR LAS REGLAS\n");
        }
//*** Hasta aqui genera la CCPN

//*** Distribución de la red de Petri sobre el canvas
        int xi = 0, yi = 20, xf = 0, yf = 20; // ULTIMA xi yi 50
        nArb = 0;
        arboles = new Vector();
        PlacesConsiderados = new Vector();
        TransConsideradas = new Vector();
        hojas = new Vector();
        headNodes = new Vector();
        for (int i = 0; i < vp.size(); i++) {
//       System.out.println("Place " + i);
            if (isHead(i)) {
                headNodes.add(new Integer(i));
//       System.out.println("PlaceCabeza " + i);
                eltosPorNivel = new Vector();
                Hoja h = new Hoja(i, 0, 0, arboles.size());
                agregaHoja(h);
                Punto ptd1 = new Punto(xi, yi);
                Punto ptd2 = new Punto(0, 0);
                Arbol myArbol = new Arbol(ptd1, ptd2, eltosPorNivel);
                xf = xi + myArbol.maximo() * 100;
                yf = (eltosPorNivel.size() + 1) * 10;
                ptd2 = new Punto(xf, yf);
                myArbol.setPtoF(ptd2);
                arboles.add(myArbol);
                xi = xf;
                yi = 20;
            }
        }

        for (int i = 0; i < vp.size(); i++) {
            if (!consideradoP(i)) {
                eltosPorNivel = new Vector();
                Hoja h = new Hoja(i, 0, 0, arboles.size());
                agregaHoja(h);
                Punto ptd1 = new Punto(xi, yi);
                Punto ptd2 = new Punto(0, 0);
                Arbol myArbol = new Arbol(ptd1, ptd2, eltosPorNivel);
                xf = xi + myArbol.maximo() * 100;
                yf = (eltosPorNivel.size() + 1) * 10;
                ptd2 = new Punto(xf, yf);
                myArbol.setPtoF(ptd2);
                arboles.add(myArbol);
                xi = xf;
                yi = 20;
            }
        }


        System.out.println("asignacion de coordenadas tables.size() = " + tables.size());
        // Asignación de coordenadas

        nxt = 0;
        nyt = 0;

        int n = 0;
        for (int m = 0; m < arboles.size(); m++) {
            int xi2 = ((Arbol) arboles.elementAt(m)).getPtoI().valX();
            int yi2 = ((Arbol) arboles.elementAt(m)).getPtoI().valY();
            int xf2 = ((Arbol) arboles.elementAt(m)).getPtoF().valX();
            Vector eltos = ((Arbol) arboles.elementAt(m)).getVector();
            int tam = ((Arbol) arboles.elementAt(m)).getVector().size();
            int[] arreglo = new int[tam];
            for (int mm = 0; mm < tam; mm++) arreglo[mm] = 1;
            while (n < hojas.size() && ((Hoja) hojas.elementAt(n)).getNumArbol() == m) {
                int niv = ((Hoja) hojas.elementAt(n)).getNivel();
                int eltosXniv = ((Integer) eltos.elementAt(niv)).intValue();
                int nx = xi2 + arreglo[niv] * ((int) ((xf2 - xi2) / (eltosXniv + 1)));
                arreglo[niv]++;
                int ny = yi2 + (niv) * 50;

                nx = ajustaX(nx);
                ny = ajustaY(ny);

                switch (((Hoja) hojas.elementAt(n)).getTipo()) {
                    case 0:
                        ((PlacePuro) vp.elementAt(((Hoja) hojas.elementAt(n)).getIndicePoT())).mover(nx, ny);
                        break;
                    case 1:
                        ((TransitionPura) vt.elementAt(((Hoja) hojas.elementAt(n)).getIndicePoT())).mover(nx, ny);
                        break;
                }
                n++;
                scroll(new Rectangle(ajustaTam(nx), ajustaTam(ny), ajustaTam(100), ajustaTam(100)));

                if (nx > nxt) {
                    nxt = nx;
                }

                if (ny > nyt) {
                    nyt = ny;
                }
            }  // while()
        }  // for(m)

        // Actualizamos coordenadas de arcos

        // Vector de arcos de Places a Transiciones
        for (int ls = 0; ls < vaPT.size(); ls++) {
            Punto orP = ((PlacePuro) vp.elementAt(((ArcPuro) vaPT.elementAt(ls)).getPlace())).getOrigen();
            Punto ceT = ((TransitionPura) vt.elementAt(((ArcPuro) vaPT.elementAt(ls)).getTransition())).getCentro();
            ((ArcPuro) vaPT.elementAt(ls)).actPuntos(orP, ceT);
        }

        // Vector de arcos de Transiciones a Places
        for (int ls = 0; ls < vaTP.size(); ls++) {
            Punto orP = ((PlacePuro) vp.elementAt(((ArcPuro) vaTP.elementAt(ls)).getPlace())).getOrigen();
            Punto ceT = ((TransitionPura) vt.elementAt(((ArcPuro) vaTP.elementAt(ls)).getTransition())).getCentro();
            ((ArcPuro) vaTP.elementAt(ls)).actPuntos(ceT, orP);
        }
        repaint();

        updated = true;
        return 0;
    }

    //*** representa a regla.accion
    public void tratoAccion(String act, TransitionPura t1) {
        int aex = existe(act);
        PlacePuro p2;
        ArcPuro a;
        System.out.println("string= " + act);
//*** si representa regla.accion
        if (aex >= 0) {
            p2 = (PlacePuro) vp.elementAt(aex);
            System.out.println("SI representa a regla.accion " + aex);
        }

//*** no representa regla.accion
        else {
            Punto pta = new Punto(0, 0);
            p2 = new PlacePuro(pta, vp.size(), vp.size(), "E" + vp.size(), act);
            p2.asignaTipo(act);
            vp.addElement(p2);
            System.out.println("NO representa a regla.accion");
        }
//*** creamos arco
        a = new ArcPuroTP(t1.getCentro(), p2.getOrigen(), vaTP.size(), p2.getIndex(), t1.getIndice());
        vaTP.addElement(a);
    }

    /**
     * Algoritmo para ubicar los componentes en X dentro de una cuadrícula
     */
    public int ajustaX(float x) {
        //*** inic coordenada de la 1ra linea
        //*** incre distancia entre dos lineas
        int j = 0, incre = 30, inic = 30, xfin = 15;

        //*** Obtenemos el tamaño del panel
        //*** d.width: ancho
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();

        //*** Encuadramos la coordenada x
        for (j = inic; j <= d.width; j = j + incre, xfin = xfin + incre) {
            if (x <= j) {
                x = xfin;
                break;
            }
        }

        return xfin;
    }

    /**
     * Algoritmo para ubicar los componentes en Y dentro de una cuadrícula
     */
    public int ajustaY(float y) {
        //*** inic: coordenada de la 1ra linea
        //*** incre: distancia entre dos lineas
        int j = 0, incre = 30, inic = 30, yfin = 15;

        //*** Obtenemos el tamaño del panel
        //*** d.height: alto
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();

        //*** Encuadramos la coordenada y
        for (j = inic; j <= d.height; j = j + incre, yfin = yfin + incre) {
            if (y <= j) {
                y = yfin;
                break;
            }
        }
        return yfin;
    }


    public void agregaHoja(Hoja h) {

        try {
            int vn = ((Integer) eltosPorNivel.elementAt(h.getNivel())).intValue();
            vn++;
            Integer ne = new Integer(vn);
            eltosPorNivel.setElementAt(ne, h.getNivel());
        } catch (Exception e) {
            Integer ne = new Integer(1);
            eltosPorNivel.add(ne);
        }

        hojas.add(h);

        switch (h.getTipo()) {
            case 0:
                considerarPlace(h.getIndicePoT());
                for (int j = 0; j < vaPT.size(); j++) {
                    if (((ArcPuro) vaPT.elementAt(j)).getPlace() == h.getIndicePoT() &&
                            (!consideradoT(((ArcPuro) vaPT.elementAt(j)).getTransition()))) {
                        int t = ((ArcPuro) vaPT.elementAt(j)).getTransition();
                        agregaHoja(new Hoja(t, 1, h.getNivel() + 1, h.getNumArbol()));
                        break;
                    }
                }
                // Nodo Terminal
                break;

            case 1:
                considerarTrans(h.getIndicePoT());
                for (int j = 0; j < vaTP.size(); j++) {
                    if (((ArcPuro) vaTP.elementAt(j)).getTransition() == h.getIndicePoT() &&
                            (!consideradoP(((ArcPuro) vaTP.elementAt(j)).getPlace()))) {
                        int p = ((ArcPuro) vaTP.elementAt(j)).getPlace();
                        agregaHoja(new Hoja(p, 0, h.getNivel() + 1, h.getNumArbol()));
                    }
                }
                break;
        } // switch()
    } // agregaHoja()


    public void considerarPlace(int pl) {
        Integer oi = new Integer(pl);
        PlacesConsiderados.add(oi);
    }

    public void considerarTrans(int tr) {
        Integer oi = new Integer(tr);
        TransConsideradas.add(oi);
    }

    public boolean isHead(int p) {
        for (int aa = 0; aa < vaTP.size(); aa++)
            if (((ArcPuro) vaTP.elementAt(aa)).getPlace() == p)
                return false;
        return true;
    }

    public boolean consideradoP(int p) {
        for (int aa = 0; aa < PlacesConsiderados.size(); aa++)
            if (((Integer) PlacesConsiderados.elementAt(aa)).intValue() == p)
                return true;
        return false;
    }

    public boolean consideradoT(int t) {
        for (int aa = 0; aa < TransConsideradas.size(); aa++)
            if (((Integer) TransConsideradas.elementAt(aa)).intValue() == t)
                return true;
        return false;
    }

    public int unePT(int p) {
        for (int x = 0; x < vaPT.size(); x++) {
            System.out.println("--p= " + p);
            System.out.println("--vaPT: " + ((ArcPuro) vaPT.elementAt(x)).getPlace());
            if (((ArcPuro) vaPT.elementAt(x)).getPlace() == p)
                return ((ArcPuro) vaPT.elementAt(x)).getTransition();
        }
        return -1;
    }

    public ArcPuro removeArcoPT(int pl) {
        ArcPuro ap = null;
        for (int m = 0; m < vaPT.size(); m++) {
            if (((ArcPuro) vaPT.elementAt(m)).getPlace() == pl) {
                ap = (ArcPuro) vaPT.remove(m);
                break;
            }
        }
        // Actualiza indices
        for (int n = 0; n < vaPT.size(); n++) {
            ((ArcPuro) vaPT.elementAt(n)).setIndice(n);
        }
        return ap;
    }

    public int existe(String act) {
        for (int x = 0; x < vp.size(); x++)
            if (((PlacePuro) vp.elementAt(x)).getCommand().equalsIgnoreCase(act))
                return x;
        return -1;
    }

    public void abrirObj(String fn) { //ESTE COMENTARIO ES NUEVO. LEE EL OBJ E INIC VAR
        tables = new Vector();
        ev = new Vector();
        rules = new Vector();
        System.out.println("abrirObj function 1 ....tables.size() = " + tables.size());
        try {
            FileInputStream fis = new FileInputStream(fn);
            ObjectInputStream ois = new ObjectInputStream(fis);

            try {
                tables = (Vector) ois.readObject();
                System.out.println("vector Tables leído");
            } catch (Exception exc) {
                System.out.println("Se cachó la excepción....tables");
            }
            try {
                ev = (Vector) ois.readObject();
                System.out.println("vector Eventos le�do");
            } catch (Exception exc) {
                System.out.println("Se cachó la excepción....ev");
            }
            try {
                rules = (Vector) ois.readObject();
                System.out.println("vector Rules le�do");
            } catch (Exception exc) {
                System.out.println("Se cachó la excepción....rules");
            }

//    ois.flush();
            ois.close();
        } catch (IOException ioe) {
            System.out.println("Error al definir objetos de IO...");
        }

//*** mostramos los datos de la tabla, de los eventos y de las reglas
        System.out.println("BUSCAR AQUI ++++++++++++++++++++++++++++++++++++++++++++");
        for (int i = 0; i < tables.size(); i++) {
//System.out.print("\n********** Tamaño tablas "+tables.size()+"\n");
            System.out.println(((Table) tables.elementAt(i)).toString());
        }
        for (int i = 0; i < ev.size(); i++) {
//System.out.print("\n********** Tamaño eventos "+ev.size()+"\n");
            System.out.println("Aquiii " + ((Event) ev.elementAt(i)).toString());
        }
        System.out.println("rules.size() = " + rules.size());
        for (int i = 0; i < rules.size(); i++) {
//System.out.print("\n********** Una reglas más\n");
            System.out.println(((Rule) rules.elementAt(i)).toString());
        }
        System.out.println("TERMINAR AQUI ++++++++++++++++++++++++++++++++++++++++++++");
    }

    public TransitionPura conectEvents(int i, int j, TransitionPura t1) {
        Punto pt;
        ArcPuro a;
        PlacePuro p2, p3;
        TransitionPura t2, t3;

        System.out.println("connect events 0...; j = " + j + "; vp.size() = " + vp.size());
        PlacePuro ptmp = (PlacePuro) vp.elementAt(j);
        int transIndex;
        System.out.println("connect events donde entra:");

//*** p1·=0 p1 tiene lugar de salida
        if ((transIndex = unePT(ptmp.getIndex())) < 0) {
            System.out.println("Entro al If unePT<0");
            System.out.println("P tiene lugar de salida, solo crea arco");
            a = new ArcPuroPT(ptmp.getOrigen(), t1.getCentro(), vaPT.size(), ptmp.getIndex(), t1.getIndice());
            if (((Event) ev.elementAt(i)).getType() == 6 && t1.getType() == 2)
                a.setInhibitorArc();

            if ((((Event) ev.elementAt(i)).getType() == 10 && t1.getType() == 2) || (((Event) ev.elementAt(i)).getType() == 12 && t1.getType() == 0))
                a.setPeso(((Event) ev.elementAt(i)).getMValue());

            vaPT.addElement(a);
        }

//*** El lugar P no tiene salida
        else {
            System.out.println("Entro al else unePT>0");
            System.out.println("P no tiene lugar de salida, cramos una transición");
            switch (((TransitionPura) vt.elementAt(transIndex)).getType()) {
                case 0: //type Transition : Rule
                case 2: //type Transition : Composite Event
                    System.out.println("Caso 2, transicion compuesto o transicion regla");
                    System.out.println("t1.getType()= " + t1.getType());

                    // Agregar COPY
                    if (t1.getType() == 1) {
                        System.out.println("Tipo 1, t2=t1");
                        t2 = t1;
                    } else {
                        System.out.println("Tipo, t2!=t1");
                        pt = new Punto(0, 0);
                        t2 = new TransitionPura(pt, vt.size(), 1);
                        t2.setH(1);
                        vt.addElement(t2);
                    }

                    ArcPuro ap = removeArcoPT(ptmp.getIndex());
                    a = new ArcPuroPT(ptmp.getOrigen(), t2.getCentro(), vaPT.size(), ptmp.getIndex(), t2.getIndice());
                    /***/a.setPeso(ap.getPeso());
                    vaPT.addElement(a);

                    pt = new Punto(0, 0);
                    System.out.println("Creamos un lugar copia");
                    p2 = new PlacePuro(pt, vp.size(), i, "CopyOf_" + ptmp.getNombre(), ptmp.getCommand());
                    p2.setTipo(14);
                    vp.addElement(p2);

                    a = new ArcPuroTP(t2.getCentro(), p2.getOrigen(), vaTP.size(), p2.getIndex(), t2.getIndice());
                    /***/a.setPeso(ap.getPeso());
                    vaTP.addElement(a);

                    t3 = (TransitionPura) vt.elementAt(transIndex);
/* posible inh */
                    a = new ArcPuroPT(p2.getOrigen(), t3.getCentro(), vaPT.size(), p2.getIndex(), t3.getIndice());
                    a.setPeso(ap.getPeso());
                    if (ap.getInhibitorArc())
                        a.setInhibitorArc();

                    vaPT.addElement(a);

                    System.out.println("Ultimo if, t1.getType()=!0");
                    if (t1.getType() != 1) {
                        System.out.println("** SIII, creamos copia");
                        pt = new Punto(0, 0);
                        p2 = new PlacePuro(pt, vp.size(), i, "CopyOf_" + ptmp.getNombre(), ptmp.getCommand());
                        p2.setTipo(14);
                        vp.addElement(p2);

                        a = new ArcPuroTP(t2.getCentro(), p2.getOrigen(), vaTP.size(), p2.getIndex(), t2.getIndice());
                        /***/a.setPeso(ap.getPeso());
                        vaTP.addElement(a);

                        pt = new Punto(0, 0);
                        a = new ArcPuroPT(p2.getOrigen(), t1.getCentro(), vaPT.size(), p2.getIndex(), t1.getIndice());
                        /***/a.setPeso(ap.getPeso());
                        if (((Event) ev.elementAt(i)).getType() == 6 && t1.getType() == 2)
                            a.setInhibitorArc();

                        vaPT.addElement(a);
// Sigue pendiente                 tratoAccion( ((ECA) vReglas.elementAt(i)).getAction(), t5, i);
                    }
                    break;

                case 1: //type Transition : Copy
                    System.out.println("tipo transicion copy?");
                    if (t1.getType() == 1) {
                        System.out.println("SIIII");
                        return ((TransitionPura) vt.elementAt(transIndex));
                    } else {
                        System.out.println("NOOO");
                        pt = new Punto(0, 0);
                        p2 = new PlacePuro(pt, vp.size(), i, "CopyOf_" + ptmp.getNombre(), "");
                        p2.setTipo(14);
                        vp.addElement(p2);
                        t2 = (TransitionPura) vt.elementAt(transIndex);
                        a = new ArcPuroTP(t2.getCentro(), p2.getOrigen(), vaTP.size(), p2.getIndex(), t2.getIndice());
                        vaTP.addElement(a);
                        a = new ArcPuroPT(p2.getOrigen(), t1.getCentro(), vaPT.size(), p2.getIndex(), t1.getIndice());
                        vaPT.addElement(a);
                    }
                    break;
            } // switch
        } // else
        return t1;
    }

    /**
     * Metodo que devuelve el indice del lugar que representa el evento de entrada para la regla en la posicion rul.
     */
    public int placeIndexOf(int indEvent) {
        for (int pif = 0; pif < vp.size(); pif++) {
            if (((PlacePuro) vp.elementAt(pif)).getIndexEvent() == indEvent)
                return pif;
        }
        return -1;
    }

    //Metodo para dibujar de una place a muchas transicions
    public void agregar_Anterior(int ptoP) {
        for (int i = 0; i < vaPT.size(); i++) {
            if (((ArcPuro) vaPT.elementAt(i)).getPlace() == ptoP) {


                if (((TransitionPura) vt.elementAt(((ArcPuro) vaPT.elementAt(i)).getTransition())).getTipo() != 2) {
                    ((TransitionPura) vt.elementAt(((ArcPuro) vaPT.elementAt(i)).getTransition())).setTipo(2);
                    //Agregar Transicion
                    Punto ptran = ((TransitionPura) vt.elementAt(((ArcPuro) vaPT.elementAt(i)).getTransition())).getCentro();
                    int indice = ((ArcPuro) vaPT.elementAt(i)).getTransition();
                    int x = ptran.valX();
                    int y = ptran.valY();

                    //Agregar place
                    Punto ptrans = new Punto(x, y + 60);
                    p = new PlacePuro(ptrans, vp.size());
                    p.setTipo(14);
                    vp.addElement(p);

                    //Agregar arco de transicion a place
                    ArcPuroTP mod = new ArcPuroTP(ptran, ptrans, vaTP.size(), vp.size() - 1, ((ArcPuro) vaPT.elementAt(i)).getTransition());
                    vaTP.addElement(mod);

                    Punto place = new Punto(x, y + 120);
                    TransitionPura t = new TransitionPura(place, vt.size());
                    vt.addElement(t);

                    //Agregar arco de place a transcion
                    ArcPuroPT arc = new ArcPuroPT(ptrans, place, vaPT.size(), vp.size() - 1, vt.size() - 1);
                    vaPT.addElement(arc);
                }
            }

        }
    }

    //Metodo para verificar si existe una transicion copia
    public boolean existeTransCopia(int indice) {
        for (int i = 0; i < vaPT.size(); i++) {
            int place = ((ArcPuro) vaPT.elementAt(i)).getPlace();
            int trans = ((ArcPuro) vaPT.elementAt(i)).getTransition();
            if (place == indice) {
                if (((TransitionPura) vt.elementAt(trans)).getTipo() == 1) {
                    return true;
                }
            }
        }
        return false;
    }

    //Metodo para verificar si apartir de una place existe ya una transicion compuesta
    public boolean existeTransCompuesta(int indice) {
        for (int i = 0; i < vaPT.size(); i++) {
            int place = ((ArcPuro) vaPT.elementAt(i)).getPlace();
            int trans = ((ArcPuro) vaPT.elementAt(i)).getTransition();
            if (place == indice) {
                if (((TransitionPura) vt.elementAt(trans)).getTipo() == 2) {
                    return true;
                }
            }
        }
        return false;
    }

    //Metodo para verificar si apartir de una place existe ya una transicion simple
    public boolean existeTransSimple(int indice) {
        for (int i = 0; i < vaPT.size(); i++) {
            int place = ((ArcPuro) vaPT.elementAt(i)).getPlace();
            int trans = ((ArcPuro) vaPT.elementAt(i)).getTransition();
            if (place == indice) {
                if (((TransitionPura) vt.elementAt(trans)).getTipo() == 0) {
                    return true;
                }
            }
        }
        return false;
    }

    public void agregarTransCompuesta(int indiceP, boolean prin, int inc, int indice_A, boolean agre, int indice_P, boolean prim) {
        //Parte que se agrega la place
        int indice = transicionCompuesta(indiceP);
        if (indice == -1) {
            System.out.println("Error");
        }
        Punto ptran = ((TransitionPura) vt.elementAt(indice)).getCentro();
        System.out.println(ptran + " ");
        int x = ptran.valX();
        int y = ptran.valY();
        Punto ptrans = new Punto(x + 90, y + 60);
        p = new PlacePuro(ptrans, vp.size());
        p.setTipo(14);
        vp.addElement(p);

        //Parte de dibujar el Arco
        ((TransitionPura) vt.elementAt(indice)).setTipo(2);
        ArcPuroTP mod = new ArcPuroTP(ptran, ptrans, vaTP.size(), vp.size() - 1, ((TransitionPura) vt.elementAt(indice)).getIndice());
        vaTP.addElement(mod);

        //Parte para dibujar la transicion
        Punto place = new Punto(x + 90, y + 120);
        TransitionPura t = new TransitionPura(place, vt.size());
        vt.addElement(t);


        //Parte de dibujar el arco de place a transicion
        ArcPuroPT arc = new ArcPuroPT(ptrans, place, vaPT.size(), vp.size() - 1, vt.size() - 1);
        vaPT.addElement(arc);

        //Parte para jalar el arco
        if (agre) {
            Punto p = ((ArcPuro) vaTP.elementAt(indice_P)).getPoint2();
            int ind = ((ArcPuro) vaTP.elementAt(indice_P)).getPlace();
            mod = new ArcPuroTP(place, p, vaTP.size(), ind, vt.size() - 1);
            vaTP.addElement(mod);
            ((ArcPuro) vaTP.elementAt(indice_P)).setSelected(true);
            borrar_selec();
            repaint();
        }
        if (prim) {
            ///Incia para dibujar lo que ya se tenia
            ptrans = new Punto(x - inc, y + 60);
            p = new PlacePuro(ptrans, vp.size());
            p.setTipo(14);
            vp.addElement(p);

            //Parte de dibujar el Arco
            ((TransitionPura) vt.elementAt(indice)).setTipo(2);
            mod = new ArcPuroTP(ptran, ptrans, vaTP.size(), vp.size() - 1, ((TransitionPura) vt.elementAt(indice)).getIndice());
            vaTP.addElement(mod);

            //Parte para dibujar la transicion
            place = new Punto(x - inc, y + 120);
            t = new TransitionPura(place, vt.size());
            vt.addElement(t);

            //Parte de dibujar el arco de place a transicion
            arc = new ArcPuroPT(ptrans, place, vaPT.size(), vp.size() - 1, vt.size() - 1);
            vaPT.addElement(arc);

            //Parte para jalar el arco
            if (prin) {
                Punto p = ((ArcPuro) vaTP.elementAt(indice_A)).getPoint2();
                int ind = ((ArcPuro) vaTP.elementAt(indice_A)).getPlace();
                mod = new ArcPuroTP(place, p, vaTP.size(), ind, vt.size() - 1);
                vaTP.addElement(mod);
                ((ArcPuro) vaTP.elementAt(indice_A)).setSelected(true);
                borrar_selec();
            }
        }
    }

    public int transicionCompuesta(int indiceP) {
        for (int i = 0; i < vaPT.size(); i++) {
            int place = ((ArcPuro) vaPT.elementAt(i)).getPlace();
            if (place == indiceP) {
                return ((ArcPuro) vaPT.elementAt(i)).getTransition();

            }
        }
        return -1;
    }

    //Regresa si tiene algo de place a transition y de transion a place
    public boolean tiene_algoT(int indice) {
        for (int i = 0; i < vaPT.size(); i++) {
            int place = ((ArcPuro) vaPT.elementAt(i)).getPlace();
            int trans = ((ArcPuro) vaPT.elementAt(i)).getTransition();
            if (place == indice) {
                if (((TransitionPura) vt.elementAt(trans)).getTipo() == 0) {
                    for (int j = 0; j < vaTP.size(); j++) {
                        if (trans == ((ArcPuro) vaTP.elementAt(j)).getTransition()) {
                            return true;
                        }
                    }
                    return false;
                }
            }
        }
        return false;
    }

    //Regresa si tiene algo de place a transition y de transion a place
    public boolean tiene_algo(int indice) {
        for (int j = 0; j < vaTP.size(); j++) {
            if (indice == ((ArcPuro) vaTP.elementAt(j)).getTransition()) {
                System.out.println("Si tiene algo en la que se va a borrar!!!!!");
                return true;
            }
        }
        return false;

    }

    public int indice_arcoT(int indice) {
        for (int i = 0; i < vaPT.size(); i++) {
            int place = ((ArcPuro) vaPT.elementAt(i)).getPlace();
            int trans = ((ArcPuro) vaPT.elementAt(i)).getTransition();
            if (place == indice) {
                if (((TransitionPura) vt.elementAt(trans)).getTipo() == 0) {
                    for (int j = 0; j < vaTP.size(); j++) {
                        if (trans == ((ArcPuro) vaTP.elementAt(j)).getTransition()) {
                            return ((ArcPuro) vaTP.elementAt(j)).getIndice();
                        }
                    }
                    return -1;
                }
            }
        }
        return -1;
    }

    public int indice_arco(int indice) {
        for (int j = 0; j < vaTP.size(); j++) {
            if (indice == ((ArcPuro) vaTP.elementAt(j)).getTransition()) {
                System.out.println("Lo que se va a regresar es: " + ((ArcPuro) vaTP.elementAt(j)).getIndice());
                System.out.println("Y la places es: " + ((ArcPuro) vaTP.elementAt(j)).getPlace());
                return ((ArcPuro) vaTP.elementAt(j)).getIndice();
            }
        }
        return -1;

    }

    //VErificar si existe algo en este punto
    boolean verificar(Punto pto) {
        if (checkTrans(pto) || checkPlace(pto)) {
            return false;
        } else
            return true;
    }

    void nuevasTrans(int indice_T, int indice_P, int indice_A, boolean existe_algo) {
        System.out.println("indice_T: " + indice_T + "\nindice_P: " + indice_P);
        int indice_comp = transicionCompuesta(indice_P);
        Punto ptran = ((TransitionPura) vt.elementAt(indice_comp)).getCentro();
        System.out.println(ptran + " ");
        int x = ptran.valX();
        int y = ptran.valY();
        Punto ptrans = new Punto(x + 120, y + 60);
        while (true) {
            if (!verificar(ptrans)) {
                ptrans.nvoX(ptrans.valX() + 30);
            } else {
                break;
            }
        }

        p = new PlacePuro(ptrans, vp.size());
        p.setTipo(14);//tipo copia
        vp.addElement(p);

        //Parte de dibujar el Arco
        ArcPuroTP mod = new ArcPuroTP(ptran, ptrans, vaTP.size(), vp.size() - 1, ((TransitionPura) vt.elementAt(indice_comp)).getIndice());
        vaTP.addElement(mod);

        Punto algo = new Punto(x + 120, y + 120);
        while (true) {
            if (!verificar(algo)) {
                algo.nvoX(algo.valX() + 30);
            } else {
                break;
            }
        }
        ((TransitionPura) vt.elementAt(indice_T)).mover(algo.valX(), algo.valY());

        //Parte de dibujar el arco de place a transicion
        ArcPuroPT arc = new ArcPuroPT(ptrans, algo, vaPT.size(), vp.size() - 1, indice_T);
        vaPT.addElement(arc);

        if (existe_algo) {
            int nueva_place = ((ArcPuro) vaTP.elementAt(indice_A)).getPlace();
            ((ArcPuro) vaTP.elementAt(indice_A)).setSelected(true);
            eliminar_TP();

            Punto a = new Punto(((PlacePuro) vp.elementAt(nueva_place)).getOrigen());
            //Parte de dibujar el Arco
            mod = new ArcPuroTP(algo, a, vaTP.size(), nueva_place, indice_T);
            vaTP.addElement(mod);
        }

    }

    void generar_reglas() {
        Compilador genR = new Compilador(this);
        genR.setVisible(true);
    }

    //vaTP
    private int ObtenerArcTP(int Trans) {
        for (int j = 0; j < vaTP.size(); j++) {
            ArcPuroTP arco = (ArcPuroTP) vaTP.elementAt(j);
            if (arco.getTransition() == Trans) {
                return j;
            }
        }
        return -1;
    }

    private int ObtenerArcPT(int Trans) {
        for (int j = 0; j < vaPT.size(); j++) {
            ArcPuroPT arco = (ArcPuroPT) vaPT.elementAt(j);
            if (arco.getTransition() == Trans)
                return j;
        }
        return -1;
    }

    private Vector<Integer> obtenerArcoTP(int indice) {
        Vector<Integer> v = new Vector();
        for (int j = 0; j < vaPT.size(); j++) {
            ArcPuroPT arco = (ArcPuroPT) vaPT.elementAt(j);
            if (arco.getTransition() == indice)
                v.add(j);
        }
        return v;
    }
}
