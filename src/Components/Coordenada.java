package Components;

/**
 * Created by serch on 19/01/15.
 */
public class Coordenada {
    private int x, y;

    public Coordenada(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public String toString() {
        return ("(" + x + "," + y + ")");
    }

    public boolean equals(Coordenada c) {
        if (this.x == c.x && this.y == c.y)
            return true;
        return false;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

}
