package org.k1s.petriCode.att

import com.thoughtworks.xstream.XStream
/**
 * Provides basic serialization for ATTs using XStream
 * @author kent
 *
 */
class ATTIO {
	
	/**
	 * Reads an ATT from XML
	 * 
	 * @param xml
	 * @return
	 */
	static def readATT(xml){
		XStream xstream = new XStream()
		def att = xstream.fromXML(xml)
		return att
	}
	
	/**
	 * Produces the serialized XML for an ATT
	 * @param att
	 * @return
	 */
	static def serializeATT(att){
		XStream xstream = new XStream()
		String xml = xstream.toXML(att)
		return xml
	}
	
	/**
	 * Writes a serialized version of an ATT to the given file
	 * @param att
	 * @param file
	 * @return
	 */
	static def writeATT(att, File file){
		XStream xstream = new XStream()
		String xml = xstream.toXML(att, new FileOutputStream(file))
		return xml
	}
	
	static def writeATTImage(att, filename){
		new ATTVisualizer().writeATTImage(att, filename)
		
	}
}
