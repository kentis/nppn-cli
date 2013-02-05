package org.k1s.nppn.att;

import static org.junit.Assert.*;

import org.junit.Test;
import org.k1s.cpn.nppn.att.ATTFactory;
import org.k1s.nppn.blocks.Principal;
import org.k1s.nppn.cpn.io.CpnIO


import static org.hamcrest.CoreMatchers.*
import static org.junit.matchers.JUnitMatchers.*

class ATTFactoryTests {
	
	@Test
	void testcreate(){
		def model = this.class.getResourceAsStream("/ProtocolModel.cpn")
		def io = new CpnIO()
		def cpn = io.readCPN(model)
		io.parsePragmatics(cpn)
		
		def att = ATTFactory.createATT(cpn, null, null)
		
		assertThat att, is(not(null))
		assertThat att.children.size(), is(2) 
	}
	
	@Test
	void testATTForPrincipal(){
		def model = this.class.getResourceAsStream("/ProtocolModel.cpn")
		def io = new CpnIO()
		def cpn = io.readCPN(model)
		io.parsePragmatics(cpn)
		
		def att = ATTFactory.createATT(cpn, null, null)
		
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
		
		def att = ATTFactory.createATT(cpn, null, null)
		
		assertThat att, is(not(null))
		assertThat att.children.size(), is(2)
		
		def sender = att.children[0]
		
		assertThat sender.children.size(), is(3)
		
	
	}
	
}
