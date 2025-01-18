package FSModelTest;
import FSModel.FSModelCore;
import FSModel.FSModelConverter;
import FSModel.TerrainParameters;

import java.io.IOException;

public class FSModelTest {

	public static void main(String ards[]) throws IOException {

		//
		// Boolean result = false;
		
		// Test parameters
//		float q = 0.015f; 	//unit: m/hr 	有效降雨量（q） 4.17*10^-6 m/s
//		float rs = 1350f; 	//unit: kg/m^3 	土体密度（ρs）
//		float h = 2f;		//unit: m 		土体厚度（h）
//		float c = 1500f; 	//unit: pa 		土体粘聚系数（c）
//		float phi = 25f; 	//unit:	°degree 土体摩擦角（φ）
//		float K = 1.05f; 	//unit:	m/hr 	土体渗透系数（K） 2.92*10^-4 m/s
//		float rw = 1000f; 	//unit:	kg/m^3 	水密度（ρw）
		
		//
		FSModelCore fsModelCore = new FSModelCore();
		
		
		
		//
		String strSlopeAngle = ".\\data\\input\\etutm47n_hengduan_cs1000_f05_SRTMGL1_chn_clip_patch_utm_1000m_slp_Mask.tif";  
		String strSpecificCatchmentArea = ".\\data\\input\\f05_srtmgl1_chn_clip_patch_utm_1000mFilSCA_mask.tif";
		Object objEffectivePrecipitation = ".\\data\\cumulativeRainfall\\20210626.tif";
		Object objSoilThickness = ".\\data\\input\\dtb_china_100_hengduan_setnull_float_utm0090m_1000m_mask.tif";
		String filename="20210626";
		Object objSoilDensity = 1350f;
		Object objSoilCohesionCoefficient = 1500f;
		Object objSoilFrictionAngle = 25f;
		Object objSoilHydraulicConductivity = 1000f;
		// Object objSoilHydraulicConductivity = 8f;
		// Object objSoilHydraulicConductivity = 150f;
		
		
		//
		fsModelCore.intializeGeotiffParameter(strSlopeAngle);
		int rows = fsModelCore.mGeotiffParameters.ySize;
		int cols = fsModelCore.mGeotiffParameters.xSize;
		
		//
		float[][] theta = FSModelConverter.GetRasterValuesFromFilePath(strSlopeAngle);		
		float[][] a = FSModelConverter.GetRasterValuesFromFilePath(strSpecificCatchmentArea);		
		
		//
		float[][] q = null; 
		if (objEffectivePrecipitation instanceof String) 
		{
			q = FSModelConverter.GetRasterValuesFromFilePath(objEffectivePrecipitation.toString());
        }
		if (objEffectivePrecipitation instanceof Float) 
		{ 
			q = FSModelConverter.GetMatrixFromValue(Float.parseFloat(objEffectivePrecipitation.toString()), rows, cols);
	    }

		//
		float[][] rs = null; 
		if (objSoilDensity instanceof String) 
		{
			rs = FSModelConverter.GetRasterValuesFromFilePath(objSoilDensity.toString());
        }
		if (objSoilDensity instanceof Float) 
		{ 
			rs = FSModelConverter.GetMatrixFromValue(Float.parseFloat(objSoilDensity.toString()), rows, cols);
	    }

		//
		float[][] h = null; 
		if (objSoilThickness instanceof String) 
		{
			h = FSModelConverter.GetRasterValuesFromFilePath(objSoilThickness.toString());
        }
		if (objSoilThickness instanceof Float) 
		{ 
			h = FSModelConverter.GetMatrixFromValue(Float.parseFloat(objSoilThickness.toString()), rows, cols);
	    }

		//
		float[][] c = null; 
		if (objSoilCohesionCoefficient instanceof String) 
		{
			c = FSModelConverter.GetRasterValuesFromFilePath(objSoilCohesionCoefficient.toString());
        }
		if (objSoilCohesionCoefficient instanceof Float) 
		{ 
			c = FSModelConverter.GetMatrixFromValue(Float.parseFloat(objSoilCohesionCoefficient.toString()), rows, cols);
	    }

		//
		float[][] phi = null; 
		if (objSoilFrictionAngle instanceof String) 
		{
			phi = FSModelConverter.GetRasterValuesFromFilePath(objSoilFrictionAngle.toString());
        }
		if (objSoilFrictionAngle instanceof Float) 
		{ 
			phi = FSModelConverter.GetMatrixFromValue(Float.parseFloat(objSoilFrictionAngle.toString()), rows, cols);
	    }

		//
		float[][] K = null; 
		if (objSoilHydraulicConductivity instanceof String) 
		{
			K = FSModelConverter.GetRasterValuesFromFilePath(objSoilHydraulicConductivity.toString());
        }
		if (objSoilHydraulicConductivity instanceof Float) 
		{ 
			K = FSModelConverter.GetMatrixFromValue(Float.parseFloat(objSoilHydraulicConductivity.toString()), rows, cols);
	    }
		
		
		
		// the targets waiting for calculating, defined by users.
		String saturationEffectivePrecipitationTifFilePath = ".\\data\\output\\"+filename+"\\heng_1000m_shc"+Float.toString(K[1][1])+"_eps.tif";  
		// String saturationFactorTifFilePath = ".\\data\\heng2\\heng_1000m_shc"+Float.toString(K[1][1])+"_ep"+Float.toString(q[1][1])+"_omega.tif"; 
		// String factorOfSlopeStabilityTifFilePath = ".\\data\\heng2\\heng_1000m_shc"+Float.toString(K[1][1])+"_ep"+Float.toString(q[1][1])+"_FS.tif"; 
		String saturationFactorTifFilePath = ".\\data\\output\\"+filename+"\\heng_1000m_shc"+Float.toString(K[1][1])+"_ep"+Integer.toString(20062022)+"_omega.tif"; 
		String factorOfSlopeStabilityTifFilePath = ".\\data\\output\\"+filename+"\\heng_1000m_shc"+Float.toString(K[1][1])+"_ep"+Integer.toString(20062022)+"_FS.tif";
		//
		String factorOfSlopeStabilitySaturationTifFilePath = ".\\data\\output\\"+filename+"\\heng_1000m_omega"+Integer.toString(1)+"_FS.tif";
		
		
		
		
		
		
		
		// Calculating the saturation EffectivePrecipitation
		float[][] eps = fsModelCore.calculateSaturationEffectivePrecipitation(K, h, theta, a, rows, cols);
		//
		Boolean result_eps = FSModelConverter.CreateRasterToFilePath(saturationEffectivePrecipitationTifFilePath, eps);

	    // Calculating the saturation factor
		float[][] omega = fsModelCore.calculateSaturationFactor(q, K, h, theta, a, rows, cols);
		//
		Boolean result_omega = FSModelConverter.CreateRasterToFilePath(saturationFactorTifFilePath, omega);

		// Calculating the factor of slope stability
		float[][] FS = fsModelCore.calculateFactorOfSlopeStability(c, rs, h, omega, theta, phi, rows, cols);
		//
		Boolean result_FS = FSModelConverter.CreateRasterToFilePath(factorOfSlopeStabilityTifFilePath, FS);

		// get omega = 1
		float[][] omega1 = K = FSModelConverter.GetMatrixFromValue(1, rows, cols);
		// Calculating the factor of slope stability, for totally saturation (omega = 1)
		float[][] FS_omega1 = fsModelCore.calculateFactorOfSlopeStability(c, rs, h, omega1, theta, phi, rows, cols);
		//
		Boolean result_FS_omega1 = FSModelConverter.CreateRasterToFilePath(factorOfSlopeStabilitySaturationTifFilePath, FS_omega1);
		
		
		
		//
		if (result_eps && result_omega && result_FS && result_FS_omega1)
		{
			System.out.println("Info: Done!");
		} 
		else
		{
			System.out.println("Err: Something wrong when invoking CreateRasterToFilePath!");
		}
				
	}
}
