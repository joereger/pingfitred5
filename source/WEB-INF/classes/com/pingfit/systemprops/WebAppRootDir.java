package com.pingfit.systemprops;

import javax.servlet.ServletConfig;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

/**
 * Holds this context's installation point
 */
public class WebAppRootDir {

    private static String root;
    private static String uniqueContextId;


    public WebAppRootDir(javax.servlet.http.HttpServletRequest request){
         setUniqueContextId(request.getSession().getServletContext());
         root = request.getSession().getServletContext().getRealPath("/");
    }

    public WebAppRootDir(ServletConfig config){
        setUniqueContextId(config.getServletContext());
        root = config.getServletContext().getRealPath("/");
    }

    public WebAppRootDir(javax.servlet.ServletContext context){
        setUniqueContextId(context);
        root = context.getRealPath("/");    
    }

    private static void setUniqueContextId(javax.servlet.ServletContext context){
        System.out.println("---------------");
        System.out.println("Looking for uniqueContextId.");
        String pathSeparator = System.getProperty("file.separator");
        if (pathSeparator.equals("\\")){
            pathSeparator = "\\\\";
        }
        String realPath = context.getRealPath(pathSeparator);
        String[] realPathParts = realPath.split(pathSeparator);
        for (int i = 0; i < realPathParts.length; i++) {
            java.lang.String realPathPart = realPathParts[i];
            System.out.println("realPathPart["+i+"]="+realPathPart);
        }
        String uniqueEngineName = realPathParts[realPathParts.length-1];
        System.out.println("uniqueEngineName="+uniqueEngineName);
        if (realPath.indexOf("tmp")>-1){
            //Likely jBoss - tmp26195ROOT-exp.war
            System.out.println("Looks like we're running on jBoss.");
            Pattern p = Pattern.compile("tmp[0-9]+([a-zA-Z0-9_]+)-exp.war");
            Matcher m = p.matcher(uniqueEngineName);
            m.find();
            if(m.matches()){
                System.out.println("Found a match, m.group(1)="+m.group(1));
                uniqueEngineName = m.group(1);
            } else {
                System.out.println("No match found so defaulting to ROOT.");
                uniqueEngineName = "ROOT";
            }
        }

        //Append everything else
        for(int i=realPathParts.length-2; i>=0; i=i-1){
            if (realPathParts[i]!=null){
                System.out.println("Appending realPathParts["+i+"]="+realPathParts[i]);
                uniqueEngineName = realPathParts[i].replaceAll(":", "") + "_" + uniqueEngineName;
            }
        }

        System.out.println("Setting uniqueContextId="+uniqueEngineName);
        System.out.println("---------------");

        uniqueContextId = uniqueEngineName;
    }

    public static String getWebAppRootPath(){
        return root;
    }

    public static String getUniqueContextId() {
        return uniqueContextId;
    }

}
