//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.3 in JDK 1.6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2018.09.25 at 02:01:12 PM WET 
//


package org.meveo.admin.sepa.jaxb.pain001;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ChequeType2Code.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="ChequeType2Code"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="CCHQ"/&gt;
 *     &lt;enumeration value="CCCH"/&gt;
 *     &lt;enumeration value="BCHQ"/&gt;
 *     &lt;enumeration value="DRFT"/&gt;
 *     &lt;enumeration value="ELDR"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "ChequeType2Code")
@XmlEnum
public enum ChequeType2Code {

    CCHQ,
    CCCH,
    BCHQ,
    DRFT,
    ELDR;

    public String value() {
        return name();
    }

    public static ChequeType2Code fromValue(String v) {
        return valueOf(v);
    }

}
