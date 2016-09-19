import ij.plugin.filter.*;
import ij.*;
import ij.gui.*;
import ij.process.*;

import java.awt.*;
import java.awt.event.*;
import java.util.*;

import ij.plugin.filter.GaussianBlur;

/**
 * This plugin implements multi scale mexican hat filter (Lindeberg 1998),
 * the plugin is based on Mexican Hat Filter plugin by 	Dimiter Prodanov   
 *    
 * @author Gherardo Varando gherardo.varando@gmail.com
 *
*/
public class Mexican_Hat_Simple implements ExtendedPlugInFilter, DialogListener{
	private PlugInFilterRunner pfr=null;
	private int flags=DOES_ALL+CONVERT_TO_FLOAT+SNAPSHOT;
    public int r;
    public float[] kernel;
    public boolean kernComputed=false;

	
    
	@Override
	public int setup(String arg, ImagePlus imp) {
		if (arg!=""){
			this.r=Integer.parseInt(arg);
			return flags+DOES_ALL;
		}
		return flags;
	}

	@Override
	public void run(ImageProcessor ip) {
		
		computeKernel2D();
		Convolver con=new Convolver();
		con.setNormalize(true);
		con.convolveFloat(ip, kernel, 2*r+1 , 2*r+1);        
		
		//double sigma=(double)(r) /3.0 + (double)(1.0/6.0);
		//sigma=sigma*sigma;
		//ip.multiply(-sigma);
	}
	
	public void computeKernel2D() {
		if (kernComputed){
			return;
		}
		double sigma2;
		double PIs;
		double x2;
		int idx;
		float[] k;
		int sz=2*(r)+1;
		k=new float[sz*sz];
		sigma2=((double)(r)/3.0+1/6)*((double)(r)/3.0 +1/6.0);
		PIs=1/(2*Math.PI*sigma2*sigma2);
		for (int u=-r; u<=r; u++) {
			for (int w=-r; w<=r; w++) {
				x2=u*u+w*w;
				idx=u+r + sz*(w+r);
				k[idx]=(float)((x2 - 2*sigma2)*Math.exp(-x2/(2*sigma2))*PIs);
			}
		}
	this.kernComputed=true;
    this.kernel = k;
	}
	
	public void setRadious(int r){
		if (r!=this.r){
		this.r=r;
		this.kernComputed=false;
		}
		return;
	}
	
	
    @Override
	public int showDialog(ImagePlus imp, String command, PlugInFilterRunner pfr) {
		this.pfr = pfr;
		GenericDialog gd=new GenericDialog("Simple Mexican Hat Filter");
		gd.addNumericField("Radius:", r, 1);
		gd.addDialogListener(this);
		gd.showDialog();
		if (gd.wasCanceled())
			return DONE;
        int flags2 = IJ.setupDialog(imp, flags);  // if stack, ask whether to process all slices
        return flags2;
	}

	@Override
	public boolean dialogItemChanged(GenericDialog gd, AWTEvent arg1) {
		
		if (gd.wasCanceled())
			return false;
		setRadious((int)(gd.getNextNumber()));
		return r>0;
	}

	@Override
	public void setNPasses(int arg0) {
		// TODO Auto-generated method stub
		
	}




}