package org.k1s.nppn.cpn.io

import org.cpntools.accesscpn.model.Page
import org.cpntools.accesscpn.model.PetriNet;
import org.cpntools.accesscpn.model.Transition;
import org.cpntools.accesscpn.model.Place;
import org.cpntools.accesscpn.model.Instance;

import org.cpntools.accesscpn.model.importer.DOMParser;
import org.cpntools.accesscpn.model.exporter.DOMGenerator;

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
		Transition.metaClass.pragmatics = []
		Place.metaClass.pragmatics = []
		Instance.metaClass.pragmatics = []
		
		pn.getPage().each{ parsePragmatics(it)}
		
	}
	
	def parsePragmatics(Page page){
		page.getObject().each{
			
		}
	}
	
	def cpnToXml(PetriNet cpn){
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		
		DOMGenerator.export(cpn, bos)
		
		return bos.toString()
	}
}
