package FSModel;
import FSModel.FSModelConverter;
import java.lang.Math;

/**
 * This class aims to analyze the stability of slope in a certain area. There
 * are three major functions in this model (i.e. Factor of Safety Model,
 * FSModel): 1). Calculating the terrain parameters, such as the Angle of
 * Slope(theta) and Specific Catchment Area(a); 2). Calculating the hydrological
 * distribution parameter, such as saturation factor(omega); 3). Calcultaing the
 * factor of slope stability (FS).
 * 
 * These algorithms are developed by Langping Li.
 * 
 * @author john
 * @version 1.1.0
 * @since 2020-12-27
 */

public class FSModelCore {
	
	// attributes
	// the acceleration of gravity (m/s^2)
	private float mg = 9.80665f;
	// the water density (kg/m^3)
	private float mrw = 1000f;

	//
	public GeotiffParameters mGeotiffParameters;

	
	/**
	 ** Hydrological Distribution Calculating Model
	 * 
	 * Formula: q = sinTheta/a*T; T =K*h
	 * 
	 * @return the saturation EffectivePrecipitation, q (m/s)
	 * @param K     The permeability coefficient of landslide mass(soil body) (m/s)
	 * @param h     The thickness of landslide mass(soil body) (m).
	 * @param theta The angle of slope
	 * @param a     The specific catchment area (m)
	 * @param rows  The rows of a certain area
	 * @param cols  The columns of a certain area
	 */
	
	public float[][] calculateSaturationEffectivePrecipitation(float[][] K, float[][] h, 
			float[][] theta, float[][] a, int rows, int cols)
	{
		//
		float[][] eps = new float[rows][cols];
		
		//
		for (int i = 0; i < rows; ++i) 
		{
			for (int j = 0; j < cols; ++j) 
			{
				//
				if(K[i][j] == mGeotiffParameters.noValue[0].floatValue() ||
				   h[i][j] == mGeotiffParameters.noValue[0].floatValue() ||
				   theta[i][j] == mGeotiffParameters.noValue[0].floatValue() ||
			       a[i][j] == mGeotiffParameters.noValue[0].floatValue())
				{
					//
					eps[i][j] = mGeotiffParameters.noValue[0].floatValue();
				}
				else if (a[i][j] == 0)
				{
					//
					eps[i][j] = -1;
				}
				else 
				{
					//
					float theta_rad = theta[i][j] * (float)Math.PI / 180;
					float sinTheta = (float) Math.sin(theta_rad);
					//
					// T is the saturation coefficient of hydraulic conductivity
					float T = K[i][j] * h[i][j];
					//
					eps[i][j] = sinTheta / a[i][j] * T;
				}
			}
		}
		
		//
		return eps;
	}

	
	/**
	 * Hydrological Distribution Calculating Model
	 * 
	 * Formula: omega = (q/T)(a/sinTheta); T =K*h
	 * 
	 * @return the saturation factor omega
	 * @param q     The effective rainfall (m/s)
	 * @param K     The permeability coefficient of landslide mass(soil body) (m/s)
	 * @param h     The thickness of landslide mass(soil body) (m).
	 * @param theta The angle of slope
	 * @param a     The specific catchment area (m)
	 * @param rows  The rows of a certain area
	 * @param cols  The columns of a certain area
	 */
	
	public float[][] calculateSaturationFactor(float[][] q, float[][] K, float[][] h, 
			float[][] theta, float[][] a, int rows, int cols)
	{
		//
		float[][] omega = new float[rows][cols];
		
		//
		for (int i = 0; i < rows; ++i) 
		{
			for (int j = 0; j < cols; ++j) 
			{
				//
				if(q[i][j] == mGeotiffParameters.noValue[0].floatValue() ||
				   K[i][j] == mGeotiffParameters.noValue[0].floatValue() ||
				   h[i][j] == mGeotiffParameters.noValue[0].floatValue() ||
				   theta[i][j] == mGeotiffParameters.noValue[0].floatValue() ||
			       a[i][j] == mGeotiffParameters.noValue[0].floatValue())
				{
					//
					omega[i][j] = mGeotiffParameters.noValue[0].floatValue();
				}
				else if (h[i][j] == 0 || theta[i][j] == 0)
				{
					//
					omega[i][j] = 1;
				}
				else 
				{
					//
					float theta_rad = theta[i][j] * (float)Math.PI / 180;
					float sinTheta = (float) Math.sin(theta_rad);
					//
					// T is the saturation coefficient of hydraulic conductivity
					float T = K[i][j] * h[i][j];
					//
					omega[i][j] = Math.min((q[i][j] / T) * (a[i][j] / sinTheta), 1);
				}
			}
		}
		
		//
		return omega;
	}
	
	
	/**
	 * Factor of Slope Stability Calculating Model
	 * 
	 * Formula: FS = (c/(rs*g*h)+(1-omega*(rw/rs))*cos(theta)*tan(phi))/((1+omega*(rw/rs))*sin(theta))
	 * 
	 * @return the saturation factor omega
	 * @param c  	The coefficient of cohesion of landslide mass(soil body) (Pa (N/m^2))
	 * @param rs 	The density of landslide mass(soil body) (kg/m^3)
	 * @param h 	The thickness of landslide mass(soil body) (m)
	 * @param omega	The saturation factor
	 * @param theta The angle of slope (radian)
	 * @param phi   The frictional angle 
	 * @param rows  The rows of a certain area
	 * @param cols  The columns of a certain area
	 */
	
	public float[][] calculateFactorOfSlopeStability(float[][] c, float[][] rs, float[][] h, 
			float[][] omega, float[][] theta, float[][] phi, 
			int rows, int cols) 
	{
		//
		float[][] FS = new float[rows][cols];
		
		//
		for (int i = 0; i < rows; ++i) 
		{
			for (int j = 0; j < cols; ++j) 
			{
				//
				if(c[i][j] == mGeotiffParameters.noValue[0].floatValue() ||
				   rs[i][j] == mGeotiffParameters.noValue[0].floatValue() ||
				   h[i][j] == mGeotiffParameters.noValue[0].floatValue() ||
				   omega[i][j] == mGeotiffParameters.noValue[0].floatValue() ||
				   theta[i][j] == mGeotiffParameters.noValue[0].floatValue() ||
			       phi[i][j] == mGeotiffParameters.noValue[0].floatValue())
				{
					//
					FS[i][j] = mGeotiffParameters.noValue[0].floatValue();
				}
				else if (h[i][j] == 0 || theta[i][j] == 0)
				{
					//
					FS[i][j] = -1;
				}
				else
				{
					//
					float c1 = c[i][j]/(rs[i][j]*mg*h[i][j]);
					float gamma = mrw/rs[i][j];
					//
					float theta_rad = theta[i][j] * (float)Math.PI / 180;
					float cosTheta = (float) Math.cos(theta_rad);
					float sinTheta = (float) Math.sin(theta_rad);
					//
					float phi_rad = phi[i][j] * (float)Math.PI / 180;
					float tanPhi = (float) Math.tan(phi_rad);
					//
					FS[i][j] = (c1 + (1-omega[i][j]*gamma) * cosTheta * tanPhi) / ((1+omega[i][j]*gamma) * sinTheta);
				}
			}
		}
		
		//
		return FS;
	}
	

	public void intializeGeotiffParameter(String tiffFilePath)
	{
		mGeotiffParameters = FSModelConverter.GetGeotiffParameters(tiffFilePath);
		FSModelConverter.setGeotiffParameters(mGeotiffParameters);
	}

}
