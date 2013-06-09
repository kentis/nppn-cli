package org.k1s.petriCode.cpn.io

import org.cpntools.accesscpn.model.Page
import org.cpntools.accesscpn.model.PetriNet;
import org.cpntools.accesscpn.model.Transition;
import org.cpntools.accesscpn.model.Place;
import org.cpntools.accesscpn.model.Instance;
import org.cpntools.accesscpn.model.RefPlace

import org.cpntools.accesscpn.model.importer.DOMParser;
import org.cpntools.accesscpn.model.exporter.DOMGenerator;
import org.k1s.cpn.nppn.pragmatics.Pragmatics;
import org.k1s.petriCode.generation.CodeGenerator;

class CpnIO {
	
	/**
	 * Reads and parses a CPN model and its pragmatics.
	 * 
	 * @param model an InputStream that contains a CPN model
	 * @return PetriNet with pragmatics added
	 */
	def readAndParseCPN(model){
		def cpn = readCPN(model)
		parsePragmatics(cpn)
		return cpn
	}
	
	
	/**
	 * Reads and parses a CPN model and its pragmatics.
	 *
	 * @param model an InputStream that contains a CPN model
	 * @return PetriNet without explicit pragmatics added
	 */
	def readCPN(model){
		org.cpntools.accesscpn.model.PetriNet pn = null;
		
		try {
			pn = DOMParser.parse(model, "");
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * Parses the pragmatics in a CPN model
	 * 
	 * @param pn a PetriNet model
	 * @return void
	 */
	void parsePragmatics(PetriNet pn){
		Transition.metaClass.pragmatics = null
		Place.metaClass.pragmatics = null
		Instance.metaClass.pragmatics = null
		RefPlace.metaClass.pragmatics = null
		
		Transition.metaClass.cleanName = null
		Place.metaClass.cleanName = null
		Instance.metaClass.cleanName = null
		RefPlace.metaClass.cleanName = null
		
		pn.getPage().each{ parsePragmatics(it)}
		
	}
	
	/**
	 * Parses the pragmatics in a Page of a CPN model
	 * 
	 * @param page a Page 
	 * @return void
	 */
	void parsePragmatics(Page page){
		page.getObject().each{
			it.pragmatics = []
			getPrags(it.name.text).each { pragDef ->
				it.pragmatics << Pragmatics.parse(pragDef)
			}
			it.cleanName = CodeGenerator.nameToFilename(it.name.text)
		}
	}
	
	/**
	 * Gets the pragmatics from the name of an element
	 * 
	 * @param elemName
	 * @return List of pragmatics
	 */
	protected def getPrags(String elemName){
		
		def retval = []
		try{
		if(elemName == null) return retval;
		//String retval = null;
		def start = 0
		
		while(elemName.indexOf("<<", start) > 0 ) {
		
			int pragStart = elemName.indexOf("<<", start) + 2;
			int pragEnd = elemName.indexOf(">>", start);
			
			String pragDef = elemName.substring(pragStart, pragEnd);
			
			if(!pragDef.contains("(")) pragDef = pragDef + "()";
			retval << pragDef
			start = pragEnd +1
		}
		return retval;
		}catch(Exception ex){
			throw new Exception("Unnable to parse $elemName",ex)
		}
		
	}
	
	/**
	 * Serializes a PetriNet to XML 
	 * 
	 * @param cpn
	 * @return String The serialized version of the parameter.
	 */
	def cpnToXml(PetriNet cpn){
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		
		DOMGenerator.export(cpn, bos)
		
		return bos.toString()
	}
}
