import ij.plugin.PlugIn;
import ij.*;
import ij.gui.*;
import ij.process.*;

/**
 * This plugin implements multi scale mexican hat filter (Lindeberg 1998),
 * the plugin is based on Mexican Hat Filter plugin by 	Dimiter Prodanov   
 *    
 * @author Gherardo Varando gherardo.varando@gmail.com
 *
*/
public class Multiscale_Mexican_Hat implements PlugIn {

    private int deep=1,minR=2,maxR=10;
	private int height,width,nSlices;
    private int step=2;
	private ImagePlus im;

	@Override
	public void run(String arg0) {
		
		  
        im=WindowManager.getCurrentImage();
	        
	    if (im==null){
	       	IJ.error("You need to open an image first.");
	       	return;
	    }
	        
	    if (showDialog()){    
	    	deep = (int) Math.max(Math.floor((maxR-minR)/step),1);
			width=  im.getWidth();
			height= im.getHeight();
			nSlices=im.getNSlices();
			float[][][][] pixels=new float[deep][nSlices][width][height];
        	float[][][] result=new float[nSlices][width][height];
        	ImageProcessor ip;
    		Mexican_Hat_Simple mh=new Mexican_Hat_Simple();
        	for (int i=0; i<deep; i++){
        		mh.setRadious(minR+i*step);
        		IJ.showStatus("LoGs computations...radious="+(minR+i*step));
        		IJ.showProgress(i, deep);
            	for (int sl=1;sl<=nSlices;sl++){
            		ip=im.getStack().getProcessor(sl).duplicate().convertToFloat();
            		mh.run(ip.convertToFloat());
					pixels[i][sl-1]=ip.getFloatArray(); 			    
				}
			}
			for (int sl=0;sl<nSlices;sl++){
				IJ.showStatus("Final filter...");
				IJ.showProgress(sl, nSlices);
				for (int x=0;x<width;x++){
					for (int y=0;y<height;y++){
				 	for (int d=0;d<(deep);d++){        		
					 	//if (pixels[d][sl][x][y]>=pixels[d+1][sl][x][y] || pixels[d][sl][x][y]>=pixels[d-1][sl][x][y]){
		        				result[sl][x][y]=Math.max(result[sl][x][y], pixels[d][sl][x][y]);
		        		//	}
		        		
				 	}
			 	}
			}
			}
			ImagePlus resultIm=IJ.createImage("MaxLoGs_minR"+minR+"_maxR"+maxR+"_step"+step, width, height, nSlices, 32);
			for (int sl=0;sl<nSlices;sl++){
				resultIm.setSlice(sl+1);
				for (int x=0;x<width;x++){
					for (int y=0;y<height;y++){
					resultIm.getProcessor().putPixelValue(x, y, result[sl][x][y]);
					}
			}
			}
			
			resultIm.show();
	    }
	}
	
	
	


	public boolean showDialog() {
		GenericDialog gd=new GenericDialog("Multiscale Mexican Hat Filter");
		gd.addMessage("The min R and max R parameter are the radious of the filter window, not the radious of the blobs detected, for that apply sqrt(2)(2*r+1)/6"  );
		gd.addNumericField("Min R:", minR, 1);
		gd.addNumericField("Max R",maxR,1);
		gd.addNumericField("step",step,1);
		gd.showDialog();
		if (gd.wasCanceled()) return false;
		minR = (int) gd.getNextNumber();
		maxR = (int) gd.getNextNumber();
		step = (int) gd.getNextNumber();
        return true;
	}








}