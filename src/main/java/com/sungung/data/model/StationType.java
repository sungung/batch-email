
package com.sungung.data.model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;


/**
 * <p>Java class for stationType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="stationType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="town" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="website-url" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="stationname" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="latitude" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="longitude" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="talkback-number" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="enquiries-number" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="fax-number" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="sms-number" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="street-number" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="street-suburb" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="street-postcode" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="po-box" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="po-suburb" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="po-postcode" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
/*
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "stationType", propOrder = {
    "town",
    "websiteUrl",
    "stationname",
    "latitude",
    "longitude",
    "talkbackNumber",
    "enquiriesNumber",
    "faxNumber",
    "smsNumber",
    "streetNumber",
    "streetSuburb",
    "streetPostcode",
    "poBox",
    "poSuburb",
    "poPostcode"
})
*/
@XmlRootElement(name = "station")
public class StationType {

    private String town;

    private String websiteUrl;

    private String stationname;

    private String latitude;

    private String longitude;

    private String talkbackNumber;

    private String enquiriesNumber;

    private String faxNumber;

    private String smsNumber;

    private String streetNumber;

    private String streetSuburb;

    private String streetPostcode;

    private String poBox;

    private String poSuburb;

    private String poPostcode;

    @XmlElement(required = true)
    public String getTown() {
        return town;
    }

    public void setTown(String town) {
        this.town = town;
    }

    @XmlElement(name = "website-url", required = true)
    public String getWebsiteUrl() {
        return websiteUrl;
    }

    public void setWebsiteUrl(String websiteUrl) {
        this.websiteUrl = websiteUrl;
    }

    @XmlElement(required = true)
    public String getStationname() {
        return stationname;
    }

    public void setStationname(String stationname) {
        this.stationname = stationname;
    }

    @XmlElement(required = true)
    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    @XmlElement(required = true)
    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    @XmlElement(name = "talkback-number", required = true)
    public String getTalkbackNumber() {
        return talkbackNumber;
    }

    public void setTalkbackNumber(String talkbackNumber) {
        this.talkbackNumber = talkbackNumber;
    }

    @XmlElement(name = "enquiries-number", required = true)
    public String getEnquiriesNumber() {
        return enquiriesNumber;
    }

    public void setEnquiriesNumber(String enquiriesNumber) {
        this.enquiriesNumber = enquiriesNumber;
    }

    @XmlElement(name = "fax-number", required = true)
    public String getFaxNumber() {
        return faxNumber;
    }

    public void setFaxNumber(String faxNumber) {
        this.faxNumber = faxNumber;
    }

    @XmlElement(name = "sms-number", required = true)
    public String getSmsNumber() {
        return smsNumber;
    }

    public void setSmsNumber(String smsNumber) {
        this.smsNumber = smsNumber;
    }

    @XmlElement(name = "street-number", required = true)
    public String getStreetNumber() {
        return streetNumber;
    }

    public void setStreetNumber(String streetNumber) {
        this.streetNumber = streetNumber;
    }

    @XmlElement(name = "street-suburb", required = true)
    public String getStreetSuburb() {
        return streetSuburb;
    }

    public void setStreetSuburb(String streetSuburb) {
        this.streetSuburb = streetSuburb;
    }

    @XmlElement(name = "street-postcode", required = true)
    public String getStreetPostcode() {
        return streetPostcode;
    }

    public void setStreetPostcode(String streetPostcode) {
        this.streetPostcode = streetPostcode;
    }

    @XmlElement(name = "po-box", required = true)
    public String getPoBox() {
        return poBox;
    }

    public void setPoBox(String poBox) {
        this.poBox = poBox;
    }

    @XmlElement(name = "po-suburb", required = true)
    public String getPoSuburb() {
        return poSuburb;
    }

    public void setPoSuburb(String poSuburb) {
        this.poSuburb = poSuburb;
    }

    @XmlElement(name = "po-postcode", required = true)
    public String getPoPostcode() {
        return poPostcode;
    }

    public void setPoPostcode(String poPostcode) {
        this.poPostcode = poPostcode;
    }
}
