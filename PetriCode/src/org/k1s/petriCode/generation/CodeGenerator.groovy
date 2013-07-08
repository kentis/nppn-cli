package org.k1s.petriCode.generation

import org.k1s.petriCode.blocks.Principal;
import org.k1s.petriCode.generationVisitors.ODGVisitor;

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
			
			def name = nameToFilename(att.name)
			
			if(bindings && bindings.prag2Binding["__FILENAME__"] ){
				
				if(bindings.prag2Binding["__FILENAME__"].template instanceof Closure){
				
					name = bindings.prag2Binding["__FILENAME__"].template(["name": name])
				} else {
				
					//TODO: run template
				}
			}
			
			def file = new File("${outdir}/${name}")
			file.createNewFile() 
			
			file.text = files[index]
		}
	}
	
	def attToFile(node){
		
		def yieald = new StringBuffer()
		if(node.metaClass.hasProperty(node, "children") || node.metaClass.respondsTo(node, "getChildren")){
			
			
			sortChildren(node).each{
				yieald.append attToFile(it).toString()
			}
			
		}
		
		def text = node.text.toString().replace("%%yield%%", yieald.toString())
		
		if(text.contains("%%yield_declarations%%")){
			if(node.declarationsText){
				text = text.replace("%%yield_declarations%%", node.declarationsText)// ? node.declarationsText : "/*no decls found*/")
			} else {
				def commentTmpl = bindings.prag2Binding["__COMMENTS__"]
				def declText = new TemplateManager().runTemplate(commentTmpl.template,[params: ['no decls found'], indentLevel: node.level])
				text = text.replace("%%yield_declarations%%", declText)
			}
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
	
	def sortChildren(node){
		if(node instanceof Principal){
			return node.children.sort{
				if(it.node.pragmatics[0].name == 'remote') return 0
				return 1
			}	
		}
		return node.children
	}
	
	static def removePrags(str){
		if(str.indexOf("<") < 0) return str
		
		return str.substring(0,str.indexOf("<")).trim()
	}
	
	static def indent(indentLevel){
		return "  " * indentLevel
	}
	
	static def nameToFilename(str){
		if(str == null) return ""
		str = removePrags(str)
		str = str.replace(" ", "_")
		return str
	}
}
