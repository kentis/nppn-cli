package org.k1s.nppn;

import static org.junit.Assert.*;

import org.junit.Test;

class NPPNTests {

	
	@Test
	void testMainUsage(){
		def arguments = "-h"
		
		def buf = new ByteArrayOutputStream()
		def newOut = new PrintStream(buf)
		def saveOut = System.out
		System.out = newOut
		
		NPPN.main(arguments.split(" "))
		
		System.out = saveOut
		println buf.toString().trim()
		assertTrue buf.toString().trim().contains("NPPN")
	}
	
	
	@Test
	void testMainPacketProtocol(){
		def arguments = "-o /home/kent/tmp -b /home/kent/ws-ePNK/NPPN-CLI/test-resources/groovy.bindings /home/kent/ws-ePNK/NPPN-CLI/test-resources/ProtocolModel.cpn"
		
		def senderFile = new File("/home/kent/tmp/Sender")
		def recieverFile = new File("/home/kent/tmp/Receiver")
		
		senderFile.delete()
		recieverFile.delete()
		
		
		def buf = new ByteArrayOutputStream()
		def newOut = new PrintStream(buf)
		def saveOut = System.out
		System.out = newOut
		
		try{
			NPPN.main(arguments.split(" "))
		} catch(Exception ex){
			ex.printStackTrace()
		}
		System.out = saveOut
		println buf.toString().trim()
		
		assertTrue new File("/home/kent/tmp/Sender").exists()
		assertTrue new File("/home/kent/tmp/Receiver").exists()
		
		
	}
	
}
