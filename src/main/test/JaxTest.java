import oralhistory.ObjectFactory;

import javax.xml.bind.JAXB;
import java.io.File;

public class JaxTest {
    public static void main(String[] args) {
        ObjectFactory factory = JAXB.unmarshal(new File("oralhistory.2017-06-08.xml"), ObjectFactory.class);
        System.out.println(factory);
    }
}
