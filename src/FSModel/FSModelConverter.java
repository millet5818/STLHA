package FSModel;

import FSModel.GeotiffParameters;

import org.gdal.gdal.Dataset;
import org.gdal.gdal.Driver;
import org.gdal.gdal.gdal;
import org.gdal.gdal.Band;
import org.gdal.gdalconst.gdalconst;
import org.gdal.gdalconst.gdalconstConstants;

import java.io.File;

public class FSModelConverter
{
	private static GeotiffParameters mGeotiffParameters;
	
	public static float[][] GetMatrixFromValue(float value, int rows, int cols)
	{
		//
		float[][] matrix = new float [rows][cols];
		
		//
		for (int i = 0; i < rows; ++i) 
		{
			for (int j = 0; j < cols; ++j) 
			{
				matrix[i][j] = value; 
			}
		}
		
		//
		return matrix;
	}
	
	public static float[][] GetRasterValuesFromFilePath(String tiffFilePath)
	{
		System.out.println("Info: Get Raster Values From File Path: " + tiffFilePath + "...");
		
		gdal.AllRegister();

		Dataset ds = gdal.Open(tiffFilePath, gdalconstConstants.GA_ReadOnly);
		if (ds == null)
		{
			System.err.println("ERR: GDALOpen failed - " + gdal.GetLastErrorNo());
			System.err.println(gdal.GetLastErrorMsg());
			System.exit(1);
		}

		Band band = ds.GetRasterBand(1);
		
		Driver hDriver = ds.GetDriver();
		//System.out.println("Driver: " + hDriver.getShortName() + "/" + hDriver.getLongName());
		int width = ds.getRasterXSize();
		int height = ds.getRasterYSize();
		//System.out.println("Size is " + width + ", " + height);
		
		float[][] resultArray = new float[height][width];
        float[] buffer = new float[width * height];

        band.ReadRaster(0, 0, width, height,gdalconst.GDT_Float32, buffer);
        for (int i = 0; i < buffer.length; i++)
        	resultArray[i/width][i%width] = buffer[i];

		ds.delete();
		gdal.GDALDestroyDriverManager();
		
		return resultArray;
	}
	
    public static Boolean CreateRasterToFilePath(String geotiffOutputFilePath, float[][] resultArray)
	{
		System.out.println("Info: Create Raster Data(.tif file) To File Path: " + geotiffOutputFilePath + "...");
		
		// if file exists, delete it.
		File file = new File(geotiffOutputFilePath);
		if(file.exists())
		{
			file.delete();
		}
		
		int countColumn = mGeotiffParameters.xSize;  //322		
		int countRow = mGeotiffParameters.ySize;     //562
		int bandCount = mGeotiffParameters.bandCount;
		int dataType = mGeotiffParameters.dataType;
		String rasterFormat = mGeotiffParameters.rasterFormat;
		
		gdal.AllRegister();
		
		Driver driver = gdal.GetDriverByName(rasterFormat);
		Dataset result = driver.Create(geotiffOutputFilePath, countColumn, countRow, bandCount, dataType);
		
		//set attributes
		result.SetGeoTransform(mGeotiffParameters.transform);
		result.SetProjection(mGeotiffParameters.projection);
		
        float[] tmpData = new float[countColumn * countRow];
        float tmp;
        for (int i = 0; i < countRow; ++i)
        {
            for (int j = 0; j < countColumn; ++j)
            {
                tmp = resultArray[i][j];
                tmpData[i * countColumn + j] = tmp;
            }
        }
        result.GetRasterBand(1).SetNoDataValue(mGeotiffParameters.noValue[0]);
        // The value of xoff and yoff indicates the location that the upper left corner of the pixel block is to write.
        int check = result.GetRasterBand(1).WriteRaster(0, 0, countColumn, countRow, dataType, tmpData);

        if (check == gdalconst.CE_Failure)
            assert false:"write tmp geotiff failed!";
        else
        	return true;
        
        return false;
	}
	
	public static GeotiffParameters GetGeotiffParameters(String tiffFilePath)
	{
		GeotiffParameters geotiffParameters = new GeotiffParameters();
		
		gdal.AllRegister();
		Dataset ds = gdal.Open(tiffFilePath, gdalconstConstants.GA_ReadOnly);
		if (ds == null)
		{
			System.err.println("ERR: GDALOpen failed - " + gdal.GetLastErrorNo());
			System.err.println(gdal.GetLastErrorMsg());
			System.exit(1);
		}
		
		geotiffParameters.projection = ds.GetProjection();
		ds.GetGeoTransform(geotiffParameters.transform);
		
		Driver hDriver = ds.GetDriver();
		//System.out.println("Driver: " + hDriver.getShortName() + "/" + hDriver.getLongName());
		
		geotiffParameters.rasterFormat = hDriver.getShortName();
        // columns(xSize) and rows(ySize) of rasters
		geotiffParameters.xSize     = ds.getRasterXSize();
		geotiffParameters.ySize     = ds.getRasterYSize();  
		geotiffParameters.bandCount = ds.getRasterCount();
		geotiffParameters.dataType = ds.GetRasterBand(1).getDataType();
		ds.GetRasterBand(1).GetNoDataValue(geotiffParameters.noValue);
		//System.out.println(geotiffParameters.noValue[0]);//-3.4028234663852886E38

		return geotiffParameters;
	}
	
	public static void setGeotiffParameters(GeotiffParameters geotiffPara)
	{
		mGeotiffParameters = geotiffPara;
	}
}
