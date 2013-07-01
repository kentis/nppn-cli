package org.k1s.petriCode.cpn.io;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*
import static org.junit.matchers.JUnitMatchers.*


import org.cpntools.accesscpn.model.Place
import org.junit.Test;
import org.k1s.petriCode.cpn.io.CpnIO
import org.k1s.petriCode.generation.CodeGeneratorTests;

class CpnIOTests {
	
	@Test
	void testReadCPNModel(){
		def model = this.class.getResourceAsStream("/ProtocolModel.cpn")

		assert model, is(not(null))
		
		def io = new CpnIO()
		
		def cpn = io.readCPN(model)
		
		assertTrue(cpn instanceof org.cpntools.accesscpn.model.PetriNet)
	}
	

	
	@Test
	void testWriteCPNModel(){
		def model = this.class.getResourceAsStream("/ProtocolModel.cpn")
		
		assert model, is(not(null))
				
		def io = new CpnIO()
		
		def cpn = io.readCPN(model)
		
		
		def cpnStr = io.cpnToXml(cpn)
		
		assertThat cpnStr, containsString("<workspaceElements>")
		assertThat cpnStr, containsString("<cpnet>")
				
	}
		
	@Test
	void testParsePragmatics(){
		def model = this.class.getResourceAsStream("/ProtocolModel.cpn")
		
		def pragmaticsDescriptor = CodeGeneratorTests.getPragmaticsDesciptors()
		def io = new CpnIO(pragmaticsDescriptor)
		
		def cpn = io.readCPN(model)
		
		io.parsePragmatics(cpn)
		
		cpn.getPage()[0].getObject().each {
			if(it instanceof Place){
				
				assertThat it.pragmatics.size(), is(0)
			} else {
				assertThat it.pragmatics.size(), is(1)
				//println it.pragmatics[0].name 
				assertTrue it.pragmatics[0].name = "Principal" || it.pragmatics[0].name == "channel"
			}
		}
	}
	
}
