package org.k1s.nppn.pragmatics;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*

import org.junit.Test;
import org.k1s.cpn.nppn.pragmatics.PrgamaticsDescriptorDSL

class PragmaticsDescriptorDSLTests {

	
	@Test
	void testNewDesc(){
		def desc = new PrgamaticsDescriptorDSL()
		
		desc.test(type: null, paramters: ['dill', 'dall'])
		
		assertThat desc.prags.size(), is(1)
		assertThat desc.prags['test'], is(not(null))
		
		assertThat desc.prags['test'].name, is("test")
	}


	
		
	
	@Test
	void testNewDescFromString(){
		def desc = new PrgamaticsDescriptorDSL()
		
		def string = "test(type: null, paramters: ['dill', 'dall'])"
		
		desc.build(string)
		
		assertThat desc.prags.size(), is(1)
		assertThat desc.prags['test'], is(not(null))
		
		assertThat desc.prags['test'].name, is("test")
	}
	
	
	@Test
	void testNewDescFromStringWith2Lines(){
		def desc = new PrgamaticsDescriptorDSL()
		
		def string = "test(type: null, paramters: ['dill', 'dall'])\n"+
					 "test2(type: null, paramters: ['dill2', 'dall2'])"
		
		desc.build(string)
		
		assertThat desc.prags.size(), is(2)
		assertThat desc.prags['test'], is(not(null))
		
		assertThat desc.prags['test'].name, is("test")
		assertThat desc.prags['test2'], is(not(null))
		
		assertThat desc.prags['test2'].name, is("test2")
	}
	
}
