package Components;

import java.io.Serializable;

/**
 * Created by serch on 17/01/15.
 */
public class ArcPuroTP extends ArcPuro implements Serializable{
    String isql = "";

    // Build ArcPuro with coordinates in (0,0) and (1,1)
    public ArcPuroTP() {
        super();
        calcDrawLine();
        calcX();
        calcY();
    }

    public ArcPuroTP(Punto pt1, Punto pt2, int indice, int indPlace, int indTransition) {
        super(pt1, pt2, indice, indPlace, indTransition);
        calcDrawLine();
        calcX();
        calcY();
    }

    @Override
    public void calcDrawLine() {
        Punto pp, pt, pp1 = getPoint1(), pp2 = getPoint2();
        double beta2 = getBeta(), beta3;
        int tipo = -1;
        if ( pp1.valX() <= pp2.valX() && pp1.valY() < pp2.valY()) {
            beta3 = beta2;
            tipo = 0;
        } else {
            if (pp2.valX() < pp1.valX() && pp1.valY() < pp2.valY()) {
                beta3 = Math.PI + beta2;
                tipo = 1;
            } else {
                if (pp2.valX() < pp1.valX() && pp2.valY() < pp1.valY()) {
                    beta3 = Math.PI + beta2;
                    tipo = 2;
                } else {
                    if (pp1.valX() > pp2.valX()) {
                        beta3 = Math.PI;
                        tipo = 0;
                    } else {
                        beta3 = Math.PI * 2 + beta2;
                        tipo = 3;
                    }
                }
            }
        }
        int xp, yp, xt, yt;
        if (beta3 < sigma1) {
            xp = getPoint2().valX() - (int) (15 /*PlacePuro.rad*/ * Math.cos(beta3));
            yp = getPoint2().valY() - (int) (15 /*PlacePuro.rad*/ * Math.sin(beta3));
            xt = getPoint1().valX() + Rectangulo.ws/2;
            yt = getPoint1().valY() + (int) (Rectangulo.ws / 2 * Math.tan(beta3));
        } else {
            if (beta3 < PI2) {
                xp = getPoint2().valX() - (int) (15 /*PlacePuro.rad*/ * Math.cos(beta2));
                yp = getPoint2().valY() - (int) (15 /*PlacePuro.rad*/ * Math.sin(beta2));
                xt = getPoint1().valX() + (int) ((float) (Rectangulo.hs / 2) / Math.tan(beta2));
                yt = getPoint1().valY() + Rectangulo.hs / 2;
            } else {
                if (beta3 < sigma2) {
                    xp = getPoint2().valX() + (int) (15 /*PlacePuro.rad*/ * Math.cos(beta2));
                    if (beta3 == PI2) {
                        yp = getPoint2().valY() - (int) (15 /*PlacePuro.rad*/ * Math.sin(beta2));
                    } else {
                        yp = getPoint2().valY() + (int) (15 /*PlacePuro.rad*/ * Math.sin(beta2));
                    }
                    xt = getPoint1().valX() + (int) ((float) (Rectangulo.hs / 2) / Math.tan(beta2));
                    yt = getPoint1().valY() + Rectangulo.hs / 2;
                } else {
                    if (beta3 < sigma3) {
                        xp = getPoint2().valX() + (int) (15 /*PlacePuro.rad*/ * Math.cos(beta2));
                        yp = getPoint2().valY() + (int) (15 /*PlacePuro.rad*/ * Math.sin(beta2));
                        xt = getPoint1().valX() - Rectangulo.ws / 2;
                        yt = getPoint1().valY() - (int) (Rectangulo.ws / 2 * Math.tan(beta3));
                    } else {
                        if (beta3 < Math.PI*1.5) {
                            xp = getPoint2().valX() + (int) (15 /*PlacePuro.rad*/ * Math.cos(beta2));
                            yp = getPoint2().valY() + (int) (15 /*PlacePuro.rad*/ * Math.sin(beta2));
                            xt = getPoint1().valX() - (int) ((float) (Rectangulo.hs/2) / Math.tan(beta3));
                            yt = getPoint1().valY() - Rectangulo.hs/2;
                        } else {
                            if(beta3 < sigma4) {
                                xp = getPoint2().valX() - (int) (15 /*PlacePuro.rad*/ * Math.cos(beta2));
                                yp = getPoint2().valY() - (int) (15 /*PlacePuro.rad*/ * Math.sin(beta2));
                                xt = getPoint1().valX() - (int) ((float) (Rectangulo.hs/2) / Math.tan(beta2));
                                yt = getPoint1().valY() - Rectangulo.hs/2;
                            } else {
                                xp = getPoint2().valX() - (int) (15 /*PlacePuro.rad*/ * Math.cos(beta2));
                                yp = getPoint2().valY() - (int) (15 /*PlacePuro.rad*/ * Math.sin(beta2));
                                xt = getPoint1().valX() + Rectangulo.ws/2;
                                yt = getPoint1().valY() + (int) (Rectangulo.ws/2 * Math.tan(beta2));
                            }
                        }
                    }
                }
            }
        }
        pp = new Punto(xp, yp);
        pt = new Punto(xt, yt);
        setLineDraw(pt, pp);
    }
}
