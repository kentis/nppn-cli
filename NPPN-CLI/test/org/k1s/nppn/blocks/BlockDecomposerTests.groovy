package org.k1s.nppn.blocks;

import static org.junit.Assert.*
import static org.junit.matchers.JUnitMatchers.*

import org.junit.Test;
import org.k1s.nppn.TestUtils;

class BlockDecomposerTests {
	
	@Test
	void testannotateLoops(){
		def loopPage = TestUtils.getLoopPage()
		new BlockDecomposer().annotateLoops(loopPage)
		
		assertThat loopPage.object[1], contains("<<startLoop")
		
		assertThat loopPage.object[3], contains("<<endLoop")
	}
	
}
