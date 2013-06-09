package org.k1s.nppn.att;

import static org.junit.Assert.*;

import org.junit.Test;
import org.k1s.petriCode.att.ATTFactory;
import org.k1s.cpn.nppn.pragmatics.PrgamaticsDescriptorDSL;
import org.k1s.petriCode.blocks.Principal;
import org.k1s.petriCode.blocks.derived.PragmaticsDerivator;
import org.k1s.petriCode.cpn.io.CpnIO


import static org.hamcrest.CoreMatchers.*

import static org.junit.matchers.JUnitMatchers.*

class ATTFactoryTests {
	
	@Test
	void testcreate(){
		def model = this.class.getResourceAsStream("/ProtocolModel.cpn")
		def io = new CpnIO()
		def cpn = io.readCPN(model)
		io.parsePragmatics(cpn)
		
		def factory = new ATTFactory(getCorePragmatics())
		def att = factory.createATT(cpn, null, null)
		
		println att
		assertThat att, is(not(null))
		assertThat att.children.size(), is(2) 
	}
	
	@Test
	void testATTForPrincipal(){
		def model = this.class.getResourceAsStream("/ProtocolModel.cpn")
		def io = new CpnIO()
		def cpn = io.readCPN(model)
		io.parsePragmatics(cpn)
		
		def factory = new ATTFactory(getCorePragmatics())
		def att = factory.createATT(cpn, null, null)
		
		assertThat att, is(not(null))
		assertThat att.children.size(), is(2)
		
		att.children.each {
			assertThat it, is(not(null))
			assertTrue it instanceof Principal
		}
	
	}
	
	@Test
	void testATTForServices(){
		def model = this.class.getResourceAsStream("/ProtocolModel.cpn")
		def io = new CpnIO()
		def cpn = io.readCPN(model)
		io.parsePragmatics(cpn)
		
		def factory = new ATTFactory(getCorePragmatics())
		def att = factory.createATT(cpn, null, null)
		
		assertThat att, is(not(null))
		assertThat att.children.size(), is(2)
		
		def sender = att.children[0]
		
		assertThat sender.children.size(), is(3)
		
	
	}
	
	@Test
	void testATTSettinCFPragmatics(){
		
		def prags = getCorePragmatics()
		
		def factory = new ATTFactory(prags)
		assertThat factory.controlFlowPragmatics.size(), is(3)
		assertTrue factory.controlFlowPragmatics.containsAll('Id', 'startLoop', 'branch')
	}
	
	
	static def getCorePragmatics(){
		def desc = new PrgamaticsDescriptorDSL()
		def string = this.class.getResourceAsStream("/core.prags")
		desc.build(string.text)
		return desc.prags
	}
	
	
	@Test
	void testATTForSenderSend(){
		def model = this.class.getResourceAsStream("/ProtocolModel.cpn")
		def io = new CpnIO()
		def cpn = io.readCPN(model)
		io.parsePragmatics(cpn)
		
		PragmaticsDerivator.addDerivedPragmatics(cpn, getCorePragmatics())
		
		def factory = new ATTFactory(getCorePragmatics())
		
		def att = factory.createATT(cpn, null, null)
		
		assertThat att, is(not(null))
		assertThat att.children.size(), is(2)
		
		def sender = att.children[0]
	
		def senderSend = sender.children[1]
		
		assertThat senderSend, is(not(null))
		assertThat senderSend.start_node, is(not(null))
		
		assertThat senderSend.children, is(not(null))
		//println "senderSend.children.size() ${senderSend.children.size()}"
		assertTrue senderSend.children.size() >= 3	
	}
	
	
	@Test
	void testATTForBranch(){
		def model = this.class.getResourceAsStream("/simplechoice.cpn")
		def io = new CpnIO()
		def cpn = io.readCPN(model)
		io.parsePragmatics(cpn)
		
		PragmaticsDerivator.addDerivedPragmatics(cpn, getCorePragmatics())
		
		
		def service = cpn.page[2]
		
		def names = service.object.pragmatics.name.flatten()
		println names
		
		assertTrue names.contains("branch")
		assertTrue names.contains("merge")
		assertFalse names.contains("startLoop")
	}
}
