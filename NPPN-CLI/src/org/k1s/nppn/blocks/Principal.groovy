package org.k1s.nppn.blocks

class Principal {
	def name
	def channels = []
	
	//services = children
	def services = []
	
	def getChildren(){
		return services
	}
	
	def setChildren(children){
		services = children
	}
	
	def lcvs = []
	def states = []
}
