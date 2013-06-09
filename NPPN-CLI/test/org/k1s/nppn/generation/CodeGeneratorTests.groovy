package org.k1s.nppn.generation;

import static org.junit.Assert.*;

import org.junit.Test;
import org.k1s.cpn.nppn.att.ATTFactory
import org.k1s.cpn.nppn.pragmatics.PragmaticsChecker;
import org.k1s.nppn.att.ATTFactoryTests;
import org.k1s.petriCode.blocks.derived.PragmaticsDerivator;
import org.k1s.petriCode.PetriCode;
import org.k1s.petriCode.cpn.io.CpnIO;
import org.k1s.petriCode.generation.BindingsDSL;
import org.k1s.petriCode.generation.CodeGenerator;

import clojure.lang.RT;
import clojure.lang.Var;

import javax.tools.*;


import static org.hamcrest.CoreMatchers.*

import static org.junit.matchers.JUnitMatchers.*

class CodeGeneratorTests {

	
	@Test
	void testEuler(){
		def model = this.class.getResourceAsStream("/eulers.cpn")
		def io = new CpnIO()
		def cpn = io.readCPN(model)
		io.parsePragmatics(cpn)
		
		//assertTrue PragmaticsChecker.check(cpn, ATTFactoryTests.getCorePragmatics())
		
		PragmaticsDerivator.addDerivedPragmatics(cpn, ATTFactoryTests.getCorePragmatics())
		
		def factory = new ATTFactory(ATTFactoryTests.getCorePragmatics())
		def att = factory.createATT(cpn, null, null)
		println att.children
		
		def bindings = getGroovyBindings()
		def generator = new CodeGenerator(att, bindings)
		def file = generator.generate()
		
		println file
		
		assertThat file, is(not(null))
		assertThat file, is(not([]))
		
		
		def eulers = new ProtcolRun().run(file[0], "new eulers()")
		
		
		def buf = new ByteArrayOutputStream()
		def newOut = new PrintStream(buf)
		def saveOut = System.out
			
		System.out = newOut
		
		eulers.euler1(1000)
		
		System.out = saveOut
		println buf.toString().trim()
		assertEquals("233168", buf.toString().trim() )
	}
	
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
			
			server.Init(31337)
			def res = server.ReceiverReceive()
			//println "And the result is:"
			println res
		}
		
		Thread.sleep(1000)
		
		//client.start("the quick brown fox jumps over the lazy dog",[port: 31337, host:'localhost'])
		client.Open([port: 31337, host:'localhost'])
		client.Send("the quick brown fox jumps over the lazy dog")
		client.Close()
		
		Thread.sleep 1000
		t.stop()
		
		System.out = saveOut
		println buf.toString().trim()
		assertEquals("the quick brown fox jumps over the lazy dog", buf.toString().trim() )
	}
	
	
	
	
	
	
	@Test
	void testMessageSendingProtocolJava(){
		//fail "NYW"
		
		def model = this.class.getResourceAsStream("/ProtocolModel.cpn")
		def io = new CpnIO()
		def cpn = io.readCPN(model)
		io.parsePragmatics(cpn)
		
		PragmaticsDerivator.addDerivedPragmatics(cpn, ATTFactoryTests.getCorePragmatics())
		
		def factory = new ATTFactory(ATTFactoryTests.getCorePragmatics())
		
		def att = factory.createATT(cpn, null, null)
		
		def bindings = getJavaBindings()
		
		def generator = new CodeGenerator(att, bindings)
		
		def file = generator.generate()
		
		println file
		
		assertThat file, is(not(null))
		
		println file
		
		//make files
		
		"rm -rf /tmp/codegenTestsTing".execute().waitFor() 
		"mkdir /tmp/codegenTestsTing".execute().waitFor()  
		"mkdir /tmp/codegenTestsTing/Sender".execute().waitFor()
		"mkdir /tmp/codegenTestsTing/Receiver".execute().waitFor()
		
		new File("/tmp/codegenTestsTing/Sender/Sender.java").write(file[0])
		new File("/tmp/codegenTestsTing/Receiver/Receiver.java").write(file[1])
		
		//compile Java
		
		JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
		
		int compilationResult = compiler.run(null, null, null, "/tmp/codegenTestsTing/Sender/Sender.java");
				if(compilationResult == 0){
					System.out.println("Compilation is successful");
				}else{
					System.out.println("Compilation Failed");
				}
		
		compilationResult = compiler.run(null, null, null, "/tmp/codegenTestsTing/Receiver/Receiver.java");
				if(compilationResult == 0){
					System.out.println("Compilation is successful");
				}else{
					System.out.println("Compilation Failed");
				}
				
		//load java
			def url = new File("/tmp/codegenTestsTing/").toURL()
			def urlArray = new URL[1]
			urlArray[0] = url
			//[url].toArray(url.getClass())
			ClassLoader cl = new URLClassLoader(urlArray);
			
			Class SenderClass = cl.loadClass("Sender.Sender")
			Class ReceiverClass= cl.loadClass("Receiver.Receiver")
		//run java
			def server = ReceiverClass.newInstance()
			def client = SenderClass.newInstance()
			
			def buf = new ByteArrayOutputStream()
			def newOut = new PrintStream(buf)
			def saveOut = System.out
			
			
			System.out = newOut
			
			
			def t = Thread.start {
					//server.start(31337)
				
				server.Init(31337)
				def res = server.ReceiverReceive()
				//println "And the result is:"
				println res
			}
			
			Thread.sleep(1000)
			
			//client.start("the quick brown fox jumps over the lazy dog",[port: 31337, host:'localhost'])
			client.Open([port: 31337, host:'localhost'])
			client.Send("the quick brown fox jumps over the lazy dog")
			client.Close()
			
			Thread.sleep 1000
			t.stop()
			
			System.out = saveOut
			println buf.toString().trim()
			assertEquals("the quick brown fox jumps over the lazy dog", buf.toString().trim() )
			
		//rydd opp		
		//"rm -rf /tmp/codegenTestsTing".execute() 
	}
	
	
	//@Test
	void testMessageSendingProtocolClojure(){
		PetriCode.strict = true
		def model = this.class.getResourceAsStream("/ProtocolModel.cpn")
		def io = new CpnIO()
		def cpn = io.readCPN(model)
		io.parsePragmatics(cpn)
		
		PragmaticsDerivator.addDerivedPragmatics(cpn, ATTFactoryTests.getCorePragmatics())
		
		def factory = new ATTFactory(ATTFactoryTests.getCorePragmatics())
		
		def att = factory.createATT(cpn, null, null)
		
		def bindings = getClojureBindings()
		
		def generator = new CodeGenerator(att, bindings)
		
		def file = generator.generate()
		
		println file
		
		assertThat file, is(not(null))

		def eval = RT.var("clojure.core", "eval")
		def read_string = RT.var("clojure.core", "load-string")
		
		eval.invoke(read_string.invoke(file[0]))
		eval.invoke(read_string.invoke(file[1]))
		
		def buf = new ByteArrayOutputStream()
		def newOut = new PrintStream(buf)
		def saveOut = System.out
		
		
		System.out = newOut
		
		def t = Thread.start {
			//server.start(31337)
		
			RT.var("Receiver.Receiver", "Init").invoke(31337)
			def res = RT.var("Receiver.Receiver", "ReceiverReceive").invoke()
			println res
		}
		
		Thread.sleep(1000)
		
		RT.var("Sender.Sender", "Open").invoke(31337)
		RT.var("Sender.Sender", "Send").invoke("the quick brown fox jumps over the lazy dog")
		RT.var("Sender.Sender", "Close").invoke()
		
		Thread.sleep 1000
		t.stop()
		
		System.out = saveOut
		println buf.toString().trim()
		assertEquals("the quick brown fox jumps over the lazy dog", buf.toString().trim() )
		
		//client.Open([port: 31337, host:'localhost'])
		//client.Send("the quick brown fox jumps over the lazy dog")
		//client.Close()
		
		//RT.var("Sender.Sender", "Send").invoke(31337)
		
		/*def testProg = """(ns dilldall (:gen-class))
 
(defn foo [a b]
 (println (str a " " b)))
"""
		
		def eval = RT.var("clojure.core", "eval")
		def read_string = RT.var("clojure.core", "load-string")
		
		eval.invoke(read_string.invoke(testProg))
	
		RT.var("dilldall", "foo").invoke("hei", "hallo") */	
	}
	
		
	
	@Test
	void testMessageSendingProtocolLCV(){
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
		assertTrue(file[0].contains("Idle = "))
		assertTrue(file[0].contains("Open = "))
		
	}
	
	
	def getGroovyBindings(){
		def string = this.class.getResourceAsStream("/groovy.bindings")
		return BindingsDSL.makeBindings(string)
	}
	
	def getJavaBindings(){
		def string = this.class.getResourceAsStream("/java.bindings")
		return BindingsDSL.makeBindings(string)
	}
	
	def getClojureBindings(){
		def string = this.class.getResourceAsStream("/clojure.bindings")
		return BindingsDSL.makeBindings(string)
	}
}

class ProtcolRun {
		def run(String code, String starter){
			return new GroovyShell().evaluate("${code}\n${starter}")
		}
}
