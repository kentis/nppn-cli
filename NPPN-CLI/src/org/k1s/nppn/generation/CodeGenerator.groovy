package org.k1s.nppn.generation

import org.k1s.nppn.generationVisitors.ODGVisitor;

/**
 * Main driver for the code generation
 * @author kent
 *
 */
class CodeGenerator {

	def att
	def bindings
	def outdir
	
	/**
	 * Constructor
	 * @param att The ATT for which code should be generated
	 * @param bindings the bindings for templates
	 * @param outdir The output directiory for the generated code
	 */
	public CodeGenerator(att, bindings, outdir = "./"){
		this.att = att
		this.bindings = bindings
		this.outdir = outdir
	}

	/**
	 * Generates code
	 * @return
	 */
	def generate(){

		att.children.each{
			ODGVisitor.visitATT(it, bindings)
		}
		
		def files = []
		att.children.each{ files << attToFile(it)  }
		return files
	}
	
	/**
	 * Writes code to the output directory
	 * @param files
	 */
	def write(files){
		
		att.children.eachWithIndex { att, index ->
			
			
			def file = new File("${outdir}/${nameToFilename(att.name)}")
			file.createNewFile() 
			
			file.text = files[index]
		}
	}
	
	def attToFile(node){
		
		def yieald = new StringBuffer()
		if(node.metaClass.hasProperty(node, "children") || node.metaClass.respondsTo(node, "getChildren")){
			
			node.children.each{
				yieald.append attToFile(it).toString()
			}
			
		}
		
		def text = node.text.toString().replace("%%yield%%", yieald.toString())
		if(text.contains("%%yield_declarations%%")){
			text = text.replace("%%yield_declarations%%", node.declarationsText ? node.declarationsText : "/*no decls found*/")
		}
		
	
		if(text =~ /%%yeild_(.+)%%/){
			
			def y = text =~ /%%yeild_(.+)%%/
			y.each{
					
					def tt = node.sequences[it[1]] == null ? null : node.sequences[it[1]]
					
					
					text = text.replace(it[0], attToFile(tt))
			}
		}
		
		return text
	}
	
	
	static def removePrags(str){
		if(str.indexOf("<") < 0) return str
		
		return str.substring(0,str.indexOf("<")).trim()
	}
	
	static def nameToFilename(str){
		if(str == null) return ""
		str = removePrags(str)
		str = str.replace(" ", "_")
		return str
	}
}
