// Copyright (c) 2003-present, Jodd Team (jodd.org). All Rights Reserved.

package jodd.joy;

import jodd.io.FileUtil;
import org.apache.catalina.startup.Tomcat;

import java.io.File;
import java.net.URL;

/**
 * Embedded Tomcat server for integration tests.
 */
public class JoyTomcatTestServer {

	// ---------------------------------------------------------------- instance

	protected File webRoot;
	protected Tomcat tomcat;

	public void start() throws Exception {
		prepareWebApplication();

		String workingDir = System.getProperty("java.io.tmpdir");

		tomcat = new Tomcat();
		tomcat.setPort(8173);
		tomcat.setBaseDir(workingDir);
		tomcat.addWebapp("/", webRoot.getAbsolutePath());

		tomcat.start();
	}

	protected void prepareWebApplication() throws Exception {
		webRoot = FileUtil.createTempDirectory("jodd-joy", "test-int");
		webRoot.deleteOnExit();

		// web-inf

		File webInfFolder = new File(webRoot, "WEB-INF");
		webInfFolder.mkdir();

		// web.xml

		URL webXmlUrl = JoyTomcatTestServer.class.getResource("/web-test-int.xml");
		File webXmlFile = FileUtil.toFile(webXmlUrl);

		FileUtil.copyFile(webXmlFile, new File(webInfFolder, "web.xml"));

		// jsp

		File jspFolder = new File(webXmlFile.getParent(), "jsp");
		FileUtil.copyDir(jspFolder, webRoot);

		// lib folder

		File libFolder = new File(webInfFolder, "lib");
		libFolder.mkdir();

		// classes

		File classes = new File(webInfFolder, "classes");
		classes.mkdirs();

		// classes/madvoc.props

		URL madvocPropsUrl = JoyTomcatTestServer.class.getResource("/madvoc.props");
		File madvocPropsFile = FileUtil.toFile(madvocPropsUrl);

		FileUtil.copyFileToDir(madvocPropsFile, classes);
	}

	public void stop() throws Exception {
		tomcat.stop();
		tomcat.destroy();
		FileUtil.deleteDir(webRoot);
	}
}
