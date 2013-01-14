package tugstory;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.JPanel;

import manipulatives.AssetView;
import manipulatives.ManipInterface;
import extras.Debug;
import extras.GameImages;

public class TugUtils {
	public static void setDesiredAssetLocations(JPanel p, ArrayList<ManipInterface> mypeeps, ArrayList<ManipInterface> oppopeeps) {
		BufferedImage man = GameImages.getRandomCharacter();
		double block = p.getWidth()/2-p.getWidth()/8;
		int y = p.getHeight()/2;
		int myside = mypeeps.size();
		int opposide = oppopeeps.size();
		int mymanspace = Math.min(man.getWidth(), (int) (block / myside));
		int oppomanspace = Math.min(man.getWidth(), (int) (block / opposide));
		for(int i = 0; i < mypeeps.size(); i++) {
			AssetView manip = (AssetView) mypeeps.get(i);
			int x = i * mymanspace;
			Debug.printlnVerbose("setting my manip to " + x + ", " + y);
			manip.setDesiredLocation(x, y);
		}
		for(int i = 0; i < oppopeeps.size(); i++) {
			AssetView manip = (AssetView) oppopeeps.get(i);
			int x = (p.getWidth()-manip.getImage().getWidth())-((i)*oppomanspace);
			Debug.printlnVerbose("setting their manip to " + x + ", " + y) ;
			manip.setDesiredLocation(x, y);
//			BufferedImage otherMan = oppopeeps.get(i).getImage();
//			int manY = calcManY(oppopeeps.get(i), y);
//			if(oppopeeps.get(i).isUpright()) {
//				g.drawImage(otherMan, (getWidth()-man.getWidth())-((i)*oppomanspace), manY, null);
//			}else{
//				g.drawImage(otherMan, ((getWidth()+15)-(man.getWidth()))-((i)*oppomanspace), manY, null);
//			}
		}
	}
}
