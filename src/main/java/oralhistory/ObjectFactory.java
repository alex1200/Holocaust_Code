
package oralhistory;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the oralhistory package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _Response_QNAME = new QName("", "response");
    private final static QName _ArrTypeStr_QNAME = new QName("", "str");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: oralhistory
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link ResponseType }
     * 
     */
    public ResponseType createResponseType() {
        return new ResponseType();
    }

    /**
     * Create an instance of {@link StrType }
     * 
     */
    public StrType createStrType() {
        return new StrType();
    }

    /**
     * Create an instance of {@link DocType }
     * 
     */
    public DocType createDocType() {
        return new DocType();
    }

    /**
     * Create an instance of {@link ArrType }
     * 
     */
    public ArrType createArrType() {
        return new ArrType();
    }

    /**
     * Create an instance of {@link ResultType }
     * 
     */
    public ResultType createResultType() {
        return new ResultType();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ResponseType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "response")
    public JAXBElement<ResponseType> createResponse(ResponseType value) {
        return new JAXBElement<ResponseType>(_Response_QNAME, ResponseType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "str", scope = ArrType.class)
    public JAXBElement<String> createArrTypeStr(String value) {
        return new JAXBElement<String>(_ArrTypeStr_QNAME, String.class, ArrType.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "str", scope = StrType.class)
    public JAXBElement<String> createStrTypeStr(String value) {
        return new JAXBElement<String>(_ArrTypeStr_QNAME, String.class, StrType.class, value);
    }

}
