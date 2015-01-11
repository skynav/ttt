//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.10 in JDK 6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2015.01.11 at 12:26:26 PM MST 
//


package com.skynav.ttv.model.ttml2.ttd;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ruby.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="ruby">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}token">
 *     &lt;enumeration value="none"/>
 *     &lt;enumeration value="container"/>
 *     &lt;enumeration value="base"/>
 *     &lt;enumeration value="baseContainer"/>
 *     &lt;enumeration value="text"/>
 *     &lt;enumeration value="textContainer"/>
 *     &lt;enumeration value="delimiter"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "ruby", namespace = "http://www.w3.org/ns/ttml#datatype")
@XmlEnum
public enum Ruby {

    @XmlEnumValue("none")
    NONE("none"),
    @XmlEnumValue("container")
    CONTAINER("container"),
    @XmlEnumValue("base")
    BASE("base"),
    @XmlEnumValue("baseContainer")
    BASE_CONTAINER("baseContainer"),
    @XmlEnumValue("text")
    TEXT("text"),
    @XmlEnumValue("textContainer")
    TEXT_CONTAINER("textContainer"),
    @XmlEnumValue("delimiter")
    DELIMITER("delimiter");
    private final String value;

    Ruby(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static Ruby fromValue(String v) {
        for (Ruby c: Ruby.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
