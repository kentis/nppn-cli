package org.k1s.nppn.generation

import groovy.lang.Closure;

/**
 * Collection of template bindings
 * @author kent
 *
 */
class Bindings {

	def bindings = [:]
	def prag2Binding = [:]
	
	/**
	 * Adds a binding using the leftShift operator
	 * @param binding
	 * 
	 */
	def leftShift(binding){
		add(binding)
	}
	
	/**
	 * Adds a Binding
	 * @param binding
	 *
	 */
	void add(Binding binding){
		bindings[binding.name] = binding
		prag2Binding[binding.pragmatic] = binding
	}
	
}
