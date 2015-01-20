package Components;

import javax.swing.JPanel;
import java.awt.*;
import java.util.Vector;

/**
 * Created by serch on 19/01/15.
 */
public class IncidenceMatrix extends JPanel {
    int[][] matrix;
    int[][] ArcsMatrix;
    Vector inputArcs, outputArcs;
    int row, col;
    Termination t;

    public IncidenceMatrix(int row, int col, Vector input, Vector output) {
        super();
        setBackground(Color.white);
        this.row = row;
        this.col = col;
        System.out.println("row = " + row);
        System.out.println("col = " + col);
        matrix = new int[row][col];
        ArcsMatrix = new int[row][col];
        inputArcs = new Vector();
        outputArcs = new Vector();
        for(int i = 0; i < row; i++)
            for(int j = 0; j<col; j++) {
                matrix[i][j] = 0;
                ArcsMatrix[i][j] = -1;
            }
        int pl, tr;
        System.out.println("input.size() = " + input.size());
        for(int i = 0; i < input.size(); i++) {
            tr = ((ArcPuro) input.elementAt(i)).getTransition();
            pl = ((ArcPuro) input.elementAt(i)).getPlace();
            System.out.println("getTrans() = " + tr);
            System.out.println("getPlace() = " + pl);
            ArcsMatrix[tr][pl] = i;
            matrix[tr][pl] = -((ArcPuro) input.elementAt(i)).getPeso();
        }
        System.out.println("output.size() = " + output.size());
        for(int i=0; i<output.size(); i++) {
            tr = ((ArcPuro) output.elementAt(i)).getTransition();
            pl = ((ArcPuro) output.elementAt(i)).getPlace();
            System.out.println("OutPut[" + i + "] = " + i + "; getTrans() = " + tr + "getPlace() = " + pl);
            ArcsMatrix[tr][pl] = i;
            matrix[tr][pl] = ((ArcPuro) output.elementAt(i)).getPeso();
        }

        for(int i = 0; i < row; i++) {
            System.out.print("\n");
            for(int j = 0; j < col; j++) {
                if(matrix[i][j] == -1)
                    System.out.print("  " + matrix[i][j] + "  ");
                else
                    System.out.print("   " + matrix[i][j] + "  ");
            }
        }

        t = new Termination(matrix, row, col);

    } // Constructor end

    public IncidenceMatrix(int row, int col, Vector input, Vector output, Vector headNodes) {
        super();
        setBackground(Color.white);
        this.row = row;
        this.col = col;
        System.out.println("row = " + row);
        System.out.println("col = " + col);
        matrix = new int[row][col];
        ArcsMatrix = new int[row][col];
        inputArcs = new Vector();
        outputArcs = new Vector();
        for(int i=0; i<row; i++)
            for(int j=0; j<col; j++) {
                matrix[i][j] = 0;
                ArcsMatrix[i][j] = -1;
            }
        int pl, tr;
        System.out.println("input.size() = " + input.size());
        for(int i=0; i<input.size(); i++) {
            tr = ((ArcPuro) input.elementAt(i)).getTransition();
            pl = ((ArcPuro) input.elementAt(i)).getPlace();
            System.out.println("getTrans() = " + tr);
            System.out.println("getPlace() = " + pl);
            ArcsMatrix[tr][pl] = i;
            matrix[tr][pl] = -((ArcPuro) input.elementAt(i)).getPeso();
        }
        System.out.println("output.size() = " + output.size());
        for(int i=0; i<output.size(); i++) {
            tr = ((ArcPuro) output.elementAt(i)).getTransition();
            pl = ((ArcPuro) output.elementAt(i)).getPlace();
            System.out.println("OutPut[" + i + "] = " + i + "; getTrans() = " + tr + "getPlace() = " + pl);
            ArcsMatrix[tr][pl] = i;
            matrix[tr][pl] = ((ArcPuro) output.elementAt(i)).getPeso();
        }

        for(int i=0; i<row; i++) {
            System.out.print("\n");
            for(int j=0; j<col; j++) {
                if(matrix[i][j] == -1)
                    System.out.print("  " + matrix[i][j] + "  ");
                else System.out.print("   " + matrix[i][j] + "  ");
            }
        }

        //t = new Termination(matrix, row, col, headNodes);

    } // Constructor end

    public Vector getInputArcs() {
        return inputArcs;
    }

    public Vector getOutputArcs() {
        return outputArcs;
    }

    public int getCol() {
        return col;
    }

    public int getRow() {
        return row;
    }

    public int[][] getMatrix() {
        return matrix;
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);


        g.drawString("P   L   A   C   E   S", (col*30)/2, 15);
        int inic = (30*row)/2 - 15;
        char[] trans = {'T', 'R', 'A', 'N', 'S', 'I', 'T', 'I', 'O', 'N', 'S' };
        int p,q, y;
        for(p = (30 * row) / 2 - 15, q = 0; q < 11; q++, p += 12) {
            y = (q == 5 || q == 7) ? 17 : 15;
            g.drawString((new Character(trans[q])).toString(), y, p);
        }

        g.drawLine(50, 40, 80, 40);
        g.drawLine(50, (30*row)+40, 80, (30*row)+40);
        g.drawLine(50, 40, 50, (30*row)+40);

        g.drawLine((30*col)+20, 40, (30*col)+50, 40 );
        g.drawLine((30*col)+20, (30*row)+40, (30*col)+50, (30*row)+40);
        g.drawLine((30*col)+50, 40, (30*col)+50, (30*row)+40);

        for(int j=0; j<col; j++)
            g.drawString((new Integer(j)).toString(), (j+2)*30-3, 33);
        for(int i=0; i<row; i++)
            g.drawString((new Integer(i)).toString(), 33, (i+2)*30);

        for(int i = 0; i < row; i++) {
            for(int j = 0; j < col; j++) {
                if(matrix[i][j] == -1)
                    g.drawString((new Integer(matrix[i][j])).toString(), (j+2)*30-3, (i+2)*30);
                else
                    g.drawString((new Integer(matrix[i][j])).toString(), (j+2)*30, (i+2)*30);
            }
        }

        Vector paths = t.getPaths();
        Vector path;
        if(paths.size() > 0) {
            int posY = (row+3)*30;
            g.drawString("Cyclic Paths", 30, posY);
            g.setColor(Color.red);
            for(int i = 0; i < paths.size(); i++) {
                path = (Vector) paths.elementAt(i);
                String pathS = new String();
                Coordenada c1, c2;
                int j, x1, y1, x2, y2;
                for(j = 0; j < path.size() - 1; j++) {
                    c1 = (Coordenada) path.elementAt(j);
                    c2 = (Coordenada) path.elementAt(j+1);
                    pathS = pathS.concat (c1.toString() + " ");
                    if(c1.getX() <= c2.getX() && c1.getY() <= c2.getY()) {
                        x1 = c1.getX();
                        y1 = c1.getY();
                        x2 = c2.getX();
                        y2 = c2.getY();
                    } else {
                        x2 = c1.getX();
                        y2 = c1.getY();
                        x1 = c2.getX();
                        y1 = c2.getY();
                    }

                    g.drawRect((y1*30) + 55, (x1*30) + 46,(y2-y1)*30+15, (x2-x1)*30+20);
                    if(matrix[c1.getX()][c1.getY()] == -1)
                        inputArcs.add(new Integer(ArcsMatrix[c1.getX()][c1.getY()]));
                    else
                        outputArcs.add(new Integer(ArcsMatrix[c1.getX()][c1.getY()]));

                }
                c1 = (Coordenada) path.elementAt(j);
                pathS = pathS.concat (c1.toString() + " ");
                g.setColor(Color.black);
                g.drawString(pathS, 30, posY + (i+1)*20);
            }
        }

    }  // paintComponent end
}
