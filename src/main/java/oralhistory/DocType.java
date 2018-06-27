
package oralhistory;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for docType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="docType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;choice maxOccurs="unbounded" minOccurs="0">
 *         &lt;element name="str" type="{}strType"/>
 *         &lt;element name="arr" type="{}arrType"/>
 *       &lt;/choice>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "docType", propOrder = {
    "strOrArr"
})
public class DocType {

    @XmlElements({
        @XmlElement(name = "str", type = StrType.class),
        @XmlElement(name = "arr", type = ArrType.class)
    })
    protected List<Object> strOrArr;

    /**
     * Gets the value of the strOrArr property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the strOrArr property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getStrOrArr().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link StrType }
     * {@link ArrType }
     * 
     * 
     */
    public List<Object> getStrOrArr() {
        if (strOrArr == null) {
            strOrArr = new ArrayList<Object>();
        }
        return this.strOrArr;
    }

}
