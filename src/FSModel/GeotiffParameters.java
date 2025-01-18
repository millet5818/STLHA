package FSModel;

public class GeotiffParameters
{
	// ATTENTION: Assume all geotiff files share the same parameters as blow.
	public double[] transform= new double[6];
    public String projection;
    public String rasterFormat;
    public int xSize;
    public int ySize;
    public int bandCount;
    public int dataType; 						//from the members of class gdalconst 
    public Double[] noValue = new Double[1]; 	// ATTENTION: Assume all geotiff files only have one band.
}
