package framework.common;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UtilTool {

    private static String reg = "\\{(.+?)\\}"; // "\\$\\{(.+?)\\}";

    public static List<String> getParam(String rawString){
        List<String> list = new ArrayList<>() ;
        Pattern pattern = Pattern.compile(reg);
        Matcher mc= pattern.matcher(rawString);
        while(mc.find()){
            String param = mc.group(1);
            System.out.println(param);
            list.add(param);
        }
        return list;
    }


}
