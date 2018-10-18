package io.microvibe.codegen;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.velocity.VelocityContext;
import org.dom4j.DocumentException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.microvibe.codegen.bean.CodeConfig;
import io.microvibe.codegen.bean.CodeEnv;
import io.microvibe.codegen.bean.CodeGroup;
import io.microvibe.codegen.bean.CodeTemplate;
import io.microvibe.codegen.bean.db.Table;
import io.microvibe.util.io.IOUtil;
import io.microvibe.util.StringUtil;
import io.microvibe.util.castor.Marshallers;

public class CodeGenerator {

	private static final Logger logger = LoggerFactory.getLogger(CodeGenerator.class);

	Map<String, Table> tables = new LinkedHashMap<String, Table>();
	private CodeEnv codeEnv;
	private Map<CodeGroup, List<Table>> tableGroups = new LinkedHashMap<CodeGroup, List<Table>>();
	private TablesReader tablesReader;

	public CodeGenerator(TablesReader tablesReader, String codeEnvXmlPath)
			throws FileNotFoundException, DocumentException {
		this.tablesReader = tablesReader;
		this.codeEnv = Marshallers.unmarshal(new CodeEnv(), IOUtil.getInputStream(codeEnvXmlPath));
		/*
		SAXReader reader = new SAXReader();
		Document doc = reader.read(IOUtil.getInputStream(codeEnvXmlPath));
		this.codeEnv = (CodeEnv) XmlUtil.readObject(doc, new CodeEnv());*/
		readConfig();
	}

	public void generate() {
		try {
			List<CodeGroup> groups = codeEnv.getGroups();
			for (CodeGroup group : groups) {
				List<Table> tableList = tableGroups.get(group);
				for (Table table : tableList) {
					write(group, table);
				}
			}
		} catch (IOException e) {
			logger.error("", e);
			throw new RuntimeException(e);
		}
	}

	private void mkdirs(File dir) {
		if (!dir.exists()) {
			if (!dir.mkdirs()) {
				throw new RuntimeException("Can't mkdir: " + dir.getAbsolutePath());
			}
		}
	}

	private void readConfig() {
		tableGroups = new LinkedHashMap<CodeGroup, List<Table>>();
		List<CodeGroup> groups = codeEnv.getGroups();
		for (CodeGroup group : groups) {
			List<Table> tableList = new ArrayList<Table>();
			tableGroups.put(group, tableList);
			for (CodeConfig codeCfg : group.getConfigs()) {
				String catalogName = codeCfg.getCatalog() == null ? null : codeCfg.getCatalog().trim();
				String schemaName = codeCfg.getSchema() == null ? null : codeCfg.getSchema().trim();
				String tableName = codeCfg.getTable() == null ? null : codeCfg.getTable().trim();
				Table table = tablesReader.read(catalogName, schemaName, tableName);
				if (table == null) {
					logger.error("can't find table [{}]", codeCfg.getTable());
					continue;
				}
				if (StringUtil.isNotEmpty(codeCfg.getJavaPackageName())) {
					table.setJavaPackageName(codeCfg.getJavaPackageName());
				}
				if (StringUtil.isNotEmpty(codeCfg.getClassify())) {
					table.setClassify(codeCfg.getClassify());
				}
				table.setProperty(codeCfg.getProperty());
				tableList.add(table);
			}
		}
	}

	private void write(CodeGroup group, Table table) throws IOException {
		String baseOutdir = codeEnv.getOutdir();
		List<CodeTemplate> templates = group.getTemplates();
		for (CodeTemplate template : templates) {
			String path = template.getPath();
			String outdir = template.getOutdir();
			String filename = template.getFilename();
			String javaPackageName = table.getJavaPackageName();

			logger.info("generate for table [{}] with template [{}]", table.getName(), path);

			Properties env = new Properties(System.getProperties());
			Map<String, String> configMap = new HashMap<String, String>();
			if (StringUtil.isNotEmpty(table.getClassify())) {
				env.setProperty("tableClassify", table.getClassify());
				configMap.put("tableClassify", table.getClassify());
			}
			env.setProperty("tablePackage", javaPackageName.replace(".", "/"));
			env.setProperty("tableJavaName", table.getJavaClassName());
			env.setProperty("tableJavaVariableName", table.getJavaVariableName());
			env.setProperty("tableXmlName", table.getXmlName());
			configMap.put("tablePackage", javaPackageName.replace(".", "/"));
			configMap.put("tableJavaName", table.getJavaClassName());
			configMap.put("tableJavaVariableName", table.getJavaVariableName());
			configMap.put("tableXmlName", table.getXmlName());

			VelocityContext context = new VelocityContext();
			if (codeEnv.getProperty() != null) {
				Set<Map.Entry<String, String>> set = codeEnv.getProperty().getProperties().entrySet();
				for (Map.Entry<String, String> entry : set) {
					env.put("config." + entry.getKey(), entry.getValue());
					env.put(entry.getKey(), entry.getValue());
					context.put(entry.getKey(), entry.getValue());
					configMap.put(entry.getKey(), entry.getValue());
				}
			}
			if (group.getProperty() != null) {
				Set<Map.Entry<String, String>> set = group.getProperty().getProperties().entrySet();
				for (Map.Entry<String, String> entry : set) {
					env.put("group." + entry.getKey(), entry.getValue());
					env.put(entry.getKey(), entry.getValue());
					context.put(entry.getKey(), entry.getValue());
					configMap.put(entry.getKey(), entry.getValue());
				}
				context.put("group", group.getProperty().getProperties());
			}
			if (template.getProperty() != null) {
				Set<Map.Entry<String, String>> set = template.getProperty().getProperties().entrySet();
				for (Map.Entry<String, String> entry : set) {
					env.put("template." + entry.getKey(), entry.getValue());
					env.put(entry.getKey(), entry.getValue());
					context.put(entry.getKey(), entry.getValue());
					configMap.put(entry.getKey(), entry.getValue());
				}
				context.put("template", template.getProperty().getProperties());
			}
			if (table.getProperty() != null) {
				Set<Map.Entry<String, String>> set = table.getProperty().getProperties().entrySet();
				for (Map.Entry<String, String> entry : set) {
					env.put("config." + entry.getKey(), entry.getValue());
					env.put(entry.getKey(), entry.getValue());
					context.put(entry.getKey(), entry.getValue());
					configMap.put(entry.getKey(), entry.getValue());
				}
			}

			context.put("config", configMap);
			context.put("table", table);

			outdir = StringUtil.bindVariable(outdir, env);
			filename = StringUtil.bindVariable(filename, env);

			/*outdir = outdir.replace("${tablePackage}", javaPackageName.replace(".", "/"));
			filename = filename.replace("${tableJavaName}", table.getJavaClassName());*/

			File dir = baseOutdir == null ? new File(outdir) : new File(baseOutdir + "/" + outdir);
			write(path, context, dir, filename);

			logger.info("generate file [{}] in dir[{}]", filename, dir.getPath());
		}
	}

	private void write(String template, VelocityContext context, File dir, String file)
			throws IOException {
		mkdirs(dir);
		FileOutputStream fos = new FileOutputStream(new File(dir, file));
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos, "utf8"));
		VelocityTemplate.write(context, bw, template);
		bw.flush();
		bw.close();
	}

}
