package org.k1s.nppn.generation

/**
 * Represents a single binding
 * @author kent
 *
 */
class Binding{
	def name
	def template
	def pragmatic
	def dependencies
	def weight
	def isContainer = false
	def isMultiContainer = false
	def parameterStrategy
	
	
	/**
	 * returns th template for this Binding
	 * @return
	 */
	def getTemplate(){
		
		return template
	}
	
	
}
