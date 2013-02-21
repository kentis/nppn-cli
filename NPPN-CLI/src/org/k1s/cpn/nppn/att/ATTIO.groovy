package org.k1s.cpn.nppn.att

import com.thoughtworks.xstream.XStream

class ATTIO {
	
	static def readATT(xml){
		XStream xstream = new XStream()
		def att = xstream.fromXML(xml)
		return att
	}
	
	static def serializeATT(att){
		XStream xstream = new XStream()
		String xml = xstream.toXML(att)
		return xml
	}
	
	static def writeATT(att, File file){
		XStream xstream = new XStream()
		String xml = xstream.toXML(att, new FileOutputStream(file))
		return xml
	}
	
}
