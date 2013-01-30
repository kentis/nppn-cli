package org.k1s.nppn.cpn.io

import org.cpntools.accesscpn.model.Page
import org.cpntools.accesscpn.model.PetriNet;
import org.cpntools.accesscpn.model.Transition;
import org.cpntools.accesscpn.model.Place;
import org.cpntools.accesscpn.model.Instance;
import org.cpntools.accesscpn.model.RefPlace

import org.cpntools.accesscpn.model.importer.DOMParser;
import org.cpntools.accesscpn.model.exporter.DOMGenerator;
import org.k1s.cpn.nppn.pragmatics.Pragmatics;

class CpnIO {
	
	
	def readCPN(model){
		org.cpntools.accesscpn.model.PetriNet pn = null;
		
		try {
			pn = DOMParser.parse(model, "");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
				throw new RuntimeException(e);
		}
	}
	
	
	def parsePragmatics(PetriNet pn){
		Transition.metaClass.pragmatics = null
		Place.metaClass.pragmatics = null
		Instance.metaClass.pragmatics = null
		RefPlace.metaClass.pragmatics = null
		
		pn.getPage().each{ parsePragmatics(it)}
		
	}
	
	def parsePragmatics(Page page){
		page.getObject().each{
			it.pragmatics = []
			getPrags(it.name.text).each { pragDef ->
				it.pragmatics << Pragmatics.parse(pragDef)
			}
			
		}
	}
	
	
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
	
	def cpnToXml(PetriNet cpn){
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		
		DOMGenerator.export(cpn, bos)
		
		return bos.toString()
	}
}
