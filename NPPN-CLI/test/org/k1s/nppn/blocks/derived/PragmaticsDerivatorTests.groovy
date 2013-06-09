package org.k1s.nppn.blocks.derived;

import static org.junit.Assert.*;

import org.junit.Test;
import org.k1s.cpn.nppn.pragmatics.PragmaticsDescriptor;
import org.k1s.cpn.nppn.pragmatics.PrgamaticsDescriptorDSL;


import org.k1s.petriCode.blocks.derived.PragmaticsDerivator;
import org.k1s.petriCode.cpn.io.CpnIO

import static org.hamcrest.CoreMatchers.*
import static org.junit.matchers.JUnitMatchers.*
class PragmaticsDerivatorTests {
	
	
	@Test
	void testGetServicePages(){
		def model = this.class.getResourceAsStream("/ProtocolModel.cpn")
		def io = new CpnIO()
		def cpn = io.readCPN(model)
		io.parsePragmatics(cpn)
		
		def services  = PragmaticsDerivator.getServicePages(cpn)
		
		assertThat services.size(), is(6)
		assertFalse services.name.text.contains("Principal")
	}
	
	@Test
	void testderriveLoops(){
		/*def model = this.class.getResourceAsStream("/ProtocolModel.cpn")
		def io = new CpnIO()
		def cpn = io.readCPN(model)
		io.parsePragmatics(cpn)
		*/
		
		def model = this.class.getResourceAsStream("/ProtocolModel.cpn")
		def io = new CpnIO()
		def cpn = io.readCPN(model)
		io.parsePragmatics(cpn)
		
		def services  = PragmaticsDerivator.getServicePages(cpn)
		def senderSend = services.findAll{ it.name.text.contains("Send")}[1]
		
		def pragsStr = this.class.getResourceAsStream("/core.prags")
		def prags = new PrgamaticsDescriptorDSL()
		prags.build(pragsStr.text)
		println "prags: $prags.prags"
		
		PragmaticsDerivator.addDerivedPragmatics(cpn, prags.prags)
		println senderSend.name
		println senderSend.object.pragmatics.name
		assertTrue senderSend.object.pragmatics.name.flatten().contains("startLoop")
		assertTrue senderSend.object.pragmatics.name.flatten().contains("endLoop")
		
		
	}
}
