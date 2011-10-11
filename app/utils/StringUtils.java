package utils;
import java.util.*;

public abstract class StringUtils {

    private static final int MIN = 192;
    private static final int MAX = 255;
    private static final List<String> map = new ArrayList<String>();

    static {
        String car  = null;
        car = "A";
        map.add( car );            /* '\u00C0'   À   alt-0192  */ 
        map.add( car );            /* '\u00C1'   Á   alt-0193  */
        map.add( car );            /* '\u00C2'   Â   alt-0194  */
        map.add( car );            /* '\u00C3'   Ã   alt-0195  */
        map.add( car );            /* '\u00C4'   Ä   alt-0196  */
        map.add( car );            /* '\u00C5'   Å   alt-0197  */
        car = "AE";
        map.add( car );            /* '\u00C6'   Æ   alt-0198  */
        car = "C";
        map.add( car );            /* '\u00C7'   Ç   alt-0199  */
        car = "E";
        map.add( car );            /* '\u00C8'   È   alt-0200  */
        map.add( car );            /* '\u00C9'   É   alt-0201  */
        map.add( car );            /* '\u00CA'   Ê   alt-0202  */
        map.add( car );            /* '\u00CB'   Ë   alt-0203  */
        car = "I";
        map.add( car );            /* '\u00CC'   Ì   alt-0204  */
        map.add( car );            /* '\u00CD'   Í   alt-0205  */
        map.add( car );            /* '\u00CE'   Î   alt-0206  */
        map.add( car );            /* '\u00CF'   Ï   alt-0207  */
        car = "D";
        map.add( car );            /* '\u00D0'   Ð   alt-0208  */
        car = "N";
        map.add( car );            /* '\u00D1'   Ñ   alt-0209  */
        car = "O";
        map.add( car );            /* '\u00D2'   Ò   alt-0210  */
        map.add( car );            /* '\u00D3'   Ó   alt-0211  */
        map.add( car );            /* '\u00D4'   Ô   alt-0212  */
        map.add( car );            /* '\u00D5'   Õ   alt-0213  */
        map.add( car );            /* '\u00D6'   Ö   alt-0214  */
        car = "*";
        map.add( car );            /* '\u00D7'   ×   alt-0215  */
        car = "0";
        map.add( car );            /* '\u00D8'   Ø   alt-0216  */
        car = "U";
        map.add( car );            /* '\u00D9'   Ù   alt-0217  */
        map.add( car );            /* '\u00DA'   Ú   alt-0218  */
        map.add( car );            /* '\u00DB'   Û   alt-0219  */
        map.add( car );            /* '\u00DC'   Ü   alt-0220  */
        car = "Y";
        map.add( car );            /* '\u00DD'   Ý   alt-0221  */
        car = "Þ";
        map.add( car );            /* '\u00DE'   Þ   alt-0222  */
        car = "B";
        map.add( car );            /* '\u00DF'   ß   alt-0223  */
        car = "a";
        map.add( car );            /* '\u00E0'   à   alt-0224  */
        map.add( car );            /* '\u00E1'   á   alt-0225  */
        map.add( car );            /* '\u00E2'   â   alt-0226  */
        map.add( car );            /* '\u00E3'   ã   alt-0227  */
        map.add( car );            /* '\u00E4'   ä   alt-0228  */
        map.add( car );            /* '\u00E5'   å   alt-0229  */
        car = "ae";
        map.add( car );            /* '\u00E6'   æ   alt-0230  */
        car = "c";
        map.add( car );            /* '\u00E7'   ç   alt-0231  */
        car = "e";
        map.add( car );            /* '\u00E8'   è   alt-0232  */
        map.add( car );            /* '\u00E9'   é   alt-0233  */
        map.add( car );            /* '\u00EA'   ê   alt-0234  */
        map.add( car );            /* '\u00EB'   ë   alt-0235  */
        car = "i";
        map.add( car );            /* '\u00EC'   ì   alt-0236  */
        map.add( car );            /* '\u00ED'   í   alt-0237  */
        map.add( car );            /* '\u00EE'   î   alt-0238  */
        map.add( car );            /* '\u00EF'   ï   alt-0239  */
        car = "d";
        map.add( car );            /* '\u00F0'   ð   alt-0240  */
        car = "n";
        map.add( car );            /* '\u00F1'   ñ   alt-0241  */
        car = "o";
        map.add( car );            /* '\u00F2'   ò   alt-0242  */
        map.add( car );            /* '\u00F3'   ó   alt-0243  */
        map.add( car );            /* '\u00F4'   ô   alt-0244  */
        map.add( car );            /* '\u00F5'   õ   alt-0245  */
        map.add( car );            /* '\u00F6'   ö   alt-0246  */
        car = "/";
        map.add( car );            /* '\u00F7'   ÷   alt-0247  */
        car = "0";
        map.add( car );            /* '\u00F8'   ø   alt-0248  */
        car = "u";
        map.add( car );            /* '\u00F9'   ù   alt-0249  */
        map.add( car );            /* '\u00FA'   ú   alt-0250  */
        map.add( car );            /* '\u00FB'   û   alt-0251  */
        map.add( car );            /* '\u00FC'   ü   alt-0252  */
        car = "y";
        map.add( car );            /* '\u00FD'   ý   alt-0253  */
        car = "þ";
        map.add( car );            /* '\u00FE'   þ   alt-0254  */
        car = "y";
        map.add( car );            /* '\u00FF'   ÿ   alt-0255  */
        map.add( car );            /* '\u00FF'       alt-0255  */
    }

    public static String noAccent(String chaine) {
        StringBuffer Result = new StringBuffer(chaine);

        for(int bcl = 0 ; bcl < Result.length() ; bcl++) {
            int carVal = chaine.charAt(bcl);
            if( carVal >= MIN && carVal <= MAX ) {
                String newVal = map.get( carVal - MIN );
                Result.replace(bcl, bcl+1,newVal);
            }   
        }
        return Result.toString();
    }
}
