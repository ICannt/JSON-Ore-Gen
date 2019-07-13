package org.icannt.jsonoregen;

import java.io.File;
import java.io.IOException;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.util.DefaultIndenter;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;

/**
 * Created by ICannt on 11/04/19.
 * 
 * JSON format file writer, intended for generating config files for the Minecraft modding project Netherending Ores.
 */

public class JSONOreGen {
	
	/**
	 * The main class gets the JsonFactory started, pretty printer configured and then
	 * attempts to run the JSON writing methods.
	 * 
	 * @param		args
	 * @throws		IOException
	 */
	public static void main(String[] args) throws IOException {
		
		JsonFactory factory = new JsonFactory();
		
		@SuppressWarnings("serial")
		DefaultPrettyPrinter prettyPrinter = new DefaultPrettyPrinter() {
			@Override
			public void writeObjectFieldValueSeparator(JsonGenerator jg) throws IOException {
				// No spaces after the quote marks for objects
				jg.writeRaw(": ");
			}
			
		    @Override
		    public void writeEndObject(JsonGenerator g, int nrOfEntries) throws IOException {
		    	super.writeEndObject(g, nrOfEntries);
		        // If there is no nesting levels left we are at end of file, add an empty line, makes git based stuff happier
		        if (_nesting == 0) {
		        	g.writeRaw(DefaultIndenter.SYS_LF);		        	
		        }
		    }
		};
		// We want tabs not spaces
		DefaultPrettyPrinter.Indenter ind = new DefaultIndenter("\t", DefaultIndenter.SYS_LF);
		prettyPrinter.indentObjectsWith(ind);
		prettyPrinter.indentArraysWith(ind);
		
		// Step through all the blocks and generate the formatted JSON
		for (BlockData blockData : BlockData.values()) {
			// Any entry that is marked with rank 999, we don't want auto generated
			if (blockData.getRank() < 999) {
				CoFH(blockData, factory, prettyPrinter);
				// TODO: Write MMD json structure
				//MMD(blockData, factory, prettyPrinter);				
			}
			if (blockData.getRank() == 999) {
				System.out.println("Not Generated: "+blockData.getBlocks());
			}
		}
		
	}
	
	/**
	 * Outputs CoFH World compliant JSON files based off input data enum.
	 * Currently is single block only per generation entry.
	 * 
	 * @param		bd Current entry from the instance of the BlockData class.
	 * @param		fac Initial JsonFactory object, gets recycled for each new
	 * 				generator 
	 * @param		pp Pretty printer object, contains all output formatting.
	 * @throws		IOException
	 */
	public static void CoFH(BlockData bd, JsonFactory fac, DefaultPrettyPrinter pp) throws IOException {
		
		String fileName = bd.getDimensionName()+"_"+bd.getRank()+"0_netherendingores_"+bd.getBlocks();
		
		JsonGenerator g = fac.createGenerator(new File("jsonout/cofh/"+fileName+".json"), JsonEncoding.UTF8).setPrettyPrinter(pp);
		
		g.writeStartObject();
			g.writeStringField("dependencies", bd.getDependencies());				
			g.writeObjectFieldStart("populate");
				g.writeObjectFieldStart(bd.getObjectRef());
					g.writeStringField("distribution", bd.getDistribution());
					g.writeObjectFieldStart("generator");
						g.writeObjectFieldStart("block");
							g.writeStringField("name", bd.getName());
							g.writeObjectFieldStart("properties");
								g.writeStringField("type", bd.getBlocks());
							g.writeEndObject();
						g.writeEndObject();
						g.writeStringField("material", bd.getMaterial());
						g.writeNumberField("cluster-size", bd.getClusterSize());
					g.writeEndObject();
					g.writeNumberField("cluster-count", bd.getClusterCount());
					g.writeNumberField("min-height", bd.getMinHeight());
					g.writeNumberField("max-height", bd.getMaxHeight());
					//g.writeBooleanField("retrogen", bd.getRetrogen());
					g.writeStringField("retrogen", bd.getRetrogen().toString()); // CoFH's default block gen files are string converted boolean
					g.writeStringField("biome", bd.getBiome());
					g.writeObjectFieldStart("dimension");
						g.writeStringField("restriction", bd.getRestriction());
						g.writeArrayFieldStart("value");
							for (int dimension : bd.getDimension()) {
								g.writeNumber(dimension);
							}
		g.close(); // Close will auto-end all end nesting levels
	}
	
	/**
	 * Outputs MMD OreSpawn compliant JSON files based off input data enum
	 * 
	 * @param		bd Current entry from the instance of the BlockData class.
	 * @param		fac Initial JsonFactory object, gets recycled for each new
	 * 				generator. 
	 * @param		pp Pretty printer object, contains all output formatting.
	 * @throws		IOException
	 */
	public static void MMD(BlockData bd, JsonFactory fac, DefaultPrettyPrinter pp) throws IOException {
		
		JsonGenerator g = fac.createGenerator(new File("jsonout/mmd/"+bd.getBlocks()+".json"), JsonEncoding.UTF8).setPrettyPrinter(pp);
		
		g.writeStartObject();

		g.writeEndObject();
		g.writeRawValue(DefaultIndenter.SYS_LF);
		g.close();		
	}
	
}
