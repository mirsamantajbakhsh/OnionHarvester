package OnionHarvester;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Mir Saman Tajbakhsh
 */
public class CharMapper {

    private static Map<String, String> nextToken = new HashMap<>();

    static {
        nextToken.put("a", "b");
        nextToken.put("b", "c");
        nextToken.put("c", "d");
        nextToken.put("d", "e");
        nextToken.put("e", "f");
        nextToken.put("f", "g");
        nextToken.put("g", "h");
        nextToken.put("h", "i");
        nextToken.put("i", "j");
        nextToken.put("j", "k");
        nextToken.put("k", "l");
        nextToken.put("l", "m");
        nextToken.put("m", "n");
        nextToken.put("n", "o");
        nextToken.put("o", "p");
        nextToken.put("p", "q");
        nextToken.put("q", "r");
        nextToken.put("r", "s");
        nextToken.put("s", "t");
        nextToken.put("t", "u");
        nextToken.put("u", "v");
        nextToken.put("v", "w");
        nextToken.put("w", "x");
        nextToken.put("x", "y");
        nextToken.put("y", "z");
        //nextToken.put("z", "0");
        //nextToken.put("0", "1");
        //nextToken.put("1", "2");
        nextToken.put("z", "2");
        nextToken.put("2", "3");
        nextToken.put("3", "4");
        nextToken.put("4", "5");
        nextToken.put("5", "6");
        nextToken.put("6", "7");
        nextToken.put("7", "a");
        //nextToken.put("7", "8");
        //nextToken.put("8", "9");
        //nextToken.put("9", "a");
    }

    public static String getNextToken(String currentToken) {
        int position = currentToken.length() - 1;
        String res = currentToken.substring(0, position);
        String lastChar = currentToken.substring(position);
        res += nextToken.get(lastChar);

        while (lastChar.equalsIgnoreCase("9") && position > 0) { //Has carry
            lastChar = res.substring(--position, position + 1);
            res = replace(res, position, nextToken.get(lastChar).charAt(0));
        }
        
        if (position == 0) {
            return null;
        } else {
            return res;
        }

    }

    private static String replace(String str, int index, char replace) {
        if (str == null) {
            return str;
        } else if (index < 0 || index >= str.length()) {
            return str;
        }
        char[] chars = str.toCharArray();
        chars[index] = replace;
        return String.valueOf(chars);
    }

}
