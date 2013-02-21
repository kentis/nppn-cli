package org.k1s.nppn.generation;

import static org.junit.Assert.*;

import org.junit.Test;
import org.k1s.cpn.nppn.att.ATTFactory
import org.k1s.nppn.att.ATTFactoryTests;
import org.k1s.nppn.blocks.derived.PragmaticsDerivator;
import org.k1s.nppn.cpn.io.CpnIO

class CodeGeneratorTests {

	@Test
	void testMessageSendingProtocol(){
		//fail "NYW"
		
		def model = this.class.getResourceAsStream("/ProtocolModel.cpn")
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
		def client = new ProtcolRun().run(file[0], "new Sender()")
		
		def server = new ProtcolRun().run(file[1], "new Receiver()")
		
		//server.metaClass.println { String str -> output << str }
		
		
		def buf = new ByteArrayOutputStream()
		def newOut = new PrintStream(buf)
		def saveOut = System.out
		
		
		System.out = newOut
		
		
		def t = Thread.start {
				//server.start(31337)
			server.ReceiverInit(31337)
			def res = server.ReceiverReceive()
			println res
		}
		
		Thread.sleep(1000)
		
		//client.start("the quick brown fox jumps over the lazy dog",[port: 31337, host:'localhost'])
		client.SenderOpen([port: 31337, host:'localhost'])
		client.SenderSend("the quick brown fox jumps over the lazy dog")
		client.SenderClose()
		
		Thread.sleep 1000
		t.stop()
		
		System.out = saveOut
		println buf.toString().trim()
		assertEquals("the quick brown fox jumps over the lazy dog", buf.toString().trim() )
	}
	
	def getGroovyBindings(){
		def string = this.class.getResourceAsStream("/groovy.bindings")
		return BindingsDSL.makeBindings(string)
	}
}

class ProtcolRun {
		def run(String code, String starter){
			return new GroovyShell().evaluate("${code}\n${starter}")
		}
}
