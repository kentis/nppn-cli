package org.k1s.petriCode.generation;

import static org.junit.Assert.*;

import org.junit.Test;
import org.k1s.petriCode.att.ATTFactory
import org.k1s.petriCode.pragmatics.Pragmatics;
import org.k1s.petriCode.att.ATTFactoryTests;
import org.k1s.petriCode.blocks.derived.PragmaticsDerivator;
import org.k1s.petriCode.cpn.io.CpnIO
import org.k1s.petriCode.generation.BindingsDSL;
import org.k1s.petriCode.generation.CodeGenerator

import static org.hamcrest.CoreMatchers.*

import static org.junit.matchers.JUnitMatchers.*
class ChoiceCodeGeneratorTests {

	
	@Test
	void testAtomicIfGroovy2(){
		
		def pragmatic = "Id(cond: '(a hei)(t hallo)')"
		pragmatic = Pragmatics.parse(pragmatic)
		
		def bindings = getGroovyBindings()
		println pragmatic.arguments
		def cond = org.k1s.petriCode.generation.Conditionals.translatePrags(pragmatic, bindings)
		println "COND: $cond"
		assertTrue cond.contains('if( a ')
		assertTrue cond.contains('else if( true ')
		
		
		pragmatic = "Id(cond: '(a hei) (t hallo)')"
		pragmatic = Pragmatics.parse(pragmatic)
		assertEquals cond, org.k1s.petriCode.generation.Conditionals.translatePrags(pragmatic, bindings)
	}
	
	@Test
	void testUnNestedConds(){
		
		def pragmatic = "Id(cond: '((eq a b hei) (eq a c hei) (t hallo)')"
		pragmatic = Pragmatics.parse(pragmatic)
		
		def bindings = getGroovyBindings()
		println pragmatic.arguments
		def cond = org.k1s.petriCode.generation.Conditionals.translatePrags(pragmatic, bindings)
		println "COND: $cond"
		assertTrue cond.contains('if( a ==  b ')
		assertTrue cond.contains('else if( true ')
		
		
	}
	
	
	@Test
	void testNestedConds(){
		
		def pragmatic = "Id(cond: '(or (eq a b) (eq a c) hei) (t hallo)')"
		pragmatic = Pragmatics.parse(pragmatic)
		
		def bindings = getGroovyBindings()
		println pragmatic.arguments
		def cond = org.k1s.petriCode.generation.Conditionals.translatePrags(pragmatic, bindings)
		println "COND: $cond"
		assertTrue cond.contains(' a ==  b ')
		assertTrue cond.contains('||')
		assertTrue cond.contains('else if( true ')
		
		
	}
	
	
	
	@Test
	void testGeneratechoiceCode(){
		def model = this.class.getResourceAsStream("/simplechoice.cpn")
		def io = new CpnIO()
		def cpn = io.readCPN(model)
		io.parsePragmatics(cpn)
		
		PragmaticsDerivator.addDerivedPragmatics(cpn, ATTFactoryTests.getCorePragmatics())
		
		def factory = new ATTFactory(ATTFactoryTests.getCorePragmatics())
		
		def att = factory.createATT(cpn, null, null)
		
		def bindings = getGroovyBindings()
		
		def generator = new CodeGenerator(att, bindings)
		
		def file = generator.generate()[0]
		
		println file
		
		assertThat file, containsString("def doSomething")
		
		assertThat file, containsString("if( a ")
		assertThat file, containsString("else")
	}
	
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
		
		client.doSomething(true)
		client.doSomething(false)
		//fail('nyi')
		
		def out = buf.toString().trim()
		
		assertEquals out, "hei\nhallo"
		
		System.out = saveOut
		println buf.toString().trim()
		
		
		
	}
	
	def getGroovyBindings(){
		def string = this.class.getResourceAsStream("/groovy.bindings")
		return BindingsDSL.makeBindings(string)
	}
}
