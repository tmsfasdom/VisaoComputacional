package br.com.tmsfasdom.deteccaoplacas;

import java.util.Comparator;

/**
 * Created by 40276655893 on 18/12/2015.
 */
public class PossivelCharComparator implements Comparator<PossivelChar> {
    @Override
    public int compare(PossivelChar possibleCharLeft, PossivelChar t1) {
        if(possibleCharLeft.getIntCenterX() > t1.getIntCenterX()){
            return 1;
        } else if(possibleCharLeft.getIntCenterX() < t1.getIntCenterX()){
            return -1;
        }
        return 0;
    }
}
