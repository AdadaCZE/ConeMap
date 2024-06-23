package pointDraw;

public class Cone {
	
	public int x;
    public int y;
    public int baseR;
    public int tipR;
    public float z;
    public float h;
    public float s;
    public ColorId id;

    public Cone(int _x, int _y, int _br, int _tr, float _z, ColorId _id) {
        x = _x;
        y = _y;
        baseR = _br;
        tipR = _tr;
        z = _z;
        id = _id;
    }

}
