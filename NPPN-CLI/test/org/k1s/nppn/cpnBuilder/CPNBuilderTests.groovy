package org.k1s.nppn.cpnBuilder;


import org.cpntools.accesscpn.model.Arc
import org.cpntools.accesscpn.model.Instance
import org.cpntools.accesscpn.model.Page
import org.cpntools.accesscpn.model.Place
import org.cpntools.accesscpn.model.RefPlace;
import org.cpntools.accesscpn.model.Transition
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

class CPNBuilderTests {
	
	@Before
	void setup(){
		Transition.metaClass.pragmatics = null
		Place.metaClass.pragmatics = null
		Instance.metaClass.pragmatics = null
		RefPlace.metaClass.pragmatics = null
	}
	
	@Test
	void testBuilderPagePlaceArcTransition(){
		//def builder = new ePNKBuilder(PnmlcoremodelFactory.eINSTANCE)
		def builder = new CPNBuilder()
		
		def pn = builder.make {
			page(name:'root'){
				def p = place(name:'place')
				def t = transition(name:'tranistion')
				arc(p,t)
			}
		}
		
		assertThat pn.page.size(),  is(1)
		assertThat pn.page[0].object.size(), is(2)
		println pn.page[0].object[0].class
		assertTrue pn.page[0].object[0] instanceof Place
		assertTrue pn.page[0].object[1] instanceof Transition
		Place p = pn.page[0].object[0]
		
		assertThat p.getSourceArc().size(), is(1)
		Arc arc = p.getSourceArc()[0]
		assertThat arc.getTarget(), is(pn.page[0].object[1])

		assertThat pn.page[0].object[1].getTargetArc()[0], is(arc)
	}
	
	
	@Test
	void testBuilderPagePlaceArcTransitionWithPragmatics(){
		//def builder = new ePNKBuilder(PnmlcoremodelFactory.eINSTANCE)
		def builder = new CPNBuilder()
		
		def pn = builder.make {
			page(name:'root'){
				def p = place(name:'place', pragmatics: 'dilldall()')
				def t = transition(name:'tranistion', pragmatics: 'tullball()')
				arc(p,t)
			}
		}
		
		assertThat pn.page.size(),  is(1)
		assertThat pn.page[0].object.size(), is(2)
		println pn.page[0].object[0].class
		assertTrue pn.page[0].object[0] instanceof Place
		assertTrue pn.page[0].object[1] instanceof Transition
		
		assertThat pn.page[0].object[0].pragmatics[0].name, is('dilldall')
		assertThat pn.page[0].object[1].pragmatics[0].name, is('tullball')
	}
	
	@Test
	void testBuilderMultiPage(){
//		fail("nyi")
//		//def builder = new ePNKBuilder(PnmlcoremodelFactory.eINSTANCE)
		def builder = new CPNBuilder()
		println "builder.factory  ${builder.factory}"
		def pn = builder.make {
			def root = page(name:'root'){
				def p = place(name:'place')
				def t = substTransition(name:'tranistion', subPageId: 'subPage')
				arc(p,t)
				def p2 = place(name:'place2')
				arc(t, p2)
			}
			def subPage = page(name: 'subpage', id: 'subPage'){
				def np = place(name: 'name')
				def nt = transition(name:'tranistion')
				arc(np,nt)
				def np2 = place(name: "anotherName")
				arc(nt, np2)
			}
		}
//		
//		//a place, a page and a transotion
		println pn.page[0].object
		assertThat pn.page[0].object.size(), is(3)
		assertTrue pn.page[0].object[1] instanceof Instance
//		/**********************************************
//		 * Make sure everything from last page is true
//		 **********************************************/
//		assertThat pn.page.size(),  is(1)
//		
		println pn.page[0].object[0].class
		assertTrue pn.page[0].object[0] instanceof Place
		assertTrue pn.page[0].object[1] instanceof Instance
		assertTrue pn.page[0].object[2] instanceof Place
		Place p = pn.page[0].object[0]
//		
		assertThat p.getSourceArc().size(), is(1)
		Arc arc = p.getSourceArc()[0]
		assertThat arc.getTarget(), is(pn.page[0].object[1])
//
		assertThat pn.page[0].object[1].getTargetArc()[0], is(arc)
//		
//		
//		/***************************************
//		 * 
//		 *************************************/
		def subst = pn.page[0].object[1]
		def page2 = pn.page[1]
		assertTrue page2 instanceof Page 
		assertThat subst.getSubPageID(), is(not(null))
		assertThat page2.id, is(not(null))
		assertThat subst.getSubPageID(), is(page2.id)
		
//		def page =  pn.page[0].object[1]
//		assertTrue page.object[0] instanceof RefPlace
//		assertTrue page.object[1] instanceof Transition
//		assertThat page.object[0].getOut().size(), is(1)
		
	}
	
	
	
}
