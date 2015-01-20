package Components;

import java.io.Serializable;

/**
 * Created by serch on 14/01/15.
 */
public class ArcPuroPT extends ArcPuro implements Serializable {
    public ArcPuroPT() {
        super();
        calcDrawLine();
        calcX();
        calcY();
    }

    public ArcPuroPT(Punto pt1, Punto pt2, int indice, int indPlace, int indTrans) {
        super(pt1, pt2, indice, indPlace, indTrans);
        calcDrawLine();
        calcX();
        calcY();
    }

    @Override
    public void calcDrawLine() {
        Punto pp, pt, pp1 = getPoint1(), pp2 = getPoint2();
        double beta2 = getBeta(), beta3;
        int tipo = -1;
        if (pp1.valX() < pp2.valX() && pp1.valY() < pp2.valY()) {
            beta3 = beta2;
            tipo = 0;
        } else {
            if (pp2.valX() < pp1.valX() && pp2.valY() < pp1.valY()) {
                beta3 = Math.PI + beta2;
                tipo = 1;
            } else {
                if (pp2.valX() < pp1.valX() && pp2.valY() < pp1.valY()) {
                    beta3 = Math.PI + beta2;
                    tipo = 2;
                } else {
                    if (beta2 > 0.0) {
                        beta3 = beta2;
                        tipo = 0;
                    } else {
                        tipo = 3;
                        if (pp2.valX() < pp1.valX() && pp2.valY() == pp1.valY()) {
                            beta3 = Math.PI;
                        } else {
                            beta3 = Math.PI * 2 + beta2;

                        }
                    }
                }
            }
        }
        // int xp = getPoint1.valX() + (int)(15 /*PlacePuro.rad*/ * Math.cos(beta2));
        // int yp = getPoint1.valY() + (int)(15 /*PlacePuro.rad*/ * Math.sin(beta2));
        int xp, yp, xt, yt;
        if (beta3 < sigma1) {
            xp = getPoint1().valX() + (int)(15 /*PlacePuro.rad*/ * Math.cos(beta3));
            yp = getPoint1().valY() + (int)(15 /*PlacePuro.rad*/ * Math.sin(beta3));
            xt = getPoint2().valX() - Rectangulo.ws / 2;
            yt = getPoint2().valY() - (int)(Rectangulo.ws / 2 * Math.tan(beta3));
        } else {
            if (beta3 < PI2) {
                xp = getPoint1().valX() + (int)(15 /*PlacePuro.rad*/ * Math.cos(beta2));
                yp = getPoint1().valY() + (int)(15/*PlacePuro.rad*/ * Math.sin(beta3));
                xt = getPoint2().valX() - (int)((float)(Rectangulo.hs / 2) / Math.tan(beta2));
                yt = getPoint2().valY() - Rectangulo.hs / 2;
            } else {
                if (beta3 < sigma2) {
                    xp = getPoint1().valX() - (int)(15/*PlacePuro.rad*/ * Math.cos(beta2));
                    if (beta3 == PI2) {
                        yp = getPoint1().valY() + (int)(15/*lacePuro*/ * Math.sin(beta2));
                    } else {
                        yp = getPoint1().valY() - (int)(15/*PlacePuro.rad*/ * Math.sin(beta2));
                    }
                    xt = getPoint2().valX() - (int)((float)(Rectangulo.hs / 2) / Math.tan(beta2));
                    yt = getPoint2().valY() - Rectangulo.hs / 2;
                } else {
                    if (beta3 < sigma3) {
                        xp = getPoint1().valX() - (int)(15/*PlacePuro.rad*/ * Math.cos(beta2));
                        yp = getPoint1().valY() - (int)(15 /*PlacePuro.rad*/ * Math.sin(beta2));
                        xt = getPoint2().valX() + Rectangulo.ws / 2;
                        yt = getPoint2().valY() + (int)(Rectangulo.ws / 2 * Math.tan(beta3));
                    } else {
                        if (beta3 < Math.PI * 1.5) {
                            xp = getPoint1().valX() - (int)(15 /*PlacePuro.rad*/ * Math.cos(beta2));
                            yp = getPoint1().valY() - (int)(15 /*PlacePuro.rad*/ * Math.sin(beta2));
                            xt = getPoint2().valX() + (int)((float)(Rectangulo.hs / 2) / Math.tan(beta3));
                            yt = getPoint2().valY() + Rectangulo.hs / 2;
                        } else {
                            if (beta3 < sigma4) {
                                xp = getPoint1().valX() + (int)(15/*PlacePuro.rad*/ * Math.cos(beta2));
                                yp = getPoint1().valY() + (int)(15/*PlacePuro.rad*/ * Math.sin(beta2));
                                xt = getPoint2().valX() + (int)((float)(Rectangulo.hs / 2) / Math.tan(beta2));
                                yt = getPoint2().valY() + Rectangulo.hs / 2;
                            } else {
                                xp = getPoint1().valX() + (int)(15/*PlacePuro.rad*/ * Math.cos(beta2));
                                yp = getPoint1().valY() + (int)(15/*PlacePuro.rad*/ * Math.sin(beta2));
                                xt = getPoint2().valX() - Rectangulo.ws / 2;
                                yt = getPoint2().valY() - (int)(Rectangulo.ws / 2 * Math.tan(beta2));
                            }
                        }
                    }
                }
            }
        }
        System.out.println("Beta3 = " + (beta3 * 180.0 / Math.PI) + "; Tipo = " + tipo);
        pp = new Punto(xp, yp);
        pt = new Punto(xt, yt);
        setLineDraw(pp, pt);
    }
}
