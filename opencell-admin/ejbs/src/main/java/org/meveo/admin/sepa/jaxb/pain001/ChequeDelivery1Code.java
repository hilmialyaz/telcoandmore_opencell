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
 * <p>Java class for ChequeDelivery1Code.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="ChequeDelivery1Code"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="MLDB"/&gt;
 *     &lt;enumeration value="MLCD"/&gt;
 *     &lt;enumeration value="MLFA"/&gt;
 *     &lt;enumeration value="CRDB"/&gt;
 *     &lt;enumeration value="CRCD"/&gt;
 *     &lt;enumeration value="CRFA"/&gt;
 *     &lt;enumeration value="PUDB"/&gt;
 *     &lt;enumeration value="PUCD"/&gt;
 *     &lt;enumeration value="PUFA"/&gt;
 *     &lt;enumeration value="RGDB"/&gt;
 *     &lt;enumeration value="RGCD"/&gt;
 *     &lt;enumeration value="RGFA"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "ChequeDelivery1Code")
@XmlEnum
public enum ChequeDelivery1Code {

    MLDB,
    MLCD,
    MLFA,
    CRDB,
    CRCD,
    CRFA,
    PUDB,
    PUCD,
    PUFA,
    RGDB,
    RGCD,
    RGFA;

    public String value() {
        return name();
    }

    public static ChequeDelivery1Code fromValue(String v) {
        return valueOf(v);
    }

}
