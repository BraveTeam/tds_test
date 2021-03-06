package com.epam.traning.tds_test.guice.module;

import java.util.concurrent.TimeUnit;

import org.openqa.selenium.WebDriver;

import com.epam.traning.tds_test.constants.CommonConstants;
import com.epam.traning.tds_test.pages.MainPage;
import com.epam.traning.tds_test.runner.LocalBrowserFactory;
import com.epam.traning.tds_test.runner.cli.FrameworkSettings;
import com.google.inject.AbstractModule;
import com.google.inject.name.Names;
import com.proxy.AbstractProxy;
import com.proxy.ProxyType;
import com.proxy.RunProxyServer;
import com.proxy.jettyproxy.ProxyServer;
import com.proxy.threadedproxy.ThreadedProxy;
import com.selenium.driver.DriverManager;
import com.utils.freeport.FreePortInGap;

public class ProxiedDriverModule extends AbstractModule {

	protected WebDriver wd;

	protected DriverManager driverManager;

	protected AbstractProxy abstractProxy;

	@Override
	protected void configure() {

		int port = new FreePortInGap().getPort();

		ProxyType proxyType = new ProxyType(ProxyServer.class, port);
		RunProxyServer runProxyServer = ThreadedProxy.getProxyRunner(proxyType);
		abstractProxy = runProxyServer.getProxyServer();
		bind(AbstractProxy.class).toInstance(abstractProxy);

		driverManager = DriverManager.getInstance(LocalBrowserFactory.createLocalFactory(FrameworkSettings.getInstance().getDriverTypes(),
				port));

		wd = driverManager.getWebDriver();
		wd.manage().window().maximize();
		wd.manage().timeouts().pageLoadTimeout(CommonConstants.PAGE_LOAD_WAIT, TimeUnit.SECONDS);
		wd.manage().timeouts().implicitlyWait(CommonConstants.IMPLICIT_WAIT, TimeUnit.SECONDS);

		bind(DriverManager.class).annotatedWith(Names.named("proxied")).toInstance(driverManager);
		bind(WebDriver.class).annotatedWith(Names.named("proxied")).toInstance(wd);
		bind(MainPage.class).annotatedWith(Names.named("proxied")).toInstance(new MainPage(wd, true));

	}
}
