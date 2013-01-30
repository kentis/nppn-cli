package org.k1s.nppn.cpn.io;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*
import static org.junit.matchers.JUnitMatchers.*


import org.junit.Test;

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
		
	}
}
