package be.phury.boilerplate.lang;

/**
 * String utilities
 */
public class Strings {


    /**
     * Joins the elements in the given array with the provided separator string
     * @param toJoin the array to join
     * @param separator the string to join with
     * @param <T> the type of the array
     * @return a String
     */
    public static <T> String join(T[] toJoin, String separator) {
        String str = "";
        for (T t : toJoin) {
            str += str.equals("") ? t.toString() : separator + t.toString();
        }
        return str;
    }

    public static boolean isNotEmpty(String str) {
        return !(str == null || str == "");
    }
}
