package FSModel;

/**
 * This class is utilized to manage two parameters of terrain.
 * Angle of Slope:theta
 * Specific Catchment Area:a
 * */

public class TerrainParameters {
	
	// Angle of Slope
	private float[][] theta;
	//Specific Catchment Area
	private float[][] a;
	//the size of DEM(Digital Elevation Model)
	private int rows;
	private int cols;

	public TerrainParameters(float[][] theta, float[][] a, int rows, int cols) {
		this.theta = theta;
		this.a = a;
		this.rows = rows;
		this.cols = cols;
	}
	
	public float[][] getAngleofSlope() {
		return theta;
	}
	
	public float[][] geSpecificCatchmentArea() {
		return a;
	}
	
	public int getRows() {
		return rows;
	}
	
	public int geCols() {
		return cols;
	}
	
	@Override
	public String toString() {
		return ("{ Angle of Slope: float[][] theta:" + ",Specific Catchment Area:float[][] a }");
	}
}