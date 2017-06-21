package org.jboss.tools.fuse.reddeer.dataformat;

import java.util.Arrays;
import java.util.List;

public enum DataFormat {

	AVRO("avro", "org.apache.camel", "camel-avro"),
	BARCODE("barcode", "org.apache.camel", "camel-barcode"),
	BASE64("base64", "org.apache.camel", "camel-base64"),
	BEANIO("beanio", "org.apache.camel", "camel-beanio"),
	BINDY_CSV("bindy-csv", "org.apache.camel", "camel-bindy"),
	BINDY_FIXED("bindy-fixed", "org.apache.camel", "camel-bindy"),
	BINDY_KVP("bindy-kvp", "org.apache.camel", "camel-bindy"),
	BOON("boon", "org.apache.camel", "camel-boon"),
	CASTOR("castor", "org.apache.camel", "camel-castor"),
	CRYPTO("crypto", "org.apache.camel", "camel-crypto"),
	CSV("csv", "org.apache.camel", "camel-csv"),
	FLATPACK("flatpack", "org.apache.camel", "camel-flatpack"),
	GZIP("gzip", "org.apache.camel", "camel-core"),
	HESSIAN("hessian", "org.apache.camel", "camel-hessian"),
	HL7("hl7", "org.apache.camel", "camel-hl7"),
	ICAL("ical", "org.apache.camel", "camel-ical"),
	JACKSONXML("jacksonxml", "org.apache.camel", "camel-jacksonxml"),
	JAXB("jaxb", "org.apache.camel", "camel-jaxb"),
	JIBX("jibx", "org.apache.camel", "camel-jibx"),
	JSON("json", "org.apache.camel", "camel-johnzon"),
	JSON_GSON("json-gson", "org.apache.camel", "camel-gson"),
	JSON_JACKSON("json-jackson", "org.apache.camel", "camel-jackson"),
	JSON_JOHNZON("json-johnzon", "org.apache.camel", "camel-johnzon"),
	JSON_XSTREAM("json-xstream", "org.apache.camel", "camel-xstream"),
	LZF("lzf", "org.apache.camel", "camel-lzf"),
	MIME_MULTIPART("mime-multipart", "org.apache.camel", "camel-mail"),
	PGP("pgp", "org.apache.camel", "camel-crypto"),
	PROTOBUF("protobuf", "org.apache.camel", "camel-protobuf"),
	RSS("rss", "org.apache.camel", "camel-rss"),
	SECUREXML("secureXML", "org.apache.camel", "camel-xmlsecurity"),
	SERIALIZATION("serialization", "org.apache.camel", "camel-core"),
	SOAPJAXB("soapjaxb", "org.apache.camel", "camel-soap"),
	STRING("string", "org.apache.camel", "camel-core"),
	SYSLOG("syslog", "org.apache.camel", "camel-syslog"),
	TARFILE("tarfile", "org.apache.camel", "camel-tarfile"),
	TIDYMARKUP("tidyMarkup", "org.apache.camel", "camel-tagsoup"),
	UNIVOCITY_CSV("univocity-csv", "org.apache.camel", "camel-univocity-parsers"),
	UNIVOCITY_FIXED("univocity-fixed", "org.apache.camel", "camel-univocity-parsers"),
	UNIVOCITY_TSV("univocity-tsv", "org.apache.camel", "camel-univocity-parsers"),
	XMLBEANS("xmlBeans", "org.apache.camel", "camel-xmlbeans"),
	XMLJSON("xmljson", "org.apache.camel", "camel-xmljson"),
	XMLRPC("xmlrpc", "org.apache.camel", "camel-xmlrpc"),
	XSTREAM("xstream", "org.apache.camel", "camel-xstream"),
	YAML_SNAKEYAML("yaml-snakeyaml", "org.apache.camel", "camel-snakeyaml"),
	ZIPFILE("zipfile", "org.apache.camel", "camel-zipfile"),
	ZIP("zip", "org.apache.camel", "camel-core");

	public static List<DataFormat> getDataFormats() {
		return Arrays.asList(new DataFormat[] {
			AVRO,
			BASE64,
			BEANIO,
			BOON,
			CASTOR,
			CRYPTO,
			CSV,
			FLATPACK,
			GZIP,
			HL7,
			ICAL,
			JACKSONXML,
			JAXB,
			JIBX,
			JSON,
			PGP,
			PROTOBUF,
			RSS,
			SECUREXML,
			SERIALIZATION,
			SOAPJAXB,
			STRING,
			SYSLOG,
			TARFILE,
			TIDYMARKUP,
			UNIVOCITY_CSV,
			UNIVOCITY_FIXED,
			UNIVOCITY_TSV,
			XMLBEANS,
			XMLJSON,
			XMLRPC,
			ZIPFILE,
			ZIP });
	}

	private String name;
	private String groupId;
	private String artifactId;

	private DataFormat(String name, String groupId, String artifactId) {
		this.name = name;
		this.groupId = groupId;
		this.artifactId = artifactId;
	}

	public String getName() {
		return name;
	}

	public String getGroupId() {
		return groupId;
	}

	public String getArtifactId() {
		return artifactId;
	}

}
