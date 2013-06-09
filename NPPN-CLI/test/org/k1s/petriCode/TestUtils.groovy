package org.k1s.petriCode

import org.k1s.petriCode.cpnBuilder.CPNBuilder

class TestUtils {

	
	static def getLoopPage(){
		def builder = new CPNBuilder()
		def pn = builder.make {
			page(name:'service'){
				
				def service = transition(name:'tranistion\n<<service(name:\'loopService\')>>')
				def start = place(name:'start')
				arc(service,start)
				
				def doStuff = transition(name:"doStuff")
				arc(start, doStuff)
				def end = place(name:"end")
				arc(doStuff, end)
				
				def ret = transition(name:"return <<return>>")
				arc(end, ret)
			}
		}
	}
	
	def getCondPage(){
	
	}

	
	def getLoopAndCondPage(){
		
	}
	
	def getCondInLoopPage(){
	
	}

	def getNestedLoopPage(){
	
	}

	
	
}
