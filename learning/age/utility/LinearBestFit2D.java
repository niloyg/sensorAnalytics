package learning.age.utility;

import learning.age.utility.LinearBestFit;

public class LinearBestFit2D {
	LinearBestFit[] array;
	double[] z;
	
	public LinearBestFit2D (LinearBestFit[] array) {
		this.array = array;
	}
	
	public LinearBestFit2D (LinearBestFit[] array, double[] z) {
		this.array = array;
		this.z = z;
	}	
	
	public double getXwhileGivenYandZ(double y, int z) {
		return array[z].getXwhileGivenY(y);
	}

	public double getXwhileGivenYandZ(double y, double lookupVar) {
		LinearBestFit lbf = null;
		for (int i=0;i<this.z.length;i++) {
			if (lookupVar>= this.z[i] && lookupVar <= this.z[i+1]) {
				//Create an intermediate LinearBestFit Object
				//x[] should be identical as z[i] or z[i+1]
				double[] x = this.array[i].x;
				
				//y'[] elements should be looked up
				double deltaFraction = (lookupVar - this.z[i]) / (this.z[i+1] - this.z[i]);
				int lbfDataPoints = this.array[i].y.length;
				double[] yLookedUp = new double[lbfDataPoints];
				for (int j=0;j<lbfDataPoints;j++) {
					yLookedUp[j] = this.array[i].y[j] + deltaFraction * (this.array[i+1].y[j] - this.array[i].y[j]);
				}
				System.out.print("ith   lbfY is:"); for (int d=0;d<this.array[i].y.length;d++) System.out.print(String.format("%.3f, ",   this.array[i].y[d]));
				System.out.print("\n");
				System.out.print("New   lbfY is:"); for (int d=0;d<yLookedUp.length;      d++) System.out.print(String.format("%.3f, ",         yLookedUp[d]));
				System.out.print("\n");
				System.out.print("i+1th lbfY is:"); for (int d=0;d<this.array[i].y.length;d++) System.out.print(String.format("%.3f, ", this.array[i+1].y[d]));
				System.out.print("\n");
				
				try {
					lbf = new LinearBestFit(this.array[i].x, yLookedUp);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;
			}
		}
		if (lbf != null) return lbf.getXwhileGivenY(y);
		else return this.array[0].getXwhileGivenY(y);
	}	
	
	public static void main (String[] args) {
		LinearBestFit2D ldf2D_abusive = LinearBestFit2D.createLinearBestFit2D("ABUSIVE");
		System.out.println("\nfor Process=20V40B & 95%tile=0.9, the estimated pressure is "+ldf2D_abusive.getXwhileGivenYandZ(0.9, 0));
		System.out.println("\nfor Process=20V30B & 95%tile=0.9, the estimated pressure is "+ldf2D_abusive.getXwhileGivenYandZ(0.9, 0.667));
		System.out.println("\nfor Process=20V20B & 95%tile=0.9, the estimated pressure is "+ldf2D_abusive.getXwhileGivenYandZ(0.9, 4.800));
		System.out.println("\nfor Process=CONT_V & 95%tile=0.9, the estimated pressure is "+ldf2D_abusive.getXwhileGivenYandZ(0.9, 1));


	}
	
	public static LinearBestFit2D createLinearBestFit2D(String abusive) {
		LinearBestFit2D lbf2D = null;
		if (abusive.equals("ABUSIVE")) {
			LinearBestFit[] array = new LinearBestFit[2];
			
			double[]  x = new double[7];double[] y0 = new double[7];double[] y1 = new double[7];
			double[] z = new double[2];
			x[0]=0.05;	y1[0]=0.337;	y0[0]=0.246;
			x[1]=0.15;	y1[1]=0.345;	y0[1]=0.301;
			x[2]=0.30;	y1[2]=0.568;	y0[2]=0.390; 
			x[3]=0.45;	y1[3]=0.697;	y0[3]=0.291; 
			x[4]=0.60;	y1[4]=0.737;	y0[4]=0.296; 
			x[5]=0.75;	y1[5]=0.784;	y0[5]=0.352;
			x[6]=0.90;	y1[6]=1.244;	y0[6]=1.480;
					   z[1]=5.0;	   z[0]  =0.5;		
			try {
				array[0] = new LinearBestFit(x, y0);
				array[1] = new LinearBestFit(x, y1);
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			lbf2D = new LinearBestFit2D(array, z);
		}
		return lbf2D;
	}
}
