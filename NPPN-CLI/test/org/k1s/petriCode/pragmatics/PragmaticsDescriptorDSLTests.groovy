package org.k1s.petriCode.pragmatics;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*

import org.eclipse.emf.ecore.resource.Resource;
import org.junit.Test;
import org.k1s.petriCode.pragmatics.PrgamaticsDescriptorDSL

class PragmaticsDescriptorDSLTests {

	
	@Test
	void testNewDesc(){
		def desc = new PrgamaticsDescriptorDSL()
		
		desc.test(paramters: ['dill', 'dall'])
		
		assertThat desc.prags.size(), is(1)
		assertThat desc.prags['test'], is(not(null))
		
		assertThat desc.prags['test'].name, is("test")
	}


	
		
	
	@Test
	void testNewDescFromString(){
		def desc = new PrgamaticsDescriptorDSL()
		
		def string = "test(paramters: ['dill', 'dall'])"
		
		desc.build(string)
		
		assertThat desc.prags.size(), is(1)
		assertThat desc.prags['test'], is(not(null))
		
		assertThat desc.prags['test'].name, is("test")
	}
	
	
	@Test
	void testNewDescFromStringWith2Lines(){
		def desc = new PrgamaticsDescriptorDSL()
		
		def string = "test( paramters: ['dill', 'dall'])\n"+
					 "test2( paramters: ['dill2', 'dall2'])"
		
		desc.build(string)
		
		assertThat desc.prags.size(), is(2)
		assertThat desc.prags['test'], is(not(null))
		
		assertThat desc.prags['test'].name, is("test")
		assertThat desc.prags['test2'], is(not(null))
		
		assertThat desc.prags['test2'].name, is("test2")
	}
	
	@Test
	void testNewDescFromCore(){
		def desc = new PrgamaticsDescriptorDSL()
		
		def string = this.class.getResourceAsStream("/core.prags")
		desc.build(string.text)
		
		println desc
		assertTrue desc.prags.size() > 3

	}
	
	
}
