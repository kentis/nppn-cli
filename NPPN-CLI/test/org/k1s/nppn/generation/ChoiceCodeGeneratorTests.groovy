package org.k1s.nppn.generation;

import static org.junit.Assert.*;

import org.junit.Test;
import org.k1s.cpn.nppn.att.ATTFactory
import org.k1s.nppn.cpn.io.CpnIO

class ChoiceCodeGeneratorTests {

	
	@Test
	void testGeneratechoice(){
		def model = this.class.getResourceAsStream("/simplechoice.cpn")
		def io = new CpnIO()
		def cpn = io.readCPN(model)
		io.parsePragmatics(cpn)
		
		PragmaticsDerivator.addDerivedPragmatics(cpn, ATTFactoryTests.getCorePragmatics())
		
		def factory = new ATTFactory(ATTFactoryTests.getCorePragmatics())
		
		def att = factory.createATT(cpn, null, null)
		
		def bindings = getGroovyBindings()
		
		def generator = new CodeGenerator(att, bindings)
		
		def file = generator.generate()
		
		println file
		
		assertThat file, is(not(null))
		
		def output = []
		def client = new ProtcolRun().run(file[0], "new Something()")
		
		
		
		
		
		
		def buf = new ByteArrayOutputStream()
		def newOut = new PrintStream(buf)
		def saveOut = System.out
		
		
		System.out = newOut
		
		
		//DOTheTest
		
		
		System.out = saveOut
		println buf.toString().trim()
		
	}
	
}
