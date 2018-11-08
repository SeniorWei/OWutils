package com;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;

public class utils {

    public static JdzwOW_NEW getConnection() throws IOException{

    String tcurl="";

    String tcuserid="";

    String tcuserpassword="";

    Properties prop =new Properties();

        InputStream in = utils.class.getClassLoader().getResourceAsStream("root.properties");

       //FileInputStream in=new FileInputStream("C:/OWroot.properties");

        prop.load(in);

        Set<Object> keyValue =prop.keySet();



        for(Iterator<Object> it = keyValue.iterator(); it.hasNext();){

            String key=(String) it.next();

        if(key.equals("host")){

            tcurl=(String)prop.get(key);

        }else if(key.equals("user")){

        tcuserid=(String)prop.get(key);

            }else if(key.equals("password")){

        tcuserpassword=(String)prop.get(key);


            }

        }


        prop.clear();
        in.close();

            return new JdzwOW_NEW(tcuserid ,tcuserpassword ,tcurl);


    }

}
